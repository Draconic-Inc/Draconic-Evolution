package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

	public static Configuration config;

	//GENERAL
	public static int teleporterUsesPerPearl;
	public static int soulDropChance;
	public static int passiveSoulDropChance;
	public static int cometRarity;
	public static int hudX;
	public static int hudY;
	public static boolean generateEnderComets;
	public static boolean generateChaosIslands;
	public static boolean pigmenBloodRage;
	public static boolean bowBlockDamage;
	public static boolean showUnlocalizedNames;
	public static boolean disableLore;
	public static boolean invertDPDSB;
	public static boolean disableOreSpawnEnd;
	public static boolean disableOreSpawnOverworld;
	public static boolean disableOreSpawnNether;
	public static boolean enableHudDisplay;
	public static boolean enableVersionChecker;
	public static boolean dragonBreaksBlocks;
	public static int[] dragonEggSpawnLocation;
	public static int[] oreGenDimentionBlacklist;
	private static String[] disabledBlocksItems;
	public static List<String> disabledNamesList = new ArrayList<String>();
	public static double maxPlayerSpeed;
	private static int[] speedDimBlackList;
	public static List<Integer> speedLimitDimList = new ArrayList<Integer>();
	public static boolean speedLimitops;
	public static boolean rapidlyDespawnMinedItems;
	public static boolean useOldArmorModel;

	//spawner
	public static String[] spawnerList;
	public static boolean spawnerListType;

	//long range dislocator
	public static int dislocator_Min_Range;
	public static int dislocator_Max_Range;
	public static int admin_dislocator_Min_Range;
	public static int admin_dislocator_Max_Range;
	public static int admin_dislocator_Detect_Range;

	//Potion IDs

	//Enchantments
	public static int reaperEnchantID;

	//Reactor
	public static boolean enableReactorBigBoom;
	public static double reactorOutputMultiplier;
	public static double reactorFuelUsageMultiplier;


	private static String[] defaultSpawnerList = new String[] {"ExampleMob1", "ExampleMob2", "ExampleMob3 (these examples can be deleted)"};

	public static void init(File confFile) {
		if (config == null) {
			config = new Configuration(confFile);
			syncConfig();
		}
	}

	public static void syncConfig() {


		try {
			//General
//			disableSunDial = config.get(Configuration.CATEGORY_GENERAL, "Disable Sun Dial", 0, "Disable Sun Dial 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
//			disableXrayBlock = config.get(Configuration.CATEGORY_GENERAL, "Disable Xray Block", 0, "Disable Distortion Flame 0:Default, 1:Disable recipe, 2:Disable completely").getInt(0);
			teleporterUsesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "Teleporter Uses PerPearl", 1, "Charm of Dislocation uses per Ender pearl").getInt(1);
			bowBlockDamage = config.get(Configuration.CATEGORY_GENERAL, "Bow Block Damage", true, "Dose Draconic bow explosion damage blocks").getBoolean(true);
			showUnlocalizedNames = config.get(Configuration.CATEGORY_GENERAL, "Show Unlocalized Names", false, "If set to true the unlocalized name of every block and item will be displayed in its tool tip").getBoolean(false);
			soulDropChance = config.get(Configuration.CATEGORY_GENERAL, "soulDropChance", 1000, "Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(1000);
			passiveSoulDropChance = config.get(Configuration.CATEGORY_GENERAL, "passiveSoulDropChance", 800, "Passive (Animals) Mobs have a 1 in this number chance to drop a soul", 1, Integer.MAX_VALUE).getInt(800);
			cometRarity = config.get(Configuration.CATEGORY_GENERAL, "Ender Comet Rarity", 10000, "Ender Comet has a 1 in {this number} chance to spawn in each chunk").getInt(10000);
			generateEnderComets = config.get(Configuration.CATEGORY_GENERAL, "Generate Ender Comets", true, "Should Ender comets be generated").getBoolean(true);
			generateChaosIslands = config.get(Configuration.CATEGORY_GENERAL, "Generate Chaos Islands", true, "Should Chaos Islands be generated").getBoolean(true);
			pigmenBloodRage = config.get(Configuration.CATEGORY_GENERAL, "Pigmen Blood Rage", true, "Is Pigmen blood rage active").getBoolean(true);
			disableLore = config.get(Configuration.CATEGORY_GENERAL, "Disable Item Lore", false, "Set to true to disable all item lore").getBoolean(false);
			invertDPDSB = config.get(Configuration.CATEGORY_GENERAL, "InvertDPDSB", false, "Invert Dislocator Pedestal display name shift behavior").getBoolean(false);
			hudX = config.get(Configuration.CATEGORY_GENERAL, "Hud Display X pos", 7).getInt(7);
			hudY = config.get(Configuration.CATEGORY_GENERAL, "Hud Display Y pos", 874).getInt(874);
			disableOreSpawnEnd = config.get(Configuration.CATEGORY_GENERAL, "Disable Ore Spawn (End)", false, "Set to true to prevent draconium ore from spawning in the end").getBoolean(false);
			disableOreSpawnNether = config.get(Configuration.CATEGORY_GENERAL, "Disable Ore Spawn (Nether)", false, "Set to true to prevent draconium ore from spawning in the nether").getBoolean(false);
			disableOreSpawnOverworld = config.get(Configuration.CATEGORY_GENERAL, "Disable Ore Spawn (Overworld)", false, "Set to true to prevent draconium ore from spawning in the overworld").getBoolean(false);
			enableHudDisplay = config.get(Configuration.CATEGORY_GENERAL, "Enable HUD info", true, "Set to false to disable the HUD info for tools and blocks").getBoolean(true);
			enableVersionChecker = config.get(Configuration.CATEGORY_GENERAL, "Enable version checker", true, "Set to false to disable the version checker").getBoolean(true);
			dragonBreaksBlocks = config.get(Configuration.CATEGORY_GENERAL, "Can dragon break blocks", true, "Set to false to disable the DE dragons ability to break blocks (dose not effect vanilla dragon)").getBoolean(true);
			dragonEggSpawnLocation = config.get(Configuration.CATEGORY_GENERAL, "Dragon egg spawn location", new int[] {0, 0, 0}, "Sets the exact location to spawn the dragon egg and disables the portal spawn (dose not effect vanilla dragon)").getIntList();
			oreGenDimentionBlacklist = config.get(Configuration.CATEGORY_GENERAL, "Ore gen dimension blacklist", new int[0], "Add the id's of dimensions you do not want draconium ore to spawn in").getIntList();
			disabledBlocksItems = config.getStringList("Disabled Blocks & Items", Configuration.CATEGORY_GENERAL, new String[0], "add the unlocalized name of a block or item to this list to disable it");
			maxPlayerSpeed = config.get(Configuration.CATEGORY_GENERAL, "Player speed cap", 10D, "Limits the max speed of players. Recommend between 0.5 - 1.0 for servers").getDouble(10D);
			speedDimBlackList = config.get(Configuration.CATEGORY_GENERAL, "Speed limit Dim black lack list", new int[] {1}, "A list of dimensions the speed limit will not effect (speed limit is not so really required in the end)").getIntList();
			speedLimitops = config.get(Configuration.CATEGORY_GENERAL, "Speed limit effects ops", false, "Dose the speed limit effect ops").getBoolean(false);
			rapidlyDespawnMinedItems = config.get(Configuration.CATEGORY_GENERAL, "Rapidly despawn aoe mined items", false, "If true items dropped by a tool in aoe mode will despawn after 5 seconds").getBoolean(false);
			useOldArmorModel = config.get(Configuration.CATEGORY_GENERAL, "Use old armor model", false, "If true the armor will use the original vanilla 2D model instead of the new 3D models").getBoolean(false);

			//Spawner
			spawnerListType = config.get("spawner", "listType", false, "Sets weather the spawner list is a white list or a black list (true = white list false = black list)").getBoolean(false);
			spawnerList = config.getStringList("Spawn List", "spawner", defaultSpawnerList, "List of names that will be ether accepted or rejected by the spawner depending on the list type");

			//LRD
			admin_dislocator_Min_Range = config.get("long range dislocator", "Dislocator Min Range", 1000).getInt(1000);
			admin_dislocator_Max_Range = config.get("long range dislocator", "Dislocator Max Range", 10000).getInt(10000);
			admin_dislocator_Detect_Range = config.get("long range dislocator", "Admin Dislocator Detect Range", 5).getInt(5);

			//Potions
//			potionFlightID = config.get("magic id's", "potionFlightID", 50).getInt(50);

			//Enchantments
			reaperEnchantID = config.get("magicId's", "Reaper Enchant id", 180).getInt(180);

			//Reactor
			enableReactorBigBoom = config.get("Draconic Reactor", "EnableBigExplosion", true, "Setting this to false will reduce the reactor explosion to little more then a tnt blast").getBoolean(true);
			reactorFuelUsageMultiplier = config.get("Draconic Reactor", "FuelUsageMultiplier", 1, "Use this to adjust how quickly the reactor uses fuel", 0, 1000000).getDouble(1);
			reactorOutputMultiplier = config.get("Draconic Reactor", "EnergyOutputMultiplier", 1, "Use this to adjust the output of the reactor", 0, 1000000).getDouble(1);

			for (String s : disabledBlocksItems) disabledNamesList.add(s);
			for (int i : speedDimBlackList) speedLimitDimList.add(i);
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
