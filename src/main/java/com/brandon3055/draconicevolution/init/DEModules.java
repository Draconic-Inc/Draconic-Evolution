//package com.brandon3055.draconicevolution.init;
//
//import com.brandon3055.draconicevolution.DraconicEvolution;
//import com.brandon3055.draconicevolution.api.TechLevel;
//import com.brandon3055.draconicevolution.api.modules_old.IModule;
//import com.brandon3055.draconicevolution.api.modules_old.ModuleType;
//import com.brandon3055.draconicevolution.items.modules.ModuleItemTest;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemGroup;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.registries.ObjectHolder;
//
//import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;
//
///**
// * Created by brandon3055 on 18/3/2016.
// * This class contains a reference to all blocks and items in Draconic Evolution
// */
//@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
//@ObjectHolder(DraconicEvolution.MODID)
//public class DEModules {
//
//    //@formatter:off
//    @ObjectHolder("module_test1")             public static ModuleItemTest                      module_test1;
//    @ObjectHolder("module_test2")             public static ModuleItemTest                      module_test2;
//    @ObjectHolder("module_test3")             public static ModuleItemTest                      module_test3;
//    @ObjectHolder("module_test4")             public static ModuleItemTest                      module_test4;
//    @ObjectHolder("module_test5")             public static ModuleItemTest                      module_test5;
//
//    //@formatter:on
//
//
//    @SubscribeEvent
//    public static void registerItems(RegistryEvent.Register<Item> event) {
//        ItemGroup moduleGroup = new ItemGroup("draconic.modules") {
//            @Override
//            public ItemStack createIcon() {
//                return new ItemStack(Items.APPLE);
//            }
//        };
//
//
//
//        registerModule(event, new ModuleItemTest(new Item.Properties().group(moduleGroup), ModuleType.ENERGY_STORAGE, TechLevel.DRACONIUM), "module_test1");
//        registerModule(event, new ModuleItemTest(new Item.Properties().group(moduleGroup), ModuleType.AREA_OF_EFFECT, TechLevel.WYVERN), "module_test2");
//        registerModule(event, new ModuleItemTest(new Item.Properties().group(moduleGroup), ModuleType.ELYTRA_FLIGHT, TechLevel.DRACONIC), "module_test3");
//        registerModule(event, new ModuleItemTest(new Item.Properties().group(moduleGroup), ModuleType.LAST_STAND, TechLevel.CHAOTIC), "module_test4");
//        registerModule(event, new ModuleItemTest(new Item.Properties().group(moduleGroup), ModuleType.CREATIVE_FLIGHT, TechLevel.DRACONIUM), "module_test5");
//
//
//    }
//
//    private static <T extends Item & IModule<T>> void registerModule(RegistryEvent.Register<Item> event, T moduleItem, String registryName) {
//        moduleItem.setRegistryName(registryName);
//        event.getRegistry().register(moduleItem);
//
//
//        ModuleRegistry.INSTANCE.registerModule(moduleItem.getModuleType(), moduleItem);
//    }
//}