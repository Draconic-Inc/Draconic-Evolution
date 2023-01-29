package com.brandon3055.draconicevolution.common.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.brandon3055.draconicevolution.common.ModBlocks;

/**
 * Created by Brandon on 28/08/2014.
 */
public class WorldGenEnderComet extends WorldGenerator {

    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int tailX;
    private int tailY;
    private int tailZ;
    private int size;

    private void initialize(Random rand, int x, int y, int z) {
        spawnX = x;
        spawnY = y;
        spawnZ = z;
        double rotation = rand.nextInt();
        double xmod = Math.sin(rotation);
        double zmod = Math.cos(rotation);
        int distMod = 150 + rand.nextInt(50);
        tailX = x + (int) (xmod * distMod);
        tailY = y + 40 + rand.nextInt(40);
        tailZ = z + (int) (zmod * distMod);
        size = 2 + rand.nextInt(8);
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        initialize(random, x, y, z);

        generateCore(world, random, size);
        generateTrail(world, random);

        return true;
    }

    private void generateCore(World world, Random rand, int r) {
        for (int x = spawnX - r; x <= spawnX + r; x++) {
            for (int z = spawnZ - r; z <= spawnZ + r; z++) {
                for (int y = spawnY - r; y <= spawnY + r; y++) {
                    if ((int) (getDistance(x, y, z, spawnX, spawnY, spawnZ)) <= r) {
                        float genP = rand.nextFloat();

                        if (0.1F > genP) {
                            world.setBlock(x, y, z, ModBlocks.draconiumOre, 0, 2);
                        } else if (0.4F > genP) {
                            world.setBlock(x, y, z, Blocks.obsidian, 0, 2);
                        } else {
                            world.setBlock(x, y, z, Blocks.end_stone, 0, 2);
                        }
                    }
                }
            }
        }
    }

    private void generateTrail(World world, Random rand) {
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
            generateTrailSphere(world, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);

            density = 1000 - (int) (pc * 10000);
            generateTrailSphere(world, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);
        }
    }

    public static void generateTrailSphere(World world, int xi, int yi, int zi, int r, int density, Random rand) {
        if (density <= 0) return;
        if (density > 10000) density = 10000;
        for (int x = xi - r; x <= xi + r; x++) {
            for (int z = zi - r; z <= zi + r; z++) {
                for (int y = yi - r; y <= yi + r; y++) {
                    if ((density >= rand.nextInt(10000)) && world.isAirBlock(x, y, z)
                            && (int) (getDistance(x, y, z, xi, yi, zi)) == r) {
                        if (0.9F >= rand.nextFloat()) world.setBlock(x, y, z, Blocks.end_stone, 0, 2);
                        else if (rand.nextBoolean()) world.setBlock(x, y, z, Blocks.obsidian, 0, 2);
                        else world.setBlock(x, y, z, ModBlocks.draconiumOre, 0, 2);
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
}
