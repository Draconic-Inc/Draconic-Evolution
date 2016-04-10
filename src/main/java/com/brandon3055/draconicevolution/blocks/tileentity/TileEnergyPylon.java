package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyPylon extends TileBCBase implements IEnergyReceiver, IEnergyProvider {
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return false;
	}
}
