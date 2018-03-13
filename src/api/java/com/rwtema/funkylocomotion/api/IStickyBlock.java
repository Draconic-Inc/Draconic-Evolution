package com.rwtema.funkylocomotion.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStickyBlock {

	/**
	 * @param world The world.
	 * @param pos   The pos.
	 * @param side  The side we are checking for stickiness.
	 * @return If we are sticky.
	 */
	boolean isStickySide(World world, BlockPos pos, EnumFacing side);
}