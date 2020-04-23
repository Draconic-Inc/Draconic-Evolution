package com.brandon3055.draconicevolution.api.modules;


import com.brandon3055.draconicevolution.api.modules.lib.BasicModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.*;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleTypes {


    public static final ModuleType<EnergyModuleProperties> ENERGY_STORAGE = new BasicModuleType<>();
    public static final ModuleType<BlankModuleProperties> ENERGY_LINK = new BasicModuleType<>(1);   //Higher tiers should work accross dimensions but not lower tiers
    public static final ModuleType<AOEModuleProperties> AREA_OF_EFFECT = new BasicModuleType<>(1);  //I want there to be a limit on maximum AOE
    public static final ModuleType<DamageModuleProperties> DAMAGE = new BasicModuleType<>();
    public static final ModuleType<SpeedModuleProperties> SPEED = new BasicModuleType<>();
    public static final ModuleType<BlankModuleProperties> SHIELD_CONTROLLER = new BasicModuleType<>(1).setDefaultSize(2, 2);
    public static final ModuleType<ShieldModuleProperties> SHIELD = new BasicModuleType<>();        //This covers both capacity and recharge
    public static final ModuleType<BlankModuleProperties> LAST_STAND = new BasicModuleType<>(1).setDefaultSize(2, 2);    //TODO this needs some properties
    public static final ModuleType<BlankModuleProperties> CREATIVE_FLIGHT = new BasicModuleType<>(1).setDefaultSize(3, 3);
    public static final ModuleType<BlankModuleProperties> ELYTRA_FLIGHT = new BasicModuleType<>(1).setDefaultSize(2, 2);  //Todo some property that controls some 'boost function' or something along those lines
    public static final ModuleType<BlankModuleProperties> NIGHT_VISION = new BasicModuleType<>(1).setDefaultSize(2, 1);
    public static final ModuleType<BlankModuleProperties> AUTO_FEED = new BasicModuleType<>(1).setDefaultSize(2, 2); //Different tiers could make it smaller? Maybe chaotic removes need for food?


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
