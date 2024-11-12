package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class TileModuleContext extends ModuleContext {
    private BlockEntity tile;

    public TileModuleContext(BlockEntity tile) {
        super();
        this.tile = tile;
    }

    @Override
    @Nullable
    public IOPStorage getOpStorage() {
        return CapabilityOP.fromBlockEntity(tile);
    }

    @Override
    public Type getType() {
        return Type.TILE_ENTITY;
    }

    /**
     * @return the tile entity this module is installed in.
     */
    public BlockEntity getTile() {
        return tile;
    }
}
