package com.brandon3055.draconicevolution.common.tileentities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileGrinder extends TileEntity implements ISidedInventory {
	//########### variables #############//
	public int meta = -1;
	List<EntityLiving> killList;
	AxisAlignedBB killBox;
	int tick = 0;
	int updateTick = 0;
	double centreX;
	double centreY = -1;
	double centreZ;
	private ItemStack[] items;
	public int energy = 0;
	public int burnTime = 1;
	public int burnTimeRemaining = 0;
	public boolean disabled = false;
	private boolean readyNext = false;

	public void updateVariables() {
		if (meta == -1) meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (centreY == -1) {
			switch (meta) {
				case 0:
					centreX = xCoord + 0.5;
					centreY = yCoord + 0.5;
					centreZ = zCoord + 0.5 - 4;
					break;
				case 1:
					centreX = xCoord + 0.5 + 4;
					centreY = yCoord + 0.5;
					centreZ = zCoord + 0.5;
					break;
				case 2:
					centreX = xCoord + 0.5;
					centreY = yCoord + 0.5;
					centreZ = zCoord + 0.5 + 4;
					break;
				case 3:
					centreX = xCoord + 0.5 - 4;
					centreY = yCoord + 0.5;
					centreZ = zCoord + 0.5;
					break;
			}
		}

		if (burnTimeRemaining > 0) {
			burnTimeRemaining -= 2;
			energy += 2;
			if (burnTimeRemaining < 0) burnTimeRemaining = 0;
		}
		if (energy > 5000) energy = 5000;
	}
	//##################################//

	public TileGrinder() {
		items = new ItemStack[1];
	}

	@Override
	public void updateEntity() {
		if ((burnTimeRemaining == 1 || (burnTimeRemaining == 0 && updateTick == 20)) && !disabled && energy < 2500) {
			updateVariables();
			tryRefuel();
		} else updateVariables();

		if (!worldObj.isRemote && updateTick >= 50 && readyNext && !disabled && energy >= 100) {
			if (killNextEntity()) {
				energy -= 100;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		if (updateTick >= 50) {
			updateTick = 0;
		}
		if (!disabled) updateTick += worldObj.rand.nextInt(10);

		if (tick >= 100) {
			tick = 0;
			checkSignal();
			readyNext = true;
			if (burnTimeRemaining > 0) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else tick++;
	}

	public void checkSignal() {
		disabled = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	private void tryRefuel() {
		if (items[0] != null && items[0].stackSize > 0) {
			int itemBurnTime = getItemBurnTime(items[0]);

			if (itemBurnTime > 0) {
				decrStackSize(0, 1);
				burnTime = itemBurnTime;
				burnTimeRemaining = itemBurnTime;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean killNextEntity() {
		killBox = AxisAlignedBB.getBoundingBox(centreX - 3.5, centreY - 1, centreZ - 3.5, centreX + 3.5, centreY + 3, centreZ + 3.5);
		killList = worldObj.getEntitiesWithinAABB(EntityLiving.class, killBox);

		if (killList.size() > 0) {
			EntityLiving mob = killList.get(worldObj.rand.nextInt(killList.size()));
			if (mob instanceof EntityCreature) {
				if (((EntityCreature) mob).isEntityAlive()) {
					((EntityCreature) mob).attackEntityFrom(DamageSource.magic, 50000F);
					dropXP(mob);
					readyNext = true;
					if (mob instanceof EntitySkeleton) {
						if (((EntitySkeleton) mob).getSkeletonType() == 1) {
							int random = worldObj.rand.nextInt(100);
							if (random == 55)
								((EntityCreature) mob).entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
						}
					}
					return true;
				}
				readyNext = true;
				return false;
			} else if (mob instanceof EntityLiving) {
				if (((EntityLiving) mob).isEntityAlive()) {
					((EntityLiving) mob).attackEntityFrom(DamageSource.magic, 50000F);
					dropXP(mob);
					readyNext = true;
					return true;
				}
				readyNext = true;
				return false;
			}
		}
		readyNext = false;
		return false;
	}

	public void dropXP(Entity mob) {
		int count = 1 + worldObj.rand.nextInt(3);
		for (int i = 0; i < count; i++) {
			worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, mob.lastTickPosX, mob.lastTickPosY, mob.lastTickPosZ, 1));
		}
	}

	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab) {
					return 150;
				}

				if (block.getMaterial() == Material.wood) {
					return 300;
				}

				if (block == Blocks.coal_block) {
					return 16000;
				}
			}

			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) return 200;
			if (item == Items.stick) return 100;
			if (item == Items.coal) return 1600;
			if (item == Items.lava_bucket) return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
			if (item == Items.blaze_rod) return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
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
		items[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.4) < 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return getItemBurnTime(stack) > 0;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[1];
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		return true;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = new NBTTagCompound();

			if (items[i] != null) {
				tag[i] = items[i].writeToNBT(tag[i]);
			}

			compound.setTag("Item" + i, tag[i]);
		}

		compound.setInteger("EnergyHelper", energy);
		compound.setBoolean("Disabled", disabled);
		compound.setInteger("BurnTime", burnTime);
		compound.setInteger("BurnTimeRemaining", burnTimeRemaining);

		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = compound.getCompoundTag("Item" + i);
			items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}

		energy = compound.getInteger("EnergyHelper");
		disabled = compound.getBoolean("Disabled");
		burnTime = compound.getInteger("BurnTime");
		burnTimeRemaining = compound.getInteger("BurnTimeRemaining");

		super.readFromNBT(compound);
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
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
