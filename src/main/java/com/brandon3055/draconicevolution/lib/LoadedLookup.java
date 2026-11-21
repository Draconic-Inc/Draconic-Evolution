package com.brandon3055.draconicevolution.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

/**
 * Block entity lookups for positions this code does not own (linked crystals, energy core and reactor links).
 * <p>
 * On the server, {@code Level.getBlockEntity} goes through {@code ServerChunkCache.getChunk(..., load=true)}: on a miss
 * in its 4-entry cache it adds a one-tick UNKNOWN ticket and loads the chunk synchronously if needed. Called every tick,
 * that pins the chunk, so a chunk that was about to unload stays loaded for as long as the link exists. This lookup
 * uses {@code getChunkNow}, which never adds a ticket and returns null for chunks that are not fully loaded.
 */
public final class LoadedLookup {
    private LoadedLookup() {}

    /** The block entity at pos if its chunk is loaded right now; never loads or holds the chunk. */
    @Nullable
    public static BlockEntity blockEntity(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server) {
            LevelChunk chunk = server.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk == null) return null;
            BlockEntity be = chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
            return be == null || be.isRemoved() ? null : be;
        }
        return level.isLoaded(pos) ? level.getBlockEntity(pos) : null;
    }

    /** True if pos is in a chunk that is loaded right now (without adding a ticket). */
    public static boolean isLoaded(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server) {
            return server.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null;
        }
        return level.isLoaded(pos);
    }
}
