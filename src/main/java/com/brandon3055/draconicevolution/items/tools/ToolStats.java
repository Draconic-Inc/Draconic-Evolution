package com.brandon3055.draconicevolution.items.tools;


import com.brandon3055.brandonscore.registry.ModConfigContainer;
import com.brandon3055.brandonscore.registry.ModConfigProperty;
import com.brandon3055.draconicevolution.DraconicEvolution;

/**
 * Created by brandon3055 on 30/06/2016.
 * This class holds all of the base stats for the tools
 */
@ModConfigContainer(modid = DraconicEvolution.MODID)
public class ToolStats {
    //Common
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseRFCapacity", autoSync = true)
    public static int WYVERN_BASE_CAPACITY = 4000000;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseRFCapacity", autoSync = true)
    public static int DRACONIC_BASE_CAPACITY = 16000000;

    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseMineAOE", autoSync = true)
    public static int BASE_WYVERN_MINING_AOE = 0;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseMineAOE", autoSync = true)
    public static int BASE_DRACONIC_MINING_AOE = 1;

    //Wyvern Shovel
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelMineSpeed", autoSync = true)
    public static double WYV_SHOVEL_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelAttackDMG", autoSync = true)
    public static double WYV_SHOVEL_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed", autoSync = true)
    public static double WYV_SHOVEL_ATTACK_SPEED = -3D;
    //Wyvern Pick
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicMineSpeed", autoSync = true)
    public static double WYV_PICK_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackDMG", autoSync = true)
    public static double WYV_PICK_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackSpeed", autoSync = true)
    public static double WYV_PICK_ATTACK_SPEED = -3D;
    //Wyvern Axe
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeMineSpeed", autoSync = true)
    public static double WYV_AXE_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeAttackDMG", autoSync = true)
    public static double WYV_AXE_ATTACK_DAMAGE = 25D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed", autoSync = true)
    public static double WYV_AXE_ATTACK_SPEED = -3.3D;
    //Wyvern Hoe
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_HOE_ATTACK_DAMAGE = 8D;
    //    @ModConfigProperty(category = "Stat Tweaks", name = "", comment =, autoSync = true "")
    public static double WYV_HOE_ATTACK_SPEED = 0;
    //Wyvern Sword
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernSwordAttackDMG", autoSync = true)
    public static double WYV_SWORD_ATTACK_DAMAGE = 15D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernSwordAttackSpeed", autoSync = true)
    public static double WYV_SWORD_ATTACK_SPEED = -2.2D;

    //Draconic Shovel
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelMineSpeed", autoSync = true)
    public static double DRA_SHOVEL_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackDMG", autoSync = true)
    public static double DRA_SHOVEL_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackSpeed", autoSync = true)
    public static double DRA_SHOVEL_ATTACK_SPEED = -2.9D;
    //Draconic Pick
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicMineSpeed", autoSync = true)
    public static double DRA_PICK_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackDMG", autoSync = true)
    public static double DRA_PICK_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackSpeed", autoSync = true)
    public static double DRA_PICK_ATTACK_SPEED = -2.9D;
    //Draconic Axe
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeMineSpeed", autoSync = true)
    public static double DRA_AXE_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackDMG", autoSync = true)
    public static double DRA_AXE_ATTACK_DAMAGE = 45D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackSpeed", autoSync = true)
    public static double DRA_AXE_ATTACK_SPEED = -3.2D;
    //Draconic Hoe
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackDMG", autoSync = true)
    public static double DRA_HOE_ATTACK_DAMAGE = 10D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackSpeed", autoSync = true)
    public static double DRA_HOE_ATTACK_SPEED = 0;
    //Draconic Sword
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackDMG", autoSync = true)
    public static double DRA_SWORD_ATTACK_DAMAGE = 35D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackSpeed", autoSync = true)
    public static double DRA_SWORD_ATTACK_SPEED = -2D;

    //Staff
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffMineSpeed", autoSync = true)
    public static double DRA_STAFF_MINING_SPEED = 60D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackDMG", autoSync = true)
    public static double DRA_STAFF_ATTACK_DAMAGE = 60D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackSpeed", autoSync = true)
    public static double DRA_STAFF_ATTACK_SPEED = -3D;

    //Armor
    @ModConfigProperty(category = "Stat Tweaks", name = "flightSpeedModifier", autoSync = true)
    public static double FLIGHT_SPEED_MODIFIER = 1D;
    @ModConfigProperty(category = "Stat Tweaks", name = "lastStandEnergyRequirement", autoSync = true)
    public static int LAST_STAND_ENERGY = 10000000;
}
