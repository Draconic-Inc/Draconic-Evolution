package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.config.ModConfigProcessor;
import com.brandon3055.brandonscore.config.ModFeatureParser;
import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.command.CommandUpgrade;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.DEWorldGenHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = DraconicEvolution.MODID, name = DraconicEvolution.MODNAME, version = DraconicEvolution.VERSION, canBeDeactivated = false, guiFactory = DraconicEvolution.GUI_FACTORY,  dependencies = DraconicEvolution.DEPENDENCIES)
public class DraconicEvolution {
	public static final String MODID	= "DraconicEvolution";
	public static final String MODNAME	= "Draconic Evolution";
	public static final String VERSION	= "${mod_version}";//todo Test
	public static final String PROXY_CLIENT = "com.brandon3055.draconicevolution.client.ClientProxy";
	public static final String PROXY_SERVER = "com.brandon3055.draconicevolution.CommonProxy";
	public static final String DEPENDENCIES = "after:NotEnoughItems;after:NotEnoughItems;after:ThermalExpansion;after:ThermalFoundation;required-after:BrandonsCore@["+ BrandonsCore.VERSION +",);";
	public static final String GUI_FACTORY 	= "com.brandon3055.draconicevolution.client.gui.DEGUIFactory";
	public static final String networkChannelName = "DEvolutionNC";
	//region Misc Fields
	public static CreativeTabs tabToolsWeapons = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "toolsAndWeapons", 0);
	public static CreativeTabs tabBlocksItems = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "blocksAndItems", 1);

	public static SimpleNetworkWrapper network;

	public static boolean debug = false;//todo

	public static Enchantment reaperEnchant;

	public static Configuration configuration;
	//endregion

	@Mod.Instance(DraconicEvolution.MODID)
	public static DraconicEvolution instance;

	@SidedProxy(clientSide = DraconicEvolution.PROXY_CLIENT, serverSide = DraconicEvolution.PROXY_SERVER)
	public static CommonProxy proxy;

	public static ModFeatureParser featureParser = new ModFeatureParser(MODID, new CreativeTabs[]{tabBlocksItems, tabToolsWeapons});
	public static ModConfigProcessor configProcessor = new ModConfigProcessor();

   // Still need to finish particle updating and ofcoarse rendering

	public DraconicEvolution()
	{
		LogHelper.info("Hello Minecraft!!!");
	}

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandUpgrade());
    }

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configuration = new Configuration(event.getSuggestedConfigurationFile());
		configProcessor.processConfig(DEConfig.class, configuration);

		featureParser.loadFeatures(DEFeatures.class);
		featureParser.loadFeatureConfig(configuration);
		featureParser.registerFeatures();

		OreHandler.initialize();
        FusionRecipes.registerRecipes();

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
}