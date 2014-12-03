package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Teleporter
{
//	public static void teleport(EntityLivingBase entity, double x, double y, double z, float yaw, float pitch, int dim)
//	{
//		//if (entity.worldObj.isRemote) return;
//		if (entity instanceof EntityPlayerMP)
//		{
//			EntityPlayerMP thePlayer = (EntityPlayerMP) entity;
//			LogHelper.info(dim +" "+thePlayer.dimension);
//
//			if (thePlayer.dimension == dim)
//			{
//				entity.setLocationAndAngles(x, y, z, yaw, pitch);
//				entity.setPositionAndUpdate(x, y, z);
//			} else
//			{
//				if (thePlayer.isRiding())
//					thePlayer.dismountEntity(thePlayer.ridingEntity);
//			//	if (thePlayer.dimension == 1) thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
//				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
//
//			}
//		} else if (dim == entity.dimension)
//		{
//			entity.setLocationAndAngles(x, y, z, yaw, pitch);
//			entity.setPositionAndUpdate(x, y, z);
//		}
//		LogHelper.info(entity.dimension);
//	}
//
//	public static void teleport(EntityPlayer player, double x, double y, double z)
//	{
//		if (player instanceof EntityPlayerMP)
//		{
//			EntityPlayerMP thePlayer = (EntityPlayerMP) player;
//
//			if (thePlayer.dimension == 1)
//			{
//				if (thePlayer.isRiding())
//					thePlayer.dismountEntity(thePlayer.ridingEntity);
//				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
//			} else
//			{
//				if (thePlayer.isRiding())
//					thePlayer.dismountEntity(thePlayer.ridingEntity);
//				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
//			}
//		}
//	}

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

		public TeleportLocation(double x, double y, double z, int dimension){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = 0;
			this.yaw = 0;
		}

		public TeleportLocation(double x, double y, double z, int dimension, float pitch, float yaw){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = pitch;
			this.yaw = yaw;
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

		public String getDimensionName() {
			return MinecraftServer.getServer().worldServerForDimension(dimension).provider.getDimensionName();
		}

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

		public void sendEntityToCoords(Entity entity){
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "portal.travel", 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);

			teleportEntity(entity, this);
//			boolean dimensional = false;
//
//			if (entity.dimension != dimension) dimensional = true;
//
//			if (entity instanceof EntityPlayerMP){
//				EntityPlayerMP player = (EntityPlayerMP)entity;
//				Entity ridingEntity = player.ridingEntity;
//
//				if (dimensional)transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
//				player.rotationPitch = pitch;
//				player.rotationYaw = yaw;
//				player.setPositionAndUpdate(xCoord, yCoord+0.3, zCoord);
//
//				if (ridingEntity != null){
//					if (dimensional)transferEntityToWorld(ridingEntity, (WorldServer)entity.worldObj, MinecraftServer.getServer().worldServerForDimension(dimension));
//					ridingEntity.setPosition(xCoord, yCoord + 0.3, zCoord);
//				}
//			}
//			else {
//				LogHelper.info("current dimension "+MinecraftServer.getServer().worldServerForDimension(entity.dimension).provider.getDimensionName());
//				LogHelper.info("new dimension "+MinecraftServer.getServer().worldServerForDimension(dimension).provider.getDimensionName());
//				//entity.setPosition(xCoord, yCoord + 0.3, zCoord);
//				//entity.setLocationAndAngles(xCoord, yCoord + 0.3, zCoord, yaw, pitch);
//				LogHelper.info(dimensional);
//				if (dimensional) travelEntity(entity.worldObj, entity);//transferEntityToWorld(entity, MinecraftServer.getServer().worldServerForDimension(entity.dimension), MinecraftServer.getServer().worldServerForDimension(dimension));
//			}
//
//			entity.fallDistance = 0;
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "portal.travel", 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}



	@SuppressWarnings("unchecked")
	private static Entity teleportEntity(Entity entity, TeleportLocation destination)
	{
		if (entity == null || entity.worldObj.isRemote) return entity;

		World startWorld = entity.worldObj;
		World destinationWorld =DraconicEvolution.proxy.getMCServer().worldServerForDimension(destination.dimension);

		if (destinationWorld == null){
			LogHelper.error("Destination world dose not exist!");
			return entity;
		}

		Entity mount = entity.ridingEntity;
		if (entity.ridingEntity != null)
		{
			entity.mountEntity(null);
			mount = teleportEntity(mount, destination);
		}

		boolean interDimensional = startWorld.provider.dimensionId != destinationWorld.provider.dimensionId;

		//startWorld.updateEntityWithOptionalForce(entity, false);

		if ((entity instanceof EntityPlayerMP) && interDimensional)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;
			player.dimension = destination.dimension;
			player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, destinationWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
			((WorldServer)startWorld).getPlayerManager().removePlayer(player);

			startWorld.playerEntities.remove(player);
			startWorld.updateAllPlayersSleepingFlag();
			int i = entity.chunkCoordX;
			int j = entity.chunkCoordZ;
			if ((entity.addedToChunk) && (startWorld.getChunkProvider().chunkExists(i, j)))
			{
				startWorld.getChunkFromChunkCoords(i, j).removeEntity(entity);
				startWorld.getChunkFromChunkCoords(i, j).isModified = true;
			}
			startWorld.loadedEntityList.remove(entity);
			startWorld.onEntityRemoved(entity);
		}

		entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, destination.pitch);

		((WorldServer)destinationWorld).theChunkProviderServer.loadChunk((int)destination.xCoord >> 4, (int)destination.zCoord >> 4);

		destinationWorld.theProfiler.startSection("placing");
		if (interDimensional)
		{
			if (!(entity instanceof EntityPlayer))
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.isDead = false;
				entityNBT.setString("id", EntityList.getEntityString(entity));
				entity.writeToNBT(entityNBT);
				entity.isDead = true;
				entity = EntityList.createEntityFromNBT(entityNBT, destinationWorld);
				if (entity == null)
				{
					LogHelper.error("Failed to teleport entity to new location");
					return null;
				}
				entity.dimension = destinationWorld.provider.dimensionId;
			}
			destinationWorld.spawnEntityInWorld(entity);
			entity.setWorld(destinationWorld);
		}
		entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

		destinationWorld.updateEntityWithOptionalForce(entity, false);
		entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

		if ((entity instanceof EntityPlayerMP))
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;
			if (interDimensional) {
				player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) destinationWorld);
			}
			player.playerNetServerHandler.setPlayerLocation(destination.xCoord, destination.yCoord, destination.zCoord, player.rotationYaw, player.rotationPitch);
		}

		destinationWorld.updateEntityWithOptionalForce(entity, false);

		if (((entity instanceof EntityPlayerMP)) && interDimensional)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;
			player.theItemInWorldManager.setWorld((WorldServer) destinationWorld);
			player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) destinationWorld);
			player.mcServer.getConfigurationManager().syncPlayerInventory(player);

			for (PotionEffect potionEffect : (Iterable<PotionEffect>) player.getActivePotionEffects())
			{
				player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potionEffect));
			}

			player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		}
		entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

		if (mount != null)
		{
			entity.mountEntity(mount);
			if ((entity instanceof EntityPlayerMP)) {
				destinationWorld.updateEntityWithOptionalForce(entity, true);
			}
		}
		destinationWorld.theProfiler.endSection();
		entity.fallDistance = 0;
		return entity;
	}
}
