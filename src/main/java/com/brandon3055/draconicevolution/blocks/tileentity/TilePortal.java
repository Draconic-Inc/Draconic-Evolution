package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.Portal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TilePortal extends TileBCBase {
    public final SyncableVec3I masterPos = new SyncableVec3I(new Vec3I(0, -1, 0), false, false);

    public TilePortal() {
        registerSyncableObject(masterPos, true);
    }

    public void validatePortal() {
        if (masterPos.vec.y == -1) {
            return;
        }

        TileEntity tile = worldObj.getTileEntity(masterPos.vec.getPos());

        if (tile instanceof TileDislocatorReceptacle) {
            if (((TileDislocatorReceptacle) tile).igniting) {
                return;
            }

            if (!((TileDislocatorReceptacle) tile).ACTIVE.value){
                worldObj.setBlockToAir(pos);
                return;
            }

            IBlockState state = worldObj.getBlockState(pos);
            for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(state.getValue(Portal.AXIS))){
                IBlockState checkPos = worldObj.getBlockState(pos.offset(facing));
                if (checkPos.getBlock() != DEFeatures.portal && checkPos.getBlock() != DEFeatures.infusedObsidian){
                    ((TileDislocatorReceptacle) tile).deactivate();
                    worldObj.setBlockToAir(pos);
                }
            }
        }
        else {
            worldObj.setBlockToAir(pos);
        }
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos.vec.set(masterPos);
    }

    public TileDislocatorReceptacle getMaster() {
        TileEntity tile = worldObj.getTileEntity(masterPos.vec.getPos());
        return tile instanceof TileDislocatorReceptacle ? (TileDislocatorReceptacle) tile : null;
    }
}
