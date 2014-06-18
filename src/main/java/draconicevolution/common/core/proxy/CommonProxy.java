package draconicevolution.common.core.proxy;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import draconicevolution.client.interfaces.GuiHandler;
import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.blocks.ModBlocks;
import draconicevolution.common.core.handler.ConfigHandler;
import draconicevolution.common.core.handler.CraftingHandler;
import draconicevolution.common.core.handler.FMLEventHandler;
import draconicevolution.common.core.handler.ModEventHandler;
import draconicevolution.common.core.network.ButtonPacket;
import draconicevolution.common.core.network.ExamplePacket;
import draconicevolution.common.core.network.ParticleGenPacket;
import draconicevolution.common.core.network.PlayerDetectorButtonPacket;
import draconicevolution.common.core.network.PlayerDetectorStringPacket;
import draconicevolution.common.core.network.TeleporterPacket;
import draconicevolution.common.core.network.TeleporterStringPacket;
import draconicevolution.common.items.ModItems;
import draconicevolution.common.tileentities.TileGrinder;
import draconicevolution.common.tileentities.TileParticleGenerator;
import draconicevolution.common.tileentities.TilePlayerDetector;
import draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;
import draconicevolution.common.tileentities.TilePotentiometer;
import draconicevolution.common.tileentities.TileSunDial;
import draconicevolution.common.tileentities.TileWeatherController;
import draconicevolution.common.world.TolkienWorldGenerator;

public class CommonProxy {
	private final static boolean debug = DraconicEvolution.debug;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		ModBlocks.init();
		ModItems.init();
		GameRegistry.registerWorldGenerator(new TolkienWorldGenerator(), 1);
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
	}

	public void registerEventListeners()
	{
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
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
		GameRegistry.registerWorldGenerator(new TolkienWorldGenerator(), 1);
	}
}
