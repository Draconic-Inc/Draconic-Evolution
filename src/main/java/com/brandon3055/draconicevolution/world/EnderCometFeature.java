package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.DraconiumOre;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brandon3055 on 21/05/2024
 */
public class EnderCometFeature extends Feature<NoneFeatureConfiguration> {

//    private BlockPos spawn;
//    private BlockPos tail;
//    private int size;
//    private Set<Long> blocks = new HashSet<>();

    public EnderCometFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!DEConfig.enderCometEnabled) {
            return false;
        }

        setBlock(context.level(), context.origin(), DEContent.COMET_SPAWNER.get().defaultBlockState());

//        spawn = context.origin();
//        RandomSource rand = context.random();
//
//        double rotation = rand.nextInt();
//        double xmod = Math.sin(rotation);
//        double zmod = Math.cos(rotation);
//
//        int distMod = 150 + rand.nextInt(50);
//        tail = spawn.offset((int) (xmod * distMod), 40 + rand.nextInt(40), (int) (zmod * distMod));
//        size = 2 + rand.nextInt(8);
//
//        generateCore(context.level(), rand);
//        generateTrail(context.level(), rand);

        return true;
    }

    public static void buildComet(Level level, BlockPos spawn, RandomSource rand) {
        double rotation = rand.nextInt();
        double xmod = Math.sin(rotation);
        double zmod = Math.cos(rotation);

        int size = 4 + rand.nextInt(6);
        int distMod = 200;//150 + rand.nextInt(50);
        BlockPos tail = spawn.offset((int) (xmod * distMod), 40 + rand.nextInt(40), (int) (zmod * distMod));

        generateCore(level, rand, spawn, size);
        generateTrail(level, rand, spawn, tail, size);
    }

    private static void generateCore(Level level, RandomSource rand, BlockPos spawn, int size) {
        for (BlockPos pos : BlockPos.betweenClosed(spawn.offset(-size, -size, -size), spawn.offset(size, size, size))) {
            if (pos.distSqr(spawn) > size * size) continue;
            float genP = rand.nextFloat();
            if (0.1F > genP) {
                level.setBlockAndUpdate(pos, DEContent.END_DRACONIUM_ORE.get().defaultBlockState());
            } else if (0.4F > genP) {
                level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
            }
        }
    }

    private static void generateTrail(Level level, RandomSource rand, BlockPos spawn, BlockPos tail, int size) {
        int xDiff = tail.getX() - spawn.getX();
        int yDiff = tail.getY() - spawn.getY();
        int zDiff = tail.getZ() - spawn.getZ();

        for (int p = 0; p < 100; p += 2) {
            int cX = spawn.getX() + (int) (((float) p / 100F) * xDiff);
            int cY = spawn.getY() + (int) (((float) p / 100F) * yDiff);
            int cZ = spawn.getZ() + (int) (((float) p / 100F) * zDiff);
            float pc = (float) p / 100F;

            int density = 500 - (int) (pc * 550);
            if (density < 20) density = 20;
            generateTrailSphere(level, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);

            density = 1000 - (int) (pc * 10000);
            generateTrailSphere(level, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);
        }
    }

    public static void generateTrailSphere(Level level, int xi, int yi, int zi, int r, int density, RandomSource rand) {
        if (density <= 0) return;
        if (density > 10000) density = 10000;
        for (int x = xi - r; x <= xi + r; x++) {
            for (int z = zi - r; z <= zi + r; z++) {
                for (int y = yi - r; y <= yi + r; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if ((density >= rand.nextInt(10000)) && level.isEmptyBlock(pos) && (int) (getDistance(x, y, z, xi, yi, zi)) == r) {
                        if (0.9F >= rand.nextFloat()) {
                            level.setBlockAndUpdate(pos, Blocks.END_STONE.defaultBlockState());
                        }
                        else if (rand.nextBoolean()) {
                            level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
                        }
                        else {
                            level.setBlockAndUpdate(pos, DEContent.END_DRACONIUM_ORE.get().defaultBlockState());
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

//    private void generateCore(LevelWriter writer, RandomSource rand) {
//        for (BlockPos pos : BlockPos.betweenClosed(spawn.offset(-size, -size, -size), spawn.offset(size, size, size))) {
//            if (pos.distSqr(spawn) > size * size) continue;
//            float genP = rand.nextFloat();
//            if (0.1F > genP) {
//                setBlock(writer, pos, DEContent.END_DRACONIUM_ORE.get().defaultBlockState());
//            } else if (0.4F > genP) {
//                setBlock(writer, pos, Blocks.OBSIDIAN.defaultBlockState());
//            } else {
//                setBlock(writer, pos, Blocks.OBSIDIAN.defaultBlockState());
//            }
//        }
//    }
//
//    private void generateTrail(LevelWriter writer, RandomSource rand) {
//        int xDiff = tail.getX() - spawn.getX();
//        int yDiff = tail.getY() - spawn.getY();
//        int zDiff = tail.getZ() - spawn.getZ();
//
//        for (int p = 0; p < 100; p += 2) {
//            int cX = spawn.getX() + (int) (((float) p / 100F) * xDiff);
//            int cY = spawn.getY() + (int) (((float) p / 100F) * yDiff);
//            int cZ = spawn.getZ() + (int) (((float) p / 100F) * zDiff);
//            float pc = (float) p / 100F;
//
//            int density = 500 - (int) (pc * 550);
//            if (density < 20) density = 20;
//            generateTrailSphere(writer, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);
//
//            density = 1000 - (int) (pc * 10000);
//            generateTrailSphere(writer, cX, cY, cZ, (size + 3) - (int) (pc * (size - 2)), density, rand);
//        }
//    }
//
//    public void generateTrailSphere(LevelWriter writer, int xi, int yi, int zi, int r, int density, RandomSource rand) {
//        if (density <= 0) return;
//        if (density > 10000) density = 10000;
//        for (int x = xi - r; x <= xi + r; x++) {
//            for (int z = zi - r; z <= zi + r; z++) {
//                for (int y = yi - r; y <= yi + r; y++) {
//                    BlockPos pos = new BlockPos(x, y, z);
//                    if ((density >= rand.nextInt(10000)) && !blocks.contains(pos.asLong()) && (int) (getDistance(x, y, z, xi, yi, zi)) == r) {
//                        if (0.9F >= rand.nextFloat()) {
//                            setBlock(writer, pos, Blocks.END_STONE.defaultBlockState());
//                        }
//                        else if (rand.nextBoolean()) {
//                            setBlock(writer, pos, Blocks.OBSIDIAN.defaultBlockState());
//                        }
//                        else {
//                            setBlock(writer, pos, DEContent.END_DRACONIUM_ORE.get().defaultBlockState());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static double getDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
//        int dx = x1 - x2;
//        int dy = y1 - y2;
//        int dz = z1 - z2;
//        return Math.sqrt((dx * dx + dy * dy + dz * dz));
//    }
//
//    @Override
//    protected void setBlock(LevelWriter pLevel, BlockPos pPos, BlockState pState) {
//        super.setBlock(pLevel, pPos, pState);
//        blocks.add(pPos.asLong());
//    }
}

