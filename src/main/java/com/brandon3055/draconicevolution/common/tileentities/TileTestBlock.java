package com.brandon3055.draconicevolution.common.tileentities;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 24/06/2014.
 */
public class TileTestBlock extends TileEntity implements IEnergyHandler {
	public EnergyStorage energy = new EnergyStorage(100000000);
	public int maxInput = 100000000;
	public float modelRotation;

	@Override
	public void updateEntity() {

		if (worldObj.isRemote) modelRotation += 0.5;

		//LogHelper.info(modelRotation);



		int test = 4;
		//if (!worldObj.isRemote)
			//System.out.println(energy.getEnergyStored());

		if ((this.energy.getEnergyStored() > 0)) {

			TileEntity tile = worldObj.getTileEntity(xCoord + UP.offsetX, yCoord + UP.offsetY, zCoord + UP.offsetZ);

			if ((tile instanceof IEnergyHandler)) {
					this.energy.extractEnergy(((IEnergyHandler)tile).receiveEnergy(UP.getOpposite(), this.energy.extractEnergy(maxInput, true), false), false);
			}
		}
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if(from != UP)
			return this.energy.receiveEnergy(Math.min(maxInput, maxReceive), simulate);
		else
			return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {;
		if (from == UP)
			return this.energy.extractEnergy(maxExtract, simulate);
		else
			return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
