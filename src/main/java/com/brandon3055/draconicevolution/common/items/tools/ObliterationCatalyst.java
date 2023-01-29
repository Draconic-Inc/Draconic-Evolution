package com.brandon3055.draconicevolution.common.items.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityChaosDrill;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 14/09/2014.
 */
public class ObliterationCatalyst extends ItemDE {

    public ObliterationCatalyst() {
        this.setUnlocalizedName(Strings.obliterationCatalystName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        LogHelper.info("onItemRightClick");
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        if (!world.isRemote) world.spawnEntityInWorld(new EntityChaosDrill(world, player));
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i1, int i2, int i3, int i4,
            float f1, float f2, float f3) {
        // LogHelper.info("onItemUse");
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i1, boolean b1) {}

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        // LogHelper.info("onUsingTick");

        super.onUsingTick(stack, player, count);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        // LogHelper.info("getMaxItemUseDuration");
        return 70000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int meta) {
        // LogHelper.info("onPlayerStoppedUsing");
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }
}
