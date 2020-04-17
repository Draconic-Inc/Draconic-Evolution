package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.IWorldGenerator;

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

    public static FillerBlockType END_FILLER_TYPE = FillerBlockType.create("END_STONE", "end_stone", state -> state.getBlock() == Blocks.END_STONE);

    //Pop Pre -> Pop Post -> Generate
    //Save/load are not directly linked load is not called when generating

    public static void initialize() {
        if (DEConfig.worldGenEnabled) {
//            ForgeRegistries.CHUNK_GENERATOR_TYPES.register(instance);
//            GameRegistry.registerWorldGenerator(instance, 0);
            MinecraftForge.EVENT_BUS.register(instance);

            if (DEConfig.enableRetroGen) {
                MinecraftForge.EVENT_BUS.register(new WorldTickHandler());
            }
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider) {
        addOreGen(random, chunkX, chunkZ, world, chunkGenerator);
    }

    public void addOreGen(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator) {
        DimensionType type = world.getDimension().getType();

        if (type == DimensionType.OVERWORLD) {
            if (!DEConfig.disableOreSpawnOverworld) {
                addOreSpawn(DEContent.ore_draconium_overworld.getDefaultState(), FillerBlockType.NATURAL_STONE, chunkGenerator, world, random, chunkX * 16, chunkZ * 16, 6, 10, 2, 2, 8);
            }
        } else if (type == DimensionType.THE_END) {
            int actualX = chunkX * 16;
            int actualZ = chunkZ * 16;
//            int x1 = actualX + random.nextInt(16);
            int y = 20 + random.nextInt(170);
//            int z1 = actualZ + random.nextInt(16);
            if (DEConfig.generateEnderComets && Math.sqrt(actualX * actualX + actualZ * actualZ) > 200 && random.nextInt(Math.max(1, DEConfig.cometRarity)) == 0) {
                new WorldGenEnderComet().generate(world, random, new BlockPos(actualX + 8, y, actualZ + 8));
            }
            if (DEConfig.generateChaosIslands) {
                ChaosWorldGenHandler.generateChunk(world, chunkX, chunkZ, null, random);
            }
            if (!DEConfig.disableOreSpawnEnd) {
                addOreSpawn(DEContent.ore_draconium_end.getDefaultState(), END_FILLER_TYPE, chunkGenerator, world, random, actualX, actualZ, 4, 5, 10, 1, 70);
            }
        } else if (type == DimensionType.THE_NETHER) {
            if (!DEConfig.disableOreSpawnNether) {
                addOreSpawn(DEContent.ore_draconium_nether.getDefaultState(), FillerBlockType.NETHERRACK, chunkGenerator, world, random, chunkX * 16, chunkZ * 16, 5, 7, 5, 1, 125);
            }
        } else {
            for (String name : DEConfig.oreGenDimentionBlacklist) {
                if (name.equals(world.getDimension().getType().getRegistryName().toString())) {
                    return;
                }
            }
            addOreSpawn(DEContent.ore_draconium_overworld.getDefaultState(), FillerBlockType.NATURAL_STONE, chunkGenerator, world, random, chunkX * 16, chunkZ * 16, 3, 4, 2, 2, 8);
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
    public void addOreSpawn(BlockState block, FillerBlockType fillerBlockType, ChunkGenerator chunkGenerator, World world, Random random, int chunkXPos, int chunkZPos, int minVainSize, int maxVainSize, int chancesToSpawn, int minY, int maxY) {
        for (int i = 0; i < chancesToSpawn; i++) {
            int posX = chunkXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = chunkZPos + random.nextInt(16);

            OreFeatureConfig config = new OreFeatureConfig(fillerBlockType, block, minVainSize + random.nextInt(maxVainSize - minVainSize));

            new OreFeature(dynamic -> config).place(world, chunkGenerator, random, new BlockPos(posX, posY, posZ), config);
        }
    }

    @SubscribeEvent
    public void chunkLoadEvent(ChunkDataEvent.Load event) {
        DimensionType dim = event.getWorld().getDimension().getType();

        CompoundNBT tag = event.getData().getCompound(DATA_TAG);

        if (tag.getBoolean("Generated") || tag.getBoolean("Loaded")) {
            return;
        }

        tag.putBoolean("Loaded", true);
        event.getData().put(DATA_TAG, tag);

        if (event.getChunk().isEmptyBetween(0, 128) && dim != DimensionType.THE_END) {
            return;
        }

        if (DEConfig.enableRetroGen) {
            ArrayDeque<ChunkPos> chunks = WorldTickHandler.chunksToGen.get(dim);

            if (chunks == null) {
                WorldTickHandler.chunksToGen.put(dim, new ArrayDeque<>(128));
                chunks = WorldTickHandler.chunksToGen.get(dim);
            }
            if (chunks != null) {
                chunks.addLast(new ChunkPos(event.getChunk().getPos().x, event.getChunk().getPos().z));
                retroGenerating.add(new ChunkReference(dim, event.getChunk().getPos().x, event.getChunk().getPos().z));
                WorldTickHandler.chunksToGen.put(dim, chunks);
            }
        }
    }

    public void retroGenComplete(DimensionType dim, int chunkX, int chunkZ) {
        retroGenerating.remove(new ChunkReference(dim, chunkX, chunkZ));
    }

    @SubscribeEvent
    public void chunkSaveEvent(ChunkDataEvent.Save event) {
        CompoundNBT genTag = event.getData().getCompound(DATA_TAG);

        if (!retroGenerating.contains(new ChunkReference(event.getWorld().getDimension().getType(), event.getChunk().getPos().x, event.getChunk().getPos().z))) {
            genTag.putBoolean("Generated", true);
        }

        genTag.remove("Loaded");
        event.getData().put(DATA_TAG, genTag);
    }


    private static class ChunkReference {
        public final DimensionType dimension;
        public final int xPos;
        public final int zPos;

        public ChunkReference(DimensionType dim, int x, int z) {
            dimension = dim;
            xPos = x;
            zPos = z;
        }

        @Override
        public int hashCode() {
            return xPos * 43 + zPos * 3 + dimension.getId();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) {
                if (o instanceof Chunk) {
                    Chunk other = (Chunk) o;
                    return xPos == other.getPos().x && zPos == other.getPos().z && dimension == other.getWorld().getDimension().getType();
                }
                return false;
            }
            ChunkReference other = (ChunkReference) o;
            return other.dimension == dimension && other.xPos == xPos && other.zPos == zPos;
        }

    }
}
