package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

/**
 * Created by brandon3055 on 25/11/2016.
 */
public class CrystalBinder extends ItemBCore implements ICrystalBinder {
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
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
//        BlockState state = context.getWorld().getBlockState(context.getPos());
//        World world = context.getWorld();
//        BlockPos pos = context.getPos();
//        Direction side = context.getFace();
//
//        if (state.getBlock().rotate(state, world, pos, Rotation.CLOCKWISE_90)) {
//            return world.isRemote ? ActionResultType.PASS : ActionResultType.SUCCESS;
//        }
        return ActionResultType.PASS;
    }
}
