package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class ReactorPart extends ItemBCore /*implements IRenderOverride*/ {

    public ReactorPart(Properties properties) {
        super(properties);
    }

    //    public ReactorPart() {
//        this.setHasSubtypes(true);
//        this.addName(0, "stabilizer_frame");
//        this.addName(1, "inner_rotor");
//        this.addName(2, "outer_rotor");
//        this.addName(3, "rotor_assembly");
//        this.addName(4, "focus_ring");
//    }

//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
//        if (isInCreativeTab(tab)) {
//            for (int i = 0; i < 5; i++) {
//                subItems.add(new ItemStack(this, 1, i));
//            }
//        }
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ModelRegistryHelper.registerItemRenderer(this, new RenderItemReactorPart());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }
}
