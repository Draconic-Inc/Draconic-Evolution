package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClientWireless;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServerWireless;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.*;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalWirelessIO extends TileCrystalBase {

    protected Map<Vec3B, Direction> receiverSideMap = new HashMap<>();
    protected LinkedList<Vec3B> linkedReceivers = new LinkedList<>();
    protected LinkedList<BlockPos> receiverCache = null;
    protected Map<BlockPos, Direction> receiverFaceCache = null;
    protected List<LinkedDevice> fastList = new ArrayList<>();
    protected List<LinkedDevice> slowList = new ArrayList<>();
    public LinkedList<int[]> receiverTransferRates = new LinkedList<>();
    public LinkedList<Byte> receiverFlowRates = new LinkedList<>();
    public final ManagedBool useUpdateOptimisation = dataManager.register(new ManagedBool("transport_state", true, DataFlags.SAVE_BOTH_SYNC_CONTAINER));
    public final ManagedBool inputMode = dataManager.register(new ManagedBool("input_mode", DataFlags.SAVE_BOTH_SYNC_TILE));

    public TileCrystalWirelessIO(BlockPos pos, BlockState state) {
        super(DEContent.TILE_WIRELESS_CRYSTAL.get(), pos, state);
    }

    public TileCrystalWirelessIO(TechLevel techLevel, BlockPos pos, BlockState state) {
        super(DEContent.TILE_WIRELESS_CRYSTAL.get(), techLevel, pos, state);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_WIRELESS_CRYSTAL, CapabilityOP.BLOCK);
    }

    //region Energy Update

    @Override
    public void tick() {
        if (!level.isClientSide) {
            updateEnergyFlow();
        }

        super.tick();
    }

    private void updateEnergyFlow() {
        if (receiverTransferRates.size() != linkedReceivers.size()) {
            rebuildReceiverTransferList();
        }

        getReceiversFaces();//Called to update caches if caches need to be updated.

        List<LinkedDevice> moveToSlow = new ArrayList<>();
        for (LinkedDevice receiver : fastList) {
            if (!updateDevice(receiver)) {
                removeReceiver(receiver.pos);
                return;
            }

            if (receiver.timeOut > 40) {
                moveToSlow.add(receiver);
            }
        }

        if (tick % 80 == 0) {
            List<LinkedDevice> moveToFast = new ArrayList<>();
            for (LinkedDevice receiver : slowList) {
                if (!updateDevice(receiver)) {
                    removeReceiver(receiver.pos);
                    return;
                }

                if (receiver.timeOut == 0) {
                    moveToFast.add(receiver);
                }
            }

            if (!moveToFast.isEmpty()) {
                fastList.addAll(moveToFast);
                slowList.removeAll(moveToFast);
            }
        }

        if (!moveToSlow.isEmpty()) {
            slowList.addAll(moveToSlow);
            fastList.removeAll(moveToSlow);
        }

        if (!level.isClientSide && DEEventHandler.serverTicks % 10 == 0) {
            receiverFlowRates.clear();
            for (int i = 0; i < linkedReceivers.size(); i++) {
                receiverFlowRates.add(flowConversion(receiverTransfer(i)));
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            inputMode.invert();
            return true;
        }
        return super.onBlockActivated(state, player, handIn, hit);
    }

    /**
     * Attempts to send energy to this receiver. Returns false if the receiver is nolonger valid.
     */
    protected boolean updateDevice(LinkedDevice receiver) {
        if (!receiver.isLinkValid(level)) {
            return receiver.invalidTime++ < 100;
        }
        receiver.invalidTime = 0;

        BlockEntity tile = receiver.getCachedTile();
        if (tile == null) {
            if (receiver.timeOut < 40) {
                receiver.timeOut = 40;
            }
            else {
                receiver.timeOut++;
            }
            return true;
        }

        long transferred;
        if (inputMode.get()) {
            transferred = EnergyUtils.extractEnergy(tile, opStorage.receiveEnergy(getMaxWirelessTransfer(), true), receiver.side, false);
            opStorage.receiveOP(transferred, false);
        }
        else {
            transferred = EnergyUtils.insertEnergy(tile, opStorage.extractEnergy(getMaxWirelessTransfer(), true), receiver.side, false);
            opStorage.extractOP(transferred, false);
        }

        if (transferred > 0) {
            receiver.timeOut = 0;
        }
        else {
            receiver.timeOut++;
        }

        receiverTransferRates.get(receiver.index)[tick % 20] = (int) Math.min(transferred, Integer.MAX_VALUE);

        return true;
    }

    public void rebuildReceiverTransferList() {
        receiverTransferRates.clear();
        receiverFlowRates.clear();
        for (int i = 0; i < linkedReceivers.size(); i++) {
            receiverTransferRates.add(new int[20]);
            receiverFlowRates.add((byte) 0);
        }
    }

    public int getMaxWirelessTransfer() {
        return getTier() == 0 ? 32000 : getTier() == 1 ? 128000 : 512000;
    }

    public int receiverTransfer(int index) {
        long sum = 0;
        for (int transfer : receiverTransferRates.get(index)) {
            sum += transfer;
        }
        return (int) (sum / 20L);
    }

    public byte flowConversion(int transferRate) {
        double d = (double) transferRate / ((getMaxWirelessTransfer() * 0.01) + (double) transferRate);
        return (byte) (d * 255);
    }

    //endregion

    //region Linking

    @Override
    public boolean binderUsed(Player player, BlockPos linkTarget, Direction sideClicked) {
        BlockEntity tile = level.getBlockEntity(linkTarget);
        if (tile == null || tile instanceof ICrystalLink) {
            return super.binderUsed(player, linkTarget, sideClicked);
        }

        if (level.isClientSide) {
            return true;
        }

        Vec3B offset = getOffset(linkTarget);

        if (linkedReceivers.contains(offset)) {
            removeReceiver(linkTarget);
            ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.link_broken").withStyle(ChatFormatting.GREEN), MSG_ID);
            return true;
        }

        if (inputMode.get()) {
            if (!EnergyUtils.canExtractEnergy(tile, sideClicked)) {
                if (EnergyUtils.getStorage(tile, sideClicked) != null) {
                    ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.side_can_not_extract").withStyle(ChatFormatting.RED), MSG_ID);
                    return false;
                }
                return super.binderUsed(player, linkTarget, sideClicked);
            }
        }
        else {
            if (!EnergyUtils.canReceiveEnergy(tile, sideClicked)) {
                if (EnergyUtils.getStorage(tile, sideClicked) != null) {
                    ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.side_can_not_receive").withStyle(ChatFormatting.RED), MSG_ID);
                    return false;
                }
                return super.binderUsed(player, linkTarget, sideClicked);
            }
        }

        if (linkedReceivers.size() >= getMaxReceivers()) {
            ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.max_receivers").withStyle(ChatFormatting.RED), MSG_ID);
            return false;
        }

        addReceiver(linkTarget, sideClicked);
        ChatHelper.sendIndexed(player, Component.translatable("gui.draconicevolution.energy_net.devices_linked").withStyle(ChatFormatting.GREEN), MSG_ID);

        return true;
    }

    public List<BlockPos> getReceivers() {
        if (receiverCache == null || receiverCache.size() != linkedReceivers.size()) {
            reCachePositions();
        }
        return receiverCache;
    }

    public Map<BlockPos, Direction> getReceiversFaces() {
        if (receiverCache == null || receiverCache.size() != linkedReceivers.size() || receiverFaceCache == null || receiverFaceCache.size() != linkedReceivers.size()) {
            reCachePositions();
        }
        return receiverFaceCache;
    }

    public int getMaxReceivers() {
        return getTier() == 0 ? 16 : getTier() == 1 ? 32 : 64;
    }

    private void reCachePositions() {
        receiverCache = new LinkedList<>();
        receiverFaceCache = new HashMap<>();
        fastList.clear();
        slowList.clear();
        for (Vec3B offset : linkedReceivers) {
            receiverCache.add(fromOffset(offset));
            receiverFaceCache.put(fromOffset(offset), receiverSideMap.get(offset));
            fastList.add(new LinkedDevice(linkedReceivers.indexOf(offset), fromOffset(offset), receiverSideMap.get(offset)));
        }

        updateBlock();
    }

    //endregion

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getCrystalType() {
        return EnergyCrystal.CrystalType.WIRELESS;
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public CrystalFXBase createStaticFX() {
        return new CrystalFXRing((ClientLevel)level, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        Vec3D thisVec = Vec3D.getCenter(worldPosition);
        Vec3D targVec = Vec3D.getCenter(linkTo);
        double dist = thisVec.distXZ(targVec);
        double offM = 0.4D;

        if (dist == 0) {
            if (worldPosition.getY() > linkTo.getY()) {
                return thisVec.subtract(0, 0.4, 0);
            }
            else {
                return thisVec.subtract(0, -0.4, 0);
            }
        }

        double xDist = thisVec.x - targVec.x;
        double zDist = thisVec.z - targVec.z;
        double xOff = xDist / dist;
        double zOff = zDist / dist;

        return thisVec.subtract(xOff * offM, 0, zOff * offM);
    }

    @Override
    public boolean renderBeamTermination() {
        return true;
    }

    @Override
    public ENetFXHandler createServerFXHandler() {
        return new ENetFXHandlerServerWireless(this);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClientWireless(this);
    }

    @Override
    public void addDisplayData(List<Component> displayList) {
        super.addDisplayData(displayList);
        displayList.add(Component.translatable("gui.draconicevolution.energy_net.hud_wireless_links").append(": " + getReceivers().size() + " / " + getMaxReceivers()).withStyle(ChatFormatting.GREEN));

        //TODO. I dont think i need to tell myself to stop this shit when i re write but... I need to stop this shit when i re write! (Injecting colours into translations like this)
        ChatFormatting colour = !inputMode.get() ? ChatFormatting.GOLD : ChatFormatting.DARK_AQUA;
        displayList.add(Component.translatable("gui.draconicevolution.energy_net.io_output_" + !inputMode.get(), colour));
    }

    //endregion

    //region sync/save

    public void addReceiver(BlockPos pos, Direction side) {
        Vec3B offset = getOffset(pos);
        linkedReceivers.add(offset);
        receiverSideMap.put(offset, side);
        reCachePositions();
        updateBlock();
    }

    public void removeReceiver(BlockPos pos) {
        Vec3B offset = getOffset(pos);
        linkedReceivers.remove(offset);
        receiverSideMap.remove(offset);
        reCachePositions();
        updateBlock();
    }

    @Override
    public void writeExtraNBT(CompoundTag compound) {
        super.writeExtraNBT(compound);
        ListTag list = new ListTag();
        for (Vec3B vec : linkedReceivers) {
            CompoundTag receiver = new CompoundTag();
            receiver.putByteArray("offset", new byte[]{vec.x, vec.y, vec.z});
            receiver.putByte("side", (byte) receiverSideMap.get(vec).get3DDataValue());
            list.add(receiver);
        }
        compound.put("linked_receivers", list);
    }

    @Override
    public void readExtraNBT(CompoundTag compound) {
        super.readExtraNBT(compound);
        ListTag list = compound.getList("linked_receivers", 10);
        linkedReceivers.clear();
        receiverSideMap.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag receiver = list.getCompound(i);
            byte[] offset = receiver.getByteArray("offset");
            Vec3B vec = new Vec3B(offset[0], offset[1], offset[2]);
            linkedReceivers.add(vec);
            receiverSideMap.put(vec, Direction.from3DDataValue(receiver.getByte("side")));
        }

        receiverCache = null;
    }

    //endregion

    //region Container

    public Map<Integer, Integer> containerReceiverFlow = new HashMap<>();
//    public Map<Integer, String> tileNamesMap = new HashMap<>();

    @Override
    public void detectAndSendContainerChanges(List<ContainerListener> listeners) {
        super.detectAndSendContainerChanges(listeners);
        if (linkedReceivers.size() != receiverTransferRates.size() && !level.isClientSide) {
            rebuildReceiverTransferList();
        }

        List<BlockPos> positions = getReceivers();
        ListTag list = new ListTag();

        for (BlockPos lPos : positions) {
            int index = positions.indexOf(lPos);

            if (!containerReceiverFlow.containsKey(index) || containerReceiverFlow.get(index) != receiverTransfer(index)) {
                containerReceiverFlow.put(index, receiverTransfer(index));
                CompoundTag data = new CompoundTag();
                data.putByte("I", (byte) index);
                data.putInt("E", receiverTransfer(index));
                list.add(data);
            }
        }

        CompoundTag compound = new CompoundTag();
        if (!list.isEmpty()) {
            compound.put("L", list);
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeCompoundNBT(compound), 1));
        }
        else if (containerReceiverFlow.size() > linkedReceivers.size()) {
            containerReceiverFlow.clear();
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeCompoundNBT(compound), 1));
        }
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        super.receivePacketFromServer(data, id);

        if (id == 1) {
            CompoundTag compound = data.readCompoundNBT();
            ListTag list = compound.getList("L", 10);

            for (int i = 0; i < list.size(); i++) {
                CompoundTag tagData = list.getCompound(i);
                containerReceiverFlow.put((int) tagData.getByte("I"), tagData.getInt("E"));
            }
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        super.receivePacketFromClient(data, client, id);

        if (id == 11) {
            int intValue = data.readInt();
            if (getReceivers().size() > intValue && intValue >= 0) {
                BlockPos target = getReceivers().get(intValue);
                removeReceiver(target);
            }
        }
        else if (id == 21) {
            List<BlockPos> links = new ArrayList<>(getReceivers());
            for (BlockPos target : links) {
                removeReceiver(target);
            }
        }
    }

    //endregion

    //region Subs

    private class LinkedDevice {
        public final int index;
        public final BlockPos pos;
        private Direction side;
        public int timeOut = 0;
        private BlockEntity tileCache = null;
        private int invalidTime = 0;

        public LinkedDevice(int index, BlockPos pos, Direction side) {
            this.index = index;
            this.pos = pos;
            this.side = side;
        }

        public boolean isLinkValid(Level world) {
            tileCache = world.getBlockEntity(pos);

            if ((tileCache == null || EnergyUtils.getStorage(tileCache, side) == null) && level.isLoaded(pos)) {
                return false;
            }

            return true;
        }

        /**
         * Must be called immediately after isLinkValid
         */
        public BlockEntity getCachedTile() {
            return tileCache;
        }
    }

    //endregion
}
