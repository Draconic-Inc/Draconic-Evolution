package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 27/08/2014.
 */
public class Key extends ItemDE {

    public Key() {
        this.setUnlocalizedName(Strings.keyName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @SuppressWarnings("all")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @SuppressWarnings("all")
    @Override
    public void addInformation(ItemStack key, EntityPlayer player, List list, boolean par4) {
        if (!player.capabilities.isCreativeMode) return;
        if (key.getItemDamage() == 1) {
            list.add("Creative Master Key");
            return;
        }

        int keyCode = ItemNBTHelper.getInteger(key, "KeyCode", 0);
        list.add(keyCode == 0 ? "Un-Bound" : "Key Code: " + String.valueOf(keyCode));

        if (keyCode != 0) {
            list.add(
                    "Linked to {X:" + ItemNBTHelper.getInteger(key, "X", 0)
                            + " Y:"
                            + ItemNBTHelper.getInteger(key, "Y", 0)
                            + " X:"
                            + ItemNBTHelper.getInteger(key, "Z", 0)
                            + "}");
            if (ItemNBTHelper.getInteger(key, "LockCount", 0) == 0) return;
            for (int i = 1; i <= ItemNBTHelper.getInteger(key, "LockCount", 0); i++) {
                list.add(
                        "Linked to {X:" + ItemNBTHelper.getInteger(key, "X_" + i, 0)
                                + " Y:"
                                + ItemNBTHelper.getInteger(key, "Y_" + i, 0)
                                + " X:"
                                + ItemNBTHelper.getInteger(key, "Z_" + i, 0)
                                + "}");
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        if (stack.getItemDamage() == 1) return true;
        return ItemNBTHelper.getInteger(stack, "KeyCode", 0) != 0;
    }
}
