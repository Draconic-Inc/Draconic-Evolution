package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.client.render.Particles;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Brandon on 28/07/2014.
 */
public class TileEnergyPylon extends TileEntity implements IEnergyHandler {

	protected EnergyStorage storage = new EnergyStorage(500000, 0, 0);
	public boolean active = false;
	public boolean input = false;
	public float modelRotation = 0;
	public float modelScale = 0;
	private TileLocation masterLocation = new TileLocation();
	private int particleRate = 0;
	private int updateDelay = 0;
	private boolean nextUpdate = false;


	@Override
	public void updateEntity() {
		if (nextUpdate) {
			updateDelay = 0;
			nextUpdate = false;
		}
		if (active && worldObj.isRemote) {
			modelRotation += 1.5;
			modelScale += input ? -0.01F : 0.01F;
			if ((modelScale < 0 && input)) modelScale = 10000F;
			if ((modelScale < 0 && !input)) modelScale = 0F;
			spawnParticles();
		} else if (worldObj.isRemote) modelScale = 0.5F;
		if (active && input){
			updateInput();
		}else if (active){
			updateOutput();
		}
	}

	public void onActivated(){
		if (!active){
			active = isValidStructure();
			findMaster();
			return;
		}
		findMaster();
		if (input){
			storage.setMaxReceive(0);
			storage.setMaxExtract(500000);
		}else{
			storage.setMaxReceive(500000);
			storage.setMaxExtract(0);
		}
	}
	//input from core out to acceptors
	private void updateInput(){
		if (updateDelay > 0){
			updateDelay--;
			if (particleRate > 0) particleRate--;
		}else if (getMaster() != null && getMaster().isOnline() && storage.getEnergyStored() < storage.getMaxEnergyStored() && getMaster().getEnergyStored() > 0) {
			int maxRecived = (int)Math.min(storage.getMaxEnergyStored() - storage.getEnergyStored(), getMaster().getEnergyStored());
			storage.modifyEnergyStored(maxRecived);
			getMaster().extractEnergy(maxRecived, false);
			particleRate = maxRecived / 40;
			if (particleRate > 100) particleRate = 100;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}else {
			if (particleRate > 0) particleRate--;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			updateDelay = 20;
		}
		if ((storage.getEnergyStored() > 0)) {
			for (int i = 0; i < 6; i++){
				TileEntity tile = worldObj.getTileEntity(xCoord + ForgeDirection.getOrientation(i).offsetX, yCoord + ForgeDirection.getOrientation(i).offsetY, zCoord + ForgeDirection.getOrientation(i).offsetZ);
				if (tile != null && tile instanceof IEnergyHandler) {
					storage.extractEnergy(((IEnergyHandler)tile).receiveEnergy(ForgeDirection.getOrientation(i).getOpposite(), storage.extractEnergy(storage.getMaxExtract(), true), false), false);
				}
			}
		}
	}
	//output to core in from generators
	private void updateOutput(){
		if (getMaster() == null || !getMaster().isOnline()) return;
		if (updateDelay > 0){
			updateDelay--;
			if (particleRate > 0) particleRate--;
		}else if (storage.getEnergyStored() > 0 && getMaster().isOnline() && getMaster().getEnergyStored() < getMaster().getMaxEnergyStored()){
			int maxSent = (int)Math.min(getMaster().getMaxEnergyStored() - getMaster().getEnergyStored(), storage.getEnergyStored());
			storage.modifyEnergyStored(-maxSent);
			getMaster().receiveEnergy(maxSent, false);
			particleRate = maxSent / 40;
			if (particleRate > 100) particleRate = 100;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}else {
			if (particleRate > 0) particleRate--;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			updateDelay = 20;
		}
	}

	private TileEnergyStorageCore getMaster(){
		return  (worldObj.getTileEntity(masterLocation.getXCoord(), masterLocation.getYCoord(), masterLocation.getZCoord()) != null && worldObj.getTileEntity(masterLocation.getXCoord(), masterLocation.getYCoord(), masterLocation.getZCoord()) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore)worldObj.getTileEntity(masterLocation.getXCoord(), masterLocation.getYCoord(), masterLocation.getZCoord()) : null;
	}

	private void findMaster(){
		int yMod = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1 ? 3 : -3;
		int range = 15;
		for (int x = xCoord-range; x <= xCoord+range; x++){
			for (int y = yCoord+yMod-(range/2); y <= yCoord+yMod+(range/2); y++){
				for (int z = zCoord-range; z <= zCoord+range; z++){
					if (worldObj.getBlock(x, y, z) == ModBlocks.energyStorageCore){
						masterLocation = new TileLocation(x, y, z);
						return;
					}
				}
			}

		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(){
		Random rand = new Random();
		int x = masterLocation.getXCoord();
		int y = masterLocation.getYCoord();
		int z = masterLocation.getZCoord();
		int cYCoord = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1 ? yCoord+1 : yCoord-1;
		TileEnergyStorageCore master = (worldObj.getTileEntity(x, y, z) != null && worldObj.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) worldObj.getTileEntity(x, y, z) : null;
		if (master == null || !master.isOnline()) return;
		float disMod = master.getTier()==0 ? 0.5F : master.getTier()==1 ? 1F: master.getTier()==2 ? 1F : master.getTier()==3 ? 2F : master.getTier()==4 ? 2F : master.getTier()==5 ? 3F : 4F;
		double spawnX;
		double spawnY;
		double spawnZ;
		double targetX;
		double targetY;
		double targetZ;
		if (particleRate > 100) particleRate = 100;
		if (input){
			spawnX = x+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			spawnY = y+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			spawnZ = z+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			targetX = xCoord + 0.5;
			targetY = cYCoord + 0.5;
			targetZ = zCoord + 0.5;
			if (rand.nextFloat() < 0.1F) {
				Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, true);
				ParticleHandler.spawnCustomParticle(passiveParticle, 50);
			}
			if (particleRate > 0){
				if (particleRate > 10) {
					for (int i = 0; i <= particleRate/10; i++) {
						spawnX = x + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
						spawnY = y + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
						spawnZ = z + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
						Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, false);
						ParticleHandler.spawnCustomParticle(passiveParticle, 50);
					}
				}else if (rand.nextInt(Math.max(1, 10 - particleRate)) == 0){
					spawnX = x + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
					spawnY = y + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
					spawnZ = z + 0.5 - disMod + (rand.nextFloat() * (disMod * 2));
					Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, false);
					ParticleHandler.spawnCustomParticle(passiveParticle, 50);
				}
			}

		}else {
			targetX = x+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			targetY = y+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			targetZ = z+0.5 - disMod + (rand.nextFloat() * (disMod*2));
			spawnX = xCoord + 0.5;
			spawnY = cYCoord + 0.5;
			spawnZ = zCoord + 0.5;
			if (rand.nextFloat() < 0.1F) {
				Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, true);
				ParticleHandler.spawnCustomParticle(passiveParticle, 50);
			}

			if (particleRate > 0){
				if (particleRate > 10) {
					for (int i = 0; i <= particleRate/10; i++) {
						targetX = x+0.5 - disMod + (rand.nextFloat() * (disMod*2));
						targetY = y+0.5 - disMod + (rand.nextFloat() * (disMod*2));
						targetZ = z+0.5 - disMod + (rand.nextFloat() * (disMod*2));
						Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, false);
						ParticleHandler.spawnCustomParticle(passiveParticle, 50);
					}
				}else if (rand.nextInt(Math.max(1, 10 - particleRate)) == 0){
					targetX = x+0.5 - disMod + (rand.nextFloat() * (disMod*2));
					targetY = y+0.5 - disMod + (rand.nextFloat() * (disMod*2));
					targetZ = z+0.5 - disMod + (rand.nextFloat() * (disMod*2));
					Particles.EnergyTransferParticle passiveParticle = new Particles.EnergyTransferParticle(worldObj, spawnX, spawnY, spawnZ, targetX, targetY, targetZ, false);
					ParticleHandler.spawnCustomParticle(passiveParticle, 50);
				}
			}
		}
	}

	private boolean isValidStructure(){
		return (isGlass(xCoord, yCoord+1, zCoord) || isGlass(xCoord, yCoord-1, zCoord)) && (!isGlass(xCoord, yCoord+1, zCoord) || !isGlass(xCoord, yCoord-1, zCoord));
	}

	private boolean isGlass(int x, int y, int z){
		return worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock && worldObj.getBlockMetadata(x, y, z) == 2;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		storage.readFromNBT(compound);
		active = compound.getBoolean("Active");
		input = compound.getBoolean("Input");
		masterLocation.readFromNBT(compound, "Master");
		particleRate = compound.getShort("ParticleRate");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {

		super.writeToNBT(compound);
		storage.writeToNBT(compound);
		compound.setBoolean("Active", active);
		compound.setBoolean("Input", input);
		masterLocation.writeToNBT(compound, "Master");
		compound.setShort("ParticleRate", (short)particleRate);
	}

	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	/* IEnergyHandler */
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		nextUpdate = true;
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		nextUpdate = true;
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
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
