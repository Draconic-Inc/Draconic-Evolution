package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

public class TileGenerator extends TileEnergyInventoryBase implements IEnergyProvider, ITickable, IChangeListener {

    private int burnSpeed = 6;
    /**
     * Energy per burn tick
     */
    private int EPBT = 14;

    //Synced Fields
    public final ManagedInt burnTime = register(new ManagedInt("burnTime", 1, SAVE_BOTH_SYNC_CONTAINER));

    public final ManagedInt burnTimeRemaining = register(new ManagedInt("burnTimeRemaining", 0, SAVE_BOTH_SYNC_CONTAINER));
    public final ManagedBool active = register(new ManagedBool("active", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool powered = register(new ManagedBool("powered", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));

    public TileGenerator() {
        setInventorySize(1);
        setEnergySyncMode().addFlags(SYNC_TILE);
        setCapacityAndTransfer(100000, 0, 1000);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {//TODO Give this a good re write!
        super.update();
        if (world.isRemote) {
            return;
        }

        active.set(burnTimeRemaining.get() > 0 && getEnergyStored() < getMaxEnergyStored());

        if (burnTimeRemaining.get() > 0 && getEnergyStored() < getMaxEnergyStored()) {
            burnTimeRemaining.subtract(burnSpeed);
            energyStorage.modifyEnergyStored(burnSpeed * EPBT);
        }

        if (burnTimeRemaining.get() <= 0 && getEnergyStored() < getMaxEnergyStored() && !powered.get()) {
            tryRefuel();
        }

        energyStorage.modifyEnergyStored(-sendEnergyToAll());
    }

    public void tryRefuel() {
        if (burnTimeRemaining.get() > 0 || getEnergyStored() >= getMaxEnergyStored()) return;
        ItemStack stack = getStackInSlot(0);
        if (!stack.isEmpty()) {
            int itemBurnTime = TileEntityFurnace.getItemBurnTime(stack);

            if (itemBurnTime > 0) {
                if (stack.getCount() == 1) {
                    stack = stack.getItem().getContainerItem(stack);
                }
                else {
                    stack.shrink(1);
                }
                setInventorySlotContents(0, stack);
                burnTime.set(itemBurnTime);
                burnTimeRemaining.set(itemBurnTime);
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

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        powered.set(world.isBlockPowered(pos));
    }
}
