package com.brandon3055.draconicevolution.world;

/**
 * Created by brandon3055 on 27/3/2016.
 * Handles World Ticks!
 */
public class WorldTickHandler {
//    public static Object2ObjectArrayMap<DimensionType, ArrayDeque<ChunkPos>> chunksToGen = new Object2ObjectArrayMap<>();

    private int tick = 0;

//    @SubscribeEvent
//    public void tickEnd(TickEvent.WorldTickEvent event) {
//        if (event.side != LogicalSide.SERVER) {
//            return;
//        }
//
//        World world = event.world;
//        DimensionType dimension = event.world.getDimension().getType();
//
//        if (event.phase == TickEvent.Phase.END) {
//            ArrayDeque<ChunkPos> chunks = chunksToGen.get(dimension);
//
//            if (chunks != null && chunks.size() > 0) {
//                ChunkPos chunkPos = chunks.pollFirst();
//
//                if (tick++ % 20 == 0) {
//                    LogHelper.dev("Retroactively adding ore to {dim: " + dimension + ", chunkPos: " + chunkPos.toString() + ", chunksToGo: " + chunks.size() + "}");
//                }
//
//                long worldSeed = world.getSeed();
//                Random rand = new Random(worldSeed);
//                long xSeed = rand.nextLong() >> 2 + 1L;
//                long zSeed = rand.nextLong() >> 2 + 1L;
//                rand.setSeed(xSeed * chunkPos.x + zSeed * chunkPos.z ^ worldSeed);
////                DEWorldGenHandler.instance.addOreGen(rand, chunkPos.x, chunkPos.z, world);
//                DEWorldGenHandler.instance.retroGenComplete(dimension, chunkPos.x, chunkPos.z);
//                chunksToGen.put(dimension, chunks);
//            }
//            else if (chunks != null) {
//                chunksToGen.remove(dimension);
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public void worldUnload(WorldEvent.Unload event) {
//        if (chunksToGen.containsKey(event.getWorld().getDimension().getType())) {
//            chunksToGen.get(event.getWorld().getDimension().getType()).clear();
//        }
//    }
}
