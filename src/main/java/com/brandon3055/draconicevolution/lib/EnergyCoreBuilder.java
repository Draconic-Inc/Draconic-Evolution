package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 2/11/18.
 */
public class EnergyCoreBuilder implements IProcess {

    private final TileEnergyStorageCore core;
    private final EntityPlayer player;
    private boolean isDead = false;
    private Map<BlockPos, IBlockState> workList = new HashMap<>();
    private LinkedList<BlockPos> workOrder = new LinkedList<>();
    private World world;

    public EnergyCoreBuilder(TileEnergyStorageCore core, EntityPlayer player) {
        this.core = core;
        this.player = player;
        this.world = core.getWorld();
        buildWorkList();
    }

    private void buildWorkList() {
        EnergyCoreStructure structure = core.coreStructure;
        MultiBlockStorage storage = structure.getStorageForTier(core.tier.value);
        BlockPos start = core.getPos().add(structure.getCoreOffset(core.tier.value));
        Map<BlockPos, Block> structureBlocks = new HashMap<>();
        storage.forEachBlock(start, (key1, value) -> {
            Map<String, Block> blockCache = new HashMap<>();
            structureBlocks.put(key1, blockCache.computeIfAbsent(value, s -> Block.getBlockFromName(value)));
        });

        World world = core.getWorld();
        for (BlockPos key : structureBlocks.keySet()) {
            Block targetBlock = structureBlocks.get(key);
            if (targetBlock == null) continue;
            if (world.isAirBlock(key)) {
                workList.put(key, targetBlock.getDefaultState());
                continue;
            }
            IBlockState state = world.getBlockState(key);
            if (state.getBlock() != targetBlock) {
                isDead = true;
                player.sendMessage(new TextComponentTranslation("ecore.de.assemble_found_invalid.txt", state.getBlock().getLocalizedName(), key.toString()).setStyle(new Style().setColor(TextFormatting.RED)));
                return;
            }
        }

        workOrder.addAll(workList.keySet());
        workOrder.sort(Comparator.comparingInt(value -> (int) value.distanceSq(core.getPos())));
    }

    @Override
    public void updateProcess() {
        if (workOrder.isEmpty() || player.isDead) {
            isDead = true;
            return;
        }

        BlockPos pos = workOrder.poll();
        IBlockState state = workList.get(pos);
        if (!world.isAirBlock(pos)) {
            if (world.getBlockState(pos).getBlock() == state.getBlock()) {
                return;
            }
            player.sendMessage(new TextComponentTranslation("ecore.de.assemble_error_expected_air.txt", pos.toString()).setStyle(new Style().setColor(TextFormatting.RED)));
            isDead = true;
            return;
        }

        ItemStack required = new ItemStack(state.getBlock());
        if (player.capabilities.isCreativeMode || extractItem(required)) {
            world.setBlockState(pos, state);
            SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        }
        else {
            player.sendMessage(new TextComponentTranslation("ecore.de.assemble_missing_required.txt", state.getBlock().getLocalizedName()).setStyle(new Style().setColor(TextFormatting.RED)));
            isDead = true;
        }
    }

    private boolean extractItem(ItemStack toExtract) {
        IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler == null) return false;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty() && inSlot.getItem() == toExtract.getItem()) {
                ItemStack extracted = handler.extractItem(i, 1, false);
                if (!extracted.isEmpty() && extracted.getItem() == toExtract.getItem()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean isDead() {
        return isDead;
    }
}
