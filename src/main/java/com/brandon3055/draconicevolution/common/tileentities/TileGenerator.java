package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.EnergyStorage;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileGenerator extends TileObjectSync implements ISidedInventory, IEnergyProvider {

    // ########### variables #############//
    private ItemStack[] items;
    public int burnTime = 1;
    public int burnTimeRemaining = 0;
    private int burnSpeed = 6;
    public boolean isBurning = false;
    public boolean isBurningCach = false;
    private int tick;
    /**
     * Energy per burn tick
     */
    private int EPBT = 14;

    public EnergyStorage storage = new EnergyStorage(100000, 0, 1000);

    // ##################################//

    public TileGenerator() {
        items = new ItemStack[1];
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        isBurning = burnTimeRemaining > 0 && storage.getEnergyStored() < storage.getMaxEnergyStored();

        if (burnTimeRemaining > 0 && storage.getEnergyStored() < storage.getMaxEnergyStored()) {
            burnTimeRemaining -= burnSpeed;
            storage.setEnergyStored(
                    storage.getEnergyStored()
                            + Math.min(burnSpeed * EPBT, storage.getMaxEnergyStored() - storage.getEnergyStored()));
        } else if (burnTimeRemaining <= 0) tryRefuel();

        if ((storage.getEnergyStored() > 0)) {
            for (int i = 0; i < 6; i++) {
                TileEntity tile = worldObj.getTileEntity(
                        xCoord + ForgeDirection.getOrientation(i).offsetX,
                        yCoord + ForgeDirection.getOrientation(i).offsetY,
                        zCoord + ForgeDirection.getOrientation(i).offsetZ);
                if (tile != null && tile instanceof IEnergyReceiver) {
                    storage.extractEnergy(
                            ((IEnergyReceiver) tile).receiveEnergy(
                                    ForgeDirection.getOrientation(i).getOpposite(),
                                    storage.extractEnergy(storage.getMaxExtract(), true),
                                    false),
                            false);
                }
            }
        }

        detectAndSentChanges(tick % 500 == 0);
        tick++;
    }

    public void tryRefuel() {
        if (burnTimeRemaining > 0 || storage.getEnergyStored() >= storage.getMaxEnergyStored()) return;
        if (items[0] != null && items[0].stackSize > 0) {
            int itemBurnTime = getItemBurnTime(items[0]);

            if (itemBurnTime > 0) {
                --items[0].stackSize;
                if (this.items[0].stackSize == 0) {
                    this.items[0] = items[0].getItem().getContainerItem(items[0]);
                }
                burnTime = itemBurnTime;
                burnTimeRemaining = itemBurnTime;
            }
        }
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab) {
                    return 150;
                }

                if (block.getMaterial() == Material.wood) {
                    return 300;
                }

                if (block == Blocks.coal_block) {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items[i];
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
        items[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
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
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (worldObj == null) {
            return true;
        }
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
            return false;
        }
        return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.4) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return new int[1];
    }

    @Override
    public boolean canInsertItem(int var1, ItemStack var2, int var3) {
        return true;
    }

    @Override
    public boolean canExtractItem(int var1, ItemStack var2, int var3) {
        return getItemBurnTime(var2) == 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = new NBTTagCompound();

            if (items[i] != null) {
                tag[i] = items[i].writeToNBT(tag[i]);
            }

            compound.setTag("Item" + i, tag[i]);
        }

        compound.setInteger("BurnTime", burnTime);
        compound.setInteger("BurnTimeRemaining", burnTimeRemaining);
        storage.writeToNBT(compound);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = compound.getCompoundTag("Item" + i);
            items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
        }

        burnTime = compound.getInteger("BurnTime");
        burnTimeRemaining = compound.getInteger("BurnTimeRemaining");
        storage.readFromNBT(compound);

        super.readFromNBT(compound);
    }

    private void detectAndSentChanges(boolean sendAnyway) {
        if (isBurning != isBurningCach || sendAnyway)
            isBurningCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 0, isBurning);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveObjectFromServer(int index, Object object) {
        if (isBurning != (Boolean) object) {
            isBurning = (Boolean) object;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    /* IEnergyHandler */
    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }
}
