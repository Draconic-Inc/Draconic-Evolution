package com.brandon3055.draconicevolution.api.modules;


import com.brandon3055.draconicevolution.api.modules.entities.*;
import com.brandon3055.draconicevolution.api.modules.types.JumpType;
import com.brandon3055.draconicevolution.api.modules.types.ModuleTypeImpl;
import com.brandon3055.draconicevolution.api.modules.data.*;
import com.brandon3055.draconicevolution.api.modules.types.DamageType;

import static com.brandon3055.draconicevolution.api.modules.ModuleCategory.*;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleTypes {


    //@formatter:off
    //Power
    public static final ModuleType<EnergyData>          ENERGY_STORAGE      = new ModuleTypeImpl<>("energy_storage",    1, 1, EnergyEntity::new, ENERGY);
    public static final ModuleType<EnergyShareData>     ENERGY_SHARE        = new ModuleTypeImpl<>("energy_share",      1, 1, ENERGY); //TODO this will require a custom entity and item
    public static final ModuleType<NoData>              ENERGY_LINK         = new ModuleTypeImpl<>("energy_link",       4, 4, ENERGY).setMaxInstallable(1);                              //Higher tiers should work accross dimensions but not lower tiers

    //Armor specific
    public static final ModuleType<ShieldControlData>   SHIELD_CONTROLLER   = new ModuleTypeImpl<>("shield_control",    2, 2, ShieldControlEntity::new, CHESTPIECE).setMaxInstallable(1);
    public static final ModuleType<ShieldData>          SHIELD_BOOST        = new ModuleTypeImpl<>("shield_boost",      1, 1, CHESTPIECE);                                      //This covers both capacity and recharge
    public static final ModuleType<FlightData>          FLIGHT              = new ModuleTypeImpl<>("flight",            3, 3, FlightEntity::new, CHESTPIECE, ARMOR_CHEST).setMaxInstallable(1);
//    public static final ModuleType<NoData>            ELYTRA_FLIGHT       = new ModuleTypeImpl<>("elytra_flight",     2, 2, CHESTPIECE, ARMOR_CHEST).setMaxInstallable(1);       //Todo some property that controls some 'boost function' or something along those lines
    public static final ModuleType<LastStandData>       LAST_STAND          = new ModuleTypeImpl<>("last_stand",        2, 2, LastStandEntity::new, CHESTPIECE, ARMOR_CHEST).setMaxInstallable(1);          //TODO this needs some properties
    public static final ModuleType<AutoFeedData>        AUTO_FEED           = new ModuleTypeImpl<>("auto_feed",         2, 1, AutoFeedEntity::new, CHESTPIECE, ARMOR_HEAD).setMaxInstallable(1);           //Different tiers could make it smaller? Maybe chaotic removes need for food?
    public static final ModuleType<NoData>              NIGHT_VISION        = new ModuleTypeImpl<>("night_vision",      2, 1, CHESTPIECE, ARMOR_HEAD).setMaxInstallable(1);
    public static final JumpType                        JUMP_BOOST          = new JumpType("jump_boost",                1, 1, CHESTPIECE, ARMOR_FEET).setMaxInstallable(3);
    public static final ModuleType<NoData>              HILL_STEP           = new ModuleTypeImpl<>("hill_step",         2, 1, CHESTPIECE, ARMOR_FEET).setMaxInstallable(1);
//    public static final ModuleType<NoData>            FALL_PROTECT        = new ModuleTypeImpl<>("fall_protect",      1, 1, CHESTPIECE, ARMOR_FEET);
    public static final ModuleType<NoData>              AQUA_ADAPT          = new ModuleTypeImpl<>("aqua_adapt",        1, 1, CHESTPIECE, ARMOR_CHEST);

    //Tool Specific
    public static final ModuleType<NoData>              MINING_STABILITY    = new ModuleTypeImpl<>("mining_stability",  1, 1, MINING_TOOL);
    //Enchantments?

    //General / Misc
    public static final ModuleType<AOEData>             AOE                 = new ModuleTypeImpl<>("aoe",               3, 3, MINING_TOOL, MELEE_WEAPON, RANGED_WEAPON).setMaxInstallable(1);                           //I want there to be a limit on maximum AOE
    public static final ModuleType<DamageData>          DAMAGE              = new DamageType("damage",                  1, 1, MELEE_WEAPON, RANGED_WEAPON);
    public static final ModuleType<SpeedData>           SPEED               = new ModuleTypeImpl<>("speed",             1, 1, MINING_TOOL, MELEE_WEAPON, RANGED_WEAPON, CHESTPIECE, ARMOR_LEGS).setMaxInstallable(8);
    public static final ModuleType<NoData>              JUNK_FILTER         = new ModuleTypeImpl<>("junk_filter",       1, 1, MINING_TOOL);
    public static final ModuleType<NoData>              VACUUM              = new ModuleTypeImpl<>("vacuum",            1, 1, MINING_TOOL, MELEE_WEAPON, RANGED_WEAPON);
    public static final ModuleType<NoData>              ITEM_TRANSLOCATION  = new ModuleTypeImpl<>("item_translocation",1, 1, MINING_TOOL, MELEE_WEAPON, RANGED_WEAPON);

    //@formatter:on

    //Vacuum upgrade (for chest and maybe grinder )

}
