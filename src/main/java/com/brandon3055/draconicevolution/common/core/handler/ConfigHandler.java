package com.brandon3055.draconicevolution.common.core.handler;

import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

	public static Configuration config;

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
	public static boolean updateFix;

	private static String[] defaultList = new String[] {"ExampleMob1", "ExampleMob2", "ExampleMob3 (these examples can be deleted)"};

	public static void init(File confFile) {
		if (config == null) {
			config = new Configuration(confFile);
			syncConfig();
		}
	}

	public static void syncConfig() {

		try {
			//config.load();

			disableSunDial = config.get(Configuration.CATEGORY_GENERAL, "Disable Sun Dial", 0, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			disableXrayBlock = config.get(Configuration.CATEGORY_GENERAL, "Disable Xray Block", 0, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			teleporterUsesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "Teleporter Uses PerPearl", 1, "Charm of Dislocation uses per Ender pearl").getInt(10);
			bowBlockDamage = config.get(Configuration.CATEGORY_GENERAL, "Bow Block Damage", true, "Dose Draconic bow explosion damage blocks").getBoolean(true);
			spawnerListType = config.get("spawner", "listType", false, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)").getBoolean(false);
			spawnerList = config.getStringList("Spawn List", "spawner", defaultList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");
			soulDropChance = config.get("spawner", "soulDropChance", 1000, "Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(1000);
			updateFix = config.get(Configuration.CATEGORY_GENERAL, "Update Fix", true, "Convert blocks from v0.9.2 to the v0.2.3+ format (set to false if you are not updating from v0.9.2 or earlier)").getBoolean(true);

			//dislocator_Min_Range = config.get("Long Range Dislocator", "Admin Dislocator Min Range", 1000000).getInt(1000000);
			//dislocator_Max_Range = config.get("Long Range Dislocator", "Admin Dislocator Max Range", 29000000).getInt(29000000);
			admin_dislocator_Min_Range = config.get("long range dislocator", "Dislocator Min Range", 1000).getInt(1000);
			admin_dislocator_Max_Range = config.get("long range dislocator", "Dislocator Max Range", 10000).getInt(10000);
			admin_dislocator_Detect_Range = config.get("long range dislocator", "Admin Dislocator Detect Range", 5).getInt(5);
			disable_LRD = config.get("long range dislocator", "Disable Dislocator", 0, "Disable Long Range Dislocator 0:Default, 1:Disable recipe, 2:Disable completely (Includes Admin Dislocator)").getInt(0);
		}
		catch (Exception e) {
			LogHelper.error("Unable to load Config");
			e.printStackTrace();
		}
		finally {
			config.save();
		}
	}
}
/*
{
			//config.load();

			disableSunDial = config.getInt("Disable Sun Dial", Configuration.CATEGORY_GENERAL, disableSunDial, 0, 2, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely");
			disableXrayBlock = config.getInt("Disable Xray Block", Configuration.CATEGORY_GENERAL, disableXrayBlock, 0, 2, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely");
			teleporterUsesPerPearl = config.getInt("Teleporter Uses PerPearl", Configuration.CATEGORY_GENERAL, teleporterUsesPerPearl, 0, Integer.MAX_VALUE, "Charm of Dislocation uses per Ender pearl");
			bowBlockDamage = config.getBoolean(Configuration.CATEGORY_GENERAL, "Bow Block Damage", bowBlockDamage, "Dose Draconic bow explosion damage blocks");
			spawnerListType = config.getBoolean("spawner", "listType", spawnerListType, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)");
			spawnerList = config.getStringList("Spawn List", "spawner", defaultList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");
			soulDropChance = config.getInt("soulDropChance", "spawner", soulDropChance, 1, Integer.MAX_VALUE, "Mobs have a 1 in this number chance to drop a soul");
			updateFix = config.getBoolean(Configuration.CATEGORY_GENERAL, "Update Fix", updateFix, "Convert blocks from v0.9.2 to the v0.2.3+ format (set to false if you are not updating from v0.9.2 or earlier)");

			//dislocator_Min_Range = config.get("Long Range Dislocator", "Admin Dislocator Min Range", 1000000).getInt(1000000);
			//dislocator_Max_Range = config.get("Long Range Dislocator", "Admin Dislocator Max Range", 29000000).getInt(29000000);
			admin_dislocator_Min_Range = config.getInt("Dislocator Min Range", "long range dislocator", admin_dislocator_Min_Range, 0, 100000, "");
			admin_dislocator_Max_Range = config.getInt("Dislocator Max Range", "long range dislocator", admin_dislocator_Max_Range, 0, 100000, "");
			admin_dislocator_Detect_Range = config.getInt("long range dislocator", "Admin Dislocator Detect Range", admin_dislocator_Detect_Range, 0, 100, "");
			disable_LRD = config.getInt("Disable Dislocator", "long range dislocator", disable_LRD, 0, 2, "Disable Long Range Dislocator 0:Default, 1:Disable recipe, 2:Disable completely (Includes Admin Dislocator)");
		}
 */
