package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by Brandon on 16/6/2015.
 */
public class TileReactorCore extends TileEntity {
	public float renderRotation = 0;



	@Override
	public void updateEntity() {
		if (worldObj.isRemote) renderRotation += 0.2F;
	}
}
