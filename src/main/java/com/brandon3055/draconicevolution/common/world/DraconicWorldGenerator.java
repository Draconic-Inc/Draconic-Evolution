package com.brandon3055.draconicevolution.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class DraconicWorldGenerator implements IWorldGenerator {

    public static boolean chaosIslandsEnabled = true;
    public static boolean cometsEnabled = true;
    public static boolean oreEnabledInEnd = true;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
            IChunkProvider chunkProvider) {
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
                for (Integer i : ConfigHandler.oreGenDimentionBlacklist) {
                    if (i == world.provider.dimensionId) return;
                }
                addOreSpawn(ModBlocks.draconiumOre, world, random, chunkX * 16, chunkZ * 16, 3, 4, 2, 2, 8);
                break;
        }
    }

    public void generateSurface(Random random, int x, int z, World world) { // minVainSise,
        // maxVainSize, spawnChance(Def2), minY, maxY
        if (!ConfigHandler.disableOreSpawnOverworld)
            addOreSpawn(ModBlocks.draconiumOre, world, random, x, z, 3, 4, 2, 2, 8);
    }

    public void generateEnd(Random random, int x, int z, World world) {
        int x1 = x + random.nextInt(16);
        int y = 20 + random.nextInt(170);
        int z1 = z + random.nextInt(16);
        if (cometsEnabled && ConfigHandler.generateEnderComets
                && Math.sqrt(x * x + z * z) > 200
                && random.nextInt(Math.max(1, ConfigHandler.cometRarity)) == 0)
            new WorldGenEnderComet().generate(world, random, x1, y, z1);
        if (chaosIslandsEnabled && ConfigHandler.generateChaosIslands)
            ChaosWorldGenHandler.generateChunk(world, x / 16, z / 16, null, random);
        if (oreEnabledInEnd && !ConfigHandler.disableOreSpawnEnd)
            addOreSpawn(ModBlocks.draconiumOre, world, random, x, z, 4, 5, 10, 1, 70);
    }

    public void generateNether(Random random, int chunkX, int chunkZ, World world) {
        if (!ConfigHandler.disableOreSpawnNether)
            addOreSpawn(ModBlocks.draconiumOre, world, random, chunkX, chunkZ, 3, 4, 5, 1, 125);
    }

    /**
     * Generate Ore Block to generate World Random Chunk x Chunk z min vain size max vain size number of tries to spawn
     * min y max y *
     */
    public void addOreSpawn(Block block, World world, Random random, int chunkXPos, int chunkZPos, int minVainSize,
            int maxVainSize, int chancesToSpawn, int minY, int maxY) {
        for (int i = 0; i < chancesToSpawn; i++) {
            int posX = chunkXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = chunkZPos + random.nextInt(16);
            new WorldGenMinable(block, 0, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.end_stone)
                    .generate(world, random, posX, posY, posZ);
            new WorldGenMinable(block, 0, 3, Blocks.stone).generate(world, random, posX, posY, posZ);
            new WorldGenMinable(block, 0, (minVainSize + random.nextInt(maxVainSize - minVainSize)), Blocks.netherrack)
                    .generate(world, random, posX, posY, posZ);
        }
    }
}
