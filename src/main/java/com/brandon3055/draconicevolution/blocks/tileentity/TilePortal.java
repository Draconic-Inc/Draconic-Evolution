package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.Portal;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TilePortal extends TileBCore {
    private final ManagedVec3I masterPos = register(new ManagedVec3I("master_pos", new Vec3I(0, -9999, 0), DataFlags.SAVE_NBT_SYNC_TILE));
    public boolean frameMoving = false;
    public boolean disabled = false;
    public long updateTime = 0;

    public TilePortal(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public TilePortal() {
        super(DEContent.tile_portal);
    }

    public void propRenderUpdate(long triggerTime, boolean reignite) {}

    public void validatePortal() {
        if (masterPos.get().y == -9999 || frameMoving) {
            return;
        }

        TileEntity tile = world.getTileEntity(getMasterPos());

        if (tile instanceof TileDislocatorReceptacle) {
            if (((TileDislocatorReceptacle) tile).igniting) {
                return;
            }

            if (!((TileDislocatorReceptacle) tile).active.get()) {
                world.removeBlock(pos, false);
                return;
            }

            BlockState state = world.getBlockState(pos);
            for (Direction facing : FacingUtils.getFacingsAroundAxis(state.get(Portal.AXIS))) {
                BlockState checkPos = world.getBlockState(pos.offset(facing));
                if (checkPos.getBlock() != DEContent.portal && checkPos.getBlock() != DEContent.infused_obsidian && checkPos.getBlock() != DEContent.dislocator_receptacle) {
                    ((TileDislocatorReceptacle) tile).deactivate();
                    world.removeBlock(pos, false);
                }
            }
            dataManager.detectAndSendChanges();
        }
        else {
            world.removeBlock(pos, false);
        }
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos.get().set(pos.subtract(masterPos));
    }

    protected BlockPos getMasterPos() {
        return pos.subtract(masterPos.get().getPos());
    }

    public TileDislocatorReceptacle getMaster() {
        TileEntity tile = world.getTileEntity(getMasterPos());
        return tile instanceof TileDislocatorReceptacle ? (TileDislocatorReceptacle) tile : null;
    }


}
