package com.brandon3055.draconicevolution.common.world;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class DraconicWorldGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
			case 0:
				generateSurface(random, chunkX * 16, chunkZ * 16, world);
				break;
			case 1:
				generateEnd(random, chunkX * 16, chunkZ * 16, world);
				break;
			case -1:
				generateNether(random, chunkX * 16, chunkZ * 16, world);
				break;
			default:
				break;
		}

	}

	public void generateSurface(Random random, int x, int z, World world) {//                                                                    minVainSise, maxVainSize, spawnChance(Def2), minY, maxY
		addOreSpawn(ModBlocks.draconiumOre, world, random, x, z, 3, 4, 2, 2, 8);
	}

	public void generateEnd(Random random, int x, int z, World world) {
		int x1 = x + random.nextInt(16);
		int y = 20 + random.nextInt(170);
		int z1 = z + random.nextInt(16);
		if (ConfigHandler.generateEnderComets && Math.sqrt(x*x + z*z) > 200 && random.nextInt(Math.max(1, ConfigHandler.cometRarity)) == 0) new WorldGenEnderComet().generate(world, random, x1, y , z1);
		int cX = x/16;
		int cZ = z/16;
		if (ConfigHandler.generateChaosIslands && Math.sqrt(x*x + z*z) > 9000 && cX%625 == 0 && cZ%625 == 0) new WorldGenEnderIsland().generate(world, random, x1, 100 , z1);
		addOreSpawn(ModBlocks.draconiumOre, world, random, x, z, 4, 5, 10, 1, 70);
	}

	public void generateNether(final Random random, final int chunkX, final int chunkZ, final World world) {
		addOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 3, 4, 5, 1, 125);
	}
	/**Generate Ore
	 * Block to generate
	 * World
	 * Random
	 * Chunk x
	 * Chunk z
	 * min vain size
	 * max vain size
	 * number of tries to spawn
	 * min y
	 * max y
	 * **/
	public void addOreSpawn(final Block block, final World world, final Random random, final int chunkXPos, final int chunkZPos, final int minVainSize, final int maxVainSize, final int chancesToSpawn, final int minY, final int maxY) {
		for (int i = 0; i < chancesToSpawn; i++) {
			final int posX = chunkXPos + random.nextInt(16);
			final int posY = minY + random.nextInt(maxY - minY);
			final int posZ = chunkZPos + random.nextInt(16);
			new WorldGenMinable(block, 0, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.end_stone).generate(world, random, posX, posY, posZ);
			new WorldGenMinable(block, 0, 3, Blocks.stone).generate(world, random, posX, posY, posZ);
			new WorldGenMinable(block, 0, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.netherrack).generate(world, random, posX, posY, posZ);
		}
	}
}
