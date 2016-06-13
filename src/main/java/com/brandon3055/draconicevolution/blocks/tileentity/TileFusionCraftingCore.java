package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingPedestal;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileFusionCraftingCore extends TileInventoryBase implements IFusionCraftingInventory, ITickable {

    public TileFusionCraftingCore() {
        setInventorySize(1);
    }

    @Override
    public void update() {

    }

    @Override
    public int receiveEnergyFromPedestal(int max, ICraftingPedestal pedestal) {
        return 0;
    }

    @Override
    public boolean craftingInProgress() {
        return false;
    }

    //region Inventory

    @Override
    public ItemStack getStackInCore() {
        return getStackInSlot(0);
    }

    @Override
    public void setStackInCore(ItemStack stack) {
        setInventorySlotContents(0, stack);
    }

    @Override
    public List<ICraftingPedestal> getPedestals() {
        return null;
    }

    //endregion
}
