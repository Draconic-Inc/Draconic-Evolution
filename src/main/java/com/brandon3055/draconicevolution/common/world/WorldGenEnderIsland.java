package com.brandon3055.draconicevolution.common.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.entity.EntityChaosCrystal;
import com.brandon3055.draconicevolution.common.entity.EntityChaosGuardian;

/**
 * Created by Brandon on 29/08/2014.
 */
public class WorldGenEnderIsland extends WorldGenerator {

    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int size;

    private void initialize(Random rand, int x, int y, int z) {
        spawnX = x;
        spawnY = y;
        spawnZ = z;
        size = 150; // + rand.nextInt(8);
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // LogHelper.info("Generate");
        for (int y1 = y - 10; y1 < y + 10; y1++) {
            if (world.getBlock(x, y1, z) == Blocks.end_stone) {
                // LogHelper.info("cancel");
                return false;
            }
        }
        initialize(random, x, y, z);
        generateCentre(world, random);
        generateBelt(world, random, size + 50, size + 200);
        generateObelisks(world, random);
        EntityChaosGuardian dragon = new EntityChaosGuardian(world);
        dragon.setPositionAndUpdate(x, 180, z);
        world.spawnEntityInWorld(dragon);
        return true;
    }

    private void generateCentre(World world, Random rand) {
        int centreThikness = 10; // multiplied by 2 and + 1
        int curve = 2;
        int diffStart = 20; // (int)((double)size * 0.1D);
        int r = size;
        int offPoint = size * curve;

        for (int x = spawnX - r; x <= spawnX + r; x++) {
            for (int z = spawnZ - r; z <= spawnZ + r; z++) {
                for (int y = spawnY - (r / 2); y <= spawnY + (r / 2); y++) {
                    if ((int) (getDistance(x, y, z, spawnX, spawnY + offPoint + centreThikness, spawnZ)) >= offPoint
                            && (int) (getDistance(x, y, z, spawnX, spawnY - offPoint - centreThikness, spawnZ))
                                    >= offPoint
                            && (int) (getDistance(x, y, z, spawnX, spawnY, spawnZ)) <= r) {
                        int dist = (int) getDistance(x, y, z, spawnX, spawnY, spawnZ);
                        int diffusionPCt = getDiffusionPct(dist - diffStart, size - diffStart);
                        int yRand = (int) (Math.max(0, getDiffusionPctD(dist - 40, size - 40))
                                * (double) (40 - rand.nextInt(80)));
                        if (dist <= diffStart) {
                            if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                        } else if (dist < (int) ((double) size * 0.3D) && diffusionPCt > rand.nextInt(3000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (dist < (int) ((double) size * 0.4D) && diffusionPCt > rand.nextInt(4000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (dist < (int) ((double) size * 0.5D) && diffusionPCt > rand.nextInt(5000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (dist < (int) ((double) size * 0.6D) && diffusionPCt > rand.nextInt(6000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (dist < (int) ((double) size * 0.7D) && diffusionPCt > rand.nextInt(7000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (dist < (int) ((double) size * 0.8D) && diffusionPCt > rand.nextInt(8000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        } else if (diffusionPCt > rand.nextInt(9000)) {
                            if (0.95F > rand.nextFloat()) {
                                if (world.isAirBlock(x, y + yRand, z))
                                    world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
                            } else if (world.isAirBlock(x, y + yRand, z))
                                world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
                        }
                    }
                }
            }
        }
    }

    private int getDiffusionPct(int dist, int maxDist) {
        double d = (double) dist / (double) maxDist;
        int i = Math.max(1, (int) (d * 1000D));
        return 1000 - i;
    }

    private double getDiffusionPctD(int dist, int maxDist) {
        double d = (double) dist / (double) maxDist;
        return d;
    }

    private void generateObelisks(World world, Random rand) {

        for (int i = 0; i < 7; i++) {
            double rotation = i * 0.9D;
            int sX = spawnX + (int) (Math.sin(rotation) * 35D);
            int sZ = spawnZ + (int) (Math.cos(rotation) * 35D);
            generateObelisk(world, sX, spawnY + 10, sZ, false, rand);
        }

        for (int i = 0; i < 14; i++) {
            double rotation = i * 0.45D;
            int sX = spawnX + (int) (Math.sin(rotation) * 70D);
            int sZ = spawnZ + (int) (Math.cos(rotation) * 70D);
            generateObelisk(world, sX, spawnY + 10, sZ, true, rand);
        }
    }

    private void generateObelisk(World world, int x1, int y1, int z1, boolean outer, Random rand) {
        if (!outer) {
            world.setBlock(x1, y1 + 20, z1, ModBlocks.infusedObsidian, 0, 2);
            if (!world.isRemote) {
                EntityChaosCrystal crystal = new EntityChaosCrystal(world);
                crystal.setPosition(x1 + 0.5, y1 + 21, z1 + 0.5);
                world.spawnEntityInWorld(crystal);
            }
            for (int y = y1; y < y1 + 20; y++) {
                world.setBlock(x1, y, z1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 + 1, y, z1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 - 1, y, z1, Blocks.obsidian, 0, 2);
                world.setBlock(x1, y, z1 + 1, Blocks.obsidian, 0, 2);
                world.setBlock(x1, y, z1 - 1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 + 1, y, z1 + 1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 - 1, y, z1 - 1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 + 1, y, z1 - 1, Blocks.obsidian, 0, 2);
                world.setBlock(x1 - 1, y, z1 + 1, Blocks.obsidian, 0, 2);
            }
        } else {
            world.setBlock(x1, y1 + 40, z1, ModBlocks.infusedObsidian, 0, 2);
            if (!world.isRemote) {
                EntityChaosCrystal crystal = new EntityChaosCrystal(world);
                crystal.setPosition(x1 + 0.5, y1 + 41, z1 + 0.5);
                world.spawnEntityInWorld(crystal);
            }
            int diff = 0;
            for (int y = y1 + 20; y < y1 + 40; y++) {
                diff++;
                double pct = (double) diff / 25D;
                int r = 3;
                for (int x = x1 - r; x <= x1 + r; x++) {
                    for (int z = z1 - r; z <= z1 + r; z++) {
                        if (getDistance(x, z, x1, z1) <= r) {
                            if (pct > rand.nextDouble()) world.setBlock(x, y, z, Blocks.obsidian, 0, 2);
                        }
                    }
                }
            }

            int cageS = 2;
            for (int x = x1 - cageS; x <= x1 + cageS; x++) {
                for (int y = y1 - cageS; y <= y1 + cageS; y++) {
                    if (0.8F > rand.nextFloat()) world.setBlock(x, y + 41, z1 + cageS, Blocks.iron_bars, 0, 2);
                    if (0.8F > rand.nextFloat()) world.setBlock(x, y + 41, z1 - cageS, Blocks.iron_bars, 0, 2);
                }
            }
            for (int z = z1 - cageS; z <= z1 + cageS; z++) {
                for (int y = y1 - cageS; y <= y1 + cageS; y++) {
                    if (0.8F > rand.nextFloat()) world.setBlock(x1 + cageS, y + 41, z, Blocks.iron_bars, 0, 2);
                    if (0.8F > rand.nextFloat()) world.setBlock(x1 - cageS, y + 41, z, Blocks.iron_bars, 0, 2);
                }
            }
            for (int z = z1 - cageS; z <= z1 + cageS; z++) {
                for (int x = x1 - cageS; x <= x1 + cageS; x++) {
                    if (0.8F > rand.nextFloat()) world.setBlock(x, y1 + 44, z, Blocks.stone_slab, 6, 2);
                }
            }
        }
    }

    private void generateBelt(World world, Random random, int innerRadius, int outerRadius) {
        int r = outerRadius;
        for (int x = spawnX - r; x <= spawnX + r; x++) {
            for (int z = spawnZ - r; z <= spawnZ + r; z++) {
                int dist = (int) (getDistance(x, z, spawnX, spawnZ));
                if (dist < outerRadius && dist >= innerRadius) {
                    int y = spawnY + (int) ((double) (spawnX - x) * 0.2D) + (random.nextInt(10) - 5);
                    if (0.1F > random.nextFloat()) world.setBlock(x, y, z, Blocks.end_stone, 0, 2);
                    if (0.001F > random.nextFloat()) world.setBlock(x, y, z, ModBlocks.draconiumOre, 0, 2);
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

    public static double getDistance(int x1, int z1, int x2, int z2) {
        int dx = x1 - x2;
        int dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }
}
