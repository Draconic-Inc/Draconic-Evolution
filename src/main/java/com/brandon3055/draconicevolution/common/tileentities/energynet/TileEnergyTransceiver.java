package com.brandon3055.draconicevolution.common.tileentities.energynet;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Brandon on 16/02/2015.
 */
public class TileEnergyTransceiver extends TileRemoteEnergyBase{

	public int facing = 0;//0=up, 1=down, 3=north, 2=south, 4=east, 5=west

	public TileEnergyTransceiver() {}

	public TileEnergyTransceiver(int powerTier)
	{
		this.powerTier = powerTier;
		this.updateStorage();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
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
		compound.setInteger("Facing", facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		facing = compound.getInteger("Facing");
	}
}
