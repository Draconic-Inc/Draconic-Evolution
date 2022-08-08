package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TilePlayerDetector extends TileEntity {
    private int tick = 0;
    public boolean output = false;
    private int scanRate = 5;
    private int range = 1;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        if (tick >= scanRate) {
            tick = 0;
            EntityPlayer player = worldObj.getClosestPlayer(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, range + 0.5D);
            if (player != null) {
                if (!output) setOutput(true);
            } else {
                if (output) setOutput(false);
            }
        } else tick++;
    }

    private void setOutput(boolean out) {
        output = out;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        updateBlocks();
    }

    public void setRange(int value) {
        this.range = value;
    }

    public int getRange() {
        return range;
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

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("OutPut", output);
        compound.setInteger("Range", range);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        output = compound.getBoolean("OutPut");
        range = compound.getInteger("Range");

        super.readFromNBT(compound);
    }
}
