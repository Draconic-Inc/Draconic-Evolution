package com.brandon3055.draconicevolution.world;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

/**
 * Created by Brandon on 28/08/2014.
 */
public class WorldGenEnderComet
{
    public static boolean place(ISeedReader seed, ChunkGenerator chunk, Random rand, BlockPos pos, NoFeatureConfig config) {

        double rotation = rand.nextInt();
        double xmod = Math.sin(rotation);
        double zmod = Math.cos(rotation);
        int distMod = 150 + rand.nextInt(50);
        int tailX = pos.getX() + (int) (xmod * distMod);
        int tailY = pos.getY() + 40 + rand.nextInt(40);
        int tailZ = pos.getZ() + (int) (zmod * distMod);
        int size = 2 + rand.nextInt(8);


        setCascadingWarningEnabled(false);

        generateCore(seed, rand, size, pos.getX(), pos.getY(), pos.getZ());
        //generateTrail(seed, rand, pos.getX(), pos.getY(), pos.getZ(), tailX, tailY, tailZ, size); // Todo: fix trail sphere generation

        setCascadingWarningEnabled(true);

        return true;

    }

    private static void generateCore(ISeedReader seed, Random rand, int r, int spawnX, int spawnY, int spawnZ) {
        for (int x = spawnX - r; x <= spawnX + r; x++) {
            for (int z = spawnZ - r; z <= spawnZ + r; z++) {
                for (int y = spawnY - r; y <= spawnY + r; y++) {
                    if ((int) (getDistance(x, y, z, spawnX, spawnY, spawnZ)) <= r) {
                        float genP = rand.nextFloat();
                        BlockPos pos = new BlockPos(x, y, z);
                        if (0.2F > genP) {
                            seed.setBlock(pos, DEContent.ore_draconium_end.defaultBlockState(), 3);
                        }
                        else if (0.4F > genP) {
                            seed.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                        else {
                            seed.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    private static void generateTrail(ISeedReader seedReader, Random rand, int tailX, int tailY, int tailZ, int spawnX, int spawnY, int spawnZ, int size) {
        int xDiff = tailX - spawnX;
        int yDiff = tailY - spawnY;
        int zDiff = tailZ - spawnZ;

        for (int p = 0; p < 100; p += 2) {
            int cX = spawnX + (int) (((float) p / 100F) * xDiff);
            int cY = spawnY + (int) (((float) p / 100F) * yDiff);
            int cZ = spawnZ + (int) (((float) p / 100F) * zDiff);
            float pc = (float) p / 100F;

            int density = 500 - (int) (pc * 550);
            if (density < 20) density = 20;
            generateTrailSphere(seedReader, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);

            density = 1000 - (int) (pc * 10000);
            generateTrailSphere(seedReader, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);

        }
    }


    //Todo: Figure out how to make this work
    // Error message: "We are asking a region for a chunk out of bound"
    public static void generateTrailSphere(ISeedReader seedReader, int xi, int yi, int zi, int r, int density, Random rand) {
        if (density <= 0) return;
        if (density > 10000) density = 10000;
        for (int x = xi - r; x <= xi + r; x++) {
            for (int z = zi - r; z <= zi + r; z++) {
                for (int y = yi - r; y <= yi + r; y++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (density >= rand.nextInt(10000) && (int) (getDistance(x, y, z, xi, yi, zi)) == r) {
                        if (0.9F >= rand.nextFloat()) {
                            seedReader.setBlock(pos, Blocks.END_STONE.defaultBlockState(), 1);
                        }
                        else if (rand.nextBoolean()) {
                            seedReader.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 1);
                        }
                        else {
                            seedReader.setBlock(pos, DEContent.ore_draconium_end.defaultBlockState(), 1);
                        }
                    }
                }
            }
        }
    }

    public static double getDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        int dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    private static ChunkPos popPos = null;
    private static ObfMapping mapping = new ObfMapping("net/minecraft/world/chunk/Chunk", "populating");

    //Given the rarity of the comets i can almost guarantee cascading world gen will never cause any actual problems.
    //If you dont like it feel free to suggest a working solution.
    private static void setCascadingWarningEnabled(boolean enabled) {
        try {
            if (enabled) {
                ReflectionManager.setField(mapping, null, popPos);
                popPos = null;
            }
            else {
                popPos = ReflectionManager.getField(mapping, null);
                ReflectionManager.setField(mapping, null, null);
            }
        } catch (Throwable ignored) {
            //oops
        }
    }


}

