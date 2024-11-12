package com.brandon3055.draconicevolution.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 9/06/2017.
 */
@Deprecated
public class OreDoublingRegistry {
//
    public static Map<String, ItemStack> oreRecipes = new HashMap<>();
    public static Map<ItemStack, ItemStack> stackToStackRecipes = new HashMap<>();
//
    public static void init() {
//        for (String oreName : OreDictionary.getOreNames()) {
//            if (!oreName.startsWith("ore") || DEConfig.oreBlacklist.contains(oreName)) {
//                continue;
//            }
//
//            String ingot = oreName.replace("ore", "ingot");
//
//            if (!OreDictionary.doesOreNameExist(ingot) || OreDictionary.getOres(ingot).isEmpty() || OreDictionary.getOres(oreName).isEmpty()) {
//                ingot = oreName.replace("ore", "gem");
//                if (!OreDictionary.doesOreNameExist(ingot) || OreDictionary.getOres(ingot).isEmpty() || OreDictionary.getOres(oreName).isEmpty()) {
//                    continue;
//                }
//            }
//
//            List<ItemStack> ingots = OreDictionary.getOres(ingot);
//            int oreId = OreDictionary.getOreID(oreName);
//            ItemStack stack = ItemStack.EMPTY;
//
//            for (ItemStack candidate : ingots) {
//                boolean invalid = false;
//                for (int id : OreDictionary.getOreIDs(candidate)) {
//                    if (id == oreId) {
//                        invalid = true;
//                        break;
//                    }
//                }
//                if (invalid) {
//                    continue;
//                }
//                stack = candidate;
//                ResourceLocation registryName = candidate.getItem().getRegistryName();
//                if (registryName != null && registryName.getResourceDomain().equals(DEConfig.oreDoublingOutputPriority)) {
//                    break;
//                }
//            }
//
//            if (!stack.isEmpty()) {
//                stack = stack.copy();
//                stack.setCount(2);
//                registerOreResult(oreName, stack);
//            }
//        }
//
//        registerDEOverrides();
    }

    public static void registerOreResult(String ore, ItemStack result) {
        oreRecipes.put(ore, result);
    }

    public static void registerResult(ItemStack input, ItemStack result) {
        stackToStackRecipes.put(input, result);
    }

    public static ItemStack getDoubledSmeltingResult(ItemStack stack, Level world) {
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

        return getSmeltingResult(stack, world);
    }

    public static ItemStack getSmeltingResult(ItemStack stack, Level world) {
        RecipeHolder<SmeltingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new RecipeWrapper(new Wrap(stack)), world).orElse(null);
        return recipe == null ? ItemStack.EMPTY : recipe.value().assemble(new RecipeWrapper(new Wrap(stack)), world.registryAccess());
    }

    private static void registerDEOverrides() {
        registerOreResult("sand", new ItemStack(Blocks.GLASS, 2));
        registerOreResult("cobblestone", new ItemStack(Blocks.STONE, 2));
        registerOreResult("netherrack", new ItemStack(Items.NETHER_BRICK, 2));
        registerOreResult("cobblestone", new ItemStack(Blocks.STONE, 2));

        registerResult(new ItemStack(Items.CLAY_BALL), new ItemStack(Items.BRICK, 2));
        registerResult(new ItemStack(Blocks.CACTUS), new ItemStack(Items.GREEN_DYE, 2));
    }

    private static class Wrap implements IItemHandlerModifiable {
        private ItemStack stack;

        public Wrap(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

        }
    }
//
}