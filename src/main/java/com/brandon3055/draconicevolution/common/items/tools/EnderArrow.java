package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

/**
 * Created by Brandon on 15/08/2014.
 */
public class EnderArrow extends ItemDE {

    public EnderArrow() {
        this.setUnlocalizedName(Strings.enderArrowName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        ModItems.register(this);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "ender_arrow");
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add(
                EnumChatFormatting.DARK_PURPLE + ""
                        + EnumChatFormatting.ITALIC
                        + StatCollector.translateToLocal("info.arrowInfo.txt"));
        list.add(
                EnumChatFormatting.DARK_PURPLE + ""
                        + EnumChatFormatting.ITALIC
                        + StatCollector.translateToLocal("info.arrowInfo0.txt"));
    }
}
