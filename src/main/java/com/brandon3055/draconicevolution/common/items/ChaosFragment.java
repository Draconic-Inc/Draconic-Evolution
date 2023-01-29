package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.common.ModItems;

/**
 * Created by brandon3055 on 8/10/2015.
 */
public class ChaosFragment extends ItemDE {

    public ChaosFragment() {
        this.setUnlocalizedName("chaosFragment");
        this.setHasSubtypes(true);

        ModItems.register(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }
}
