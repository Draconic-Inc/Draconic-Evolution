package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.client.ClientProxy;

import com.brandon3055.draconicevolution.utils.LogHelper;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Mod(DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

    public DraconicEvolution() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.construct();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
//        ClientPlayerNetworkEvent.
//        ClientPlayerNetworkEvent.LoggedInEvent
//        DeferredRegister


        DEConfig.load();
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        proxy.serverSetup(event);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }


    //    @Mod.EventHandler
//    public void serverStart(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandUpgrade());
//        event.registerServerCommand(new CommandReloadFusion());
//    }
//
//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) {
//        ModFeatureParser.registerModFeatures(MODID);
//        proxy.preInit(event);
//        proxy.registerParticles();
//        OreHandler.initialize();
//    }
//
//    @Mod.EventHandler
//    public void init(FMLInitializationEvent event) {
//        DEWorldGenHandler.initialize();
//        GuiHandler.initialize();
//        proxy.init(event);
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        proxy.postInit(event);
//    }

//    /**
//     * To register an itemstack to be doubled by the DE chest the message should be as followes
//     * <p>
//     * FMLInterModComms.sendMessage("draconicevolution", "addChestRecipe:minecraft:coal", new ItemStack(Items.diamond, 2));
//     * <p>
//     * The input stack format is similar to the format used by the give command. Here are some examples.
//     * minecraft:stone                <br>
//     * minecraft:stone,64             <br>
//     * minecraft:stone,64,3           <br>
//     * minecraft:stone,64,3,{NBT}     <br>
//     */
//    @Mod.EventHandler
//    public void processMessage(FMLInterModComms.IMCEvent event) {
//        for (FMLInterModComms.IMCMessage m : event.getMessages()) {
//            LogHelper.info(m.key);
//            if (m.isItemStackMessage() && m.key.startsWith("addChestRecipe:")) {
//                String s = m.key.replace("addChestRecipe:", "");
//                StackReference reference = StackReference.fromString(s);
//                if (reference == null) {
//                    LogHelper.error("IMC error. Mod: " + m.getSender() + " tried to register a smelting override but the specified input stack was invalid! Input: " + s);
//                    continue;
//                }
//                ItemStack stack = reference.createStack();
//                if (stack.isEmpty()) {
//                    LogHelper.error("IMC error. Mod: " + m.getSender() + " tried to register a smelting override but the specified input stack could not be found! Input: " + s);
//                    continue;
//                }
//
//                OreDoublingRegistry.registerResult(stack, m.getItemStackValue());
//                LogHelper.info("Added Chest recipe override: " + stack + " -> " + m.getItemStackValue());
//            }
//        }
//    }

}