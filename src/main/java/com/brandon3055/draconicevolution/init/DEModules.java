package com.brandon3055.draconicevolution.init;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.*;
import com.brandon3055.draconicevolution.api.modules.items.EnderCollectionModuleItem;
import com.brandon3055.draconicevolution.api.modules.items.EnergyLinkModuleItem;
import com.brandon3055.draconicevolution.api.modules.items.EnergyModuleItem;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.brandon3055.draconicevolution.modules.ProjectileVelocityModule;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static com.brandon3055.draconicevolution.api.modules.ModuleTypes.*;

/**
 * Created by brandon3055 on 18/4/20.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
public class DEModules {
    public static final ResourceKey<Registry<Module<?>>> MODULE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "modules"));
    public static Registry<Module<?>> REGISTRY;

    public static final DeferredRegister<Module<?>> MODULES = DeferredRegister.create(MODULE_KEY, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    public static final Set<String> MODULE_PROVIDING_MODS = new HashSet<>();

    public static void init(IEventBus eventBus) {
        eventBus.addListener(DEModules::createRegistries);
        MODULES.register(eventBus);
        ITEMS.register(eventBus);
        eventBus.addListener(DEModules::registerEvent);
    }

    public static void createRegistries(NewRegistryEvent event) {
        REGISTRY = event.create(new RegistryBuilder<>(MODULE_KEY)
                .sync(true)
        );
    }

    public static void registerEvent(FMLCommonSetupEvent event) {
        REGISTRY.forEach(module -> ((BaseModule<?>)module).reloadData());
    }

    //@formatter:off

    //Energy
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_ENERGY                           = MODULES.register("draconium_energy",                      () -> new ModuleImpl<>(ENERGY_STORAGE,                  DRACONIUM,              energyData(1000000,  16000)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_ENERGY                              = MODULES.register("wyvern_energy",                         () -> new ModuleImpl<>(ENERGY_STORAGE,                  WYVERN,                 energyData(4000000,  64000)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_ENERGY                            = MODULES.register("draconic_energy",                       () -> new ModuleImpl<>(ENERGY_STORAGE,                  DRACONIC,               energyData(16000000, 256000)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_ENERGY                             = MODULES.register("chaotic_energy",                        () -> new ModuleImpl<>(ENERGY_STORAGE,                  CHAOTIC,                energyData(64000000, 1024000)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_ENERGY                       = ITEMS.register("item_draconium_energy",                   () -> new EnergyModuleItem(DRACONIUM_ENERGY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_ENERGY                          = ITEMS.register("item_wyvern_energy",                      () -> new EnergyModuleItem(WYVERN_ENERGY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_ENERGY                        = ITEMS.register("item_draconic_energy",                    () -> new EnergyModuleItem(DRACONIC_ENERGY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_ENERGY                         = ITEMS.register("item_chaotic_energy",                     () -> new EnergyModuleItem(CHAOTIC_ENERGY));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_ENERGY_LINK                         = MODULES.register("wyvern_energy_link",                    () -> new ModuleImpl<>(ENERGY_LINK,                     WYVERN,                 energyLinkData(4000000,  512,  2048, false)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_ENERGY_LINK                       = MODULES.register("draconic_energy_link",                  () -> new ModuleImpl<>(ENERGY_LINK,                     DRACONIC,               energyLinkData(16000000, 2048, 16000, true)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_ENERGY_LINK                        = MODULES.register("chaotic_energy_link",                   () -> new ModuleImpl<>(ENERGY_LINK,                     CHAOTIC,                energyLinkData(64000000, 8192, 128000, true)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_ENERGY_LINK                     = ITEMS.register("item_wyvern_energy_link",                 () -> new EnergyLinkModuleItem(WYVERN_ENERGY_LINK));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_ENERGY_LINK                   = ITEMS.register("item_draconic_energy_link",               () -> new EnergyLinkModuleItem(DRACONIC_ENERGY_LINK));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_ENERGY_LINK                    = ITEMS.register("item_chaotic_energy_link",                () -> new EnergyLinkModuleItem(CHAOTIC_ENERGY_LINK));

    //Tools
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_SPEED                            = MODULES.register("draconium_speed",                       () -> new ModuleImpl<>(SPEED,                           DRACONIUM,              speedData(0.10)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_SPEED                               = MODULES.register("wyvern_speed",                          () -> new ModuleImpl<>(SPEED,                           WYVERN,                 speedData(0.25)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_SPEED                             = MODULES.register("draconic_speed",                        () -> new ModuleImpl<>(SPEED,                           DRACONIC,               speedData(0.50)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_SPEED                              = MODULES.register("chaotic_speed",                         () -> new ModuleImpl<>(SPEED,                           CHAOTIC,                speedData(1.50)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_SPEED                        = ITEMS.register("item_draconium_speed",                    () -> new ModuleItem<>(DRACONIUM_SPEED));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_SPEED                           = ITEMS.register("item_wyvern_speed",                       () -> new ModuleItem<>(WYVERN_SPEED));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_SPEED                         = ITEMS.register("item_draconic_speed",                     () -> new ModuleItem<>(DRACONIC_SPEED));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_SPEED                          = ITEMS.register("item_chaotic_speed",                      () -> new ModuleItem<>(CHAOTIC_SPEED));

    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_DAMAGE                           = MODULES.register("draconium_damage",                      () -> new ModuleImpl<>(DAMAGE,                          DRACONIUM,              damageData(2)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_DAMAGE                              = MODULES.register("wyvern_damage",                         () -> new ModuleImpl<>(DAMAGE,                          WYVERN,                 damageData(4)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_DAMAGE                            = MODULES.register("draconic_damage",                       () -> new ModuleImpl<>(DAMAGE,                          DRACONIC,               damageData(8)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_DAMAGE                             = MODULES.register("chaotic_damage",                        () -> new ModuleImpl<>(DAMAGE,                          CHAOTIC,                damageData(16)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_DAMAGE                       = ITEMS.register("item_draconium_damage",                   () -> new ModuleItem<>(DRACONIUM_DAMAGE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_DAMAGE                          = ITEMS.register("item_wyvern_damage",                      () -> new ModuleItem<>(WYVERN_DAMAGE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_DAMAGE                        = ITEMS.register("item_draconic_damage",                    () -> new ModuleItem<>(DRACONIC_DAMAGE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_DAMAGE                         = ITEMS.register("item_chaotic_damage",                     () -> new ModuleItem<>(CHAOTIC_DAMAGE));

    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_AOE                              = MODULES.register("draconium_aoe",                         () -> new ModuleImpl<>(AOE,                             DRACONIUM,              aoeData(1)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_AOE                                 = MODULES.register("wyvern_aoe",                            () -> new ModuleImpl<>(AOE,                             WYVERN,                 aoeData(2)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_AOE                               = MODULES.register("draconic_aoe",                          () -> new ModuleImpl<>(AOE,                             DRACONIC,               aoeData(3)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_AOE                                = MODULES.register("chaotic_aoe",                           () -> new ModuleImpl<>(AOE,                             CHAOTIC,                aoeData(5)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_AOE                          = ITEMS.register("item_draconium_aoe",                      () -> new ModuleItem<>(DRACONIUM_AOE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_AOE                             = ITEMS.register("item_wyvern_aoe",                         () -> new ModuleItem<>(WYVERN_AOE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_AOE                           = ITEMS.register("item_draconic_aoe",                       () -> new ModuleItem<>(DRACONIC_AOE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_AOE                            = ITEMS.register("item_chaotic_aoe",                        () -> new ModuleItem<>(CHAOTIC_AOE));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_MINING_STABILITY                    = MODULES.register("wyvern_mining_stability",               () -> new ModuleImpl<>(MINING_STABILITY,                WYVERN,                 noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_MINING_STABILITY                = ITEMS.register("item_wyvern_mining_stability",            () -> new ModuleItem<>(WYVERN_MINING_STABILITY));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_TREE_HARVEST                        = MODULES.register("wyvern_tree_harvest",                   () -> new ModuleImpl<>(TREE_HARVEST,                    WYVERN,                 treeHarvestData(16, 5)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_TREE_HARVEST                      = MODULES.register("draconic_tree_harvest",                 () -> new ModuleImpl<>(TREE_HARVEST,                    DRACONIC,               treeHarvestData(48, 15)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_TREE_HARVEST                       = MODULES.register("chaotic_tree_harvest",                  () -> new ModuleImpl<>(TREE_HARVEST,                    CHAOTIC,                treeHarvestData(144, 45)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_TREE_HARVEST                    = ITEMS.register("item_wyvern_tree_harvest",                () -> new ModuleItem<>(WYVERN_TREE_HARVEST));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_TREE_HARVEST                  = ITEMS.register("item_draconic_tree_harvest",              () -> new ModuleItem<>(DRACONIC_TREE_HARVEST));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_TREE_HARVEST                   = ITEMS.register("item_chaotic_tree_harvest",               () -> new ModuleItem<>(CHAOTIC_TREE_HARVEST));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_JUNK_FILTER                         = MODULES.register("wyvern_junk_filter",                    () -> new ModuleImpl<>(JUNK_FILTER,                     WYVERN,                 noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_JUNK_FILTER                     = ITEMS.register("item_wyvern_junk_filter",                 () -> new ModuleItem<>(WYVERN_JUNK_FILTER));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_ENDER_COLLECTION                    = MODULES.register("wyvern_ender_collection",               () -> new ModuleImpl<>(ENDER_COLLECTION,                WYVERN,                 noData()));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_ENDER_COLLECTION                  = MODULES.register("draconic_ender_collection",             () -> new ModuleImpl<>(ENDER_COLLECTION,                DRACONIC,               noData(), 2, 2));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_ENDER_COLLECTION                = ITEMS.register("item_wyvern_ender_collection",            () -> new EnderCollectionModuleItem(WYVERN_ENDER_COLLECTION));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_ENDER_COLLECTION              = ITEMS.register("item_draconic_ender_collection",          () -> new EnderCollectionModuleItem(DRACONIC_ENDER_COLLECTION));

    //Arrow base velocity is 60 m/s
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_PROJ_VELOCITY                       = MODULES.register("wyvern_proj_velocity",                  () -> new ProjectileVelocityModule(PROJ_MODIFIER,       WYVERN,                 projVelocityData(0.15F, 1.0F)).setMaxInstall(8));   // (1 + (0.15 * 8) * 60) = 132 m/s max
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_VELOCITY                     = MODULES.register("draconic_proj_velocity",                () -> new ProjectileVelocityModule(PROJ_MODIFIER,       DRACONIC,               projVelocityData(0.35F, 0.5F)).setMaxInstall(8)); // (1 + (0.35 * 8) * 60) = 228 m/s max
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_PROJ_VELOCITY                      = MODULES.register("chaotic_proj_velocity",                 () -> new ProjectileVelocityModule(PROJ_MODIFIER,       CHAOTIC,                projVelocityData(0.75F, 0.0F)).setMaxInstall(8));  // (1 + (0.75 * 8) * 60) = 420 m/s max
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_PROJ_VELOCITY                   = ITEMS.register("item_wyvern_proj_velocity",               () -> new ModuleItem<>(WYVERN_PROJ_VELOCITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_VELOCITY                 = ITEMS.register("item_draconic_proj_velocity",             () -> new ModuleItem<>(DRACONIC_PROJ_VELOCITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_PROJ_VELOCITY                  = ITEMS.register("item_chaotic_proj_velocity",              () -> new ModuleItem<>(CHAOTIC_PROJ_VELOCITY));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_PROJ_ACCURACY                       = MODULES.register("wyvern_proj_accuracy",                  () -> new ModuleImpl<>(PROJ_MODIFIER,                   WYVERN,                 projAccuracyData(0.125F, 2.0F), 2, 1));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_ACCURACY                     = MODULES.register("draconic_proj_accuracy",                () -> new ModuleImpl<>(PROJ_MODIFIER,                   DRACONIC,               projAccuracyData(0.20F, 1.0F), 2, 1));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_PROJ_ACCURACY                      = MODULES.register("chaotic_proj_accuracy",                 () -> new ModuleImpl<>(PROJ_MODIFIER,                   CHAOTIC,                projAccuracyData(0.25F, 0.0F), 2, 1));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_PROJ_ACCURACY                   = ITEMS.register("item_wyvern_proj_accuracy",               () -> new ModuleItem<>(WYVERN_PROJ_ACCURACY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_ACCURACY                 = ITEMS.register("item_draconic_proj_accuracy",             () -> new ModuleItem<>(DRACONIC_PROJ_ACCURACY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_PROJ_ACCURACY                  = ITEMS.register("item_chaotic_proj_accuracy",              () -> new ModuleItem<>(CHAOTIC_PROJ_ACCURACY));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_PROJ_GRAV_COMP                      = MODULES.register("wyvern_proj_grav_comp",                 () -> new ModuleImpl<>(PROJ_MODIFIER,                   WYVERN,                 projAntiGravData(0.20F, 2.0F), 2, 1));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_GRAV_COMP                    = MODULES.register("draconic_proj_grav_comp",               () -> new ModuleImpl<>(PROJ_MODIFIER,                   DRACONIC,               projAntiGravData(0.25F, 1.0F), 2, 1));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_PROJ_GRAV_COMP                     = MODULES.register("chaotic_proj_grav_comp",                () -> new ModuleImpl<>(PROJ_MODIFIER,                   CHAOTIC,                projAntiGravData(0.50F, 0.0F), 2, 1));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_PROJ_GRAV_COMP                  = ITEMS.register("item_wyvern_proj_grav_comp",              () -> new ModuleItem<>(WYVERN_PROJ_GRAV_COMP));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_GRAV_COMP                = ITEMS.register("item_draconic_proj_grav_comp",            () -> new ModuleItem<>(DRACONIC_PROJ_GRAV_COMP));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_PROJ_GRAV_COMP                 = ITEMS.register("item_chaotic_proj_grav_comp",             () -> new ModuleItem<>(CHAOTIC_PROJ_GRAV_COMP));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_PROJ_PENETRATION                    = MODULES.register("wyvern_proj_penetration",               () -> new ModuleImpl<>(PROJ_MODIFIER,                   WYVERN,                 projPenetrationData(0.25F, 2.0F), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_PENETRATION                  = MODULES.register("draconic_proj_penetration",             () -> new ModuleImpl<>(PROJ_MODIFIER,                   DRACONIC,               projPenetrationData(0.50F, 1.0F), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_PROJ_PENETRATION                   = MODULES.register("chaotic_proj_penetration",              () -> new ModuleImpl<>(PROJ_MODIFIER,                   CHAOTIC,                projPenetrationData(0.75F, 0.0F), 2, 2));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_PROJ_PENETRATION                = ITEMS.register("item_wyvern_proj_penetration",            () -> new ModuleItem<>(WYVERN_PROJ_PENETRATION));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_PENETRATION              = ITEMS.register("item_draconic_proj_penetration",          () -> new ModuleItem<>(DRACONIC_PROJ_PENETRATION));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_PROJ_PENETRATION               = ITEMS.register("item_chaotic_proj_penetration",           () -> new ModuleItem<>(CHAOTIC_PROJ_PENETRATION));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_PROJ_DAMAGE                         = MODULES.register("wyvern_proj_damage",                    () -> new ModuleImpl<>(PROJ_MODIFIER,                   WYVERN,                 projDamageData(0.25F, 2.0F)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_DAMAGE                       = MODULES.register("draconic_proj_damage",                  () -> new ModuleImpl<>(PROJ_MODIFIER,                   DRACONIC,               projDamageData(0.50F, 1.0F)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_PROJ_DAMAGE                        = MODULES.register("chaotic_proj_damage",                   () -> new ModuleImpl<>(PROJ_MODIFIER,                   CHAOTIC,                projDamageData(0.75F, 0.0F)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_PROJ_DAMAGE                     = ITEMS.register("item_wyvern_proj_damage",                 () -> new ModuleItem<>(WYVERN_PROJ_DAMAGE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_DAMAGE                   = ITEMS.register("item_draconic_proj_damage",               () -> new ModuleItem<>(DRACONIC_PROJ_DAMAGE));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_PROJ_DAMAGE                    = ITEMS.register("item_chaotic_proj_damage",                () -> new ModuleItem<>(CHAOTIC_PROJ_DAMAGE));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_AUTO_FIRE                           = MODULES.register("wyvern_auto_fire",                      () -> new ModuleImpl<>(AUTO_FIRE,                       WYVERN,                 noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_AUTO_FIRE                       = ITEMS.register("item_wyvern_auto_fire",                   () -> new ModuleItem<>(WYVERN_AUTO_FIRE));

    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_PROJ_ANTI_IMMUNE                  = MODULES.register("draconic_proj_anti_immune",             () -> new ModuleImpl<>(PROJ_ANTI_IMMUNE,                DRACONIC,               noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_PROJ_ANTI_IMMUNE              = ITEMS.register("item_draconic_proj_anti_immune",          () -> new ModuleItem<>(DRACONIC_PROJ_ANTI_IMMUNE));

//    public static final RegistryObject<Module<?>> DRACONIC_FIRE_MOD                         = MODULES.register("draconic_fire_mod",                     () -> new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new FireDmgMod()))); //TODO
//    public static final RegistryObject<Module<?>> CHAOTIC_FIRE_MOD                          = MODULES.register("chaotic_fire_mod",                      () -> new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new FireDmgMod()))); //TODO
//    public static final RegistryObject<ModuleItem<?>> ITEM_DRACONIC_FIRE_MOD                = ITEMS.register("item_draconic_fire_mod",                  () -> new ModuleItem<>(DRACONIC_FIRE_MOD));
//    public static final RegistryObject<ModuleItem<?>> ITEM_CHAOTIC_FIRE_MOD                 = ITEMS.register("item_chaotic_fire_mod",                   () -> new ModuleItem<>(CHAOTIC_FIRE_MOD));

//    public static final RegistryObject<Module<?>> DRACONIC_LIGHTNING_MOD                    = MODULES.register("draconic_lightning_mod",                () -> new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new LightningDmgMod()))); //TODO
//    public static final RegistryObject<Module<?>> CHAOTIC_LIGHTNING_MOD                     = MODULES.register("chaotic_lightning_mod",                 () -> new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new LightningDmgMod()))); //TODO
//    public static final RegistryObject<ModuleItem<?>> ITEM_DRACONIC_LIGHTNING_MOD           = ITEMS.register("item_draconic_lightning_mod",             () -> new ModuleItem<>(DRACONIC_LIGHTNING_MOD));
//    public static final RegistryObject<ModuleItem<?>> ITEM_CHAOTIC_LIGHTNING_MOD            = ITEMS.register("item_chaotic_lightning_mod",              () -> new ModuleItem<>(CHAOTIC_LIGHTNING_MOD));

//    public static final RegistryObject<Module<?>> DRACONIC_ICE_MOD                          = MODULES.register("draconic_ice_mod",                      () -> new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new IceDmgMod()))); //TODO
//    public static final RegistryObject<Module<?>> CHAOTIC_ICE_MOD                           = MODULES.register("chaotic_ice_mod",                       () -> new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new IceDmgMod()))); //TODO
//    public static final RegistryObject<ModuleItem<?>> ITEM_DRACONIC_ICE_MOD                 = ITEMS.register("item_draconic_ice_mod",                   () -> new ModuleItem<>(DRACONIC_ICE_MOD));
//    public static final RegistryObject<ModuleItem<?>> ITEM_CHAOTIC_ICE_MOD                  = ITEMS.register("item_chaotic_ice_mod",                    () -> new ModuleItem<>(CHAOTIC_ICE_MOD));

    //Armor
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_SHIELD_CONTROL                      = MODULES.register("wyvern_shield_control",                 () -> new ModuleImpl<>(SHIELD_CONTROLLER,               WYVERN,                 shieldControl(20.0)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_SHIELD_CONTROL                    = MODULES.register("draconic_shield_control",               () -> new ModuleImpl<>(SHIELD_CONTROLLER,               DRACONIC,               shieldControl(10.0)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_SHIELD_CONTROL                     = MODULES.register("chaotic_shield_control",                () -> new ModuleImpl<>(SHIELD_CONTROLLER,               CHAOTIC,                shieldControl(5.0)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_SHIELD_CONTROL                  = ITEMS.register("item_wyvern_shield_control",              () -> new ModuleItem<>(WYVERN_SHIELD_CONTROL));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_SHIELD_CONTROL                = ITEMS.register("item_draconic_shield_control",            () -> new ModuleItem<>(DRACONIC_SHIELD_CONTROL));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_SHIELD_CONTROL                 = ITEMS.register("item_chaotic_shield_control",             () -> new ModuleItem<>(CHAOTIC_SHIELD_CONTROL));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_SHIELD_CAPACITY                     = MODULES.register("wyvern_shield_capacity",                () -> new ModuleImpl<>(SHIELD_BOOST,                    WYVERN,                 shieldData(25,  0.1)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_SHIELD_CAPACITY                   = MODULES.register("draconic_shield_capacity",              () -> new ModuleImpl<>(SHIELD_BOOST,                    DRACONIC,               shieldData(50,  0.25)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_SHIELD_CAPACITY                    = MODULES.register("chaotic_shield_capacity",               () -> new ModuleImpl<>(SHIELD_BOOST,                    CHAOTIC,                shieldData(100, 0.5)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_LARGE_SHIELD_CAPACITY               = MODULES.register("wyvern_large_shield_capacity",          () -> new ModuleImpl<>(SHIELD_BOOST,                    WYVERN,                 shieldData(25*5,  0.0D), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_LARGE_SHIELD_CAPACITY             = MODULES.register("draconic_large_shield_capacity",        () -> new ModuleImpl<>(SHIELD_BOOST,                    DRACONIC,               shieldData(50*5,  0.0D), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_LARGE_SHIELD_CAPACITY              = MODULES.register("chaotic_large_shield_capacity",         () -> new ModuleImpl<>(SHIELD_BOOST,                    CHAOTIC,                shieldData(100*5, 0.0D), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_SHIELD_RECOVERY                     = MODULES.register("wyvern_shield_recovery",                () -> new ModuleImpl<>(SHIELD_BOOST,                    WYVERN,                 shieldData(5,   1.0)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_SHIELD_RECOVERY                   = MODULES.register("draconic_shield_recovery",              () -> new ModuleImpl<>(SHIELD_BOOST,                    DRACONIC,               shieldData(10,  2.5)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_SHIELD_RECOVERY                    = MODULES.register("chaotic_shield_recovery",               () -> new ModuleImpl<>(SHIELD_BOOST,                    CHAOTIC,                shieldData(20,  5.0)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_SHIELD_CAPACITY                 = ITEMS.register("item_wyvern_shield_capacity",             () -> new ModuleItem<>(WYVERN_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_SHIELD_CAPACITY               = ITEMS.register("item_draconic_shield_capacity",           () -> new ModuleItem<>(DRACONIC_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_SHIELD_CAPACITY                = ITEMS.register("item_chaotic_shield_capacity",            () -> new ModuleItem<>(CHAOTIC_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_LARGE_SHIELD_CAPACITY           = ITEMS.register("item_wyvern_large_shield_capacity",       () -> new ModuleItem<>(WYVERN_LARGE_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_LARGE_SHIELD_CAPACITY         = ITEMS.register("item_draconic_large_shield_capacity",     () -> new ModuleItem<>(DRACONIC_LARGE_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_LARGE_SHIELD_CAPACITY          = ITEMS.register("item_chaotic_large_shield_capacity",      () -> new ModuleItem<>(CHAOTIC_LARGE_SHIELD_CAPACITY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_SHIELD_RECOVERY                 = ITEMS.register("item_wyvern_shield_recovery",             () -> new ModuleItem<>(WYVERN_SHIELD_RECOVERY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_SHIELD_RECOVERY               = ITEMS.register("item_draconic_shield_recovery",           () -> new ModuleItem<>(DRACONIC_SHIELD_RECOVERY));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_SHIELD_RECOVERY                = ITEMS.register("item_chaotic_shield_recovery",            () -> new ModuleItem<>(CHAOTIC_SHIELD_RECOVERY));

//    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_CLOAKING                           = MODULES.register("wyvern_cloaking",                       () -> new ModuleImpl<>(CLOAKING,             WYVERN,         noData())); //TODO
//    public static final DeferredHolder<Module<?>, ModuleItem<?>> ITEM_WYVERN_CLOAKING                  = ITEMS.register("item_wyvern_cloaking",                    () -> new ModuleItem<>(WYVERN_CLOAKING));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_FLIGHT                              = MODULES.register("wyvern_flight",                         () -> new ModuleImpl<>(FLIGHT,                          WYVERN,                 flightData(true, false, 1), 2, 2));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_FLIGHT                            = MODULES.register("draconic_flight",                       () -> new ModuleImpl<>(FLIGHT,                          DRACONIC,               flightData(true, true, 2)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_FLIGHT                             = MODULES.register("chaotic_flight",                        () -> new ModuleImpl<>(FLIGHT,                          CHAOTIC,                flightData(true, true, 3.5)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_FLIGHT                          = ITEMS.register("item_wyvern_flight",                      () -> new ModuleItem<>(WYVERN_FLIGHT));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_FLIGHT                        = ITEMS.register("item_draconic_flight",                    () -> new ModuleItem<>(DRACONIC_FLIGHT));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_FLIGHT                         = ITEMS.register("item_chaotic_flight",                     () -> new ModuleItem<>(CHAOTIC_FLIGHT));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_UNDYING                             = MODULES.register("wyvern_undying",                        () -> new ModuleImpl<>(UNDYING,                         WYVERN,                 undyingData(6F,  25F, 15*20,  4*30*20, 5000000, 2)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_UNDYING                           = MODULES.register("draconic_undying",                      () -> new ModuleImpl<>(UNDYING,                         DRACONIC,               undyingData(12F, 50F, 30*20,  2*30*20, 10000000, 3)).setMaxInstall(2));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_UNDYING                            = MODULES.register("chaotic_undying",                       () -> new ModuleImpl<>(UNDYING,                         CHAOTIC,                undyingData(20F, 100F,120*20, 45*20, 20000000, 3)).setMaxInstall(3));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_UNDYING                         = ITEMS.register("item_wyvern_undying",                     () -> new ModuleItem<>(WYVERN_UNDYING));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_UNDYING                       = ITEMS.register("item_draconic_undying",                   () -> new ModuleItem<>(DRACONIC_UNDYING));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_UNDYING                        = ITEMS.register("item_chaotic_undying",                    () -> new ModuleItem<>(CHAOTIC_UNDYING));

    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_AUTO_FEED                        = MODULES.register("draconium_auto_feed",                   () -> new ModuleImpl<>(AUTO_FEED,                       DRACONIUM,              autoFeedData(40)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_AUTO_FEED                           = MODULES.register("wyvern_auto_feed",                      () -> new ModuleImpl<>(AUTO_FEED,                       WYVERN,                 autoFeedData(150)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_AUTO_FEED                         = MODULES.register("draconic_auto_feed",                    () -> new ModuleImpl<>(AUTO_FEED,                       DRACONIC,               autoFeedData(400)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_AUTO_FEED                    = ITEMS.register("item_draconium_auto_feed",                () -> new ModuleItem<>(DRACONIUM_AUTO_FEED));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_AUTO_FEED                       = ITEMS.register("item_wyvern_auto_feed",                   () -> new ModuleItem<>(WYVERN_AUTO_FEED));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_AUTO_FEED                     = ITEMS.register("item_draconic_auto_feed",                 () -> new ModuleItem<>(DRACONIC_AUTO_FEED));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_NIGHT_VISION                        = MODULES.register("wyvern_night_vision",                   () -> new ModuleImpl<>(NIGHT_VISION,                    WYVERN,                 noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_NIGHT_VISION                    = ITEMS.register("item_wyvern_night_vision",                () -> new ModuleItem<>(WYVERN_NIGHT_VISION));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_HILL_STEP                           = MODULES.register("wyvern_hill_step",                      () -> new ModuleImpl<>(HILL_STEP,                       WYVERN,                 noData()));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_HILL_STEP                       = ITEMS.register("item_wyvern_hill_step",                   () -> new ModuleItem<>(WYVERN_HILL_STEP));

    public static final DeferredHolder<Module<?>, Module<?>> DRACONIUM_JUMP                             = MODULES.register("draconium_jump",                        () -> new ModuleImpl<>(JUMP_BOOST,                      DRACONIUM,              jumpData(0.25)));
    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_JUMP                                = MODULES.register("wyvern_jump",                           () -> new ModuleImpl<>(JUMP_BOOST,                      WYVERN,                 jumpData(0.75)));
    public static final DeferredHolder<Module<?>, Module<?>> DRACONIC_JUMP                              = MODULES.register("draconic_jump",                         () -> new ModuleImpl<>(JUMP_BOOST,                      DRACONIC,               jumpData(1.25)));
    public static final DeferredHolder<Module<?>, Module<?>> CHAOTIC_JUMP                               = MODULES.register("chaotic_jump",                          () -> new ModuleImpl<>(JUMP_BOOST,                      CHAOTIC,                jumpData(4.00)));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_JUMP                         = ITEMS.register("item_draconium_jump",                     () -> new ModuleItem<>(DRACONIUM_JUMP));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_JUMP                            = ITEMS.register("item_wyvern_jump",                        () -> new ModuleItem<>(WYVERN_JUMP));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_JUMP                          = ITEMS.register("item_draconic_jump",                      () -> new ModuleItem<>(DRACONIC_JUMP));
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_JUMP                           = ITEMS.register("item_chaotic_jump",                       () -> new ModuleItem<>(CHAOTIC_JUMP));

    public static final DeferredHolder<Module<?>, Module<?>> WYVERN_AQUA_ADAPT                          = MODULES.register("wyvern_aqua_adapt",                     () -> new ModuleImpl<>(AQUA_ADAPT,           WYVERN,         noData())); //TODO
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_AQUA_ADAPT                      = ITEMS.register("item_wyvern_aqua_adapt",                  () -> new ModuleItem<>(WYVERN_AQUA_ADAPT));
    //@formatter:on



    //region Data Helpers

    private static Function<Module<EnergyData>, EnergyData> energyData(long defCapacity, long defTransfer) {
        return e -> {
            long capacity = ModuleCfg.getModuleLong(e, "capacity", defCapacity);
            long transfer = ModuleCfg.getModuleLong(e, "transfer", defTransfer);
            return new EnergyData(capacity, transfer);
        };
    }

    private static Function<Module<EnergyLinkData>, EnergyLinkData> energyLinkData(long defActivation, long defOperation, long defTransfer, boolean defDimensional) {
        return e -> {
            long activation = ModuleCfg.getModuleLong(e, "activation", defActivation);
            long operation = ModuleCfg.getModuleLong(e, "operation", defOperation);
            long transfer = ModuleCfg.getModuleLong(e, "transfer", defTransfer);
            boolean dimensional = ModuleCfg.getModuleBoolean(e, "dimensional", defDimensional);
            return new EnergyLinkData(activation, operation, transfer, dimensional);
        };
    }

    private static Function<Module<ShieldData>, ShieldData> shieldData(int defCapacity, double defRechargePerSecond) {
        return e -> {
            int capacity = ModuleCfg.getModuleInt(e, "capacity", defCapacity);
            double recharge = ModuleCfg.getModuleDouble(e, "recharge", defRechargePerSecond / 20); //Convert to per-tick
            return new ShieldData(capacity, recharge);
        };
    }

    private static Function<Module<ShieldControlData>, ShieldControlData> shieldControl(double defSeconds) {
        return e -> {
            int ticks = ModuleCfg.getModuleInt(e, "cool_down_ticks", (int) (defSeconds * 20D));
            return new ShieldControlData(ticks);
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

    private static Function<Module<FlightData>, FlightData> flightData(boolean elytra, boolean creative, double defSpeed) {
        return e -> {
            double speed = ModuleCfg.getModuleDouble(e, "elytra_boost_speed", defSpeed);
            return new FlightData(elytra, creative, speed);
        };
    }

    private static Function<Module<UndyingData>, UndyingData> undyingData(float defHealthBoost, float defShieldBoost, int shieldBoostTime, int defChargeTime, long defChargeEnergy, double defInvulnSeconds) {
        return e -> {
            float health = (float) ModuleCfg.getModuleDouble(e, "health_boost", defHealthBoost);
            float shield = (float) ModuleCfg.getModuleDouble(e, "shield_boost", defShieldBoost);
            int shieldTime = ModuleCfg.getModuleInt(e, "shield_boost_time", shieldBoostTime);
            int charge = ModuleCfg.getModuleInt(e, "charge_ticks", defChargeTime);
            long energy = ModuleCfg.getModuleLong(e, "charge_energy", defChargeEnergy);
            double invuln = ModuleCfg.getModuleDouble(e, "invulnerable_time", defInvulnSeconds);
            return new UndyingData(health, shield, shieldTime, charge, energy, (int) (invuln * 20));
        };
    }

    private static Function<Module<AutoFeedData>, AutoFeedData> autoFeedData(float defFoodStorage) {
        return e -> {
            float foodStorage = (float) ModuleCfg.getModuleDouble(e, "food_storage", defFoodStorage);
            return new AutoFeedData(foodStorage);
        };
    }

    private static Function<Module<DamageModData>, DamageModData> dmgModData(IDamageModifier modifier) {
        return e -> {
            return new DamageModData(modifier);
        };
    }

    private static Function<Module<ProjectileData>, ProjectileData> projectileData(float defVelocityModifier, float defAccuracyModifier, float defAntiGravModifier, float defPenetrationModifier, float defDamageModifier) {
        return e -> {
            float velocityModifier = (float) ModuleCfg.getModuleDouble(e, "velocity_modifier", defVelocityModifier);
            float accuracyModifier = (float) ModuleCfg.getModuleDouble(e, "accuracy_modifier", defAccuracyModifier);
            float antiGravModifier = (float) ModuleCfg.getModuleDouble(e, "anti_grav_modifier", defAntiGravModifier);
            float penetrationModifier = (float) ModuleCfg.getModuleDouble(e, "penetration_modifier", defPenetrationModifier);
            float damageModifier = (float) ModuleCfg.getModuleDouble(e, "damage_modifier", defDamageModifier);
            return new ProjectileData(velocityModifier, accuracyModifier, antiGravModifier, penetrationModifier, damageModifier);
        };
    }

    private static Function<Module<ProjectileData>, ProjectileData> projVelocityData(float velocityModifier, float penaltyModifier) {
        float accuracyPenalty = velocityModifier * -0.125F * penaltyModifier;
        float penetrationBoost = velocityModifier * 0.25F * (1 - penaltyModifier);
        return projectileData(velocityModifier, accuracyPenalty, 0, penetrationBoost, 0);
    }

    private static Function<Module<ProjectileData>, ProjectileData> projAccuracyData(float accuracyModifier, float penaltyModifier) {
        float velocityPenalty = accuracyModifier * -0.25F * penaltyModifier;
        float penetrationPenalty = velocityPenalty * 0.25F;
        return projectileData(velocityPenalty, accuracyModifier, 0, penetrationPenalty, 0);
    }

    private static Function<Module<ProjectileData>, ProjectileData> projAntiGravData(float antiGravModifier, float penaltyModifier) {
        float velocityPenalty = antiGravModifier * (-0.0625f) * penaltyModifier;
        float accuracyPenalty = antiGravModifier * -0.125F * penaltyModifier;
        float damagePenalty = antiGravModifier * -0.15F * penaltyModifier;
        float penetrationPenalty = velocityPenalty * 0.25F;
        return projectileData(velocityPenalty, accuracyPenalty, antiGravModifier, penetrationPenalty, damagePenalty);
    }

    private static Function<Module<ProjectileData>, ProjectileData> projPenetrationData(float penetrationModifier, float penaltyModifier) {
        float accuracyPenalty = penetrationModifier * -0.25F * penaltyModifier;
        return projectileData(0, accuracyPenalty, 0, penetrationModifier, 0);
    }

    private static Function<Module<ProjectileData>, ProjectileData> projDamageData(float damageModifier, float penaltyModifier) {
        float accuracyPenalty = damageModifier * -0.125F * penaltyModifier;
        return projectileData(0, accuracyPenalty, 0, 0, damageModifier);
    }

    private static Function<Module<TreeHarvestData>, TreeHarvestData> treeHarvestData(int defRange, int defSpeed) {
        return e -> new TreeHarvestData(ModuleCfg.getModuleInt(e, "range", defRange), ModuleCfg.getModuleInt(e, "speed", defSpeed));
    }

    private static Function<Module<NoData>, NoData> noData() {
        return e -> new NoData();
    }

    //endregion
}