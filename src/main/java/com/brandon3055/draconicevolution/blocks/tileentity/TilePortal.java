package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedPos;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TilePortal extends TileBCore {
    private final ManagedPos controllerPos = register(new ManagedPos("controller_pos", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    public boolean frameMoving = false;
    public long updateTime = 0;

    public TilePortal(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public TilePortal() {
        super(DEContent.tile_portal);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos.set(worldPosition.subtract(controllerPos));
    }

    protected BlockPos getControllerPos() {
        return controllerPos.get() == null ? BlockPos.ZERO : worldPosition.subtract(controllerPos.get());
    }

    public TileDislocatorReceptacle getController() {
        TileEntity tile = level.getBlockEntity(getControllerPos());
        return tile instanceof TileDislocatorReceptacle ? (TileDislocatorReceptacle) tile : null;
    }

    public boolean isPortalActive() {
        TileDislocatorReceptacle controller = getController();
        return controller != null && controller.isActive();
    }
}
