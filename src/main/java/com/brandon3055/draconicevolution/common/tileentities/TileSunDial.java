package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileSunDial extends TileEntity implements IInventory {

    int tick;
    int tick2;
    public boolean running = false;

    @Override
    public void updateEntity() {
        CheckPower();
        if (running) {
            tick++;
            tick2++;
            Runn();
        }
    }

    private void CheckPower() {
        if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) running = true;
        else {
            if (running) {
                tick = 0;
                tick2 = 0;
                running = false;
                worldObj.playSoundEffect(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D,
                        "draconicevolution:discharge",
                        5F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    private void Runn() {
        if (tick == 1) {
            worldObj.playSoundEffect(
                    xCoord + 0.5D,
                    yCoord + 0.5D,
                    zCoord + 0.5D,
                    "draconicevolution:charge",
                    10F,
                    worldObj.rand.nextFloat() * 0.1F + 0.9F);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        if (tick < 74) {
            worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 0.1, zCoord, 0D, 0D, -0.01D);
            worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 0.1, zCoord + 1, 0D, 0D, 0.01D);
            worldObj.spawnParticle("explode", xCoord + 0, yCoord + 0.1, zCoord + 0.5, -0.01D, 0D, 0D);
            worldObj.spawnParticle("explode", xCoord + 1, yCoord + 0.1, zCoord + 0.5, 0.01D, 0D, 0D);
        }
        if (tick > 74 && tick2 == 1) {
            worldObj.spawnParticle(
                    "flame",
                    xCoord + worldObj.rand.nextFloat(),
                    yCoord + worldObj.rand.nextFloat(),
                    zCoord,
                    0D,
                    0D,
                    -0.01D);
            worldObj.spawnParticle(
                    "flame",
                    xCoord + worldObj.rand.nextFloat(),
                    yCoord + worldObj.rand.nextFloat(),
                    zCoord + 1,
                    0D,
                    0D,
                    0.01D);
            worldObj.spawnParticle(
                    "flame",
                    xCoord + 0,
                    yCoord + worldObj.rand.nextFloat(),
                    zCoord + worldObj.rand.nextFloat(),
                    -0.01D,
                    0D,
                    0D);
            worldObj.spawnParticle(
                    "flame",
                    xCoord + 1,
                    yCoord + worldObj.rand.nextFloat(),
                    zCoord + worldObj.rand.nextFloat(),
                    0.01D,
                    0D,
                    0D);
        }
        if (tick > 74) Track(getTime());
        if (tick > 74) worldObj.playSoundEffect(
                xCoord + 0.5D,
                yCoord + 0.5D,
                zCoord + 0.5D,
                "draconicevolution:beam",
                5F,
                worldObj.rand.nextFloat() * 0.1F + 0.9F);
        if (tick > 74) worldObj.playSoundEffect(
                xCoord + 0.5D,
                yCoord + 0.5D,
                zCoord + 0.5D,
                "draconicevolution:boom",
                20F,
                worldObj.rand.nextFloat() * 0.1F + 0.9F);
        if (tick2 == 5) tick2 = 0;
    }

    private void Track(final double t) {
        double time = t;
        if ((time >= 0 && time <= 5) || (time >= 19.3 && time <= 20)) {
            if (time > 5) {
                Beam(1, (time - 19.33) / 3);
            } else {
                final double x = 5 - time;
                final double y = 1 * time + 1.2;
                final double x1 = map(x, 0, 5, 0, 1);
                final double y1 = map(y, 0, 5, 0, 1);
                Beam(x1, y1);
            }
        } else if (time >= 5 && time <= 10.63) {
            final double x = (time - 5) * -1;
            final double y = 6.2 - (time - 5);
            final double x1 = map(x, 0, 5, 0, 1);
            final double y1 = map(y, 0, 5, 0, 1);
            if (time > 10) Beam(x1, y1 - ((time - 10) / 4.5));
            else Beam(x1, y1);
        } else if (time > 10.63 && time < 15) {
            final double comp = time - 10.63;
            time = (time - 10) - (0.63 - (comp / 7.936));
            final double x = time - 5;
            final double y = 0 - time;
            final double x1 = map(x, 0, 5, 0, 1);
            final double y1 = map(y, 0, 5, 0, 1);
            Beam(x1, y1);
        } else if (time > 15 && time < 19.3) {
            final double comp = time - 15;
            time = time + comp / 6;
            final double x = 0 + (time - 15);
            final double y = -5 + (time - 15);
            final double x1 = map(x, 0, 5, 0, 1);
            final double y1 = map(y, 0, 5, 0, 1);
            Beam(x1, y1);
        }
    }

    private void Beam(final double x, final double y) {
        long worldTime = worldObj.getWorldTime();
        worldTime = worldTime + 30;
        worldObj.setWorldTime(worldTime);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 2, y * 2, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 3, y * 3, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 4, y * 4, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 5, y * 5, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 6, y * 6, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 7, y * 7, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 8, y * 8, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 9, y * 9, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 10, y * 10, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 19.3, y * 19.3, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 19.5, y * 19.5, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 19.7, y * 19.7, 0D);
        worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, x * 20, y * 20, 0D);
        worldObj.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, 0D, 0D, 0D);
        worldObj.spawnParticle("explode", xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, 0D, 0D, 0D);
    }

    private double getTime() {
        final double days = worldObj.getWorldTime() / 24000;
        final double time = worldObj.getWorldTime() - days * 24000;
        final double time2 = time / 1.2;
        final double time3 = Math.round(time2 / 10);
        final double time4 = time3 / 100;
        return time4;
    }

    private double map(final double x, final double in_min, final double in_max, final double out_min,
            final double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    // ###################################Inventory#####################################################//
    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {}

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
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
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
}
