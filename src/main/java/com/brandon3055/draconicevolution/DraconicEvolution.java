package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.command.CommandKaboom;
import com.brandon3055.draconicevolution.command.CommandMakeRecipe;
import com.brandon3055.draconicevolution.command.CommandRespawnGuardian;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final Logger LOGGER = LogManager.getLogger("DraconicEvolution"); //TODO going to slowly transition everything to this.
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

    public DraconicEvolution() {
        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.construct();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        DraconicAPI.FUSION_RECIPE_TYPE = IRecipeType.register(MODID + ":fusion_crafting");
        CraftingHelper.register(DraconicAPI.INGREDIENT_STACK_TYPE, IngredientStack.SERIALIZER);
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

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandKaboom.register(event.getDispatcher());
        CommandMakeRecipe.register(event.getDispatcher());
        CommandRespawnGuardian.register(event.getDispatcher());
    }
}