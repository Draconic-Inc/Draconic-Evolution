package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Brandon on 15/01/2015.
 */
public class ContainerToolConfig extends ContainerDataSync {//todo delete
	public int slot = -1;

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	@Override
	public void receiveSyncData(int index, int value) {
		if (index == 0) slot = value;
	}

	public void setSlot(int slot){
		if (DraconicEvolution.proxy.isDedicatedServer()){
			sendObjectToServer(null, 0, slot);
		}
		this.slot = slot;
	}

}
