package com.rwtema.funkylocomotion.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAdvStickyBlock {
	Iterable<BlockPos> getBlocksToMove(World world, BlockPos pos);
}