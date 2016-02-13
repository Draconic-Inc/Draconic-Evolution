package com.brandon3055.draconicevolution.common.items.armor;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/2/2016.
 */
public interface ICustomArmor {

	/**Returns the max number of protection points this armor piece can provide
	 * 1 protection point equals 1 half heart of protection.
	 * */
	public float getProtectionPoints(ItemStack stack);

	/**Reruns the number of Upgrade points applied to shield recovery*/
	public int getRecoveryPoints(ItemStack stack);

	/*
	TODO
\
	Create a damage event handler
	Handle all the damage calculations and save the current remaining protection points directly to the item from the damage handler
	When player is hit save the ticks till regen can start and the new entropy value directly to the armor
	Work out overall recovery by averaging the recovery points
	use a player tick handler to regen the armor and save tick down the entropy

	*/


}
