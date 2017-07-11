package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.client.render.item.RenderItemReactorPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class ReactorPart extends ItemBCore implements IRenderOverride {

    public ReactorPart() {
        this.setHasSubtypes(true);
        this.addName(0, "stabilizer_frame");
        this.addName(1, "inner_rotor");
        this.addName(2, "outer_rotor");
        this.addName(3, "rotor_assembly");
        this.addName(4, "focus_ring");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < 5; i++) {
                subItems.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderer(Feature feature) {
        ModelRegistryHelper.registerItemRenderer(this, new RenderItemReactorPart());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }
}
