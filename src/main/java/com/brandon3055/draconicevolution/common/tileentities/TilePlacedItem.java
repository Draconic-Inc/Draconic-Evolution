package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.brandon3055.draconicevolution.common.ModBlocks;

/**
 * Created by Brandon on 14/08/2014.
 */
public class TilePlacedItem extends TileEntity {

    public ItemStack stack;
    public float rotation = 0F;
    private boolean hasUpdated = false;

    @Override
    public void updateEntity() {
        if (!hasUpdated && stack != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            hasUpdated = true;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, ModBlocks.placedItem, 20);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound[] tag = new NBTTagCompound[1];
        tag[0] = new NBTTagCompound();
        if (stack != null) tag[0] = stack.writeToNBT(tag[0]);
        compound.setTag("Item" + 0, tag[0]);
        compound.setFloat("Rotation", rotation);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound[] tag = new NBTTagCompound[1];
        tag[0] = compound.getCompoundTag("Item" + 0);
        stack = ItemStack.loadItemStackFromNBT(tag[0]);
        rotation = compound.getFloat("Rotation");
    }
}
