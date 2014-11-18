package com.brandon3055.draconicevolution.common.achievements;

import com.brandon3055.draconicevolution.common.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

import java.util.HashMap;

/**
 * Created by Brandon on 18/11/2014.
 */
public class Achievements {

	private static AchievementPage achievementsPage;
	private static HashMap<String, Achievement> achievementsList = new HashMap<String, Achievement>();

	public static void addAchievement (String name, Achievement achievement)
	{
		achievementsList.put(name, achievement.registerStat());
	}

	public static Achievement getAchievement (String name)
	{
		return achievementsList.get(name);
	}

	public static void triggerAchievement (EntityPlayer player, String name)
	{

		Achievement ach = getAchievement(name);

		if (ach != null)
		{
			player.triggerAchievement(ach);
		}
	}

	public static void addModAchievements()
	{
		addAchievement("draconicevolution.dust", new Achievement("draconicevolution.dust", "draconicevolution.dust", 0, 0, ModItems.draconiumDust, null).initIndependentStat()); //Mine Draconium Ore (pick up dust)
		addAchievement("draconicevolution", new Achievement("draconicevolution", "draconicevolution", 2, 1, ModItems.draconiumDust, getAchievement("draconicevolution")));



//		addAchievement("tconstruct.pattern", new Achievement("tconstruct.pattern", "tconstruct.pattern", 2, 1, TinkerTools.blankPattern, getAchievement("tconstruct.beginner")));
//		addAchievement("tconstruct.tinkerer", new Achievement("tconstruct.tinkerer", "tconstruct.tinkerer", 2, 2, new ItemStack(TinkerTools.titleIcon, 1, 4096), getAchievement("tconstruct.pattern")));
//		addAchievement("tconstruct.preparedFight", new Achievement("tconstruct.preparedFight", "tconstruct.preparedFight", 1, 3, new ItemStack(TinkerTools.titleIcon, 1, 4097), getAchievement("tconstruct.tinkerer")));
//		addAchievement("tconstruct.proTinkerer", new Achievement("tconstruct.proTinkerer", "tconstruct.proTinkerer", 4, 3, new ItemStack(TinkerTools.titleIcon, 1, 4098), getAchievement("tconstruct.tinkerer")));
//		addAchievement("tconstruct.smelteryMaker", new Achievement("tconstruct.smelteryMaker", "tconstruct.smelteryMaker", -2, -1, TinkerSmeltery.smeltery, getAchievement("tconstruct.beginner")));
//		addAchievement("tconstruct.enemySlayer", new Achievement("tconstruct.enemySlayer", "tconstruct.enemySlayer", 0, 5, new ItemStack(TinkerTools.titleIcon, 1, 4099), getAchievement("tconstruct.preparedFight")));
//		addAchievement("tconstruct.dualConvenience", new Achievement("tconstruct.dualConvenience", "tconstruct.dualConvenience", 0, 7, new ItemStack(TinkerTools.titleIcon, 1, 4100), getAchievement("tconstruct.enemySlayer")).setSpecial());
	}

	public static void registerAchievementPane ()
	{
		Achievement[] achievements = new Achievement[achievementsList.size()];

		achievements = achievementsList.values().toArray(achievements);
		achievementsPage = new AchievementPage(StatCollector.translateToLocal("draconicevolution.achievementPage.name"), achievements);
		AchievementPage.registerAchievementPage(achievementsPage);
	}
}
