package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.utils.LinkedHashList;
import com.brandon3055.brandonscore.utils.LogHelperBC;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.network.PacketExplosionFX;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
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

import java.util.*;

/**
 * Created by brandon3055 on 16/03/2017.
 * Based on BlockPlacementBatcher by covers1624 which if i recall correctly was originally a collaborative effort between me and covers. (By that i mean i had the idea then covers did all the work)
 */
public class ExplosionHelper {

    private final WorldServer serverWorld;
    private BlockPos start;
    private THashSet<Chunk> modifiedChunks = new THashSet<>();
    private HashSet<BlockPos> blocksToUpdate = new HashSet<>();
    private HashSet<BlockPos> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, Chunk> chunkCache = new HashMap<>();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private Map<Integer, LinkedHashList<BlockPos>> radialRemovalMap = new HashMap<>();

    public ExplosionHelper(WorldServer serverWorld, BlockPos start) {
        this.serverWorld = serverWorld;
        this.start = start;
    }

    public void addBlock(BlockPos pos) {
        if (!hasBlockStorage(pos) || isAirBlock(pos)) {
            return;
        }

        int xd = Math.abs(pos.getX() - start.getX()) / 16;
        int zd = Math.abs(pos.getZ() - start.getZ()) / 16;
        int d = Math.max(xd, zd);

        if (!radialRemovalMap.containsKey(d)) {
            radialRemovalMap.put(d, new LinkedHashList<BlockPos>());
        }

        LinkedHashList<BlockPos> list = radialRemovalMap.get(d);
        list.add(pos);
    }

    public void addBlocksForRemoval(Collection<BlockPos> blocksToRemove) {
        for (BlockPos pos : blocksToRemove) {
            addBlock(pos);
        }
    }

    public void addBlocksForUpdate(Collection<BlockPos> blocksToUpdate) {
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

//            return;
        }

        //?
//        setRecalcPrecipitationHeightMap(pos);

        ExtendedBlockStorage storage = getBlockStorage(pos);
        storage.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);

        fireBlockBreak(pos, oldState);
//        removeTileEntity(pos);

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
        LogHelperBC.dev("EH: finish");
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

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
        }

        @Override
        public void updateProcess() {
            LogHelperBC.info("Processing chunks ar rad: " + index);
            if (helper.radialRemovalMap.containsKey(index)) {
                List<BlockPos> list = helper.radialRemovalMap.get(index);
                for (BlockPos pos : list) {
                    helper.removeBlock(pos);
                }
                helper.radialRemovalMap.remove(index);
                finishChunks();
            }

            if (helper.radialRemovalMap.isEmpty()) {
                isDead = true;
                updateBlocks();
            }

            index++;
        }

        public void finishChunks() {
            PlayerChunkMap playerChunkMap = helper.serverWorld.getPlayerChunkMap();
            if (playerChunkMap == null) {
                return;
            }

            for (Chunk chunk : helper.modifiedChunks) {
                chunk.setModified(true);
                chunk.generateSkylightMap(); //This is where this falls short. It can calculate basic sky lighting for blocks exposed to the sky but thats it.

                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(chunk.xPosition, chunk.zPosition);
                if (watcher != null) {//TODO Change chunk mask to only the sub chunks changed.
                    watcher.sendPacket(new SPacketChunkData(chunk, 65535));
                }
            }

            helper.modifiedChunks.clear();
        }

        private void updateBlocks() {
            LogHelperBC.startTimer("Updating Blocks");

            try {
                LogHelperBC.dev("Updating " + helper.blocksToUpdate.size() + " Blocks");
                BlockFalling.fallInstantly = true;
                for (BlockPos pos : helper.blocksToUpdate) {
                    IBlockState state = helper.serverWorld.getBlockState(pos);
                    if (state.getBlock() instanceof BlockFalling) {
                        state.getBlock().updateTick(helper.serverWorld, pos, state, helper.serverWorld.rand);
                    }
                    state.neighborChanged(helper.serverWorld, pos, Blocks.AIR);
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            LogHelperBC.stopTimer();

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
