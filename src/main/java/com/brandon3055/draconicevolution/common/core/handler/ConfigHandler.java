package com.brandon3055.draconicevolution.common.core.handler;

import java.io.File;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static int disableSunDial;
	public static int teleporterUsesPerPearl;
	public static int disableXrayBlock;
	public static boolean bowBlockDamage;
	public static String[] spawnerList;
	public static boolean spawnerListType;
	public static int soulDropChance;

	private static String[] defaultList = new String[] {"ExampleMob1", "ExampleMob2", "ExampleMob3 (these examples can be deleted)"};


	public static void init(File confFile) {
		Configuration config = new Configuration(confFile);

		try {
			config.load();

			disableSunDial = config.get(Configuration.CATEGORY_GENERAL, "disableSunDial", 0, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			disableXrayBlock = config.get(Configuration.CATEGORY_GENERAL, "disableXrayBlock", 0, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			teleporterUsesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "teleporterUsesPerPearl", 10, "Charm of Dislocation uses per Ender pearl").getInt(10);
			bowBlockDamage = config.get(Configuration.CATEGORY_GENERAL, "bowBlockDamage", true, "Dose Draconic bow explosion damage blocks").getBoolean(true);
			spawnerListType = config.get("Spawner", "listType", false, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)").getBoolean(false);
			spawnerList = config.getStringList("Spawn List", "Spawner", defaultList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");
			soulDropChance = config.get("Spawner", "soulDropChance", 1000, "Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(1000);
		}
		catch (Exception e) {
			//DraconicEvolution.logger.error("Unable to load Config");
		}
		finally {
			config.save();
		}
	}
}
