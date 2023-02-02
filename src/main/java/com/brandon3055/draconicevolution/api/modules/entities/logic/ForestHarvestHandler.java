package com.brandon3055.draconicevolution.api.modules.entities.logic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by brandon3055 on 31/01/2023
 */
public class ForestHarvestHandler implements IHarvestHandler {

    private final int speed;
    private final int range;
    private final boolean harvestLeaves;
    private BlockPos origin;
    //List of blocks that need to be harvested sorted by distance from origin
    private PriorityQueue<Pos> sortedHarvestQue = new PriorityQueue<>();
    private LinkedList<Long> searchQue = new LinkedList<>();
    private Set<Long> processedBlocks = new HashSet<>();
    private Set<Long> searchedXZPositions = new HashSet<>();
    private boolean complete = false;

    private BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

    public ForestHarvestHandler(int speed, int range, boolean leaves) {
        this.speed = speed;
        this.range = range;
        this.harvestLeaves = leaves;
    }

    @Override
    public boolean start(BlockPos origin, Level level, ServerPlayer player) {
        BlockState state = level.getBlockState(origin);
        if (!canSearchUnder(state)) {
            return false;
        }

        int i = 0;
        do {
            if (i++ > 80) return false;
            origin = origin.below();
            if (origin.getY() < level.getMinBuildHeight()) return false;
            state = level.getBlockState(origin);
            if (!canSearchThrough(state) || isHarvestable(state)) continue;
            if (!canSearchUnder(level.getBlockState(origin.above()))) {
                return false;
            }

            searchQue.add(origin.asLong());
            this.origin = origin.immutable();
            break;
        } while (true);

        return true;
    }

    @Override
    public void tick(Level level, ServerPlayer player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector) {
        if (origin == null || complete || level.isClientSide) return;

        if (!sortedHarvestQue.isEmpty()) {
            for (int i = 0; i < speed && !sortedHarvestQue.isEmpty(); i++) {
                updateHarvest(level, player, stack, storage, stackCollector);
            }
//            return;
        }

        if (!searchQue.isEmpty()) {
            for (int i = 0; i < speed * 4 && !searchQue.isEmpty(); i++) {
                updateSearch(level, player);
            }
        }

        if (!sortedHarvestQue.isEmpty() || !searchQue.isEmpty()) return;

        complete = true;//temp
//        harvestComplete();
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * ((player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.7F + 1.8F));
    }

    private void updateSearch(Level level, Player player) {
        mPos.set(searchQue.removeFirst());



        if (level instanceof ServerLevel serverLevel && level.random.nextFloat() < 0.1) {
            serverLevel.sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, true, mPos.getX() + 0.5, mPos.getY() + 1.5, mPos.getZ() + 0.5, 1, 0, 0, 0, 0.1);
        }

        Utils.hollowCube(mPos.offset(-1, -1, -1), mPos.offset(1, 1, 1), pos -> {
            long longXZPos = BlockPos.asLong(pos.getX(), 0, pos.getZ());
            if (searchedXZPositions.contains(longXZPos)) return;
            long longPos = pos.asLong();

            if (origin.distSqr(pos) > range * range) {
                searchedXZPositions.add(longXZPos);
                processedBlocks.add(longPos);
                return;
            }

            BlockState state = level.getBlockState(pos);
            if (isHarvestable(state)) {
                searchedXZPositions.add(longXZPos);
                queHarvest(level, pos);
                return;
            }

            BlockState aboveState = level.getBlockState(pos.above());
            if (canSearchThrough(state) && canSearchUnder(aboveState)) {
                searchedXZPositions.add(longXZPos);
//                level.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
                searchQue.add(pos.asLong());
                if (isHarvestable(aboveState)) {
                    queHarvest(level, pos.above());
                }
            }

            processedBlocks.add(longPos);
        });
    }

    private void updateHarvest(Level level, Player player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector) {
//        Pos next = sortedScanQue.remove(0);
        Pos next = sortedHarvestQue.poll();
        if (next == null) return;
        mPos.set(next.pos);

        //Harvest
        doHarvest(stack, player, level, mPos.immutable(), storage, stackCollector);

        if (level instanceof ServerLevel serverLevel && level.random.nextFloat() < 0.10) {
            serverLevel.sendParticles((ServerPlayer) player, ParticleTypes.FLAME, true, mPos.getX() + 0.5, mPos.getY() + 0.5, mPos.getZ() + 0.5, 1, 0, 0, 0, 0.1);
        }

        //Search surrounding
        Utils.hollowCube(mPos.offset(-1, -1, -1), mPos.offset(1, 1, 1), pos -> {
            long longPos = pos.asLong();
            if (processedBlocks.contains(longPos)) return;
            processedBlocks.add(longPos);

            if (origin.distSqr(pos) > range * range) {
                return;
            }

            BlockState state = level.getBlockState(pos);

            if (isHarvestable(state)) {
                queHarvest(level, pos);
            }
        });
    }

    private void queHarvest(Level level, BlockPos pos) {
        sortedHarvestQue.add(new Pos(pos.asLong(), (int) Utils.getDistanceSq(pos.getX(), pos.getZ(), origin.getX(), origin.getZ()) + level.random.nextInt(16)));
    }


    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void stop(Level level, ServerPlayer player) {

    }

    /**
     * If true then the block can be traversed by the search process
     */
    private boolean canSearchThrough(BlockState state) {
        return !state.isAir() && !state.is(BlockTags.REPLACEABLE_PLANTS) && !state.is(BlockTags.FLOWERS) && !state.is(BlockTags.LOGS) && !state.is(BlockTags.LEAVES) && state.getFluidState().isEmpty();
    }

    private boolean canSearchUnder(BlockState state) {
        return (state.isAir() || state.is(BlockTags.REPLACEABLE_PLANTS) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) && state.getFluidState().isEmpty();
    }

    private boolean isHarvestable(BlockState state) {
        return state.is(BlockTags.LOGS) || (harvestLeaves && state.is(BlockTags.LEAVES));
    }

    private record Pos(long pos, int distance) implements Comparable<Pos> {
        @Override
        public int compareTo(@NotNull ForestHarvestHandler.Pos o) {
            return Integer.compare(distance, o.distance);
        }
    }
}
