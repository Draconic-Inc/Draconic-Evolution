package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.config.ModConfigProcessor;
import com.brandon3055.brandonscore.config.ModFeatureParser;
import com.brandon3055.brandonscore.handlers.FileHandler;
import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.command.CommandUpgrade;
import com.brandon3055.draconicevolution.items.tools.ToolStats;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.DEWorldGenHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.io.File;

@Mod(modid = DraconicEvolution.MODID, name = DraconicEvolution.MODNAME, version = DraconicEvolution.VERSION, canBeDeactivated = false, guiFactory = DraconicEvolution.GUI_FACTORY,  dependencies = DraconicEvolution.DEPENDENCIES)
public class DraconicEvolution {
	public static final String MODID	= "draconicevolution";
	public static final String MODNAME	= "Draconic Evolution";
	public static final String VERSION	= "${mod_version}";
    public static final String MOD_PREFIX = MODID.toLowerCase() + ":";
	public static final String PROXY_CLIENT = "com.brandon3055.draconicevolution.client.ClientProxy";
	public static final String PROXY_SERVER = "com.brandon3055.draconicevolution.CommonProxy";
	public static final String DEPENDENCIES = "after:NotEnoughItems;after:ThermalExpansion;after:ThermalFoundation;required-after:brandonscore@["+ BrandonsCore.VERSION +",);";
	public static final String GUI_FACTORY 	= "";//TODO com.brandon3055.draconicevolution.client.gui.DEGUIFactory";
	public static final String networkChannelName = "DEvolutionNC";
	//region Misc Fields
	public static CreativeTabs tabToolsWeapons = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "toolsAndWeapons", 0);
	public static CreativeTabs tabBlocksItems = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "blocksAndItems", 1);
	public static SimpleNetworkWrapper network;
	public static boolean debug = false;//todo
	public static Configuration configuration;
	//endregion

	@Mod.Instance(DraconicEvolution.MODID)
	public static DraconicEvolution instance;

	@SidedProxy(clientSide = DraconicEvolution.PROXY_CLIENT, serverSide = DraconicEvolution.PROXY_SERVER)
	public static CommonProxy proxy;

	public static ModFeatureParser featureParser = new ModFeatureParser(MODID, new CreativeTabs[]{tabBlocksItems, tabToolsWeapons});
	public static ModConfigProcessor configProcessor = new ModConfigProcessor();

	public DraconicEvolution()
	{
		LogHelper.info("Hello Minecraft!!!");
        LogHelper.info("Starting Draconic Evolution non-Beta! Deactivating Random Bug Generator!!!");
	}

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandUpgrade());
    }

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configuration = new Configuration(new File(FileHandler.brandon3055Folder, "DraconicEvolution.cfg"));
		configProcessor.initialize(configuration, DEConfig.comments, DEConfig.class, ToolStats.class);
        configProcessor.loadConfig();

		featureParser.loadFeatures(DEFeatures.class);
		featureParser.loadFeatureConfig(configuration);
		featureParser.registerFeatures();
		DEConfig.init();

		OreHandler.initialize();

		proxy.preInit(event);
        proxy.registerParticles();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		DEWorldGenHandler.initialize();
		GuiHandler.initialize();

		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

	//FMLInterModComms.sendMessage("DraconicEvolution", "addChestRecipe:item.coal", new ItemStack(Items.diamond, 2));
	@Mod.EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			LogHelper.info(m.key);
			if (m.isItemStackMessage() && m.key.contains("addChestRecipe:")) {
				String s = m.key.substring(m.key.indexOf("addChestRecipe:") + 15);
//				OreDoublingRegistry.resultOverrides.put(s, m.getItemStackValue());			//TODO Update ore doubling registry
				LogHelper.info("Added Chest recipe override: " + s + " to " + m.getItemStackValue());
			}
		}
	}

//	@Mod.EventHandler
//	public void remapEvent(FMLMissingMappingsEvent event) {
//		for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
//			if (mapping.name.startsWith(DraconicEvolution.MOD_PREFIX)) {
//				if (mapping.type == GameRegistry.Type.BLOCK) {
//					if (mapping.name.equals(DraconicEvolution.MOD_PREFIX + "creativeRFSource")) {
//						mapping.remap(DEFeatures.creativeRFSource);
//						continue;
//					}
//					Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapping.name)));
//
//					if (newBlock == Blocks.AIR) {
//						LogHelper.bigError("Could not remap block! " + mapping.name);
//					}
//
//					mapping.remap(newBlock);
//				}
//				else if (mapping.type == GameRegistry.Type.ITEM) {
//					if (mapping.name.equals(DraconicEvolution.MOD_PREFIX + "creativeRFSource")) {
//						mapping.remap(Item.getItemFromBlock(DEFeatures.creativeRFSource));
//						continue;
//					}
//					Item newItem = Item.REGISTRY.getObject(new ResourceLocation(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapping.name)));
//
//					if (newItem == null) {
//						LogHelper.bigError("Could not remap item! " + mapping.name);
//						continue;
//					}
//
//					mapping.remap(newItem);
//				}
//			}
//		}
//	}
}