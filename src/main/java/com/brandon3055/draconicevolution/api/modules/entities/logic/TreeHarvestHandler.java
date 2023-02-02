package com.brandon3055.draconicevolution.api.modules.entities.logic;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.Utils;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by brandon3055 on 31/01/2023
 */
public class TreeHarvestHandler implements IHarvestHandler {

    private final int speed;
    private Direction direction;
    private final boolean harvestLeaves;

    public TreeHarvestHandler(int speed, @Nullable Direction direction, boolean leaves) {
        this.speed = speed;
        this.direction = direction;
        this.harvestLeaves = leaves;
    }

    //List of blocks that need to be harvested sorted by distance from origin
    private LinkedList<Long> scanQue = new LinkedList<>();
    private IntObjectMap<LinkedList<Long>> leavesWait = new IntObjectHashMap<>();
    private BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
    /** Set of all block positions that have already been visited/cleared */
    private Set<Long> processedBlocks = new HashSet<>();
    private int leavesWaitIndex = 0;
    private boolean complete = false;

    @Override
    public boolean start(BlockPos origin, Level level, ServerPlayer player) {
        complete = false;
        BlockState state = level.getBlockState(origin);
        if (state.is(BlockTags.LOGS)) {
            queLogAndSearchLeaves(level, origin);
        } else {
            origin = origin.relative(direction == null ? player.getDirection() : direction.getOpposite());
            state = level.getBlockState(origin);
            if (state.is(BlockTags.LOGS)) {
                queLogAndSearchLeaves(level, origin);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void tick(Level level, ServerPlayer player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector) {
        if (complete) return;

        if (!scanQue.isEmpty()) {
            for (int i = 0; i < speed && !scanQue.isEmpty(); i++) {
                updateTreeHarvest(player.level, player, stack, storage, stackCollector);
            }
            return;
        }

        if (leavesWaitIndex <= 7) {
            if (leavesWaitIndex < 0) {
                leavesWaitIndex++;
            } else {
                LinkedList<Long> que = leavesWait.get(leavesWaitIndex);
                for (int i = 0; i < speed && que != null && !que.isEmpty(); i++) {
                    updateLeavesHarvest(player.level, que, player, stack, storage, stackCollector);
                }

                if (que == null || que.isEmpty()) {
                    leavesWaitIndex++;
                }
            }
            return;
        }

        harvestComplete();
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * ((player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.7F + 1.8F));
    }

    private void updateTreeHarvest(Level level, Player player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector) {
        mPos.set(scanQue.removeFirst());

        doHarvest(stack, player, level, mPos.immutable(), storage, stackCollector);

        Utils.betweenClosed(mPos.offset(-1, -1, -1), mPos.offset(1, 1, 1), pos -> {
            long longPos = pos.asLong();
            if (processedBlocks.contains(longPos)) return;
            processedBlocks.add(longPos);

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) return;
            if (state.is(BlockTags.LOGS)) {
                queLogAndSearchLeaves(level, pos);
            }
        });
    }

    private void queLogAndSearchLeaves(Level level, BlockPos pos) {
        scanQue.add(pos.asLong());
        //Scan leaves
        if (!harvestLeaves) return;
        int r = 7;
        if (leavesWaitIndex >= 0) {
            leavesWaitIndex = -4;
            BlockPos.betweenClosed(pos.offset(-r, -r, -r), pos.offset(r, r, r)).forEach(e -> scanLeaves(level, e));
        } else {
            Utils.hollowCube(pos.offset(-r, -r, -r), pos.offset(r, r, r), e -> scanLeaves(level, e));
        }
    }

    private void scanLeaves(Level level, BlockPos pos) {
        long longPos = pos.asLong();
        if (processedBlocks.contains(longPos)) return;
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.LOGS)) return; //If its logs then it needs to be left for the regular scan to find in case its part of another tree,
        //Anything else we can ignore.
        processedBlocks.add(longPos);

        if (state.is(BlockTags.LEAVES)) {
            if (state.getBlock() instanceof LeavesBlock) {
                int distance = MathHelper.clip(state.getValue(LeavesBlock.DISTANCE), 0, 7);
                if (distance == LeavesBlock.DECAY_DISTANCE) {
                    leavesWait.computeIfAbsent(7, e -> new LinkedList<>()).add(longPos);
                } else {
                    leavesWait.computeIfAbsent(distance, e -> new LinkedList<>()).add(longPos);
                }
            } else {
                leavesWait.computeIfAbsent(7, e -> new LinkedList<>()).add(longPos);
            }
        }
    }

    private void updateLeavesHarvest(Level level, LinkedList<Long> que, Player player, ItemStack stack, IOPStorage storage, InventoryDynamic stackCollector) {
        mPos.set(que.removeFirst());
        BlockState state = level.getBlockState(mPos);
        if (state.getBlock() instanceof LeavesBlock && state.getValue(LeavesBlock.DISTANCE) < LeavesBlock.DECAY_DISTANCE) return;
        doHarvest(stack, player, level, mPos.immutable(), storage, stackCollector);
    }

    private void harvestComplete() {
        scanQue.clear();
        processedBlocks.clear();
        leavesWait.clear();
        complete = true;
    }

    @Override
    public boolean isDone() {
        return complete;
    }

    @Override
    public void stop(Level level, ServerPlayer player) {
        harvestComplete();
    }
}
