package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;

/**
 * Created by Brandon on 31/10/2014.
 */
public class DraconiumChestItemBlock extends ItemBlockCustomData {

    public DraconiumChestItemBlock(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add(StatCollector.translateToLocal("info.draconiumChestInfo1.txt"));
        list.add(StatCollector.translateToLocal("info.draconiumChestInfo2.txt"));
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public boolean getShareTag() {
        return false;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
