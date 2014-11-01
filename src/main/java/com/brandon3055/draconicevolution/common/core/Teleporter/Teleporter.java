package com.brandon3055.draconicevolution.common.core.Teleporter;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class Teleporter
{
	public static void teleport(EntityLivingBase entity, double x, double y, double z, float yaw, float pitch, int dim)
	{
		if (entity.worldObj.isRemote) return;
		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP) entity;

			if (thePlayer.dimension == dim)
			{
				entity.setLocationAndAngles(x, y, z, yaw, pitch);
				entity.setPositionAndUpdate(x, y, z);
			} else
			{
				if (thePlayer.isRiding())
					thePlayer.dismountEntity(thePlayer.ridingEntity);
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
			}
		} else if (dim == entity.dimension)
		{
			entity.setLocationAndAngles(x, y, z, yaw, pitch);
			entity.setPositionAndUpdate(x, y, z);
		}
	}

	public static void teleport(EntityPlayer player, double x, double y, double z)
	{
		if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP) player;

			if (thePlayer.dimension == 1)
			{
				if (thePlayer.isRiding())
					thePlayer.dismountEntity(thePlayer.ridingEntity);
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
			} else
			{
				if (thePlayer.isRiding())
					thePlayer.dismountEntity(thePlayer.ridingEntity);
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
			}
		}
	}

	public static class TeleportLocation {
		protected double xCoord;
		protected double yCoord;
		protected double zCoord;
		protected int dimension;
		protected float pitch;
		protected float yaw;
		protected String name;
		protected boolean writeProtected = false;

		public TeleportLocation(){

		}

		public TeleportLocation(double x, double y, double z, int dimension, float pitch, float yaw, String name){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = pitch;
			this.yaw = yaw;
			this.name = name;
		}

		public double getXCoord(){
			return xCoord;
		}

		public double getYCoord(){
			return yCoord;
		}

		public double getZCoord(){
			return zCoord;
		}

		public int getDimension() {return dimension;}

		public float getPitch() {return pitch;}

		public float getYaw() {return yaw;}

		public String getName() {return name;}

		public boolean getWriteProtected() {return writeProtected;}

		public void setXCoord(double x){
			xCoord = x;
		}

		public void setYCoord(double y){
			yCoord = y;
		}

		public void setZCoord(double z){
			zCoord = z;
		}

		public void setDimension(int d) {dimension = d;}

		public void setPitch(float p) {pitch = p;}

		public void setYaw(float y) {yaw = y;}

		public void setName(String s) {name = s;}

		public void setWriteProtected(boolean b) {writeProtected = b;}

		public void writeToNBT(NBTTagCompound compound) {
			compound.setDouble("X", xCoord);
			compound.setDouble("Y", yCoord);
			compound.setDouble("Z", zCoord);
			compound.setInteger("Dimension", dimension);
			compound.setFloat("Pitch", pitch);
			compound.setFloat("Yaw", yaw);
			compound.setString("Name", name);
			compound.setBoolean("WP", writeProtected);
		}

		public void readFromNBT(NBTTagCompound compound) {
			xCoord = compound.getDouble("X");
			yCoord = compound.getDouble("Y");
			zCoord = compound.getDouble("Z");
			dimension = compound.getInteger("Dimension");
			pitch = compound.getFloat("Pitch");
			yaw = compound.getFloat("Yaw");
			name = compound.getString("Name");
			writeProtected = compound.getBoolean("WP");
		}

		public void sendEntityToCoords(EntityLivingBase entity){
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "portal.travel", 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			if (entity.isRiding() && entity.dimension == getDimension()){
				sendEntityToCoords((EntityLivingBase)entity.ridingEntity);
			}
			Teleporter.teleport(entity, getXCoord(), getYCoord(), getZCoord(), getYaw(), getPitch(), getDimension());
			entity.fallDistance = 0;
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "portal.travel", 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
}
