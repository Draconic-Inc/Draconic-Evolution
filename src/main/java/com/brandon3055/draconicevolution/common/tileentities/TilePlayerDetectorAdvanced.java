package com.brandon3055.draconicevolution.common.tileentities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.brandon3055.draconicevolution.common.container.ContainerPlayerDetector;

public class TilePlayerDetectorAdvanced extends TileEntity implements IInventory {

    public String[] names = new String[42];
    private ItemStack[] items;
    public boolean whiteList = false;
    public int range = 10;
    private int tick = 0;
    private int scanRate = 5;
    public boolean output = false;
    public boolean outputInverted = false;
    private List<EntityLiving> EntityList;

    public TilePlayerDetectorAdvanced() {
        for (int i = 0; i < names.length; i++) if (names[i] == null) names[i] = "";

        items = new ItemStack[1];
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        if (tick >= scanRate) {

            tick = 0;

            if (shouldEmit()) {
                if (!output) setOutput(true);
            } else {
                if (output) setOutput(false);
            }
        } else tick++;
    }

    public boolean shouldEmit() {
        findEntitys();

        boolean b = false;
        Iterator<EntityLiving> i = EntityList.iterator();
        while (i.hasNext()) {
            Entity ent = i.next();
            if (!(ent instanceof EntityPlayer)) return false;

            String name = ((EntityPlayer) ent).getCommandSenderName();
            if (whiteList) {
                if (isPlayerListed(name)) return true;
            } else {
                if (!isPlayerListed(name)) return true;
            }
        }
        return b;
    }

    private void findEntitys() {
        double x1 = xCoord + 0.5 - range;
        double y1 = yCoord + 0.5 - range;
        double z1 = zCoord + 0.5 - range;
        double x2 = xCoord + 0.5 + range;
        double y2 = yCoord + 0.5 + range;
        double z2 = zCoord + 0.5 + range;

        // System.out.println(x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2);

        // ScanBox = AxisAlignedBB.getBoundingBox(x1, y1, z1, x2, y2, z2);
        // ScanBox = AxisAlignedBB.getAABBPool().getAABB(xCoord + 0.5 - range, yCoord + 0.5 - range, zCoord + 0.5 -
        // range, xCoord + 0.5 + range, yCoord + 0.5 + range, zCoord + 0.5 + range);
        EntityList = worldObj
                .getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x1, y1, z1, x2, y2, z2));
        // System.out.println(EntityList);
    }

    private void setOutput(boolean out) {
        output = out;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        updateBlocks();
    }

    public void updateBlocks() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, worldObj.getBlock(xCoord, yCoord, zCoord));
    }

    public boolean isPlayerListed(String name) {
        if (name == null) return false;

        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) return true;
        }
        return false;
    }

    // ###################INVENTORY###########################
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.4) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }

    // ######################################################

    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        this.writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public Container getGuiContainer(InventoryPlayer inventoryplayer) {
        return new ContainerPlayerDetector(inventoryplayer, this);
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

        for (int i = 0; i < names.length; i++) {
            String name = (names[i] != null) ? names[i] : "";
            compound.setString("Name_" + i, name);
        }

        compound.setBoolean("WhiteList", whiteList);
        compound.setBoolean("Output", output);
        compound.setInteger("Range", range);
        compound.setBoolean("OutputInverted", outputInverted);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = compound.getCompoundTag("Item" + i);
            items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
        }

        for (int i = 0; i < names.length; i++) names[i] = compound.getString("Name_" + i);

        whiteList = compound.getBoolean("WhiteList");
        range = compound.getInteger("Range");
        output = compound.getBoolean("Output");
        outputInverted = compound.getBoolean("OutputInverted");

        super.readFromNBT(compound);
    }
}
