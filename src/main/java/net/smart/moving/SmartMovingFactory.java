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

package net.smart.moving;

import java.util.*;

import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

public class SmartMovingFactory extends SmartMovingContext
{
	private static SmartMovingFactory factory;

	private Hashtable<Integer, SmartMovingOther> otherSmartMovings;

	public SmartMovingFactory()
	{
		if(factory != null)
			throw new RuntimeException("FATAL: Can only create one instance of type 'SmartMovingFactory'");
		factory = this;
	}

	protected static boolean isInitialized()
	{
		return factory != null;
	}

	public static void initialize()
	{
		if(!isInitialized())
			new SmartMovingFactory();
	}

	public static void handleMultiPlayerTick(Minecraft minecraft)
	{
		factory.doHandleMultiPlayerTick(minecraft);
	}

	public static SmartMoving getInstance(EntityPlayer entityPlayer)
	{
		return factory.doGetInstance(entityPlayer);
	}

	public static SmartMoving getOtherSmartMoving(int entityId)
	{
		return factory.doGetOtherSmartMoving(entityId);
	}

	public static SmartMovingOther getOtherSmartMoving(EntityOtherPlayerMP entity)
	{
		return factory.doGetOtherSmartMoving(entity);
	}

	protected void doHandleMultiPlayerTick(Minecraft minecraft)
	{
		Iterator<?> others = minecraft.theWorld.playerEntities.iterator();
		while(others.hasNext())
		{
			Entity player = (Entity)others.next();
			if(player instanceof EntityOtherPlayerMP)
			{
				EntityOtherPlayerMP otherPlayer = (EntityOtherPlayerMP)player;
				SmartMovingOther moving = doGetOtherSmartMoving(otherPlayer);
				moving.spawnParticles(minecraft, otherPlayer.posX - otherPlayer.prevPosX, otherPlayer.posZ - otherPlayer.prevPosZ);
				moving.foundAlive = true;
			}
		}

		if(otherSmartMovings == null || otherSmartMovings.isEmpty())
			return;

		Iterator<Integer> entityIds = otherSmartMovings.keySet().iterator();
		while(entityIds.hasNext())
		{
			Integer entityId = entityIds.next();
			SmartMovingOther moving = otherSmartMovings.get(entityId);
			if(moving.foundAlive)
				moving.foundAlive = false;
			else
				entityIds.remove();
		}
	}

	protected SmartMoving doGetInstance(EntityPlayer entityPlayer)
	{
		if(entityPlayer instanceof EntityOtherPlayerMP)
			return doGetOtherSmartMoving(entityPlayer.getEntityId());
		else if(entityPlayer instanceof IEntityPlayerSP)
			return ((IEntityPlayerSP)entityPlayer).getMoving();
		return null;
	}

	protected SmartMoving doGetOtherSmartMoving(int entityId)
	{
		SmartMoving moving = tryGetOtherSmartMoving(entityId);
		if(moving == null)
		{
			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
			if(entity != null && entity instanceof EntityOtherPlayerMP)
				moving = addOtherSmartMoving((EntityOtherPlayerMP)entity);
		}
		return moving;
	}

	protected SmartMovingOther doGetOtherSmartMoving(EntityOtherPlayerMP entity)
	{
		SmartMovingOther moving = tryGetOtherSmartMoving(entity.getEntityId());
		if(moving == null)
			moving = addOtherSmartMoving(entity);
		return moving;
	}

	protected final SmartMovingOther tryGetOtherSmartMoving(int entityId)
	{
		if(otherSmartMovings == null)
			otherSmartMovings = new Hashtable<Integer, SmartMovingOther>();
		return otherSmartMovings.get(entityId);
	}

	protected final SmartMovingOther addOtherSmartMoving(EntityOtherPlayerMP entity)
	{
		SmartMovingOther moving = new SmartMovingOther(entity);
		otherSmartMovings.put(entity.getEntityId(), moving);
		return moving;
	}
}