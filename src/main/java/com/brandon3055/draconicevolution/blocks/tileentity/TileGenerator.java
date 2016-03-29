package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyProvider;
import com.brandon3055.brandonscore.blocks.TileBCEnergyBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableInt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileGenerator extends TileBCEnergyBase implements IEnergyProvider, ITickable {

	private int burnSpeed = 6;
	/**
	 * Energy per burn tick
	 */
	private int EPBT = 14;

	//Synced Fields
	public final SyncableInt burnTime = new SyncableInt(1, false, true);
	public final SyncableInt burnTimeRemaining = new SyncableInt(0, false, true);
	public final SyncableInt energyStored = new SyncableInt(0, false, true);
	public final SyncableBool active = new SyncableBool(false, true, false, true);

	public TileGenerator() {
		setInventorySize(1);
		registerSyncableObject(burnTime);
		registerSyncableObject(burnTimeRemaining);
		registerSyncableObject(energyStored);
		registerSyncableObject(active);
		setCapacityAndTransfer(100000, 0, 1000);
	}

	@Override
	public void update() {
		super.detectAndSendChanges();
		if (worldObj.isRemote) return;

		active.value = burnTimeRemaining.value > 0 && getEnergyStored() < getMaxEnergyStored();

		if (burnTimeRemaining.value > 0 && getEnergyStored() < getMaxEnergyStored()) {
			burnTimeRemaining.value -= burnSpeed;
			energyStorage.setEnergyStored(getEnergyStored() + Math.min(burnSpeed * EPBT, getMaxEnergyStored() - getEnergyStored()));
		}
		if (burnTimeRemaining.value <= 0 && getEnergyStored() < getMaxEnergyStored()) tryRefuel();

		sendEnergyToAll();
		energyStored.value = getEnergyStored();
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
	public void writeDataToNBT(NBTTagCompound dataCompound) {
		super.writeDataToNBT(dataCompound);
		burnTime.toNBT(dataCompound);
		burnTimeRemaining.toNBT(dataCompound);
		active.toNBT(dataCompound);
	}

	@Override
	public void readDataFromNBT(NBTTagCompound dataCompound) {
		super.readDataFromNBT(dataCompound);
		burnTime.fromNBT(dataCompound);
		burnTimeRemaining.fromNBT(dataCompound);
		active.fromNBT(dataCompound);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return TileEntityFurnace.getItemBurnTime(stack) > 0;
	}
}