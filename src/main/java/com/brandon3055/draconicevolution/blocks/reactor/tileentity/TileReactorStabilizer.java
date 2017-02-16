package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorStabilizer extends TileReactorComponent implements IEnergyProvider{

    @Override
    public void update() {
        super.update();


    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }
}
