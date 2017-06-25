package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 27/07/2016.
 */
public class RecipeDislocatorClone implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack slot1 = inv.getStackInSlot(0);
        ItemStack slot2 = inv.getStackInSlot(1);
        return slot1 != null && slot1.getItem() instanceof Dislocator && slot2 != null && slot2.getItem() instanceof Dislocator;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack source = inv.getStackInSlot(0);
        ItemStack target = inv.getStackInSlot(1);

        if (source.isEmpty() || target.isEmpty() || !source.hasTagCompound() || !(source.getItem() instanceof Dislocator) || !(target.getItem() instanceof Dislocator)) {
            return ItemStack.EMPTY;
        }

        ItemStack output = target.copy();
        NBTTagList sourceList = new NBTTagList();

        if (source.getItem() == DEFeatures.dislocator) {
            TeleportLocation location = ((Dislocator) source.getItem()).getLocation(source);
            location.setName("*-Copy-*");
            NBTTagCompound compound = new NBTTagCompound();
            location.writeToNBT(compound);
            sourceList.appendTag(compound);
        }
        else if (source.getItem() == DEFeatures.dislocatorAdvanced) {
            sourceList = ItemNBTHelper.getCompound(source).getTagList("Locations", 10);
        }

        if (output.getItem() == DEFeatures.dislocator) {
            TeleportLocation location = ((Dislocator) source.getItem()).getLocation(source);
            if (location == null) {
                return ItemStack.EMPTY;
            }
            NBTTagCompound compound = new NBTTagCompound();
            location.writeToNBT(compound);
            compound.setBoolean("IsSet", true);
            output.setTagCompound(compound);
        }
        else if (output.getItem() == DEFeatures.dislocatorAdvanced) {
            NBTTagCompound compound = ItemNBTHelper.getCompound(output);
            NBTTagList targetList = compound.getTagList("Locations", 10);

            for (int i = 0; i < sourceList.tagCount(); i++) {
                boolean matchFound = false;
                TeleportLocation sourceLocation = new TeleportLocation();
                NBTTagCompound sourceCompound = sourceList.getCompoundTagAt(i);
                sourceLocation.readFromNBT(sourceCompound);

                for (int j = 0; j < targetList.tagCount(); j++) {
                    TeleportLocation targetLocation = new TeleportLocation();
                    targetLocation.readFromNBT(targetList.getCompoundTagAt(j));

                    if (sourceLocation.hashCode() == targetLocation.hashCode()) {
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    targetList.appendTag(sourceCompound);
                }
            }

            compound.setTag("Locations", targetList);
        }

        return output;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        ItemStack stack = inv.getStackInSlot(0);
        NonNullList<ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            list.set(0, stack.copy());
        }
        return list;
    }
}
