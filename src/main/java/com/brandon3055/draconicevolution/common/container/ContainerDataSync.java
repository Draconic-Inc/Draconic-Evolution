package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.network.ObjectPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

/**
 * Created by Brandon on 14/11/2014.
 */
/**This class is for syncing values to bug for ICrafting*/
public abstract class ContainerDataSync extends Container {

	/**
	 * Sends two ints to the client-side Container. Normally the first int identifies which variable to update, and the second contains the new
	 * value.
	 * if tile != null the packet will be sent to the tile client tile instead of the client container.
	 */
	public Object sendObject(TileObjectSync tile, short dataType, int index, Object object){
		for (Object p : crafters){
			DraconicEvolution.network.sendTo(new ObjectPacket(tile, ObjectPacket.INT, index, object), (EntityPlayerMP)p);
		}
		return object;
	}

	/**Called when the client receives a sync packet from the server*/
	@SideOnly(Side.CLIENT)
	public abstract void receiveSyncData(int index, Object value);
}
