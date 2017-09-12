package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 25/11/2016.
 */
public class CrystalBinder extends ItemBCore implements ICrystalBinder {

    public CrystalBinder() {
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock().rotateBlock(world, pos, side)) {
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
