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

import java.io.*;
import java.util.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.internal.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.smart.moving.config.*;
import net.smart.properties.*;

public class SmartMovingServer
{
	public static final float SmallSizeItemGrabHeight = 0.25F;

	protected final IEntityPlayerMP mp;
	private boolean resetFallDistance = false;
	private boolean resetTicksForFloatKick = false;
	private boolean initialized = false;
	private boolean withinOnLivingUpdate = false;

	public boolean crawlingInitialized;
	public int crawlingCooldown;
	public boolean isCrawling;
	public boolean isSmall;
	public float hunger;

	private int disableAddExhaustionDepth;
	private boolean disableAddExhaustion;
	private boolean isSneakButtonPressed;
	private Boolean forceIsSneaking;

	public SmartMovingServer(IEntityPlayerMP mp, boolean onTheFly)
	{
		this.mp = mp;
		if (onTheFly)
			initialize(true);
	}

	public void initialize(boolean alwaysSendMessage)
	{
		if(Options._globalConfig.value)
			SmartMovingPacketStream.sendConfigContent(mp, optionsHandler.writeToProperties(), null);
		else if(Options._serverConfig.value)
			SmartMovingPacketStream.sendConfigContent(mp, optionsHandler.writeToProperties(mp, false), null);
		else if(alwaysSendMessage)
			SmartMovingPacketStream.sendConfigContent(mp, Options.enabled ? new String[0] : null, null);
		initialized = true;
	}

	public void processStatePacket(FMLProxyPacket packet, long state)
	{
		if(!initialized)
			initialize(false);

		boolean isCrawling = ((state >>> 13) & 1) != 0;
		setCrawling(isCrawling);

		boolean isSmall = ((state >>> 15) & 1) != 0;
		setSmall(isSmall);

		boolean isClimbing = ((state >>> 14) & 1) != 0;
		boolean isCrawlClimbing = ((state >>> 12) & 1) != 0;
		boolean isCeilingClimbing = ((state >>> 18) & 1) != 0;

		boolean isWallJumping = ((state >>> 31) & 1) != 0;

		isSneakButtonPressed = ((state >>> 33) & 1) != 0;

		resetFallDistance = isClimbing || isCrawlClimbing || isCeilingClimbing || isWallJumping;
		resetTicksForFloatKick = isClimbing || isCrawlClimbing || isCeilingClimbing;
		mp.sendPacketToTrackedPlayers(packet);
	}

	public void processConfigPacket(String clientConfigurationVersion)
	{
		boolean warn = true;
		String type = "unknown";
		if(clientConfigurationVersion != null)
			for(int i = 0; i < SmartMovingConfig._all.length; i++)
				if(clientConfigurationVersion.equals(SmartMovingConfig._all[i]))
				{
					warn = i > 0;
					type = warn ? "outdated" : "matching";
					break;
				}

		String message = "Smart Moving player \"" + mp.getUsername() + "\" connected with " + type + " configuration system";
		if(clientConfigurationVersion != null)
			message += " version \"" + clientConfigurationVersion + "\"";

		if(warn)
			FMLLog.warning(message);
		else
			FMLLog.info(message);
	}

	public void processConfigChangePacket(String localUserName)
	{
		if(!Options._globalConfig.value)
		{
			toggleSingleConfig();
			return;
		}

		String username = mp.getUsername();

		if(localUserName == username)
		{
			toggleConfig();
			return;
		}

		String[] rightPlayerNames = Options._usersWithChangeConfigRights.value;
		for(int i=0; i<rightPlayerNames.length; i++)
			if(rightPlayerNames[i].equals(username))
			{
				toggleConfig();
				return;
			}

		SmartMovingPacketStream.sendConfigChange(mp);
	}

	public void processSpeedChangePacket(int difference, String localUserName)
	{
		if(!Options._globalConfig.value)
		{
			changeSingleSpeed(difference);
			return;
		}

		if(!hasRight(localUserName, Options._usersWithChangeSpeedRights))
			SmartMovingPacketStream.sendSpeedChange(mp, 0, null);
		else
			changeSpeed(difference);
	}

	public void processHungerChangePacket(float hunger)
	{
		this.hunger = hunger;
	}

	public void processSoundPacket(String soundId, float volume, float pitch)
	{
		mp.localPlaySound(soundId, volume, pitch);
	}

	private boolean hasRight(String localUserName, Property<String[]> rights)
	{
		String username = mp.getUsername();

		if(localUserName == username)
			return true;

		String[] rightPlayerNames = rights.value;
		for(int i=0; i<rightPlayerNames.length; i++)
			if(rightPlayerNames[i].equals(username))
				return true;

		return false;
	}

	public void toggleSingleConfig()
	{
		SmartMovingPacketStream.sendConfigContent(mp, optionsHandler.writeToProperties(mp, true), mp.getUsername());
	}

	public void toggleConfig()
	{
		optionsHandler.toggle(mp);
		String[] config = optionsHandler.writeToProperties();
		IEntityPlayerMP[] players = mp.getAllPlayers();
		for(int n=0; n<players.length; n++)
			SmartMovingPacketStream.sendConfigContent(players[n], config, mp.getUsername());
	}

	public void changeSingleSpeed(int difference)
	{
		optionsHandler.changeSingleSpeed(mp, difference);
		SmartMovingPacketStream.sendSpeedChange(mp, difference, mp.getUsername());
	}

	public void changeSpeed(int difference)
	{
		optionsHandler.changeSpeed(difference, mp);
		IEntityPlayerMP[] players = mp.getAllPlayers();
		for(int n=0; n<players.length; n++)
			SmartMovingPacketStream.sendSpeedChange(players[n], difference, mp.getUsername());
	}

	public void afterOnUpdate()
	{
		if(resetFallDistance)
			mp.resetFallDistance();
		if(resetTicksForFloatKick)
			mp.resetTicksForFloatKick();
	}

	public static void initialize(File optionsPath, int gameType, SmartMovingConfig config)
	{
		Options = config;
		optionsHandler = new SmartMovingServerOptions(Options, optionsPath, gameType);
		FMLLog.getLogger().info(SmartMovingInfo.ModComMessage);
	}

	public void setCrawling(boolean crawling)
	{
		if(!crawling && isCrawling)
			crawlingCooldown = 10;
		isCrawling = crawling;
	}

	public void setSmall(boolean isSmall)
	{
		mp.setHeight(isSmall ? 0.8F : 1.8F);
		this.isSmall = isSmall;
	}

	@SuppressWarnings("unused")
	public void afterSetPosition(double d, double d1, double d2)
	{
		if(!crawlingInitialized)
			mp.setMaxY(mp.getMinY() + mp.getHeight() - 1);
	}

	public void beforeIsPlayerSleeping()
	{
		if(!crawlingInitialized)
		{
			mp.setMaxY(mp.getMinY() + mp.getHeight());
			crawlingInitialized = true;
		}
	}

	public void beforeOnUpdate()
	{
		if (crawlingCooldown > 0)
			crawlingCooldown --;
	}

	public void beforeOnLivingUpdate()
	{
		withinOnLivingUpdate = true;
	}

	public void afterOnLivingUpdate()
	{
		withinOnLivingUpdate = false;

		if(!isSmall)
			return;

		if (mp.doGetHealth() <= 0)
			return;

		double offset = SmallSizeItemGrabHeight;
		AxisAlignedBB box = mp.expandBox(mp.getBox(), 1, offset, 1);

		List<?> offsetEntities = mp.getEntitiesExcludingPlayer(box);
		if (offsetEntities != null && offsetEntities.size() > 0)
		{
			Object[] offsetEntityArray = offsetEntities.toArray();

			box = mp.expandBox(box, 0, -offset, 0);
			List<?> standardEntities = mp.getEntitiesExcludingPlayer(box);

			for (int i=0; i<offsetEntityArray.length; i++)
			{
				Entity offsetEntity = (Entity)offsetEntityArray[i];
				if(standardEntities != null && standardEntities.contains(offsetEntity))
					continue;

				if (!mp.isDeadEntity(offsetEntity))
					mp.onCollideWithPlayer(offsetEntity);
			}
		}
	}

	public boolean isEntityInsideOpaqueBlock()
	{
		if(crawlingCooldown > 0)
			return false;

		return mp.localIsEntityInsideOpaqueBlock();
	}

	public void addMovementStat(double var1, double var3, double var5)
	{
		beforeAddMovingHungerBatch();
		mp.localAddMovementStat(var1, var3, var5);
		if(disableAddExhaustion && hunger != 0 && !withinOnLivingUpdate)
			mp.localAddExhaustion(hunger);
		afterAddMovingHungerBatch();
	}

	public void beforeAddMovingHungerBatch()
	{
		disableAddExhaustionDepth++;
		if(hunger != -1)
			disableAddExhaustion = true;
	}

	public void addExhaustion(float exhaustion)
	{
		if(!disableAddExhaustion)
			mp.localAddExhaustion(exhaustion);
	}

	public void afterAddMovingHungerBatch()
	{
		disableAddExhaustionDepth--;
		if(disableAddExhaustionDepth == 0)
			disableAddExhaustion = false;
	}

	public boolean isSneaking()
	{
		if(forceIsSneaking != null)
			return forceIsSneaking;

		return mp.localIsSneaking();
	}

	public void beforeActivateBlockOrUseItem()
	{
		forceIsSneaking = isSneakButtonPressed;
	}

	public void afterActivateBlockOrUseItem()
	{
		forceIsSneaking = null;
	}

	public static SmartMovingConfig Options = null;
	private static SmartMovingServerOptions optionsHandler = null;
}