package com.brandon3055.draconicevolution.blocks.energynet.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.brandonscore.api.IDataRetainerTile;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.ITilePlaceListener;
import com.brandon3055.brandonscore.lib.Vec3B;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.ID_CRYSTAL_MAP;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public abstract class TileCrystalBase extends TileBCBase implements IDataRetainerTile, ITilePlaceListener, ICrystalLink, IEnergyHandler, ITickable {

    //region Stats

    private static Map<CrystalType, int[]> MAX_LINKS = new HashMap<>();

    static {
        MAX_LINKS.put(CrystalType.RELAY, new int[]{8, 16, 32});
        MAX_LINKS.put(CrystalType.CRYSTAL_IO, new int[]{2, 3, 4});
        MAX_LINKS.put(CrystalType.WIRELESS, new int[]{4, 8, 16});
    }

    //endregion

    private int crystalTier = -1;
    protected LinkedList<Vec3B> linkedCrystals = new LinkedList<>();
    private LinkedList<BlockPos> linkedPosCache = null;
    protected EnergyStorage energyStorage = new EnergyStorage(0);
    private ENetFXHandler fxHandler;

    public TileCrystalBase() {
        this.setShouldRefreshOnBlockChange();
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);
    }

    //region Energy Balance

    @Override
    public void update() {
        detectAndSendChanges();
        fxHandler.update();
    }

    //endregion

    //region Linking

    @Nonnull
    @Override
    public List<BlockPos> getLinks() {
        if (linkedPosCache == null) { //TODO make sure linkedPosCache is set null client side when the links change!
            linkedPosCache = new LinkedList<>();
            for (Vec3B offset : linkedCrystals) {
                linkedPosCache.add(new BlockPos(fromOffset(offset)));
            }
            fxHandler.reloadConnections();
        }
        return linkedPosCache;
    }

    //Remember: This is called when a binder linked to "this" tile is used on another block.
    @Override
    public boolean binderUsed(EntityPlayer player, BlockPos linkTarget) {
        TileEntity te = worldObj.getTileEntity(linkTarget);

        //region Check if the target device is valid
        if (!(te instanceof ICrystalLink)) {
            ChatHelper.tranClient(player, "eNet.de.deviceInvalid.info", TextFormatting.RED);
            return false;
        }
        //endregion

        ICrystalLink target = (ICrystalLink) te;

        //region Check if the devices are already linked and if they are break the link
        if (getLinks().contains(te.getPos())) {
            breakLink(target);
            target.breakLink(this);
            ChatHelper.tranClient(player, "eNet.de.linkBroken.info", TextFormatting.GREEN);
            return true;
        }
        //endregion

        //region Check if both devices to see if ether of them have reached their connection limit.
        if (getLinks().size() >= maxLinks()) {
            ChatHelper.tranClient(player, "eNet.de.linkLimitReachedThis.info", TextFormatting.RED);
            return false;
        }
        else if (target.getLinks().size() >= target.maxLinks()) {
            ChatHelper.tranClient(player, "eNet.de.linkLimitReachedTarget.info", TextFormatting.RED);
            return false;
        }
        //endregion

        //region Check both devices are in range
        if (!Utils.inRangeSphere(pos, linkTarget, maxLinkRange())) {
            ChatHelper.tranClient(player, "eNet.de.thisRangeLimit.info", TextFormatting.RED);
            return false;
        }
        else if (!Utils.inRangeSphere(pos, linkTarget, target.maxLinkRange())) {
            ChatHelper.tranClient(player, "eNet.de.targetRangeLimit.info", TextFormatting.RED);
            return false;
        }
        //endregion

        //region All checks have passed. Make the link!
        if (!target.createLink(this)) {
            ChatHelper.tranClient(player, "eNet.de.linkFailedUnknown.info", TextFormatting.RED);
            return false;
        }

        if (!createLink(target)) {
            //Ensure we don't leave a half linked device if this fails.
            target.breakLink(this);
            ChatHelper.tranClient(player, "eNet.de.linkFailedUnknown.info", TextFormatting.RED);
            return false;
        }

        ChatHelper.tranClient(player, "eNet.de.devicesLinked.info", TextFormatting.GREEN);
        return true;
        //endregion
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        Vec3B offset = getOffset(((TileEntity) otherCrystal).getPos());
        linkedCrystals.add(offset);
        linkedPosCache = null;
        updateBlock();
        return true;
    }

    @Override
    public void breakLink(ICrystalLink otherCrystal) {
//        if (worldObj.isRemote) {
//            return;
//        }
        Vec3B offset = getOffset(((TileEntity) otherCrystal).getPos());

        if (linkedCrystals.contains(offset)) {
            linkedCrystals.remove(offset);
        }

        linkedPosCache = null;
        updateBlock();
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
                return 512000;
            case 1:
                return 4096000;
            case 2:
                return 32768000;
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
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {

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
            ID_CRYSTAL_MAP.put(getIDHash(), this);
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
}
