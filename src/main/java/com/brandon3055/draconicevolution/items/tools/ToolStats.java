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
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernBaseRFCapacity", requiresSync = true)
    public static int WYVERN_BASE_CAPACITY = 4000000;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicBaseRFCapacity", requiresSync = true)
    public static int DRACONIC_BASE_CAPACITY = 16000000;

    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernBaseMineAOE", requiresSync = true)
    public static int BASE_WYVERN_MINING_AOE = 0;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicBaseMineAOE", requiresSync = true)
    public static int BASE_DRACONIC_MINING_AOE = 1;

    //Wyvern Shovel
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernShovelMineSpeed", requiresSync = true)
    public static double WYV_SHOVEL_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernShovelAttackDMG", requiresSync = true)
    public static double WYV_SHOVEL_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernAttackSpeed", requiresSync = true)
    public static double WYV_SHOVEL_ATTACK_SPEED = -3D;
    //Wyvern Pick
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernPicMineSpeed", requiresSync = true)
    public static double WYV_PICK_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernPicAttackDMG", requiresSync = true)
    public static double WYV_PICK_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernPicAttackSpeed", requiresSync = true)
    public static double WYV_PICK_ATTACK_SPEED = -3D;
    //Wyvern Axe
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernAxeMineSpeed", requiresSync = true)
    public static double WYV_AXE_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernAxeAttackDMG", requiresSync = true)
    public static double WYV_AXE_ATTACK_DAMAGE = 25D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernAttackSpeed", requiresSync = true)
    public static double WYV_AXE_ATTACK_SPEED = -3.3D;
    //Wyvern Hoe
//    @ModConfigProperty(category = "Item Stat Tweaks", name = "", comment = "")
    public static double WYV_HOE_ATTACK_DAMAGE = 8D;
    //    @ModConfigProperty(category = "Item Stat Tweaks", name = "", comment =, requiresSync = true "")
    public static double WYV_HOE_ATTACK_SPEED = 0;
    //Wyvern Sword
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernSwordAttackDMG", requiresSync = true)
    public static double WYV_SWORD_ATTACK_DAMAGE = 15D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "wyvernSwordAttackSpeed", requiresSync = true)
    public static double WYV_SWORD_ATTACK_SPEED = -2.2D;

    //Draconic Shovel
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicShovelMineSpeed", requiresSync = true)
    public static double DRA_SHOVEL_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicShovelAttackDMG", requiresSync = true)
    public static double DRA_SHOVEL_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicShovelAttackSpeed", requiresSync = true)
    public static double DRA_SHOVEL_ATTACK_SPEED = -2.9D;
    //Draconic Pick
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicPicMineSpeed", requiresSync = true)
    public static double DRA_PICK_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicPicAttackDMG", requiresSync = true)
    public static double DRA_PICK_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicPicAttackSpeed", requiresSync = true)
    public static double DRA_PICK_ATTACK_SPEED = -2.9D;
    //Draconic Axe
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicAxeMineSpeed", requiresSync = true)
    public static double DRA_AXE_MINING_SPEED = 18D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicAxeAttackDMG", requiresSync = true)
    public static double DRA_AXE_ATTACK_DAMAGE = 45D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicAxeAttackSpeed", requiresSync = true)
    public static double DRA_AXE_ATTACK_SPEED = -3.2D;
    //Draconic Hoe
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicHoeAttackDMG", requiresSync = true)
    public static double DRA_HOE_ATTACK_DAMAGE = 10D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicHoeAttackSpeed", requiresSync = true)
    public static double DRA_HOE_ATTACK_SPEED = 0;
    //Draconic Sword
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicSwordAttackDMG", requiresSync = true)
    public static double DRA_SWORD_ATTACK_DAMAGE = 35D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicSwordAttackSpeed", requiresSync = true)
    public static double DRA_SWORD_ATTACK_SPEED = -2D;

    //Staff
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicStaffMineSpeed", requiresSync = true)
    public static double DRA_STAFF_MINING_SPEED = 60D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicStaffAttackDMG", requiresSync = true)
    public static double DRA_STAFF_ATTACK_DAMAGE = 60D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "draconicStaffAttackSpeed", requiresSync = true)
    public static double DRA_STAFF_ATTACK_SPEED = -3D;

    //Armor
    @ModConfigProperty(category = "Item Stat Tweaks", name = "flightSpeedModifier", requiresSync = true)
    public static double FLIGHT_SPEED_MODIFIER = 1D;
    @ModConfigProperty(category = "Item Stat Tweaks", name = "lastStandEnergyRequirement", requiresSync = true)
    public static int LAST_STAND_ENERGY = 10000000;
}
