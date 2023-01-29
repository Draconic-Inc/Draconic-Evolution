package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;

public class TileEarth extends TileObjectSync {

    // Prevent culling when block is out of frame so model can remain active.
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    private static final int maxRotationSpeed = 64;
    private static final int maxSize = 200;

    private int size = 1;
    private int rotationSpeed = 0;

    public void incrementSize() {
        size++;
    }

    public void increaseRotationSpeed() {
        rotationSpeed++;
    }

    public int getSize() {
        return size % maxSize;
    }

    public int getRotationSpeed() {
        return rotationSpeed % maxRotationSpeed;
    }

    private static final String rotationSpeedNBTTag = "DE:rotationSpeed";
    private static final String sizeNBTTag = "DE:size";

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(rotationSpeedNBTTag, rotationSpeed);
        compound.setInteger(sizeNBTTag, size);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        rotationSpeed = compound.getInteger(rotationSpeedNBTTag);
        size = compound.getInteger(sizeNBTTag);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}
