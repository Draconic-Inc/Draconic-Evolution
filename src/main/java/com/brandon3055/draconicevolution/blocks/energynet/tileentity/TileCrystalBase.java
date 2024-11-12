package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.ITilePlaceListener;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public abstract class TileCrystalBase extends TileBCore implements ITilePlaceListener, ICrystalLink, IInteractTile, IENetEffectTile {

    //region Stats
    public static final UUID MSG_ID = UUID.fromString("b0729ef4-3c3a-4339-bda1-8630f7395df6");

    private static Map<CrystalType, int[]> MAX_LINKS = new HashMap<>();

    static {
        MAX_LINKS.put(CrystalType.RELAY, new int[]{8, 16, 32});
        MAX_LINKS.put(CrystalType.CRYSTAL_IO, new int[]{2, 3, 4});
        MAX_LINKS.put(CrystalType.WIRELESS, new int[]{4, 8, 16});
    }

    //endregion

    protected int tick = 0;
    //    private int crystalTier = -1;
    protected LinkedList<Vec3B> linkedCrystals = new LinkedList<>();
    public LinkedList<int[]> transferRatesArrays = new LinkedList<>();
    public LinkedList<Byte> flowRates = new LinkedList<>();
    private LinkedList<BlockPos> linkedPosCache = null;
    //    protected EnergyStorage energyStorage = new EnergyStorage(0);
    protected OPStorage opStorage = new OPStorage(this, 0);
    protected ENetFXHandler fxHandler;
    protected TechLevel techLevel;

    public TileCrystalBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        this(tileEntityTypeIn, TechLevel.DRACONIUM, pos, state);
    }

    public TileCrystalBase(BlockEntityType<?> tileEntityTypeIn, TechLevel techLevel, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.techLevel = techLevel;
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);

        capManager.setInternalManaged("energy", CapabilityOP.BLOCK, opStorage).saveBoth().syncContainer();
    }

    //region Energy Balance

    @Override
    public void tick() {
        super.tick();
        if (linkedCrystals.size() != transferRatesArrays.size() && !level.isClientSide) {
            rebuildTransferList();
        }

        balanceLinkedDevices();
        fxHandler.update();

        if (!level.isClientSide && DEEventHandler.serverTicks % 10 == 0) {
            flowRates.clear();
            for (int i = 0; i < linkedCrystals.size(); i++) {
                flowRates.add(calculateFlow(i));
            }
            fxHandler.detectAndSendChanges();
        }

        tick++;
    }

    public void balanceLinkedDevices() {
        if (level.isClientSide) {
            return;
        }
        for (BlockPos linkedPos : getLinks()) {
            BlockEntity linkedTile = level.getBlockEntity(linkedPos);

            if (!(linkedTile instanceof ICrystalLink)) {
                if (level.hasChunkAt(linkedPos)) {
                    breakLink(linkedPos);
                    return;
                } else {
                    continue;
                }
            }

            ICrystalLink linkedCrystal = (ICrystalLink) linkedTile;
            double thisCap = (double) getEnergyStored() / (double) getMaxEnergyStored();
            double thatCap = (double) linkedCrystal.getEnergyStored() / (double) linkedCrystal.getMaxEnergyStored();
            double diff = thisCap - thatCap;

            if (linkedCrystal.balanceMode() == 0 && thatCap < 1) {
                transferRatesArrays.get(linkedPosCache.indexOf(linkedPos))[tick % 20] = balanceTransfer(linkedCrystal, 1 - thatCap);
                continue;
            }

            if (diff <= 0 || linkedCrystal.balanceMode() == 2) {
                transferRatesArrays.get(linkedPosCache.indexOf(linkedPos))[tick % 20] = 0;
                continue;
            }

            transferRatesArrays.get(linkedPosCache.indexOf(linkedPos))[tick % 20] = balanceTransfer(linkedCrystal, diff);
        }
    }

    protected int balanceTransfer(ICrystalLink sendTo, double capDiff) {
        long stored = getEnergyStored();

        if (stored <= 0) {
            return 0;
        }

        double transferCap = Math.min(getMaxEnergyStored(), sendTo.getMaxEnergyStored());
        int energyToEqual = (int) (((capDiff) * (double) sendTo.getMaxEnergyStored()) / 2.1);//The / 2 is needed! diff/2 == amount to equal
        double maxFlow = Math.min(energyToEqual, Math.min(transferCap, sendTo.getMaxEnergyStored() - sendTo.getEnergyStored()));

        double flowRate = Math.min(1, capDiff * 10D); //Offsets the flow rate so bellow 10% there is max flow.
        int transfer = (int) (flowRate * maxFlow);

        double minFlow = 0.002 * transferCap;

        if (transfer < minFlow) {
            transfer = (int) Math.min(minFlow, energyToEqual);
        }

        sendTo.modifyEnergyStored((transfer = (int)opStorage.modifyEnergyStored(-transfer)));

        return transfer;
    }

    public void rebuildTransferList() {
        transferRatesArrays.clear();
        flowRates.clear();
        for (int i = 0; i < linkedCrystals.size(); i++) {
            transferRatesArrays.add(new int[20]);
            flowRates.add((byte) 0);
        }
    }

    //endregion

    //region Linking

    @Nonnull
    @Override
    public List<BlockPos> getLinks() {
        if (linkedPosCache == null || linkedPosCache.size() != linkedCrystals.size()) { //TODO make sure linkedPosCache is set null client side when the links change!
            linkedPosCache = new LinkedList<>();
            for (Vec3B offset : linkedCrystals) {
                linkedPosCache.add(fromOffset(offset));
            }
            fxHandler.reloadConnections();
            updateBlock();
        }
        return linkedPosCache;
    }

    //Remember: This is called when a binder linked to "this" tile is used on another block.
    @Override
    public boolean binderUsed(Player player, BlockPos linkTarget, Direction sideClicked) {
        BlockEntity te = level.getBlockEntity(linkTarget);

        //region Check if the target device is valid
        if (!(te instanceof ICrystalLink)) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.device_invalid").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }
        //endregion

        ICrystalLink target = (ICrystalLink) te;

        //region Check if the devices are already linked and if they are break the link
        if (getLinks().contains(te.getBlockPos())) {
            breakLink(te.getBlockPos());
            target.breakLink(worldPosition);
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_broken").withStyle(ChatFormatting.GREEN), MSG_ID);
            return true;
        }
        //endregion

        //region Check if both devices to see if ether of them have reached their connection limit.
        if (getLinks().size() >= maxLinks()) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_limit_reached_this").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        } else if (target.getLinks().size() >= target.maxLinks()) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_limit_reached_target").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }
        //endregion

        //region Check both devices are in range
        if (!Utils.inRangeSphere(worldPosition, linkTarget, maxLinkRange())) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.this_range_limit").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        } else if (!Utils.inRangeSphere(worldPosition, linkTarget, target.maxLinkRange())) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.target_range_limit").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }
        //endregion

        //region All checks have passed. Make the link!
        if (!target.createLink(this)) {
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_failed_unknown").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }

        if (!createLink(target)) {
            //Ensure we don't leave a half linked device if this fails.
            target.breakLink(worldPosition);
            ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_failed_unknown").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }

        ChatHelper.sendDeDupeIndexed(player, Component.translatable("gui.draconicevolution.energy_net.devices_linked").withStyle(ChatFormatting.GREEN), MSG_ID);
        return true;
        //endregion
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        Vec3B offset = getOffset(((BlockEntity) otherCrystal).getBlockPos());
        linkedCrystals.add(offset);
        linkedPosCache = null;
        updateBlock();
        dirtyBlock();
        fxHandler.reloadConnections();
        return true;
    }

    @Override
    public void breakLink(BlockPos otherCrystal) {
        Vec3B offset = getOffset(otherCrystal);

        if (linkedCrystals.contains(offset)) {
            linkedCrystals.remove(offset);
        }

        linkedPosCache = null;
        updateBlock();
        dirtyBlock();
        fxHandler.reloadConnections();
    }

    //endregion

    //region ICrystalLink and some other stuffs...

    @Override
    public int maxLinks() {
        return MAX_LINKS.get(getCrystalType())[getTier()];
    }

    @Override
    public int maxLinkRange() {
        switch (getTier()) {
            case 0:
                return 32;
            case 1:
                return 64;
            case 2:
                return 127;
        }
        return 0;
    }

    @Override
    public int balanceMode() {
        return 1;
    }

    @Override
    public long getEnergyStored() {
        return opStorage.getEnergyStored();
    }

    @Override
    public long getMaxEnergyStored() {
        return opStorage.getMaxEnergyStored();
    }

    @Override
    public void modifyEnergyStored(long energy) {
        opStorage.modifyEnergyStored(energy);
    }

    @Override
    public int getTier() {
//        if (crystalTier == -1) {
//            TODO 1.13. REMOVE ALL BLOCK STATE USAGE FROM TILE ENTITIES! The number of stupid crashes caused by stupid mods doing stupid things is ridiculous. Not to mention the vanilla issues...
//            crystalTier = getBlockState().get(EnergyCrystal.TIER);
//        }
        return techLevel.index;
    }

    public abstract CrystalType getCrystalType();

    private int getCapacityForTier(int tier) {
        switch (tier) {
            case 0:
                return 4000000;
            case 1:
                return 16000000;
            case 2:
                return 64000000;
        }
        return 0;
    }

    //endregion

    //region Misc

    /**
     * Returns the offset of the target block relative to the position of this block.
     */
    public Vec3B getOffset(BlockPos target) {
        return new Vec3B(worldPosition.subtract(target));
    }

    /**
     * Returns the actual position of the target block based on its offset relative to this block.
     */
    public BlockPos fromOffset(Vec3B targetOffset) {
        return worldPosition.subtract(targetOffset.getPos());
    }

    public ENetFXHandler getFxHandler() {
        return fxHandler;
    }

    public byte calculateFlow(int index) {
        long sum = 0;
        for (int transfer : transferRatesArrays.get(index)) {
            sum += transfer;
        }

        double rf = (sum / 20L);
        double d = rf / ((getMaxEnergyStored() * 0.001) + rf);
        return (byte) (d * 255);
    }

    public void getLinkData(List<LinkData> data) {
        for (BlockPos target : getLinks()) {
            BlockEntity tile = level.getBlockEntity(target);
            if (tile == null) {
                continue;
            }

            LinkData ld = new LinkData();
            ld.displayName = tile.getClass().getSimpleName();

            long sum = 0;
            for (int transfer : transferRatesArrays.get(linkedPosCache.indexOf(target))) {
                sum += transfer;
            }

            ld.transferPerTick = (int) (sum / 20L);
            ld.linkTarget = target;
            ld.data = "Data...";
            data.add(ld);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
//        if (!world.isRemote) {
//            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_CRYSTAL, world, pos.getX(), pos.getY(), pos.getZ());
//        }
        return true;
    }

    public String getUnlocalizedName() {
        return "tile.draconicevolution:energy_crystal." + getCrystalType().getSerializedName() + "." + (getTier() == 0 ? "basic" : getTier() == 1 ? "wyvern" : "draconic") + ".name";
    }

    //endregion

    //region Render

    @Override
    @OnlyIn (Dist.CLIENT)
    public abstract CrystalFXBase createStaticFX();

    public void addDisplayData(List<Component> displayList) {
        double charge = MathUtils.round(((double) getEnergyStored() / (double) getMaxEnergyStored()) * 100D, 100);
        displayList.add(Component.translatable("gui.draconicevolution.energy_net.hud_charge").append(": " + Utils.formatNumber(getEnergyStored()) + " / " + Utils.formatNumber(getMaxEnergyStored()) + " RF [" + charge + "%]").withStyle(ChatFormatting.BLUE));
        displayList.add(Component.translatable("gui.draconicevolution.energy_net.hud_links").append(": " + getLinks().size() + " / " + maxLinks()).withStyle(ChatFormatting.GREEN));
    }

    @Override
    public ENetFXHandler createServerFXHandler() {
        return new ENetFXHandlerServer(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClient(this);
    }

    //endregion

    //region Sync/Save

    @Override
    public void writeExtraNBT(CompoundTag compound) {
        compound.putByte("tech_level", (byte) techLevel.ordinal());
        ListTag list = new ListTag();
        for (Vec3B vec : linkedCrystals) {
            list.add(new ByteArrayTag(new byte[]{vec.x, vec.y, vec.z}));
        }
        compound.put("linked_crystals", list);
        fxHandler.writeToNBT(compound);

        byte[] array = new byte[flowRates.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = flowRates.get(i);
        }
        compound.putByteArray("flow_rates", array);
        super.writeExtraNBT(compound);
    }

    @Override
    public void readExtraNBT(CompoundTag compound) {
        techLevel = TechLevel.values()[compound.getInt("tech_level")];
        ListTag list = compound.getList("linked_crystals", 7);
        linkedCrystals.clear();
        for (int i = 0; i < list.size(); i++) {
            byte[] data = ((ByteArrayTag) list.get(i)).getAsByteArray();
            linkedCrystals.add(new Vec3B(data[0], data[1], data[2]));
        }
        if (linkedPosCache != null) {
            linkedPosCache.clear();
        }
        fxHandler.readFromNBT(compound);

        if (compound.contains("flow_rates")) {
            byte[] array = compound.getByteArray("flow_rates");
            flowRates.clear();
            for (byte b : array) {
                flowRates.add(b);
            }
        }
        int cap = getCapacityForTier(getTier());
        opStorage.setCapacity(cap).setMaxTransfer(cap);
        super.readExtraNBT(compound);
    }

    @Override
    public void writeToItemStack(CompoundTag compound, boolean willHarvest) {
        super.writeToItemStack(compound, willHarvest);
    }

    @Override
    public void readFromItemStack(CompoundTag compound) {
        super.readFromItemStack(compound);
    }

    @Override
    public void onTilePlaced(BlockPlaceContext context, BlockState state) {
        int cap = getCapacityForTier(getTier());
        opStorage.setCapacity(cap).setMaxTransfer(cap);
    }

    boolean hashCached = false;
    int hashID = 0;

    @Override
    public int getIDHash() {
        if (!hashCached) {
            hashID = worldPosition.hashCode();
            hashCached = true;
        }
        return hashID;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!CrystalUpdateBatcher.ID_CRYSTAL_MAP.containsKey(getIDHash())) {
            CrystalUpdateBatcher.ID_CRYSTAL_MAP.put(getIDHash(), worldPosition);
        }
    }

    @Override
    public void onChunkUnloaded() {
        if (CrystalUpdateBatcher.ID_CRYSTAL_MAP.containsKey(getIDHash())) {
            CrystalUpdateBatcher.ID_CRYSTAL_MAP.remove(getIDHash());
        }
        fxHandler.tileUnload();
    }

    public void receiveBatchedUpdate(BatchedCrystalUpdate update) {
        fxHandler.updateReceived(update);
    }

    //endregion

    //region Container

    public Map<Integer, Integer> containerEnergyFlow = new HashMap<>();
//    public Map<Integer, String> tileNamesMap = new HashMap<>();

    public void detectAndSendContainerChanges(List<ContainerListener> listeners) {
        if (linkedCrystals.size() != transferRatesArrays.size() && !level.isClientSide) {
            rebuildTransferList();
        }

        List<BlockPos> positions = getLinks();
        ListTag list = new ListTag();

        for (BlockPos lPos : positions) {
            int index = positions.indexOf(lPos);

            if (!containerEnergyFlow.containsKey(index) || containerEnergyFlow.get(index) != getLinkFlow(index)) {
                containerEnergyFlow.put(index, getLinkFlow(index));
                CompoundTag data = new CompoundTag();
                data.putByte("I", (byte) index);
                data.putInt("E", getLinkFlow(index));
                list.add(data);
            }
        }

        if (!list.isEmpty()) {
            CompoundTag compound = new CompoundTag();
            compound.put("L", list);
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeCompoundNBT(compound), 0));
        } else if (containerEnergyFlow.size() > linkedCrystals.size()) {
            containerEnergyFlow.clear();
            sendUpdateToListeners(listeners, sendPacketToClient(output -> {
            }, 1));
        }
    }

    public void sendUpdateToListeners(List<ContainerListener> listeners, PacketCustom packet) {
        for (ContainerListener listener : listeners) {
            if (listener instanceof ServerPlayer) {
                packet.sendToPlayer((ServerPlayer) listener);
            }
        }
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 0) {
            CompoundTag compound = data.readCompoundNBT();
            ListTag list = compound.getList("L", 10);

            for (int i = 0; i < list.size(); i++) {
                CompoundTag tagData = list.getCompound(i);
                containerEnergyFlow.put((int) tagData.getByte("I"), tagData.getInt("E"));
            }
        }

//        Iterator<Map.Entry<Integer, Integer>> i = containerEnergyFlow.entrySet().iterator(); WTF was this random iterator? Did i forget to finish something?
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (id == 10) {
            int intValue = data.readInt();
            if (getLinks().size() > intValue && intValue >= 0) {
                BlockPos target = getLinks().get(intValue);
                breakLink(target);
                BlockEntity targetTile = level.getBlockEntity(target);
                if (targetTile instanceof ICrystalLink) {
                    ((ICrystalLink) targetTile).breakLink(worldPosition);
                }
            }
        } else if (id == 20) {
            List<BlockPos> links = new ArrayList<>(getLinks());
            for (BlockPos target : links) {
                breakLink(target);
                BlockEntity targetTile = level.getBlockEntity(target);
                if (targetTile instanceof ICrystalLink) {
                    ((ICrystalLink) targetTile).breakLink(worldPosition);
                }
            }
        }
    }

    public int getLinkFlow(int linkIndex) {
        if (transferRatesArrays.size() > linkIndex) {
            long sum = 0;
            for (int i : transferRatesArrays.get(linkIndex)) {
                sum += i;
            }
            return (int) (sum / 20L);
        }

        return 0;
    }

    //endregion

    //region Capability

//    @Override
//    public boolean hasCapability(Capability<?> capability, Direction facing) {
//        return (capability == CapabilityEnergy.ENERGY && (facing == null || canConnectEnergy(facing))) || super.hasCapability(capability, facing);
//    }
//
//    @Override
//    public <T> T getCapability(Capability<T> capability, Direction facing) {
//        if (capability == CapabilityEnergy.ENERGY && (facing == null || canConnectEnergy(facing))) {
//            return CapabilityEnergy.ENERGY.cast(new EnergyHandlerWrapper(this, facing));
//        }
//
//        return super.getCapability(capability, facing);
//    }

    //endregion

    @Override
    public LinkedList<Byte> getFlowRates() {
        return flowRates;
    }

    public static class LinkData {
        public String displayName;
        public int transferPerTick;
        public BlockPos linkTarget;
        public String data;

        public LinkData() {
        }

        public LinkData(String displayName, int transferPerTick, BlockPos linkTarget, String data) {
            this.displayName = displayName;
            this.transferPerTick = transferPerTick;
            this.linkTarget = linkTarget;
            this.data = data;
        }

        public void toBytes(ByteBuf buf) {
            //TODO ByteBufUtils
//            ByteBufUtils.writeUTF8String(buf, displayName);
            buf.writeInt(transferPerTick);
            buf.writeLong(linkTarget.asLong());
//            ByteBufUtils.writeUTF8String(buf, data);
        }

        public static LinkData fromBytes(ByteBuf buf) {
            LinkData data = new LinkData();
//            data.displayName = ByteBufUtils.readUTF8String(buf);
            data.transferPerTick = buf.readInt();
            data.linkTarget = BlockPos.of(buf.readLong());
//            data.data = ByteBufUtils.readUTF8String(buf);
            return data;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
