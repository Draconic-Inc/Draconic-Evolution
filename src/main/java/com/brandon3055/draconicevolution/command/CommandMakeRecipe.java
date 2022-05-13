package com.brandon3055.draconicevolution.command;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.tags.ITag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class CommandMakeRecipe {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("gen_recipe")
                        .requires(cs -> cs.hasPermission(3))
                        .then(Commands.literal("fusion")
                                .executes(CommandMakeRecipe::genFusion))
                        .then(Commands.literal("crafting")
                                .executes(CommandMakeRecipe::genCrafting))
        );
    }

    private static int genFusion(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockHitResult result = RayTracer.retrace(player, 10, ClipContext.Block.OUTLINE);
        if (result.getType() != HitResult.Type.BLOCK) {
            throw new CommandRuntimeException(new TextComponent("No chest found.\nYou must be looking at a single chest with the recipe laid out on the far left and the result in the center slot.\nFor fusion recipes all slots other than center are ingredients except row 2, slot 2 which is the catalyst."));
        }

        BlockPos pos = result.getBlockPos();
        IItemHandler handler = getInventory(player.level, pos);
        String recipe = getFusionRecipe(handler);

        while (player.getOffhandItem().getItem() == Items.GOLDEN_APPLE) {
            pos = pos.above();
            if (player.level.getBlockEntity(pos) instanceof ChestBlockEntity) {
                handler = getInventory(player.level, pos);
                recipe += "\n\n" + getFusionRecipe(handler);
            } else {
                break;
            }
        }

        BrandonsCore.proxy.setClipboardString(recipe);
        ChatHelper.sendMessage(player, new TextComponent("Recipe copied to clipboard"));
        return 0;
    }

    private static int genCrafting(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
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
            throw new CommandRuntimeException(new TextComponent("Result slot (center of chest) is empty"));
        } else if (catalyst.isEmpty()) {
            throw new CommandRuntimeException(new TextComponent("Catalyst slot (row 2 column 2) is empty"));
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
            throw new CommandRuntimeException(new TextComponent("An error occurred while getting items"));
        }
        return names;
    }

    public static Map<ResourceLocation, String> getTags() {
        Map<ResourceLocation, String> names = new HashMap<>();
        try {
            for (Field field : Tags.Items.class.getFields()) {
                if (TagKey.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((TagKey) field.get(null)).location(), "Tags.Items." + field.getName());
                }
            }
            for (Field field : DETags.Items.class.getFields()) {
                if (TagKey.class.isAssignableFrom(field.getType()) && (field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0) {
                    names.put(((TagKey) field.get(null)).location(), "DETags.Items." + field.getName());
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new CommandRuntimeException(new TextComponent("An error occurred while getting tags"));
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
        throw new CommandRuntimeException(new TextComponent("Failed to locate item field for key: " + key));
    }

    public static String getItem(ItemStack stack) {
        return getItem(stack, false);
    }

    public static String getIngredient(ItemStack stack, boolean ignoreSize) {
        Map<ResourceLocation, String> tags = getTags();
        for (ResourceLocation tag : stack.getTags().map(TagKey::location).toList()) {
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

    private static IItemHandler getInventory(Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ChestBlockEntity) {
            LazyOptional<IItemHandler> optional = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (optional.isPresent()) {
                IItemHandler handler = optional.orElseThrow(WTFException::new);
                if (handler.getSlots() != 27) {
                    throw new CommandRuntimeException(new TextComponent("Must be a single chest with result in center slot"));
                }
                return handler;
            }
        }
        throw new CommandRuntimeException(new TextComponent("No chest found.\nYou must be looking at a single chest with the recipe laid out on the far left and the result in the center slot.\nFor fusion recipes all slots other than center are ingredients except row 2, slot 2 which is the catalyst."));
    }

}
