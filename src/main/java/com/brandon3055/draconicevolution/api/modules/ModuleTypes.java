package com.brandon3055.draconicevolution.api.modules;


import com.brandon3055.draconicevolution.api.modules.data.*;
import com.brandon3055.draconicevolution.api.modules.entities.*;
import com.brandon3055.draconicevolution.api.modules.types.DamageType;
import com.brandon3055.draconicevolution.api.modules.types.JumpType;
import com.brandon3055.draconicevolution.api.modules.types.ModuleTypeImpl;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleTypes {


    //@formatter:off
    //Power
    public static final ModuleType<EnergyData>           ENERGY_STORAGE      = new ModuleTypeImpl<>("energy_storage",    1, 1, EnergyEntity::new, ModuleCategory.ENERGY);
    public static final ModuleType<EnergyShareData>      ENERGY_SHARE        = new ModuleTypeImpl<>("energy_share",      1, 1, ModuleCategory.ENERGY); //TODO this will require a custom entity and item
    public static final ModuleType<EnergyLinkData>       ENERGY_LINK         = new ModuleTypeImpl<>("energy_link",       4, 4, ModuleCategory.ENERGY).setMaxInstallable(1);                              //Higher tiers should work accross dimensions but not lower tiers

    //Armor specific
    public static final ModuleType<ShieldControlData>    SHIELD_CONTROLLER   = new ModuleTypeImpl<>("shield_control",    2, 2, ShieldControlEntity::new, ModuleCategory.CHESTPIECE).setMaxInstallable(1);
    public static final ModuleType<ShieldData>           SHIELD_BOOST        = new ModuleTypeImpl<>("shield_boost",      1, 1, ModuleCategory.CHESTPIECE);
    public static final ModuleType<NoData>               CLOAKING            = new ModuleTypeImpl<>("cloaking",          2, 2, ModuleCategory.CHESTPIECE);
    public static final ModuleType<FlightData>           FLIGHT              = new ModuleTypeImpl<>("flight",            3, 3, FlightEntity::new, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_CHEST).setMaxInstallable(1);
    public static final ModuleType<UndyingData>          UNDYING             = new ModuleTypeImpl<>("undying",           2, 2, UndyingEntity::new, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_CHEST).setMaxInstallable(1);
    public static final ModuleType<AutoFeedData>         AUTO_FEED           = new ModuleTypeImpl<>("auto_feed",         2, 1, AutoFeedEntity::new, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_HEAD).setMaxInstallable(1);           //Different tiers could make it smaller? Maybe chaotic removes need for food?
    public static final ModuleType<NoData>               NIGHT_VISION        = new ModuleTypeImpl<>("night_vision",      2, 1, NightVisionEntity::new, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_HEAD).setMaxInstallable(1);
    public static final JumpType                         JUMP_BOOST          = new JumpType("jump_boost",                1, 1, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_FEET).setMaxInstallable(3);
    public static final ModuleType<NoData>               HILL_STEP           = new ModuleTypeImpl<>("hill_step",         2, 1, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_FEET).setMaxInstallable(1);
    //    public static final ModuleType<NoData>             FALL_PROTECT        = new ModuleTypeImpl<>("fall_protect",      1, 1, CHESTPIECE, ARMOR_FEET);
    public static final ModuleType<NoData>               AQUA_ADAPT          = new ModuleTypeImpl<>("aqua_adapt",        1, 1, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_CHEST);

    //Tool Specific
    public static final ModuleType<NoData>               MINING_STABILITY    = new ModuleTypeImpl<>("mining_stability",  1, 1, ModuleCategory.MINING_TOOL);
    public static final ModuleType<TreeHarvestData>      TREE_HARVEST        = new ModuleTypeImpl<>("tree_harvest",      2, 2, TreeHarvestEntity::new, ModuleCategory.TOOL_AXE).setMaxInstallable(1);
    //Shootables
    public static final ModuleType<NoData>               AUTO_FIRE           = new ModuleTypeImpl<>("auto_fire",         2, 1, AutoFireEntity::new, ModuleCategory.RANGED_WEAPON).setMaxInstallable(1);
    //Projectiles
    public static final ModuleType<ProjectileData>       PROJ_MODIFIER       = new ModuleTypeImpl<>("proj_modifier",     1, 1, ModuleCategory.RANGED_WEAPON);
    public static final ModuleType<NoData>               PROJ_ANTI_IMMUNE    = new ModuleTypeImpl<>("proj_anti_immune",  2, 2, ModuleCategory.RANGED_WEAPON).setMaxInstallable(1);
    //Enchantments?

    //General / Misc
    public static final ModuleType<AOEData>             AOE                 = new ModuleTypeImpl<>("aoe",               3, 3, ModuleCategory.MINING_TOOL, ModuleCategory.MELEE_WEAPON).setMaxInstallable(1);                           //I want there to be a limit on maximum AOE
    public static final ModuleType<DamageData>          DAMAGE              = new DamageType("damage",                  1, 1, ModuleCategory.MELEE_WEAPON);
    public static final ModuleType<SpeedData>           SPEED               = new ModuleTypeImpl<>("speed",             1, 1, ModuleCategory.MINING_TOOL, ModuleCategory.MELEE_WEAPON, ModuleCategory.RANGED_WEAPON, ModuleCategory.CHESTPIECE, ModuleCategory.ARMOR_LEGS).setMaxInstallable(8);
    public static final ModuleType<NoData>              JUNK_FILTER         = new ModuleTypeImpl<>("junk_filter",       2, 2, JunkFilterEntity::new, ModuleCategory.MINING_TOOL, ModuleCategory.MELEE_WEAPON);
    public static final ModuleType<NoData>              VACUUM              = new ModuleTypeImpl<>("vacuum",            1, 1, ModuleCategory.MINING_TOOL, ModuleCategory.MELEE_WEAPON);
    public static final ModuleType<NoData>              ENDER_COLLECTION    = new ModuleTypeImpl<>("ender_collection",  1, 1, EnderCollectionEntity::new, ModuleCategory.MINING_TOOL, ModuleCategory.MELEE_WEAPON).setMaxInstallable(1);
    public static final ModuleType<DamageModData>       DAMAGE_MOD          = new ModuleTypeImpl<>("damage_mod",        3, 3).setMaxInstallable(1);

    //@formatter:on

    //Vacuum upgrade (for chest and maybe grinder )

}
