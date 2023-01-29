package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 24/11/2014.
 */
public class DraconiumEnergyCore extends ItemDE {

    IIcon[] icons = new IIcon[2];

    public DraconiumEnergyCore() {
        this.setUnlocalizedName(Strings.draconiumEnergyCoreName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHasSubtypes(true);
        ModItems.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName()) + 0);
        icons[1] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName()) + 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return icons[damage];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }
}
