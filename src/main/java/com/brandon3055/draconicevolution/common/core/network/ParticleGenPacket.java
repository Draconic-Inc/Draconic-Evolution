package com.brandon3055.draconicevolution.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;

public class ParticleGenPacket implements IPacket
{
	byte buttonId = 0;
	short value = 0;
	int tileX = 0;
	int tileY = 0;
	int tileZ = 0;
	
	public ParticleGenPacket() {}

	public ParticleGenPacket(byte buttonId, short value, int x, int y, int z) {
		this.buttonId = buttonId;
		this.value = value;
		this.tileX = x;
		this.tileY = y;
		this.tileZ = z;
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeByte(buttonId);
		bytes.writeShort(value);
		bytes.writeInt(tileX);
		bytes.writeInt(tileY);
		bytes.writeInt(tileZ);
	}
	
	@Override
	public void readBytes(ByteBuf bytes)
	{
		this.buttonId = bytes.readByte();
		this.value = bytes.readShort();
		this.tileX = bytes.readInt();
		this.tileY = bytes.readInt();
		this.tileZ = bytes.readInt();
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player)
	{
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer player)
	{	
		TileEntity tile = player.playerEntity.worldObj.getTileEntity(tileX, tileY, tileZ);
		TileParticleGenerator gen = (tile != null && tile instanceof TileParticleGenerator) ? (TileParticleGenerator)tile : null;
		if (gen != null)
		{
			//System.out.println(buttonId + " " + value);
			switch (buttonId) {
				case 0://red
					gen.red = (int) value;
					break;
				case 1://green
					gen.green = (int) value;
					break;
				case 2://blue
					gen.blue = (int) value;
					break;
				case 3://mx
					gen.motion_x = (float) value / 1000F;
					break;
				case 4://my
					gen.motion_y = (float) value / 1000F;
					break;
				case 5://mz
					gen.motion_z = (float) value / 1000F;
					break;
				case 6://ged
					gen.red = (int) value;
					break;
				case 7://green
					gen.green = (int) value;
					break;
				case 8://blue
					gen.blue = (int) value;
					break;
				case 9://mx
					gen.motion_x = (float) value / 1000F;
					break;
				case 10://my
					gen.motion_y = (float) value / 1000F;
					break;
				case 11://mz
					gen.motion_z = (float) value / 1000F;
					break;
				case 12://red
					gen.random_red = (int) value;
					break;
				case 13://green
					gen.random_green = (int) value;
					break;
				case 14://blue
					gen.random_blue = (int) value;
					break;
				case 15://mx
					gen.random_motion_x = (float) value / 1000F;
					break;
				case 16://my
					gen.random_motion_y = (float) value / 1000F;
					break;
				case 17://mz
					gen.random_motion_z = (float) value / 1000F;
					break;
				case 18://ged
					gen.random_red = (int) value;
					break;
				case 19://green
					gen.random_green = (int) value;
					break;
				case 20://blue
					gen.random_blue = (int) value;
					break;
				case 21://mx
					gen.random_motion_x = (float) value / 1000F;
					break;
				case 22://my
					gen.random_motion_y = (float) value / 1000F;
					break;
				case 23://mz
					gen.random_motion_z = (float) value / 1000F;
					break;
				case 24://Life +
					gen.life = (int) value;
					break;
				case 25://Life -
					gen.life = (int) value;
					break;
				case 26://RLife +
					gen.random_life = (int) value;
					break;
				case 27://RLife -
					gen.random_life = (int) value;
					break;
				case 28://Size +
					gen.scale = (float) value / 100F;
					break;
				case 29://Size -
					gen.scale = (float) value / 100F;
					break;
				case 30://RSize +
					gen.random_scale = (float) value / 100F;
					break;
				case 31://RSize -
					gen.random_scale = (float) value / 100F;
					break;
				case 32://SX +
					gen.page = (int) value;
					break;
				case 33://SX -
					gen.page = (int) value;
					break;
				case 34://SX +
					gen.spawn_x = (float) value / 100F;
					break;
				case 35://SX -
					gen.spawn_x = (float) value / 100F;
					break;
				case 36://RSX +
					gen.random_spawn_x = (float) value / 100F;
					break;
				case 37://RSX -
					gen.random_spawn_x = (float) value / 100F;
					break;
				case 38://SY +
					gen.spawn_y = (float) value / 100F;
					break;
				case 39://SY -
					gen.spawn_y = (float) value / 100F;
					break;
				case 40://RSY +
					gen.random_spawn_y = (float) value / 100F;
					break;
				case 41://RSY -
					gen.random_spawn_y = (float) value / 100F;
					break;
				case 42://SZ +
					gen.spawn_z = (float) value / 100F;
					break;
				case 43://SZ -
					gen.spawn_z = (float) value / 100F;
					break;
				case 44://RSZ +
					gen.random_spawn_z = (float) value / 100F;
					break;
				case 45://RSZ -
					gen.random_spawn_z = (float) value / 100F;
					break;
				case 46://Delay -
					gen.spawn_rate = (int) value;
					break;
				case 47://Delay -
					gen.spawn_rate = (int) value;
					break;
				case 48://Fade -
					gen.fade = (int) value;
					break;
				case 49://Fade -
					gen.fade = (int) value;
					break;
				case 50://Collision -
					gen.collide = value == 0 ? false : true;
					break;
				case 51://Particle Selected -
					gen.selected_particle = (int) value;
					break;
				case 52://Gravity +
					gen.gravity = (float) value / 1000F;
					break;
				case 53://Gravity -
					gen.gravity = (float) value / 1000F;
					break;
				case 54://Info Page (3)
					gen.page = (int) value;
					break;
				case 55://Back
					gen.page = (int) value;
					break;
			}
			player.playerEntity.worldObj.markBlockForUpdate(tileX, tileY, tileZ);
		}
	}

}