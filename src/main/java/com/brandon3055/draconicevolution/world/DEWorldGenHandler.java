package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.DraconiumOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by brandon3055 on 24/3/2016.
 * This class will handle generation and retro generation of DE ores
 * This method of retrogening is borrowed from CoFH. Because its just so good!
 */
public class DEWorldGenHandler implements IWorldGenerator {
    public static DEWorldGenHandler instance = new DEWorldGenHandler();
    private static String DATA_TAG = "DEWorldGen";
    private static HashSet<ChunkReference> retroGenerating = new HashSet<ChunkReference>();

    //Pop Pre -> Pop Post -> Generate
    //Save/load are not directly linked load is not called when generating

    public static void initialize() {
        if (DEConfig.worldGenEnabled) {
            GameRegistry.registerWorldGenerator(instance, 0);
            MinecraftForge.EVENT_BUS.register(instance);

            if (DEConfig.enableRetroGen) {
                MinecraftForge.EVENT_BUS.register(new WorldTickHandler());
            }
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        addOreGen(random, chunkX, chunkZ, world);
    }

    public void addOreGen(Random random, int chunkX, int chunkZ, World world) {
        switch (world.provider.getDimension()) {
            case 0:
                if (!DEConfig.disableOreSpawnOverworld) {
                    addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.NORMAL), Blocks.STONE.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 6, 10, 2, 2, 8);
                }
                break;
            case 1:
                int actualX = chunkX * 16;
                int actualZ = chunkZ * 16;
                int x1 = actualX + random.nextInt(16);
                int y = 20 + random.nextInt(170);
                int z1 = actualZ + random.nextInt(16);
                if (DEConfig.generateEnderComets && Math.sqrt(actualX * actualX + actualZ * actualZ) > 200 && random.nextInt(Math.max(1, DEConfig.cometRarity)) == 0) {
                    new WorldGenEnderComet().generate(world, random, new BlockPos(actualX + 8, y, actualZ + 8));
                }
                if (DEConfig.generateChaosIslands) {
                    ChaosWorldGenHandler.generateChunk(world, chunkX, chunkZ, null, random);
                }
                if (!DEConfig.disableOreSpawnEnd) {
                    addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.END), Blocks.END_STONE.getDefaultState(), world, random, actualX, actualZ, 4, 5, 10, 1, 70);
                }

                break;
            case -1:
                if (!DEConfig.disableOreSpawnNether) {
                    addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.NETHER), Blocks.NETHERRACK.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 5, 7, 5, 1, 125);
                }
                break;
            default:
                for (Integer i : DEConfig.oreGenDimentionBlacklist) {
                    if (i == world.provider.getDimension()) return;
                }
                addOreSpawn(DEFeatures.draconiumOre.getDefaultState().withProperty(DraconiumOre.ORE_TYPE, DraconiumOre.EnumType.NORMAL), Blocks.STONE.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 3, 4, 2, 2, 8);
                break;
        }
    }

    /**
     * Generate Ore
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
     **/
    public void addOreSpawn(IBlockState block, IBlockState baseBlock, World world, Random random, int chunkXPos, int chunkZPos, int minVainSize, int maxVainSize, int chancesToSpawn, int minY, int maxY) {
        for (int i = 0; i < chancesToSpawn; i++) {
            int posX = chunkXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = chunkZPos + random.nextInt(16);

            new WorldGenMinable(block, (minVainSize + random.nextInt(maxVainSize - minVainSize)), BlockMatcher.forBlock(baseBlock.getBlock())).generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

    @SubscribeEvent
    public void chunkLoadEvent(ChunkDataEvent.Load event) {
        int dim = event.getWorld().provider.getDimension();

        NBTTagCompound tag = event.getData().getCompoundTag(DATA_TAG);

        if (tag.getBoolean("Generated") || tag.getBoolean("Loaded")) {
            return;
        }

        tag.setBoolean("Loaded", true);
        event.getData().setTag(DATA_TAG, tag);

        if (event.getChunk().isEmptyBetween(0, 128) && dim != 1) {
            return;
        }

        if (DEConfig.enableRetroGen) {
            ArrayDeque<ChunkPos> chunks = WorldTickHandler.chunksToGen.get(dim);

            if (chunks == null) {
                WorldTickHandler.chunksToGen.put(dim, new ArrayDeque<>(128));
                chunks = WorldTickHandler.chunksToGen.get(dim);
            }
            if (chunks != null) {
                chunks.addLast(new ChunkPos(event.getChunk().x, event.getChunk().z));
                retroGenerating.add(new ChunkReference(dim, event.getChunk().x, event.getChunk().z));
                WorldTickHandler.chunksToGen.put(dim, chunks);
            }
        }
    }

    public void retroGenComplete(int dim, int chunkX, int chunkZ) {
        retroGenerating.remove(new ChunkReference(dim, chunkX, chunkZ));
    }

    @SubscribeEvent
    public void chunkSaveEvent(ChunkDataEvent.Save event) {
        NBTTagCompound genTag = event.getData().getCompoundTag(DATA_TAG);

        if (!retroGenerating.contains(new ChunkReference(event.getWorld().provider.getDimension(), event.getChunk().x, event.getChunk().z))) {
            genTag.setBoolean("Generated", true);
        }

        genTag.removeTag("Loaded");
        event.getData().setTag(DATA_TAG, genTag);
    }


    private static class ChunkReference {
        public final int dimension;
        public final int xPos;
        public final int zPos;

        public ChunkReference(int dim, int x, int z) {
            dimension = dim;
            xPos = x;
            zPos = z;
        }

        @Override
        public int hashCode() {
            return xPos * 43 + zPos * 3 + dimension;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) {
                if (o instanceof Chunk) {
                    Chunk other = (Chunk) o;
                    return xPos == other.x && zPos == other.z && dimension == other.getWorld().provider.getDimension();
                }
                return false;
            }
            ChunkReference other = (ChunkReference) o;
            return other.dimension == dimension && other.xPos == xPos && other.zPos == zPos;
        }

    }
}
