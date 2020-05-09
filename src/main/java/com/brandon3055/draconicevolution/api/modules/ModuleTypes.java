package com.brandon3055.draconicevolution.api.modules;


import com.brandon3055.draconicevolution.api.modules.lib.BasicModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.*;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleTypes {


    //@formatter:off
    public static final ModuleType<EnergyData>  ENERGY_STORAGE      = new BasicModuleType<>("energy_storage",   1, 1);
    public static final ModuleType<NoData>      ENERGY_LINK         = new BasicModuleType<>("energy_link",      4, 4).setMaxInstallable(1);                              //Higher tiers should work accross dimensions but not lower tiers
    public static final ModuleType<AOEData>     AREA_OF_EFFECT      = new BasicModuleType<>("area_of_effect",   2, 2).setMaxInstallable(1);                           //I want there to be a limit on maximum AOE
    public static final ModuleType<DamageData>  DAMAGE              = new BasicModuleType<>("damage",           1, 1);
    public static final ModuleType<SpeedData>   SPEED               = new BasicModuleType<>("speed",            1, 1);
    public static final ModuleType<NoData>      SHIELD_CONTROLLER   = new BasicModuleType<>("shield_control",   2, 2).setMaxInstallable(1);
    public static final ModuleType<ShieldData>  SHIELD              = new BasicModuleType<>("shield",           1, 1);                                      //This covers both capacity and recharge
    public static final ModuleType<NoData>      LAST_STAND          = new BasicModuleType<>("last_stand",       2, 2).setMaxInstallable(1);          //TODO this needs some properties
    public static final ModuleType<NoData>      CREATIVE_FLIGHT     = new BasicModuleType<>("creative_flight",  3, 3).setMaxInstallable(1);
    public static final ModuleType<NoData>      ELYTRA_FLIGHT       = new BasicModuleType<>("elytra_flight",    2, 2).setMaxInstallable(1);       //Todo some property that controls some 'boost function' or something along those lines
    public static final ModuleType<NoData>      NIGHT_VISION        = new BasicModuleType<>("night_vision",     2, 1).setMaxInstallable(1);
    public static final ModuleType<NoData>      AUTO_FEED           = new BasicModuleType<>("auto_feed",        2, 2).setMaxInstallable(1);           //Different tiers could make it smaller? Maybe chaotic removes need for food?
    //@formatter:on

    //Vacuum upgrade (for chest and maybe grinder )

    /*
     * (All the obvious stuff)
     * Night vision
     * Auto Feed
     * [Character Enhancements]
     * Damage
     * AOE
     *
     * */

    //Enchants
    //Arbitrary (armor, )
}
