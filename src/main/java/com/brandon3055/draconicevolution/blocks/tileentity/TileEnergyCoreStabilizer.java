package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 *  Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyCoreStabilizer extends TileBCBase implements ITickable{

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, true);
    //public final SyncableInt coreXOffset = new SyncableInt(0, true, false, true);
    //public final SyncableInt coreYOffset = new SyncableInt(-1, true, false, true);
    //public final SyncableInt coreZOffset = new SyncableInt(0, true, false, true);
    public final SyncableBool hasCoreLock = new SyncableBool(false, true, false, true);
    //public EnumFacing coreDirection = EnumFacing.DOWN;

    public TileEnergyCoreStabilizer(){
        registerSyncableObject(coreOffset, true);
        //registerSyncableObject(coreXOffset, true);
        //registerSyncableObject(coreYOffset, true);
        //registerSyncableObject(coreZOffset, true);
        registerSyncableObject(hasCoreLock, true);
    }

    @Override
    public void update() {
        detectAndSendChanges();
    }

    public void onTileClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        if (worldObj.isRemote) return;

        TileEnergyStorageCore core = getCore();
        if (core == null){
            core = findCore();
        }

        if (core != null){
            core.onStructureClicked(world, pos, state, player);
        }
        else {
            player.addChatComponentMessage(new TextComponentTranslation("msg.de.coreNotFound.txt").setChatStyle(new Style().setColor(TextFormatting.DARK_RED)));
        }
    }

    public TileEnergyStorageCore findCore(){
        if (getCore() != null) {
            return getCore();
        }

        for (EnumFacing facing : EnumFacing.VALUES) {
            for (int i = 0; i < 16; i++){
                TileEntity tile = worldObj.getTileEntity(pos.add(facing.getFrontOffsetX() * i, facing.getFrontOffsetY() * i, facing.getFrontOffsetZ() * i));
                if (tile instanceof TileEnergyStorageCore){
                    TileEnergyStorageCore core = (TileEnergyStorageCore) tile;
                    core.validateStructure();
                    if (core.active.value){
                        continue;
                    }
                    return core;
                }
            }
        }

        return null;
    }

    public TileEnergyStorageCore getCore(){
        if (hasCoreLock.value){
            TileEntity tile = worldObj.getTileEntity(getCorePos());
            if (tile instanceof TileEnergyStorageCore){
                return (TileEnergyStorageCore)tile;
            }
            else {
                hasCoreLock.value = false;
            }
        }
        return null;
    }

    private BlockPos getCorePos(){
        return pos.subtract(coreOffset.vec.getPos());
        //return pos.add(-coreOffset.vec.x, -coreOffset.vec.y, -coreOffset.vec.z);
    }

    public void setCore(TileEnergyStorageCore core) {
        coreOffset.vec = new Vec3I(pos.subtract(core.getPos()));
    //    coreOffset.vec = new Vec3I(pos.getX() - core.getPos().getX(), pos.getY() - core.getPos().getY(), pos.getZ() - core.getPos().getZ());

    //    coreXOffset.value = pos.getX() - core.getPos().getX();
    //    coreYOffset.value = pos.getY() - core.getPos().getY();
    //    coreZOffset.value = pos.getZ() - core.getPos().getZ();
        hasCoreLock.value = true;
    }

    public boolean isStabilizerValid(int coreTier){
        return true;//ToDo Dos something fancy here
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    //    coreXOffset.toNBT(compound);
    //    coreYOffset.toNBT(compound);
    //    coreZOffset.toNBT(compound);
    //    hasCoreLock.toNBT(compound);
    //    compound.setShort("CoreDIR", (short) coreDirection.getIndex());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    //    coreXOffset.fromNBT(compound);
    //    coreYOffset.fromNBT(compound);
    //    coreZOffset.fromNBT(compound);
    //    hasCoreLock.fromNBT(compound);
    //    coreDirection = EnumFacing.getFront(compound.getInteger("CoreDIR"));
    }

}
