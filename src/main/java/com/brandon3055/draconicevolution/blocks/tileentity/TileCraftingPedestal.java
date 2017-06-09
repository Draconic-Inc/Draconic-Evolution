package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableInt;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingPedestal;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.blocks.machines.CraftingPedestal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileCraftingPedestal extends TileInventoryBase implements IEnergyReceiver, ICraftingPedestal {

    public final SyncableByte facing = new SyncableByte((byte)0, true, false, true);
    private final SyncableInt energy = new SyncableInt(0, true, false);
    private final SyncableVec3I lastCorePos = new SyncableVec3I(new Vec3I(0, 0, 0), true, false);
    public IFusionCraftingInventory currentCraftingInventory = null;
    private int chargeSpeedModifier = 300;

    public TileCraftingPedestal(){
        this.setInventorySize(1);
        registerSyncableObject(facing, true);
        registerSyncableObject(energy, true);
        registerSyncableObject(lastCorePos, true);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void updateBlock() {
        super.updateBlock();
        detectAndSendChanges();
    }

    //region IEnergy

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        validateCraftingInventory();
        if (currentCraftingInventory != null){
            int maxRFPerTick = currentCraftingInventory.getRequiredCharge() / chargeSpeedModifier;
            int maxAccept = Math.min(maxReceive, Math.min(currentCraftingInventory.getRequiredCharge() - energy.value, maxRFPerTick));

            if (!simulate){
                energy.value += maxAccept;
            }

            detectAndSendChanges();
            return maxAccept;
        }

        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energy.value;
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
        String tier = getState(DEFeatures.craftingPedestal).getValue(CraftingPedestal.TIER);
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
        if (craftingInventory == null) {
            currentCraftingInventory = null;
            return false;
        }
        if (validateCraftingInventory() && !worldObj.isRemote) {
            return false;
        }
        currentCraftingInventory = craftingInventory;
        lastCorePos.vec = new Vec3I(((TileEntity) craftingInventory).getPos());
        chargeSpeedModifier = 300 - (getPedestalTier() * 80);
        return true;
    }

    @Override
    public EnumFacing getDirection() {
        return EnumFacing.getFront(facing.value);
    }

    @Override
    public int getCharge() {
        return energy.value;
    }

    private boolean validateCraftingInventory(){
        if (getStackInPedestal() != null && currentCraftingInventory != null && currentCraftingInventory.craftingInProgress() && !((TileEntity)currentCraftingInventory).isInvalid()){
            return true;
        }

        currentCraftingInventory = null;
        return false;
    }


    @Override
    public void onCraft() {
        if (currentCraftingInventory != null){
            energy.value = 0;
        }
    }

    //endregion

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);

        TileEntity tile = worldObj.getTileEntity(lastCorePos.vec.getPos());
        if (tile instanceof IFusionCraftingInventory) {
            worldObj.notifyNeighborsOfStateChange(tile.getPos(), tile.getBlockType());
        }

        updateBlock();
    }
}
