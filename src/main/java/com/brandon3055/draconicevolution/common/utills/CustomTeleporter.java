package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CustomTeleporter extends Teleporter
{
	private double tX;
	private double tY;
	private double tZ;
	private float yaw;
	private float pitch;

	public CustomTeleporter(WorldServer worldServer, double tX, double tY, double tZ, float yaw, float pitch) {
		super(worldServer);
		
		this.tX = tX;
		this.tY = tY;
		this.tZ = tZ;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float r)
	{
		entity.setLocationAndAngles(this.tX, this.tY, this.tZ, this.yaw, this.pitch);
	}

	@Override
	public void removeStalePortalLocations(long par1)
	{
	}

	@Override
	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
		return false;
	}
}
