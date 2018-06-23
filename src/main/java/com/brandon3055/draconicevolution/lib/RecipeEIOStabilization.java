package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier.*;

/**
 * Created by brandon3055 on 27/07/2016.
 */
public class RecipeEIOStabilization implements IRecipe {

    public Item cachedCore = null;
    private Item brokenSpawner;
    private ResourceLocation name;

    public RecipeEIOStabilization(Item brokenSpawner) {
        this.brokenSpawner = brokenSpawner;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundSpawner = false;
        boolean foundCore = false;
        Item foundCoreItem = null;

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (!itemstack.isEmpty()) {
                    boolean flag = false;

                    if (itemstack.getItem() instanceof ItemCore && !foundCore) {
                        foundCore = flag = true;
                        foundCoreItem = itemstack.getItem();
                    }
                    else if (itemstack.getItem() == brokenSpawner && !foundSpawner) {
                        foundSpawner = flag = true;
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        boolean valid = foundSpawner && foundCore;
        if (valid) {
            cachedCore = foundCoreItem;
        }
        return valid;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack spawner = ItemStack.EMPTY;
        ItemStack core = ItemStack.EMPTY;

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (!itemstack.isEmpty()) {
                    LogHelper.dev(itemstack + " " + itemstack.getTagCompound());
                    boolean flag = false;

                    if (itemstack.getItem() instanceof ItemCore && core.isEmpty()) {
                        core = itemstack;
                        flag = true;
                    }
                    else if (itemstack.getItem() == brokenSpawner && spawner.isEmpty()) {
                        spawner = itemstack;
                        flag = true;
                    }

                    if (!flag) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (spawner.isEmpty() || core.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = new ItemStack(DEFeatures.stabilizedSpawner);

        String name = spawner.hasTagCompound() && spawner.getTagCompound().hasKey("entityId") ? spawner.getTagCompound().getString("entityId") : null;
        SpawnerTier tier = core.getItem() == DEFeatures.draconicCore ? BASIC : core.getItem() == DEFeatures.wyvernCore ? WYVERN : core.getItem() == DEFeatures.awakenedCore ? DRACONIC : CHAOTIC;
        DEFeatures.stabilizedSpawner.setStackData(output, name, tier);

        cachedCore = core.getItem();
        return output;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(DEFeatures.stabilizedSpawner);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        NonNullList<ItemStack> list = NonNullList.create();
        DataUtils.forEachMatch(aitemstack, Objects::nonNull, list::add);
        return list;
    }


    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 || height >= 2;
    }

    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     * <p>
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     * <p>
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     * <p>
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Class<IRecipe> getRegistryType() {
        return IRecipe.class;
    }
}
