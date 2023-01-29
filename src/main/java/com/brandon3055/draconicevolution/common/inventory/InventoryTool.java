package com.brandon3055.draconicevolution.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.container.ContainerAdvTool;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 13/01/2015.
 */
public class InventoryTool implements IInventory {

    public int size;
    public ItemStack inventoryItem;
    private ItemStack[] inventoryStacks;
    private EntityPlayer player;
    private ContainerAdvTool container;
    private int slot = -1;

    public InventoryTool(EntityPlayer player, ItemStack stack) {
        this.inventoryItem = stack;
        this.player = player;
        if (stack != null && stack.getItem() instanceof IInventoryTool) {
            this.size = ((IInventoryTool) stack.getItem()).getInventorySlots();
            readFromNBT(ItemNBTHelper.getCompound(stack));
        }
        this.inventoryStacks = new ItemStack[size + 5];
    }

    public void setAndReadFromStack(ItemStack stack, int slot) {
        this.slot = slot;
        this.inventoryItem = stack;
        this.size = ((IInventoryTool) stack.getItem()).getInventorySlots();
        this.inventoryStacks = new ItemStack[size + 5];
        readFromNBT(ItemNBTHelper.getCompound(inventoryItem));
    }

    public void setContainer(ContainerAdvTool container) {
        this.container = container;
    }

    @Override
    public int getSizeInventory() {
        return inventoryStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inventoryStacks[i];
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
        inventoryStacks[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return inventoryItem != null && inventoryItem.getItem() instanceof IInventoryTool
                ? !StringUtils.isNullOrEmpty(((IInventoryTool) inventoryItem.getItem()).getInventoryName())
                        ? ((IInventoryTool) inventoryItem.getItem()).getInventoryName()
                        : ""
                : "";
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
    public void markDirty() {

        for (int i = 0; i < getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
                inventoryStacks[i] = null;
            }
        }

        if (getItem() != null) {
            writeToNBT(ItemNBTHelper.getCompound(getItem()));
            readFromNBT(ItemNBTHelper.getCompound(getItem()));
        } else {
            LogHelper.error("[InventoryItem] storage item == null This is not a good thing...");
        }

        container.detectAndSendChanges();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    private ItemStack getItem() {
        if (slot != -1 && player.inventory.getStackInSlot(slot) != null
                && inventoryItem != null
                && player.inventory.getStackInSlot(slot).getItem() == inventoryItem.getItem()) {
            return player.inventory.getStackInSlot(slot);
        } else {
            LogHelper.error("Error getting inventory item [InventoryTool#getItem() - " + slot + "]");
            return null;
        }
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[size];
        NBTTagList enchList = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); i++) {
            if (i < size) {
                tag[i] = new NBTTagCompound();

                if (inventoryStacks[i] != null) {
                    tag[i] = inventoryStacks[i].writeToNBT(tag[i]);
                }

                compound.setTag("Item" + i, tag[i]);
            } else if (inventoryStacks[i] != null) {
                if (inventoryStacks[i].getTagCompound() == null) inventoryStacks[i] = null;
                else {
                    enchList.appendTag(
                            inventoryStacks[i].getTagCompound().getTagList("StoredEnchantments", 10)
                                    .getCompoundTagAt(0));
                }
            }
            compound.setTag("ench", enchList);
        }

        // for (int i = size; i < getSizeInventory(); i++)
        // {
        // if (inventoryStacks[i] != null && inventoryStacks[i].getItem() == Items.enchanted_book &&
        // inventoryStacks[i].hasTagCompound() && inventoryStacks[i].getTagCompound().hasKey("StoredEnchantments"))
        // {
        // NBTTagList enchants = inventoryStacks[i].getTagCompound().getTagList("StoredEnchantments", 10);
        // compound.setTag("ench", enchants);
        // }
        // }

        if (compound.hasKey("ench") && compound.getTagList("ench", 10).tagCount() == 0) compound.removeTag("ench");
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound[] tag = new NBTTagCompound[size];
        NBTTagList enchList = null;
        if (compound.hasKey("ench")) enchList = compound.getTagList("ench", 10);

        for (int i = 0; i < getSizeInventory(); i++) {
            inventoryStacks[i] = null;
            if (i < size) {
                tag[i] = compound.getCompoundTag("Item" + i);
                inventoryStacks[i] = ItemStack.loadItemStackFromNBT(tag[i]);
            } else if (enchList != null && enchList.tagCount() > i - size) {
                inventoryStacks[i] = new ItemStack(Items.enchanted_book);
                inventoryStacks[i].setTagCompound(new NBTTagCompound());
                NBTTagList list = new NBTTagList();
                list.appendTag(enchList.getCompoundTagAt(i - size));
                inventoryStacks[i].getTagCompound().setTag("StoredEnchantments", list);
            }
        }

        // if (compound.hasKey("ench"))
        // {
        // NBTTagList enchants = compound.getTagList("ench", 10);
        // for (int i = size; i < getSizeInventory(); i++)
        // {
        // if (!enchants.getCompoundTagAt(i - size).hasNoTags())
        // {
        // inventoryStacks[i] = new ItemStack(Items.enchanted_book);
        // NBTTagCompound compound1 = new NBTTagCompound();
        // NBTTagList list = new NBTTagList();
        // list.appendTag(enchants.getCompoundTagAt(i - size));
        // compound1.setTag("StoredEnchantments", list);
        // inventoryStacks[i].setTagCompound(compound1);
        // }
        // }
        // }

        if (compound.hasKey("ench") && compound.getTagList("ench", 10).tagCount() == 0) compound.removeTag("ench");
    }
}
