package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyProvider;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableInt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	public final SyncableInt burnTime = new SyncableInt(1, false, true);
	public final SyncableInt burnTimeRemaining = new SyncableInt(0, false, true);
	public final SyncableBool active = new SyncableBool(false, true, false, true);

	public TileGenerator() {
		setInventorySize(1);
		registerSyncableObject(burnTime, false);
		registerSyncableObject(burnTimeRemaining, false);
		registerSyncableObject(active, false);
		registerSyncableObject(energyStored, false);
		setCapacityAndTransfer(100000, 0, 1000);
	}

	@Override
	public void update() {//TODO Give this a good re write!
		super.detectAndSendChanges();
		if (worldObj.isRemote) {
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
		if (stack != null && stack.stackSize > 0) {
			int itemBurnTime = TileEntityFurnace.getItemBurnTime(stack);

			if (itemBurnTime > 0) {
				stack.stackSize--;
				if (stack.stackSize == 0) {
					stack = stack.getItem().getContainerItem(stack);
				}
				setInventorySlotContents(0, stack);
				burnTime.value = itemBurnTime;
				burnTimeRemaining.value = itemBurnTime;
				//updateBlock();
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
	public void writeRetainedData(NBTTagCompound dataCompound) {
		super.writeRetainedData(dataCompound);
		burnTime.toNBT(dataCompound);
		burnTimeRemaining.toNBT(dataCompound);
		active.toNBT(dataCompound);
	}

	@Override
	public void readRetainedData(NBTTagCompound dataCompound) {
		super.readRetainedData(dataCompound);
		burnTime.fromNBT(dataCompound);
		burnTimeRemaining.fromNBT(dataCompound);
		active.fromNBT(dataCompound);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return TileEntityFurnace.getItemBurnTime(stack) > 0;
	}
}