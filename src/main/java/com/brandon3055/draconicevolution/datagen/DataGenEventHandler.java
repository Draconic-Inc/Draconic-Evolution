package com.brandon3055.draconicevolution.datagen;

import com.google.gson.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.data.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.jline.utils.InputStreamReader;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 26/2/20.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

//    private static final Gson GSON = new GsonBuilder()
//            .registerTypeAdapter(Variant.class, new Variant.Deserializer())
//            .registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
//            .registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
//            .create();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new LangGenerator(gen));
            gen.addProvider(new BlockStateGenerator(gen, event.getExistingFileHelper()));
            gen.addProvider(new ItemModelGenerator(gen, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            gen.addProvider(new RecipeGenerator(gen));
            gen.addProvider(new LootTableGenerator(gen));
        }

    }

//    public static class Recipes extends RecipeProvider implements IConditionBuilder
//    {
//        public Recipes(DataGenerator gen)
//        {
//            super(gen);
//        }
//
//        @Override
//        protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
//        {
//            ResourceLocation ID = new ResourceLocation("data_gen_test", "conditional");
//
//            ConditionalRecipe.builder()
//                    .addCondition(
//                            and(
//                                    not(modLoaded("minecraft")),
//                                    itemExists("minecraft", "dirt"),
//                                    FALSE()
//                            )
//                    )
//                    .addRecipe(
//                            ShapedRecipeBuilder.shapedRecipe(Blocks.DIAMOND_BLOCK, 64)
//                                    .patternLine("XXX")
//                                    .patternLine("XXX")
//                                    .patternLine("XXX")
//                                    .key('X', Blocks.DIRT)
//                                    .setGroup("")
//                                    .addCriterion("has_dirt", hasItem(Blocks.DIRT)) //Doesn't actually print... TODO: nested/conditional advancements?
//                                    ::build
//                    )
//                    .setAdvancement(ID,
//                            ConditionalAdvancement.builder()
//                                    .addCondition(
//                                            and(
//                                                    not(modLoaded("minecraft")),
//                                                    itemExists("minecraft", "dirt"),
//                                                    FALSE()
//                                            )
//                                    )
//                                    .addAdvancement(
//                                            Advancement.Builder.builder()
//                                                    .withParentId(new ResourceLocation("minecraft", "root"))
//                                                    .withDisplay(Blocks.DIAMOND_BLOCK,
//                                                            new StringTextComponent("Dirt2Diamonds"),
//                                                            new StringTextComponent("The BEST crafting recipe in the game!"),
//                                                            null, FrameType.TASK, false, false, false
//                                                    )
//                                                    .withRewards(AdvancementRewards.Builder.recipe(ID))
//                                                    .withCriterion("has_dirt", hasItem(Blocks.DIRT))
//                                    )
//                    )
//                    .build(consumer, ID);
//        }
//    }


}
