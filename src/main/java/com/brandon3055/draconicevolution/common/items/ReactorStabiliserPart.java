package com.brandon3055.draconicevolution.common.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;

/**
 * Created by brandon3055 on 2/10/2015.
 */
public class ReactorStabiliserPart extends ItemDE {

    public static final Map<Integer, String> parts = new HashMap<Integer, String>() {

        {
            put(0, "frame");
            put(1, "rotorInner");
            put(2, "rotorOuter");
            put(3, "rotorAssembly");
            put(4, "stabilizerRing");
        }
    };

    public ReactorStabiliserPart() {
        this.setUnlocalizedName("reactorCraftingPart");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHasSubtypes(true);
        ModItems.register(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        for (Integer i : parts.keySet()) list.add(new ItemStack(item, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        if (parts.containsKey(itemStack.getItemDamage()))
            return super.getUnlocalizedName(itemStack) + "." + parts.get(itemStack.getItemDamage());
        else return super.getUnlocalizedName(itemStack);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {}
}
