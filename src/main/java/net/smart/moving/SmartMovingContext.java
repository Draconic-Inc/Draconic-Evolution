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

import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.*;

import net.minecraft.client.*;
import net.minecraft.server.*;

import net.smart.moving.config.*;
import net.smart.render.*;

public abstract class SmartMovingContext extends SmartRenderContext
{
	public static final float ClimbPullMotion = 0.3F;

	public static final double FastUpMotion = 0.2D;
	public static final double MediumUpMotion = 0.14D;
	public static final double SlowUpMotion = 0.1D;
	public static final double HoldMotion = 0.08D;
	public static final double SinkDownMotion = 0.05D;
	public static final double ClimbDownMotion = 0.01D;
	public static final double CatchCrawlGapMotion = 0.17D;

	public static final float SwimCrawlWaterMaxBorder = 1F;
	public static final float SwimCrawlWaterTopBorder = 0.65F;
	public static final float SwimCrawlWaterMediumBorder = 0.6F;
	public static final float SwimCrawlWaterBottomBorder = 0.55F;

	public static final float HorizontalGroundDamping = 0.546F;
	public static final float HorizontalAirDamping = 0.91F;
	public static final float HorizontalAirodynamicDamping = 0.999F;

	public static final float SwimSoundDistance = 1F / 0.7F;
	public static final float SlideToHeadJumpingFallDistance = 0.05F;


	public static final SmartMovingClient Client = new SmartMovingClient();
	public static final SmartMovingOptions Options = new SmartMovingOptions();
	public static final SmartMovingServerConfig ServerConfig = new SmartMovingServerConfig();
	public static SmartMovingClientConfig Config = Options;


	private static boolean wasInitialized;

	public static void onTickInGame()
	{
		Minecraft minecraft = Minecraft.getMinecraft();

		if(minecraft.theWorld != null && minecraft.theWorld.isRemote)
			SmartMovingFactory.handleMultiPlayerTick(minecraft);

		Options.initializeForGameIfNeccessary();

		initializeServerIfNecessary();
	}

	public static void initialize()
	{
		if(!wasInitialized)
			net.smart.render.statistics.SmartStatisticsContext.setCalculateHorizontalStats(true);

		ClientRegistry.registerKeyBinding(Options.keyBindGrab);
		ClientRegistry.registerKeyBinding(Options.keyBindConfigToggle);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedIncrease);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedDecrease);

		if(wasInitialized)
			return;

		wasInitialized = true;

		System.out.println(SmartMovingInfo.ModComMessage);
		FMLLog.getLogger().info(SmartMovingInfo.ModComMessage);
	}

	public static void initializeServerIfNecessary()
	{
		MinecraftServer currentMinecraftServer = MinecraftServer.getServer();
		if(currentMinecraftServer != null && currentMinecraftServer != lastMinecraftServer)
			SmartMovingServer.initialize(SmartMovingOptions.optionsPath, currentMinecraftServer.getGameType().getID(), Options);
		lastMinecraftServer = currentMinecraftServer;
	}

	public static void registerRenderers()
	{
		registerRenderers(net.smart.moving.render.RenderPlayer.class);
	}

	private static MinecraftServer lastMinecraftServer = null;
}