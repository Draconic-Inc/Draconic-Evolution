package com.brandon3055.draconicevolution.common.tileentities;

import com.brandon3055.draconicevolution.client.render.EnergyParticle;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import sun.rmi.runtime.Log;

import java.util.Iterator;
import java.util.List;

public class TileCustomSpawner extends TileEntity
{
	public boolean isSetToSpawn = false;
	public boolean trySet = false;
	public EntityPlayer owner;
	private EntityLivingBase target;
	private int setTick = 0;
	boolean foundTarget = false;

	private final CustomSpawnerBaseLogic spawnerBaseLogic = new CustomSpawnerBaseLogic(){
		public void blockEvent(int par1)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, Blocks.mob_spawner, par1, 0);
		}
		public World getSpawnerWorld()
		{
			return worldObj;
		}
		public int getSpawnerX()
		{
			return xCoord;
		}
		public int getSpawnerY()
		{
			return yCoord;
		}
		public int getSpawnerZ()
		{
			return zCoord;
		}
	};

	public void updateEntity(){
		if (isSetToSpawn) {
			spawnerBaseLogic.updateSpawner();
		}
		/*
		else if (trySet && owner != null){

			if (worldObj.isRemote) spawnParticles(false);

			if (!foundTarget && setTick > 70 && target == null && setTick % 10 == 0)
			{
				foundTarget = findTargetEntity();
				setTick = 71;
			}

			if (foundTarget)
			{
				target.setHealth(10);
				if (setTick < 150) {
					target.setPosition(xCoord + 0.5, yCoord, zCoord + 0.5);
				} else {
					target.setPosition(xCoord + 0.5, yCoord - 0.5, zCoord + 0.5);
				}
				spawnParticles(true);
				if (setTick > 151){
					spawnerBaseLogic.entityName = target.getCommandSenderName();
					target.setDead();
					setTick = 0;
					trySet = false;
					isSetToSpawn = true;
				}


			}

			setTick++;
			if (setTick > 200){
				setTick = 0;
				trySet = false;
			}
		} else {
			trySet = false;
		}
		*/
	}

	public void writeToNBT(NBTTagCompound tagCompound){
		super.writeToNBT(tagCompound);
		spawnerBaseLogic.writeToNBT(tagCompound);
		tagCompound.setBoolean("Running", isSetToSpawn);
	}

	public void readFromNBT(NBTTagCompound tagCompound){
		super.readFromNBT(tagCompound);
		spawnerBaseLogic.readFromNBT(tagCompound);
		isSetToSpawn = tagCompound.getBoolean("Running");
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

	public CustomSpawnerBaseLogic getBaseLogic()
	{
		return spawnerBaseLogic;
	}

	private boolean findTargetEntity(){
		List<EntityLivingBase> candidates = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord+0.5 - 2.5, yCoord+0.5 - 1, zCoord+0.5 - 2.5, xCoord+0.5 + 2.5, yCoord+0.5 + 2, zCoord+0.5 + 2.5));

		if (candidates.isEmpty())
			return false;

		Iterator<EntityLivingBase> i = candidates.iterator();
		while(i.hasNext())
		{
			EntityLivingBase ent = i.next();
			if (!(ent instanceof EntityPlayer))
			{
				target = ent;
				LogHelper.info(target);
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(boolean flag){
		if (!flag) {
			for (int i = 0; i < 5; i++) {
				float rotation = worldObj.rand.nextFloat() * 6.5F;
				double x = (double) xCoord + 0.5 + (Math.sin(rotation) * 3);
				double y = (double) yCoord - 1 + worldObj.rand.nextInt(4);
				double z = (double) zCoord + 0.5 + (Math.cos(rotation) * 3);
				EnergyParticle particle = new EnergyParticle(worldObj, x, y, z, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0, false);
				ParticleHandler.spawnCustomParticle(particle, 20);
			}
		}else
		{
			double x = (double) xCoord + (worldObj.rand.nextDouble());
			double y = (double) yCoord + (double)target.getEyeHeight() + worldObj.rand.nextDouble();
			double z = (double) zCoord + (worldObj.rand.nextDouble());
			EnergyParticle particle = new EnergyParticle(worldObj, x, y, z, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0);
			ParticleHandler.spawnCustomParticle(particle, 20);
		}
	}
}
