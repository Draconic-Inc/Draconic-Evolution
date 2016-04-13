package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.InvisibleEnergyCoreBlock;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 *  Created by brandon3055 on 13/4/2016.
 */
public class TileInvisibleEnergyCoreBlock extends TileBCBase implements IExtendedRFStorage, IEnergyProvider, IEnergyReceiver{

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, true);

    public TileInvisibleEnergyCoreBlock(){
        registerSyncableObject(coreOffset, true);
    }

    public void revert(){
        String string = worldObj.getBlockState(pos).getValue(InvisibleEnergyCoreBlock.BLOCK_TYPE);
        Block block = Block.blockRegistry.getObject(new ResourceLocation(string));
        //Block block = string.equals("draconicevolution:draconiumBlock") ? DEFeatures.draconiumBlock : string.equals("draconicevolution:draconicBlock") ? DEFeatures.draconicBlock : string.equals("minecraft:redstone_block") ? Blocks.redstone_block : string.equals("minecraft:glass") ? Blocks.glass : Blocks.air;
        if (block != null) {
            worldObj.setBlockState(pos, block.getDefaultState());
        }
        else {
            worldObj.setBlockToAir(pos);
        }
    }

    public void setCore(TileEnergyStorageCore core) {
        coreOffset.vec = new Vec3I(pos.subtract(core.getPos()));
        //coreOffset.vec = new Vec3I(pos.getX() - core.getPos().getX(), pos.getY() - core.getPos().getY(), pos.getZ() - core.getPos().getZ());
    }
    public TileEnergyStorageCore getCore(){
        TileEntity tile = worldObj.getTileEntity(getCorePos());
        if (tile instanceof TileEnergyStorageCore){
            return (TileEnergyStorageCore)tile;
        }
        else {
            revert();
        }

        return null;
    }

    private BlockPos getCorePos(){
        return pos.add(-coreOffset.vec.x, -coreOffset.vec.y, -coreOffset.vec.z);
    }

    //region Interfaces

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public long getExtendedStorage() {
        return 0;
    }

    @Override
    public long getExtendedCapacity() {
        return 0;
    }

    //endregion
}
