package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.draconicevolution.items.tools.old.WyvernAxe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 25/08/2016.
 */
public class TreeCollector {

    private World world;
    private boolean breakDown;
    private int connectionRadius;
    private ItemStack stack;
    private PlayerEntity player;
    private WyvernAxe axe;
    private boolean collectionComplete = false;
    private boolean isKilled = false;
    private List<IProcess> activeProcesses = new ArrayList<IProcess>();
    private InventoryDynamic inventory = new InventoryDynamic();
    private CollectorCallBack collectionCallback = null;
    public int collected = 0;
    public int energyUsed = 0;

    public TreeCollector(World world, boolean breakDown, int connectionRadius, ItemStack stack, PlayerEntity player, WyvernAxe axe) {
        this.world = world;
        this.breakDown = breakDown;
        this.connectionRadius = connectionRadius;
        this.stack = stack;
        this.player = player;
        this.axe = axe;
    }

    public void collectTree(BlockPos startPos) {
        CollectTreeProcess process = new CollectTreeProcess(this, startPos, inventory, new ArrayList<BlockPos>());
        ProcessHandler.addProcess(process);
    }

    public void killCollector() {
        isKilled = true;
    }

    public void clearCollected() {
        collectionComplete = false;
        isKilled = false;
        inventory.clear();
        collected = 0;
    }

    public boolean isCollectionComplete() {
        return collectionComplete;
    }

    public InventoryDynamic getCollected() {
        return inventory;
    }

    public void setCollectionCallback(CollectorCallBack collectionCallback) {
        this.collectionCallback = collectionCallback;
    }

    public static class CollectTreeProcess implements IProcess {

        private TreeCollector collector;
        private final World world;
        private final BlockPos pos;
        private InventoryDynamic inventory;
        private final List<BlockPos> processedBlocks;
        private boolean isDead = false;
        private int ticks = 0;
        private double randOffset = 0;

        public CollectTreeProcess(TreeCollector selector, BlockPos pos, InventoryDynamic inventory, List<BlockPos> processedBlocks) {
            this.collector = selector;
            this.world = selector.world;
            this.pos = pos;
            this.inventory = inventory;
            this.processedBlocks = processedBlocks;
            selector.activeProcesses.add(this);
            randOffset = 0.5D + (selector.world.rand.nextDouble() * 0.5D);
        }

        @Override
        public void updateProcess() {
            if (ticks < (double) (collector.activeProcesses.size() / 2) * randOffset) {
                ticks++;
                return;
            }

            if (collector.isKilled) {
                isDead = true;
                collector.activeProcesses.remove(this);
                if (collector.activeProcesses.size() == 0) {
                    collector.collectionComplete = true;
                }
                return;
            }

            if ((collector.axe.getEnergyStored(collector.stack) - collector.energyUsed) < collector.axe.energyPerOperation && !collector.player.abilities.isCreativeMode) {
                isDead = true;
                return;
            }

            BlockState state = world.getBlockState(pos);

            if (!ForgeHooks.canHarvestBlock(state, collector.player, world, pos)) {
                isDead = true;
                return;
            }

            if (collector.collectionCallback != null) {
                collector.collectionCallback.call(pos);
            }

            int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) collector.player).interactionManager.getGameType(), (ServerPlayerEntity) collector.player, pos);
            if (xp == -1) {
                isDead = true;
                return;
            }

            inventory.xp += xp;

            collector.energyUsed += collector.axe.energyPerOperation;

            world.playEvent(2001, pos, Block.getStateId(state));
            BlockToStackHelper.breakAndCollect(world, pos, inventory, xp);
            collector.collected++;

            int rad = collector.connectionRadius;
            Iterable<BlockPos> blocks = BlockPos.getAllInBoxMutable(pos.add(-rad, collector.breakDown ? -rad : 0, -rad), pos.add(rad, rad, rad));
            for (BlockPos newPos : blocks) {

                if (processedBlocks.contains(newPos)) {
                    continue;
                }

                processedBlocks.add(newPos);
                BlockState newState = world.getBlockState(newPos);

                if (newState.getMaterial() == Material.WOOD) {
                    ProcessHandler.addProcess(new CollectTreeProcess(collector, newPos, inventory, processedBlocks));
                }
            }

            isDead = true;
            collector.activeProcesses.remove(this);
            if (collector.activeProcesses.size() == 0) {
                collector.collectionComplete = true;
            }
        }

        @Override
        public boolean isDead() {
            return isDead;
        }
    }
}
