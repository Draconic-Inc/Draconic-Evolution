package com.rwtema.funkylocomotion.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMoveFactory {
	CompoundNBT destroyBlock(World world, BlockPos pos);

	boolean recreateBlock(World world, BlockPos pos, CompoundNBT tag);
}