package com.brandon3055.draconicevolution.client.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DETab extends CreativeTabs {

    private String label;
    private int tab;

    static ItemStack iconStackStaff;

    public static void initialize() {
        if (ModItems.isEnabled(ModItems.draconicDestructionStaff)) iconStackStaff = ItemNBTHelper
                .setInteger(new ItemStack(ModItems.draconicDestructionStaff), "Energy", 30000000);
        else iconStackStaff = new ItemStack(Items.stick);
    }

    public DETab(int id, String modid, String label, int tab) {
        super(id, modid);
        this.label = label;
        this.tab = tab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack() {

        if (tab == 0) return iconStackStaff;
        else if (ModBlocks.isEnabled(ModBlocks.energyInfuser)) return new ItemStack(ModBlocks.energyInfuser);
        return new ItemStack(Items.ender_eye);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return getIconItemStack().getItem();
    }

    @Override
    public String getTabLabel() {
        return this.label;
    }
}
