package com.brandon3055.draconicevolution.common.world;

import com.brandon3055.draconicevolution.common.DEConfig;
import com.brandon3055.draconicevolution.common.DEFeatures;
import com.brandon3055.draconicevolution.common.blocks.DraconiumOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

/**
 * Created by brandon3055 on 24/3/2016.
 * This class will handle generation and retro generation of DE ores
 */
public class DEWorldGenHandler implements IWorldGenerator {
	public static DEWorldGenHandler instance = new DEWorldGenHandler();
	private static String DATA_TAG = "DEWorld";

	public static void initialize(){
		GameRegistry.registerWorldGenerator(instance, 0);
		MinecraftForge.EVENT_BUS.register(instance);
	}


	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		addOreGen(random, chunkX, chunkZ, world, false);
	}


	public void addOreGen(Random random, int chunkX, int chunkZ, World world, boolean retroGen){
		switch (world.provider.getDimensionId()){
			case 0:
				if (!DEConfig.disableOreSpawnOverworld) {
					addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.NORMAL), Blocks.stone.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 3, 4, 2, 2, 8);
				}
				break;
			case 1:
				int actualX = chunkX * 16;
				int actualZ = chunkZ * 16;
				int x1 = actualX + random.nextInt(16);
				int y = 20 + random.nextInt(170);
				int z1 = actualZ + random.nextInt(16);
				if (DEConfig.generateEnderComets && Math.sqrt(actualX*actualX + actualZ*actualZ) > 200 && random.nextInt(Math.max(1, DEConfig.cometRarity)) == 0) new WorldGenEnderComet().generate(world, random, new BlockPos(x1, y , z1));
				if (DEConfig.generateChaosIslands) ChaosWorldGenHandler.generateChunk(world, chunkX, chunkZ, null, random);
				if (!DEConfig.disableOreSpawnEnd)addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.END), Blocks.end_stone.getDefaultState(), world, random, actualX, actualZ, 4, 5, 10, 1, 70);

				break;
			case -1:
				if (!DEConfig.disableOreSpawnNether){
					addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.NETHER), Blocks.netherrack.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 3, 4, 5, 1, 125);
				}
				break;
			default:
				break;
		}
	}

	/**Generate Ore
	 * Block to generate
	 * Block to overwrite
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
	public void addOreSpawn(IBlockState block, IBlockState baseBlock, World world, Random random, int chunkXPos, int chunkZPos, int minVainSize, int maxVainSize, int chancesToSpawn, int minY, int maxY) {
		for (int i = 0; i < chancesToSpawn; i++) {
			int posX = chunkXPos + random.nextInt(16);
			int posY = minY + random.nextInt(maxY - minY);
			int posZ = chunkZPos + random.nextInt(16);

			new WorldGenMinable(block, (minVainSize + random.nextInt(maxVainSize - minVainSize)), BlockHelper.forBlock(baseBlock.getBlock())).generate(world, random, new BlockPos(posX, posY, posZ));
		}
	}

	@SubscribeEvent
	public void chunkLoadEvent(ChunkDataEvent.Load event){

	}

	@SubscribeEvent
	public void chunkSaveEvent(ChunkDataEvent.Save event){

	}
}
