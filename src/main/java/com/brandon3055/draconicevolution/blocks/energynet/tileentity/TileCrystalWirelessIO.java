package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClientWireless;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServerWireless;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class TileCrystalWirelessIO extends TileCrystalBase {

    protected Map<Vec3B, EnumFacing> receiverSideMap = new HashMap<>();
    protected LinkedList<Vec3B> linkedReceivers = new LinkedList<>();
    protected LinkedList<BlockPos> receiverCache = null;
    protected Map<BlockPos, EnumFacing> receiverFaceCache = null;
    protected List<LinkedReceiver> fastList = new ArrayList<>();
    protected List<LinkedReceiver> slowList = new ArrayList<>();
    public LinkedList<int[]> receiverTransferRates = new LinkedList<>();
    public LinkedList<Byte> receiverFlowRates = new LinkedList<>();
    public final ManagedBool useUpdateOptimisation = dataManager.register("transportState", new ManagedBool(true)).syncViaContainer().saveToTile().saveToItem().finish();

//    public final ManagedBool useUpdateOptimisation = new ManagedBool(true, false, true);

    public TileCrystalWirelessIO() {
        super();
    }

    //region Energy Update

    @Override
    public void update() {
        if (!world.isRemote) {
            updateEnergyFlow();
        }

        super.update();
    }

    private void updateEnergyFlow() {
        if (receiverTransferRates.size() != linkedReceivers.size()) {
            rebuildReceiverTransferList();
        }

        getReceiversFaces();//Called to update caches if caches need to be updated.

        List<LinkedReceiver> moveToSlow = new ArrayList<>();
        for (LinkedReceiver receiver : fastList) {
            if (!updateReceiver(receiver)) {
                removeReceiver(receiver.pos);
                return;
            }

            if (receiver.timeOut > 40) {
                moveToSlow.add(receiver);
            }
        }

        if (tick % 80 == 0) {
            List<LinkedReceiver> moveToFast = new ArrayList<>();
            for (LinkedReceiver receiver : slowList) {
                if (!updateReceiver(receiver)) {
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

        if (!world.isRemote && DEEventHandler.serverTicks % 10 == 0) {
            receiverFlowRates.clear();
            for (int i = 0; i < linkedReceivers.size(); i++) {
                receiverFlowRates.add(flowConversion(receiverTransfer(i)));
            }
        }
    }

    /**
     * Attempts to send energy to this receiver. Returns false if the receiver is nolonger valid.
     */
    protected boolean updateReceiver(LinkedReceiver receiver) {
        if (!receiver.isLinkValid(world)) {
            return false;
        }

        TileEntity tile = receiver.getCachedTile();
        if (tile == null) {
            if (receiver.timeOut < 40) {
                receiver.timeOut = 40;
            }
            else {
                receiver.timeOut++;
            }
            return true;
        }

        int inserted = EnergyHelper.insertEnergy(tile, energyStorage.extractEnergy(getMaxWirelessTransfer(), true), receiver.side, false);
        energyStorage.extractEnergy(inserted, false);

        if (inserted > 0) {
            receiver.timeOut = 0;
        }
        else {
            receiver.timeOut++;
        }

        receiverTransferRates.get(receiver.index)[tick % 20] = inserted;

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
    public boolean binderUsed(EntityPlayer player, BlockPos linkTarget, EnumFacing sideClicked) {
        TileEntity tile = world.getTileEntity(linkTarget);
        if (tile == null || tile instanceof ICrystalLink) {
            return super.binderUsed(player, linkTarget, sideClicked);
        }

        Vec3B offset = getOffset(linkTarget);

        if (linkedReceivers.contains(offset)) {
            removeReceiver(linkTarget);
            ChatHelper.indexedTrans(player, "eNet.de.linkBroken.info", TextFormatting.GREEN, -442611624);
            return true;
        }

        if (!EnergyHelper.canReceiveEnergy(tile, sideClicked)) {
            if (EnergyHelper.isEnergyTile(tile)) {
                ChatHelper.indexedTrans(player, "eNet.de.sideCanNotReceive.info", TextFormatting.RED, -442611624);
                return false;
            }
            return super.binderUsed(player, linkTarget, sideClicked);
        }

        if (linkedReceivers.size() >= getMaxReceivers()) {
            ChatHelper.indexedTrans(player, "eNet.de.maxReceivers.info", TextFormatting.RED, -442611624);
            return false;
        }

        addReceiver(linkTarget, sideClicked);
        ChatHelper.indexedTrans(player, "eNet.de.devicesLinked.info", TextFormatting.GREEN, -442611624);

        return true;
    }

    public List<BlockPos> getReceivers() {
        if (receiverCache == null || receiverCache.size() != linkedReceivers.size()) {
            reCachePositions();
        }
        return receiverCache;
    }

    public Map<BlockPos, EnumFacing> getReceiversFaces() {
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
            fastList.add(new LinkedReceiver(linkedReceivers.indexOf(offset), fromOffset(offset), receiverSideMap.get(offset)));
        }

        updateBlock();
    }

    //endregion

    //region Rendering

    @Override
    public EnergyCrystal.CrystalType getType() {
        return EnergyCrystal.CrystalType.WIRELESS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrystalGLFXBase createStaticFX() {
        return new CrystalFXRing(world, this);
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        Vec3D thisVec = Vec3D.getCenter(pos);
        Vec3D targVec = Vec3D.getCenter(linkTo);
        double dist = thisVec.distXZ(targVec);
        double offM = 0.4D;

        if (dist == 0) {
            if (pos.getY() > linkTo.getY()) {
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

    @SideOnly(Side.CLIENT)
    @Override
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClientWireless(this);
    }

    @Override
    public void addDisplayData(List<String> displayList) {
        super.addDisplayData(displayList);
        displayList.add(TextFormatting.GREEN + I18n.format("eNet.de.hudWirelessLinks.info") + ": " + getReceivers().size() + " / " + getMaxReceivers());
    }

    //endregion

    //region sync/save

    public void addReceiver(BlockPos pos, EnumFacing side) {
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
    public void writeExtraNBT(NBTTagCompound compound) {
        super.writeExtraNBT(compound);
        NBTTagList list = new NBTTagList();
        for (Vec3B vec : linkedReceivers) {
            NBTTagCompound receiver = new NBTTagCompound();
            receiver.setByteArray("Offset", new byte[]{vec.x, vec.y, vec.z});
            receiver.setByte("Side", (byte) receiverSideMap.get(vec).getIndex());
            list.appendTag(receiver);
        }
        compound.setTag("LinkedReceivers", list);
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        super.readExtraNBT(compound);
        NBTTagList list = compound.getTagList("LinkedReceivers", 10);
        linkedReceivers.clear();
        receiverSideMap.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound receiver = list.getCompoundTagAt(i);
            byte[] offset = receiver.getByteArray("Offset");
            Vec3B vec = new Vec3B(offset[0], offset[1], offset[2]);
            linkedReceivers.add(vec);
            receiverSideMap.put(vec, EnumFacing.getFront(receiver.getByte("Side")));
        }

        receiverCache = null;
    }

    //endregion

    //region Container

    public Map<Integer, Integer> containerReceiverFlow = new HashMap<>();
//    public Map<Integer, String> tileNamesMap = new HashMap<>();

    @Override
    public void detectAndSendContainerChanges(List<IContainerListener> listeners) {
        super.detectAndSendContainerChanges(listeners);
        if (linkedReceivers.size() != receiverTransferRates.size() && !world.isRemote) {
            rebuildReceiverTransferList();
        }

        List<BlockPos> positions = getReceivers();
        NBTTagList list = new NBTTagList();

        for (BlockPos lPos : positions) {
            int index = positions.indexOf(lPos);

            if (!containerReceiverFlow.containsKey(index) || containerReceiverFlow.get(index) != receiverTransfer(index)) {
                containerReceiverFlow.put(index, receiverTransfer(index));
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("I", (byte) index);
                data.setInteger("E", receiverTransfer(index));
                list.appendTag(data);
            }
        }

        if (!list.hasNoTags()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("L", list);
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeNBTTagCompound(compound), 1));
        }
        else if (containerReceiverFlow.size() > linkedReceivers.size()) {
            containerReceiverFlow.clear();
            sendUpdateToListeners(listeners, sendPacketToClient(output -> output.writeNBTTagCompound(new NBTTagCompound()), 1));
        }
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        super.receivePacketFromServer(data, id);

        if (id == 1) {
            NBTTagCompound compound = data.readNBTTagCompound();
            NBTTagList list = compound.getTagList("L", 10);

            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tagData = list.getCompoundTagAt(i);
                containerReceiverFlow.put((int) tagData.getByte("I"), tagData.getInteger("E"));
            }
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
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

    private class LinkedReceiver {
        public final int index;
        public final BlockPos pos;
        private EnumFacing side;
        public int timeOut = 0;
        private TileEntity tileCache = null;

        public LinkedReceiver(int index, BlockPos pos, EnumFacing side) {
            this.index = index;
            this.pos = pos;
            this.side = side;
        }

        public boolean isLinkValid(World world) {
            tileCache = world.getTileEntity(pos);

            if ((tileCache == null || !EnergyHelper.isEnergyTile(tileCache)) && world.getChunkFromBlockCoords(pos).isLoaded()) {
                return false;
            }

            return true;
        }

        /**
         * Must be called immediately after isLinkValid
         */
        public TileEntity getCachedTile() {
            return tileCache;
        }
    }

    //endregion
}
