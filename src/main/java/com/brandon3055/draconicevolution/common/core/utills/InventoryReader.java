package com.brandon3055.draconicevolution.common.core.utills;

import com.brandon3055.draconicevolution.common.items.tools.Reader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryReader implements IInventory
{
	private World world;
	private EntityPlayer player;
	private ItemStack reader;
	private ItemStack[] inventory;
	public InventoryReader(EntityPlayer player) {
		this.player = player;
		this.world = player.worldObj;
		this.reader = player.getCurrentEquippedItem();
		if(this.inventory == null) {
			this.inventory = loadItemInventory(21);
		}
	}
	
	public InventoryReader(World world, ItemStack stack) {
		this.world = world;
		this.reader = stack;
		if(this.inventory == null) {
			this.inventory = loadItemInventory(21);
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (inventory[i] != null) {
			if (inventory[i].stackSize <= j) {
				ItemStack itemstack = inventory[i];
				inventory[i] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = inventory[i].splitStack(j);
			if (inventory[i].stackSize == 0) {
				inventory[i] = null;
			}
			markDirty();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return getStackInSlot(var1);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
		inventory[i] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return null;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		saveItemInventory();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return true;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{	
	}
	

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2)
	{
		return false;
	}
	
	public ItemStack[] loadItemInventory(int size)  {
		inventory = new ItemStack[size];
		if(!world.isRemote) {
			ItemStack reader = player != null? player.getCurrentEquippedItem(): this.reader;
			if(reader != null && reader.getItem() instanceof Reader) {
				NBTTagCompound inventoryTagCompound = reader.hasTagCompound() ? reader.stackTagCompound: new NBTTagCompound();
				NBTTagList inventoryTagList = inventoryTagCompound.getTagList("Inventory", 10);

				if (inventoryTagList != null) {
					ItemStack[] inventory = new ItemStack[size];
					for (int i = 0; i < inventoryTagList.tagCount(); i++) {
						NBTTagCompound itemTagCompound = inventoryTagList.getCompoundTagAt(i);
						byte byte0 = itemTagCompound.getByte("Slot");
						if (byte0 >= 0 && byte0 < inventory.length) {
							inventory[byte0] = ItemStack.loadItemStackFromNBT(itemTagCompound);
						}
					}

					return inventory;
				}
				
				return new ItemStack[size];
			}
		}
		
		return new ItemStack[size];
	}
	
	public void saveItemInventory(){
		if (!world.isRemote) {
			ItemStack reader = player != null? player.getCurrentEquippedItem(): this.reader;
			if(reader != null && reader.getItem() instanceof Reader) {
				try {
					NBTTagList inventoryTagList = new NBTTagList();
					for (int i = 0; i < inventory.length; i++) {
						if (inventory[i] != null) {
							NBTTagCompound itemTagCompound = new NBTTagCompound();
							itemTagCompound.setByte("Slot", (byte) i);
							inventory[i].writeToNBT(itemTagCompound);
							inventoryTagList.appendTag(itemTagCompound);
						}
					}
					if (!reader.hasTagCompound()) {
						reader.setTagCompound(new NBTTagCompound());
					}
					
					reader.stackTagCompound.setTag("Inventory", inventoryTagList);

				} catch (Exception e) {
					
				}
			}
		}
	}

}
