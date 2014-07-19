package com.brandon3055.draconicevolution.common.core.proxy;

import com.brandon3055.draconicevolution.common.core.handler.MinecraftForgeEventHandler;
import com.brandon3055.draconicevolution.common.core.network.*;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.tileentities.*;
import com.brandon3055.draconicevolution.common.world.DraconicWorldGenerator;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.core.handler.CraftingHandler;
import com.brandon3055.draconicevolution.common.core.handler.FMLEventHandler;
import com.brandon3055.draconicevolution.common.items.ModItems;

public class CommonProxy {
	private final static boolean debug = DraconicEvolution.debug;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		ModBlocks.init();
		ModItems.init();
		GameRegistry.registerWorldGenerator(new DraconicWorldGenerator(), 1);
		registerTileEntities();
	}

	public void init(FMLInitializationEvent event)
	{
		CraftingHandler.init();
		registerEventListeners();
		registerGuiHandeler();
		registerWorldGen();
		DraconicEvolution.channelHandler.initialise();
		registerPackets();
		registerEntitys();
	}

	public void postInit(FMLPostInitializationEvent event)
	{
		DraconicEvolution.channelHandler.postInitialise();
	}

	public void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileWeatherController.class, "TileWeatherController");
		GameRegistry.registerTileEntity(TileSunDial.class, "TileSunDial");
		GameRegistry.registerTileEntity(TileGrinder.class, "TileGrinder");
		GameRegistry.registerTileEntity(TilePotentiometer.class, "TilePotentiometer");
		GameRegistry.registerTileEntity(TileParticleGenerator.class, "TileParticleGenerator");
		GameRegistry.registerTileEntity(TilePlayerDetector.class, "TilePlayerDetector");
		GameRegistry.registerTileEntity(TilePlayerDetectorAdvanced.class, "TilePlayerDetectorAdvanced");
		GameRegistry.registerTileEntity(TileEnergyInfuser.class, "TileEnergyInfuser");
		GameRegistry.registerTileEntity(TileCustomSpawner.class, "TileCustomSpawner");
		if(DraconicEvolution.debug)
			GameRegistry.registerTileEntity(TileTestBlock.class, "TileTestBlock");
	}

	public void registerEventListeners()
	{
		MinecraftForge.EVENT_BUS.register(new MinecraftForgeEventHandler());
		FMLCommonHandler.instance().bus().register(new FMLEventHandler());
	}

	public void registerPackets()
	{
		if(debug){
			System.out.println("[DEBUG]CommonProxy: registerPackets");
			DraconicEvolution.channelHandler.registerPacket(ExamplePacket.class);
		}
		
		DraconicEvolution.channelHandler.registerPacket(ButtonPacket.class);
		DraconicEvolution.channelHandler.registerPacket(TeleporterPacket.class);
		DraconicEvolution.channelHandler.registerPacket(TeleporterStringPacket.class);
		DraconicEvolution.channelHandler.registerPacket(ParticleGenPacket.class);
		DraconicEvolution.channelHandler.registerPacket(PlayerDetectorStringPacket.class);
		DraconicEvolution.channelHandler.registerPacket(PlayerDetectorButtonPacket.class);
	}

	public void registerGuiHandeler()
	{
		new GuiHandler();
	}

	public void registerWorldGen()
	{
		GameRegistry.registerWorldGenerator(new DraconicWorldGenerator(), 1);
	}

	public void registerEntitys(){
		int dragonID = EntityRegistry.findGlobalUniqueEntityId();
		int cyan = (0 << 16) + (255 << 8) + 255;
		EntityRegistry.registerGlobalEntityID(EntityCustomDragon.class, "enderDragon", dragonID, cyan, 0);
	}
}
