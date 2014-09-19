package com.brandon3055.draconicevolution.client.interfaces.manual;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Brandon on 16/09/2014.
 */
public class DummyContainer extends Container {
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
