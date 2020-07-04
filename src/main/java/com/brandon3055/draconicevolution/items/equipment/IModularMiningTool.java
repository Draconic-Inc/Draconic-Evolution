package com.brandon3055.draconicevolution.items.equipment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

import java.util.Set;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularMiningTool extends IModularTieredItem {

    @Override
    default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        return false;
    }
}
