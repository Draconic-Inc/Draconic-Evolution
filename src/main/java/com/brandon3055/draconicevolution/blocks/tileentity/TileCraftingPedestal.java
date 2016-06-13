package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingPedestal;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.blocks.CraftingPedestal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileCraftingPedestal extends TileInventoryBase implements IEnergyReceiver, ICraftingPedestal {

    public final SyncableByte facing = new SyncableByte((byte)0, true, false, true);
    private IFusionCraftingInventory currentCraftingInventory;

    public TileCraftingPedestal(){
        this.setInventorySize(1);
        registerSyncableObject(facing, true);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        updateBlock();
    }

    @Override
    public void updateBlock() {
        super.updateBlock();
        detectAndSendChanges();
    }

    //region IEnergy

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (currentCraftingInventory != null){
            return currentCraftingInventory.receiveEnergyFromPedestal(maxReceive, this);
        }
        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from != EnumFacing.getFront(facing.value);
    }

    //endregion

    //region ICraftingPedestal

    @Override
    public int getPedestalTier() {
        String tier = worldObj.getBlockState(pos).getValue(CraftingPedestal.TIER);
        return CraftingPedestal.TIER.toMeta(tier);
    }

    @Override
    public ItemStack getStackInPedestal() {
        return getStackInSlot(0);
    }

    @Override
    public void setStackInPedestal(ItemStack stack) {
        setInventorySlotContents(0, stack);
    }

    @Override
    public boolean setCraftingInventory(IFusionCraftingInventory craftingInventory) {
        if (currentCraftingInventory != null && currentCraftingInventory.craftingInProgress() && !((TileEntity)currentCraftingInventory).isInvalid()) {
            return false;
        }

        currentCraftingInventory = craftingInventory;
        return true;
    }

    //endregion
}
