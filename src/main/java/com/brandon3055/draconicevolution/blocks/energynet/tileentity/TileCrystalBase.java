package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.brandonscore.api.IDataRetainerTile;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.ITilePlaceListener;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.ID_CRYSTAL_MAP;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public abstract class TileCrystalBase extends TileBCBase implements IDataRetainerTile, ITilePlaceListener, ICrystalLink, IEnergyHandler, ITickable, IActivatableTile {

    //region Stats

    private static Map<CrystalType, int[]> MAX_LINKS = new HashMap<>();

    static {
        MAX_LINKS.put(CrystalType.RELAY, new int[]{8, 16, 32});
        MAX_LINKS.put(CrystalType.CRYSTAL_IO, new int[]{2, 3, 4});
        MAX_LINKS.put(CrystalType.WIRELESS, new int[]{4, 8, 16});
    }

    //endregion

    protected int tick = 0;
    private int crystalTier = -1;
    protected LinkedList<Vec3B> linkedCrystals = new LinkedList<>();
    public LinkedList<int[]> transferRatesArrays = new LinkedList<>();
    public LinkedList<Byte> flowRates = new LinkedList<>();
    private LinkedList<BlockPos> linkedPosCache = null;
    protected EnergyStorage energyStorage = new EnergyStorage(0);
    protected ENetFXHandler fxHandler;

    public TileCrystalBase() {
        this.setShouldRefreshOnBlockChange();
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);
    }

    //region Energy Balance

    @Override
    public void update() {
        detectAndSendChanges();
        if (linkedCrystals.size() != transferRatesArrays.size() && !worldObj.isRemote) {
            rebuildTransferList();
        }

        balanceLinkedDevices();
        fxHandler.update();

        if (!worldObj.isRemote && DEEventHandler.serverTicks % 10 == 0) {
            flowRates.clear();
            for (int i = 0; i < linkedCrystals.size(); i++) {
                flowRates.add(calculateFlow(i));
                fxHandler.detectAndSendChanges();
            }
        }

//        if (worldObj.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, false) != null) {
//            LogHelper.dev(flowRates+" "+linkedCrystals);
//        }

        tick++;
    }

    public void balanceLinkedDevices() {
        if (worldObj.isRemote) {
            return;
        }
        for (BlockPos linkedPos : getLinks()) {
            TileEntity linkedTile = worldObj.getTileEntity(linkedPos);

            if (!(linkedTile instanceof ICrystalLink)) {
                if (worldObj.getChunkFromBlockCoords(linkedPos).isLoaded()) {
                    breakLink(linkedPos);
                    return;
                }
                else {
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
        int stored = getEnergyStored();

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

        sendTo.modifyEnergyStored((transfer = energyStorage.extractEnergy(transfer, false)));

        return transfer;
    }

    public void rebuildTransferList() {
        transferRatesArrays.clear();
        flowRates.clear();
        for (int i = 0; i < linkedCrystals.size(); i++) {
            transferRatesArrays.add(new int[20]);
            flowRates.add((byte)0);
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
    public boolean binderUsed(EntityPlayer player, BlockPos linkTarget, EnumFacing sideClicked) {
        TileEntity te = worldObj.getTileEntity(linkTarget);

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
        }
        else if (target.getLinks().size() >= target.maxLinks()) {
            ChatHelper.indexedTrans(player, "eNet.de.linkLimitReachedTarget.info", TextFormatting.RED, -442611624);
            return false;
        }
        //endregion

        //region Check both devices are in range
        if (!Utils.inRangeSphere(pos, linkTarget, maxLinkRange())) {
            ChatHelper.indexedTrans(player, "eNet.de.thisRangeLimit.info", TextFormatting.RED, -442611624);
            return false;
        }
        else if (!Utils.inRangeSphere(pos, linkTarget, target.maxLinkRange())) {
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

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return false;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxEnergyStored();
    }

    //endregion

    //region ICrystalLink and some other stuffs...

    @Override
    public int maxLinks() {
        return MAX_LINKS.get(getType())[getTier()];
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
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public void modifyEnergyStored(int energy) {
        energyStorage.modifyEnergyStored(energy);
    }

    public int getTier() {
        if (crystalTier == -1) {
            crystalTier = getState(getBlockType()).getValue(EnergyCrystal.TIER);
        }
        return crystalTier;
    }

    public abstract CrystalType getType();

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
            TileEntity tile = worldObj.getTileEntity(target);
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
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldObj.isRemote) {
            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_CRYSTAL, worldObj, pos.getX(), pos.getY(), pos.getZ());
        }
        return false;
    }

    //endregion

    //region Render

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || !DEShaders.useShaders();
    }

    @SideOnly(Side.CLIENT)
    public abstract CrystalGLFXBase createStaticFX();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    @SideOnly(Side.CLIENT)
    public void addDisplayData(List<String> displayList) {
        double charge = Utils.round(((double) getEnergyStored() / (double) getMaxEnergyStored()) * 100D, 100);
        displayList.add(TextFormatting.BLUE + I18n.format("eNet.de.hudCharge.info") + ": " + Utils.formatNumber(getEnergyStored()) + " / " + Utils.formatNumber(getMaxEnergyStored()) + " RF [" + charge + "%]");
        displayList.add(TextFormatting.GREEN + I18n.format("eNet.de.hudLinks.info") + ": " + getLinks().size() + " / " + maxLinks() + "");

//        if (BrandonsCore.proxy.getClientPlayer().isSneaking()) {
//            for (BlockPos lPos : getLinks()) {
//                displayList.add(TextFormatting.GRAY + " " + String.format("[x:%s, y:%s, z:%s]", lPos.getX(), lPos.getY(), lPos.getZ()));
//            }
//        }
    }

    public ENetFXHandler createServerFXHandler() {
        return new ENetFXHandlerServer(this);
    }

    @SideOnly(Side.CLIENT)
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClient(this);
    }

    //endregion

    //region Sync/Save

    //Dont need to write link info to the item when it is broken so using this to store all that,
    @Override
    public void writeExtraNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Vec3B vec : linkedCrystals) {
            list.appendTag(new NBTTagByteArray(new byte[]{vec.x, vec.y, vec.z}));
        }
        compound.setTag("LinkedCrystals", list);
        fxHandler.writeToNBT(compound);

        byte[] array = new byte[flowRates.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = flowRates.get(i);
        }
        compound.setByteArray("FlowRates", array);
//        LogHelper.dev("Write "+flowRates+" "+array);
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("LinkedCrystals", 7);
        linkedCrystals.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            byte[] data = ((NBTTagByteArray) list.get(i)).getByteArray();
            linkedCrystals.add(new Vec3B(data[0], data[1], data[2]));
        }
        if (linkedPosCache != null) {
            linkedPosCache.clear();
        }
        fxHandler.readFromNBT(compound);

        if (compound.hasKey("FlowRates")) {
            byte[] array = compound.getByteArray("FlowRates");
            flowRates.clear();
            for (byte b : array) {
                flowRates.add(b);
            }
//            LogHelper.dev("Read "+flowRates+" ");
        }
    }

    @Override
    public void writeRetainedData(NBTTagCompound dataCompound) {
        super.writeRetainedData(dataCompound);
        dataCompound.setByte("Tier", (byte) getTier());
        energyStorage.writeToNBT(dataCompound);
    }

    @Override
    public void readRetainedData(NBTTagCompound dataCompound) {
        super.readRetainedData(dataCompound);
        int cap = getCapacityForTier(dataCompound.getByte("Tier"));
        energyStorage.setCapacity(cap).setMaxTransfer(cap);
        energyStorage.readFromNBT(dataCompound);
    }

    @Override
    public void onTilePlaced(World world, BlockPos pos, EnumFacing placedAgainst, float hitX, float hitY, float hitZ, EntityPlayer placer, ItemStack stack) {
        int cap = getCapacityForTier(getTier());
        energyStorage.setCapacity(cap).setMaxTransfer(cap);
    }

    boolean hashCached = false;
    int hashID = 0;

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
    public void onChunkUnload() {
        super.onChunkUnload();
        if (ID_CRYSTAL_MAP.containsKey(getIDHash())) {
            ID_CRYSTAL_MAP.remove(getIDHash());
        }
        fxHandler.tileUnload();
    }

    public void receiveBatchedUpdate(BatchedCrystalUpdate update) {
        fxHandler.updateReceived(update);
    }

    //endregion

    public static class LinkData {
        public String displayName;
        public int transferPerTick;
        public BlockPos linkTarget;
        public String data;

        public LinkData() {}

        public LinkData(String displayName, int transferPerTick, BlockPos linkTarget, String data) {
            this.displayName = displayName;
            this.transferPerTick = transferPerTick;
            this.linkTarget = linkTarget;
            this.data = data;
        }

        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, displayName);
            buf.writeInt(transferPerTick);
            buf.writeLong(linkTarget.toLong());
            ByteBufUtils.writeUTF8String(buf, data);
        }

        public static LinkData fromBytes(ByteBuf buf) {
            LinkData data = new LinkData();
            data.displayName = ByteBufUtils.readUTF8String(buf);
            data.transferPerTick = buf.readInt();
            data.linkTarget = BlockPos.fromLong(buf.readLong());
            data.data = ByteBufUtils.readUTF8String(buf);
            return data;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
