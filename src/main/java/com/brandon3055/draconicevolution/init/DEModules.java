package com.brandon3055.draconicevolution.init;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.client.utils.CyclingItemGroup;
import com.brandon3055.draconicevolution.DraconicEvolution;

import com.brandon3055.draconicevolution.api.modules.data.*;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.brandon3055.draconicevolution.api.modules.lib.EnergyModuleItem;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.brandon3055.draconicevolution.items.modules.TestModuleHost;
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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.api.modules.ModuleTypes.*;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Created by brandon3055 on 18/4/20.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEModules {
    private static transient ArrayList<ResourceLocation> ITEM_REGISTRY_ORDER = new ArrayList<>();
    private static transient CyclingItemGroup moduleGroup = new CyclingItemGroup("draconicevolution.modules", 20, () -> new Object[]{Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE}, ITEM_REGISTRY_ORDER);
    public static transient Map<BaseModule<?>, Item> moduleItemMap = new LinkedHashMap<>();
    public static ForgeRegistry<Module<?>> MODULE_REGISTRY;

    //@formatter:off
    @ObjectHolder("draconium_energy")       public static Module<EnergyData> draconiumEnergy;
    @ObjectHolder("wyvern_energy")          public static Module<EnergyData> wyvernEnergy;
    @ObjectHolder("draconic_energy")        public static Module<EnergyData> draconicEnergy;
    @ObjectHolder("chaotic_energy")         public static Module<EnergyData> chaoticEnergy;

    //@formatter:on


    private static void registerModules() {
        Properties props = new Properties().group(moduleGroup);
        //@formatter:off

        //Energy
        register(new ModuleImpl<>(ENERGY_STORAGE,   DRACONIUM,    energyData(1000000,  16000)),   new EnergyModuleItem(props), "draconium_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,   WYVERN,       energyData(4000000,  64000)),   new EnergyModuleItem(props), "wyvern_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,   DRACONIC,     energyData(16000000, 256000)),  new EnergyModuleItem(props), "draconic_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,   CHAOTIC,      energyData(64000000, 1024000)), new EnergyModuleItem(props), "chaotic_energy");

        //Tools
        register(new ModuleImpl<>(SPEED,            DRACONIUM,    speedData(0.10)),         "draconium_speed");
        register(new ModuleImpl<>(SPEED,            WYVERN,       speedData(0.25)),         "wyvern_speed");
        register(new ModuleImpl<>(SPEED,            DRACONIC,     speedData(1.00)),         "draconic_speed");
        register(new ModuleImpl<>(SPEED,            CHAOTIC,      speedData(2.00)),         "chaotic_speed");

        register(new ModuleImpl<>(DAMAGE,           DRACONIUM,    damageData(2)),           "draconium_damage");
        register(new ModuleImpl<>(DAMAGE,           WYVERN,       damageData(4)),           "wyvern_damage");
        register(new ModuleImpl<>(DAMAGE,           DRACONIC,     damageData(8)),           "draconic_damage");
        register(new ModuleImpl<>(DAMAGE,           CHAOTIC,      damageData(16)),          "chaotic_damage");

        register(new ModuleImpl<>(AOE,              DRACONIUM,    aoeData(1)),              "draconium_aoe");
        register(new ModuleImpl<>(AOE,              WYVERN,       aoeData(2)),              "wyvern_aoe");
        register(new ModuleImpl<>(AOE,              DRACONIC,     aoeData(3)),              "draconic_aoe");
        register(new ModuleImpl<>(AOE,              CHAOTIC,      aoeData(5)),              "chaotic_aoe");

        //Armor
        register(new ModuleImpl<>(JUMP_BOOST,       DRACONIUM,    jumpData(0.25)),          "draconium_jump");
        register(new ModuleImpl<>(JUMP_BOOST,       WYVERN,       jumpData(1.00)),          "wyvern_jump");
        register(new ModuleImpl<>(JUMP_BOOST,       DRACONIC,     jumpData(4.00)),          "draconic_jump");
        register(new ModuleImpl<>(JUMP_BOOST,       CHAOTIC,      jumpData(8.00)),          "chaotic_jump");




//        register(new ModuleImpl<>(SHIELD_BOOST, WYVERN, shieldData(1000, 0), 2, 1), "shield_module");








        //@formatter:on
    }


    //region Data Helpers

    private static Function<Module<EnergyData>, EnergyData> energyData(long defCapacity, long defTransfer) {
        return e -> {
            long capacity = ModuleCfg.getModuleLong(e, "capacity", defCapacity);
            long transfer = ModuleCfg.getModuleLong(e, "transfer", defTransfer);
            return new EnergyData(capacity, transfer);
        };
    }

    private static Function<Module<ShieldData>, ShieldData> shieldData(int defCapacity, int defRecharge) {
        return e -> {
            int capacity = ModuleCfg.getModuleInt(e, "capacity", defCapacity);
            int transfer = ModuleCfg.getModuleInt(e, "recharge", defRecharge);
            return new ShieldData(capacity, transfer);
        };
    }

    private static Function<Module<SpeedData>, SpeedData> speedData(double defMultiplier) {
        return e -> new SpeedData(ModuleCfg.getModuleDouble(e, "speed_boost", defMultiplier));
    }

    private static Function<Module<DamageData>, DamageData> damageData(double defDamage) {
        return e -> new DamageData(ModuleCfg.getModuleDouble(e, "damage_boost", defDamage));
    }

    private static Function<Module<AOEData>, AOEData> aoeData(int defAOE) {
        return e -> new AOEData(ModuleCfg.getModuleInt(e, "aoe", defAOE));
    }

    private static Function<Module<JumpData>, JumpData> jumpData(double defMultiplier) {
        return e -> new JumpData(ModuleCfg.getModuleDouble(e, "jump_boost", defMultiplier));
    }

    //endregion

    //region Registry

    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        MODULE_REGISTRY = SneakyUtils.unsafeCast(new RegistryBuilder<>()//
                .setName(new ResourceLocation(DraconicEvolution.MODID, "modules"))//
                .setType(SneakyUtils.unsafeCast(Module.class))//
                .disableSaving()//
                .create()//
        );
    }

    private static void register(ModuleImpl<?> module, String name) {
        ModuleItem<?> item = new ModuleItem<>(new Properties().group(moduleGroup), module);
        item.setRegistryName(name + "_module");
        module.setRegistryName(name);
        module.setModuleItem(item);
        moduleItemMap.put(module, item);
    }

    private static <T extends ModuleData<T>> void register(ModuleImpl<T> module, ModuleItem<T> item, String name) {
        item.setRegistryName(name + "_module");
        item.setModule(module);
        module.setRegistryName(name);
        module.setModuleItem(item);
        moduleItemMap.put(module, item);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        moduleItemMap.clear();
        registerModules();
        moduleItemMap.keySet().forEach(BaseModule::reloadData);
        moduleItemMap.values().forEach(e -> event.getRegistry().register(e));
        ModuleCfg.saveStateConfig();
    }

    @SubscribeEvent
    public static void registerModules(RegistryEvent.Register<Module<?>> event) {
        moduleItemMap.keySet().forEach(e -> event.getRegistry().register(e));
    }

    //endregion
}