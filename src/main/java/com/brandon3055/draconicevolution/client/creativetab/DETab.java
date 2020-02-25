//package com.brandon3055.draconicevolution.client.creativetab;
//
//
//import com.brandon3055.brandonscore.registry.ModFeatureParser;
//import com.brandon3055.draconicevolution.DEFeatures;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.init.Items;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class DETab extends CreativeTabs {
//    private String label;
//    private int tab;
//
//    static ItemStack itemStackStaff = ItemStack.EMPTY;
//
//    public DETab(int id, String modid, String label, int tab) {
//        super(id, modid);
//        this.label = label;
//        this.tab = tab;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public ItemStack getIconItemStack() {
//        if (tab == 0) {
//            if (itemStackStaff.isEmpty()) {
//                itemStackStaff = new ItemStack(DEFeatures.draconicStaffOfPower);
//                DEFeatures.draconicStaffOfPower.modifyEnergy(itemStackStaff, DEFeatures.draconicStaffOfPower.getCapacity(itemStackStaff));
//            }
//
//            return itemStackStaff;
//        }
//        else if (ModFeatureParser.isEnabled(DEFeatures.chaoticCore)) {
//            return new ItemStack(DEFeatures.chaoticCore);
//        }
//        else return new ItemStack(Items.ENDER_EYE);
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public ItemStack getTabIconItem() {
//        return getIconItemStack();
//    }
//
//    @Override
//    public String getTabLabel() {
//        return this.label;
//    }
//}
