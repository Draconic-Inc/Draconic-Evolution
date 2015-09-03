package com.brandon3055.draconicevolution.api;

import com.brandon3055.draconicevolution.common.world.WorldGenEnderComet;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderIsland;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Brandon on 11/7/2015.
 */
public class GeneratorOverride {

	public void spawnCometAt(World world, int x, int y, int z, Random rand) {
		new WorldGenEnderComet().generate(world, rand, x, y, z);
	}

	public void spawnChaosIslendAt(World world, int x, int y, int z, Random rand) {
		new WorldGenEnderIsland().generate(world, rand, x, y, z);
	}
}
