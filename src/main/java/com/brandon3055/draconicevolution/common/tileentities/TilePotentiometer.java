package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TilePotentiometer extends TileEntity {

    public int power = 0;

    public void increasePower() {
        if (power < 15) {
            power++;
            worldObj.playSound(xCoord, yCoord, zCoord, "random.click", 1, 0.5F + ((float) power / 15F), false);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        updateBlocks();
    }

    public void decreasePower() {
        if (power > 0) {
            power--;
            worldObj.playSound(xCoord, yCoord, zCoord, "random.click", 1, 0.5F + ((float) power / 15F), false);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        updateBlocks();
    }

    public void updateBlocks() {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));

        if (meta == 1) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord - 1,
                    yCoord,
                    zCoord,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else if (meta == 2) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord + 1,
                    yCoord,
                    zCoord,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else if (meta == 3) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord,
                    yCoord,
                    zCoord - 1,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else if (meta == 4) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord,
                    yCoord,
                    zCoord + 1,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else if (meta == 5) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord,
                    yCoord + 1,
                    zCoord,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else if (meta == 6) {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord,
                    yCoord - 1,
                    zCoord,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        } else {
            worldObj.notifyBlocksOfNeighborChange(
                    xCoord,
                    yCoord - 1,
                    zCoord,
                    worldObj.getBlock(xCoord, yCoord, zCoord));
        }
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        power = compound.getInteger("Power");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Power", power);
        super.writeToNBT(compound);
    }
}
