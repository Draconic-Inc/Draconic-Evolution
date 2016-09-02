package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyProvider;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class TileCreativeRFCapacitor extends TileBCBase implements IEnergyProvider, ITickable {
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public void update() {
        int value = Integer.MAX_VALUE;
        for (EnumFacing direction : EnumFacing.VALUES) {
            TileEnergyBase.sendEnergyTo(worldObj, pos, value, direction);
        }
    }
}
