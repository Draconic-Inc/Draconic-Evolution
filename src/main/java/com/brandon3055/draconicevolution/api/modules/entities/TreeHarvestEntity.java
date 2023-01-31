package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.TreeHarvestData;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.brandon3055.draconicevolution.DraconicEvolution.LOGGER;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class TreeHarvestEntity extends ModuleEntity<TreeHarvestData> implements EntityOverridesItemUse {

    //Single tree mode (Right click tree for single? Right click air for area?)
    //User defined range
    //Ability to enable/disable leave collection

    private boolean areaMode = true;
    private boolean complete = true;
    /**
     * The origin of the tree scan area.
     */
    private BlockPos areaModeOrigin = null;

    public TreeHarvestEntity(Module<TreeHarvestData> module) {
        super(module);
    }

    /**
     * Remaining blocks to scan in the current tree.
     * These are empty positions that previously contained logs.
     */
    private LinkedList<Long> scanQue = new LinkedList<>();
    private IntObjectMap<LinkedList<Long>> leavesWait = new IntObjectHashMap<>();

    /** Set of all block positions that have already been visited/cleared by the scanner or the harvester */
    private Set<Long> processedBlocks = new HashSet<>();

    private BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

    private int leavesWaitIndex = 0;

    private void useTick(LivingEntityUseItemEvent.Tick event) {
        if (complete || !(event.getEntity() instanceof ServerPlayer player)) return;

        if (!scanQue.isEmpty()) {
            for (int i = 0; i < 100 && !scanQue.isEmpty(); i++) {
                updateTreeHarvest(player.level);
            }

        } else if (leavesWaitIndex <= 7) {
            if (leavesWaitIndex < 0) {
                leavesWaitIndex++;
                return;
            }
            LinkedList<Long> que = leavesWait.get(leavesWaitIndex);
//            LOGGER.info("Process Leaves at distance " + leavesWaitIndex + ", " + (que == null ? 0 : que.size()));
            for (int i = 0; i < 200 && que != null && !que.isEmpty(); i++) {
                updateLeavesHarvest(player.level, que);
            }
            if (que == null || que.isEmpty()) {
                leavesWaitIndex++;
            }
        } else if (areaMode && areaModeOrigin != null) {
            if (scanPos == -1) {
                initScanArea();
                return;
            }

            LinkedList<Long> que = scanPos < 0 ? null : treeSearch.get(scanPos);
            if (que == null || que.isEmpty()) {
                scanPos++;
                LOGGER.info("Scanning at range " + scanPos);
                if (scanPos > getModule().getData().getRange()) {
                    harvestComplete();
                }
                return;
            }

            for (int i = 0; i < 1000 && scanQue.isEmpty(); i++) {
                updateTreeLocate(player.level, que);
                if (que.isEmpty() || complete) return;
            }
        } else {
            harvestComplete();
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * ((player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.7F + 1.8F));
        }
    }

    private IntObjectMap<LinkedList<Long>> treeSearch = new IntObjectHashMap<>();
    private int scanPos = -1;

    private void initScanArea() {
        treeSearch.clear();
        if (areaModeOrigin == null) {
            harvestComplete();
            return;
        }
        int radius = getModule().getData().getRange();
        int radSq = radius * radius;

        IntObjectMap<List<Long>> tempMap = new IntObjectHashMap<>();
        Utils.betweenClosed(areaModeOrigin.offset(-radius, 0, -radius), areaModeOrigin.offset(radius, 0, radius), pos -> {
            int distSq = (int) areaModeOrigin.distSqr(pos);
            if (distSq > radSq) return;
            tempMap.computeIfAbsent(distSq, i -> new LinkedList<>()).add(pos.asLong());
        });

        tempMap.forEach((distSq, positions) -> treeSearch.computeIfAbsent((int) Math.sqrt(distSq), e -> new LinkedList<>()).addAll(positions));
        scanPos = 0;
    }

    private void updateTreeLocate(Level level, LinkedList<Long> que) {
        mPos.set(que.removeFirst());
//        mPos.move(-10 + level.random.nextInt(20), 0, -10 + level.random.nextInt(20));
        if (processedBlocks.contains(mPos.asLong())) return;

//        level.setBlockAndUpdate(mPos, Blocks.GLASS.defaultBlockState());

        BlockState state = level.getBlockState(mPos);
        if (state.is(BlockTags.LOGS)) {
            leavesWait.clear();

        processedBlocks.clear();
            processLog(level, mPos);
            LOGGER.info("Start: " + leavesWaitIndex);
        }
    }

    private void updateTreeHarvest(Level level) {
        mPos.set(scanQue.removeFirst());
        Utils.betweenClosed(mPos.offset(-1, -1, -1), mPos.offset(1, 1, 1), pos -> {
            long longPos = pos.asLong();
            if (processedBlocks.contains(longPos)) return;
            processedBlocks.add(longPos);

            BlockState state = level.getBlockState(pos);
            if (state.isAir()) return;
            if (state.is(BlockTags.LOGS)) {
                processLog(level, pos);
            }
        });
    }

    private void processLog(Level level, BlockPos pos) {
        scanQue.add(pos.asLong());
        level.destroyBlock(pos, false);

        //Scan leaves
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

    private void updateLeavesHarvest(Level level, LinkedList<Long> que) {
        mPos.set(que.removeFirst());
        BlockState state = level.getBlockState(mPos);
        if (state.getBlock() instanceof LeavesBlock && state.getValue(LeavesBlock.DISTANCE) < LeavesBlock.DECAY_DISTANCE) return;
        level.destroyBlock(mPos, false);
    }


    private void harvestComplete() {
        scanQue.clear();
        processedBlocks.clear();
        leavesWait.clear();
        areaModeOrigin = null;
        complete = areaMode = true;
        LOGGER.info("Harvest end");
    }

    private void endUse(LivingEntityUseItemEvent event) {
        harvestComplete();
    }

    @Override
    public void onEntityUseItem(LivingEntityUseItemEvent useEvent) {
        if (useEvent.isCanceled()) return;
        if (useEvent instanceof LivingEntityUseItemEvent.Start event) {
            event.setDuration(72000);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Tick event) {
            useTick(event);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Stop || useEvent instanceof LivingEntityUseItemEvent.Finish) {
            endUse(useEvent);
        }
    }

    @Override
    public void onPlayerInteractEvent(PlayerInteractEvent playerEvent) {

//        if (!(playerEvent instanceof PlayerInteractEvent.RightClickBlock)) {
//            playerEvent.setCanceled(true);
//            return;
//        }
//
//        Level level = playerEvent.getWorld();
//        Utils.hollowCube(playerEvent.getPos(), playerEvent.getPos().offset(10, 5, 20), pos -> {
//            if (level.isEmptyBlock(pos)) {
//                level.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
//            } else if (level.getBlockState(pos).is(Blocks.GLASS)) {
//                level.setBlockAndUpdate(pos, Blocks.RED_STAINED_GLASS.defaultBlockState());
//            }
//        });
//
//        if (true) {
//            playerEvent.setCanceled(true);
//            return;
//        }

        if (playerEvent.isCanceled()) return;
        complete = false;
        if (playerEvent instanceof PlayerInteractEvent.RightClickItem event && areaMode) {
            if (getModule().getData().getRange() <= 0) return;
            if (!playerEvent.getWorld().isClientSide()) {
                areaModeOrigin = event.getPos();
                scanPos = -1;
            }
            LOGGER.info("Start Area");
        } else if (playerEvent instanceof PlayerInteractEvent.RightClickBlock event) {
            BlockState state = event.getPlayer().level.getBlockState(event.getPos());
            if (!state.is(BlockTags.LOGS)) return;
            areaMode = false;
            if (!playerEvent.getWorld().isClientSide()) {
                leavesWaitIndex = 0;
                processLog(event.getWorld(), event.getPos());
            }
            LOGGER.info("Start Single");
        } else {
            return;
        }

        playerEvent.setCanceled(true);
        playerEvent.getPlayer().startUsingItem(playerEvent.getHand());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("module." + MODID + ".tree_harvest.single").withStyle(ChatFormatting.DARK_GRAY));
            if (getModule().getData().getRange() > 0) {
                tooltip.add(new TranslatableComponent("module." + MODID + ".tree_harvest.area").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modifyFirstPersonUsingPose(RenderHandEvent event, boolean leftHand) {
        PoseStack poseStack = event.getPoseStack();
        Player player = Minecraft.getInstance().player;
        int handOffset = !leftHand ? 1 : -1;

        poseStack.translate((float) handOffset * -0.2785682F, 0.18344387F, 0.15731531F);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) handOffset * 35.3F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) handOffset * -9.785F));
        float drawTime = (float) 72000 - ((float) player.getUseItemRemainingTicks() - event.getPartialTicks() + 1.0F);
        float charge = drawTime / 20.0F;
        charge = (charge * charge + charge * 2.0F) / 3.0F;
        if (charge > 1.0F) {
            charge = 1.0F;
        }

        if (charge > 0.1F) {
            float f15 = Mth.sin((drawTime - 0.1F) * 1.3F);
            float f18 = charge - 0.1F;
            float animOffset = f15 * f18;
            poseStack.translate(animOffset * 0.0F, animOffset * 0.004F, animOffset * 0.0F);
        }

        poseStack.translate(charge * 0.0F, charge * 0.0F, charge * 0.04F);
        poseStack.scale(1.0F, 1.0F, 1.0F + charge * 0.2F);
        poseStack.mulPose(Vector3f.YN.rotationDegrees((float) handOffset * 45.0F));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modifyPlayerModelPose(Player player, PlayerModel<?> model, boolean leftHand) {
        if (!leftHand) {
            model.rightArm.yRot = -0.1F + model.head.yRot;
            model.leftArm.yRot = 0.1F + model.head.yRot + 0.4F;
            model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
        } else {
            model.rightArm.yRot = -0.1F + model.head.yRot - 0.4F;
            model.leftArm.yRot = 0.1F + model.head.yRot;
            model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
        }

        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
    }
}
