package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ItemStackHandlerExt;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

public class TileGenerator extends TileBCore implements ITickable, IChangeListener {

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

    public ItemStackHandlerExt itemHandler;
    public OPStorage opStorage;

    public TileGenerator() {
        itemHandler = addItemHandlerCap(new ItemStackHandlerExt(1)).getData();
        itemHandler.setStackValidator((integer, itemStack) -> TileEntityFurnace.getItemBurnTime(itemStack) > 0);
        opStorage = addEnergyCap(new OPStorage(100000, 0, 1000)).syncContainer(true).getData();
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {//TODO Give this a good re write!
        super.update();
        if (world.isRemote) {
            return;
        }

        active.set(burnTimeRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored());

        if (burnTimeRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored()) {
            burnTimeRemaining.subtract(burnSpeed);
            opStorage.modifyEnergyStored(burnSpeed * EPBT);
        }

        if (burnTimeRemaining.get() <= 0 && opStorage.getOPStored() < opStorage.getMaxOPStored() && !powered.get()) {
            tryRefuel();
        }

        opStorage.modifyEnergyStored(-sendEnergyToAll(opStorage.getMaxExtract(), opStorage.getOPStored()));
    }

    public void tryRefuel() {
        if (burnTimeRemaining.get() > 0 || opStorage.getOPStored() >= opStorage.getMaxOPStored()) return;
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            int itemBurnTime = TileEntityFurnace.getItemBurnTime(stack);

            if (itemBurnTime > 0) {
                if (stack.getCount() == 1) {
                    stack = stack.getItem().getContainerItem(stack);
                }
                else {
                    stack.shrink(1);
                }
                itemHandler.setStackInSlot(0, stack);
                burnTime.set(itemBurnTime);
                burnTimeRemaining.set(itemBurnTime);
            }
        }
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        powered.set(world.isBlockPowered(pos));
    }
}


//public class TileGenerator extends TileEnergyInventoryBase implements IEnergyProvider, ITickable, IChangeListener {
//
//    private int burnSpeed = 6;
//    /**
//     * Energy per burn tick
//     */
//    private int EPBT = 14;
//
//    //Synced Fields
//    public final ManagedInt burnTime = register(new ManagedInt("burnTime", 1, SAVE_BOTH_SYNC_CONTAINER));
//
//    public final ManagedInt burnTimeRemaining = register(new ManagedInt("burnTimeRemaining", 0, SAVE_BOTH_SYNC_CONTAINER));
//    public final ManagedBool active = register(new ManagedBool("active", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));
//    public final ManagedBool powered = register(new ManagedBool("powered", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));
//
//    public TileGenerator() {
//        setInventorySize(1);
//        setEnergySyncMode().addFlags(SYNC_TILE);
//        setCapacityAndTransfer(100000, 0, 1000);
//        setShouldRefreshOnBlockChange();
//    }
//
//    @Override
//    public void update() {//TODO Give this a good re write!
//        super.update();
//        if (world.isRemote) {
//            return;
//        }
//
//        active.set(burnTimeRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored());
//
//        if (burnTimeRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored()) {
//            burnTimeRemaining.subtract(burnSpeed);
//            energyStorage.modifyEnergyStored(burnSpeed * EPBT);
//        }
//
//        if (burnTimeRemaining.get() <= 0 && opStorage.getOPStored() < opStorage.getMaxOPStored() && !powered.get()) {
//            tryRefuel();
//        }
//
//        energyStorage.modifyEnergyStored(-sendEnergyToAll());
//    }
//
//    public void tryRefuel() {
//        if (burnTimeRemaining.get() > 0 || opStorage.getOPStored() >= opStorage.getMaxOPStored()) return;
//        ItemStack stack = getStackInSlot(0);
//        if (!stack.isEmpty()) {
//            int itemBurnTime = TileEntityFurnace.getItemBurnTime(stack);
//
//            if (itemBurnTime > 0) {
//                if (stack.getCount() == 1) {
//                    stack = stack.getItem().getContainerItem(stack);
//                }
//                else {
//                    stack.shrink(1);
//                }
//                setInventorySlotContents(0, stack);
//                burnTime.set(itemBurnTime);
//                burnTimeRemaining.set(itemBurnTime);
//            }
//        }
//    }
//
//    //region IEnergyProvider
//    @Override
//    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
//        return super.extractEnergy(from, maxExtract, simulate);
//    }
//
//    @Override
//    public int opStorage.getOPStored(EnumFacing from) {
//        return super.getEnergyStored();
//    }
//
//    @Override
//    public int opStorage.getMaxOPStored(EnumFacing from) {
//        return super.getMaxEnergyStored();
//    }
//
//    @Override
//    public boolean canConnectEnergy(EnumFacing from) {
//        return true;
//    }
//    //endregion
//
//    @Override
//    public boolean isItemValidForSlot(int index, ItemStack stack) {
//        return TileEntityFurnace.getItemBurnTime(stack) > 0;
//    }
//
//    @Override
//    public void onNeighborChange(BlockPos neighbor) {
//        powered.set(world.isBlockPowered(pos));
//    }
//}