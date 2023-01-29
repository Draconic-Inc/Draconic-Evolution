package com.brandon3055.draconicevolution.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.handlers.IProcess;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.common.utills.SimplexNoise;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.entity.EntityChaosCrystal;
import com.brandon3055.draconicevolution.common.entity.EntityChaosGuardian;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.tileentities.TileChaosShard;

/**
 * Created by brandon3055 on 9/9/2015.
 */
public class ChaosWorldGenHandler {

    /**
     * Used to generate the chaos island chunk by chunk
     *
     * @param world        The world.
     * @param chunkX       The X position of the chunk being generated.
     * @param chunkZ       The Z position of the chunk being generated.
     * @param islandCenter Where to generate the island. If left null the islands will generate in a 10000 by 10000
     *                     grid.
     */
    public static void generateChunk(World world, int chunkX, int chunkZ,
            DataUtills.XZPair<Integer, Integer> islandCenter, Random random) {
        DataUtills.XZPair<Integer, Integer> closestSpawn = islandCenter == null ? getClosestChaosSpawn(chunkX, chunkZ)
                : islandCenter;

        if (closestSpawn.x == 0 && closestSpawn.z == 0) return;
        int posX = chunkX * 16;
        int posZ = chunkZ * 16;
        int copyStartDistance = 180;
        if (Math.abs(posX - closestSpawn.x) > copyStartDistance || Math.abs(posZ - closestSpawn.z) > copyStartDistance)
            return;

        if (closestSpawn.x > posX && closestSpawn.x <= posX + 16
                && closestSpawn.z > posZ
                && closestSpawn.z <= posZ + 16)
            generateStructures(world, closestSpawn, random);

        // long l = System.nanoTime();

        for (int trueX = posX; trueX < posX + 16; trueX++) {
            for (int y = 0; y < 255; y++) {
                for (int trueZ = posZ; trueZ < posZ + 16; trueZ++) {
                    int x = trueX - closestSpawn.x;
                    int z = trueZ - closestSpawn.z;
                    int size = 80;
                    double dist = Math.sqrt(x * x + (y - 16) * (y - 16) + z * z);
                    double xd, yd, zd;
                    double density, centerFalloff, plateauFalloff, heightMapFalloff;

                    xd = (double) x / size;
                    yd = (double) y / (32);
                    zd = (double) z / size;

                    // Calculate Center Falloff
                    double diameterScale = 150D;
                    // centerFalloff = (1D - (dist / diameterScale)) * 1D;
                    centerFalloff = 1D / (dist * 0.05D);
                    if (centerFalloff < 0) centerFalloff = 0;

                    // Calculate Plateau Falloff
                    if (yd < 0.4D) plateauFalloff = yd * 2.5D;
                    else if (yd <= 0.6D) plateauFalloff = 1D;
                    else if (yd > 0.6D && yd < 1D) plateauFalloff = 1D - (yd - 0.6D) * 2.5D;
                    else plateauFalloff = 0;

                    // Trim Further calculations
                    if (plateauFalloff == 0 || centerFalloff == 0) continue;

                    // Calculate heightMapFalloff
                    heightMapFalloff = 0;
                    for (int octave = 1; octave < 5; octave++) heightMapFalloff += ((SimplexNoise
                            .noise(xd * octave + closestSpawn.x, zd * octave + closestSpawn.z) + 1) * 0.5D) * 0.01D
                            * (octave * 10D * 1 - (dist * 0.001D));
                    if (heightMapFalloff <= 0) heightMapFalloff = 0;
                    heightMapFalloff += ((0.5D - Math.abs(yd - 0.5D)) * 0.15D);
                    if (heightMapFalloff == 0) continue;

                    density = centerFalloff * plateauFalloff * heightMapFalloff;

                    if (density > 0.1 && world.getBlock(x + closestSpawn.x, y + 64, z + closestSpawn.z) == Blocks.air)
                        world.setBlock(
                                x + closestSpawn.x,
                                y + 64,
                                z + closestSpawn.z,
                                (dist > 60 || dist > random.nextInt(60)) ? Blocks.end_stone : Blocks.obsidian);

                    // if (density > 0.1 && world.getBlock(x + closestSpawn.x, y + 64, z + closestSpawn.z) ==
                    // Blocks.air) {
                    // boolean b = dist > 60 || dist > random.nextInt(60);
                    // world.setBlock(x + closestSpawn.x, y + 64, z + closestSpawn.z, (dist > 60 || dist >
                    // random.nextInt(60)) ? Blocks.end_stone : Blocks.obsidian);
                    // }
                }
            }
        }
    }

    public static void generateStructures(World world, DataUtills.XZPair<Integer, Integer> islandCenter,
            Random random) {
        int outerRadius = 330;

        // Gen Chaos Cavern
        int shardY = 80;

        int coreHeight = 10;
        int coreWidth = 20;

        for (int y = shardY - coreHeight; y <= shardY + coreHeight; y++) {
            int h = Math.abs(y - shardY);
            int inRadius = h - 3;
            double yp = (coreHeight - h) / (double) coreHeight;
            int outRadius = (int) (yp * coreWidth);
            outRadius -= (outRadius * outRadius) / 100;

            genCoreSlice(world, islandCenter.x, y, islandCenter.z, inRadius, shardY, coreWidth, true, random);
            genCoreSlice(world, islandCenter.x, y, islandCenter.z, outRadius, shardY, coreWidth, false, random);
        }

        world.setBlock(islandCenter.x, shardY, islandCenter.z, ModBlocks.chaosCrystal);
        TileChaosShard tileChaosShard = (TileChaosShard) world.getTileEntity(islandCenter.x, shardY, islandCenter.z);
        tileChaosShard.locationHash = tileChaosShard
                .getLocationHash(islandCenter.x, shardY, islandCenter.z, world.provider.dimensionId);

        EntityChaosGuardian guardian = new EntityChaosGuardian(world);
        guardian.setPositionAndUpdate(islandCenter.x, shardY, islandCenter.z);
        world.spawnEntityInWorld(guardian);

        // Gen Ring
        int rings = 4;
        int width = 20;
        int spacing = 8;
        for (int x = islandCenter.x - outerRadius; x <= islandCenter.x + outerRadius; x++) {
            for (int z = islandCenter.z - outerRadius; z <= islandCenter.z + outerRadius; z++) {
                int dist = (int) (Utills.getDistanceAtoB(x, z, islandCenter.x, islandCenter.z));
                for (int i = 0; i < rings; i++) {
                    // if (dist < outerRadius1 && dist >= innerRadius1 || dist < outerRadius2 && dist >= innerRadius2)
                    if (dist < (outerRadius - ((width + spacing) * i))
                            && dist >= (outerRadius - width - ((width + spacing) * i))) {
                        int y = 90 + (int) ((double) (islandCenter.x - x) * 0.1D) + (random.nextInt(10) - 5);
                        if (0.1F > random.nextFloat()) world.setBlock(x, y, z, Blocks.end_stone, 0, 2);
                        if (0.001F > random.nextFloat()) world.setBlock(x, y, z, ModBlocks.draconiumOre, 0, 2);
                    }
                }
            }
        }
        generateObelisks(world, islandCenter, random);
    }

    public static void genCoreSlice(World world, int xi, int yi, int zi, int ringRadius, int yc, int coreRadious,
            boolean fillIn, Random rand) {
        for (int x = xi - coreRadious; x <= xi + coreRadious; x++) {
            for (int z = zi - coreRadious; z <= zi + coreRadious; z++) {
                double dist = Utills.getDistanceAtoB(x, yi, z, xi, yc, zi);

                double oRad = coreRadious - (Math.abs(yc - yi) * Math.abs(yc - yi)) / 10;
                if (dist > oRad - 3D && rand.nextDouble() * 3D < dist - (oRad - 3D)) continue;

                if (fillIn && (int) (Utills.getDistanceAtoB(x, z, xi, zi)) <= ringRadius) {
                    if ((int) dist < 9) world.setBlock(x, yi, z, ModBlocks.infusedObsidian);
                    else world.setBlock(x, yi, z, Blocks.obsidian);
                } else if (!fillIn && (int) (Utills.getDistanceAtoB(x, z, xi, zi)) >= ringRadius) {
                    world.setBlock(x, yi, z, Blocks.obsidian);
                } else if (!fillIn && (int) Utills.getDistanceAtoB(x, z, xi, zi) <= ringRadius) {
                    Block b = world.getBlock(x, yi, z);
                    if (b == Blocks.air || b == Blocks.end_stone || b == Blocks.obsidian)
                        world.setBlock(x, yi, z, ModBlocks.chaosShardAtmos);
                }
            }
        }
    }

    public static DataUtills.XZPair<Integer, Integer> getClosestChaosSpawn(int chunkX, int chunkZ) {
        return new DataUtills.XZPair<Integer, Integer>(
                Utills.getNearestMultiple(chunkX * 16, ConfigHandler.chaosIslandSeparation),
                Utills.getNearestMultiple(chunkZ * 16, ConfigHandler.chaosIslandSeparation));
    }

    private static void generateObelisks(World world, DataUtills.XZPair<Integer, Integer> islandCenter, Random rand) {

        for (int i = 0; i < 7; i++) {
            double rotation = i * 0.9D;
            int sX = islandCenter.x + (int) (Math.sin(rotation) * 45D);
            int sZ = islandCenter.z + (int) (Math.cos(rotation) * 45D);
            generateObelisk(world, sX, 90, sZ, false, rand);
        }

        for (int i = 0; i < 14; i++) {
            double rotation = i * 0.45D;
            int sX = islandCenter.x + (int) (Math.sin(rotation) * 90D);
            int sZ = islandCenter.z + (int) (Math.cos(rotation) * 90D);
            generateObelisk(world, sX, 90, sZ, true, rand);
        }
    }

    private static void generateObelisk(World world, int x1, int y1, int z1, boolean outer, Random rand) {
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
                        if (Utills.getDistanceAtoB(x, z, x1, z1) <= r) {
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

    public static class CrystalRemover implements IProcess {

        private boolean dead = false;

        private Entity entity;
        private int delay = 2;

        public CrystalRemover(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void updateProcess() {
            if (delay > 0) delay--;
            else {
                boolean flag = true;
                int y = (int) entity.posY - 1;
                for (; flag;) {
                    flag = false;
                    for (int x = (int) Math.floor(entity.posX) - 4; x <= (int) Math.floor(entity.posX) + 4; x++) {
                        for (int z = (int) Math.floor(entity.posZ) - 4; z <= (int) Math.floor(entity.posZ) + 4; z++) {
                            Block block = entity.worldObj.getBlock(x, y, z);
                            if (block == Blocks.bedrock || block == Blocks.obsidian) {
                                flag = true;
                                entity.worldObj.setBlockToAir(x, y, z);
                            }
                        }
                    }
                    if (flag) y--;
                }
                entity.setDead();
                this.dead = true;
            }
        }

        @Override
        public boolean isDead() {
            return dead;
        }
    }
}
