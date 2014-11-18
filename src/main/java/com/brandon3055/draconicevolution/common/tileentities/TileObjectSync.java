package com.brandon3055.draconicevolution.common.tileentities;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.network.ObjectPacket;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Brandon on 14/11/2014.
 */
public abstract class TileObjectSync extends TileEntity {

	public Object sendObject(byte dataType, int index, Object object){
		return sendObject(dataType, index, object, new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64));
	}

	public Object sendObject(byte dataType, int index, Object object, TargetPoint point){
		DraconicEvolution.network.sendToAllAround(new ObjectPacket(this, dataType, index, object), point);
		return object;
	}

	@SideOnly(Side.CLIENT)
	public abstract void receiveObject(int index, Object object);
}
