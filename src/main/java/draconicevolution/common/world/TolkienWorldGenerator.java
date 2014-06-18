package draconicevolution.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;
import draconicevolution.common.blocks.ModBlocks;

public class TolkienWorldGenerator implements IWorldGenerator {
	@Override
	public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider)
	{
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

	public void generateSurface(final Random random, final int chunkX, final int chunkZ, final World world)
	{//                                                                    minVainSise, maxVainSize, spawnChance(Def2), minY, maxY
		//addOreSpawn(ModBlocks.MySidedBlock, world, random, chunkX, chunkZ, 10, 50, 10, 4, 100);
	}

	public void generateEnd(final Random random, final int chunkX, final int chunkZ, final World world)
	{
		addEndOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 4, 4, 10, 1, 70);
	}

	public void generateNether(final Random random, final int chunkX, final int chunkZ, final World world)
	{
		addNetherOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 3, 4, 5, 1, 125);
	}

	public void addEndOreSpawn(final Block block, final World world, final Random random, final int blockXPos, final int blockZPos, final int minVainSize, final int maxVainSize, final int chancesToSpawn, final int minY, final int maxY)
	{
		for (int i = 0; i < chancesToSpawn; i++) {
			final int posX = blockXPos + random.nextInt(16);
			final int posY = minY + random.nextInt(maxY - minY);
			final int posZ = blockZPos + random.nextInt(16);
			new WorldGenMinable(block, 3, Blocks.end_stone).generate(world, random, posX, posY, posZ);
		}
	}

	public void addOverworldOreSpawn(final Block block, final World world, final Random random, final int blockXPos, final int blockZPos, final int minVainSize, final int maxVainSize, final int chancesToSpawn, final int minY, final int maxY)
	{
		for (int i = 0; i < chancesToSpawn; i++) {
			final int posX = blockXPos + random.nextInt(16);
			final int posY = minY + random.nextInt(maxY - minY);
			final int posZ = blockZPos + random.nextInt(16);
			new WorldGenMinable(block, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.stone).generate(world, random, posX, posY, posZ);
		}
	}

	public void addNetherOreSpawn(final Block block, final World world, final Random random, final int blockXPos, final int blockZPos, final int minVainSize, final int maxVainSize, final int chancesToSpawn, final int minY, final int maxY)
	{
		for (int i = 0; i < chancesToSpawn; i++) {
			final int posX = blockXPos + random.nextInt(16);
			final int posY = minY + random.nextInt(maxY - minY);
			final int posZ = blockZPos + random.nextInt(16);
			new WorldGenMinable(block, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.netherrack).generate(world, random, posX, posY, posZ);
		}
	}

}
