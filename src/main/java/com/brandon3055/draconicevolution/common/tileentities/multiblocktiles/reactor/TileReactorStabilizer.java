package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 5/7/2015.
 */
public class TileReactorStabilizer extends TileEntity {

	public float coreRotation = 0F;
	public float ringRotation = 0F;
	public float coreSpeed = 1F;
	public float ringSpeed = 1F;
	public float modelIllumination = 0F;
	public int facingDirection = ForgeDirection.UP.ordinal();
	public TileLocation masterLocation = new TileLocation();
	public boolean isValid = false;
	public int tick = 0;

	@Override
	public void updateEntity() {
		tick++;

		if (worldObj.isRemote) {
			coreRotation += coreSpeed;
			ringRotation += ringSpeed;
			coreSpeed = 30F;
			ringSpeed = 5F;
			modelIllumination = 1F;
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
			if (worldObj.getTileEntity(x, y, z) instanceof TileReactorCore){
				masterLocation.set(x, y, z);
				isValid = true;
				worldObj.markBlockForUpdate(x, y, z);
				return true;
			}
		}
		isValid = false;
		return false;
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
}
