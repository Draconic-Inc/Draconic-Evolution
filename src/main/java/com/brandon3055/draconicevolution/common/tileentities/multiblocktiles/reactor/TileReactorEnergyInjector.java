package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleReactorBeam;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IIsSlave;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 23/7/2015.
 */
public class TileReactorEnergyInjector extends TileEntity implements IIsSlave, IEnergyReceiver{

	public float modelIllumination = 1F;
	public int facingDirection = ForgeDirection.UP.ordinal();
	public MultiblockHelper.TileLocation masterLocation = new MultiblockHelper.TileLocation();
	public boolean isValid = false;
	public int tick = 0;
	private ParticleReactorBeam beam = null;
	private TileReactorCore core = null;

	@Override
	public void updateEntity() {
		if (worldObj.isRemote && isValid){
			beam = DraconicEvolution.proxy.reactorBeam(this, beam, true);
		}
	}

	public void onPlaced() {
		checkForMaster();
	}

	public boolean checkForMaster(){
		for (int i = 1; i < 10; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(facingDirection);
			int x = xCoord + (dir.offsetX*i);
			int y = yCoord + (dir.offsetY*i);
			int z = zCoord + (dir.offsetZ*i);
			if (!worldObj.isAirBlock(x, y, z))
			{
				TileEntity tile = worldObj.getTileEntity(x, y, z);
				if (tile instanceof TileReactorCore)
				{
					masterLocation.set(x, y, z);
					isValid = true;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					return true;
				}else {
					isValid = false;
					return false;
				}
			}
		}
		isValid = false;
		return false;
	}

	@Override
	public boolean isActive() {
		return isValid;
	}

	@Override
	public void shutDown() {
		masterLocation = new MultiblockHelper.TileLocation();
		isValid = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		masterLocation.writeToNBT(compound, "Master");
		compound.setInteger("Facing", facingDirection);
		compound.setBoolean("IsValid", isValid);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound compound = pkt.func_148857_g();
		masterLocation.readFromNBT(compound, "Master");
		facingDirection = compound.getInteger("Facing");
		isValid = compound.getBoolean("IsValid");
		super.onDataPacket(net, pkt);
	}

	public TileReactorCore getCore(){
		if (core == null || core.isInvalid()) {
			if (getMaster().getTileEntity(worldObj) instanceof TileReactorCore) core = (TileReactorCore)getMaster().getTileEntity(worldObj);
			else core = null;
		}
		return core;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		masterLocation.writeToNBT(compound, "Master");
		compound.setInteger("Facing", facingDirection);
		compound.setBoolean("IsValid", isValid);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		masterLocation.readFromNBT(compound, "Master");
		facingDirection = compound.getInteger("Facing");
		isValid = compound.getBoolean("IsValid");
	}

	@Override
	public MultiblockHelper.TileLocation getMaster() {
		return masterLocation;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (simulate) return Integer.MAX_VALUE;
		else if (getCore() != null) return getCore().injectEnergy(maxReceive);
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from == ForgeDirection.getOrientation(facingDirection).getOpposite();
	}
}
