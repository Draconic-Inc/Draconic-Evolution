//package com.brandon3055.draconicevolution.api.modules_old;
//
//import com.brandon3055.brandonscore.items.ItemBCore;
//import com.brandon3055.draconicevolution.api.TechLevel;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.world.World;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
///**
// * Created by brandon3055 on 8/4/20.
// *
// * This is meant as a convenient base class for your item modules
// */
//public class ModuleItemBase extends ItemBCore implements IModule {
//
//    private final ModuleType<?> type;
//    private final TechLevel techLevel;
//
//    public ModuleItemBase(Properties properties, ModuleType<?> type, TechLevel techLevel) {
//        super(properties);
//        this.type = type;
//        this.techLevel = techLevel;
//    }
//
//    @Override
//    public ModuleType<?> getModuleType() {
//        return type;
//    }
//
//    @Override
//    public TechLevel getTechLevel() {
//        return techLevel;
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//        //TODO add info like module type and module grid size
//    }
//}
