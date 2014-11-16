package com.brandon3055.draconicevolution.common.tileentities;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.common.core.utills.EnergyStorage;
import com.brandon3055.draconicevolution.common.core.utills.ICustomItemData;
import com.brandon3055.draconicevolution.common.core.utills.InventoryUtils;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.lib.OreDoublingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created by Brandon on 27/06/2014.
 */
public class TileDraconiumChest extends TileEntity implements IInventory, IEnergyHandler, ICustomItemData {
	ItemStack[] items = new ItemStack[240];
	ItemStack[] itemsCrafting = new ItemStack[10];
	private int ticksSinceSync = -1;
	public float prevLidAngle;
	public float lidAngle;
	private int numUsingPlayers;
	private int facing;
	public int red = 100;
	public int green = 0;
	public int blue = 150;
	public boolean editMode = false;
	private String customName;
	public EnergyStorage energy = new EnergyStorage(1000000, 10000, 0);

	/**
	 * Current smelting progress. Ranges from 0 to 1000
	 */
	public int smeltingProgressTime;
	/**
	 * How fast the furnace is smelting (used by flame animation) range:0 - 100
	 */
	public int smeltingBurnSpeed;
	public final int smeltingMaxBurnSpeed = 50;
	public final int smeltingCompleateTime = 1600;
	public int smeltingAutoFeed = 0;
	public int tick;

	@Override
	public void updateEntity() {
		super.updateEntity();

		// Resynchronize clients with the server state
		if (worldObj != null && !this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
			this.numUsingPlayers = 0;
			float var1 = 5.0F;
			@SuppressWarnings("unchecked") List<EntityPlayer> var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double) ((float) this.xCoord - var1), (double) ((float) this.yCoord - var1), (double) ((float) this.zCoord - var1), (double) ((float) (this.xCoord + 1) + var1), (double) ((float) (this.yCoord + 1) + var1), (double) ((float) (this.zCoord + 1) + var1)));

			for (EntityPlayer var4 : var2) {
				if (var4.openContainer instanceof ContainerDraconiumChest) {
					++this.numUsingPlayers;
				}
			}
		}

		if (worldObj != null && !worldObj.isRemote && ticksSinceSync < 0) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModBlocks.draconiumChest, 3, ((numUsingPlayers << 3) & 0xF8) | (facing & 0x7));
		}

		this.ticksSinceSync++;
		prevLidAngle = lidAngle;
		float f = 0.1F;
		if (numUsingPlayers > 0 && lidAngle == 0.0F) {
			double d = (double) xCoord + 0.5D;
			double d1 = (double) zCoord + 0.5D;
			worldObj.playSoundEffect(d, (double) yCoord + 0.5D, d1, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
			float f1 = lidAngle;
			if (numUsingPlayers > 0) {
				lidAngle += f;
			} else {
				lidAngle -= f;
			}
			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}
			float f2 = 0.5F;
			if (lidAngle < f2 && f1 >= f2) {
				double d2 = (double) xCoord + 0.5D;
				double d3 = (double) zCoord + 0.5D;
				worldObj.playSoundEffect(d2, (double) yCoord + 0.5D, d3, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
		updateFurnace();
		updateEnergy();
	}

	public void updateEnergy() {
		if (energy.getEnergyStored() < energy.getMaxEnergyStored() && getStackInSlot(239) != null && getStackInSlot(239).getItem() instanceof IEnergyContainerItem) {
			IEnergyContainerItem item = (IEnergyContainerItem) getStackInSlot(239).getItem();
			item.extractEnergy(getStackInSlot(239), receiveEnergy(ForgeDirection.DOWN, item.extractEnergy(getStackInSlot(239), energy.getMaxReceive(), true), false), false);
		}
	}

	public void updateFurnace() {
		if (worldObj.isRemote) return;
		tick++;
		boolean canSmelt = false;
		boolean flag = true;
		for (int i = 0; i < 5; i++) {
			if (getStackInSlot(234 + i) == null) continue;
			ItemStack stack = getStackInSlot(234 + i);
			if (isSmeltable(stack)) canSmelt = true;
			else flag = false;
		}


		if (!flag) canSmelt = false;

		if (canSmelt) {//Check if there is room for the output
			for (int i = 0; i < 5; i++) {

				if (getStackInSlot(234 + i) == null) continue;
				ItemStack output = getResult(getStackInSlot(234 + i)).copy();

				for (int j = 0; j < getSizeInventory(); j++) {
					InventoryUtils.insertItemIntoInventory(this, output, ForgeDirection.DOWN, j, false);
					if (output.stackSize == 0) break;
				}

				if (output.stackSize > 0) {
					canSmelt = false;
					break;
				}
			}
		}

		if (canSmelt && getLock()){ //Confirm that the firnace can still run when lock mode is enabled
			flag = false;
			for (int i = 0; i < 5; i++) {

				ItemStack recipe = getStackInSlot(234 + i);

				if (recipe == null || recipe.stackSize == 1) continue;

				flag = true;
			}
			canSmelt = flag;
		}

		flag = false;
		if (canSmelt && smeltingProgressTime >= smeltingCompleateTime) {
			int itemsToProccess = 5;
			int proccessAttempts = 0;
			do {
				for (int i = 0; i < 5; i++) {

					ItemStack recipe = getStackInSlot(234 + i);

					if (recipe == null || (getLock() && recipe.stackSize == 1)) continue;

					ItemStack result = getResult(recipe).copy();

					for (int j = 0; j < getSizeInventory(); j++) {
						if (getStackInSlot(j) == null) continue;
						InventoryUtils.tryMergeStacks(result, getStackInSlot(j));
					}

					if (result.stackSize > 0) InventoryUtils.insertItemIntoInventory(this, result);

					if (result.stackSize == 0) {
						recipe.stackSize--;
						if (recipe.stackSize == 0) setInventorySlotContents(234 + i, null);
						itemsToProccess--;
						flag = true;
					}

					if (itemsToProccess == 0) break;
				}

				proccessAttempts++;

			} while (itemsToProccess > 0 && proccessAttempts < 5);
		}

		if ((flag && (getFill() || getLock() || getAll())) || (!canSmelt && (getLock() || getAll()) && tick % 60 == 0)) feedNextItem();

		if (canSmelt) {
			smeltingBurnSpeed = Math.min(energy.getEnergyStored() / 1000, smeltingMaxBurnSpeed);
		} else smeltingBurnSpeed = 0;

		if (canSmelt && smeltingProgressTime < smeltingCompleateTime) {
			smeltingProgressTime += smeltingBurnSpeed;
			energy.modifyEnergyStored(-smeltingBurnSpeed * 5);
		} else smeltingProgressTime = 0;

	}

	public void feedNextItem() {
		boolean[] stacksFull = new boolean[]{false,false,false,false,false};

		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) == null) continue;

			ItemStack candidate = getStackInSlot(i);

			for (int j = 0; j < 5; j++) {
				if (getStackInSlot(234 + j) == null && getAll())
				{

					if (candidate == null) break;
					boolean candidateSmeltable = FurnaceRecipes.smelting().getSmeltingResult(candidate) != null;
					if (candidateSmeltable) {
						setInventorySlotContents(234 + j, candidate.copy());
						setInventorySlotContents(i, null);
						candidate = null;
					}
				}

				if (getStackInSlot(234 + j) == null || candidate == null) continue;

				ItemStack inputSlot = getStackInSlot(234 + j);

				InventoryUtils.tryMergeStacks(candidate, inputSlot);

				if (candidate != null && candidate.stackSize == 0) {
					setInventorySlotContents(i, null);
					candidate = null;
				}

				if (inputSlot.stackSize == inputSlot.getMaxStackSize()) {
					stacksFull[j] = true;
				}
			}

			if (candidate != null && candidate.stackSize == 0) setInventorySlotContents(i, null);

			if (stacksFull[0] && stacksFull[1] && stacksFull[2] && stacksFull[3] && stacksFull[4]) break;
		}
	}

	private boolean getFill() {return smeltingAutoFeed == 1;}
	private boolean getLock() {return smeltingAutoFeed == 2;}
	private boolean getAll() {return smeltingAutoFeed == 3;}

	private boolean isSmeltable(ItemStack stack) {
		return FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
	}

	private ItemStack getResult(ItemStack stack) {
		ItemStack oreResult = OreDoublingRegistry.getOreResult(stack);
		return oreResult != null ? oreResult : FurnaceRecipes.smelting().getSmeltingResult(stack);
	}

	public void setAutoFeed(int i){
		smeltingAutoFeed = i;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean receiveClientEvent(int i, int j) {
		if (i == 1) {
			numUsingPlayers = j;
		} else if (i == 2) {
			facing = (byte) j;
		} else if (i == 3) {
			facing = (byte) (j & 0x7);
			numUsingPlayers = (j & 0xF8) >> 3;
		}
		return true;
	}

	public void rotateAround(ForgeDirection axis) {
		setFacing((byte) ForgeDirection.getOrientation(facing).getRotation(axis).ordinal());
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModBlocks.draconiumChest, 3, ((numUsingPlayers << 3) & 0xF8) | (facing & 0x7));
		LogHelper.info(facing);
	}

	public int getFacing() {
		return this.facing;
	}

	public void setFacing(int facing2) {
		this.facing = facing2;
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
	public int getSizeInventory() {
		return items.length - 6;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	public ItemStack getStackInCraftingSlot(int i) {
		return itemsCrafting[i];
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

	public void setInventoryCraftingSlotContents(int i, ItemStack itemstack) {
		itemsCrafting[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? customName : StatCollector.translateToLocal(ModBlocks.draconiumChest.getUnlocalizedName() + ".name");
	}

	public void setCustomName(String s){
		customName = s;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return customName != null && customName.length() > 0;
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
		if (worldObj == null) return;
		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModBlocks.draconiumChest, 1, numUsingPlayers);
	}

	@Override
	public void closeInventory() {
		if (worldObj == null) return;
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModBlocks.draconiumChest, 1, numUsingPlayers);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = new NBTTagCompound();

			if (items[i] != null) {
				tag[i] = items[i].writeToNBT(tag[i]);
			}

			compound.setTag("Item" + i, tag[i]);
		}

		for (int i = 0; i < itemsCrafting.length; i++) {
			tag[i] = new NBTTagCompound();

			if (itemsCrafting[i] != null) {
				tag[i] = itemsCrafting[i].writeToNBT(tag[i]);
			}

			compound.setTag("CraftingItem" + i, tag[i]);
		}

		compound.setByte("facing", (byte) facing);
		compound.setInteger("Red", red);
		compound.setInteger("Green", green);
		compound.setInteger("Blue", blue);
		compound.setBoolean("Edit", editMode);
		compound.setByte("AutoFeed", (byte) smeltingAutoFeed);
		if (customName != null && customName.length() > 0)compound.setString("CustomName", customName);
		energy.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = compound.getCompoundTag("Item" + i);
			items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}

		for (int i = 0; i < itemsCrafting.length; i++) {
			tag[i] = compound.getCompoundTag("CraftingItem" + i);
			itemsCrafting[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}

		facing = compound.getByte("facing");
		red = compound.getInteger("Red");
		green = compound.getInteger("Green");
		blue = compound.getInteger("Blue");
		editMode = compound.getBoolean("Edit");
		smeltingAutoFeed = compound.getByte("AutoFeed");
		customName = compound.getString("CustomName");
		energy.readFromNBT(compound);
	}

	@Override
	public void writeDataToItem(NBTTagCompound compound, ItemStack stack) {
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = new NBTTagCompound();

			if (items[i] != null) {
				tag[i] = items[i].writeToNBT(tag[i]);
			}

			compound.setTag("Item" + i, tag[i]);
		}

		for (int i = 0; i < itemsCrafting.length; i++) {
			tag[i] = new NBTTagCompound();

			if (itemsCrafting[i] != null) {
				tag[i] = itemsCrafting[i].writeToNBT(tag[i]);
			}

			compound.setTag("CraftingItem" + i, tag[i]);
		}

		compound.setInteger("Red", red);
		compound.setInteger("Green", green);
		compound.setInteger("Blue", blue);
		compound.setBoolean("Edit", editMode);
		compound.setByte("AutoFeed", (byte) smeltingAutoFeed);
		if (hasCustomInventoryName()) stack.setStackDisplayName(customName);
		LogHelper.info(stack.getDisplayName());
		energy.writeToNBT(compound);
	}

	@Override
	public void readDataFromItem(NBTTagCompound compound, ItemStack stack) {
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++) {
			tag[i] = compound.getCompoundTag("Item" + i);
			items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}

		for (int i = 0; i < itemsCrafting.length; i++) {
			tag[i] = compound.getCompoundTag("CraftingItem" + i);
			itemsCrafting[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}

		red = compound.getInteger("Red");
		green = compound.getInteger("Green");
		blue = compound.getInteger("Blue");
		editMode = compound.getBoolean("Edit");
		smeltingAutoFeed = compound.getByte("AutoFeed");
		energy.readFromNBT(compound);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return this.energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		;
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
}
