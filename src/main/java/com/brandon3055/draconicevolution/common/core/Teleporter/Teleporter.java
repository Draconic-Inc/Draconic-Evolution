package com.brandon3055.draconicevolution.common.core.Teleporter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class Teleporter
{
	public static void teleport(EntityPlayer player, double x, double y, double z, float yaw, float pitch, int dim)
	{
		if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP) player;

			if (thePlayer.dimension == dim)
			{
				player.setLocationAndAngles(x, y, z, yaw, pitch);
				player.setPositionAndUpdate(x, y, z);
			} else if (thePlayer.dimension == 1)
			{
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
			} else
			{
				if (thePlayer.isRiding())
					thePlayer.dismountEntity(thePlayer.ridingEntity);
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, dim, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(dim), x, y, z, yaw, pitch));
			}
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
				//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
			} else
			{
				if (thePlayer.isRiding())
					thePlayer.dismountEntity(thePlayer.ridingEntity);
				thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, thePlayer.dimension, new CustomTeleporter(thePlayer.mcServer.worldServerForDimension(thePlayer.dimension), x, y, z, 0, 0));
			}
		}
	}
}
