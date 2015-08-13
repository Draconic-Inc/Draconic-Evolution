package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.inventory.GenericInventory;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 30/7/2015.
 */
public class ContainerReactor extends ContainerDataSync {

	private TileReactorCore reactor;
	private EntityPlayer player;
	private GenericInventory ioSlots = new GenericInventory() {
		private ItemStack[] items = new ItemStack[2];

		@Override
		public ItemStack[] getStorage() {
			return items;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return itemstack != null && (itemstack.getItem() == ModItems.draconicIngot || itemstack.getItem() == Item.getItemFromBlock(ModBlocks.draconicBlock));
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;//super.getInventoryStackLimit();
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack stack) {
			if (i == 0 && stack != null && (stack.getItem() == ModItems.draconicIngot || stack.getItem() == Item.getItemFromBlock(ModBlocks.draconicBlock)))
			{
				if (stack.getItem() == ModItems.draconicIngot) reactor.reactorFuel += stack.stackSize * 144;
				if (stack.getItem() == Item.getItemFromBlock(ModBlocks.draconicBlock)) reactor.reactorFuel += stack.stackSize * 1296;
			}
			else getStorage()[i] = stack;
		}
	};

	//Syncing		//todo sort out syncing
	public int LTConversionUnit = -1;
	public int LTReactionIntensity = -1;
	public int LTMaxReactIntensity = -1;
	public int LTFieldStrength = -1;
	public int LTMaxFieldStrength = -1;
	public int LTEnergySaturation = -1;
	public int LTMaxEnergySaturation = -1;
	public int LTFuelConversion = -1;
	//#######

	public ContainerReactor(EntityPlayer player, TileReactorCore reactor){
		this.reactor = reactor;
		this.player = player;

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(player.inventory, x, 44 + 18 * x, 198));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 44 + 18 * x, 140 + y * 18));
			}
		}

		if (reactor.reactorState == TileReactorCore.STATE_OFFLINE){
			addSlotToContainer(new SlotInsert(ioSlots, 0, 15, 140, reactor));
			addSlotToContainer(new SlotExtract(ioSlots, 1, 217, 140));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer entityPlayer) {
		if (ioSlots.getStackInSlot(1) != null && !player.worldObj.isRemote) {
			entityPlayer.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, entityPlayer.posX, player.posY, player.posZ, ioSlots.getStackInSlot(1)));
			ioSlots.setInventorySlotContents(1, null);
		}
		super.onContainerClosed(entityPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {//todo (the chest thing)
		return true;
	}

	@Override
	public void detectAndSendChanges() { //todo check what values are being synced by the tile and remove them from here
		if (reactor.conversionUnit != LTConversionUnit)					LTConversionUnit = 		(Integer) sendObjectToClient(null, 0, (int)(reactor.conversionUnit*100));
//		if ((int)reactor.reactionTemperature != LTReactionIntensity)LTReactionIntensity = 	(Integer) sendObjectToClient(null, 1, (int) reactor.reactionTemperature);
//		if ((int)reactor.maxReactTemperature != LTMaxReactIntensity)LTMaxReactIntensity = 	(Integer) sendObjectToClient(null, 2, (int) reactor.maxReactTemperature);
//		if ((int)reactor.fieldCharge != LTFieldStrength) 			LTFieldStrength = 		(Integer) sendObjectToClient(null, 3, (int) reactor.fieldCharge);
//		if ((int)reactor.maxFieldCharge != LTMaxFieldStrength) 		LTMaxFieldStrength = 	(Integer) sendObjectToClient(null, 7, (int) reactor.maxFieldCharge);
//		if (reactor.energySaturation != LTEnergySaturation) 		LTEnergySaturation = 	(Integer) sendObjectToClient(null, 4, reactor.energySaturation);
//		if (reactor.maxEnergySaturation != LTMaxEnergySaturation)	LTMaxEnergySaturation = (Integer) sendObjectToClient(null, 5, reactor.maxEnergySaturation);
//		if (reactor.convertedFuel != LTFuelConversion) 				LTFuelConversion = 		(Integer) sendObjectToClient(null, 6, reactor.convertedFuel);

		super.detectAndSendChanges();
	}

	@Override
	public void receiveSyncData(int index, int value) {
		if (index == 0) reactor.conversionUnit = (double)value/100D;
//		else if (index == 1) reactor.reactionTemperature = value;
//		else if (index == 2) reactor.maxReactTemperature = value;
//		else if (index == 3) reactor.fieldCharge = value;
//		else if (index == 4) reactor.energySaturation = value;
//		else if (index == 5) reactor.maxEnergySaturation = value;
//		else if (index == 6) reactor.convertedFuel = value;
//		else if (index == 7) reactor.maxFieldCharge = value;
		if (index == 20){
			reactor.processButtonPress(value);
		}
	}

	@Override
	public ItemStack slotClick(int slot, int button, int p_75144_3_, EntityPlayer player1) {
		if (slot == 37 && player1.inventory.getItemStack() == null){
			if (reactor.reactorFuel / 144 >= 64){
				int i = reactor.reactorFuel / 1296;
				int i2 = Math.min(64, i);
				ioSlots.getStorage()[1] = new ItemStack(ModBlocks.draconicBlock, i2);
				reactor.reactorFuel -= i2 * 1296;
			}
			else if (reactor.reactorFuel >= 144){
				int i = reactor.reactorFuel / 144;
				int i2 = Math.min(64, i);
				ioSlots.getStorage()[1] = new ItemStack(ModItems.draconicIngot, i2);
				reactor.reactorFuel -= i2 * 144;
			}

		}
		return super.slotClick(slot, button, p_75144_3_, player1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
		return null;
	}

	public static class SlotInsert extends Slot {
		private int numberAllowed = -1;
		private TileReactorCore reactor;

		public SlotInsert(IInventory iInventory, int iSlot, int x, int y, TileReactorCore reactor) {
			super(iInventory, iSlot, x, y);
			this.reactor = reactor;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			if (stack == null) return false;
			else if (stack.getItem() == ModItems.draconicIngot) numberAllowed = (10368 - (reactor.reactorFuel + reactor.convertedFuel)) / 144;
			else if (stack.getItem() == Item.getItemFromBlock(ModBlocks.draconicBlock)) numberAllowed = (10368 - (reactor.reactorFuel + reactor.convertedFuel)) / 1296;
			//	(stack.getItem() == ModItems.draconicIngot || stack.getItem() == Item.getItemFromBlock(ModBlocks.draconicBlock));
			LogHelper.info(numberAllowed);
			return numberAllowed > 0;
		}

		@Override
		public int getSlotStackLimit() {//todo add the fuel limit
			LogHelper.info(numberAllowed);
			return numberAllowed;
		}
	}

	public static class SlotExtract extends Slot {

		private GenericInventory inventory;

		public SlotExtract(GenericInventory iInventory, int iSlot, int x, int y) {
			super(iInventory, iSlot, x, y);
			inventory = iInventory;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	}
}
