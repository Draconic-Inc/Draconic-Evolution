package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.command.CommandUpgrade;
import com.brandon3055.draconicevolution.lib.OreDoublingRegistry;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.DEWorldGenHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = DraconicEvolution.MODID, name = DraconicEvolution.MODNAME, version = DraconicEvolution.VERSION, guiFactory = DraconicEvolution.GUI_FACTORY, dependencies = DraconicEvolution.DEPENDENCIES)
public class DraconicEvolution {
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";
    public static final String VERSION = "${mod_version}";
    public static final String MOD_PREFIX = MODID.toLowerCase() + ":";
    public static final String PROXY_CLIENT = "com.brandon3055.draconicevolution.client.ClientProxy";
    public static final String PROXY_SERVER = "com.brandon3055.draconicevolution.CommonProxy";
    public static final String DEPENDENCIES = "before:thermalexpansion;after:thermalfoundation;required-after:brandonscore@[" + BrandonsCore.VERSION + ",);";
    public static final String GUI_FACTORY = "com.brandon3055.draconicevolution.DEGuiFactory";
    public static final String networkChannelName = "DEvolutionNC";
    //region Misc Fields
    public static CreativeTabs tabToolsWeapons = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "toolsAndWeapons", 0);
    public static CreativeTabs tabBlocksItems = new DETab(CreativeTabs.getNextID(), DraconicEvolution.MODID, "blocksAndItems", 1);
    public static SimpleNetworkWrapper network;
    //endregion

    @Mod.Instance(DraconicEvolution.MODID)
    public static DraconicEvolution instance;

    @SidedProxy(clientSide = DraconicEvolution.PROXY_CLIENT, serverSide = DraconicEvolution.PROXY_SERVER)
    public static CommonProxy proxy;

    public DraconicEvolution() {
        LogHelper.info("Hello Minecraft!!!");
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandUpgrade());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
//        configuration = new Configuration(new File(FileHandler.brandon3055Folder, "DraconicEvolution.cfg"));
        ModFeatureParser.registerModFeatures(MODID);
        proxy.preInit(event);
        proxy.registerParticles();
        OreHandler.initialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        DEWorldGenHandler.initialize();
        GuiHandler.initialize();

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    /**
     * To register an itemstack to be doubled by the DE chest the message should be as followes
     * <p>
     * FMLInterModComms.sendMessage("draconicevolution", "addChestRecipe:minecraft:coal", new ItemStack(Items.diamond, 2));
     * <p>
     * The input stack format is similar to the format used by the give command. Here are some examples.
     * minecraft:stone                <br>
     * minecraft:stone,64             <br>
     * minecraft:stone,64,3           <br>
     * minecraft:stone,64,3,{NBT}     <br>
     */
    @Mod.EventHandler
    public void processMessage(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage m : event.getMessages()) {
            LogHelper.info(m.key);
            if (m.isItemStackMessage() && m.key.startsWith("addChestRecipe:")) {
                String s = m.key.replace("addChestRecipe:", "");
                StackReference reference = StackReference.fromString(s);
                if (reference == null) {
                    LogHelper.error("IMC error. Mod: " + m.getSender() + " tried to register a smelting override but the specified input stack was invalid! Input: " + s);
                    continue;
                }
                ItemStack stack = reference.createStack();
                if (stack.isEmpty()) {
                    LogHelper.error("IMC error. Mod: " + m.getSender() + " tried to register a smelting override but the specified input stack could not be found! Input: " + s);
                    continue;
                }

                OreDoublingRegistry.registerResult(stack, m.getItemStackValue());
                LogHelper.info("Added Chest recipe override: " + stack + " -> " + m.getItemStackValue());
            }
        }
    }

}