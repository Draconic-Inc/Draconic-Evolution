package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.command.CommandKaboom;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

    public DraconicEvolution() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.construct();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.addListener(DraconicEvolution::registerCommands);

        DEConfig.load();
//
//        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(MODID, "fusion_crafting"), DraconicAPI.FUSION_RECIPE_TYPE = new IRecipeType<FusionRecipe>() {
//            public String toString() { return "draconicevolution:fusion_crafting"; }
//        });

        DraconicAPI.FUSION_RECIPE_TYPE = IRecipeType.register(MODID + ":fusion_crafting");
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

//    @SubscribeEvent
//    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {}

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandKaboom.register(event.getDispatcher());
    }

}