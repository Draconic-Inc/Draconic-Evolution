package com.brandon3055.draconicevolution.common.tileentities.energynet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyBeam;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 11/02/2015.
 */
public class LinkedEnergyDevice {

    @SideOnly(Side.CLIENT)
    public ParticleEnergyBeam beam;
    /**
     * Energy Flow to this device (range: 0D - 100D)
     */
    public double energyFlow = 0;

    public double lastTickEnergyFlow = 0;
    public int xCoord;
    public int yCoord;
    public int zCoord;

    public LinkedEnergyDevice() {}

    public LinkedEnergyDevice(int xCoord, int yCoord, int zCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }

    // public LinkedEnergyDevice(int xCoord, int yCoord, int zCoord, ParticleEnergyBeam beam) {
    // this.xCoord = xCoord;
    // this.yCoord = yCoord;
    // this.zCoord = zCoord;
    // this.beam = beam;
    // }

    public TileEntity getTile(World world) {
        return world.getTileEntity(xCoord, yCoord, zCoord);
    }

    public IRemoteEnergyHandler getEnergyTile(World world) {
        return getTile(world) instanceof IRemoteEnergyHandler ? (IRemoteEnergyHandler) getTile(world) : null;
    }

    public LinkedEnergyDevice writeToNBT(NBTTagCompound compound, String key) {
        compound.setInteger("X_" + key, xCoord);
        compound.setInteger("Y_" + key, yCoord);
        compound.setInteger("Z_" + key, zCoord);
        try {
            if (beam != null) {
                compound.setShort("Flow_" + key, (short) beam.getFlow());
            }
        } catch (Throwable e) {}
        return this;
    }

    public LinkedEnergyDevice readFromNBT(NBTTagCompound compound, String key) {
        xCoord = compound.getInteger("X_" + key);
        yCoord = compound.getInteger("Y_" + key);
        zCoord = compound.getInteger("Z_" + key);

        try {
            if (beam != null) {
                beam.setFlow(compound.getShort("Flow" + key));
            }
        } catch (Throwable e) {}
        return this;
    }

    @Override
    public String toString() {
        return "[x: " + xCoord + " y: " + yCoord + " z: " + zCoord + "]";
    }

    public boolean isValid(World world) {
        return getEnergyTile(world) != null;
    }
}
