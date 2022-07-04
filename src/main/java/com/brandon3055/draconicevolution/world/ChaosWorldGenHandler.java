package com.brandon3055.draconicevolution.world;

import com.brandon3055.brandonscore.lib.PairXZ;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.SimplexNoise;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by brandon3055 on 9/9/2015.
 */
public class ChaosWorldGenHandler {

    /**
     * Used to generate the chaos island chunk by chunk
     * <p>
     * //     * @param world        The world.
     * //     * @param chunkX       The X position of the chunk being generated.
     * //     * @param chunkZ       The Z position of the chunk being generated.
     * //     * @param islandCenter Where to generate the island. If left null the islands will generate in a 10000 by 10000 grid.
     */
    public static boolean generateChunk(Feature<NoFeatureConfig> feature, ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos featurePos) {
//    public static void generateChunk(World world, int chunkX, int chunkZ, PairXZ<Integer, Integer> islandCenter, Random random) {
        ChunkPos chunkPos = new ChunkPos(featurePos);
        PairXZ<Integer, Integer> closestSpawn = getClosestChaosSpawn(chunkPos);

        if (closestSpawn.x == 0 && closestSpawn.z == 0) {
            return false;
        }
        int posX = chunkPos.x * 16;
        int posZ = chunkPos.z * 16;
        int copyStartDistance = 180;
        if (Math.abs(posX - closestSpawn.x) > copyStartDistance || Math.abs(posZ - closestSpawn.z) > copyStartDistance) return false;

        boolean chunkModified = false;
        if (closestSpawn.x > posX && closestSpawn.x <= posX + 16 && closestSpawn.z > posZ && closestSpawn.z <= posZ + 16) {
            generateStructures(reader, closestSpawn, rand);
            chunkModified = true;
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
                        if (density > 0.1 && (reader.isEmptyBlock(pos) && reader.getBlockState(pos).getBlock() != Blocks.CAVE_AIR/*DEContent.chaosShardAtmos*/)) {
                            reader.setBlock(pos, (dist > 60 || dist > rand.nextInt(60)) ? Blocks.END_STONE.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState(), 3);
                            chunkModified = true;
                        }
                    }
                }
            }
        }
        return chunkModified;
    }

    public static void generateStructures(ISeedReader reader, PairXZ<Integer, Integer> islandCenter, Random random) {
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

            genCoreSlice(reader, islandCenter.x, y, islandCenter.z, inRadius, shardY, coreWidth, true, random);
            genCoreSlice(reader, islandCenter.x, y, islandCenter.z, outRadius, shardY, coreWidth, false, random);
        }

        BlockPos center = new BlockPos(islandCenter.x, shardY, islandCenter.z);

        reader.setBlock(center, DEContent.chaos_crystal.defaultBlockState(), 3);
        TileChaosCrystal tileChaosShard = (TileChaosCrystal) reader.getBlockEntity(center);
        if (tileChaosShard != null){
            tileChaosShard.onValidPlacement();
        }
        WorldEntityHandler.addWorldEntity(reader.getLevel(), new GuardianFightManager(center));

//        EntityChaosGuardian guardian = new EntityChaosGuardian(reader);
//        guardian.setPosition(islandCenter.x, shardY, islandCenter.z);
//        guardian.homeY = shardY;
//        reader.addEntity(guardian);

//        //Gen Ring
//        int rings = 4;
//        int width = 20;
//        int spacing = 8;
//        for (int x = islandCenter.x - outerRadius; x <= islandCenter.x + outerRadius; x++) {
//            for (int z = islandCenter.z - outerRadius; z <= islandCenter.z + outerRadius; z++) {
//                int dist = (int) (Utils.getDistanceAtoB(x, z, islandCenter.x, islandCenter.z));
//                for (int i = 0; i < rings; i++) {
//                    //if (dist < outerRadius1 && dist >= innerRadius1 || dist < outerRadius2 && dist >= innerRadius2)
//                    if (dist < (outerRadius - ((width + spacing) * i)) && dist >= (outerRadius - width - ((width + spacing) * i))) {
//                        int y = 90 + (int) ((double) (islandCenter.x - x) * 0.1D) + (random.nextInt(10) - 5);
//                        BlockPos pos = new BlockPos(x, y + DEOldConfig.chaosIslandYOffset, z);
//                        if (0.1F > random.nextFloat()) {
////                            reader.setBlockState(pos, Blocks.END_STONE.getDefaultState(), 3);
//                        }
////                        if (0.001F > random.nextFloat() && !DEOldConfig.disableOreSpawnEnd) {
////                            reader.setBlockState(pos, DEContent.ore_draconium_end.getDefaultState(), 3);
////                        }
//                    }
//                }
//            }
//        }
//        generateObelisks(reader, islandCenter, random);
    }

    public static void genCoreSlice(ISeedReader world, int xi, int yi, int zi, int ringRadius, int yc, int coreRadious, boolean fillIn, Random rand) {
        if (DEConfig.chaosIslandVoidMode) return;
        for (int x = xi - coreRadious; x <= xi + coreRadious; x++) {
            for (int z = zi - coreRadious; z <= zi + coreRadious; z++) {
                double dist = Utils.getDistanceAtoB(x, yi, z, xi, yc, zi);

                double oRad = coreRadious - (Math.abs(yc - yi) * Math.abs(yc - yi)) / 10;
                if (dist > oRad - 3D && rand.nextDouble() * 3D < dist - (oRad - 3D)) continue;

                if (fillIn && (int) (Utils.getDistanceAtoB(x, z, xi, zi)) <= ringRadius) {
                    if ((int) dist < 9) world.setBlock(new BlockPos(x, yi, z), DEContent.infused_obsidian.defaultBlockState(), 3);
                    else world.setBlock(new BlockPos(x, yi, z), Blocks.OBSIDIAN.defaultBlockState(), 3);
                } else if (!fillIn && (int) (Utils.getDistanceAtoB(x, z, xi, zi)) >= ringRadius) {
                    world.setBlock(new BlockPos(x, yi, z), Blocks.OBSIDIAN.defaultBlockState(), 3);
                } else if (!fillIn && (int) Utils.getDistanceAtoB(x, z, xi, zi) <= ringRadius) {
                    Block b = world.getBlockState(new BlockPos(x, yi, z)).getBlock();
                    if (b == Blocks.AIR || b == Blocks.END_STONE || b == Blocks.OBSIDIAN) world.setBlock(new BlockPos(x, yi, z), /*TODO DEContent.chaosShardAtmos*/Blocks.CAVE_AIR.defaultBlockState(), 3);
                }

            }
        }
    }


    public static PairXZ<Integer, Integer> getClosestChaosSpawn(ChunkPos pos) {
        return new PairXZ<>(MathUtils.getNearestMultiple(pos.x * 16, DEConfig.chaosIslandSeparation), MathUtils.getNearestMultiple(pos.z * 16, DEConfig.chaosIslandSeparation));
    }

    public static void generateObelisk(ServerWorld world, BlockPos genPos, Random rand) {
        for (int i = 0; i < 20; i+=3) {
            LightningBoltEntity entity = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
            entity.setPos(genPos.getX() - 2 + rand.nextInt(5), genPos.getY() - rand.nextInt(20), genPos.getZ() - 2 + rand.nextInt(5));
            world.addFreshEntity(entity);
        }


        if (DEConfig.chaosIslandVoidMode) return;

        int r = 3;
        BlockPos.betweenClosedStream(genPos.offset(-r, -25, -r), genPos.offset(r, 4, r)).forEach(pos -> {
            if (pos.getY() < genPos.getY()) {
                double pct = (double) (genPos.getY() - pos.getY()) / 25D;
                if (Utils.getDistanceAtoB(pos.getX(), pos.getZ(), genPos.getX(), genPos.getZ()) <= r + 0.5) {
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
