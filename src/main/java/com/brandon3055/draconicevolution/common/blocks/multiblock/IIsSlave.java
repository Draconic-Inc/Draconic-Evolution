package com.brandon3055.draconicevolution.common.blocks.multiblock;

/**
 * Created by Brandon on 23/7/2015.
 */
public interface IIsSlave {
	public MultiblockHelper.TileLocation getMaster();

	public void shutDown();

	public boolean checkForMaster();

	public boolean isActive();
}
