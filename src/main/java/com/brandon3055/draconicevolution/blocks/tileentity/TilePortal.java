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

        TileEntity tile = level.getBlockEntity(getMasterPos());

        if (tile instanceof TileDislocatorReceptacle) {
            if (((TileDislocatorReceptacle) tile).igniting) {
                return;
            }

            if (!((TileDislocatorReceptacle) tile).active.get()) {
                level.removeBlock(worldPosition, false);
                return;
            }

            BlockState state = level.getBlockState(worldPosition);
            for (Direction facing : FacingUtils.getFacingsAroundAxis(state.getValue(Portal.AXIS))) {
                BlockState checkPos = level.getBlockState(worldPosition.relative(facing));
                if (checkPos.getBlock() != DEContent.portal && checkPos.getBlock() != DEContent.infused_obsidian && checkPos.getBlock() != DEContent.dislocator_receptacle) {
                    ((TileDislocatorReceptacle) tile).deactivate();
                    level.removeBlock(worldPosition, false);
                }
            }
            dataManager.detectAndSendChanges();
        }
        else {
            level.removeBlock(worldPosition, false);
        }
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos.get().set(worldPosition.subtract(masterPos));
    }

    protected BlockPos getMasterPos() {
        return worldPosition.subtract(masterPos.get().getPos());
    }

    public TileDislocatorReceptacle getMaster() {
        TileEntity tile = level.getBlockEntity(getMasterPos());
        return tile instanceof TileDislocatorReceptacle ? (TileDislocatorReceptacle) tile : null;
    }


}
