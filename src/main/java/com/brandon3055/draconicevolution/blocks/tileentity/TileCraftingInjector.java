package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.EnergyHandlerWrapper;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileCraftingInjector extends TileInventoryBase implements IEnergyReceiver, ICraftingInjector, IExtendedRFStorage {

    public final ManagedByte facing = register("facing", new ManagedByte(0)).syncViaTile().saveToTile().trigerUpdate().finish();
    private final ManagedLong energy = register("energy", new ManagedLong(0)).syncViaTile().saveToTile().finish();
    private final ManagedVec3I lastCorePos = register("lastCorePos", new ManagedVec3I(new Vec3I(0, 0, 0))).syncViaTile().saveToTile().finish();
    public final ManagedBool singleItem = register("singleItem", new ManagedBool(false)).syncViaTile().saveToTile().finish();
    public IFusionCraftingInventory currentCraftingInventory = null;
    private int chargeSpeedModifier = 300;

    public TileCraftingInjector() {
        this.setInventorySize(1);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void updateBlock() {
        super.updateBlock();
        super.update();
    }

    //region IEnergy

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        validateCraftingInventory();
        if (currentCraftingInventory != null) {
            long maxRFPerTick = currentCraftingInventory.getIngredientEnergyCost() / chargeSpeedModifier;
            long maxAccept = Math.min(maxReceive, Math.min(currentCraftingInventory.getIngredientEnergyCost() - energy.value, maxRFPerTick));

            if (!simulate) {
                energy.value += maxAccept;
            }

            super.update();
            return (int) maxAccept;
        }

        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (int) Math.min(Integer.MAX_VALUE, getExtendedStorage());
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return (int) Math.min(Integer.MAX_VALUE, getExtendedCapacity());
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from != EnumFacing.VALUES[MathHelper.abs(facing.value % EnumFacing.VALUES.length)];
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(new EnergyHandlerWrapper(this, facing));
        }

        return super.getCapability(capability, facing);
    }

    //endregion

    //region ICraftingPedestal

    @Override
    public int getPedestalTier() {
        String tier = getState(DEFeatures.craftingInjector).getValue(CraftingInjector.TIER);
        return CraftingInjector.TIER.toMeta(tier);
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
        if (craftingInventory == null) {
            currentCraftingInventory = null;
            return false;
        }
        if (validateCraftingInventory() && !world.isRemote) {
            return false;
        }
        currentCraftingInventory = craftingInventory;
        lastCorePos.vec = new Vec3I(((TileEntity) craftingInventory).getPos());
        chargeSpeedModifier = 300 - (getPedestalTier() * 80);
        return true;
    }

    @Override
    public EnumFacing getDirection() {
        return EnumFacing.VALUES[MathHelper.abs(facing.value % EnumFacing.VALUES.length)];
    }

    @Override
    public long getInjectorCharge() {
        return energy.value;
    }

    private boolean validateCraftingInventory() {
        if (!getStackInPedestal().isEmpty() && currentCraftingInventory != null && currentCraftingInventory.craftingInProgress() && !((TileEntity) currentCraftingInventory).isInvalid()) {
            return true;
        }

        currentCraftingInventory = null;
        return false;
    }


    @Override
    public void onCraft() {
        if (currentCraftingInventory != null) {
            energy.value = 0;
        }
    }

    //endregion


    @Override
    public int getInventoryStackLimit() {
        return singleItem.value ? 1 : 64;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventoryStacks.set(index, stack);
        markDirty();

        TileEntity tile = world.getTileEntity(lastCorePos.vec.getPos());
        if (tile instanceof IFusionCraftingInventory) {
            world.notifyNeighborsOfStateChange(tile.getPos(), tile.getBlockType(), true);
        }

        updateBlock();
    }

    @Override
    public long getExtendedStorage() {
        return energy.value;
    }

    @Override
    public long getExtendedCapacity() {
        validateCraftingInventory();
        if (currentCraftingInventory != null) {
            currentCraftingInventory.getIngredientEnergyCost();
        }
        return 0;
    }
}
