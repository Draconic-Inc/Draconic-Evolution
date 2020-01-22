package com.brandon3055.draconicevolution.world;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.lib.PairXZ;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.brandonscore.utils.SimplexNoise;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.DraconiumOre;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
import com.brandon3055.draconicevolution.entity.EntityGuardianCrystal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

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
     * @param islandCenter Where to generate the island. If left null the islands will generate in a 10000 by 10000 grid.
     */
    public static void generateChunk(World world, int chunkX, int chunkZ, PairXZ<Integer, Integer> islandCenter, Random random) {
        PairXZ<Integer, Integer> closestSpawn = islandCenter == null ? getClosestChaosSpawn(chunkX, chunkZ) : islandCenter;

        if (closestSpawn.x == 0 && closestSpawn.z == 0) {
            return;
        }
        int posX = chunkX * 16;
        int posZ = chunkZ * 16;
        int copyStartDistance = 180;
        if (Math.abs(posX - closestSpawn.x) > copyStartDistance || Math.abs(posZ - closestSpawn.z) > copyStartDistance) return;

        if (closestSpawn.x > posX && closestSpawn.x <= posX + 16 && closestSpawn.z > posZ && closestSpawn.z <= posZ + 16) {
            generateStructures(world, closestSpawn, random);
        }

        //long l = System.nanoTime();

        if (!DEConfig.chaosIslandVoidMode) {
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

                        //Calculate Center Falloff
                        centerFalloff = 1D / (dist * 0.05D);
                        if (centerFalloff < 0) centerFalloff = 0;

                        //Calculate Plateau Falloff
                        if (yd < 0.4D) {
                            plateauFalloff = yd * 2.5D;
                        }
                        else if (yd <= 0.6D) {
                            plateauFalloff = 1D;
                        }
                        else if (yd > 0.6D && yd < 1D) {
                            plateauFalloff = 1D - (yd - 0.6D) * 2.5D;
                        }
                        else {
                            plateauFalloff = 0;
                        }

                        //Trim Further calculations
                        if (plateauFalloff == 0 || centerFalloff == 0) {
                            continue;
                        }

                        //Calculate heightMapFalloff
                        heightMapFalloff = 0;
                        for (int octave = 1; octave < 5; octave++){
                            heightMapFalloff += ((SimplexNoise.noise(xd * octave + closestSpawn.x, zd * octave + closestSpawn.z) + 1) * 0.5D) * 0.01D * (octave * 10D * 1 - (dist * 0.001D));
                        }
                        if (heightMapFalloff <= 0) {
                            heightMapFalloff = 0;
                        }
                        heightMapFalloff += ((0.5D - Math.abs(yd - 0.5D)) * 0.15D);
                        if (heightMapFalloff == 0) {
                            continue;
                        }

                        density = centerFalloff * plateauFalloff * heightMapFalloff;

                        BlockPos pos = new BlockPos(x + closestSpawn.x, y + 64 + DEConfig.chaosIslandYOffset, z + closestSpawn.z);
                        if (density > 0.1 && (world.isAirBlock(pos) && world.getBlockState(pos).getBlock() != DEFeatures.chaosShardAtmos)) {
                            world.setBlockState(pos, (dist > 60 || dist > random.nextInt(60)) ? Blocks.END_STONE.getDefaultState() : Blocks.OBSIDIAN.getDefaultState());
                        }

                    }
                }
            }
        }
    }

    public static void generateStructures(World world, PairXZ<Integer, Integer> islandCenter, Random random) {
        int outerRadius = 330;

        //Gen Chaos Cavern
        int shardY = 80 + DEConfig.chaosIslandYOffset;

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

        BlockPos center = new BlockPos(islandCenter.x, shardY, islandCenter.z);

        if (ModFeatureParser.isEnabled(DEFeatures.chaosCrystal)) {
            world.setBlockState(center, DEFeatures.chaosCrystal.getDefaultState());
            TileChaosCrystal tileChaosShard = (TileChaosCrystal) world.getTileEntity(center);
            tileChaosShard.setLockPos();
        }

        EntityChaosGuardian guardian = new EntityChaosGuardian(world);
        guardian.setPosition(islandCenter.x, shardY, islandCenter.z);
        guardian.homeY = shardY;
        world.spawnEntity(guardian);

        //Gen Ring
        int rings = 4;
        int width = 20;
        int spacing = 8;
        for (int x = islandCenter.x - outerRadius; x <= islandCenter.x + outerRadius; x++) {
            for (int z = islandCenter.z - outerRadius; z <= islandCenter.z + outerRadius; z++) {
                int dist = (int) (Utils.getDistanceAtoB(x, z, islandCenter.x, islandCenter.z));
                for (int i = 0; i < rings; i++) {
                    //if (dist < outerRadius1 && dist >= innerRadius1 || dist < outerRadius2 && dist >= innerRadius2)
                    if (dist < (outerRadius - ((width + spacing) * i)) && dist >= (outerRadius - width - ((width + spacing) * i))) {
                        int y = 90 + (int) ((double) (islandCenter.x - x) * 0.1D) + (random.nextInt(10) - 5);
                        BlockPos pos = new BlockPos(x, y + DEConfig.chaosIslandYOffset, z);
                        if (0.1F > random.nextFloat()) {
                            world.setBlockState(pos, Blocks.END_STONE.getDefaultState());
                        }
                        if (0.001F > random.nextFloat() && !DEConfig.disableOreSpawnEnd) {
                            world.setBlockState(pos, DraconiumOre.getEnd());
                        }
                    }
                }
            }
        }
        generateObelisks(world, islandCenter, random);
    }

    public static void genCoreSlice(World world, int xi, int yi, int zi, int ringRadius, int yc, int coreRadious, boolean fillIn, Random rand) {
        if (DEConfig.chaosIslandVoidMode) return;
        for (int x = xi - coreRadious; x <= xi + coreRadious; x++) {
            for (int z = zi - coreRadious; z <= zi + coreRadious; z++) {
                double dist = Utils.getDistanceAtoB(x, yi, z, xi, yc, zi);

                double oRad = coreRadious - (Math.abs(yc - yi) * Math.abs(yc - yi)) / 10;
                if (dist > oRad - 3D && rand.nextDouble() * 3D < dist - (oRad - 3D)) continue;

                if (fillIn && (int) (Utils.getDistanceAtoB(x, z, xi, zi)) <= ringRadius) {
                    if ((int) dist < 9) world.setBlockState(new BlockPos(x, yi, z), DEFeatures.infusedObsidian.getDefaultState());
                    else world.setBlockState(new BlockPos(x, yi, z), Blocks.OBSIDIAN.getDefaultState());
                }
                else if (!fillIn && (int) (Utils.getDistanceAtoB(x, z, xi, zi)) >= ringRadius) {
                    world.setBlockState(new BlockPos(x, yi, z), Blocks.OBSIDIAN.getDefaultState());
                }
                else if (!fillIn && (int) Utils.getDistanceAtoB(x, z, xi, zi) <= ringRadius) {
                    Block b = world.getBlockState(new BlockPos(x, yi, z)).getBlock();
                    if (b == Blocks.AIR || b == Blocks.END_STONE || b == Blocks.OBSIDIAN) world.setBlockState(new BlockPos(x, yi, z), DEFeatures.chaosShardAtmos.getDefaultState());
                }

            }
        }
    }


    public static PairXZ<Integer, Integer> getClosestChaosSpawn(int chunkX, int chunkZ) {
        return new PairXZ<>(MathUtils.getNearestMultiple(chunkX * 16, DEConfig.chaosIslandSeparation), MathUtils.getNearestMultiple(chunkZ * 16, DEConfig.chaosIslandSeparation));
    }

    private static void generateObelisks(World world, PairXZ<Integer, Integer> islandCenter, Random rand) {

        for (int i = 0; i < 7; i++) {
            double rotation = i * 0.9D;
            int sX = islandCenter.x + (int) (Math.sin(rotation) * 45D);
            int sZ = islandCenter.z + (int) (Math.cos(rotation) * 45D);
            generateObelisk(world, sX, 90 + DEConfig.chaosIslandYOffset, sZ, false, rand);
        }

        for (int i = 0; i < 14; i++) {
            double rotation = i * 0.45D;
            int sX = islandCenter.x + (int) (Math.sin(rotation) * 90D);
            int sZ = islandCenter.z + (int) (Math.cos(rotation) * 90D);
            generateObelisk(world, sX, 90 + DEConfig.chaosIslandYOffset, sZ, true, rand);
        }

    }

    private static void generateObelisk(World world, int x1, int y1, int z1, boolean outer, Random rand) {
        if (!outer) {
            world.setBlockState(new BlockPos(x1, y1 + 20, z1), DEFeatures.infusedObsidian.getDefaultState());
            if (!world.isRemote) {
                EntityGuardianCrystal crystal = new EntityGuardianCrystal(world);
                crystal.setPosition(x1 + 0.5, y1 + 21, z1 + 0.5);
                world.spawnEntity(crystal);
            }
            if (DEConfig.chaosIslandVoidMode) return;
            for (int y = y1; y < y1 + 20; y++) {
                world.setBlockState(new BlockPos(x1, y, z1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 + 1, y, z1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 - 1, y, z1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1, y, z1 + 1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1, y, z1 - 1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 + 1, y, z1 + 1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 - 1, y, z1 - 1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 + 1, y, z1 - 1), Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(new BlockPos(x1 - 1, y, z1 + 1), Blocks.OBSIDIAN.getDefaultState());
            }
        }
        else {
            world.setBlockState(new BlockPos(x1, y1 + 40, z1), DEFeatures.infusedObsidian.getDefaultState());
            if (!world.isRemote) {
                EntityGuardianCrystal crystal = new EntityGuardianCrystal(world);
                crystal.setPosition(x1 + 0.5, y1 + 41, z1 + 0.5);
                world.spawnEntity(crystal);
            }
            if (DEConfig.chaosIslandVoidMode) return;
            int diff = 0;
            for (int y = y1 + 20; y < y1 + 40; y++) {
                diff++;
                double pct = (double) diff / 25D;
                int r = 3;
                for (int x = x1 - r; x <= x1 + r; x++) {
                    for (int z = z1 - r; z <= z1 + r; z++) {
                        if (Utils.getDistanceAtoB(x, z, x1, z1) <= r) {
                            if (pct > rand.nextDouble()) world.setBlockState(new BlockPos(x, y, z), Blocks.OBSIDIAN.getDefaultState());
                        }
                    }
                }
            }

            int cageS = 2;
            for (int x = x1 - cageS; x <= x1 + cageS; x++) {
                for (int y = y1 - cageS; y <= y1 + cageS; y++) {
                    if (0.8F > rand.nextFloat()) world.setBlockState(new BlockPos(x, y + 41, z1 + cageS), Blocks.IRON_BARS.getDefaultState());
                    if (0.8F > rand.nextFloat()) world.setBlockState(new BlockPos(x, y + 41, z1 - cageS), Blocks.IRON_BARS.getDefaultState());
                }
            }
            for (int z = z1 - cageS; z <= z1 + cageS; z++) {
                for (int y = y1 - cageS; y <= y1 + cageS; y++) {
                    if (0.8F > rand.nextFloat()) world.setBlockState(new BlockPos(x1 + cageS, y + 41, z), Blocks.IRON_BARS.getDefaultState());
                    if (0.8F > rand.nextFloat()) world.setBlockState(new BlockPos(x1 - cageS, y + 41, z), Blocks.IRON_BARS.getDefaultState());
                }
            }
            for (int z = z1 - cageS; z <= z1 + cageS; z++) {
                for (int x = x1 - cageS; x <= x1 + cageS; x++) {
                    if (0.8F > rand.nextFloat()) world.setBlockState(new BlockPos(x, y1 + 44, z), Blocks.STONE_SLAB.getDefaultState());
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
                for (; flag; ) {
                    flag = false;
                    for (int x = (int) Math.floor(entity.posX) - 4; x <= (int) Math.floor(entity.posX) + 4; x++) {
                        for (int z = (int) Math.floor(entity.posZ) - 4; z <= (int) Math.floor(entity.posZ) + 4; z++) {
                            Block block = entity.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                            if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
                                flag = true;
                                entity.world.setBlockToAir(new BlockPos(x, y, z));
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
