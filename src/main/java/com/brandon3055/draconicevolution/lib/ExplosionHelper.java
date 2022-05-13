package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.ShortPos;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by brandon3055 on 16/03/2017.
 * Based on BlockPlacementBatcher by covers1624 which if i recall correctly was originally a collaborative effort between me and covers. (By that i mean i had the idea then covers did all the work)
 */
public class ExplosionHelper {

    private final ServerLevel serverWorld;
    private BlockPos start;
    private ShortPos shortPos;
    private HashSet<LevelChunk> modifiedChunks = new HashSet<>();
    private HashSet<Integer> blocksToUpdate = new HashSet<>();
    private HashSet<Integer> lightUpdates = new HashSet<>();
    private HashSet<Integer> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, LevelChunk> chunkCache = new HashMap<>();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Integer>> toRemove = new LinkedList<>();

    public ExplosionHelper(ServerLevel serverWorld, BlockPos start, ShortPos shortPos) {
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
        LevelChunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock() instanceof EntityBlock) {
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

        LevelChunkSection storage = getBlockStorage(pos);
        if (storage != null) {
            storage.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
        serverWorld.getLightEngine().checkBlock(pos);
    }

    public void setChunkModified(BlockPos blockPos) {
        LevelChunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(LevelChunk chunk) {
        modifiedChunks.add(chunk);
    }

    private LevelChunk getChunk(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        if (!chunkCache.containsKey(cp)) {
            chunkCache.put(cp, serverWorld.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
        }

        return chunkCache.get(cp);
    }

    private LevelChunkSection getBlockStorage(BlockPos pos) {
        LevelChunk chunk = getChunk(pos);
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
        LevelChunkSection storage = getBlockStorage(pos);
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
            for (LevelChunk chunk : helper.modifiedChunks) {
                chunk.setUnsaved(true);
                ThreadedLevelLightEngine lightManager = (ThreadedLevelLightEngine) helper.serverWorld.getLightEngine();
                lightManager.lightChunk(chunk, false);//todo???
//                        .thenRun(() -> helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
//                        .forEach(e -> e.connection.send(new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null, null, true))));

                ClientboundLightUpdatePacket packet = new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null, null, true);
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
            } catch (Throwable e) {
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
