package com.brandon3055.draconicevolution.world;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Brandon on 28/08/2014.
 */
public class WorldGenEnderComet /*extends WorldGenerator*/ {

    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int tailX;
    private int tailY;
    private int tailZ;
    private int size;

    public WorldGenEnderComet() {
//        super(false);
    }

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

//    @Override
    public boolean generate(World world, Random random, BlockPos pos) {
        initialize(random, pos.getX(), pos.getY(), pos.getZ());
//        Cuboid6 bb = new Cuboid6(Math.min(spawnX, tailX), Math.min(spawnY, tailY), Math.min(spawnZ, tailZ), Math.max(spawnX, tailX), Math.max(spawnY, tailY), Math.max(spawnZ, tailZ));
//        bb.expand(size + 5);
//        for (int x = (int) bb.min.x; x <= bb.max.x; x++) {
//            for (int y = (int) bb.min.y; y <= bb.max.y; y++) {
//                for (int z = (int) bb.min.z; z <= bb.max.z; z++) {
//                    int sm = ((x == bb.min.x || x == bb.max.x) ? 1 : 0) + ((y == bb.min.y || y == bb.max.y) ? 1 : 0) + ((z == bb.min.z || z == bb.max.z) ? 1 : 0);
//                    if (sm > 1) {
//                        setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), Blocks.GLOWSTONE.getDefaultState());
////                        world.setBlockState();
//                    }
//                }
//            }
//        }

        setCascadingWarningEnabled(false);

        generateCore(world, random, size);
        generateTrail(world, random);

        setCascadingWarningEnabled(true);
        return true;
    }

    private void generateCore(World world, Random rand, int r) {
//        for (int x = spawnX - r; x <= spawnX + r; x++) {
//            for (int z = spawnZ - r; z <= spawnZ + r; z++) {
//                for (int y = spawnY - r; y <= spawnY + r; y++) {
//                    if ((int) (getDistance(x, y, z, spawnX, spawnY, spawnZ)) <= r) {
//                        float genP = rand.nextFloat();
//                        BlockPos pos = new BlockPos(x, y, z);
//                        if (0.1F > genP) {
//                            setBlockAndNotifyAdequately(world, pos, DraconiumOre.getEnd());
//                        }
//                        else if (0.4F > genP) {
//                            setBlockAndNotifyAdequately(world, pos, Blocks.OBSIDIAN.getDefaultState());
//                        }
//                        else {
//                            setBlockAndNotifyAdequately(world, pos, Blocks.OBSIDIAN.getDefaultState());
//                        }
//                    }
//                }
//            }
//        }
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

    public void generateTrailSphere(World world, int xi, int yi, int zi, int r, int density, Random rand) {
//        if (density <= 0) return;
//        if (density > 10000) density = 10000;
//        for (int x = xi - r; x <= xi + r; x++) {
//            for (int z = zi - r; z <= zi + r; z++) {
//                for (int y = yi - r; y <= yi + r; y++) {
//                    BlockPos pos = new BlockPos(x, y, z);
//                    if ((density >= rand.nextInt(10000)) && world.isAirBlock(pos) && (int) (getDistance(x, y, z, xi, yi, zi)) == r) {
//                        if (0.9F >= rand.nextFloat()) {
//                            setBlockAndNotifyAdequately(world, pos, Blocks.END_STONE.getDefaultState());
//                        }
//                        else if (rand.nextBoolean()) {
//                            setBlockAndNotifyAdequately(world, pos, Blocks.OBSIDIAN.getDefaultState());
//                        }
//                        else {
//                            setBlockAndNotifyAdequately(world, pos, DraconiumOre.getEnd());
//                        }
//                    }
//                }
//            }
//        }
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

