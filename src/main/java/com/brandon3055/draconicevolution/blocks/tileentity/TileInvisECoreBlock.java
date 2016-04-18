package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 *  Created by brandon3055 on 13/4/2016.
 */
public class TileInvisECoreBlock extends TileBCBase{

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, true);
    public String blockName = "";

    public TileInvisECoreBlock(){
        registerSyncableObject(coreOffset, true);
    }

    public void revert(){
        Block block = Block.blockRegistry.getObject(new ResourceLocation(blockName));
        if (block != null) {
            worldObj.setBlockState(pos, block.getDefaultState());
        }
        else {
            worldObj.setBlockToAir(pos);
        }
    }

    public void setCore(TileEnergyStorageCore core) {
        coreOffset.vec = new Vec3I(pos.subtract(core.getPos()));
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

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("BlockName", blockName);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        blockName = compound.getString("BlockName");
    }
}
