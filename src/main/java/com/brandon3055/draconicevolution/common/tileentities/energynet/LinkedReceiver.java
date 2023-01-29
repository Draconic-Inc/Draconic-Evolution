package com.brandon3055.draconicevolution.common.tileentities.energynet;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

/**
 * Created by Brandon on 26/03/2015.
 */
public class LinkedReceiver {

    public int xCoord;
    public int yCoord;
    public int zCoord;
    public int connectionSide;
    public int particleEnergyCounter = 0;
    public Block target = null;
    private double tBBXMin = -1;
    private double tBBXMax = -1;
    private double tBBYMin = -1;
    private double tBBYMax = -1;
    private double tBBZMin = -1;
    private double tBBZMax = -1;

    public LinkedReceiver() {}

    public LinkedReceiver(int xCoord, int yCoord, int zCoord, int connectionSide) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.connectionSide = connectionSide;
    }

    public boolean isStillValid(World world) {
        TileEntity tile = world.getTileEntity(xCoord, yCoord, zCoord);
        return tile instanceof IEnergyReceiver
                && ((IEnergyReceiver) tile).canConnectEnergy(ForgeDirection.getOrientation(connectionSide));
    }

    public IEnergyReceiver getReceiver(World world) {
        return isStillValid(world) ? (IEnergyReceiver) world.getTileEntity(xCoord, yCoord, zCoord) : null;
    }

    public int receiveEnergy(World world, int maxReceive, boolean simulate) {
        if (isStillValid(world)) return getReceiver(world)
                .receiveEnergy(ForgeDirection.getOrientation(connectionSide), maxReceive, simulate);
        else return 0;
    }

    public int getEnergyStored(World world) {
        if (isStillValid(world))
            return getReceiver(world).getEnergyStored(ForgeDirection.getOrientation(connectionSide));
        else return 0;
    }

    public int getMaxEnergyStored(World world) {
        if (isStillValid(world))
            return getReceiver(world).getMaxEnergyStored(ForgeDirection.getOrientation(connectionSide));
        else return 0;
    }

    public void updateTarget(World world) {
        target = world.getBlock(xCoord, yCoord, zCoord);
        tBBXMin = target.getBlockBoundsMinX();
        tBBXMax = target.getBlockBoundsMinX();
        tBBYMin = target.getBlockBoundsMinX();
        tBBYMax = target.getBlockBoundsMinX();
        tBBZMin = target.getBlockBoundsMinX();
        tBBZMax = target.getBlockBoundsMinX();
    }

    public void writeToNBT(NBTTagCompound compound, String tag) {
        compound.setInteger(tag + "_XCoord", xCoord);
        compound.setInteger(tag + "_YCoord", yCoord);
        compound.setInteger(tag + "_ZCoord", zCoord);
        compound.setInteger(tag + "_Side", connectionSide);
    }

    public void readFromNBT(NBTTagCompound compound, String tag) {
        xCoord = compound.getInteger(tag + "_XCoord");
        yCoord = compound.getInteger(tag + "_YCoord");
        zCoord = compound.getInteger(tag + "_ZCoord");
        connectionSide = compound.getInteger(tag + "_Side");
    }
}
