package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileGenerator extends TileEnergyInventoryBase implements IEnergyProvider, ITickable {

    private int burnSpeed = 6;
    /**
     * Energy per burn tick
     */
    private int EPBT = 14;

    //Synced Fields
    public final ManagedInt burnTime = register("burnTime", new ManagedInt(1)).saveToTile().saveToItem().syncViaContainer().finish();
    public final ManagedInt burnTimeRemaining = register("burnTimeRemaining", new ManagedInt(0)).saveToTile().saveToItem().syncViaContainer().finish();
    public final ManagedBool active = register("active", new ManagedBool(false)).saveToTile().saveToItem().syncViaTile().trigerUpdate().finish();

    public TileGenerator() {
        setInventorySize(1);
        setEnergySyncMode().syncViaContainer();
        setCapacityAndTransfer(100000, 0, 1000);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {//TODO Give this a good re write!
        super.update();
        if (world.isRemote) {
            return;
        }

        active.value = burnTimeRemaining.value > 0 && getEnergyStored() < getMaxEnergyStored();

        if (burnTimeRemaining.value > 0 && getEnergyStored() < getMaxEnergyStored()) {
            burnTimeRemaining.value -= burnSpeed;
            energyStorage.modifyEnergyStored(burnSpeed * EPBT);
        }
        if (burnTimeRemaining.value <= 0 && getEnergyStored() < getMaxEnergyStored()) {
            tryRefuel();
        }

        energyStorage.modifyEnergyStored(-sendEnergyToAll());
    }

    public void tryRefuel() {
        if (burnTimeRemaining.value > 0 || getEnergyStored() >= getMaxEnergyStored()) return;
        ItemStack stack = getStackInSlot(0);
        if (!stack.isEmpty()) {
            int itemBurnTime = TileEntityFurnace.getItemBurnTime(stack);

            if (itemBurnTime > 0) {
                stack.shrink(1);
                if (stack.getCount() == 0) {
                    stack = stack.getItem().getContainerItem(stack);
                }
                setInventorySlotContents(0, stack);
                burnTime.value = itemBurnTime;
                burnTimeRemaining.value = itemBurnTime;
            }
        }
    }

    //region IEnergyProvider
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return super.extractEnergy(from, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }
    //endregion

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack) > 0;
    }
}
