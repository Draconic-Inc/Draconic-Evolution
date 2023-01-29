package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.WorldInfo;

import com.brandon3055.draconicevolution.DraconicEvolution;

public class TileWeatherController extends TileEntity implements IInventory {

    int tick = 0;
    boolean running = false;
    public boolean lastTickInput = false;
    public int mode = 0;
    private static final boolean debug = DraconicEvolution.debug;

    private ItemStack[] items;
    public int charges;

    public TileWeatherController() {
        items = new ItemStack[1];
    }

    @Override
    public void updateEntity() {
        if (charges == 0 && !worldObj.isRemote) reload();

        WorldInfo worldinfo = worldObj.getWorldInfo();
        toggleDownfall(worldinfo);
    }

    public void activate() {
        WorldInfo worldinfo = worldObj.getWorldInfo();
        if (!running && charges > 0) {
            if (mode == 0 && worldinfo.isRaining()) running = true;
            else if (mode == 1 && !worldinfo.isRaining()) running = true;
            else if (mode == 2 && !worldinfo.isRaining()) running = true;
        }
    }

    private void reload() {
        ItemStack fuel = getStackInSlot(0);

        if (charges == 0 && fuel != null) {

            if (fuel.stackSize > 0 && fuel.isItemEqual(new ItemStack(Items.emerald))) {
                addCharge(10);
            }
        }
    }

    private void toggleDownfall(final WorldInfo worldinfo) {
        if (running) {

            if (tick == 1 && !worldObj.isRemote) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            if (tick < 70 && worldObj.isRemote) {
                worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, 0D, 3D, 0D);
                worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, 0D, 5D, 0D);
                worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1, zCoord + 0.5, 0D, 1D, 0D);
                worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1, zCoord + 0.5, 0D, 2D, 0D);
                worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1, zCoord + 0.5, 0D, 1D, 0D);
                worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, 0D, -0.3D, 0D);
                worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, 0D, -0.3D, 0D);
                worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, 0D, -0.3D, 0D);

                worldObj.spawnParticle(
                        "flame",
                        xCoord + 0.2,
                        yCoord + 1,
                        zCoord + 0.8,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5,
                        1.7D,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5);
                worldObj.spawnParticle(
                        "flame",
                        xCoord + 0.8,
                        yCoord + 1,
                        zCoord + 0.8,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5,
                        1.7D,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5);
                worldObj.spawnParticle(
                        "flame",
                        xCoord + 0.2,
                        yCoord + 1,
                        zCoord + 0.2,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5,
                        1.7D,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5);
                worldObj.spawnParticle(
                        "flame",
                        xCoord + 0.8,
                        yCoord + 1,
                        zCoord + 0.2,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5,
                        1.7D,
                        (worldObj.rand.nextFloat() - 0.5) * 1.5);
                worldObj.playSound(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        "mob.ghast.fireball",
                        10F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F,
                        false);
            }
            if (tick > 80 && tick < 90) {
                if (!worldObj.isRemote) {
                    if (mode == 0) worldinfo.setRaining(false);
                    else if (mode == 1) worldinfo.setRaining(true);
                    else if (mode == 2) {
                        worldinfo.setRaining(true);
                        worldinfo.setThundering(true);
                    }
                }
                worldObj.playSoundEffect(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        "draconicevolution:boom",
                        10F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (tick > 110 && worldObj.isRemote) {

                worldObj.playSound(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        "random.fizz",
                        10F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F,
                        false);
                worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 0, zCoord + 0, 0D, 0D, 0D);
                worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 0, zCoord + 1, 0D, 0D, 0D);
                worldObj.spawnParticle("explode", xCoord + 0, yCoord + 0, zCoord + 0.5, 0D, 0D, 0D);
                worldObj.spawnParticle("explode", xCoord + 1, yCoord + 0, zCoord + 0.5, 0D, 0D, 0D);
            }
            if (tick > 130) {
                tick = 0;
                running = false;
                if (!worldObj.isRemote) useCharge();
            }
            tick++;
        }
    }

    private void addCharge(int count) {
        if (!worldObj.isRemote) {
            charges = 10;
            decrStackSize(0, 1);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void useCharge() {
        if (!worldObj.isRemote) charges--;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        this.writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    // ==============================================INVENTORY====================================================//

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
        return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack.isItemEqual(new ItemStack(Items.emerald));
    }

    // ############################################################################################################//
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

        compound.setInteger("Tick", tick);
        compound.setShort("Charges", (short) charges);
        compound.setBoolean("Running", running);
        compound.setShort("Mode", (short) mode);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = compound.getCompoundTag("Item" + i);
            items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
        }

        tick = compound.getInteger("Tick");
        charges = compound.getShort("Charges");
        running = compound.getBoolean("Running");
        mode = compound.getShort("Mode");

        super.readFromNBT(compound);
    }

    public void reciveButtonEvent(byte buttonId) {
        if (buttonId == 0) {
            if (mode < 2) mode++;
            else mode = 0;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
