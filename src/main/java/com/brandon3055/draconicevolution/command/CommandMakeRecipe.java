package com.brandon3055.draconicevolution.command;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.datagen.FusionRecipeBuilder;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandon3055.brandonscore.api.TechLevel.WYVERN;
import static com.brandon3055.draconicevolution.init.DEContent.core_awakened;
import static com.brandon3055.draconicevolution.init.DEContent.core_wyvern;
import static com.brandon3055.draconicevolution.init.DETags.Items.INGOTS_DRACONIUM_AWAKENED;
import static net.minecraftforge.common.Tags.Items.NETHER_STARS;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandMakeRecipe {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("gen_recipe")
                        .requires(cs -> cs.hasPermissionLevel(3))
                        .then(Commands.literal("fusion")
                                .executes(CommandMakeRecipe::genFusion))
                        .then(Commands.literal("crafting")
                                .executes(CommandMakeRecipe::genCrafting))
        );
    }

    private static int genFusion(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        BlockRayTraceResult result = RayTracer.retrace(player, 10, RayTraceContext.BlockMode.OUTLINE);
        if (result.getType() != RayTraceResult.Type.BLOCK) {
            throw new CommandException(new StringTextComponent("No chest found.\nYou must be looking at a single chest with the recipe laid out on the far left and the result in the center slot.\nFor fusion recipes all slots other than center are ingredients except row 2, slot 2 which is the catalyst."));
        }

        BlockPos pos = result.getPos();
        IItemHandler handler = getInventory(player.world, pos);
        String recipe = getFusionRecipe(handler);

        while (player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            pos = pos.up();
            if (player.world.getTileEntity(pos) instanceof ChestTileEntity) {
                handler = getInventory(player.world, pos);
                recipe += "\n\n" + getFusionRecipe(handler);
            } else {
                break;
            }
        }

        BrandonsCore.proxy.setClipboardString(recipe);
        ChatHelper.sendMessage(player, new StringTextComponent("Recipe copied to clipboard"));
        return 0;
    }

    private static int genCrafting(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
//        IItemHandler handler = getInventory(player.);
        return 0;
    }

    private static String getFusionRecipe(IItemHandler handler) {
        List<ItemStack> ingredients = new ArrayList<>();
        ItemStack result = ItemStack.EMPTY;
        ItemStack catalyst = ItemStack.EMPTY;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (i == 13) {
                result = stack;
            } else if (i == 10) {
                catalyst = stack;
            } else if (!stack.isEmpty()) {
                ingredients.add(stack);
            }
        }

        if (result.isEmpty()) {
            throw new CommandException(new StringTextComponent("Result slot (center of chest) is empty"));
        } else if (catalyst.isEmpty()) {
            throw new CommandException(new StringTextComponent("Catalyst slot (row 2 column 2) is empty"));
        }

        StringBuilder recipeCode = new StringBuilder();
        if (result.getCount() > 1) {
            recipeCode.append(String.format("FusionRecipeBuilder.fusionRecipe(%s, %s)", getItem(result, true), result.getCount())).append("\n");
        } else {
            recipeCode.append(String.format("FusionRecipeBuilder.fusionRecipe(%s)", getItem(result))).append("\n");
        }

        if (catalyst.getCount() > 1) {
            recipeCode.append(String.format(".catalyst(%s, %s)", catalyst.getCount(), getIngredient(catalyst, true))).append("\n");
        } else {
            recipeCode.append(String.format(".catalyst(%s)", getIngredient(catalyst))).append("\n");
        }


        recipeCode.append(".energy(energyvalue)").append("\n");
        recipeCode.append(".techLevel(tecklevel)").append("\n");

        for (ItemStack stack : ingredients) {
            recipeCode.append(String.format(".ingredient(%s)", getIngredient(stack, true))).append("\n");
        }

        recipeCode.append(String.format(".build(consumer, folder(\"tools\", " + getItem(result, true) + "));", result.getItem().getRegistryName()));
        return recipeCode.toString();
    }

    public static Map<ResourceLocation, String> getFields() {
        Map<ResourceLocation, String> names = new HashMap<>();
        try {
            for (Field field : DEContent.class.getFields()) {
                if (field.get(null) != null && Item.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((Item) field.get(null)).getRegistryName(), "DEContent." + field.getName());
                }
                if (field.get(null) != null && Block.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((Block) field.get(null)).getRegistryName(), "DEContent." + field.getName());
                }
            }
            for (Field field : Items.class.getFields()) {
                if (Item.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((Item) field.get(null)).getRegistryName(), "Items." + field.getName());
                }
            }
            for (Field field : Blocks.class.getFields()) {
                if (Block.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((Block) field.get(null)).getRegistryName(), "Blocks." + field.getName());
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new CommandException(new StringTextComponent("An error occurred while getting items"));
        }
        return names;
    }

    public static Map<ResourceLocation, String> getTags() {
        Map<ResourceLocation, String> names = new HashMap<>();
        try {
            for (Field field : Tags.Items.class.getFields()) {
                if (ITag.INamedTag.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((ITag.INamedTag) field.get(null)).getName(), "Tags.Items." + field.getName());
                }
            }
            for (Field field : DETags.Items.class.getFields()) {
                if (ITag.INamedTag.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((ITag.INamedTag) field.get(null)).getName(), "DETags.Items." + field.getName());
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new CommandException(new StringTextComponent("An error occurred while getting tags"));
        }
        List<String> tagPrefixes = new ArrayList<>();
        tagPrefixes.add("forge:dusts/");
        tagPrefixes.add("forge:nuggets/");
        tagPrefixes.add("forge:ingots/");
        tagPrefixes.add("forge:gems/");
        tagPrefixes.add("forge:storage_blocks/");
        tagPrefixes.add("forge:rods/");
        tagPrefixes.add("forge:glass/");
        tagPrefixes.add("forge:glass_panes/");
        tagPrefixes.add("forge:ores/");

        Map<ResourceLocation, String> tagMap = new HashMap<>();
        names.forEach((name, field) -> {
            for (String prefix : tagPrefixes) {
                if (name.toString().startsWith(prefix)) {
                    tagMap.put(name, field);
                    break;
                }
            }
        });

        return tagMap;
    }

    public static String getItem(ItemStack stack, boolean ignnoreSize) {
        Map<ResourceLocation, String> fields = getFields();
        ResourceLocation key = stack.getItem().getRegistryName();
        if (fields.containsKey(key)) {
            if (stack.getCount() > 1 && !ignnoreSize) {
                return "new ItemStack(" + fields.get(key) + ", " + stack.getCount() + ")";
            } else {
                return fields.get(key);
            }
        }
        throw new CommandException(new StringTextComponent("Failed to locate item field for key: " + key));
    }

    public static String getItem(ItemStack stack) {
        return getItem(stack, false);
    }

    public static String getIngredient(ItemStack stack, boolean ignoreSize) {
        Map<ResourceLocation, String> tags = getTags();
        for (ResourceLocation tag : stack.getItem().getTags()) {
            if (tags.containsKey(tag)) {
                if (stack.getCount() > 1 && !ignoreSize) {
                    return "IngredientStack.fromTag(" + tags.get(tag) + ", " + stack.getCount() + ")";
                } else {
//                    return "Ingredient.fromTag(" + tags.get(tag) + ")";
                    return tags.get(tag);
                }
            }
        }

        String item = getItem(stack);
        if (stack.getCount() > 1 && !ignoreSize) {
            return "IngredientStack.fromItems(" + stack.getCount() + "," + item + ")";
        } else {
//            return "Ingredient.fromItems(" + item + ")";
            return item;
        }
    }

    public static String getIngredient(ItemStack stack) {
        return getIngredient(stack, false);
    }

    private static IItemHandler getInventory(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ChestTileEntity) {
            LazyOptional<IItemHandler> optional = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (optional.isPresent()) {
                IItemHandler handler = optional.orElseThrow(WTFException::new);
                if (handler.getSlots() != 27) {
                    throw new CommandException(new StringTextComponent("Must be a single chest with result in center slot"));
                }
                return handler;
            }
        }
        throw new CommandException(new StringTextComponent("No chest found.\nYou must be looking at a single chest with the recipe laid out on the far left and the result in the center slot.\nFor fusion recipes all slots other than center are ingredients except row 2, slot 2 which is the catalyst."));
    }

}
