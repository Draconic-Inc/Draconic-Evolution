package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Created by Brandon on 25/07/2014.
 */
public class MultiblockHelper {

    public static class TileLocation extends ChunkCoordinates {
        public boolean initialized = false;

        public TileLocation() {}

        public TileLocation(int x, int y, int z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            initialized = true;
        }

        public int getXCoord() {
            return posX;
        }

        public int getYCoord() {
            return posY;
        }

        public int getZCoord() {
            return posZ;
        }

        public boolean isThisLocation(int x, int y, int z) {
            return x == posX && y == posY && z == posZ;
        }

        @Override
        public void set(int x, int y, int z) {
            initialized = true;
            super.set(x, y, z);
        }

        public void setXCoord(int x) {
            posX = x;
            initialized = true;
        }

        public void setYCoord(int y) {
            posY = y;
            initialized = true;
        }

        public void setZCoord(int z) {
            posZ = z;
            initialized = true;
        }

        public TileEntity getTileEntity(World world) {
            return world.getTileEntity(posX, posY, posZ);
        }

        public void writeToNBT(NBTTagCompound compound, String key) {
            compound.setInteger("X_" + key, posX);
            compound.setInteger("Y_" + key, posY);
            compound.setInteger("Z_" + key, posZ);
            compound.setBoolean("Init_" + key, initialized);
        }

        public void readFromNBT(NBTTagCompound compound, String key) {
            posX = compound.getInteger("X_" + key);
            posY = compound.getInteger("Y_" + key);
            posZ = compound.getInteger("Z_" + key);
            initialized = compound.getBoolean("Z_" + key);
        }
    }

    public static class TileOffset {
        public int offsetX;
        public int offsetY;
        public int offsetZ;

        public TileOffset() {}

        public TileOffset(int x, int y, int z, int offsetX, int offsetY, int offsetZ) {
            this.offsetX = x - offsetX;
            this.offsetY = y - offsetY;
            this.offsetZ = z - offsetZ;
        }

        public TileOffset(TileEntity tile1, TileEntity offsetTile) {
            this.offsetX = tile1.xCoord - offsetTile.xCoord;
            this.offsetY = tile1.yCoord - offsetTile.yCoord;
            this.offsetZ = tile1.zCoord - offsetTile.zCoord;
        }

        public int getXCoord(TileEntity tileEntity) {
            return tileEntity.xCoord - offsetX;
        }

        public int getYCoord(TileEntity tileEntity) {
            return tileEntity.yCoord - offsetY;
        }

        public int getZCoord(TileEntity tileEntity) {
            return tileEntity.zCoord - offsetZ;
        }

        public int getXCoord(int xCoord) {
            return xCoord - offsetX;
        }

        public int getYCoord(int yCoord) {
            return yCoord - offsetY;
        }

        public int getZCoord(int zCoord) {
            return zCoord - offsetZ;
        }

        public TileEntity getTileEntity(TileEntity tileEntity) {
            return tileEntity
                    .getWorldObj()
                    .getTileEntity(getXCoord(tileEntity), getYCoord(tileEntity), getZCoord(tileEntity));
        }

        public void writeToNBT(NBTTagCompound compound, String key) {
            compound.setInteger("X_" + key, offsetX);
            compound.setInteger("Y_" + key, offsetY);
            compound.setInteger("Z_" + key, offsetZ);
        }

        public void readFromNBT(NBTTagCompound compound, String key) {
            offsetX = compound.getInteger("X_" + key);
            offsetY = compound.getInteger("Y_" + key);
            offsetZ = compound.getInteger("Z_" + key);
        }
    }
}
