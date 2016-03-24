// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving.playerapi;

import io.netty.buffer.*;

import java.util.*;

import cpw.mods.fml.common.network.internal.*;

import api.player.server.*;

import net.smart.moving.*;
import net.smart.utilities.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class SmartMovingServerPlayerBase extends ServerPlayerBase implements IEntityPlayerMP
{
	public static void registerPlayerBase()
	{
		ServerPlayerAPI.register(SmartMovingInfo.ModName, SmartMovingServerPlayerBase.class);
	}

	public static SmartMovingServerPlayerBase getPlayerBase(Object player)
	{
		return (SmartMovingServerPlayerBase)((IServerPlayerAPI)player).getServerPlayerBase(SmartMovingInfo.ModName);
	}

	public SmartMovingServerPlayerBase(ServerPlayerAPI playerApi)
	{
		super(playerApi);
		moving = new SmartMovingServer(this, false);
	}

	@Override
	public float getHeight()
	{
		return player.height;
	}

	@Override
	public double getMinY()
	{
		return player.boundingBox.minY;
	}

	@Override
	public void setMaxY(double maxY)
	{
		player.boundingBox.maxY = maxY;
	}

	@Override
	public void afterSetPosition(double d, double d1, double d2)
	{
		moving.afterSetPosition(d, d1, d2);
	}

	@Override
	public void beforeIsPlayerSleeping()
	{
		moving.beforeIsPlayerSleeping();
	}

	@Override
	public void beforeOnUpdate()
	{
		moving.beforeOnUpdate();
	}

	@Override
	public void afterOnUpdate()
	{
		moving.afterOnUpdate();
	}

	@Override
	public void beforeOnLivingUpdate()
	{
		moving.beforeOnLivingUpdate();
	}

	@Override
	public void afterOnLivingUpdate()
	{
		moving.afterOnLivingUpdate();
	}

	@Override
	public float doGetHealth()
	{
		return player.getHealth();
	}

	@Override
	public AxisAlignedBB getBox()
	{
		return player.boundingBox;
	}

	@Override
	public AxisAlignedBB expandBox(AxisAlignedBB box, double x, double y, double z)
	{
		return box.expand(x, y, z);
	}

	@Override
	public List<?> getEntitiesExcludingPlayer(AxisAlignedBB box)
	{
		return player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
	}

	@Override
	public boolean isDeadEntity(Entity entity)
	{
		return entity.isDead;
	}

	@Override
	public void onCollideWithPlayer(Entity entity)
	{
		entity.onCollideWithPlayer(player);
	}

	@Override
	public float getEyeHeight()
	{
		return player.height - 0.18F;
	}

	@Override
	public boolean isEntityInsideOpaqueBlock()
	{
		return moving.isEntityInsideOpaqueBlock();
	}

	@Override
	public boolean localIsEntityInsideOpaqueBlock()
	{
		return super.isEntityInsideOpaqueBlock();
	}

	@Override
	public void addExhaustion(float exhaustion)
	{
		moving.addExhaustion(exhaustion);
	}

	@Override
	public void localAddExhaustion(float exhaustion)
	{
		super.addExhaustion(exhaustion);
	}

	@Override
	public void addMovementStat(double x, double y, double z)
	{
		moving.addMovementStat(x, y, z);
	}

	@Override
	public void localAddMovementStat(double x, double y, double z)
	{
		super.addMovementStat(x, y, z);
	}

	@Override
	public void localPlaySound(String soundId, float volume, float pitch)
	{
		player.playSound(soundId, volume, pitch);
	}

	@Override
	public void beforeUpdatePotionEffects()
	{
		moving.afterAddMovingHungerBatch();
	}

	@Override
	public void afterUpdatePotionEffects()
	{
		moving.beforeAddMovingHungerBatch();
	}

	@Override
	public boolean isSneaking()
	{
		return moving.isSneaking();
	}

	@Override
	public boolean localIsSneaking()
	{
		return playerAPI.localIsSneaking();
	}

	@Override
	public void setHeight(float height)
	{
		player.height = height;
	}

	@Override
	public void sendPacket(byte[] data)
	{
		player.playerNetServerHandler.sendPacket(new FMLProxyPacket(Unpooled.wrappedBuffer(data), SmartMovingPacketStream.Id));
	}

	@Override
	public String getUsername()
	{
		return player.getGameProfile().getName();
	}

	@Override
	public void resetFallDistance()
	{
		player.fallDistance = 0;
		player.motionY = 0.08;
	}

	@Override
	public void resetTicksForFloatKick()
	{
		Reflect.SetField(net.minecraft.network.NetHandlerPlayServer.class, player.playerNetServerHandler, SmartMovingInstall.NetServerHandler_ticksForFloatKick, 0);
	}

	@Override
	public void sendPacketToTrackedPlayers(FMLProxyPacket packet)
	{
		player.mcServer.worldServerForDimension(player.dimension).getEntityTracker().func_151247_a(player, packet);
	}

	@Override
	public SmartMovingServer getMoving()
	{
		return moving;
	}

	@Override
	public IEntityPlayerMP[] getAllPlayers()
	{
		List<?> playerEntityList = player.mcServer.getConfigurationManager().playerEntityList;
		IEntityPlayerMP[] result = new IEntityPlayerMP[playerEntityList.size()];
		for(int i=0; i<playerEntityList.size(); i++)
			result[i] = (IEntityPlayerMP)((IServerPlayerAPI)playerEntityList.get(i)).getServerPlayerBase(SmartMovingInfo.ModName);
		return result;
	}

	public final SmartMovingServer moving;
}