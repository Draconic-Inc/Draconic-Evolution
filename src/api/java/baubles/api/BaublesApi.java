package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.BaublesInventoryWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

/**
 * @author Azanor
 */
public class BaublesApi
{
	/**
	 * Retrieves the baubles inventory capability handler for the supplied player
	 */
	public static IBaublesItemHandler getBaublesHandler(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return handler;
	}

	/**
	 * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
	 */
	@Deprecated
	public static IInventory getBaubles(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return new BaublesInventoryWrapper(handler, player);
	}
	
	/**
	 * Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
	 * @return -1 if not found and slot number if it is found 
	 */
	public static int isBaubleEquipped(EntityPlayer player, Item bauble) {
		IBaublesItemHandler handler = getBaublesHandler(player);
		for (int a=0;a<handler.getSlots();a++) {
			if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem()==bauble) return a;
		}
		return -1;
	}
}
