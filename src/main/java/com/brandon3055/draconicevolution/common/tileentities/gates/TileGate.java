package com.brandon3055.draconicevolution.common.tileentities.gates;

import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 29/6/2015.
 */
public abstract class TileGate extends TileObjectSync {

	public ForgeDirection output = ForgeDirection.DOWN;
	public int flowRSLow = 0;
	public int flowRSHigh = 1000;
	public int signal = -1;

	public abstract String getFlowSetting(int selector);

	public abstract void incrementFlow(int selector, boolean ctrl, boolean shift, boolean add, int button);

	public abstract String getToolTip(int selector, boolean shift, boolean ctrl);

	public int getActualFlow(){
		if (signal == -1 ) signal = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
		return flowRSLow + (int) (((double)signal / 15D) * (double)(flowRSHigh - flowRSLow));
	}

	@Override
	public void receiveObjectFromClient(int index, Object object) {
		if (index == 0) flowRSLow = (Integer)object;
		else if (index == 1) flowRSHigh = (Integer)object;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		writeToNBT(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("Output", output.ordinal());
		compound.setInteger("FlowRSLow", flowRSLow);
		compound.setInteger("FlowRSHigh", flowRSHigh);
		compound.setInteger("Signal", signal);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		output = ForgeDirection.getOrientation(compound.getInteger("Output"));
		flowRSLow = compound.getInteger("FlowRSLow");
		flowRSHigh = compound.getInteger("FlowRSHigh");
		signal = compound.getInteger("Signal");
	}
}
