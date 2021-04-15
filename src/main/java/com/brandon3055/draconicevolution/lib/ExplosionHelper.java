package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.brandonscore.lib.ShortPos;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by brandon3055 on 16/03/2017.
 * Based on BlockPlacementBatcher by covers1624 which if i recall correctly was originally a collaborative effort between me and covers. (By that i mean i had the idea then covers did all the work)
 */
public class ExplosionHelper {

    private final ServerWorld serverWorld;
    private BlockPos start;
    private ShortPos shortPos;
    private HashSet<Chunk> modifiedChunks = new HashSet<>();
    private HashSet<Integer> blocksToUpdate = new HashSet<>();
    private HashSet<Integer> lightUpdates = new HashSet<>();
    private HashSet<Integer> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, Chunk> chunkCache = new HashMap<>();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Integer>> toRemove = new LinkedList<>();

    public ExplosionHelper(ServerWorld serverWorld, BlockPos start, ShortPos shortPos) {
        this.serverWorld = serverWorld;
        this.start = start;
        this.shortPos = shortPos;
    }

    public void setBlocksForRemoval(LinkedList<HashSet<Integer>> list) {
        this.toRemove = list;
    }

    public void addBlocksForUpdate(Collection<Integer> blocksToUpdate) {
        this.blocksToUpdate.addAll(blocksToUpdate);
    }

    private void removeBlock(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock().hasTileEntity(oldState)) {
            serverWorld.removeBlock(pos, false);

//            PlayerChunkMap playerChunkMap = serverWorld.getPlayerChunkMap();
//            if (playerChunkMap != null) {
//                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
//                if (watcher != null) {
//                    watcher.sendPacket(new SPacketBlockChange(serverWorld, pos));
//                }
//            }

            serverWorld.getLightEngine().checkBlock(pos);
            return;
        }

        ChunkSection storage = getBlockStorage(pos);
        if (storage != null) {
            storage.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
        serverWorld.getLightEngine().checkBlock(pos);
    }

    public void setChunkModified(BlockPos blockPos) {
        Chunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(Chunk chunk) {
        modifiedChunks.add(chunk);
    }

    private Chunk getChunk(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        if (!chunkCache.containsKey(cp)) {
            chunkCache.put(cp, serverWorld.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
        }

        return chunkCache.get(cp);
    }

    private ChunkSection getBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        return chunk.getSections()[pos.getY() >> 4];
    }

    /**
     * Call when finished removing blocks to calculate lighting and send chunk updates to the client.
     */
    public void finish() {
        LogHelper.dev("EH: finish");
        RemovalProcess process = new RemovalProcess(this);
        ProcessHandler.addProcess(process);
    }

    public boolean isAirBlock(BlockPos pos) {
        return serverWorld.isEmptyBlock(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        ChunkSection storage = getBlockStorage(pos);
        if (storage == null) {
            return Blocks.AIR.defaultBlockState();
        }
        return storage.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static class RemovalProcess implements IProcess {

        public boolean isDead = false;
        private ExplosionHelper helper;
        int index = 0;
        private MinecraftServer server;

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
            this.server = helper.serverWorld.getServer();
        }

        @Override
        public void updateProcess() {
            server.nextTickTime = Util.getMillis();
            while (Util.getMillis() - server.nextTickTime < 50 && helper.toRemove.size() > 0) {
                LogHelper.dev("Processing chunks at rad: " + index);
                HashSet<Integer> set = helper.toRemove.removeFirst();
                for (int pos : set) {
                    helper.removeBlock(helper.shortPos.getActualPos(pos));
                }
                index++;
            }
            finishChunks();

            if (helper.toRemove.isEmpty()) {
                isDead = true;
                updateBlocks();
                DraconicNetwork.sendExplosionEffect(helper.serverWorld.dimension(), helper.start, 0, true);
            }
        }

        public void finishChunks() {
            for (Chunk chunk : helper.modifiedChunks) {
                chunk.setUnsaved(true);
                ServerWorldLightManager lightManager = (ServerWorldLightManager) helper.serverWorld.getLightEngine();
                lightManager.lightChunk(chunk, false)
                        .thenRun(() -> helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
                        .forEach(e -> e.connection.send(new SUpdateLightPacket(chunk.getPos(), helper.serverWorld.getLightEngine(), true))));

                SChunkDataPacket packet = new SChunkDataPacket(chunk, 65535);
                helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
            }

            helper.modifiedChunks.clear();
        }

        private void updateBlocks() {
            LogHelper.startTimer("Updating Blocks");

            try {
                LogHelper.dev("Updating " + helper.blocksToUpdate.size() + " Blocks");
                for (int pos : helper.blocksToUpdate) {
                    BlockState state = helper.serverWorld.getBlockState(helper.shortPos.getActualPos(pos));
                    if (state.getBlock() instanceof FallingBlock) {
                        state.getBlock().tick(state, helper.serverWorld, helper.shortPos.getActualPos(pos), helper.serverWorld.random);
                    }
                    state.neighborChanged(helper.serverWorld, helper.shortPos.getActualPos(pos), Blocks.AIR, helper.shortPos.getActualPos(pos).above(), false);
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            LogHelper.stopTimer();
        }

        @Override
        public boolean isDead() {
            return isDead;
        }
    }
}
