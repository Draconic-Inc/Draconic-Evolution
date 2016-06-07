package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.utils.LogHelper;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by brandon3055 on 27/3/2016.
 * Handles World Ticks!
 */
public class WorldTickHandler {
	public static TIntObjectHashMap<ArrayDeque<ChunkPos>> chunksToGen = new TIntObjectHashMap<ArrayDeque<ChunkPos>>();

	@SubscribeEvent
	public void tickEnd(TickEvent.WorldTickEvent event) {
		if (event.side != Side.SERVER) return;

		World world = event.world;
		int dimension = event.world.provider.getDimension();

		if (event.phase == TickEvent.Phase.END){
			ArrayDeque<ChunkPos> chunks = chunksToGen.get(dimension);

			if (chunks != null && chunks.size() > 0){
                ChunkPos chunkPos = chunks.pollFirst();

				LogHelper.info("Retroactively adding ore to {dim: "+dimension+", chunkPos: "+chunkPos.toString()+", chunksToGo: "+chunks.size()+"}");

				long worldSeed = world.getSeed();
				Random rand = new Random(worldSeed);
				long xSeed = rand.nextLong() >> 2 + 1L;
				long zSeed = rand.nextLong() >> 2 + 1L;
				rand.setSeed(xSeed * chunkPos.chunkXPos + zSeed * chunkPos.chunkZPos ^ worldSeed);
				DEWorldGenHandler.instance.addOreGen(rand, chunkPos.chunkXPos, chunkPos.chunkZPos, world);
				chunksToGen.put(dimension, chunks);
			}else if (chunks != null){
				chunksToGen.remove(dimension);
			}
		}
	}

	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event){
		if (chunksToGen.containsKey(event.getWorld().provider.getDimension())){
			chunksToGen.get(event.getWorld().provider.getDimension()).clear();
		}
	}
}
