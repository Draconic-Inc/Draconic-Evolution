package com.brandon3055.draconicevolution.common.tileentities.energynet;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Brandon on 16/02/2015.
 */
public class TileEnergyTransceiver extends TileRemoteEnergyBase{

	public TileEnergyTransceiver() {}

	public TileEnergyTransceiver(int powerTier)
	{
		this.powerTier = powerTier;
		this.updateStorage();
	}


	@Override
	public double getBeamX() {
		return xCoord + 0.5;
	}

	@Override
	public double getBeamY() {
		return yCoord + 0.5;
	}

	@Override
	public double getBeamZ() {
		return zCoord + 0.5;
	}

	@Override
	public int getCap() {
		return  50000 + (powerTier * 450000);
	}

	@Override
	public int getRec() {
		return  50000 + (powerTier * 450000);
	}

	@Override
	public int getExt() {
		return  50000 + (powerTier * 450000);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}
}
