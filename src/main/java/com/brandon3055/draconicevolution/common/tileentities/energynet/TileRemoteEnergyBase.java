package com.brandon3055.draconicevolution.common.tileentities.energynet;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import com.brandon3055.draconicevolution.common.utills.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 10/02/2015.
 */
public abstract class TileRemoteEnergyBase extends TileObjectSync implements IRemoteEnergyHandler {

	/**The tier of the relay (0-1)*/
	protected int powerTier;
	protected EnergyStorage storage = new EnergyStorage(0);
	public List<LinkedEnergyDevice> linkedDevices = new ArrayList<LinkedEnergyDevice>();
	protected int lastTickEnergy;

	public byte inView = 0;

	public void updateStorage()
	{
		storage.setCapacity(getCap());
		storage.setMaxExtract(getExt());
		storage.setMaxReceive(getRec());
	}

	public abstract int getCap();

	public abstract int getRec();

	public abstract int getExt();

	@Override
	public void updateEntity() {
		updateLinkedDevices();

		if (worldObj.isRemote && inView > 0) --inView;
	}

	//**********Distribution Logic**********

	private void updateLinkedDevices()
	{
		//LogHelper.info(storage.getEnergyStored());
		for (LinkedEnergyDevice device : linkedDevices)
		{
			if (!device.isValid(worldObj)) {
				linkedDevices.remove(device);
				updateLinkedDevices();
				return;
			}

			IRemoteEnergyHandler remoteTile = device.getEnergyTile(worldObj);

			if (worldObj.isRemote)
			{
				int rType = 0;
				if (this instanceof TileEnergyRelay)
				{
					if (remoteTile instanceof TileEnergyTransceiver) rType = 2;
					else rType = 3;
				}
				else if (this instanceof TileEnergyTransceiver)
				{
					if (remoteTile instanceof TileEnergyRelay) rType = 1;
					else rType = 0;
				}

				device.beam = DraconicEvolution.proxy.energyBeam(worldObj, getBeamX(), getBeamY(), getBeamZ(), remoteTile.getBeamX(), remoteTile.getBeamY(), remoteTile.getBeamZ(), (int)device.energyFlow, getPowerTier() == 1, device.beam, true, rType);
			}
			else
			{
				int transferCap = storage.getMaxExtract();

				double difference = getCapacity() - remoteTile.getCapacity();


				int energyToEqual = (int)(((difference / 100D) * (double)remoteTile.getMaxEnergyStored(ForgeDirection.UNKNOWN)) / 2.1D);

				double maxFlow = Math.min(energyToEqual, Math.min(transferCap, remoteTile.getMaxEnergyStored(ForgeDirection.UNKNOWN) - remoteTile.getEnergyStored(ForgeDirection.UNKNOWN)));

				device.energyFlow = getFlow(getCapacity(), remoteTile.getCapacity());
				int flow = (int) ((device.energyFlow / 100D) * maxFlow);


				storage.extractEnergy(remoteTile.receiveEnergy(ForgeDirection.UNKNOWN, storage.extractEnergy(flow, true), false), false);

				//LogHelper.info("dif:" + difference + " flow:" + getFlow(getCapacity(), remoteTile.getCapacity()) + " EtoE:" + energyToEqual + " tfrd:" + transfered + " X:" + xCoord + " lCap:" + getCapacity() + " rCap:" + remoteTile.getCapacity() + " strd:" + storage.getEnergyStored() + " T:" + type + " mxflw:" + maxFlow);

				//LogHelper.info(flow + " " + transfered + " " + getEnergyStored(ForgeDirection.UNKNOWN));
				//if (device.energyFlow > 0)LogHelper.info(device.energyFlow + " " + transfered);
				//if (device.energyFlow == 0) LogHelper.info(device.getEnergyTile(worldObj));//todo clean up
				detectAndSendChanges(linkedDevices.indexOf(device));
			}
		}
	}

	protected void detectAndSendChanges(int index)
	{
		if (linkedDevices.get(index).energyFlow != linkedDevices.get(index).lastTickEnergyFlow)	{
			sendObject(References.TWO_INTS_ID, 0, new DataUtills.TwoXInteger(index, (int)linkedDevices.get(index).energyFlow));
			linkedDevices.get(index).lastTickEnergyFlow = linkedDevices.get(index).energyFlow;
		}

		if (getEnergyStored(ForgeDirection.UNKNOWN) != lastTickEnergy) sendObject(References.INT_ID, 1, getEnergyStored(ForgeDirection.UNKNOWN));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void receiveObject(int index, Object object) {
		if (index == 0 && object instanceof DataUtills.TwoXInteger && linkedDevices.size() > ((DataUtills.TwoXInteger) object).i1)
		{
			linkedDevices.get(((DataUtills.TwoXInteger) object).i1).energyFlow = ((DataUtills.TwoXInteger) object).i2;
		}
		else if (index == 1) storage.setEnergyStored((Integer)object);
	}


	@Override
	public double getCapacity() {
		return ((double) getEnergyStored(ForgeDirection.UNKNOWN) / (double) getMaxEnergyStored(ForgeDirection.UNKNOWN)) * 100D;
	}

	/**Calculates the energy flow based on the local buffer
	 * and the remote buffer
	 *return double between 0 to 100*/
	public double getFlow(double localCap, double remoteCap)
	{
		return Math.max(0, Math.min(100, (localCap - remoteCap) * 10D/*Flow Multiplier*/));
	}

	public int getPowerTier() {
		return powerTier;
	}

	public void onBlockActivated(EntityPlayer player) {

		if (player.getHeldItem() != null && player.getHeldItem().getItem() == ModItems.wrench)
		{
			ItemStack wrench = player.getHeldItem();
			NBTTagCompound linkDat = null;
			if (wrench.hasTagCompound() && wrench.getTagCompound().hasKey("LinkData")) linkDat = wrench.getTagCompound().getCompoundTag("LinkData");

			if (linkDat != null && linkDat.hasKey("Bound") && linkDat.getBoolean("Bound"))
			{
				handleBinding(player, linkDat.getInteger("XCoord"), linkDat.getInteger("YCoord"), linkDat.getInteger("ZCoord"), true);
				linkDat.setBoolean("Bound", false);
			}
			else
			{
				linkDat = new NBTTagCompound();
				linkDat.setInteger("XCoord", xCoord);
				linkDat.setInteger("YCoord", yCoord);
				linkDat.setInteger("ZCoord", zCoord);
				linkDat.setBoolean("Bound", true);
				ItemNBTHelper.getCompound(wrench).setTag("LinkData", linkDat);
				if (worldObj.isRemote) player.addChatComponentMessage(new ChatComponentText("txt.posSaved"));
			}
		}
		else {
			if (!player.isSneaking())receiveEnergy(ForgeDirection.UNKNOWN, 100000, false);
			else extractEnergy(ForgeDirection.UNKNOWN, 100000, false);
			LogHelper.info(getEnergyStored(ForgeDirection.UNKNOWN));
		}
	}

	@Override
	public boolean handleBinding(EntityPlayer player, int x, int y, int z, boolean callOther)
	{
		int range = (powerTier + 1) * 10;
		IRemoteEnergyHandler tile = worldObj.getTileEntity(x, y, z) instanceof IRemoteEnergyHandler ? (IRemoteEnergyHandler) worldObj.getTileEntity(x, y, z) : null;

		if (tile == null || (x == xCoord && y == yCoord && z == zCoord))
		{
			if (worldObj.isRemote) player.addChatComponentMessage(new ChatComponentText("txt.invalidTile"));
			return false;
		}
		else if (Utills.getDistanceAtoB(xCoord, yCoord, zCoord, x, y, z) > range)
		{
			if (worldObj.isRemote) player.addChatComponentMessage(new ChatComponentText("txt.outOfRange"));
			return false;
		}



		if (callOther && !tile.handleBinding(player, xCoord, yCoord, zCoord, false))
		{
			LogHelper.info("other Invalid");
			return false;
		}

		if (callOther && worldObj.isRemote) player.addChatComponentMessage(new ChatComponentText("txt.linked"));

		for (LinkedEnergyDevice ld : linkedDevices)
		{
			if (ld.xCoord == x && ld.yCoord == y && ld.zCoord == z) return true;
		}

		linkedDevices.add(new LinkedEnergyDevice(x, y, z, null));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return true;
	}



	//**************************************

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setByte("Type", (byte) powerTier);
		int index = 0;
		compound.setInteger("LinkCount", linkedDevices.size());
		for (LinkedEnergyDevice ld : linkedDevices)
		{
			ld.writeToNBT(compound, "LinkedDevice_" + index);
			++index;
		}
		super.writeToNBT(compound);
		storage.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		powerTier = compound.getByte("Type");
		this.updateStorage();
		int linkCount = compound.getInteger("LinkCount");
		linkedDevices = new ArrayList<LinkedEnergyDevice>();
		for (int i = 0; i < linkCount; i++)
		{
			linkedDevices.add(new LinkedEnergyDevice().readFromNBT(compound, "LinkedDevice_" + i));
		}
		super.readFromNBT(compound);
		storage.readFromNBT(compound);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

}
