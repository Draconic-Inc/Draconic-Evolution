package com.brandon3055.draconicevolution.lib;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
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

import java.util.*;

/**
 * Created by brandon3055 on 16/03/2017.
 * Based on BlockPlacementBatcher by covers1624 which if i recall correctly was originally a collaborative effort between me and covers. (By that i mean i had the idea then covers did all the work)
 */
public class ExplosionHelper {

    private final ServerLevel serverWorld;
    private BlockPos start;
    private HashSet<LevelChunk> modifiedChunks = new HashSet<>();
    private HashSet<Long> blocksToUpdate = new HashSet<>();
    private HashSet<Long> lightUpdates = new HashSet<>();
    private HashSet<Long> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, LevelChunk> chunkCache = new HashMap<>();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    public BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Long>> toRemove = new LinkedList<>();

    public ExplosionHelper(ServerLevel serverWorld, BlockPos start) {
        this.serverWorld = serverWorld;
        this.start = start;
    }

    public void setBlocksForRemoval(LinkedList<HashSet<Long>> list) {
        this.toRemove = list;
    }

    public void addBlocksForUpdate(Collection<Long> blocksToUpdate) {
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
        return chunk.getSection(chunk.getSectionIndex(pos.getY()));
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
        public BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
            this.server = helper.serverWorld.getServer();
        }

        @Override
        public void updateProcess() {
            server.nextTickTimeNanos = Util.getNanos();
            while (Util.getNanos() - server.nextTickTimeNanos < 50000000 && helper.toRemove.size() > 0) {
                LogHelper.dev("Processing chunks at rad: " + index);
                HashSet<Long> set = helper.toRemove.removeFirst();
                for (long pos : set) {
                    helper.removeBlock(mPos.set(pos));
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
                chunk.setLightCorrect(false);
                ThreadedLevelLightEngine lightManager = (ThreadedLevelLightEngine) helper.serverWorld.getLightEngine();


                //                    level.getChunk(player.getOnPos()).setLightCorrect(false); //Works for chunk after reload
//                    level.getLightEngine().checkBlock(player.getOnPos()); //Updates block after reload
//                    level.getLightEngine().propagateLightSources(new ChunkPos(player.getOnPos())); //Works on chunk after reload

//                lightManager.runUpdates()
//                lightManager.lightChunk(chunk, true)
//                        .thenRun(() -> helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
//                                .forEach(e -> e.connection.send(new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null, null))));

//                ClientboundLightUpdatePacket packet = new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null, null, true);
//                helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));

//                ClientboundLightUpdatePacket packet = new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null, null, true);
                ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(chunk, helper.serverWorld.getLightEngine(), null, null);
                helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
//                DraconicNetwork.sendChunkRelight(chunk);
            }

            helper.modifiedChunks.clear();
        }

        private void updateBlocks() {
            LogHelper.startTimer("Updating Blocks");

            try {
                LogHelper.dev("Updating " + helper.blocksToUpdate.size() + " Blocks");
                int i = 0;
                List<Vector3> list = new ArrayList<>();
                for (long pos : helper.blocksToUpdate) {
                    BlockState state = helper.serverWorld.getBlockState(mPos.set(pos));
                    if (state.getBlock() instanceof FallingBlock) {
//                        i++;
                        state.getBlock().tick(state, helper.serverWorld, helper.mPos.set(pos), helper.serverWorld.random);
                    }
//                    list.add(Vector3.fromBlockPos(mPos.set(pos)));
                    state.neighborChanged(helper.serverWorld, mPos.set(pos), Blocks.AIR, mPos.set(pos).above(), false);
                }
//                BCClientEventHandler.debugBlockList = list;
                LogHelper.dev("Total Falling Blocks " + i);
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
