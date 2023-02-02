package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.client.utils.CyclingItemGroup;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.*;
import com.brandon3055.draconicevolution.api.modules.lib.*;
import com.brandon3055.draconicevolution.modules.ProjectileVelocityModule;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static com.brandon3055.draconicevolution.api.modules.ModuleTypes.*;

/**
 * Created by brandon3055 on 18/4/20.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(MODID)
public class DEModules {
    private static final transient ArrayList<ResourceLocation> ITEM_REGISTRY_ORDER = new ArrayList<>();
    public static transient Map<BaseModule<?>, Item> moduleItemMap = new LinkedHashMap<>();
    private static final transient CyclingItemGroup moduleGroup = new CyclingItemGroup("draconicevolution.modules", 20, () -> moduleItemMap.values().toArray(new Item[0]), ITEM_REGISTRY_ORDER);
    public static IForgeRegistry<Module<?>> MODULE_REGISTRY;

    //@formatter:off
    @ObjectHolder("draconium_energy")               public static Module<EnergyData>        draconiumEnergy;
    @ObjectHolder("wyvern_energy")                  public static Module<EnergyData>        wyvernEnergy;
    @ObjectHolder("draconic_energy")                public static Module<EnergyData>        draconicEnergy;
    @ObjectHolder("chaotic_energy")                 public static Module<EnergyData>        chaoticEnergy;

    @ObjectHolder("wyvern_energy_link")             public static Module<EnergyLinkData>    wyvernEnergyLink;
    @ObjectHolder("draconic_energy_link")           public static Module<EnergyLinkData>    draconicEnergyLink;
    @ObjectHolder("chaotic_energy_link")            public static Module<EnergyLinkData>    chaoticEnergyLink;

    @ObjectHolder("draconium_speed")                public static Module<SpeedData>         draconiumSpeed;
    @ObjectHolder("wyvern_speed")                   public static Module<SpeedData>         wyvernSpeed;
    @ObjectHolder("draconic_speed")                 public static Module<SpeedData>         draconicSpeed;
    @ObjectHolder("chaotic_speed")                  public static Module<SpeedData>         chaoticSpeed;

    @ObjectHolder("draconium_damage")               public static Module<DamageData>        draconiumDamage;
    @ObjectHolder("wyvern_damage")                  public static Module<DamageData>        wyvernDamage;
    @ObjectHolder("draconic_damage")                public static Module<DamageData>        draconicDamage;
    @ObjectHolder("chaotic_damage")                 public static Module<DamageData>        chaoticDamage;

    @ObjectHolder("draconium_aoe")                  public static Module<AOEData>           draconiumAOE;
    @ObjectHolder("wyvern_aoe")                     public static Module<AOEData>           wyvernAOE;
    @ObjectHolder("draconic_aoe")                   public static Module<AOEData>           draconicAOE;
    @ObjectHolder("chaotic_aoe")                    public static Module<AOEData>           chaoticAOE;


    @ObjectHolder("wyvern_mining_stability")        public static Module<NoData>            wyvernMiningStability;


    @ObjectHolder("wyvern_tree_harvest")            public static Module<TreeHarvestData>   wyvernTreeHarvest;
    @ObjectHolder("draconic_tree_harvest")          public static Module<TreeHarvestData>   draconicTreeHarvest;
    @ObjectHolder("chaotic_tree_harvest")           public static Module<TreeHarvestData>   chaoticTreeHarvest;

    @ObjectHolder("wyvern_junk_filter")             public static Module<NoData>            wyvernJunkFilter;

    @ObjectHolder("wyvern_ender_collection")        public static Module<NoData>            wyvernEnderCollection;
    @ObjectHolder("draconic_ender_collection")      public static Module<NoData>            draconicEnderCollection;

    @ObjectHolder("wyvern_auto_fire")               public static Module<NoData>            wyvernAutoFire;

    @ObjectHolder("draconic_proj_anti_immune")      public static Module<NoData>            draconicProjAntiImmune;

    @ObjectHolder("wyvern_proj_velocity")           public static Module<ProjectileData>    wyvernProjVelocity;
    @ObjectHolder("draconic_proj_velocity")         public static Module<ProjectileData>    draconicProjVelocity;
    @ObjectHolder("chaotic_proj_velocity")          public static Module<ProjectileData>    chaoticProjVelocity;

    @ObjectHolder("wyvern_proj_accuracy")           public static Module<ProjectileData>    wyvernProjAccuracy;
    @ObjectHolder("draconic_proj_accuracy")         public static Module<ProjectileData>    draconicProjAccuracy;
    @ObjectHolder("chaotic_proj_accuracy")          public static Module<ProjectileData>    chaoticProjAccuracy;

    @ObjectHolder("wyvern_proj_grav_comp")          public static Module<ProjectileData>    wyvernProjGravComp;
    @ObjectHolder("draconic_proj_grav_comp")        public static Module<ProjectileData>    draconicProjGravComp;
    @ObjectHolder("chaotic_proj_grav_comp")         public static Module<ProjectileData>    chaoticProjGravComp;

    @ObjectHolder("wyvern_proj_penetration")        public static Module<ProjectileData>    wyvernProjPenetration;
    @ObjectHolder("draconic_proj_penetration")      public static Module<ProjectileData>    draconicProjPenetration;
    @ObjectHolder("chaotic_proj_penetration")       public static Module<ProjectileData>    chaoticProjPenetration;

    @ObjectHolder("wyvern_proj_damage")             public static Module<ProjectileData>    wyvernProjDamage;
    @ObjectHolder("draconic_proj_damage")           public static Module<ProjectileData>    draconicProjDamage;
    @ObjectHolder("chaotic_proj_damage")            public static Module<ProjectileData>    chaoticProjDamage;

    @ObjectHolder("draconic_fire_mod")              public static Module<DamageModData>     draconicFireMod;
    @ObjectHolder("chaotic_fire_mod")               public static Module<DamageModData>     chaoticFireMod;

    @ObjectHolder("draconic_lightning_mod")         public static Module<DamageModData>     draconicLightningMod;
    @ObjectHolder("chaotic_lightning_mod")          public static Module<DamageModData>     chaoticLightningMod;

    @ObjectHolder("draconic_ice_mod")               public static Module<DamageModData>     draconicIceMod;
    @ObjectHolder("chaotic_ice_mod")                public static Module<DamageModData>     chaoticIceMod;

    @ObjectHolder("wyvern_shield_control")          public static Module<NoData>            wyvernShieldControl;
    @ObjectHolder("draconic_shield_control")        public static Module<NoData>            draconicShieldControl;
    @ObjectHolder("chaotic_shield_control")         public static Module<NoData>            chaoticShieldControl;

    @ObjectHolder("wyvern_shield_capacity")         public static Module<ShieldData>        wyvernShieldCapacity;
    @ObjectHolder("draconic_shield_capacity")       public static Module<ShieldData>        draconicShieldCapacity;
    @ObjectHolder("chaotic_shield_capacity")        public static Module<ShieldData>        chaoticShieldCapacity;
    @ObjectHolder("wyvern_large_shield_capacity")   public static Module<ShieldData>        wyvernLargeShieldCapacity;
    @ObjectHolder("draconic_large_shield_capacity") public static Module<ShieldData>        draconicLargeShieldCapacity;
    @ObjectHolder("chaotic_large_shield_capacity")  public static Module<ShieldData>        chaoticLargeShieldCapacity;
    @ObjectHolder("wyvern_shield_recovery")         public static Module<ShieldData>        wyvernShieldRecovery;
    @ObjectHolder("draconic_shield_recovery")       public static Module<ShieldData>        draconicShieldRecovery;
    @ObjectHolder("chaotic_shield_recovery")        public static Module<ShieldData>        chaoticShieldRecovery;

    @ObjectHolder("wyvern_cloaking")                public static Module<NoData>            wyvernCloaking;

    @ObjectHolder("wyvern_flight")                  public static Module<FlightData>        wyvernFlight;
    @ObjectHolder("draconic_flight")                public static Module<FlightData>        draconicFlight;
    @ObjectHolder("chaotic_flight")                 public static Module<FlightData>        chaoticFlight;

    @ObjectHolder("wyvern_undying")                 public static Module<NoData>            wyvernUndying;
    @ObjectHolder("draconic_undying")               public static Module<NoData>            draconicUndying;
    @ObjectHolder("chaotic_undying")                public static Module<NoData>            chaoticUndying;

    @ObjectHolder("draconium_auto_feed")            public static Module<NoData>            draconiumAutoFeed;
    @ObjectHolder("wyvern_auto_feed")               public static Module<NoData>            wyvernAutoFeed;
    @ObjectHolder("draconic_auto_feed")             public static Module<NoData>            draconicAutoFeed;

    @ObjectHolder("wyvern_night_vision")            public static Module<NoData>            wyvernNightVision;

    @ObjectHolder("draconium_jump")                 public static Module<JumpData>          draconiumJump;
    @ObjectHolder("wyvern_jump")                    public static Module<JumpData>          wyvernJump;
    @ObjectHolder("draconic_jump")                  public static Module<JumpData>          draconicJump;
    @ObjectHolder("chaotic_jump")                   public static Module<JumpData>          chaoticJump;

    @ObjectHolder("wyvern_aqua_adapt")              public static Module<NoData>            wyvernAquaAdapt;

    @ObjectHolder("wyvern_hill_step")               public static Module<NoData>            wyvernHillStep;

    //@formatter:on


    private static void registerModules() {
        Properties props = new Properties().tab(moduleGroup);
        //@formatter:off

        //Energy
        register(new ModuleImpl<>(ENERGY_STORAGE,       DRACONIUM,      energyData(1000000,  16000)),   new EnergyModuleItem(props), "draconium_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,       WYVERN,         energyData(4000000,  64000)),   new EnergyModuleItem(props), "wyvern_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,       DRACONIC,       energyData(16000000, 256000)),  new EnergyModuleItem(props), "draconic_energy");
        register(new ModuleImpl<>(ENERGY_STORAGE,       CHAOTIC,        energyData(64000000, 1024000)), new EnergyModuleItem(props), "chaotic_energy");

//        register(new ModuleImpl<>(ENERGY_LINK,          WYVERN,         e -> new EnergyLinkData()),                 "wyvern_energy_link");
//        register(new ModuleImpl<>(ENERGY_LINK,          DRACONIC,       e -> new EnergyLinkData()),                 "draconic_energy_link");
//        register(new ModuleImpl<>(ENERGY_LINK,          CHAOTIC,        e -> new EnergyLinkData()),                 "chaotic_energy_link");

        //Tools
        register(new ModuleImpl<>(SPEED,                DRACONIUM,      speedData(0.10)),                           "draconium_speed");
        register(new ModuleImpl<>(SPEED,                WYVERN,         speedData(0.25)),                           "wyvern_speed");
        register(new ModuleImpl<>(SPEED,                DRACONIC,       speedData(0.50)),                           "draconic_speed");
        register(new ModuleImpl<>(SPEED,                CHAOTIC,        speedData(1.50)),                           "chaotic_speed");

        register(new ModuleImpl<>(DAMAGE,               DRACONIUM,      damageData(2)),                             "draconium_damage");
        register(new ModuleImpl<>(DAMAGE,               WYVERN,         damageData(4)),                             "wyvern_damage");
        register(new ModuleImpl<>(DAMAGE,               DRACONIC,       damageData(8)),                             "draconic_damage");
        register(new ModuleImpl<>(DAMAGE,               CHAOTIC,        damageData(16)),                            "chaotic_damage");

        register(new ModuleImpl<>(AOE,                  DRACONIUM,      aoeData(1)),                                "draconium_aoe");
        register(new ModuleImpl<>(AOE,                  WYVERN,         aoeData(2)),                                "wyvern_aoe");
        register(new ModuleImpl<>(AOE,                  DRACONIC,       aoeData(3)),                                "draconic_aoe");
        register(new ModuleImpl<>(AOE,                  CHAOTIC,        aoeData(5)),                                "chaotic_aoe");

//        register(new ModuleImpl<>(MINING_STABILITY,     WYVERN,         noData()),                                  "wyvern_mining_stability");

        register(new ModuleImpl<>(TREE_HARVEST,         WYVERN,         treeHarvestData(16, 5)),                        "wyvern_tree_harvest");
        register(new ModuleImpl<>(TREE_HARVEST,         DRACONIC,       treeHarvestData(48, 15)),                       "draconic_tree_harvest");
        register(new ModuleImpl<>(TREE_HARVEST,         CHAOTIC,        treeHarvestData(144, 45)),                       "chaotic_tree_harvest");

        register(new ModuleImpl<>(JUNK_FILTER,          WYVERN,         noData()),                                  "wyvern_junk_filter");

        register(new ModuleImpl<>(ENDER_COLLECTION,     WYVERN,         noData()),       new EnderCollectionModuleItem(props), "wyvern_ender_collection");
        register(new ModuleImpl<>(ENDER_COLLECTION,     DRACONIC,       noData(), 2, 2), new EnderCollectionModuleItem(props), "draconic_ender_collection");

        //Arrow base velocity is 60 m/s
        register(new ProjectileVelocityModule(PROJ_MODIFIER,        WYVERN,         projVelocityData(0.15F, 1.0F)).setMaxInstall(8),   "wyvern_proj_velocity");   // (1 + (0.15 * 8) * 60) = 132 m/s max
        register(new ProjectileVelocityModule(PROJ_MODIFIER,        DRACONIC,       projVelocityData(0.35F, 0.5F)).setMaxInstall(8),   "draconic_proj_velocity"); // (1 + (0.35 * 8) * 60) = 228 m/s max
        register(new ProjectileVelocityModule(PROJ_MODIFIER,        CHAOTIC,        projVelocityData(0.75F, 0.0F)).setMaxInstall(8),   "chaotic_proj_velocity");  // (1 + (0.75 * 8) * 60) = 420 m/s max

        register(new ModuleImpl<>(PROJ_MODIFIER,        WYVERN,         projAccuracyData(0.125F, 2.0F), 2, 1),      "wyvern_proj_accuracy");
        register(new ModuleImpl<>(PROJ_MODIFIER,        DRACONIC,       projAccuracyData(0.20F, 1.0F), 2, 1),       "draconic_proj_accuracy");
        register(new ModuleImpl<>(PROJ_MODIFIER,        CHAOTIC,        projAccuracyData(0.25F, 0.0F), 2, 1),       "chaotic_proj_accuracy");

        register(new ModuleImpl<>(PROJ_MODIFIER,        WYVERN,         projAntiGravData(0.20F, 2.0F), 2, 1),      "wyvern_proj_grav_comp");
        register(new ModuleImpl<>(PROJ_MODIFIER,        DRACONIC,       projAntiGravData(0.25F, 1.0F), 2, 1),       "draconic_proj_grav_comp");
        register(new ModuleImpl<>(PROJ_MODIFIER,        CHAOTIC,        projAntiGravData(0.50F, 0.0F), 2, 1),       "chaotic_proj_grav_comp");

        register(new ModuleImpl<>(PROJ_MODIFIER,        WYVERN,         projPenetrationData(0.25F, 2.0F), 2, 2),    "wyvern_proj_penetration");
        register(new ModuleImpl<>(PROJ_MODIFIER,        DRACONIC,       projPenetrationData(0.50F, 1.0F), 2, 2),    "draconic_proj_penetration");
        register(new ModuleImpl<>(PROJ_MODIFIER,        CHAOTIC,        projPenetrationData(0.75F, 0.0F), 2, 2),    "chaotic_proj_penetration");

        register(new ModuleImpl<>(PROJ_MODIFIER,        WYVERN,         projDamageData(0.25F, 2.0F)),               "wyvern_proj_damage");
        register(new ModuleImpl<>(PROJ_MODIFIER,        DRACONIC,       projDamageData(0.50F, 1.0F)),               "draconic_proj_damage");
        register(new ModuleImpl<>(PROJ_MODIFIER,        CHAOTIC,        projDamageData(0.75F, 0.0F)),               "chaotic_proj_damage");

        register(new ModuleImpl<>(AUTO_FIRE,            WYVERN,         noData()), "wyvern_auto_fire");

        register(new ModuleImpl<>(PROJ_ANTI_IMMUNE,     DRACONIC,       noData()), "draconic_proj_anti_immune");

//        register(new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new FireDmgMod())),      "draconic_fire_mod");
//        register(new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new FireDmgMod())),      "chaotic_fire_mod");
//
//        register(new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new LightningDmgMod())), "draconic_lightning_mod");
//        register(new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new LightningDmgMod())), "chaotic_lightning_mod");
//
//        register(new ModuleImpl<>(DAMAGE_MOD,           DRACONIC,       dmgModData(new IceDmgMod())),       "draconic_ice_mod");
//        register(new ModuleImpl<>(DAMAGE_MOD,           CHAOTIC,        dmgModData(new IceDmgMod())),       "chaotic_ice_mod");

        //Armor
        register(new ModuleImpl<>(SHIELD_CONTROLLER,    WYVERN,         shieldControl(20.0)),                       "wyvern_shield_control");
        register(new ModuleImpl<>(SHIELD_CONTROLLER,    DRACONIC,       shieldControl(10.0)),                       "draconic_shield_control");
        register(new ModuleImpl<>(SHIELD_CONTROLLER,    CHAOTIC,        shieldControl(5.0)),                        "chaotic_shield_control");

        register(new ModuleImpl<>(SHIELD_BOOST,         WYVERN,         shieldData(25,  0.1)),                      "wyvern_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         DRACONIC,       shieldData(50,  0.25)),                     "draconic_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         CHAOTIC,        shieldData(100, 0.5)),                      "chaotic_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         WYVERN,         shieldData(25*5,  0.0D), 2, 2),             "wyvern_large_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         DRACONIC,       shieldData(50*5,  0.0D), 2, 2),             "draconic_large_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         CHAOTIC,        shieldData(100*5, 0.0D), 2, 2),             "chaotic_large_shield_capacity");
        register(new ModuleImpl<>(SHIELD_BOOST,         WYVERN,         shieldData(5,   1.0)),                      "wyvern_shield_recovery");
        register(new ModuleImpl<>(SHIELD_BOOST,         DRACONIC,       shieldData(10,  2.5)),                      "draconic_shield_recovery");
        register(new ModuleImpl<>(SHIELD_BOOST,         CHAOTIC,        shieldData(20,  5.0)),                      "chaotic_shield_recovery");

//        register(new ModuleImpl<>(CLOAKING,             WYVERN,         noData()),                                  "wyvern_cloaking");

        register(new ModuleImpl<>(FLIGHT,               WYVERN,         flightData(true, false, 1), 2, 2),          "wyvern_flight");
        register(new ModuleImpl<>(FLIGHT,               DRACONIC,       flightData(true, true, 2)),                 "draconic_flight");
        register(new ModuleImpl<>(FLIGHT,               CHAOTIC,        flightData(true, true, 3.5)),               "chaotic_flight");

        register(new ModuleImpl<>(UNDYING,              WYVERN,         undyingData(6F,  25F, 15*20,  4*30*20, 5000000, 2)),                       "wyvern_undying");
        register(new ModuleImpl<>(UNDYING,              DRACONIC,       undyingData(12F, 50F, 30*20,  2*30*20, 10000000, 3)).setMaxInstall(2),     "draconic_undying");
        register(new ModuleImpl<>(UNDYING,              CHAOTIC,        undyingData(20F, 100F,120*20, 45*20, 20000000, 3)).setMaxInstall(3),     "chaotic_undying");

        register(new ModuleImpl<>(AUTO_FEED,            DRACONIUM,      autoFeedData(40)),                          "draconium_auto_feed");
        register(new ModuleImpl<>(AUTO_FEED,            WYVERN,         autoFeedData(150)),                         "wyvern_auto_feed");
        register(new ModuleImpl<>(AUTO_FEED,            DRACONIC,       autoFeedData(400)),                         "draconic_auto_feed");

        register(new ModuleImpl<>(NIGHT_VISION,         WYVERN,         noData()),                                  "wyvern_night_vision");

        register(new ModuleImpl<>(HILL_STEP,            WYVERN,         noData()),                                  "wyvern_hill_step");

        register(new ModuleImpl<>(JUMP_BOOST,           DRACONIUM,      jumpData(0.25)),                            "draconium_jump");
        register(new ModuleImpl<>(JUMP_BOOST,           WYVERN,         jumpData(0.75)),                            "wyvern_jump");
        register(new ModuleImpl<>(JUMP_BOOST,           DRACONIC,       jumpData(1.25)),                            "draconic_jump");
        register(new ModuleImpl<>(JUMP_BOOST,           CHAOTIC,        jumpData(4.00)),                            "chaotic_jump");

//        register(new ModuleImpl<>(AQUA_ADAPT,           WYVERN,         noData()),                                  "wyvern_aqua_adapt");
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

    //region Registry

    @SubscribeEvent
    public static void createRegistries(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Module<?>>()
                        .setName(new ResourceLocation(MODID, "modules"))
                        .setType(SneakyUtils.unsafeCast(Module.class))
                        .disableSaving(),
                ts -> MODULE_REGISTRY = ts);
    }

    private static void register(ModuleImpl<?> module, String name) {
        ModuleItem<?> item = new ModuleItem<>(new Properties().tab(moduleGroup), module);
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