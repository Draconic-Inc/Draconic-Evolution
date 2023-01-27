package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.command.DECommands;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.handlers.LootEventHandler;
import com.brandon3055.draconicevolution.handlers.ModuleEventHandler;
import com.brandon3055.draconicevolution.init.ClientInit;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.init.ModCapabilities;
import com.brandon3055.draconicevolution.integration.computers.ComputerCraftCompatEventHandler;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

    public DraconicEvolution() {
        proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        DraconicAPI.FUSION_RECIPE_TYPE = new RecipeType<>() {
            @Override
            public String toString() {
                return MODID + ":fusion_crafting";
            }
        };
        CraftingHelper.register(DraconicAPI.INGREDIENT_STACK_TYPE, IngredientStack.SERIALIZER);

        DEConfig.load();
        DETags.init();
        DraconicNetwork.init();
        EquipmentManager.initialize();
        DECommands.init();
        ModCapabilities.init();
        LootEventHandler.init();
        ModuleEventHandler.init();

        OptionalMod.of("computercraft").ifPresent(e -> MinecraftForge.EVENT_BUS.register(new ComputerCraftCompatEventHandler()));
        MinecraftForge.EVENT_BUS.addListener(Dislocator::onAnvilUpdate);
        MinecraftForge.EVENT_BUS.register(new DEEventHandler());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);
    }
}