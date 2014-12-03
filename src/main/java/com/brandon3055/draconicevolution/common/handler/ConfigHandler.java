package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

	public static Configuration config;

	//GENERAL
	public static int disableSunDial;
	public static int teleporterUsesPerPearl;
	public static int disableXrayBlock;
	public static int soulDropChance;
	public static int passiveSoulDropChance;
	public static int cometRarity;
	public static String[] obliterationList;
	public static boolean generateEnderComets;
	public static boolean generateChaosIslands;
	public static boolean pigmenBloodRage;
	public static boolean bowBlockDamage;
	public static boolean showUnlocalizedNames;
	public static boolean disableLore;
	public static boolean invertDPDSB;

	//spawner
	public static String[] spawnerList;
	public static boolean spawnerListType;

	//long range dislocator
	public static int dislocator_Min_Range;
	public static int dislocator_Max_Range;
	public static int admin_dislocator_Min_Range;
	public static int admin_dislocator_Max_Range;
	public static int admin_dislocator_Detect_Range;
	public static int disable_LRD;

	//Potion IDs

	//Enchantments
	public static int reaperEnchantID;


	private static String[] defaultSpawnerList = new String[] {"ExampleMob1", "ExampleMob2", "ExampleMob3 (these examples can be deleted)"};
	private static String[] defaultObliterationList = new String[] {"tile.stonebrick", "tile.gravel", "tile.dirt", "tile.stone", "tile.dirt", "tile.sandStone", "tile.sand", "tile.grass", "tile.hellrock"};

	public static void init(File confFile) {
		if (config == null) {
			config = new Configuration(confFile);
			syncConfig();
		}
	}

	public static void syncConfig() {


		try {
			//General
			disableSunDial = config.get(Configuration.CATEGORY_GENERAL, "Disable Sun Dial", 0, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			disableXrayBlock = config.get(Configuration.CATEGORY_GENERAL, "Disable Xray Block", 0, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			teleporterUsesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "Teleporter Uses PerPearl", 1, "Charm of Dislocation uses per Ender pearl").getInt(1);
			bowBlockDamage = config.get(Configuration.CATEGORY_GENERAL, "Bow Block Damage", true, "Dose Draconic bow explosion damage blocks").getBoolean(true);
			obliterationList = config.getStringList("Oblit Mode List", Configuration.CATEGORY_GENERAL, defaultObliterationList, "List of block (unlocalized)names that will be destroyed by tools in obliteration mode. To find the unlocalized name of a block see the \"Show Unlocalized Names\" config option");
			showUnlocalizedNames = config.get(Configuration.CATEGORY_GENERAL, "Show Unlocalized Names", false, "If set to true the unlocalized name of every block and item will be displayed in its tool tip").getBoolean(false);
			soulDropChance = config.get(Configuration.CATEGORY_GENERAL, "soulDropChance", 1000, "Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(1000);
			passiveSoulDropChance = config.get(Configuration.CATEGORY_GENERAL, "passiveSoulDropChance", 800, "Passive (Animals) Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(800);
			cometRarity = config.get(Configuration.CATEGORY_GENERAL, "Ender Comet Rarity", 10000, "Ender Comet has a 1 in {this number} chance to spawn in each chunk").getInt(10000);
			generateEnderComets = config.get(Configuration.CATEGORY_GENERAL, "Generate Ender Comets", true, "Should Ender comets be generated").getBoolean(true);
			generateChaosIslands = config.get(Configuration.CATEGORY_GENERAL, "Generate Chaos Islands", true, "Should Chaos Islands be generated").getBoolean(true);
			pigmenBloodRage = config.get(Configuration.CATEGORY_GENERAL, "Pigmen Blood Rage", true, "Is Pigmen blood rage active").getBoolean(true);
			disableLore = config.get(Configuration.CATEGORY_GENERAL, "Disable Item Lore", false, "Set to true to disable all item lore").getBoolean(false);
			invertDPDSB = config.get(Configuration.CATEGORY_GENERAL, "InvertDPDSB", false, "Invert Dislocator Pedestal display name shift behavior").getBoolean(false);

			//Spawner
			spawnerListType = config.get("spawner", "listType", false, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)").getBoolean(false);
			spawnerList = config.getStringList("Spawn List", "spawner", defaultSpawnerList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");

			//LRD
			admin_dislocator_Min_Range = config.get("long range dislocator", "Dislocator Min Range", 1000).getInt(1000);
			admin_dislocator_Max_Range = config.get("long range dislocator", "Dislocator Max Range", 10000).getInt(10000);
			admin_dislocator_Detect_Range = config.get("long range dislocator", "Admin Dislocator Detect Range", 5).getInt(5);
			disable_LRD = config.get("long range dislocator", "Disable Dislocator", 0, "Disable Long Range Dislocator 0:Default, 1:Disable recipe, 2:Disable completely (Includes Admin Dislocator)").getInt(0);

			//Potions
//			potionFlightID = config.get("magic id's", "potionFlightID", 50).getInt(50);

			//Enchantments
			reaperEnchantID = config.get("magic id's", "Reaper Enchant id", 180).getInt(180);
		}
		catch (Exception e) {
			LogHelper.error("Unable to load Config");
			e.printStackTrace();
		}
		finally {
			if (config.hasChanged()) config.save();
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
			spawnerList = config.getStringList("Spawn List", "spawner", defaultSpawnerList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");
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
