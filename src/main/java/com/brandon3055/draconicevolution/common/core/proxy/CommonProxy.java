package com.brandon3055.draconicevolution.common.core.proxy;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.core.handler.CraftingHandler;
import com.brandon3055.draconicevolution.common.core.handler.FMLEventHandler;
import com.brandon3055.draconicevolution.common.core.handler.MinecraftForgeEventHandler;
import com.brandon3055.draconicevolution.common.core.network.*;
import com.brandon3055.draconicevolution.common.core.utills.Utills;
import com.brandon3055.draconicevolution.common.entity.*;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.*;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnderResurrection;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileInvisibleMultiblock;
import com.brandon3055.draconicevolution.common.world.DraconicWorldGenerator;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	//private final static boolean debug = DraconicEvolution.debug;

	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		registerEventListeners();
		ModBlocks.init();
		ModItems.init();
		GameRegistry.registerWorldGenerator(new DraconicWorldGenerator(), 1);
		registerTileEntities();
		initializeNetwork();
	}

	public void init(FMLInitializationEvent event) {
		CraftingHandler.init();
		registerGuiHandeler();
		registerWorldGen();
		registerEntitys();
		DETab.initialize();
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void initializeNetwork(){
		DraconicEvolution.network = NetworkRegistry.INSTANCE.newSimpleChannel(DraconicEvolution.networkChannelName);
		DraconicEvolution.network.registerMessage(ButtonPacket.Handler.class, ButtonPacket.class, 0, Side.SERVER);
		DraconicEvolution.network.registerMessage(ParticleGenPacket.Handler.class, ParticleGenPacket.class, 1, Side.SERVER);
		DraconicEvolution.network.registerMessage(PlacedItemPacket.Handler.class, PlacedItemPacket.class, 2, Side.SERVER);
		DraconicEvolution.network.registerMessage(PlayerDetectorButtonPacket.Handler.class, PlayerDetectorButtonPacket.class, 3, Side.SERVER);
		DraconicEvolution.network.registerMessage(PlayerDetectorStringPacket.Handler.class, PlayerDetectorStringPacket.class, 4, Side.SERVER);
		DraconicEvolution.network.registerMessage(TeleporterPacket.Handler.class, TeleporterPacket.class, 5, Side.SERVER);
	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileWeatherController.class, References.RESOURCESPREFIX + "TileWeatherController");
		GameRegistry.registerTileEntity(TileSunDial.class, References.RESOURCESPREFIX + "TileSunDial");
		GameRegistry.registerTileEntity(TileGrinder.class, References.RESOURCESPREFIX + "TileGrinder");
		GameRegistry.registerTileEntity(TilePotentiometer.class, References.RESOURCESPREFIX + "TilePotentiometer");
		GameRegistry.registerTileEntity(TileParticleGenerator.class, References.RESOURCESPREFIX + "TileParticleGenerator");
		GameRegistry.registerTileEntity(TilePlayerDetector.class, References.RESOURCESPREFIX + "TilePlayerDetector");
		GameRegistry.registerTileEntity(TilePlayerDetectorAdvanced.class, References.RESOURCESPREFIX + "TilePlayerDetectorAdvanced");
		GameRegistry.registerTileEntity(TileEnergyInfuser.class, References.RESOURCESPREFIX + "TileEnergyInfuser");
		GameRegistry.registerTileEntity(TileCustomSpawner.class, References.RESOURCESPREFIX + "TileCustomSpawner");
		GameRegistry.registerTileEntity(TileGenerator.class, References.RESOURCESPREFIX + "TileGenerator");
		GameRegistry.registerTileEntity(TileEnergyStorageCore.class, References.RESOURCESPREFIX + "TileEnergyStorageCore");
		GameRegistry.registerTileEntity(TileInvisibleMultiblock.class, References.RESOURCESPREFIX + "TileInvisibleMultiblock");
		GameRegistry.registerTileEntity(TileEnergyPylon.class, References.RESOURCESPREFIX + "TileEnergyPylon");
		GameRegistry.registerTileEntity(Utills.TileBlockChanger.class, References.RESOURCESPREFIX + "TileBlockChanger");
		GameRegistry.registerTileEntity(TileEnderResurrection.class, References.RESOURCESPREFIX + "TileEnderResurrection");
		GameRegistry.registerTileEntity(TilePlacedItem.class, References.RESOURCESPREFIX + "TilePlacedItem");
		GameRegistry.registerTileEntity(TileCKeyStone.class, References.RESOURCESPREFIX + "TileCKeyStone");
		GameRegistry.registerTileEntity(TileDissEnchanter.class, References.RESOURCESPREFIX + "TileDissEnchanter");
		GameRegistry.registerTileEntity(TileTeleporterStand.class, References.RESOURCESPREFIX + "TileTeleporterStand");
		GameRegistry.registerTileEntity(TileDraconiumChest.class, References.RESOURCESPREFIX + "TileDraconiumChest");
		if (DraconicEvolution.debug)
		{
			GameRegistry.registerTileEntity(TileTestBlock.class, References.RESOURCESPREFIX + "TileTestBlock");
			GameRegistry.registerTileEntity(TileContainerTemplate.class, References.RESOURCESPREFIX + "TileContainerTemplate");
		}
	}

	public void registerEventListeners() {
		MinecraftForge.EVENT_BUS.register(new MinecraftForgeEventHandler());
		FMLCommonHandler.instance().bus().register(new FMLEventHandler());
	}

	public void registerGuiHandeler() {
		new GuiHandler();
	}

	public void registerWorldGen() {
		GameRegistry.registerWorldGenerator(new DraconicWorldGenerator(), 0);
	}

	public void registerEntitys() {
		EntityRegistry.registerModEntity(EntityCustomDragon.class, "EnderDragon", 0, DraconicEvolution.instance, 500, 3, true);
		EntityRegistry.registerModEntity(EntityPersistentItem.class, "Persistent Item", 1, DraconicEvolution.instance, 32, 5, true);
		EntityRegistry.registerModEntity(EntityDraconicArrow.class, "Arrow", 2, DraconicEvolution.instance, 32, 5, true);
		EntityRegistry.registerModEntity(EntityEnderArrow.class, "Ender Arrow", 2, DraconicEvolution.instance, 32, 5, true);
		EntityRegistry.registerModEntity(EntityChaosDrill.class, "Chaos Drill", 3, DraconicEvolution.instance, 10, 5, false);
	}
}
