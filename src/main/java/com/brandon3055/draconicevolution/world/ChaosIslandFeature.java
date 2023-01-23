package com.brandon3055.draconicevolution.world;

import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.SimplexNoise;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEWorldGen;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

/**
 * Created by brandon3055 on 05/11/2022
 */
public class ChaosIslandFeature extends Feature<NoneFeatureConfiguration> {
    public final int islandYPos; // The y position of the chaos crystal
    public final int islandSeparation;
    public final int islandSize;

    public ChaosIslandFeature(Codec<NoneFeatureConfiguration> codec, int islandYPos, int islandSeparation, int islandSize) {
        super(codec);
        this.islandYPos = islandYPos;
        this.islandSeparation = islandSeparation;
        this.islandSize = islandSize;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        ChunkPos chunkPos = new ChunkPos(origin);
        ChunkPos closestSpawn = getClosestSpawn(chunkPos);

        if (closestSpawn.x == 0 && closestSpawn.z == 0) {
            return false;
        }

        WorldGenLevel level = context.level();
        Random rand = context.random();
        BlockPos islandOrigin = closestSpawn.getBlockAt(0, islandYPos, 0);

        if (islandOrigin.distSqr(origin) > (islandSize * 5) * (islandSize * 5)) {
            return false;
        }

        boolean chunkModified = false;
        if (!DEWorldGen.chaosIslandVoidMode) {
            chunkModified = genIslandChunk(level, origin, islandOrigin, rand);
            chunkModified |= genCoreChunk(level, origin, islandOrigin, rand);
            chunkModified |= genRingChunk(level, origin, islandOrigin, rand);
        }

        if (inChunk(origin, islandOrigin)) {
            level.setBlock(islandOrigin, DEContent.chaos_crystal.defaultBlockState(), 3);
            if (level.getBlockEntity(islandOrigin) instanceof TileChaosCrystal tile) {
                tile.onValidPlacement();
            }
            WorldEntityHandler.addWorldEntity(level.getLevel(), new GuardianFightManager(islandOrigin));
            chunkModified = true;
        }

        return chunkModified;
    }

    public boolean genIslandChunk(WorldGenLevel level, BlockPos chunkOrigin, BlockPos islandOrigin, Random rand) {
        boolean chunkModified = false;
        int minY = islandYPos - 40;
        int maxY = islandYPos + 40;

        for (BlockPos pos : BlockPos.betweenClosed(chunkOrigin.offset(0, minY - chunkOrigin.getY(), 0), chunkOrigin.offset(16, maxY - chunkOrigin.getY(), 16))) {
            int x = pos.getX() - islandOrigin.getX();
            int y = pos.getY() - islandOrigin.getY();
            int z = pos.getZ() - islandOrigin.getZ();

            double dist = Math.sqrt(pos.distSqr(islandOrigin));
            double xd, yd, zd;
            double density, centerFalloff, plateauFalloff, heightMapFalloff;

            //Scaling
            xd = (double) x / islandSize;
            yd = 0.5 + ((double) y / (32));
            zd = (double) z / islandSize;
            dist *= 80D / islandSize;

            //Calculate Center Falloff
            centerFalloff = 1D / (dist * 0.05D);
            if (centerFalloff < 0) centerFalloff = 0;

            //Calculate Plateau Falloff
            if (yd < 0.4D) {
                plateauFalloff = yd * 2.5D;
            } else if (yd <= 0.6D) {
                plateauFalloff = 1D;
            } else if (yd > 0.6D && yd < 1D) {
                plateauFalloff = 1D - (yd - 0.6D) * 2.5D;
            } else {
                plateauFalloff = 0;
            }

            //Trim Further calculations
            if (plateauFalloff == 0 || centerFalloff == 0) {
                continue;
            }

            //Calculate heightMapFalloff
            heightMapFalloff = 0;
            for (int octave = 1; octave < 5; octave++) {
                heightMapFalloff += ((SimplexNoise.noise(xd * octave + islandOrigin.getX(), zd * octave + islandOrigin.getZ()) + 1) * 0.5D) * 0.01D * (octave * 10D * 1 - (dist * 0.001D));
            }
            if (heightMapFalloff <= 0) {
                heightMapFalloff = 0;
            }
            heightMapFalloff += ((0.5D - Math.abs(yd - 0.5D)) * 0.15D);
            if (heightMapFalloff == 0) {
                continue;
            }

            density = centerFalloff * plateauFalloff * heightMapFalloff;

            if (density > 0.1 && (level.isEmptyBlock(pos) && level.getBlockState(pos).getBlock() != Blocks.CAVE_AIR/*DEContent.chaosShardAtmos*/)) {
                level.setBlock(pos, (dist > 60 || dist > rand.nextInt(60)) ? Blocks.END_STONE.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState(), 3);
                chunkModified = true;
            }
        }


        return chunkModified;
    }

    public boolean genCoreChunk(WorldGenLevel level, BlockPos chunkOrigin, BlockPos islandOrigin, Random rand) {
        boolean chunkModified = false;

        int coreHeight = 10;
        int coreWidth = 20;
        int originX = islandOrigin.getX();
        int originZ = islandOrigin.getZ();

        for (int y = islandYPos - coreHeight; y <= islandYPos + coreHeight; y++) {
            int yDist = Math.abs(y - islandYPos);
            int inRadius = yDist - 3;
            double yp = (coreHeight - yDist) / (double) coreHeight;
            int outRadius = (int) (yp * coreWidth);
            outRadius -= (outRadius * outRadius) / 100;

            BlockPos sliceMin = new BlockPos(originX - outRadius, y, originZ - outRadius);
            BlockPos sliceMax = new BlockPos(originX + outRadius, y, originZ + outRadius);
            BlockPos slicePos = new BlockPos(originX, y, originZ);

            for (BlockPos pos : BlockPos.betweenClosed(sliceMin, sliceMax)) {
                if (!inChunk(chunkOrigin, pos)) continue;

                double distSq = (pos.distSqr(islandOrigin));
                double distXZSq = (pos.distSqr(slicePos));

                genCoreSlice(level, distSq, distXZSq, pos, inRadius, coreWidth, true, rand);
                genCoreSlice(level, distSq, distXZSq, pos, outRadius, coreWidth, false, rand);
            }

        }

        return chunkModified;
    }

    public boolean genCoreSlice(WorldGenLevel level, double distSq, double distXZSq, BlockPos pos, int ringRadius, int coreRadious, boolean fillIn, Random rand) {
        int yOffset = Math.abs(islandYPos - pos.getY());
        double oRad = coreRadious - (yOffset * yOffset) / 10D;

        if (distSq > (oRad - 3D) * (oRad - 3D) && rand.nextDouble() * 3D < distSq - ((oRad - 3D) * (oRad - 3D))) {
            return false;
        }

        //Fills the inner core section
        if (fillIn && Math.sqrt(distXZSq) <= ringRadius) {
            if (Math.sqrt(distSq) < 9) {
                level.setBlock(pos, DEContent.infused_obsidian.defaultBlockState(), 3);
            } else {
                level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
            return true;

        }
        //Places the outer rings
        else if (!fillIn && distXZSq >= (ringRadius - 1) * (ringRadius - 1)) {
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            return true;
        } else if (!fillIn && distXZSq <= (ringRadius - 1) * (ringRadius - 1)) {
            Block b = level.getBlockState(pos).getBlock();
            if (b == Blocks.AIR || b == Blocks.END_STONE || b == Blocks.OBSIDIAN) {
                level.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 3);
                return true;
            }
        }

        return false;
    }

    public boolean genRingChunk(WorldGenLevel level, BlockPos chunkOrigin, BlockPos islandOrigin, Random rand) {
        boolean chunkModified = false;
        int outerRadius = islandSize * 4;
        int rings = 4;
        int width = 20;
        int spacing = 20;

        for (int x = chunkOrigin.getX(); x < chunkOrigin.getX() + 16; x++) {
            for (int z = chunkOrigin.getZ(); z < chunkOrigin.getZ() + 16; z++) {
                int distSq = (int) Utils.getDistanceSq(x, z, islandOrigin.getX(), islandOrigin.getZ());
                for (int i = 0; i < rings; i++) {
                    int max = outerRadius - ((width + spacing) * i);
                    int min = outerRadius - width - ((width + spacing) * i);
                    if (distSq < max * max && distSq >= min * min) {
                        int y = 10 + (int) ((double) (islandOrigin.getX() - x) * 0.1D) + (rand.nextInt(10) - 5);
                        BlockPos setPos = new BlockPos(x, y + islandYPos, z);
                        if (0.1F > rand.nextFloat()) {
                            level.setBlock(setPos, Blocks.END_STONE.defaultBlockState(), 3);
                            chunkModified = true;
                        }
                        if (0.001F > rand.nextFloat() && DEWorldGen.DRACONIUM_ORE_PLACED_END != null) {
                            level.setBlock(setPos, DEContent.ore_draconium_end.defaultBlockState(), 3);
                            chunkModified = true;
                        }
                    }
                }
            }
        }

        return chunkModified;
    }

    public ChunkPos getClosestSpawn(ChunkPos pos) {
        return new ChunkPos(MathUtils.getNearestMultiple(pos.x * 16, islandSeparation) / 16, MathUtils.getNearestMultiple(pos.z * 16, islandSeparation) / 16);
    }

    private static boolean inChunk(BlockPos ref, BlockPos test) {
        int x = ref.getX() - (ref.getX() % 16);
        int z = ref.getZ() - (ref.getZ() % 16);
        return test.getX() >= x && test.getZ() >= z && test.getX() <= x + 15 && test.getZ() <= z + 15;
    }

    public static void generateObelisk(ServerLevel world, BlockPos genPos, Random rand) {
        for (int i = 0; i < 20; i += 3) {
            LightningBolt entity = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
            entity.setPos(genPos.getX() - 2 + rand.nextInt(5), genPos.getY() - rand.nextInt(20), genPos.getZ() - 2 + rand.nextInt(5));
            world.addFreshEntity(entity);
        }

        if (DEWorldGen.chaosIslandVoidMode) return;

        int r = 3;
        BlockPos.betweenClosedStream(genPos.offset(-r, -25, -r), genPos.offset(r, 4, r)).forEach(pos -> {
            if (pos.getY() < genPos.getY()) {
                double pct = (double) (genPos.getY() - pos.getY()) / 25D;
                if (Utils.getDistance(pos.getX(), pos.getZ(), genPos.getX(), genPos.getZ()) <= r + 0.5) {
                    if (1D - pct > rand.nextDouble()) {
                        float block = rand.nextFloat();
                        if (block < 0.1) {
                            world.setBlock(pos, DEContent.infused_obsidian.defaultBlockState(), 3);
                        } else if (block < 0.4) {
                            world.setBlock(pos, Blocks.NETHER_BRICKS.defaultBlockState(), 3);
                        } else {
                            world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                    }
                }
            }
            int relY = pos.getY() - genPos.getY();
            int absRelX = Math.abs(pos.getX() - genPos.getX());
            int absRelZ = Math.abs(pos.getZ() - genPos.getZ());
            if ((absRelX == 2 || absRelZ == 2) && absRelX <= 2 && absRelZ <= 2 && relY < 4 && relY > -1) {
                world.setBlock(pos, Blocks.IRON_BARS.defaultBlockState(), 3);
            }
            if (relY == 4 && absRelX <= 2 && absRelZ <= 2) {
                world.setBlock(pos, Blocks.NETHER_BRICK_SLAB.defaultBlockState(), 3);
            }
        });
    }
}

