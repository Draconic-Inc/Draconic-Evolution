package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedLong;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileFusionCraftingInjector extends TileBCore implements IFusionInjector {

    public final ManagedLong energy = register(new ManagedLong("energy", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedLong energyRequired = register(new ManagedLong("energy_required", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedLong chargeRate = register(new ManagedLong("charge_rate", DataFlags.SAVE_NBT));
    public final ManagedBool singleItem = register(new ManagedBool("single_item", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedVec3I corePos = register(new ManagedVec3I("core_pos", new Vec3I(0, -9999, 0), DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(this, 1);
    private TechLevel techLevelCache = null;

    public TileFusionCraftingInjector(BlockPos pos, BlockState state) {
        super(DEContent.TILE_CRAFTING_INJECTOR.get(), pos, state);
        capManager.setManaged("inventory", Capabilities.ItemHandler.BLOCK, itemHandler).saveBoth().syncTile();
        itemHandler.setPerSlotLimit(() -> singleItem.get() ? 1 : 64);
        itemHandler.setContentsChangeListener(i -> inventoryChange());
        capManager.set(CapabilityOP.BLOCK, new OPStorage(this, 0) {
            @Override
            public long receiveOP(long maxReceive, boolean simulate) {
                long opStored = getOPStored();
                long received = Math.max(Math.min(getMaxOPStored() - opStored, Math.min(maxReceive, chargeRate.get())), 0);
                if (!simulate) {
                    TileFusionCraftingInjector.this.energy.add(received);
                }
                return received;
            }

            @Override
            public boolean canReceive() {
                return true;
            }

            @Override
            public long getOPStored() {
                return TileFusionCraftingInjector.this.energy.get();
            }

            @Override
            public long getMaxOPStored() {
                return TileFusionCraftingInjector.this.energyRequired.get();
            }
        });
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_CRAFTING_INJECTOR, CapabilityOP.BLOCK);
        capability(event, DEContent.TILE_CRAFTING_INJECTOR, Capabilities.ItemHandler.BLOCK);
    }

    public boolean setCore(@Nullable TileFusionCraftingCore core) {
        //TODO add some validation or at least let any existing core know we have left the party.
        TileFusionCraftingCore oldCore = getCore();
        if (oldCore != null && oldCore != core && oldCore.isCrafting()) {
            oldCore.cancelCraft();
        }
        if (core == null) {
            corePos.set(new Vec3I(0, -9999, 0));
        } else {
            corePos.set(new Vec3I(core.getBlockPos()));
        }
        setEnergyRequirement(0, 0);
        return true;
    }

    @Nullable
    public TileFusionCraftingCore getCore() {
        if (corePos.get().y == -9999 || level == null) {
            return null;
        }
        BlockEntity tile = level.getBlockEntity(corePos.get().getPos());
        return tile instanceof TileFusionCraftingCore ? (TileFusionCraftingCore) tile : null;
    }

    @Override
    public TechLevel getInjectorTier() {
        if (techLevelCache == null) {
            Block block = getBlockState().getBlock();
            techLevelCache = block instanceof CraftingInjector ? ((CraftingInjector) block).getTechLevel() : TechLevel.DRACONIUM;
        }
        return techLevelCache;
    }

    @Override
    public ItemStack getInjectorStack() {
        return itemHandler.getStackInSlot(0);
    }

    @Override
    public void setInjectorStack(ItemStack stack) {
        itemHandler.setStackInSlot(0, stack);
    }

    @Override
    public long getInjectorEnergy() {
        return energy.get();
    }

    @Override
    public void setInjectorEnergy(long energy) {
        this.energy.set(energy);
    }

    @Override
    public void setEnergyRequirement(long maxEnergy, long chargeRate) {
        this.energyRequired.set(maxEnergy);
        this.chargeRate.set(chargeRate);
    }

    @Override
    public long getEnergyRequirement() {
        return energyRequired.get();
    }

    @Override
    public boolean validate() {
        // the isRemoved check is probably really all i need but better safe than sorry.
        return !isRemoved() && level != null && level.getBlockEntity(worldPosition) == this;
    }

    public Direction getRotation() {
        BlockState state = getBlockState();
        return state.getBlock() instanceof CraftingInjector ? state.getValue(CraftingInjector.FACING) : Direction.UP;
    }

    private void inventoryChange() {
        TileFusionCraftingCore core = getCore();
        if (core != null) {
            core.inventoryChanged();
        }
        updateBlock();
    }

    public void onDestroyed() {
        TileFusionCraftingCore core = getCore();
        if (!getInjectorStack().isEmpty() && core != null) {
            core.cancelCraft();
        }
    }


//    private boolean validateCraftingInventory() {
//        if (!getStackInPedestal().isEmpty() && currentCraftingInventory != null && currentCraftingInventory.craftingInProgress() && !((TileEntity) currentCraftingInventory).isRemoved()) {
//            return true;
//        }
//
//        currentCraftingInventory = null;
//        return false;
//    }
//
//
//    @Override
//    public void onCraft() {
//        if (currentCraftingInventory != null) {
//            energy.zero();
//        }
//    }
//
//    //endregion
//
//    public void slotContentsChanged(int index) {
//        setChanged();
//
//        TileEntity tile = level.getBlockEntity(lastCorePos.get().getPos());
//        if (tile instanceof IFusionCraftingInventory) {
//            level.updateNeighborsAt(tile.getBlockPos(), tile.getBlockState().getBlock());
//        }
//
//        updateBlock();
//    }
}
