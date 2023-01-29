package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 23/08/2014.
 */
public class SafetyMatch extends ItemDE {

    IIcon boxIcon;

    public SafetyMatch() {
        this.setUnlocalizedName(Strings.safetyMatchName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setHasSubtypes(true);
        ModItems.register(this);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        if (itemStack.getItemDamage() == 1000) return super.getUnlocalizedName(itemStack);
        else return super.getUnlocalizedName(itemStack) + "Box";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1000));
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItemDamage() == 1000 ? 16 : 1;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return stack.getItemDamage() == 1000 ? 1000 : 20;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "safety_match");
        boxIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "box_of_matches");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.getItemDamage() != 1000 && stack.getItemDamage() != 0;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return damage == 1000 ? itemIcon : boxIcon;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_,
            float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_7_ == 0) {
            --y;
        }

        if (p_77648_7_ == 1) {
            ++y;
        }

        if (p_77648_7_ == 2) {
            --z;
        }

        if (p_77648_7_ == 3) {
            ++z;
        }

        if (p_77648_7_ == 4) {
            --x;
        }

        if (p_77648_7_ == 5) {
            ++x;
        }

        if (!player.canPlayerEdit(x, y, z, p_77648_7_, stack)) {
            return false;
        } else {
            if (world.isAirBlock(x, y, z)) {
                world.playSoundEffect(
                        (double) x + 0.5D,
                        (double) y + 0.5D,
                        (double) z + 0.5D,
                        "fire.ignite",
                        1.0F,
                        itemRand.nextFloat() * 0.4F + 0.8F);
                world.setBlock(x, y, z, ModBlocks.safetyFlame);
            }

            if (!player.capabilities.isCreativeMode) {
                if (stack.getItemDamage() == 1000) stack.stackSize--;
                else stack.damageItem(1, player);
            }
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.safetyMatch.txt"));
    }
}
