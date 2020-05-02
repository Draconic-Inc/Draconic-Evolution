package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class TileModuleContext extends ModuleContext {
    private TileEntity tile;

    public TileModuleContext(ModuleHost moduleHost, TileEntity tile) {
        super(moduleHost);
        this.tile = tile;
    }

    @Override
    public Type getType() {
        return Type.TILE_ENTITY;
    }

    /**
     * @return the tile entity this module is installed in.
     */
    public TileEntity getTile() {
        return tile;
    }
}
