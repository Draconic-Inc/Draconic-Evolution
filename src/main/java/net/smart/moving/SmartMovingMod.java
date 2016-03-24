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
import java.lang.reflect.*;
import java.util.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.network.FMLNetworkEvent.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.smart.core.*;
import net.smart.moving.config.*;
import net.smart.utilities.*;

@Mod(modid = "SmartMoving", name = "Smart Moving", version = "15.6", dependencies = "required-after:PlayerAPI@[1.3,)")
public class SmartMovingMod
{
	protected static String ModComVersion = "2.3.1";

	private final boolean isClient;

	private boolean hasRenderer = false;

	public SmartMovingMod()
	{
		isClient = FMLCommonHandler.instance().getSide().isClient();
	}

	@EventHandler
	@SuppressWarnings("unused")
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.newEventDrivenChannel(SmartMovingPacketStream.Id).register(this);

		if(isClient)
		{
			hasRenderer = Loader.isModLoaded("RenderPlayerAPI");

			net.smart.moving.playerapi.SmartMoving.register();

			if(hasRenderer)
			{
				Class<?> type = Reflect.LoadClass(SmartMovingMod.class, new Name("net.smart.moving.render.playerapi.SmartMoving"), true);
				Method method = Reflect.GetMethod(type, new Name("register"));
				Reflect.Invoke(method, null);
			}
			else
				net.smart.render.SmartRenderMod.doNotAddRenderer();

			SmartMovingServerComm.localUserNameProvider = new LocalUserNameProvider();
			if(!hasRenderer)
				SmartMovingContext.registerRenderers();

			registerGameTicks();

			net.smart.moving.playerapi.SmartMovingFactory.initialize();

			checkForPresentModsAndInitializeOptions();

			SmartMovingContext.initialize();
		}
		else
			SmartMovingServer.initialize(new File("."), FMLCommonHandler.instance().getMinecraftServerInstance().getGameType().getID(), new SmartMovingConfig());

		SmartCoreEventHandler.Add(new SmartMovingCoreEventHandler());
	}

	@EventHandler
	@SuppressWarnings("unused")
	public void postInit(FMLPostInitializationEvent event)
	{
		if(!isClient)
			net.smart.moving.playerapi.SmartMovingServerPlayerBase.registerPlayerBase();
	}

	@SubscribeEvent
	@SuppressWarnings({ "static-method", "unused" })
	public void tickStart(ClientTickEvent event)
	{
		SmartMovingContext.onTickInGame();
	}

	@SubscribeEvent
	@SuppressWarnings("static-method")
	public void onPacketData(ServerCustomPacketEvent event)
	{
		SmartMovingPacketStream.receivePacket(event.packet, SmartMovingServerComm.instance, net.smart.moving.playerapi.SmartMovingServerPlayerBase.getPlayerBase(((NetHandlerPlayServer)event.handler).playerEntity));
	}

	@SubscribeEvent
	@SuppressWarnings("static-method")
	public void onPacketData(ClientCustomPacketEvent event)
	{
		SmartMovingPacketStream.receivePacket(event.packet, SmartMovingComm.instance, null);
	}

	public void registerGameTicks()
	{
		FMLCommonHandler.instance().bus().register(this);
	}

	@SuppressWarnings("static-method")
	public Object getInstance(EntityPlayer entityPlayer)
	{
		return SmartMovingFactory.getInstance(entityPlayer);
	}

	@SuppressWarnings("static-method")
	public Object getClient()
	{
		return SmartMovingContext.Client;
	}

	@SuppressWarnings("static-method")
	public void checkForPresentModsAndInitializeOptions()
	{
		List<ModContainer> modList = Loader.instance().getActiveModList();
		boolean hasRedPowerWiring = false;
		boolean hasBuildCraftTransport = false;
		boolean hasFiniteLiquid = false;
		boolean hasBetterThanWolves = false;
		boolean hasSinglePlayerCommands = false;
		boolean hasRopesPlus = false;
		boolean hasASGrapplingHook = false;
		boolean hasBetterMisc = false;

		for(int i = 0; i < modList.size(); i++)
		{
			ModContainer mod = modList.get(i);
			String name = mod.getName();

			if(name.contains("RedPowerWiring"))
				hasRedPowerWiring = true;
			else if(name.contains("BuildCraftTransport"))
				hasBuildCraftTransport = true;
			else if(name.contains("Liquid"))
				hasFiniteLiquid = true;
			else if(name.contains("FCBetterThanWolves"))
				hasBetterThanWolves = true;
			else if(name.contains("SinglePlayerCommands"))
				hasSinglePlayerCommands = true;
			else if(name.contains("ASGrapplingHook"))
				hasASGrapplingHook = true;
			else if(name.contains("BetterMisc"))
				hasBetterMisc = true;
		}

		hasRopesPlus = Reflect.CheckClasses(SmartMovingMod.class, SmartMovingInstall.RopesPlusCore);

		SmartMovingOptions.initialize(hasRedPowerWiring, hasBuildCraftTransport, hasFiniteLiquid, hasBetterThanWolves, hasSinglePlayerCommands, hasRopesPlus, hasASGrapplingHook, hasBetterMisc);
	}
}