package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.client.render.item.RenderItemReactorPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class ReactorPart extends ItemBCore implements ICustomRender {

    public ReactorPart() {
        this.setHasSubtypes(true);
        this.addName(0, "stabilizer_frame");
        this.addName(1, "inner_rotor");
        this.addName(2, "outer_rotor");
        this.addName(3, "rotor_assembly");
        this.addName(4, "focus_ring");
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ModelRegistryHelper.registerItemRenderer(this, new RenderItemReactorPart());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }
}
