package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.config.ModConfigProperty;

/**
 * Created by brandon3055 on 30/06/2016.
 * This class holds all of the base stats for the tools
 */
public class ToolStats {
    //Common
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseRFCapacity")
    public static int WYVERN_BASE_CAPACITY = 4000000;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseRFCapacity")
    public static int DRACONIC_BASE_CAPACITY = 16000000;

    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernBaseMineAOE")
    public static int BASE_WYVERN_MINING_AOE = 1;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicBaseMineAOE")
    public static int BASE_DRACONIC_MINING_AOE = 2;

    //Wyvern Shovel
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelMineSpeed")
    public static double WYV_SHOVEL_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernShovelAttackDMG")
    public static double WYV_SHOVEL_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed")
    public static double WYV_SHOVEL_ATTACK_SPEED = -3D;
    //Wyvern Pick
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicMineSpeed")
    public static double WYV_PICK_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackDMG")
    public static double WYV_PICK_ATTACK_DAMAGE = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernPicAttackSpeed")
    public static double WYV_PICK_ATTACK_SPEED = -3D;
    //Wyvern Axe
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeMineSpeed")
    public static double WYV_AXE_MINING_SPEED = 12D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAxeAttackDMG")
    public static double WYV_AXE_ATTACK_DAMAGE = 25D;
    @ModConfigProperty(category = "Stat Tweaks", name = "wyvernAttackSpeed")
    public static double WYV_AXE_ATTACK_SPEED = -3.3D;
    //Wyvern Hoe
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_HOE_ATTACK_DAMAGE = 8D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_HOE_ATTACK_SPEED = 0;
    //Wyvern Sword
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_SWORD_ATTACK_DAMAGE = 15D;
//    @ModConfigProperty(category = "Stat Tweaks", name = "", comment = "")
    public static double WYV_SWORD_ATTACK_SPEED = -2.2D;

    //Draconic Shovel
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelMineSpeed")
    public static double DRA_SHOVEL_MINING_SPEED = 15D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackDMG")
    public static double DRA_SHOVEL_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicShovelAttackSpeed")
    public static double DRA_SHOVEL_ATTACK_SPEED = -2.9D;
    //Draconic Pick
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicMineSpeed")
    public static double DRA_PICK_MINING_SPEED = 15D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackDMG")
    public static double DRA_PICK_ATTACK_DAMAGE = 20D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicPicAttackSpeed")
    public static double DRA_PICK_ATTACK_SPEED = -2.9D;
    //Draconic Axe
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeMineSpeed")
    public static double DRA_AXE_MINING_SPEED = 15D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackDMG")
    public static double DRA_AXE_ATTACK_DAMAGE = 45D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicAxeAttackSpeed")
    public static double DRA_AXE_ATTACK_SPEED = -3.2D;
    //Draconic Hoe
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackDMG")
    public static double DRA_HOE_ATTACK_DAMAGE = 10D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicHoeAttackSpeed")
    public static double DRA_HOE_ATTACK_SPEED = 0;
    //Draconic Sword
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackDMG")
    public static double DRA_SWORD_ATTACK_DAMAGE = 35D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicSwordAttackSpeed")
    public static double DRA_SWORD_ATTACK_SPEED = -2D;

    //Staff
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffMineSpeed")
    public static double DRA_STAFF_MINING_SPEED = 60D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackDMG")
    public static double DRA_STAFF_ATTACK_DAMAGE = 60D;
    @ModConfigProperty(category = "Stat Tweaks", name = "draconicStaffAttackSpeed")
    public static double DRA_STAFF_ATTACK_SPEED = -3D;
}
