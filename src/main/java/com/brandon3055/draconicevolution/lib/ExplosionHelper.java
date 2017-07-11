package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.ShortPos;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.network.PacketExplosionFX;
import com.brandon3055.draconicevolution.utils.LogHelper;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by brandon3055 on 16/03/2017.
 * Based on BlockPlacementBatcher by covers1624 which if i recall correctly was originally a collaborative effort between me and covers. (By that i mean i had the idea then covers did all the work)
 */
public class ExplosionHelper {

    private final WorldServer serverWorld;
    private BlockPos start;
    private ShortPos shortPos;
    private THashSet<Chunk> modifiedChunks = new THashSet<>();
    private HashSet<Integer> blocksToUpdate = new HashSet<>();
    private HashSet<Integer> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, Chunk> chunkCache = new HashMap<>();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Integer>> toRemove = new LinkedList<>();

    public ExplosionHelper(WorldServer serverWorld, BlockPos start, ShortPos shortPos) {
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
        IBlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock().hasTileEntity(oldState)) {
            serverWorld.setBlockToAir(pos);

            PlayerChunkMap playerChunkMap = serverWorld.getPlayerChunkMap();
            if (playerChunkMap != null) {
                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
                if (watcher != null) {
                    watcher.sendPacket(new SPacketBlockChange(serverWorld, pos));
                }
            }

            return;
        }

        ExtendedBlockStorage storage = getBlockStorage(pos);
        if (storage != null) {
            storage.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
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
            chunkCache.put(cp, serverWorld.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4));
        }

        return chunkCache.get(cp);
    }

    private boolean hasBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        return chunk.storageArrays[pos.getY() >> 4] != null;
    }

    private ExtendedBlockStorage getBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        return chunk.storageArrays[pos.getY() >> 4];
    }

    private void setRecalcPrecipitationHeightMap(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        int i = (pos.getZ() & 15) << 4 | (pos.getX() & 15);
        if (pos.getY() >= chunk.precipitationHeightMap[i] - 1) {
            chunk.precipitationHeightMap[i] = -999;
        }
    }

    private void fireBlockBreak(BlockPos pos, IBlockState oldState) {
        oldState.getBlock().breakBlock(serverWorld, pos, oldState);
    }

    private void removeTileEntity(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        TileEntity tileEntity = chunk.getTileEntity(pos, EnumCreateEntityType.CHECK);
        if (tileEntity != null) {
            serverWorld.removeTileEntity(pos);
        }
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
        return serverWorld.isAirBlock(pos);
    }

    public IBlockState getBlockState(BlockPos pos) {
        ExtendedBlockStorage storage = getBlockStorage(pos);
        if (storage == null) {
            return Blocks.AIR.getDefaultState();
        }
        return storage.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static class RemovalProcess implements IProcess {

        public boolean isDead = false;
        private ExplosionHelper helper;
        int index = 0;
        private MinecraftServer server;

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
            this.server = helper.serverWorld.getMinecraftServer();
        }

        @Override
        public void updateProcess() {
            server.currentTime = MinecraftServer.getCurrentTimeMillis();
            while (MinecraftServer.getCurrentTimeMillis() - server.currentTime < 50 && helper.toRemove.size() > 0) {
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
            }


//            LogHelper.dev("Processing chunks ar rad: " + index);
//            if (helper.radialRemovalMap.containsKey(index)) {
//                List<Integer> list = helper.radialRemovalMap.get(index);
//                for (int pos : list) {
//                    helper.removeBlock(helper.shortPos.getActualPos(pos));
//                }
//                helper.radialRemovalMap.remove(index);
//                finishChunks();
//            }
//
//            if (helper.radialRemovalMap.isEmpty()) {
//                isDead = true;
//                updateBlocks();
//            }
//
//            index++;
        }

        public void finishChunks() {
            PlayerChunkMap playerChunkMap = helper.serverWorld.getPlayerChunkMap();
            if (playerChunkMap == null) {
                return;
            }

            for (Chunk chunk : helper.modifiedChunks) {
                chunk.setModified(true);
                chunk.generateSkylightMap(); //This is where this falls short. It can calculate basic sky lighting for blocks exposed to the sky but thats it.

                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(chunk.x, chunk.z);
                if (watcher != null) {//TODO Change chunk mask to only the sub chunks changed.
                    watcher.sendPacket(new SPacketChunkData(chunk, 65535));
                }
            }

            helper.modifiedChunks.clear();
        }

        private void updateBlocks() {
            LogHelper.startTimer("Updating Blocks");

            try {
                LogHelper.dev("Updating " + helper.blocksToUpdate.size() + " Blocks");
                BlockFalling.fallInstantly = true;
                for (int pos : helper.blocksToUpdate) {
                    IBlockState state = helper.serverWorld.getBlockState(helper.shortPos.getActualPos(pos));
                    if (state.getBlock() instanceof BlockFalling) {
                        state.getBlock().updateTick(helper.serverWorld, helper.shortPos.getActualPos(pos), state, helper.serverWorld.rand);
                    }
                    state.neighborChanged(helper.serverWorld, helper.shortPos.getActualPos(pos), Blocks.AIR, helper.shortPos.getActualPos(pos).up());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            LogHelper.stopTimer();

            BlockFalling.fallInstantly = false;

            PacketExplosionFX packet = new PacketExplosionFX(helper.start, 0, true);
            DraconicEvolution.network.sendToAllAround(packet, new NetworkRegistry.TargetPoint(helper.serverWorld.provider.getDimension(), helper.start.getX(), helper.start.getY(), helper.start.getZ(), 500));
        }

        @Override
        public boolean isDead() {
            return isDead;
        }
    }
}
