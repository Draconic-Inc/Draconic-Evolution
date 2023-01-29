package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModItems;

/**
 * Created by Brandon on 27/08/2014.
 */
public class TileCKeyStone extends TileEntity {

    public boolean isActivated = false;
    private int keyCode = 0;
    private int activeTicks = 0;
    public int delay = 5;

    @Override
    public void updateEntity() {
        if (isActivated && (getMeta() == 1 || getMeta() == 3)) {
            activeTicks++;
            if (activeTicks >= delay) {
                activeTicks = 0;
                isActivated = false;
                updateBlocks();
                worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.5F);
            }
        }
    }

    public boolean onActivated(ItemStack stack, EntityPlayer player) {
        if (stack == null || stack.getItem() != ModItems.key) {
            if (player.capabilities.isCreativeMode && stack == null
                    && player.isSneaking()
                    && (getMeta() == 1 || getMeta() == 3))
                delay += 5;
            if (player.capabilities.isCreativeMode && stack == null) giveInformation(player);
            return false;
        }
        if (isMasterKey(stack)) return true;
        if (setKey(stack)) return true;
        if (!isKeyValid(stack, player)) return false;

        switch (getMeta()) {
            case 0: // Permanent Activation
                isActivated = true;
                player.destroyCurrentEquippedItem();
                worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.6F);
                break;
            case 1: // Button Activation
                isActivated = true;
                worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.6F);
                break;
            case 2: // Toggle Activation
                if (!isActivated) worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.6F);
                else worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.5F);
                isActivated = !isActivated;
                break;
            case 3: // Button Activation (Consume Key)
                isActivated = true;
                player.destroyCurrentEquippedItem();
                worldObj.playSoundEffect(
                        (double) xCoord + 0.5D,
                        (double) yCoord + 0.5D,
                        (double) zCoord + 0.5D,
                        "random.click",
                        0.3F,
                        0.6F);
                break;
            case 4:
                break;
        }

        updateBlocks();
        return true;
    }

    private void giveInformation(EntityPlayer player) {
        if (worldObj.isRemote) {
            player.addChatComponentMessage(new ChatComponentTranslation("msg.cKeyStoneType" + getMeta() + ".txt"));
            player.addChatComponentMessage(new ChatComponentText("Key: " + keyCode));
            if (getMeta() == 1 || getMeta() == 3) player.addChatComponentMessage(
                    new ChatComponentText("Delay: " + delay + "t (" + (((double) delay) / 20D) + "s)"));
        }
    }

    private boolean isMasterKey(ItemStack key) {
        if (key.getItemDamage() == 1) {
            if (!isActivated) worldObj.playSoundEffect(
                    (double) xCoord + 0.5D,
                    (double) yCoord + 0.5D,
                    (double) zCoord + 0.5D,
                    "random.click",
                    0.3F,
                    0.6F);
            else worldObj.playSoundEffect(
                    (double) xCoord + 0.5D,
                    (double) yCoord + 0.5D,
                    (double) zCoord + 0.5D,
                    "random.click",
                    0.3F,
                    0.5F);
            isActivated = !isActivated;
            updateBlocks();
            return true;
        }
        return false;
    }

    private boolean setKey(ItemStack key) {
        if (keyCode == 0 && ItemNBTHelper.getInteger(key, "KeyCode", 0) == 0) {
            keyCode = worldObj.rand.nextInt();
            ItemNBTHelper.setInteger(key, "KeyCode", keyCode);
            ItemNBTHelper.setInteger(key, "X", xCoord);
            ItemNBTHelper.setInteger(key, "Y", yCoord);
            ItemNBTHelper.setInteger(key, "Z", zCoord);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return true;
        } else if (keyCode == 0 && ItemNBTHelper.getInteger(key, "KeyCode", 0) != 0) {
            keyCode = ItemNBTHelper.getInteger(key, "KeyCode", 0);
            ItemNBTHelper.setInteger(key, "LockCount", ItemNBTHelper.getInteger(key, "LockCount", 0) + 1);
            ItemNBTHelper.setInteger(key, "X_" + ItemNBTHelper.getInteger(key, "LockCount", 0), xCoord);
            ItemNBTHelper.setInteger(key, "Y_" + ItemNBTHelper.getInteger(key, "LockCount", 0), yCoord);
            ItemNBTHelper.setInteger(key, "Z_" + ItemNBTHelper.getInteger(key, "LockCount", 0), zCoord);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return true;
        }
        return false;
    }

    private boolean isKeyValid(ItemStack key, EntityPlayer player) {
        if (ItemNBTHelper.getInteger(key, "KeyCode", 0) != keyCode) {
            if (worldObj.isRemote) player.addChatComponentMessage(new ChatComponentTranslation("msg.wrongKey.txt"));
            return false;
        }
        return true;
    }

    private int getMeta() {
        return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    }

    public int getKeyCode() {
        return keyCode;
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

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("IsActivated", isActivated);
        compound.setInteger("KeyCode", keyCode);
        compound.setInteger("Delay", delay);
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isActivated = compound.getBoolean("IsActivated");
        keyCode = compound.getInteger("KeyCode");
        delay = compound.getInteger("Delay");
        super.readFromNBT(compound);
    }

    public void updateBlocks() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, worldObj.getBlock(xCoord, yCoord, zCoord));
    }
}
