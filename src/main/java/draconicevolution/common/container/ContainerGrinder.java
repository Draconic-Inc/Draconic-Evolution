package draconicevolution.common.container;

import draconicevolution.client.interfaces.SlotItemValid;
import draconicevolution.common.tileentities.TileGrinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGrinder extends Container {

	private TileGrinder tile;

	public ContainerGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
		this.tile = tile;

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
			}
		}
		
		addSlotToContainer(new SlotItemValid(tile, 0, 64, 35, true));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tile.isUseableByPlayer(player);
	}

	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	public TileGrinder getTileWC(){
		return tile;
	}
}
