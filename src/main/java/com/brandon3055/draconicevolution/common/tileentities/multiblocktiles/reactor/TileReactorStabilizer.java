package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleReactorBeam;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IIsSlave;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by brandon3055 on 5/7/2015.
 */
public class TileReactorStabilizer extends TileEntity implements IIsSlave , IEnergyProvider{

	public float coreRotation = 0F;
	public float ringRotation = 0F;
	public float coreSpeed = 1F;
	public float ringSpeed = 1F;
	public float modelIllumination = 0F;
	public int facingDirection = ForgeDirection.UP.ordinal();
	public TileLocation masterLocation = new TileLocation();
	public boolean isValid = false;
	public int tick = 0;
	private ParticleReactorBeam beam = null;

	@Override
	public void updateEntity() {
		tick++;

		if (worldObj.isRemote) {
			if (!(masterLocation.getTileEntity(worldObj) instanceof TileReactorCore)){
				coreSpeed = 0;
				ringSpeed = 0;
				modelIllumination = 0;
				return;
			}
			TileReactorCore core = (TileReactorCore)masterLocation.getTileEntity(worldObj);
			coreRotation += coreSpeed;
			ringRotation += ringSpeed;
			coreSpeed = 30F * core.renderSpeed;
			ringSpeed = 5F * core.renderSpeed;
			modelIllumination = core.renderSpeed;
			if (isValid) beam = DraconicEvolution.proxy.reactorBeam(this, beam, true);
			return;
		}

		TileEntity master = masterLocation.getTileEntity(worldObj);
		if (master instanceof TileReactorCore && ((TileReactorCore) master).reactorState == TileReactorCore.STATE_ONLINE){
			ForgeDirection back = ForgeDirection.getOrientation(facingDirection).getOpposite();
			TileEntity output = worldObj.getTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
			if (output instanceof IEnergyReceiver)
			{
				int sent = ((IEnergyReceiver) output).receiveEnergy(back.getOpposite(), Math.min(((TileReactorCore) master).energySaturation, ((TileReactorCore) master).maxEnergySaturation / 100), false);
				((TileReactorCore) master).energySaturation -= sent;
			}
		}

	}

	public void onPlaced() {

		checkForMaster();
	}

	public void onBroken() {

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
				if (tile instanceof TileReactorCore && ((TileReactorCore) tile).stabilizerLocations.size() < 4) //todo add check reactor side to make sure this aligns with other stabilizers
				{
					((TileReactorCore) tile).stabilizerLocations.add(new TileLocation(xCoord, yCoord, zCoord));
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
	public void shutDown(){
		isValid = false;
		masterLocation = new TileLocation();
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
	public TileLocation getMaster() {
		return masterLocation;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from == ForgeDirection.getOrientation(facingDirection).getOpposite();
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 40960.0D;
	}
//	@SideOnly(Side.CLIENT)
//	@Override
//	public AxisAlignedBB getRenderBoundingBox() {
//		return INFINITE_EXTENT_AABB;
//	}
}
