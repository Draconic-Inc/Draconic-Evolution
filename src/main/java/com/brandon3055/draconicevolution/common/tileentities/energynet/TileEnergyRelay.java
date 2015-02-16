package com.brandon3055.draconicevolution.common.tileentities.energynet;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyRing;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Brandon on 10/02/2015.
 */
public class TileEnergyRelay extends TileRemoteEnergyBase {
	private ParticleEnergyRing ring;

	public TileEnergyRelay(){}

	public TileEnergyRelay(int powerTier){
		this.powerTier = powerTier;
		this.updateStorage();
	}


	@Override
	public int getCap() {
		return 50000 + (powerTier * 450000);
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
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote)
		{
			ring = DraconicEvolution.proxy.energyRing(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0, powerTier == 1, ring, inView > 0);
			return;
		}
	}



	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

//	@Override
//	public double getCapacity() {
//		return ((double) getEnergyStored(ForgeDirection.UNKNOWN) / (double) getMaxEnergyStored(ForgeDirection.UNKNOWN)) * 100D;
//	}

	/**Calculates the energy flow based on the local buffer
	 * and the remote buffer
	 *return double between 0 to 100*/
	public double getFlow(double localCap, double remoteCap)
	{
		return  Math.max(0, Math.min(100, (localCap - remoteCap) * 10D/*Flow Multiplier*/));
	}

	@Override
	public double getBeamX() {
		return xCoord + 0.5D;
	}

	@Override
	public double getBeamY() {
		return yCoord + 0.5D;
	}

	@Override
	public double getBeamZ() {
		return zCoord + 0.5D;
	}
}
