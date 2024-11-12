package com.brandon3055.draconicevolution.achievements;


import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;

import java.util.HashMap;

/**
 * Created by Brandon on 18/11/2014.
 */
public class Achievements {

//    private static AchievementPage achievementsPage;
    private static HashMap<String, Advancement> achievementsList = new HashMap<String, Advancement>();
    private static HashMap<String, AchievementCondition> achievementItems = new HashMap<String, AchievementCondition>();

    public static void addAchievement(String name, Advancement achievement, ItemStack stack, String triggerCondition) {
//        if (stack.isEmpty()) {
//            return;
//        }
//        achievementsList.put(name, achievement.registerStat());
//        achievementItems.put(stack.getUnlocalizedName(), new AchievementCondition(name, triggerCondition));
    }

    public static void addAchievement(String name, Advancement achievement, String triggerCondition) {
//        addAchievement(name, achievement, achievement.getDisplay().getIcon(), triggerCondition);
    }

    public static void addAchievement(String name, Advancement achievement) {
//        addAchievement(name, achievement, achievement.theItemStack, "null");
    }

    public static Advancement getAchievement(String name) {
        return achievementsList.get(name);
    }

    public static void triggerAchievement(Player player, String name) {

//        Achievement ach = getAchievement(name);
//
//        if (ach != null) {
//            player.addStat(ach);
//        }
    }

//    public static void addModAchievements() {
////todo Achivements
//        int x = 5;
//        addAchievement("draconicevolution.dust", new Advancement("draconicevolution.dust", "draconicevolution.dust", -10 + x, 0, DEFeatures.draconiumDust, null).initIndependentStat().setSpecial(), "pickup");
//        addAchievement("draconicevolution.ingot", new Achievement("draconicevolution.ingot", "draconicevolution.ingot", -8 + x, 0, DEFeatures.draconiumIngot, getAchievement("draconicevolution.dust")), "smelt");
//        addAchievement("draconicevolution.core", new Achievement("draconicevolution.core", "draconicevolution.core", -6 + x, 0, DEFeatures.draconicCore, getAchievement("draconicevolution.ingot")), "craftpickup");
//        addAchievement("draconicevolution.core2", new Achievement("draconicevolution.core2", "draconicevolution.core2", -4 + x, 0, DEFeatures.wyvernCore, getAchievement("draconicevolution.core")).setSpecial(), "craftpickup");
//
//        addAchievement("draconicevolution.dislocator", new Achievement("draconicevolution.dislocator", "draconicevolution.dislocator", -8 + x, 1, DEFeatures.dislocator, getAchievement("draconicevolution.dust")), "craft");
////		addAchievement("draconicevolution.chest", new Achievement("draconicevolution.chest", "draconicevolution.chest", -5+x, 2, DEFeatures.draconiumChest, getAchievement("draconicevolution.core")), "craft");
//        addAchievement("draconicevolution.particles", new Achievement("draconicevolution.particles", "draconicevolution.particles", -7 + x, 2, DEFeatures.particleGenerator, getAchievement("draconicevolution.core")), "craft");
//        addAchievement("draconicevolution.weather", new Achievement("draconicevolution.weather", "draconicevolution.weather", -5 + x, -2, DEFeatures.celestialManipulator, getAchievement("draconicevolution.core")), "craft");
//        addAchievement("draconicevolution.dissenchanter", new Achievement("draconicevolution.dissenchanter", "draconicevolution.dissenchanter", -7 + x, -2, DEFeatures.dissEnchanter, getAchievement("draconicevolution.core")), "craft");
//        addAchievement("draconicevolution.ecore", new Achievement("draconicevolution.ecore", "draconicevolution.ecore", -4 + x, -6, DEFeatures.energyStorageCore, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wpick", new Achievement("draconicevolution.wpick", "draconicevolution.wpick", -2 + x, 1, DEFeatures.wyvernPick, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wshovel", new Achievement("draconicevolution.wshovel", "draconicevolution.wshovel", -2 + x, -1, DEFeatures.wyvernShovel, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wsword", new Achievement("draconicevolution.wsword", "draconicevolution.wsword", -2 + x, 2, DEFeatures.wyvernSword, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wbow", new Achievement("draconicevolution.wbow", "draconicevolution.wbow", -2 + x, -2, DEFeatures.wyvernBow, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.whelm", new Achievement("draconicevolution.whelm", "draconicevolution.whelm", -2 + x, -4, DEFeatures.wyvernHelm, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wchest", new Achievement("draconicevolution.wchest", "draconicevolution.wchest", -2 + x, -3, DEFeatures.wyvernChest, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wleggs", new Achievement("draconicevolution.wleggs", "draconicevolution.wleggs", -2 + x, 3, DEFeatures.wyvernLegs, getAchievement("draconicevolution.core2")), "craftpickup");
//        addAchievement("draconicevolution.wboots", new Achievement("draconicevolution.wboots", "draconicevolution.wboots", -2 + x, 4, DEFeatures.wyvernBoots, getAchievement("draconicevolution.core2")), "craftpickup");
////		addAchievement("draconicevolution.resurrection", new Achievement("draconicevolution.resurrection", "draconicevolution.resurrection", -4+x, 6, DEFeatures.resurrectionStone, getAchievement("draconicevolution.core2")), "craft");
//        addAchievement("draconicevolution.dislocator2", new Achievement("draconicevolution.dislocator2", "draconicevolution.dislocator2", -2 + x, -5, DEFeatures.dislocatorAdvanced, getAchievement("draconicevolution.core2")), "craft");
//        addAchievement("draconicevolution.flux", new Achievement("draconicevolution.flux", "draconicevolution.flux", -2 + x, 5, DEFeatures.wyvernCapacitor, getAchievement("draconicevolution.core2")), "craftpickup");
//
//        addAchievement("draconicevolution.heart", new Achievement("draconicevolution.heart", "draconicevolution.heart", 0 + x, 0, DEFeatures.dragonHeart, getAchievement("draconicevolution.core2")).setSpecial(), "pickup");
//        addAchievement("draconicevolution.awakenedblock", new Achievement("draconicevolution.awakenedblock", "draconicevolution.awakenedblock", 2 + x, 0, DEFeatures.draconicBlock, getAchievement("draconicevolution.heart")), "craftpickup");
//        addAchievement("draconicevolution.core3", new Achievement("draconicevolution.core3", "draconicevolution.core3", 4 + x, 0, DEFeatures.awakenedCore, getAchievement("draconicevolution.awakenedblock")).setSpecial(), "craftpickup");
//
//        addAchievement("draconicevolution.dhelm", new Achievement("draconicevolution.dhelm", "draconicevolution.dhelm", 2 + x, -3, DEFeatures.draconicHelm, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dChest", new Achievement("draconicevolution.dChest", "draconicevolution.dChest", 2 + x, -2, DEFeatures.draconicChest, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dleggs", new Achievement("draconicevolution.dleggs", "draconicevolution.dleggs", 2 + x, 2, DEFeatures.draconicLegs, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dboots", new Achievement("draconicevolution.dboots", "draconicevolution.dboots", 2 + x, 3, DEFeatures.draconicBoots, getAchievement("draconicevolution.core3")), "craftpickup");
//
//        addAchievement("draconicevolution.dpick", new Achievement("draconicevolution.dpick", "draconicevolution.dpick", 6 + x, 0, DEFeatures.draconicPick, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dshovel", new Achievement("draconicevolution.dshovel", "draconicevolution.dshovel", 6 + x, -1, DEFeatures.draconicShovel, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.daxe", new Achievement("draconicevolution.daxe", "draconicevolution.daxe", 6 + x, 1, DEFeatures.draconicAxe, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dsword", new Achievement("draconicevolution.dsword", "draconicevolution.dsword", 6 + x, -2, DEFeatures.draconicSword, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dbow", new Achievement("draconicevolution.dbow", "draconicevolution.dbow", 6 + x, 2, DEFeatures.draconicBow, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.flux2", new Achievement("draconicevolution.flux2", "draconicevolution.flux2", 6 + x, -3, DEFeatures.draconicCapacitor, getAchievement("draconicevolution.core3")), "craftpickup");
//        addAchievement("draconicevolution.dhoe", new Achievement("draconicevolution.dhoe", "draconicevolution.dhoe", 6 + x, 3, DEFeatures.draconicHoe, getAchievement("draconicevolution.core3")), "craftpickup");
//
//        addAchievement("draconicevolution.dstaff", new Achievement("draconicevolution.dstaff", "draconicevolution.dstaff", 8 + x, 0, DEFeatures.draconicStaffOfPower, getAchievement("draconicevolution.dpick")).setSpecial(), "craftpickup");
//
//        ItemStack mobSoul = new ItemStack(DEFeatures.mobSoul);
//        ItemNBTHelper.setString(mobSoul, "Name", "[Random-Display]");
//        addAchievement("draconicevolution.soul", new Achievement("draconicevolution.soul", "draconicevolution.soul", 0 + x, -2, mobSoul, null).initIndependentStat(), "null");
//        addAchievement("draconicevolution.manual", new Achievement("draconicevolution.manual", "draconicevolution.manual", -8 + x, -1, DEFeatures.infoTablet, getAchievement("draconicevolution.dust")), "craft");
//    }
//

    public static void registerAchievementPane() {
//        Achievement[] achievements = new Achievement[achievementsList.size()];
//
//        achievements = achievementsList.values().toArray(achievements);
//        achievementsPage = new AchievementPage(I18n.translateToLocal("draconicevolution.achievementPage.name"), achievements);
//        AchievementPage.registerAchievementPage(achievementsPage);
    }

    @SubscribeEvent
    public void entityPickupEvent(EntityItemPickupEvent event) {
//        ItemStack stack = event.getItem().getEntityItem().copy();
//        stack.setCount(1);
//        if (achievementItems.containsKey(stack.getUnlocalizedName()) && achievementItems.get(stack.getUnlocalizedName()).isCorrectCondition("pickup")) {
//            triggerAchievement(event.getEntityPlayer(), achievementItems.get(stack.getUnlocalizedName()).getName());
//        }
    }

//    @SubscribeEvent
//    public void craftEvent(PlayerEvent.ItemCraftedEvent event) {
//        ItemStack stack = event.getCrafting().copy();
//        stack.setCount(1);
//        if (achievementItems.containsKey(stack.getDescriptionId()) && achievementItems.get(stack.getDescriptionId()).isCorrectCondition("craft")) {
//            triggerAchievement(event.getPlayer(), achievementItems.get(stack.getDescriptionId()).getName());
//        }
//    }
//
//    @SubscribeEvent
//    public void smeltEvent(PlayerEvent.ItemSmeltedEvent event) {
//        ItemStack stack = event.getSmelting().copy();
//        stack.setCount(1);
//        if (achievementItems.containsKey(stack.getDescriptionId()) && achievementItems.get(stack.getDescriptionId()).isCorrectCondition("smelt")) {
//            triggerAchievement(event.getPlayer(), achievementItems.get(stack.getDescriptionId()).getName());
//        }
//    }

    private static class AchievementCondition {
        private final String name;
        public final String condition;

        public AchievementCondition(String name, String condition) {
            this.name = name;
            this.condition = condition;
        }

        public boolean isCorrectCondition(String s) {
            return s.contains(condition);
        }

        public String getName() {
            return name;
        }
    }

}
