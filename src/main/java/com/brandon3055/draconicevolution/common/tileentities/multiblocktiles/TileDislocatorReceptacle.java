package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.brandon3055.brandonscore.common.utills.Teleporter;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKI;
import com.brandon3055.draconicevolution.common.utills.PortalHelper;

/**
 * Created by Brandon on 22/5/2015.
 */
public class TileDislocatorReceptacle extends TileEntity implements IInventory {

    private ItemStack dislocator;
    public PortalHelper.PortalStructure structure = null;
    public boolean isActive = false;
    public boolean updating = false;
    public int coolDown = 0;
    public int ticksTillStart = -1;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (coolDown > 0) coolDown--;
        if (ticksTillStart > -1) ticksTillStart--;
        if (ticksTillStart == 0)
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1);
    }

    public void validateActivePortal() {
        if (updating) return;
        if (structure == null) {
            isActive = false;
            return;
        }
        isActive = structure.checkFrameIsValid(worldObj, xCoord, yCoord, zCoord)
                && structure.scanPortal(worldObj, xCoord, yCoord, zCoord, false, true);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void updateState() {
        if (structure == null || !isActive)
            structure = PortalHelper.getValidStructure(worldObj, xCoord, yCoord, zCoord);
        if (structure == null) {
            isActive = false;
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return;
        }

        if (isActive) {
            if (getStackInSlot(0) == null || !structure.checkFrameIsValid(worldObj, xCoord, yCoord, zCoord))
                isActive = false;
        } else {
            boolean frameValid = structure.checkFrameIsValid(worldObj, xCoord, yCoord, zCoord);
            boolean portalEmpty = structure.scanPortal(worldObj, xCoord, yCoord, zCoord, false, false);

            if (getLocation() != null && frameValid && portalEmpty) {
                isActive = true;
                structure.scanPortal(worldObj, xCoord, yCoord, zCoord, true, false);
            }
        }
        worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public Teleporter.TeleportLocation getLocation() {
        if (getStackInSlot(0) != null && getStackInSlot(0).getItem() instanceof TeleporterMKI)
            return ((TeleporterMKI) getStackInSlot(0).getItem()).getLocation(getStackInSlot(0));
        return null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setBoolean("IsActive", isActive);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        isActive = pkt.func_148857_g().getBoolean("IsActive");
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i == 0 ? dislocator : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);

        if (itemstack != null) {
            if (itemstack.stackSize <= count) {
                setInventorySlotContents(i, null);
            } else {
                itemstack = itemstack.splitStack(count);
                if (itemstack.stackSize == 0) {
                    setInventorySlotContents(i, null);
                }
            }
        }
        if (isActive) updateState();
        else ticksTillStart = 1;
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        ItemStack item = getStackInSlot(i);
        if (item != null) setInventorySlotContents(i, null);
        return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (i != 0) return;
        dislocator = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        if (isActive) updateState();
        else ticksTillStart = 1;
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (worldObj == null) {
            return true;
        }
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
            return false;
        }
        return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() instanceof TeleporterMKI
                && ((TeleporterMKI) itemstack.getItem()).getLocation(itemstack) != null;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("IsActive", isActive);

        if (dislocator != null) {
            NBTTagCompound stack = new NBTTagCompound();
            dislocator.writeToNBT(stack);
            compound.setTag("Dislocator", stack);
        }

        if (structure != null) {
            compound.setBoolean("HasStructure", true);
            structure.writeToNBT(compound);
        }

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isActive = compound.getBoolean("IsActive");

        if (compound.hasKey("Dislocator"))
            dislocator = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Dislocator"));

        if (compound.hasKey("HasStructure")) {
            structure = new PortalHelper.PortalStructure();
            structure.readFromNBT(compound);
        }
        super.readFromNBT(compound);
    }
}
