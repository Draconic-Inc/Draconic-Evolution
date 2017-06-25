package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
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
    public final ManagedVec3I masterPos = register("masterPos", new ManagedVec3I(new Vec3I(0, -1, 0))).saveToTile().finish();

    public void validatePortal() {
        if (masterPos.vec.y == -1) {
            return;
        }

        TileEntity tile = world.getTileEntity(masterPos.vec.getPos());

        if (tile instanceof TileDislocatorReceptacle) {
            if (((TileDislocatorReceptacle) tile).igniting) {
                return;
            }

            if (!((TileDislocatorReceptacle) tile).ACTIVE.value) {
                world.setBlockToAir(pos);
                return;
            }

            IBlockState state = world.getBlockState(pos);
            for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(state.getValue(Portal.AXIS))) {
                IBlockState checkPos = world.getBlockState(pos.offset(facing));
                if (checkPos.getBlock() != DEFeatures.portal && checkPos.getBlock() != DEFeatures.infusedObsidian && checkPos.getBlock() != DEFeatures.dislocatorReceptacle) {
                    ((TileDislocatorReceptacle) tile).deactivate();
                    world.setBlockToAir(pos);
                }
            }
        }
        else {
            world.setBlockToAir(pos);
        }
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos.vec.set(masterPos);
    }

    public TileDislocatorReceptacle getMaster() {
        TileEntity tile = world.getTileEntity(masterPos.vec.getPos());
        return tile instanceof TileDislocatorReceptacle ? (TileDislocatorReceptacle) tile : null;
    }
}
