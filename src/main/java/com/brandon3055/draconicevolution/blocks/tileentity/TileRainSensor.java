package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.draconicevolution.blocks.RainSensor;
import com.brandon3055.draconicevolution.init.DEContent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileRainSensor extends TileBCore implements IRedstoneEmitter {

	public TileRainSensor(BlockPos pos, BlockState state) {
		super(DEContent.tile_rain_sensor, pos, state);
	}

	@Override
	public int getWeakPower(BlockState blockState, Direction side) {
		return blockState.getValue(RainSensor.ACTIVE) ? 15 : 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, Direction side) {
		return blockState.getValue(RainSensor.ACTIVE) ? 15 : 0;
	}

	@Override
	public void tick() {
		Level world = this.getLevel();
		if (world.getGameTime() % 20 == 0) {
			boolean raining = world.isRaining() && world.canSeeSky(getBlockPos());
			world.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(RainSensor.ACTIVE, raining));
		}
	}
}
