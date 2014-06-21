package com.brandon3055.draconicevolution.common.tileentities;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import com.brandon3055.draconicevolution.client.render.CustomParticle;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;

public class TileParticleGenerator extends TileEntity
{
	public int red = 0;
	public int green = 0;
	public int blue = 0;
	public int random_red = 0;
	public int random_green = 0;
	public int random_blue = 0;
	public float motion_x = 0.0F;
	public float motion_y = 0.0F;
	public float motion_z = 0.0F;
	public float random_motion_x = 0.0F;
	public float random_motion_y = 0.0F;
	public float random_motion_z = 0.0F;
	public float scale = 1F;
	public float random_scale = 0F;
	public int life = 100;
	public int random_life = 0;
	public float spawn_x = 0;
	public float spawn_y = 0;
	public float spawn_z = 0;
	public float random_spawn_x = 0;
	public float random_spawn_y = 0;
	public float random_spawn_z = 0;
	public int page = 1;
	public int fade = 0;
	public int spawn_rate = 1;
	public boolean collide = false;
	public int selected_particle = 1;
	public float gravity = 0F;
	public boolean active = true;
	public boolean signal = false;
	public boolean inverted = false;
	
	
	private int tick = 0;

	@SideOnly(Side.CLIENT)
	@Override
	public void updateEntity()
	{
		if (signal && !inverted)
			active = true;
		else if (!signal && inverted)
			active = true;
		else
			active = false;
		
		if (tick >= spawn_rate && active)
		{
			tick = 0;
			if (worldObj.isRemote)
			{
				Random rand = worldObj.rand;

				float MX = motion_x + (random_motion_x * rand.nextFloat());
				float MY = motion_y + (random_motion_y * rand.nextFloat());
				float MZ = motion_z + (random_motion_z * rand.nextFloat());
				float SCALE = scale + (random_scale * rand.nextFloat());
				double spawnX = xCoord + spawn_x + (random_spawn_x * rand.nextFloat());
				double spawnY = yCoord + spawn_y + (random_spawn_y * rand.nextFloat());
				double spawnZ = zCoord + spawn_z + (random_spawn_z * rand.nextFloat());

				CustomParticle particle = new CustomParticle(worldObj, spawnX + 0.5, spawnY + 0.5, spawnZ + 0.5, MX, MY, MZ, SCALE, collide, this.selected_particle);
				particle.red = this.red + rand.nextInt(random_red + 1);
				particle.green = this.green + rand.nextInt(random_green + 1);
				particle.blue = this.blue + rand.nextInt(random_blue + 1);
				particle.maxAge = this.life + rand.nextInt(random_life + 1);
				particle.fadeTime = this.fade;
				particle.fadeLength = this.fade;
				particle.gravity = this.gravity;

				ParticleHandler.spawnCustomParticle(particle);
			}
		}
		else
			tick++;
	}
	
	public void toggleInverted(){
		inverted = !inverted;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("Red", red);
		compound.setInteger("Green", green);
		compound.setInteger("Blue", blue);
		compound.setInteger("RandomRed", random_red);
		compound.setInteger("RandomGreen", random_green);
		compound.setInteger("RandomBlue", random_blue);
		compound.setFloat("MotionX", motion_x);
		compound.setFloat("MotionY", motion_y);
		compound.setFloat("MotionZ", motion_z);
		compound.setFloat("RandomMotionX", random_motion_x);
		compound.setFloat("RandomMotionY", random_motion_y);
		compound.setFloat("RandomMotionZ", random_motion_z);
		compound.setFloat("Scale", scale);
		compound.setFloat("RandomScale", random_scale);
		compound.setInteger("Life", life);
		compound.setInteger("RandomLife", random_life);
		compound.setFloat("SpawnX", spawn_x);
		compound.setFloat("SpawnY", spawn_y);
		compound.setFloat("SpawnZ", spawn_z);
		compound.setFloat("RandomSpawnX", random_spawn_x);
		compound.setFloat("RandomSpawnY", random_spawn_y);
		compound.setFloat("RandomSpawnZ", random_spawn_z);
		compound.setInteger("Page", page);
		compound.setInteger("SpawnRate", spawn_rate);
		compound.setBoolean("CanCollide", collide);
		compound.setInteger("Fade", fade);
		compound.setInteger("SelectedParticle", selected_particle);
		compound.setFloat("Gravity", gravity);
		compound.setBoolean("Active", active);
		compound.setBoolean("Signal", signal);
        compound.setBoolean("Inverted", inverted);

		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		red = compound.getInteger("Red");
		green = compound.getInteger("Green");
		blue = compound.getInteger("Blue");
		random_red = compound.getInteger("RandomRed");
		random_green = compound.getInteger("RandomGreen");
		random_blue = compound.getInteger("RandomBlue");
		motion_x = compound.getFloat("MotionX");
		motion_y = compound.getFloat("MotionY");
		motion_z = compound.getFloat("MotionZ");
		random_motion_x = compound.getFloat("RandomMotionX");
		random_motion_y = compound.getFloat("RandomMotionY");
		random_motion_z = compound.getFloat("RandomMotionZ");
		scale = compound.getFloat("Scale");
		random_scale = compound.getFloat("RandomScale");
		life = compound.getInteger("Life");
		random_life = compound.getInteger("RandomLife");
		spawn_x = compound.getFloat("SpawnX");
		spawn_y = compound.getFloat("SpawnY");
		spawn_z = compound.getFloat("SpawnZ");
		random_spawn_x = compound.getFloat("RandomSpawnX");
		random_spawn_y = compound.getFloat("RandomSpawnY");
		random_spawn_z = compound.getFloat("RandomSpawnZ");
		page = compound.getInteger("Page");
		spawn_rate = compound.getInteger("SpawnRate");
		collide = compound.getBoolean("CanCollide");
		fade = compound.getInteger("Fade");
		selected_particle = compound.getInteger("SelectedParticle");
		gravity = compound.getFloat("Gravity");
		active = compound.getBoolean("Active");
		signal = compound.getBoolean("Signal");
        inverted = compound.getBoolean("Inverted");

		super.readFromNBT(compound);
	}

}
