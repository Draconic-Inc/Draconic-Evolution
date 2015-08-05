package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IIsSlave;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Brandon on 16/6/2015.
 */
public class TileReactorCore extends TileObjectSync {
	public static final int MAX_SLAVE_RANGE = 10;
	public static final int STATE_OFFLINE = 0;
	public static final int STATE_START = 1;
	public static final int STATE_ONLINE = 2;
	public static final int STATE_STOP = 3;

	public int reactorState = 0;
	public float renderRotation = 0;
	public boolean isStructureValid = true;
	public boolean isActive = true;
	public float renderStartProgress = 0F;	//todo go from 0 to one when the reactor is starting and from 1 to 0 when the reactor is stopping (to control render transitions)


	//Key operational figures
	public int reactorFuel = 0;
	public int convertedFuel = 0;			//The amount of fuel that has converted to chaos
	public double conversionUnit = 0;		//used to smooth out the conversion between int and floating point. When >= 1 minus one and convert one int worth of fuel

	public double reactionTemperature = 20;
	public double maxReactTemperature = 10000;	//Todo make this dependent on the fuel amount + fuel conversion

	public double fieldCharge = 0;
	public double maxFieldCharge = 200;		//Todo make this dependent on the fuel value

	public int energySaturation = 0;		//Todo make this the energy buffer that the generation dumps into and that the stabilizers extract from
	public int maxEnergySaturation = 10000;	//Todo make this dependent on the fuel amount, This is the max RF/t output when at 0% saturation

	public int startupEnergy = 0;			//Used when starting. startup energy needs to reach max startup energy for the reactor to start
	public int maxStartupEnergy = 0;		//Todo make this dependent on the fuel level //maby just use saturation for startup...
	//#######################


	public List<TileLocation> stabilizerLocations = new ArrayList<TileLocation>();

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) renderRotation += 0.2F;
//		if (!worldObj.isRemote) {
//			renderRotation += 0.05;
//
//			reactionTemperature = (int)(((1D + Math.sin(renderRotation)) / 2D) * (double)maxReactTemperature);
//
//			fieldCharge = (int)(((1D + Math.sin(renderRotation + 1)) / 2D) * maxFieldCharge);
//
//			energySaturation = (int)(((1D + Math.sin(renderRotation + 2)) / 2D) * (double)maxEnergySaturation);
//
//			convertedFuel = (int)(((1D + Math.sin(renderRotation + 3)) / 2D) * (double)reactorFuel);
//		}
	}

	public void validateStructure(){
		boolean updateRequired = false;
		LogHelper.info("validate");

		//Check that all of the stabilizers are still valid
		Iterator<TileLocation> i = stabilizerLocations.iterator();
		while (i.hasNext())	{
			TileLocation location = i.next();
			if (!(location.getTileEntity(worldObj) instanceof TileReactorStabilizer) || !((TileReactorStabilizer) location.getTileEntity(worldObj)).masterLocation.isThisLocation(xCoord, yCoord, zCoord)) {
				i.remove();
				updateRequired = true;
			}
		}


		if (updateRequired) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onPlaced(){
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
			boolean flag = false;
			for (int i = 1; i < MAX_SLAVE_RANGE && !flag; i++){
				TileLocation location = new TileLocation(xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i);
				if (location.getTileEntity(worldObj) != null){
					if (location.getTileEntity(worldObj) instanceof IIsSlave && !((IIsSlave) location.getTileEntity(worldObj)).getMaster().initialized) ((IIsSlave) location.getTileEntity(worldObj)).checkForMaster();
					flag = true;
				}
			}
		}
	}

	public void onBroken(){
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
			for (int i = 1; i < MAX_SLAVE_RANGE; i++){
				TileLocation location = new TileLocation(xCoord + direction.offsetX * i, yCoord + direction.offsetY * i, zCoord + direction.offsetZ * i);
				if (location.getTileEntity(worldObj) instanceof IIsSlave && ((IIsSlave) location.getTileEntity(worldObj)).getMaster().compareTo(new TileLocation(xCoord, yCoord, zCoord)) == 0) ((IIsSlave)location.getTileEntity(worldObj)).shutDown();
			}
		}

//		for (TileLocation location : stabilizerLocations) {
//			LogHelper.info(location.getTileEntity(worldObj));
//			if (location.getTileEntity(worldObj) instanceof TileReactorStabilizer) ((TileReactorStabilizer) location.getTileEntity(worldObj)).shutDown();
//		}
	}

	public boolean onStructureRightClicked(EntityPlayer player){
		if (!worldObj.isRemote) player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_REACTOR, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	public int injectEnergy(int RF){
		return 0;
	}

	public void processButtonPress(boolean start, boolean stop){

	}

	//Getters
	public double getCoreDiameter(){
		double volume = (double)reactorFuel / 1296D;
		volume *= 1 + ((double) reactionTemperature / (double) maxReactTemperature) * 10D;
		return Math.cbrt(volume / (4/3 * Math.PI)) * 2;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList stabilizerList = new NBTTagList();
		for (TileLocation offset : stabilizerLocations){
			NBTTagCompound compound1 = new NBTTagCompound();
			offset.writeToNBT(compound1, "tag");
			stabilizerList.appendTag(compound1);
		}
		if (stabilizerList.tagCount() > 0) compound.setTag("Stabilizers", stabilizerList);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound compound = pkt.func_148857_g();

		stabilizerLocations = new ArrayList<TileLocation>();
		if (compound.hasKey("Stabilizers")){
			NBTTagList stabilizerList = compound.getTagList("Stabilizers", 10);
			for (int i = 0; i < stabilizerList.tagCount(); i++){
				TileLocation offset = new TileLocation();
				offset.readFromNBT(stabilizerList.getCompoundTagAt(i), "tag");
				stabilizerLocations.add(offset);
			}
		}

		super.onDataPacket(net, pkt);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList stabilizerList = new NBTTagList();
		for (TileLocation offset : stabilizerLocations){
			NBTTagCompound compound1 = new NBTTagCompound();
			offset.writeToNBT(compound1, "tag");
			stabilizerList.appendTag(compound1);
		}
		if (stabilizerList.tagCount() > 0) compound.setTag("Stabilizers", stabilizerList);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		stabilizerLocations = new ArrayList<TileLocation>();
		if (compound.hasKey("Stabilizers")){
			NBTTagList stabilizerList = compound.getTagList("Stabilizers", 10);
			for (int i = 0; i < stabilizerList.tagCount(); i++){
				TileLocation offset = new TileLocation();
				offset.readFromNBT(stabilizerList.getCompoundTagAt(i), "tag");
				stabilizerLocations.add(offset);
			}
		}
	}
}
