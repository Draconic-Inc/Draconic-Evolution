package com.brandon3055.draconicevolution.common.core.utills;

/**
 * Everything in this class is copied from openblocks
 */
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class InventoryUtils {

	/***
	 * Try to merge the supplied stack into the supplied slot in the target
	 * inventory
	 *
	 * @param targetInventory
	 *            Although it doesn't return anything, it'll REDUCE the stack
	 *            size of the stack that you pass in
	 *
	 * @param slot
	 * @param stack
	 */
	public static void tryInsertStack(IInventory targetInventory, int slot, ItemStack stack, boolean canMerge) {
		if (targetInventory.isItemValidForSlot(slot, stack)) {
			ItemStack targetStack = targetInventory.getStackInSlot(slot);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(slot, stack.copy());
				stack.stackSize = 0;
			} else if (canMerge) {
				if (targetInventory.isItemValidForSlot(slot, stack) &&
						areMergeCandidates(stack, targetStack)) {
					int space = targetStack.getMaxStackSize()
							- targetStack.stackSize;
					int mergeAmount = Math.min(space, stack.stackSize);
					ItemStack copy = targetStack.copy();
					copy.stackSize += mergeAmount;
					targetInventory.setInventorySlotContents(slot, copy);
					stack.stackSize -= mergeAmount;
				}
			}
		}
	}

	public static boolean areItemAndTagEqual(final ItemStack stackA, ItemStack stackB) {
		return stackA.isItemEqual(stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}

	public static boolean areMergeCandidates(ItemStack source, ItemStack target) {
		return areItemAndTagEqual(source, target) && target.stackSize < target.getMaxStackSize();
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack) {
		insertItemIntoInventory(inventory, stack, ForgeDirection.UNKNOWN, -1);
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, ForgeDirection side, int intoSlot) {
		insertItemIntoInventory(inventory, stack, side, intoSlot, true);
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, ForgeDirection side, int intoSlot, boolean doMove) {
		insertItemIntoInventory(inventory, stack, side, intoSlot, doMove, true);
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, ForgeDirection side, int intoSlot, boolean doMove, boolean canStack) {
		if (stack == null) return;

		final int sideId = side.ordinal();
		IInventory targetInventory = inventory;

		// if we're not meant to move, make a clone of the inventory
		if (!doMove) {
			GenericInventory copy = new GenericInventory("temporary.inventory", false, targetInventory.getSizeInventory());
			copy.copyFrom(inventory);
			targetInventory = copy;
		}

		final Set<Integer> attemptSlots = Sets.newTreeSet();

		// if it's a sided inventory, get all the accessible slots
		final boolean isSidedInventory = inventory instanceof ISidedInventory && side != ForgeDirection.UNKNOWN;

		if (isSidedInventory) {
			int[] accessibleSlots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(sideId);
			for (int slot : accessibleSlots)
				attemptSlots.add(slot);
		} else {
			// if it's just a standard inventory, get all slots
			for (int a = 0; a < inventory.getSizeInventory(); a++) {
				attemptSlots.add(a);
			}
		}

		// if we've defining a specific slot, we'll just use that
		if (intoSlot > -1) attemptSlots.retainAll(ImmutableSet.of(intoSlot));

		if (attemptSlots.isEmpty()) return;

		for (Integer slot : attemptSlots) {
			if (stack.stackSize <= 0) break;
			if (isSidedInventory && !((ISidedInventory)inventory).canInsertItem(slot, stack, sideId)) continue;
			tryInsertStack(targetInventory, slot, stack, canStack);
		}
	}

	public static int moveItemInto(IInventory fromInventory, int fromSlot, Object target, int intoSlot, int maxAmount, ForgeDirection direction, boolean doMove) {
		return moveItemInto(fromInventory, fromSlot, target, intoSlot, maxAmount, direction, doMove, true);
	}

	/***
	 * Move an item from the fromInventory, into the target. The target can be
	 * an inventory or pipe.
	 * Double checks are automagically wrapped. If you're not bothered what slot
	 * you insert into, pass -1 for intoSlot. If you're passing false for
	 * doMove, it'll create a dummy inventory and its calculations on that
	 * instead
	 *
	 * @param fromInventory
	 *            the inventory the item is coming from
	 * @param fromSlot
	 *            the slot the item is coming from
	 * @param target
	 *            the inventory you want the item to be put into. can be BC pipe
	 *            or IInventory
	 * @param intoSlot
	 *            the target slot. Pass -1 for any slot
	 * @param maxAmount
	 *            The maximum amount you wish to pass
	 * @param direction
	 *            The direction of the move. Pass UNKNOWN if not applicable
	 * @param doMove
	 * @param canStack
	 * @return The amount of items moved
	 */
	public static int moveItemInto(IInventory fromInventory, int fromSlot, Object target, int intoSlot, int maxAmount, ForgeDirection direction, boolean doMove, boolean canStack) {

		fromInventory = InventoryUtils.getInventory(fromInventory);

		// if we dont have a stack in the source location, return 0
		ItemStack sourceStack = fromInventory.getStackInSlot(fromSlot);
		if (sourceStack == null) { return 0; }

		if (fromInventory instanceof ISidedInventory
				&& !((ISidedInventory)fromInventory).canExtractItem(fromSlot, sourceStack, direction.ordinal())) return 0;

		// create a clone of our source stack and set the size to either
		// maxAmount or the stackSize
		ItemStack clonedSourceStack = sourceStack.copy();
		clonedSourceStack.stackSize = Math.min(clonedSourceStack.stackSize, maxAmount);
		int amountToMove = clonedSourceStack.stackSize;
		int inserted = 0;

		if (target instanceof IInventory) {
			IInventory targetInventory = getInventory((IInventory)target);
			ForgeDirection side = direction.getOpposite();
			// try insert the item into the target inventory. this'll reduce the
			// stackSize of our stack
			InventoryUtils.insertItemIntoInventory(targetInventory, clonedSourceStack, side, intoSlot, doMove, canStack);
			inserted = amountToMove - clonedSourceStack.stackSize;

		}

		// if we've done the move, reduce/remove the stack from our source
		// inventory
		if (doMove) {
			ItemStack newSourcestack = sourceStack.copy();
			newSourcestack.stackSize -= inserted;
			if (newSourcestack.stackSize == 0) {
				fromInventory.setInventorySlotContents(fromSlot, null);
			} else {
				fromInventory.setInventorySlotContents(fromSlot, newSourcestack);
			}
		}

		return inserted;
	}

	private static IInventory doubleChestFix(TileEntity te) {
		final World world = te.getWorldObj();
		final int x = te.xCoord;
		final int y = te.yCoord;
		final int z = te.zCoord;
		if (world.getBlock(x - 1, y, z) == Blocks.chest) return new InventoryLargeChest("Large chest", (IInventory)world.getTileEntity(x - 1, y, z), (IInventory)te);
		if (world.getBlock(x + 1, y, z) == Blocks.chest) return new InventoryLargeChest("Large chest", (IInventory)te, (IInventory)world.getTileEntity(x + 1, y, z));
		if (world.getBlock(x, y, z - 1) == Blocks.chest) return new InventoryLargeChest("Large chest", (IInventory)world.getTileEntity(x, y, z - 1), (IInventory)te);
		if (world.getBlock(x, y, z + 1) == Blocks.chest) return new InventoryLargeChest("Large chest", (IInventory)te, (IInventory)world.getTileEntity(x, y, z + 1));
		return (te instanceof IInventory)? (IInventory)te : null;
	}

	public static IInventory getInventory(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityChest) return doubleChestFix(tileEntity);
		if (tileEntity instanceof IInventory) return (IInventory)tileEntity;
		return null;
	}

	public static IInventory getInventory(World world, int x, int y, int z, ForgeDirection direction) {
		if (direction != null) {
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
		}
		return getInventory(world, x, y, z);

	}

	public static IInventory getInventory(IInventory inventory) {
		if (inventory instanceof TileEntityChest) return doubleChestFix((TileEntity)inventory);
		return inventory;
	}

	public static List<ItemStack> getInventoryContents(IInventory inventory) {
		List<ItemStack> result = Lists.newArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (slot != null) result.add(slot);
		}
		return result;
	}

	/***
	 * Get the indexes of all slots containing a stack of the supplied item
	 * type.
	 *
	 * @param inventory
	 * @param stack
	 * @return Returns a set of the slot indexes
	 */
	public static Set<Integer> getSlotsWithStack(IInventory inventory, ItemStack stack) {
		inventory = getInventory(inventory);
		Set<Integer> slots = Sets.newHashSet();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stackInSlot = inventory.getStackInSlot(i);
			if (stackInSlot != null && stackInSlot.isItemEqual(stack)) slots.add(i);
		}
		return slots;
	}

	/***
	 * Get the first slot containing an item type matching the supplied type.
	 *
	 * @param inventory
	 * @param stack
	 * @return Returns -1 if none found
	 */
	public static int getFirstSlotWithStack(IInventory inventory, ItemStack stack) {
		inventory = getInventory(inventory);
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stackInSlot = inventory.getStackInSlot(i);
			if (stackInSlot != null && stackInSlot.isItemEqual(stack)) { return i; }
		}
		return -1;
	}

	/***
	 * Consume ONE of the supplied item types
	 *
	 * @param inventory
	 * @param stack
	 * @return Returns whether or not it was able to
	 */
	public static boolean consumeInventoryItem(IInventory inventory, ItemStack stack) {
		int slotWithStack = getFirstSlotWithStack(inventory, stack);
		if (slotWithStack > -1) {
			ItemStack stackInSlot = inventory.getStackInSlot(slotWithStack);
			stackInSlot.stackSize--;
			if (stackInSlot.stackSize == 0) {
				inventory.setInventorySlotContents(slotWithStack, null);
			}
			return true;
		}
		return false;
	}

	/**
	 * Get the first slot index in an inventory with an item
	 *
	 * @param invent
	 * @return The slot index, or -1 if the inventory is empty
	 */
	public static int getSlotIndexOfNextStack(IInventory invent) {
		for (int i = 0; i < invent.getSizeInventory(); i++) {
			ItemStack stack = invent.getStackInSlot(i);
			if (stack != null) { return i; }
		}
		return -1;
	}

	/***
	 * Removes an item stack from the inventory and returns a copy of it
	 *
	 * @param invent
	 * @return A copy of the stack it removed
	 */
	public static ItemStack removeNextItemStack(IInventory invent) {
		int nextFilledSlot = getSlotIndexOfNextStack(invent);
		if (nextFilledSlot > -1) {
			ItemStack copy = invent.getStackInSlot(nextFilledSlot).copy();
			invent.setInventorySlotContents(nextFilledSlot, null);
			return copy;
		}
		return null;
	}

	public static int moveItemsFromOneOfSides(TileEntity te, IInventory inv, int maxAmount, int intoSlot, Set<ForgeDirection> sides) {
		return moveItemsFromOneOfSides(te, inv, null, maxAmount, intoSlot, sides);
	}

	public static int moveItemsFromOneOfSides(TileEntity te, IInventory inv, ItemStack filterStack, int maxAmount, int intoSlot, Set<ForgeDirection> sides) {
		List<ForgeDirection> shuffledSides = Lists.newArrayList(sides);
		Collections.shuffle(shuffledSides);

		IInventory ourInventory = getInventory(inv);

		// loop through the shuffled sides
		for (ForgeDirection dir : sides) {
			TileEntity tileOnSurface = getTileInDirection(te, dir);
			// if it's an inventory
			if (tileOnSurface instanceof IInventory) {
				final IInventory neighbor = (IInventory)tileOnSurface;

				Set<Integer> slots;

				if (filterStack == null) slots = getAllSlots(neighbor);
				else slots = getSlotsWithStack(neighbor, filterStack);

				for (Integer slot : slots) {
					int moved = InventoryUtils.moveItemInto(neighbor, slot, ourInventory, intoSlot, maxAmount, dir.getOpposite(), true);
					if (moved > 0) return moved;
				}
			}
		}
		return 0;
	}

	/**
	 * Tests to see if an item stack can be inserted in to an inventory Does not
	 * perform the insertion, only tests the possibility
	 *
	 * @param inventory
	 *            The inventory to insert the stack into
	 * @param item
	 *            the stack to insert
	 * @return the amount of items that could be put in to the stack
	 */
	public static int testInventoryInsertion(IInventory inventory, ItemStack item) {
		if (item == null || item.stackSize == 0) return 0;
		if (inventory == null) return 0;
		int slotCount = inventory.getSizeInventory();
		/*
		 * Allows counting down the item size, without cloning or changing the
		 * object
		 */
		int itemSizeCounter = item.stackSize;
		for (int i = 0; i < slotCount && itemSizeCounter > 0; i++) {

			if (!inventory.isItemValidForSlot(i, item)) continue;
			ItemStack inventorySlot = inventory.getStackInSlot(i);
			/*
			 * If the slot is empty, dump the biggest stack we can, taking in to
			 * consideration, the remaining amount of stack
			 */
			if (inventorySlot == null) {
				itemSizeCounter -= Math.min(Math.min(itemSizeCounter, inventory.getInventoryStackLimit()), item.getMaxStackSize());
			}
			/* If the slot is not empty, check that these items stack */
			else if (areMergeCandidates(item, inventorySlot)) {
				/* If they stack, decrement by the amount of space that remains */

				int space = inventorySlot.getMaxStackSize()
						- inventorySlot.stackSize;
				itemSizeCounter -= Math.min(itemSizeCounter, space);
			}
		}
		// itemSizeCounter might be less than zero here. It shouldn't be, but I
		// don't trust me. -NC
		if (itemSizeCounter != item.stackSize) {
			itemSizeCounter = Math.max(itemSizeCounter, 0);
			return item.stackSize - itemSizeCounter;
		}
		return 0;
	}

	public static Set<Integer> getAllSlots(IInventory inventory) {
		inventory = getInventory(inventory);
		Set<Integer> slots = new HashSet<Integer>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			slots.add(i);
		}
		return slots;
	}

	public static Map<Integer, ItemStack> getAllItems(IInventory inventory) {
		Map<Integer, ItemStack> result = Maps.newHashMap();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null) result.put(i, stack);
		}

		return result;

	}

	public static int moveItemsToOneOfSides(TileEntity te, IInventory inv, int fromSlot, int maxAmount, Set<ForgeDirection> sides) {
		// wrap it. Sure, we dont need to really as this'll never be a double
		// chest but, whatevs. (M?)
		final IInventory inventory = getInventory(inv);

		// if we've not got a stack in that slot, we dont care.
		if (inventory.getStackInSlot(fromSlot) == null) return 0;

		// shuffle the sides that have been passed in
		List<ForgeDirection> shuffledSides = Lists.newArrayList(sides);
		Collections.shuffle(shuffledSides);

		for (ForgeDirection dir : shuffledSides) {
			// grab the tile in the current direction
			TileEntity tileOnSurface = getTileInDirection(te, dir);
			if (tileOnSurface == null) { return 0; }

			int moved = InventoryUtils.moveItemInto(inventory, fromSlot, tileOnSurface, -1, maxAmount, dir, true);
			// move the object from our inventory, in the specified slot, to a
			// pipe or any slot in an inventory in a particular direction
			if (moved > 0) return moved;
		}
		return 0;
	}

	public static boolean inventoryIsEmpty(IInventory inventory) {
		for (int i = 0, l = inventory.getSizeInventory(); i < l; i++)
			if (inventory.getStackInSlot(i) != null) return false;
		return true;
	}

	public static boolean tryMergeStacks(ItemStack stackToMerge, ItemStack stackInSlot) {
		if (stackInSlot == null || !stackInSlot.isItemEqual(stackToMerge) || !ItemStack.areItemStackTagsEqual(stackToMerge, stackInSlot)) return false;

		int newStackSize = stackInSlot.stackSize + stackToMerge.stackSize;

		final int maxStackSize = stackToMerge.getMaxStackSize();
		if (newStackSize <= maxStackSize) {
			stackToMerge.stackSize = 0;
			stackInSlot.stackSize = newStackSize;
			return true;
		} else if (stackInSlot.stackSize < maxStackSize) {
			stackToMerge.stackSize -= maxStackSize - stackInSlot.stackSize;
			stackInSlot.stackSize = maxStackSize;
			return true;
		}

		return false;
	}

	public static ItemStack returnItem(ItemStack stack) {
		return (stack == null || stack.stackSize <= 0)? null : stack.copy();
	}

	public static void swapStacks(IInventory inventory, int slot1, int slot2) {
		swapStacks(inventory, slot1, slot2, true, true);
	}

	public static void swapStacks(IInventory inventory, int slot1, int slot2, boolean copy, boolean validate) {
		Preconditions.checkElementIndex(slot1, inventory.getSizeInventory(), "input slot id");
		Preconditions.checkElementIndex(slot2, inventory.getSizeInventory(), "output slot id");

		ItemStack stack1 = inventory.getStackInSlot(slot1);
		ItemStack stack2 = inventory.getStackInSlot(slot2);

		if (validate) {
			isItemValid(inventory, slot2, stack1);
			isItemValid(inventory, slot1, stack2);
		}

		if (copy) {
			if (stack1 != null) stack1 = stack1.copy();
			if (stack2 != null) stack2 = stack2.copy();
		}

		inventory.setInventorySlotContents(slot1, stack2);
		inventory.setInventorySlotContents(slot2, stack1);
		inventory.markDirty();
	}

	public static void swapStacks(ISidedInventory inventory, int slot1, ForgeDirection side1, int slot2, ForgeDirection side2) {
		swapStacks(inventory, slot1, side1, slot2, side2, true, true);
	}

	public static void swapStacks(ISidedInventory inventory, int slot1, ForgeDirection side1, int slot2, ForgeDirection side2, boolean copy, boolean validate) {
		Preconditions.checkElementIndex(slot1, inventory.getSizeInventory(), "input slot id");
		Preconditions.checkElementIndex(slot2, inventory.getSizeInventory(), "output slot id");

		ItemStack stack1 = inventory.getStackInSlot(slot1);
		ItemStack stack2 = inventory.getStackInSlot(slot2);

		if (validate) {
			isItemValid(inventory, slot2, stack1);
			isItemValid(inventory, slot1, stack2);

			canExtract(inventory, slot1, side1, stack1);
			canInsert(inventory, slot2, side2, stack1);

			canExtract(inventory, slot2, side2, stack2);
			canInsert(inventory, slot1, side1, stack2);
		}

		if (copy) {
			if (stack1 != null) stack1 = stack1.copy();
			if (stack2 != null) stack2 = stack2.copy();
		}

		inventory.setInventorySlotContents(slot1, stack2);
		inventory.setInventorySlotContents(slot2, stack1);
		inventory.markDirty();
	}

	protected static void isItemValid(IInventory inventory, int slot, ItemStack stack) {
		Preconditions.checkArgument(inventory.isItemValidForSlot(slot, stack), "Slot %s cannot accept item", slot);
	}

	protected static void canInsert(ISidedInventory inventory, int slot, ForgeDirection side, ItemStack stack) {
		Preconditions.checkArgument(inventory.canInsertItem(slot, stack, side.ordinal()),
				"Item cannot be inserted into slot %s on side %s", slot, side);
	}

	protected static void canExtract(ISidedInventory inventory, int slot, ForgeDirection side, ItemStack stack) {
		Preconditions.checkArgument(inventory.canExtractItem(slot, stack, side.ordinal()),
				"Item cannot be extracted from slot %s on side %s", slot, side);
	}

	public static TileEntity getTileInDirection(TileEntity tile, ForgeDirection direction) {
		int targetX = tile.xCoord + direction.offsetX;
		int targetY = tile.yCoord + direction.offsetY;
		int targetZ = tile.zCoord + direction.offsetZ;
		return tile.getWorldObj().getTileEntity(targetX, targetY, targetZ);
	}

	public static class GenericInventory implements IInventory {

		protected String inventoryTitle;
		protected int slotsCount;
		protected ItemStack[] inventoryContents;
		protected boolean isInvNameLocalized;

		public GenericInventory(String name, boolean isInvNameLocalized, int size) {
			this.isInvNameLocalized = isInvNameLocalized;
			this.slotsCount = size;
			this.inventoryTitle = name;
			this.inventoryContents = new ItemStack[size];
		}

		@Override
		public ItemStack decrStackSize(int par1, int par2)
		{
			if (this.inventoryContents[par1] != null)
			{
				ItemStack itemstack;

				if (this.inventoryContents[par1].stackSize <= par2)
				{
					itemstack = this.inventoryContents[par1];
					this.inventoryContents[par1] = null;
					return itemstack;
				}
				itemstack = this.inventoryContents[par1].splitStack(par2);
				if (this.inventoryContents[par1].stackSize == 0)
				{
					this.inventoryContents[par1] = null;
				}

				return itemstack;
			}
			return null;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public int getSizeInventory() {
			return slotsCount;
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return this.inventoryContents[i];
		}

		public ItemStack getStackInSlot(Enum<?> i) {
			return getStackInSlot(i.ordinal());
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int i) {
			if (i >= this.inventoryContents.length) { return null; }
			if (this.inventoryContents[i] != null) {
				ItemStack itemstack = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				return itemstack;
			}
			return null;
		}

		public boolean isItem(int slot, Item item) {
			return inventoryContents[slot] != null
					&& inventoryContents[slot].getItem() == item;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return true;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}


		public void clearAndSetSlotCount(int amount) {
			this.slotsCount = amount;
			inventoryContents = new ItemStack[amount];
		}

		public void readFromNBT(NBTTagCompound tag) {
			if (tag.hasKey("size")) {
				this.slotsCount = tag.getInteger("size");
			}
			NBTTagList nbttaglist = tag.getTagList("Items", 10);
			inventoryContents = new ItemStack[slotsCount];
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
				int j = stacktag.getByte("Slot");
				if (j >= 0 && j < inventoryContents.length) {
					inventoryContents[j] = ItemStack.loadItemStackFromNBT(stacktag);
				}
			}
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			this.inventoryContents[i] = itemstack;

			if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("size", getSizeInventory());
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < inventoryContents.length; i++) {
				if (inventoryContents[i] != null) {
					NBTTagCompound stacktag = new NBTTagCompound();
					stacktag.setByte("Slot", (byte)i);
					inventoryContents[i].writeToNBT(stacktag);
					nbttaglist.appendTag(stacktag);
				}
			}
			tag.setTag("Items", nbttaglist);
		}

		/**
		 * This bastard never even gets called, so don't rely on it.
		 */
		@Override
		public void markDirty() {
		}

		public void copyFrom(IInventory inventory) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (i < getSizeInventory()) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (stack != null) {
						setInventorySlotContents(i, stack.copy());
					} else {
						setInventorySlotContents(i, null);
					}
				}
			}
		}

		public List<ItemStack> contents() {
			return Arrays.asList(inventoryContents);
		}

		@Override
		public String getInventoryName() {
			return this.inventoryTitle;
		}

		@Override
		public boolean hasCustomInventoryName() {
			return this.isInvNameLocalized;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}
	}
}
