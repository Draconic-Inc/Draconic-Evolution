package com.brandon3055.draconicevolution.init;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.client.utils.CyclingItemGroup;
import com.brandon3055.draconicevolution.DraconicEvolution;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.properties.*;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.brandon3055.draconicevolution.items.modules.TestModuleHost;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;

import static com.brandon3055.draconicevolution.api.TechLevel.*;
import static com.brandon3055.draconicevolution.api.modules.ModuleTypes.*;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Created by brandon3055 on 18/4/20.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEModules {
    public static transient ArrayList<ResourceLocation> ITEM_REGISTRY_ORDER = new ArrayList<>();
    public static ForgeRegistry<Module<?>> MODULE_REGISTRY;



    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        MODULE_REGISTRY = SneakyUtils.unsafeCast(new RegistryBuilder<>()//
                .setName(new ResourceLocation(DraconicEvolution.MODID, "modules"))//
                .setType(SneakyUtils.unsafeCast(Module.class))//
                .disableSaving()//
                .create()//
        );
    }

    //@formatter:off
//    @ObjectHolder("test_module_host")       public static Item                          test_module_host;

    @ObjectHolder("test_module_1")          public static Item                          test_module_item_1;
    @ObjectHolder("test_module_2")          public static Item                          test_module_item_2;
    @ObjectHolder("test_module_3")          public static Item                          test_module_item_3;
    @ObjectHolder("test_module_4")          public static Item                          test_module_item_4;
    @ObjectHolder("test_module_5")          public static Item                          test_module_item_5;
    @ObjectHolder("test_module_6")          public static Item                          test_module_item_6;
    @ObjectHolder("test_module_7")          public static Item                          test_module_item_7;
    @ObjectHolder("test_module_8")          public static Item                          test_module_item_8;
    @ObjectHolder("test_module_9")          public static Item                          test_module_item_9;
    //@formatter:on

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        //TODO Some sort of 'generate' method which creates all of the modules in a slightly more sane way when just pipes the appropriate junk into the register events.

        CyclingItemGroup moduleGroup = new CyclingItemGroup("draconic.modules", 20, () -> new Object[]{Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE}, ITEM_REGISTRY_ORDER);

        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 5, 5).setRegistryName("test_module_host_5x5"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 10, 10).setRegistryName("test_module_host_10x10"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 15, 15).setRegistryName("test_module_host_15x15"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 20, 20).setRegistryName("test_module_host_20x20"));

        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 10, 5).setRegistryName("test_module_host_10x5"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 15, 5).setRegistryName("test_module_host_15x5"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 20, 5).setRegistryName("test_module_host_20x5"));
        registerItem(event, new TestModuleHost(new Properties().group(moduleGroup).maxStackSize(1), 20, 10).setRegistryName("test_module_host_20x10"));

        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_1).setRegistryName("test_module_1"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_2).setRegistryName("test_module_2"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_3).setRegistryName("test_module_3"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_4).setRegistryName("test_module_4"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_5).setRegistryName("test_module_5"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_6).setRegistryName("test_module_6"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_7).setRegistryName("test_module_7"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_8).setRegistryName("test_module_8"));
        registerItem(event, new ModuleItem<>(new Properties().group(moduleGroup), () -> test_module_9).setRegistryName("test_module_9"));
    }

    //@formatter:off
    @ObjectHolder("test_module_1")        public static ModuleImpl<?>          test_module_1;
    @ObjectHolder("test_module_2")        public static ModuleImpl<?>          test_module_2;
    @ObjectHolder("test_module_3")        public static ModuleImpl<?>          test_module_3;
    @ObjectHolder("test_module_4")        public static ModuleImpl<?>          test_module_4;
    @ObjectHolder("test_module_5")        public static ModuleImpl<?>          test_module_5;
    @ObjectHolder("test_module_6")        public static ModuleImpl<?>          test_module_6;
    @ObjectHolder("test_module_7")        public static ModuleImpl<?>          test_module_7;
    @ObjectHolder("test_module_8")        public static ModuleImpl<?>          test_module_8;
    @ObjectHolder("test_module_9")        public static ModuleImpl<?>          test_module_9;
    //@formatter:on

    @SubscribeEvent
    public static void registerModules(RegistryEvent.Register<Module<?>> event) {
        //@formatter:off
        event.getRegistry().register(new ModuleImpl<>(ENERGY_STORAGE,   DRACONIUM,  new EnergyData(100000L),            test_module_item_1).setRegistryName("test_module_1"));
        event.getRegistry().register(new ModuleImpl<>(SHIELD,           WYVERN,     new ShieldData(1000, 0),    2, 1,   test_module_item_2).setRegistryName("test_module_2"));
        event.getRegistry().register(new ModuleImpl<>(AREA_OF_EFFECT,   WYVERN,     new AOEData(2),             3, 1,   test_module_item_3).setRegistryName("test_module_3"));
        event.getRegistry().register(new ModuleImpl<>(DAMAGE,           WYVERN,     new DamageData(4),          1, 3,   test_module_item_4).setRegistryName("test_module_4"));
        event.getRegistry().register(new ModuleImpl<>(SPEED,            WYVERN,     new SpeedData(5, 5),                test_module_item_5).setRegistryName("test_module_5"));
        event.getRegistry().register(new ModuleImpl<>(DAMAGE,           DRACONIUM,  new DamageData(4),          1, 3,   test_module_item_6).setRegistryName("test_module_6"));
        event.getRegistry().register(new ModuleImpl<>(DAMAGE,           WYVERN,     new DamageData(4),          1, 3,   test_module_item_7).setRegistryName("test_module_7"));
        event.getRegistry().register(new ModuleImpl<>(DAMAGE,           DRACONIC,   new DamageData(4),          1, 3,   test_module_item_8).setRegistryName("test_module_8"));
        event.getRegistry().register(new ModuleImpl<>(DAMAGE,           CHAOTIC,    new DamageData(4),          1, 3,   test_module_item_9).setRegistryName("test_module_9"));
        //@formatter:on
    }


//    Maybe add some more module types just to get them oout of the way.
//    Then work on ModuleHost and ModuleGrid ModuleHost should probably be a basic shell that ModuleGrid can load and save data to.


    private static void registerItem(RegistryEvent.Register<Item> event, Item item) {
        event.getRegistry().register(item);
        ITEM_REGISTRY_ORDER.add(item.getRegistryName());
    }
}