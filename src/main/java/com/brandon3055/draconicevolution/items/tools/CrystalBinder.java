package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 25/11/2016.
 */
public class CrystalBinder extends Item implements ICrystalBinder {
    public CrystalBinder(Properties properties) {
        super(properties);
    }

    //    public CrystalBinder() {
//        this.setMaxStackSize(1);
//    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }


    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState rotated = state.rotate(world, pos, Rotation.CLOCKWISE_90);
        if (!rotated.equals(state)) {
            world.setBlockAndUpdate(pos, rotated);
            return world.isClientSide ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
