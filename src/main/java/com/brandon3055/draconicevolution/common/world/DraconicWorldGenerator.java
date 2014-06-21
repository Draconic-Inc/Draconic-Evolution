package com.brandon3055.draconicevolution.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;

public class DraconicWorldGenerator implements IWorldGenerator {
	@Override
	public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider) {
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
				;
		}

	}

	public void generateSurface(final Random random, final int chunkX, final int chunkZ, final World world) {//                                                                    minVainSise, maxVainSize, spawnChance(Def2), minY, maxY
		addOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 3, 4, 2, 2, 8);
	}

	public void generateEnd(final Random random, final int chunkX, final int chunkZ, final World world) {
		addOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 4, 5, 10, 1, 70);
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
