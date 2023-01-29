package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;

/**
 * Created by brandon3055 on 8/10/2015.
 */
public class Nugget extends ItemDE {

    private IIcon draconium;
    private IIcon awakened;

    public Nugget() {
        this.setUnlocalizedName("nugget");
        this.setHasSubtypes(true);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);

        ModItems.register(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + (itemStack.getItemDamage() == 0 ? ".draconium" : ".awakened");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        draconium = iconRegister.registerIcon(References.RESOURCESPREFIX + "nuggetDraconium");
        awakened = iconRegister.registerIcon(References.RESOURCESPREFIX + "nuggetAwakened");
    }

    @Override
    public IIcon getIconFromDamage(int dmg) {
        return dmg == 0 ? draconium : awakened;
    }
}
