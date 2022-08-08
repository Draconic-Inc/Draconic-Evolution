package com.brandon3055.draconicevolution.common.tileentities;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergy;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.EnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 27/06/2014.
 */
public class TileEnergyInfuser extends TileObjectSync implements IEnergyReceiver, ISidedInventory {
    ItemStack[] items = new ItemStack[1];
    public EnergyStorage energy =
            new EnergyStorage(BalanceConfigHandler.energyInfuserStorage, BalanceConfigHandler.energyInfuserMaxTransfer);
    public boolean running = false;
    public boolean runningCach = false;
    private int tick = 0;
    public float rotation = 0;
    /**
     * True is energy was transferred this tick
     */
    public boolean transfer = false;

    public boolean transferCach = false;

    // ==============================================LOGIC=======================================================//

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            if (running) {
                rotation += 0.5F;
                if (rotation > 360F) {
                    rotation = 0;
                }
                spawnParticles();
            }
            return;
        }

        if (tick % 100 == 0) tryStartOrStop();
        if (tick % 400 == 0) detectAndSendChanges(true);

        if (running && tryStartOrStop()) {
            IEnergyContainerItem item = (IEnergyContainerItem) items[0].getItem();
            setTransfer(energy.extractEnergy(item.receiveEnergy(items[0], energy.getEnergyStored(), false), false) > 0);
        } else setTransfer(false);

        detectAndSendChanges(false);
        tick++;
    }

    private boolean tryStartOrStop() {
        if (items[0] != null
                && items[0].stackSize == 1
                && items[0] != null
                && items[0].getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem item = (IEnergyContainerItem) items[0].getItem();
            if (item.getEnergyStored(items[0]) < item.getMaxEnergyStored(items[0])) {
                running = true;
            } else {
                running = false;
            }
        } else {
            running = false;
        }

        return running;
    }

    private void setTransfer(boolean t) {
        transfer = t;
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles() {
        if (worldObj.isRemote && running && transfer) {
            Random rand = worldObj.rand;
            double rotationF;
            double yRand;
            double radRand;
            ParticleEnergy particle;
            float y = 0.6f;

            yRand = ((rand.nextFloat() - 0.5) / 2);
            radRand = 1 - rand.nextFloat() / 2;
            rotationF = rotation / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y + yRand,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    1);
            ParticleHandler.spawnCustomParticle(particle);

            yRand = ((rand.nextFloat() - 0.5) / 2);
            radRand = 1 - rand.nextFloat() / 2;
            rotationF = (rotation + 90) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y + yRand,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    1);
            ParticleHandler.spawnCustomParticle(particle);

            yRand = ((rand.nextFloat() - 0.5) / 2);
            radRand = 1 - rand.nextFloat() / 2;
            rotationF = (rotation + 180) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y + yRand,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    1);
            ParticleHandler.spawnCustomParticle(particle);

            yRand = ((rand.nextFloat() - 0.5) / 2);
            radRand = 1 - rand.nextFloat() / 2;
            rotationF = (rotation + 270) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y + yRand,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    1);
            ParticleHandler.spawnCustomParticle(particle);

            y = 0.79f;
            radRand = 0.35;
            rotationF = rotation / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    0);
            ParticleHandler.spawnCustomParticle(particle);

            rotationF = (rotation + 90) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    0);
            ParticleHandler.spawnCustomParticle(particle);

            rotationF = (rotation + 180) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    0);
            ParticleHandler.spawnCustomParticle(particle);

            rotationF = (rotation + 270) / 57F;
            particle = new ParticleEnergy(
                    worldObj,
                    xCoord + 0.5 + radRand * Math.sin(rotationF),
                    yCoord + y,
                    zCoord + 0.5 + radRand * Math.cos(rotationF),
                    xCoord + 0.5,
                    yCoord + 0.7,
                    zCoord + 0.5,
                    0);
            ParticleHandler.spawnCustomParticle(particle);
        }
    }
    // ==============================================ENERGY======================================================//

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return this.energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    // ==========================================SYNCHRONIZATION==================================================//

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

    public void detectAndSendChanges(boolean sendAnyway) {
        if (runningCach != running || sendAnyway) {
            runningCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 0, running);
        }
        if (transferCach != transfer || sendAnyway) {
            transferCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 1, transfer);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveObjectFromServer(int index, Object object) {
        if (index == 0) running = (Boolean) object;
        if (index == 1) transfer = (Boolean) object;
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
        tryStartOrStop();
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
    public void openInventory() {
        System.out.println("open");
    }

    @Override
    public void closeInventory() {
        System.out.println("close");
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return true;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        if (item == null || !(item.getItem() instanceof IEnergyContainerItem)) return true;

        if ((((IEnergyContainerItem) item.getItem()).getEnergyStored(item)
                        >= ((IEnergyContainerItem) item.getItem()).getMaxEnergyStored(item))
                || ((IEnergyContainerItem) item.getItem()).receiveEnergy(item, 1, true) == 0) return true;
        else return false;
    }

    // ===========================================================================================================//

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
        compound.setBoolean("Running", running);
        compound.setBoolean("Transfer", transfer);
        energy.writeToNBT(compound);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[items.length];

        for (int i = 0; i < items.length; i++) {
            tag[i] = compound.getCompoundTag("Item" + i);
            items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
        }
        running = compound.getBoolean("Running");
        transfer = compound.getBoolean("Transfer");
        energy.readFromNBT(compound);

        super.readFromNBT(compound);
    }
}
