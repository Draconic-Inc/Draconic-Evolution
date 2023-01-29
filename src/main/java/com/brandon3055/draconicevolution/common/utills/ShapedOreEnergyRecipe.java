package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;

/**
 * Created by Brandon on 8/12/2014.
 */
public class ShapedOreEnergyRecipe extends ShapedOreRecipe {

    public ShapedOreEnergyRecipe(Block result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedOreEnergyRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedOreEnergyRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        ItemStack result = super.getCraftingResult(var1);

        int energy = 0;
        for (int i = 0; i < var1.getSizeInventory(); i++) {
            ItemStack stack = var1.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
                energy += ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);
            }
        }

        if (energy > 0 && result != null && result.getItem() instanceof IEnergyContainerItem)
            ItemNBTHelper.setInteger(result, "Energy", energy);

        return result;
    }
}
