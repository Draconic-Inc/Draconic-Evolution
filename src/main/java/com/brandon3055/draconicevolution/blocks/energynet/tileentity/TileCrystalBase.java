package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.ITilePlaceListener;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.ID_CRYSTAL_MAP;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public abstract class TileCrystalBase extends TileBCore implements ITilePlaceListener, ICrystalLink, ITickableTileEntity, IActivatableTile, IENetEffectTile {

    //region Stats

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
    protected OPStorage opStorage = new OPStorage(0);
    protected ENetFXHandler fxHandler;
    protected TechLevel techLevel;

    public TileCrystalBase(TileEntityType<?> tileEntityTypeIn) {
        this(tileEntityTypeIn, TechLevel.DRACONIUM);
    }

    public TileCrystalBase(TileEntityType<?> tileEntityTypeIn, TechLevel techLevel) {
        super(tileEntityTypeIn);
        this.techLevel = techLevel;
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);

        capManager.setInternalManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();
    }

    //region Energy Balance

    @Override
    public void tick() {
        super.tick();
        if (linkedCrystals.size() != transferRatesArrays.size() && !world.isRemote) {
            rebuildTransferList();
        }

        balanceLinkedDevices();
        fxHandler.update();

        if (!world.isRemote && DEEventHandler.serverTicks % 10 == 0) {
            flowRates.clear();
            for (int i = 0; i < linkedCrystals.size(); i++) {
                flowRates.add(calculateFlow(i));
            }
            fxHandler.detectAndSendChanges();

//            if (Utils.getClosestPlayer(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2) != null) {
//                LogHelper.dev(flowRates + " " + linkedCrystals);
//            }
        }

//        if (Utils.getClosestPlayer(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2) != null) {
//            LogHelper.dev(flowRates + " " + linkedCrystals);
//        }

        tick++;
    }

    public void balanceLinkedDevices() {
        if (world.isRemote) {
            return;
        }
        for (BlockPos linkedPos : getLinks()) {
            TileEntity linkedTile = world.getTileEntity(linkedPos);

            if (!(linkedTile instanceof ICrystalLink)) {
                if (world.isBlockLoaded(linkedPos)) {
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
    public boolean binderUsed(PlayerEntity player, BlockPos linkTarget, Direction sideClicked) {
        TileEntity te = world.getTileEntity(linkTarget);

        //region Check if the target device is valid
        if (!(te instanceof ICrystalLink)) {
            ChatHelper.indexedTrans(player, "eNet.de.deviceInvalid.info", TextFormatting.RED, -442611624);
            return false;
        }
        //endregion

        ICrystalLink target = (ICrystalLink) te;

        //region Check if the devices are already linked and if they are break the link
        if (getLinks().contains(te.getPos())) {
            breakLink(te.getPos());
            target.breakLink(pos);
            ChatHelper.indexedTrans(player, "eNet.de.linkBroken.info", TextFormatting.GREEN, -442611624);
            return true;
        }
        //endregion

        //region Check if both devices to see if ether of them have reached their connection limit.
        if (getLinks().size() >= maxLinks()) {
            ChatHelper.indexedTrans(player, "eNet.de.linkLimitReachedThis.info", TextFormatting.RED, -442611624);
            return false;
        } else if (target.getLinks().size() >= target.maxLinks()) {
            ChatHelper.indexedTrans(player, "eNet.de.linkLimitReachedTarget.info", TextFormatting.RED, -442611624);
            return false;
        }
        //endregion

        //region Check both devices are in range
        if (!Utils.inRangeSphere(pos, linkTarget, maxLinkRange())) {
            ChatHelper.indexedTrans(player, "eNet.de.thisRangeLimit.info", TextFormatting.RED, -442611624);
            return false;
        } else if (!Utils.inRangeSphere(pos, linkTarget, target.maxLinkRange())) {
            ChatHelper.indexedTrans(player, "eNet.de.targetRangeLimit.info", TextFormatting.RED, -442611624);
            return false;
        }
        //endregion

        //region All checks have passed. Make the link!
        if (!target.createLink(this)) {
            ChatHelper.indexedTrans(player, "eNet.de.linkFailedUnknown.info", TextFormatting.RED, -442611624);
            return false;
        }

        if (!createLink(target)) {
            //Ensure we don't leave a half linked device if this fails.
            target.breakLink(pos);
            ChatHelper.indexedTrans(player, "eNet.de.linkFailedUnknown.info", TextFormatting.RED, -442611624);
            return false;
        }

        ChatHelper.indexedTrans(player, "eNet.de.devicesLinked.info", TextFormatting.GREEN, -442611624);
        return true;
        //endregion
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        Vec3B offset = getOffset(((TileEntity) otherCrystal).getPos());
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

    //region IEnergyHandler

//    @Override
//    public boolean canConnectEnergy(Direction from) {
//        return false;
//    }
//
//    @Override
//    public int getEnergyStored(Direction from) {
//        return getEnergyStored();
//    }
//
//    @Override
//    public int getMaxEnergyStored(Direction from) {
//        return getMaxEnergyStored();
//    }

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
        return new Vec3B(pos.subtract(target));
    }

    /**
     * Returns the actual position of the target block based on its offset relative to this block.
     */
    public BlockPos fromOffset(Vec3B targetOffset) {
        return pos.subtract(targetOffset.getPos());
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
            TileEntity tile = world.getTileEntity(target);
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
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        if (!world.isRemote) {
//            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_CRYSTAL, world, pos.getX(), pos.getY(), pos.getZ());
//        }
        return false;
    }

    public String getUnlocalizedName() {
        return "tile.draconicevolution:energy_crystal." + getCrystalType().getName() + "." + (getTier() == 0 ? "basic" : getTier() == 1 ? "wyvern" : "draconic") + ".name";
    }

    //endregion

    //region Render

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 0 || !DEShaders.useShaders();
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public abstract CrystalFXBase createStaticFX();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    @OnlyIn(Dist.CLIENT)
    public void addDisplayData(List<String> displayList) {
        double charge = MathUtils.round(((double) getEnergyStored() / (double) getMaxEnergyStored()) * 100D, 100);
        displayList.add(TextFormatting.BLUE + I18n.format("eNet.de.hudCharge.info") + ": " + Utils.formatNumber(getEnergyStored()) + " / " + Utils.formatNumber(getMaxEnergyStored()) + " RF [" + charge + "%]");
        displayList.add(TextFormatting.GREEN + I18n.format("eNet.de.hudLinks.info") + ": " + getLinks().size() + " / " + maxLinks() + "");
//        if (BrandonsCore.proxy.getClientPlayer().isSneaking()) {
//            for (BlockPos lPos : getLinks()) {
//                displayList.add(TextFormatting.GRAY + " " + String.format("[x:%s, y:%s, z:%s]", lPos.getX(), lPos.getY(), lPos.getZ()));
//            }
//        }
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
    public void writeExtraNBT(CompoundNBT compound) {
        compound.putByte("tech_level", (byte) techLevel.ordinal());
        ListNBT list = new ListNBT();
        for (Vec3B vec : linkedCrystals) {
            list.add(new ByteArrayNBT(new byte[]{vec.x, vec.y, vec.z}));
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
    public void readExtraNBT(CompoundNBT compound) {
        techLevel = TechLevel.values()[compound.getInt("tech_level")];
        ListNBT list = compound.getList("linked_crystals", 7);
        linkedCrystals.clear();
        for (int i = 0; i < list.size(); i++) {
            byte[] data = ((ByteArrayNBT) list.get(i)).getByteArray();
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
    public void writeToItemStack(CompoundNBT compound, boolean willHarvest) {
        super.writeToItemStack(compound, willHarvest);
    }

    @Nullable
    @Override
    public void readFromItemStack(CompoundNBT compound) {
        super.readFromItemStack(compound);
    }

    @Override
    public void onTilePlaced(BlockItemUseContext context, BlockState state) {
        int cap = getCapacityForTier(getTier());
        opStorage.setCapacity(cap).setMaxTransfer(cap);
    }

    boolean hashCached = false;
    int hashID = 0;

    @Override
    public int getIDHash() {
        if (!hashCached) {
            hashID = pos.hashCode();
            hashCached = true;
        }
        return hashID;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!ID_CRYSTAL_MAP.containsKey(getIDHash())) {
            ID_CRYSTAL_MAP.put(getIDHash(), pos);
        }
    }

    @Override
    public void onChunkUnloaded() {
        if (ID_CRYSTAL_MAP.containsKey(getIDHash())) {
            ID_CRYSTAL_MAP.remove(getIDHash());
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

    public void detectAndSendContainerChanges(List<IContainerListener> listeners) {
        if (linkedCrystals.size() != transferRatesArrays.size() && !world.isRemote) {
            rebuildTransferList();
        }

        List<BlockPos> positions = getLinks();
        ListNBT list = new ListNBT();

        for (BlockPos lPos : positions) {
            int index = positions.indexOf(lPos);

            if (!containerEnergyFlow.containsKey(index) || containerEnergyFlow.get(index) != getLinkFlow(index)) {
                containerEnergyFlow.put(index, getLinkFlow(index));
                CompoundNBT data = new CompoundNBT();
                data.putByte("I", (byte) index);
                data.putInt("E", getLinkFlow(index));
                list.add(data);
            }
        }

        if (!list.isEmpty()) {
            CompoundNBT compound = new CompoundNBT();
            compound.put("L", list);
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeCompoundNBT(compound), 0));
        } else if (containerEnergyFlow.size() > linkedCrystals.size()) {
            containerEnergyFlow.clear();
            sendUpdateToListeners(listeners, sendPacketToClient(output -> {
            }, 1));
        }
    }

    public void sendUpdateToListeners(List<IContainerListener> listeners, PacketCustom packet) {
        for (IContainerListener listener : listeners) {
            if (listener instanceof ServerPlayerEntity) {
                packet.sendToPlayer((ServerPlayerEntity) listener);
            }
        }
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 0) {
            CompoundNBT compound = data.readCompoundNBT();
            ListNBT list = compound.getList("L", 10);

            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tagData = list.getCompound(i);
                containerEnergyFlow.put((int) tagData.getByte("I"), tagData.getInt("E"));
            }
        }

//        Iterator<Map.Entry<Integer, Integer>> i = containerEnergyFlow.entrySet().iterator(); WTF was this random iterator? Did i forget to finish something?
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
        if (id == 10) {
            int intValue = data.readInt();
            if (getLinks().size() > intValue && intValue >= 0) {
                BlockPos target = getLinks().get(intValue);
                breakLink(target);
                TileEntity targetTile = world.getTileEntity(target);
                if (targetTile instanceof ICrystalLink) {
                    ((ICrystalLink) targetTile).breakLink(pos);
                }
            }
        } else if (id == 20) {
            List<BlockPos> links = new ArrayList<>(getLinks());
            for (BlockPos target : links) {
                breakLink(target);
                TileEntity targetTile = world.getTileEntity(target);
                if (targetTile instanceof ICrystalLink) {
                    ((ICrystalLink) targetTile).breakLink(pos);
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
            buf.writeLong(linkTarget.toLong());
//            ByteBufUtils.writeUTF8String(buf, data);
        }

        public static LinkData fromBytes(ByteBuf buf) {
            LinkData data = new LinkData();
//            data.displayName = ByteBufUtils.readUTF8String(buf);
            data.transferPerTick = buf.readInt();
            data.linkTarget = BlockPos.fromLong(buf.readLong());
//            data.data = ByteBufUtils.readUTF8String(buf);
            return data;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
