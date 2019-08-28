package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.DEConfig;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 9/06/2017.
 */
public class OreDoublingRegistry {

    public static Map<String, ItemStack> oreRecipes = new HashMap<>();
    public static Map<ItemStack, ItemStack> stackToStackRecipes = new HashMap<>();

    public static void init() {
        for (String oreName : OreDictionary.getOreNames()) {
            if (!oreName.startsWith("ore") || DEConfig.oreBlacklist.contains(oreName)) {
                continue;
            }

            String ingot = oreName.replace("ore", "ingot");

            if (!OreDictionary.doesOreNameExist(ingot) || OreDictionary.getOres(ingot).isEmpty() || OreDictionary.getOres(oreName).isEmpty()) {
                ingot = oreName.replace("ore", "gem");
                if (!OreDictionary.doesOreNameExist(ingot) || OreDictionary.getOres(ingot).isEmpty() || OreDictionary.getOres(oreName).isEmpty()) {
                    continue;
                }
            }

            List<ItemStack> ingots = OreDictionary.getOres(ingot);
            int oreId = OreDictionary.getOreID(oreName);
            ItemStack stack = ItemStack.EMPTY;

            for (ItemStack candidate : ingots) {
                boolean invalid = false;
                for (int id : OreDictionary.getOreIDs(candidate)) {
                    if (id == oreId) {
                        invalid = true;
                        break;
                    }
                }
                if (invalid) {
                    continue;
                }
                stack = candidate;
                ResourceLocation registryName = candidate.getItem().getRegistryName();
                if (registryName != null && registryName.getNamespace().equals(DEConfig.oreDoublingOutputPriority)) {
                    break;
                }
            }

            if (!stack.isEmpty()) {
                stack = stack.copy();
                stack.setCount(2);
                registerOreResult(oreName, stack);
            }
        }

        registerDEOverrides();
    }

    public static void registerOreResult(String ore, ItemStack result) {
        oreRecipes.put(ore, result);
    }

    public static void registerResult(ItemStack input, ItemStack result) {
        stackToStackRecipes.put(input, result);
    }

    public static ItemStack getSmeltingResult(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getCount() != 1) {
            stack = stack.copy();
            stack.setCount(1);
        }

        if (stackToStackRecipes.containsKey(stack)) {
            return stackToStackRecipes.get(stack);
        }

        int ids[] = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String sid = OreDictionary.getOreName(id);
            if (oreRecipes.containsKey(sid)) {
                return oreRecipes.getOrDefault(sid, ItemStack.EMPTY);
            }
        }

        return FurnaceRecipes.instance().getSmeltingResult(stack);
    }

    private static void registerDEOverrides() {
        registerOreResult("sand", new ItemStack(Blocks.GLASS, 2));
        registerOreResult("cobblestone", new ItemStack(Blocks.STONE, 2));
        registerOreResult("netherrack", new ItemStack(Items.NETHERBRICK, 2));
        registerOreResult("cobblestone", new ItemStack(Blocks.STONE, 2));

        registerResult(new ItemStack(Items.CLAY_BALL), new ItemStack(Items.BRICK, 2));
        registerResult(new ItemStack(Blocks.CACTUS), new ItemStack(Items.DYE, 2, 2));
    }

}