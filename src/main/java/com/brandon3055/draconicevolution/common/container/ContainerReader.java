package draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import draconicevolution.common.core.utills.InventoryReader;
import draconicevolution.common.items.tools.Reader;

public class ContainerReader extends Container
{
	protected final InventoryReader itemInventory;

	public ContainerReader(IInventory inventoryPlayer, InventoryReader itemInventory) {
		this.itemInventory = itemInventory;

		for (int i = 0; i < 7; i++) {
			this.addSlotToContainer(new CustomSlot(itemInventory, i, 17 + (i * 21), 11));
		}

		for (int i = 0; i < 7; i++) {
			this.addSlotToContainer(new CustomSlot(itemInventory, i + 7, 17 + (i * 21), 36));
		}

		for (int i = 0; i < 7; i++) {
			this.addSlotToContainer(new CustomSlot(itemInventory, i + 14, 17 + (i * 21), 61));
		}

		bindPlayerInventory(inventoryPlayer, 10);
	}

	private void bindPlayerInventory(IInventory inventory, int yOffset) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, (84 + i * 18) + yOffset));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142 + yOffset));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.itemInventory.isUseableByPlayer(player);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public ItemStack slotClick(int slotID, int button, int par3, EntityPlayer player) {
		Slot slot = (slotID < 0 || slotID > this.inventorySlots.size()) ? null : (Slot) this.inventorySlots.get(slotID);
		if (slot != null && player.getCurrentEquippedItem() != null && slot.getHasStack() && slot.getStack().areItemStacksEqual(slot.getStack(), player.getCurrentEquippedItem())) return null;
		return super.slotClick(slotID, button, par3, player);
	}
	
	public static boolean isBook(ItemStack stack) {
		String name = Item.itemRegistry.getNameForObject(stack.getItem());
		if(name != null) {
			/*
			for (String check : Config.prefixes) {
				if (name.contains(check))
					return true;
			}*/
			return true;
		}

		return stack.getItem() instanceof Reader;
	}
	
	public static class CustomSlot extends Slot {
		public CustomSlot(IInventory inventoryPlayer, int id, int x, int y) {
			super(inventoryPlayer, id, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return true;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}

}
