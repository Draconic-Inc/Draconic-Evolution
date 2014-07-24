package com.brandon3055.draconicevolution.common.core.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static int disableSunDial;
	public static int teleporterUsesPerPearl;
	public static int disableXrayBlock;
	public static boolean bowBlockDamage;
	public static String[] spawnerList;
	public static boolean spawnerListType;
	public static int soulDropChance;
	public static int dislocator_Min_Range;
	public static int dislocator_Max_Range;
	public static int admin_dislocator_Min_Range;
	public static int admin_dislocator_Max_Range;
	public static int admin_dislocator_Detect_Range;
	public static int disable_LRD;

	private static String[] defaultList = new String[] {"ExampleMob1", "ExampleMob2", "ExampleMob3 (these examples can be deleted)"};


	public static void init(File confFile) {
		Configuration config = new Configuration(confFile);

		try {
			config.load();

			disableSunDial = config.get(Configuration.CATEGORY_GENERAL, "Disable Sun Dial", 0, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			disableXrayBlock = config.get(Configuration.CATEGORY_GENERAL, "Disable Xray Block", 0, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			teleporterUsesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "Teleporter Uses PerPearl", 10, "Charm of Dislocation uses per Ender pearl").getInt(10);
			bowBlockDamage = config.get(Configuration.CATEGORY_GENERAL, "Bow Block Damage", true, "Dose Draconic bow explosion damage blocks").getBoolean(true);
			spawnerListType = config.get("Spawner", "listType", false, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)").getBoolean(false);
			spawnerList = config.getStringList("Spawn List", "Spawner", defaultList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");
			soulDropChance = config.get("Spawner", "soulDropChance", 1000, "Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(1000);

			//dislocator_Min_Range = config.get("Long Range Dislocator", "Admin Dislocator Min Range", 1000000).getInt(1000000);
			//dislocator_Max_Range = config.get("Long Range Dislocator", "Admin Dislocator Max Range", 29000000).getInt(29000000);
			admin_dislocator_Min_Range = config.get("Long Range Dislocator", "Dislocator Min Range", 1000).getInt(1000);
			admin_dislocator_Max_Range = config.get("Long Range Dislocator", "Dislocator Max Range", 10000).getInt(10000);
			admin_dislocator_Detect_Range = config.get("Long Range Dislocator", "Admin Dislocator Detect Range", 5).getInt(5);
			disable_LRD = config.get("Long Range Dislocator", "Disable Dislocator", 0, "Disable Long Range Dislocator 0:Default, 1:Disable recipe, 2:Disable completely (Includes Admin Dislocator)").getInt(0);
		}
		catch (Exception e) {
			//DraconicEvolution.logger.error("Unable to load Config");
		}
		finally {
			config.save();
		}
	}
}
