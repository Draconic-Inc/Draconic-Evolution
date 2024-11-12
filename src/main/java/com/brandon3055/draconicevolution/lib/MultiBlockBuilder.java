package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.multiblock.MultiBlockDefinition;
import com.brandon3055.brandonscore.multiblock.MultiBlockPart;
import com.brandon3055.draconicevolution.blocks.tileentity.MultiBlockController;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 2/11/18.
 */
public class MultiBlockBuilder implements IProcess {

    private final Level level;
    private final BlockPos inWorldOrigin;
    private final MultiBlockDefinition definition;
    private final Player player;
    private MultiBlockController controller;
    private boolean isDead = false;
    private Map<BlockPos, MultiBlockPart> workList = new HashMap<>();
    private LinkedList<BlockPos> workOrder = new LinkedList<>();

    public MultiBlockBuilder(Level level, BlockPos inWorldOrigin, MultiBlockDefinition definition, Player player, @Nullable MultiBlockController controller) {
        this.level = level;
        this.inWorldOrigin = inWorldOrigin;
        this.definition = definition;
        this.player = player;
        this.controller = controller;
        buildWorkList();
    }

    private void buildWorkList() {
        Map<BlockPos, MultiBlockPart> structureBlocks = definition.getBlocksAt(inWorldOrigin);
        for (Map.Entry<BlockPos, MultiBlockPart> entry : structureBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            MultiBlockPart part = entry.getValue();
            if (!part.isMatch(level, pos)) {
                if (level.isEmptyBlock(pos)) {
                    workList.put(pos, part);
                } else {
                    isDead = true;
                    player.sendSystemMessage(Component.translatable("struct_build.brandonscore.found_invalid", level.getBlockState(pos).getBlock().getName(), "[X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + "]").withStyle(ChatFormatting.RED));
                    return;
                }
            }
        }

        workOrder.addAll(workList.keySet());
        workOrder.sort(Comparator.comparingInt(value -> (int) value.distSqr(inWorldOrigin)));
    }

    @Override
    public void updateProcess() {
        if (workOrder.isEmpty() || !player.isAlive() || player.level() != level) {
            isDead = true;
            if (controller != null) {
                controller.validateStructure();
            }
            return;
        }

        BlockPos pos = workOrder.poll();
        MultiBlockPart part = workList.get(pos);
        if (part.isMatch(level, pos)) {
            return;
        } else if (!level.isEmptyBlock(pos)) {
            player.sendSystemMessage(Component.translatable("struct_build.brandonscore.found_invalid", level.getBlockState(pos).getBlock().getName(), "[X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + "]").withStyle(ChatFormatting.RED));
            isDead = true;
            return;
        }

        Block placeBlock = extractPart(player, part);
        if (placeBlock != null) {
            level.setBlockAndUpdate(pos, placeBlock.defaultBlockState());
            SoundType soundtype = placeBlock.getSoundType(placeBlock.defaultBlockState(), level, pos, player);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        } else {
            player.sendSystemMessage(Component.translatable("struct_build.brandonscore.missing_required", part.getFirstValidBlock().getName()).withStyle(ChatFormatting.RED));
            isDead = true;
        }
    }

    @Nullable
    private Block extractPart(Player player, MultiBlockPart part) {
        if (player.getAbilities().instabuild) {
            return part.getFirstValidBlock();
        }

        IItemHandler handler = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack inSlot = handler.getStackInSlot(i);
                if (!inSlot.isEmpty() && inSlot.getItem() instanceof BlockItem) {
                    Block slotBlock = ((BlockItem) inSlot.getItem()).getBlock();
                    if (part.validBlocks().contains(slotBlock)) {
                        ItemStack extracted = handler.extractItem(i, 1, false);
                        if (!extracted.isEmpty() && extracted.getItem() instanceof BlockItem && ((BlockItem) extracted.getItem()).getBlock() == slotBlock) {
                            return slotBlock;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
