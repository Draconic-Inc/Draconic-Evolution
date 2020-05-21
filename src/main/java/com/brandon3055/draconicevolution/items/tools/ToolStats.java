package com.brandon3055.draconicevolution.items.tools;


/**
 * Created by brandon3055 on 30/06/2016.
 * This class holds all of the base stats for the tools
 */
//@ModConfigContainer(modid = DraconicEvolution.MODID)
@Deprecated //Nope this shit needs to go!
public class ToolStats {
    //Common
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseRFCapacity", requiresSync = true)
    public static int WYVERN_BASE_CAPACITY = 4000000;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseRFCapacity", requiresSync = true)
    public static int DRACONIC_BASE_CAPACITY = 16000000;

//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseMineAOE", requiresSync = true)
    public static int BASE_WYVERN_MINING_AOE = 0;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseMineAOE", requiresSync = true)
    public static int BASE_DRACONIC_MINING_AOE = 1;

    //Wyvern Shovel
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelMineSpeed", requiresSync = true)
    public static double WYV_SHOVEL_MINING_SPEED = 12D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelAttackDMG", requiresSync = true)
    public static double WYV_SHOVEL_ATTACK_DAMAGE = 12D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed", requiresSync = true)
    public static double WYV_SHOVEL_ATTACK_SPEED = -3D;
    //Wyvern Pick
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicMineSpeed", requiresSync = true)
    public static double WYV_PICK_MINING_SPEED = 12D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackDMG", requiresSync = true)
    public static double WYV_PICK_ATTACK_DAMAGE = 12D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackSpeed", requiresSync = true)
    public static double WYV_PICK_ATTACK_SPEED = -3D;
    //Wyvern Axe
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeMineSpeed", requiresSync = true)
    public static double WYV_AXE_MINING_SPEED = 12D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeAttackDMG", requiresSync = true)
    public static double WYV_AXE_ATTACK_DAMAGE = 25D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed", requiresSync = true)
    public static double WYV_AXE_ATTACK_SPEED = -3.3D;
    //Wyvern Hoe
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_HOE_ATTACK_DAMAGE = 8D;
    //    @ModConfigProperty(category = "Stat Tweaks", name = "", comment =, requiresSync = true "")
    public static double WYV_HOE_ATTACK_SPEED = 0;
    //Wyvern Sword
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernSwordAttackDMG", requiresSync = true)
    public static double WYV_SWORD_ATTACK_DAMAGE = 15D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernSwordAttackSpeed", requiresSync = true)
    public static double WYV_SWORD_ATTACK_SPEED = -2.2D;

    //Draconic Shovel
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelMineSpeed", requiresSync = true)
    public static double DRA_SHOVEL_MINING_SPEED = 18D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackDMG", requiresSync = true)
    public static double DRA_SHOVEL_ATTACK_DAMAGE = 20D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackSpeed", requiresSync = true)
    public static double DRA_SHOVEL_ATTACK_SPEED = -2.9D;
    //Draconic Pick
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicMineSpeed", requiresSync = true)
    public static double DRA_PICK_MINING_SPEED = 18D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackDMG", requiresSync = true)
    public static double DRA_PICK_ATTACK_DAMAGE = 20D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackSpeed", requiresSync = true)
    public static double DRA_PICK_ATTACK_SPEED = -2.9D;
    //Draconic Axe
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeMineSpeed", requiresSync = true)
    public static double DRA_AXE_MINING_SPEED = 18D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackDMG", requiresSync = true)
    public static double DRA_AXE_ATTACK_DAMAGE = 45D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackSpeed", requiresSync = true)
    public static double DRA_AXE_ATTACK_SPEED = -3.2D;
    //Draconic Hoe
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackDMG", requiresSync = true)
    public static double DRA_HOE_ATTACK_DAMAGE = 10D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackSpeed", requiresSync = true)
    public static double DRA_HOE_ATTACK_SPEED = 0;
    //Draconic Sword
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackDMG", requiresSync = true)
    public static double DRA_SWORD_ATTACK_DAMAGE = 35D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackSpeed", requiresSync = true)
    public static double DRA_SWORD_ATTACK_SPEED = -2D;
    
    //Bow
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowBaseDamage", requiresSync = true)
    public static int BOW_BASE_DAMAGE = 2;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowUpgradeMultiplierDamage", requiresSync = true)
    public static int BOW_MULT_DAMAGE = 2;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowTierMultiplierDamage", requiresSync = true)
    public static int BOW_TIER_MULT_DAMAGE = 1;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowBaseSpeed", requiresSync = true)
    public static int BOW_BASE_SPEED = 100;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowUpgradeMultiplierSpeed", requiresSync = true)
    public static int BOW_MULT_SPEED = 100;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowTierMultiplierSpeed", requiresSync = true)
    public static int BOW_TIER_MULT_SPEED = 100;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowMaxExplosionPower", requiresSync = true)
    public static int BOW_MAX_EXPLOSION_POWER = 4;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowWyvernMaxZoom", requiresSync = true)
    public static int BOW_WYVERN_MAX_ZOOM = 300;
//    @ModConfigProperty(category = "Stat Tweaks", name = "bowDraconicMaxZoom", requiresSync = true)
    public static int BOW_DRACONIC_MAX_ZOOM = 600;

    //Staff
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffMineSpeed", requiresSync = true)
    public static double DRA_STAFF_MINING_SPEED = 60D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackDMG", requiresSync = true)
    public static double DRA_STAFF_ATTACK_DAMAGE = 60D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackSpeed", requiresSync = true)
    public static double DRA_STAFF_ATTACK_SPEED = -3D;

    //Armor
//    @ModConfigProperty(category = "Stat Tweaks", name = "flightSpeedModifier", requiresSync = true)
    public static double FLIGHT_SPEED_MODIFIER = 1D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "lastStandEnergyRequirement", requiresSync = true)
    public static int LAST_STAND_ENERGY = 10000000;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseShieldCapacity", comment = "Allows you to adjust the total shield capacity of a full set of Wyvern Armor.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int WYVERN_BASE_SHIELD_CAPACITY = 256;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShieldRechargeCost", comment = "Allows you to adjust the amount of RF that Wyvern Armor requires to recharge 1 shield point.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int WYVERN_SHIELD_RECHARGE_COST = 1000;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShieldRecovery", comment = "Allows you to adjust how fast Wyvern Armor is able to recover entropy.  Value is {this number}% every 5 seconds.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static double WYVERN_SHIELD_RECOVERY = 2D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernMaxRecieve", comment = "Allows you to adjust how fast Wyvern Armor is able to recieve RF/tick.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int WYVERN_MAX_RECIEVE = 512000;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseShieldCapacity", comment = "Allows you to adjust the total shield capacity of a full set of Draconic Armor.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int DRACONIC_BASE_SHIELD_CAPACITY = 512;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShieldRechargeCost", comment = "Allows you to adjust the amount of RF that Draconic Armor requires to recharge 1 shield point.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int DRACONIC_SHIELD_RECHARGE_COST = 1000;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShieldRecovery", comment = "Allows you to adjust how fast Draconic Armor is able to recover entropy.  Value is {this number}% every 5 seconds.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static double DRACONIC_SHIELD_RECOVERY = 4D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "draconicMaxRecieve", comment = "Allows you to adjust how fast Draconic Armor is able to recieve RF/tick.", autoSync = true)
//    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int DRACONIC_MAX_RECIEVE = 1000000;
}
