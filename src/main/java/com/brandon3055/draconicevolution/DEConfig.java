package com.brandon3055.draconicevolution;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class DEConfig {

    private static ConfigTag config;
    private static ConfigTag clientTag;
    private static ConfigTag serverTag;

    public static void load() {
        config = new StandardConfigFile(Paths.get("./config/brandon3055/DraconicEvolution.cfg")).load();
        loadServer();
        loadClient();
        EquipCfg.loadConfig(config);
        ModuleCfg.loadConfig(config);
        config.runSync();
        config.save();
    }


    //Server properties
    public static String serverID;
    public static double armorSpeedLimit;
    public static boolean enableElytraFlight;
    public static boolean enableCreativeFlight;
    public static boolean enableOreEnd;
    public static boolean enableOreOverworld;
    public static boolean enableOreNether;
    public static int dislocatorBlinkRange;
    public static int dislocatorBlinksPerPearl;
    public static int fusionInjectorRange;
    public static int fusionInjectorMinDist;
    public static List<Integer> fusionChargeTime;
    public static List<Integer> fusionCraftTime;
    public static int guardianCrystalShield;
    public static int guardianCrystalUnstableWindow;
    public static boolean chaoticBypassCrystalShield;
    public static int guardianHealth;
    public static int guardianShield;
    public static List<String> projectileAntiImmuneEntities;
    public static int dislocatorMaxFuel;
    public static int portalMaxArea;
    public static int portalMaxDistanceSq;
    public static int chaosDropCount;
    public static int dragonDustLootModifier;
    public static boolean dragonEggSpawnOverride;

    public static double reactorOutputMultiplier = 10;
    public static double reactorFuelUsageMultiplier = 5;
    public static double reactorExplosionScale = 1;
    public static boolean disableLargeReactorBoom = false;

    public static int soulDropChance = 1000;
    public static int passiveSoulDropChance = 800;
    public static String[] spawnerList = {};
    public static boolean spawnerListWhiteList = false;
    public static boolean allowBossSouls = false;
    public static Integer[] spawnerDelays = new Integer[]{200, 800, 100, 400, 50, 200, 25, 100};

    private static void loadServer() {
        serverTag = config.getTag("Server");
        ConfigTag serverIDTag = serverTag.getTag("serverID")
                .setSyncToClient()
                .setComment("This is a randomly generated id that clients will use to map their tool config settings to this server.")
                .setDefaultString(UUID.randomUUID().toString());
        serverIDTag.setSyncCallback((tag, type) -> serverID = tag.getString());

        serverTag.getTag("armorSpeedLimit")
                .setSyncToClient()
                .setComment("This can be used to limit the maximum speed boost allowed by the modular armor.", "A value of for example 1 means a maximum boost of +100%% over default character speed.", "Set to -1 for no limit, Default: 16 (+1600%%)")
                .setDefaultDouble(16)
                .setSyncCallback((tag, type) -> armorSpeedLimit = tag.getDouble());

        serverTag.getTag("enableElytraFlight")
                .setSyncToClient()
                .setComment("Allows you to disable elytra flight supplied by DE's armor")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> enableElytraFlight = tag.getBoolean());

        serverTag.getTag("enableCreativeFlight")
                .setSyncToClient()
                .setComment("Allows you to disable creative flight supplied by DE's armor")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> enableCreativeFlight = tag.getBoolean());

        serverTag.getTag("enableOreEnd")
                .setSyncToClient()
                .setComment("Allows you to disable draconium ore generation in the End")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> enableOreEnd = tag.getBoolean());
        serverTag.getTag("enableOreOverworld")
                .setSyncToClient()
                .setComment("Allows you to disable draconium ore generation in the Overworld")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> enableOreOverworld = tag.getBoolean());
        serverTag.getTag("enableOreNether")
                .setSyncToClient()
                .setComment("Allows you to disable draconium ore generation in the Nether")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> enableOreNether = tag.getBoolean());

        serverTag.getTag("dislocatorBlinkRange")
                .setSyncToClient()
                .setComment("Sets the maximum blink range for the Advanced Dislocator")
                .setDefaultInt(32)
                .setSyncCallback((tag, type) -> dislocatorBlinkRange = tag.getInt());

        serverTag.getTag("dislocatorBlinksPerPearl")
                .setSyncToClient()
                .setComment("Sets the blinks to regular fuel ratio. Default 1 regular fuel (1 pearl) allows 4 blinks.")
                .setDefaultInt(4)
                .setSyncCallback((tag, type) -> dislocatorBlinksPerPearl = tag.getInt());

        serverTag.getTag("fusionInjectorRange")
                .setSyncToClient()
                .setComment("Sets how far fusion crafting injectors can be from the fusion crafting core")
                .setDefaultInt(16)
                .setSyncCallback((tag, type) -> fusionInjectorRange = tag.getInt());
        serverTag.getTag("fusionInjectorMinDist")
                .setSyncToClient()
                .setComment("Sets the minimum distance a fusion injector must be from the fusion crafting core.")
                .setDefaultInt(2)
                .setSyncCallback((tag, type) -> fusionInjectorMinDist = tag.getInt());
        serverTag.getTag("fusionChargeTime")
                .setSyncToClient()
                .setComment("Time in ticks required for charging phase of fusion crafting with each injector tier. Draconium, Wyvern, Draconic, Chaotic")
                .setDefaultIntList(Lists.newArrayList(300, 220, 140, 60))
                .setSyncCallback((tag, type) -> fusionChargeTime = tag.getIntList());
        serverTag.getTag("fusionCraftTime")
                .setSyncToClient()
                .setComment("Time in ticks required for crafting phase of fusion crafting with each injector tier. Draconium, Wyvern, Draconic, Chaotic\nThe time selected is based on the lowest tier injector used in the craft.\nMax value for any of these is 32,767 (27.3 minutes)")
                .setDefaultIntList(Lists.newArrayList(300, 220, 140, 60))
                .setSyncCallback((tag, type) -> fusionCraftTime = tag.getIntList());

        serverTag.getTag("projectileAntiImmuneEntities")
                .setSyncToClient()
                .setComment("This is a list of entities that the \"Projectile Immunity Cancellation\" module will work on. Add additional entities as required. (Let me know if i missed any)")
                .setDefaultStringList(Lists.newArrayList("minecraft:enderman", "minecraft:wither", "minecraft:ender_dragon", "draconicevolution:guardian_wither"))
                .setSyncCallback((tag, type) -> projectileAntiImmuneEntities = tag.getStringList());

        {
            ConfigTag guardianFight = serverTag.getTag("Guardian Fight");
            guardianFight.setComment("Config values related to the chaos guardian fight.\nThe default values of -99 are markers indicating the internal hard coded value should be used.\nThis allows these values to be updated between mod versions for balance adjustments. Setting them to anything other than -99 will override the internal values.");
            guardianFight.getTag("guardianCrystalShield")
                    .setSyncToClient()
                    .setComment("Sets the base shield strength for chaos guardian crystals.\nDefault: 512")
                    .setDefaultInt(-99)
                    .setSyncCallback((tag, type) -> guardianCrystalShield = tag.getInt() == -99 ? 512 : tag.getInt());
            guardianFight.getTag("guardianCrystalUnstableWindow")
                    .setSyncToClient()
                    .setComment("Sets how long the guarian crystal's shield will be unstable after receiving damage from the chaos guardian\nDefault: 200 (10 seconds)")
                    .setDefaultInt(-99)
                    .setSyncCallback((tag, type) -> guardianCrystalUnstableWindow = tag.getInt() == -99 ? 200 : tag.getInt());
            guardianFight.getTag("guardianHealth")
                    .setSyncToClient()
                    .setComment("Sets the guardians base health value (After you break through the guardians shield)\nDefault: 1000")
                    .setDefaultInt(-99)
                    .setSyncCallback((tag, type) -> guardianHealth = tag.getInt() == -99 ? 1000 : tag.getInt());
            guardianFight.getTag("guardianShield")
                    .setSyncToClient()
                    .setComment("Sets the guardians shield capacity (You will need to break through this after disabling the guardian crystals)\nKeep in mind there is no limit to how fast you can hit the guardians shield so this will melt with a high damage rapid fire bow.\nDefault: 16000")
                    .setDefaultInt(-99)
                    .setSyncCallback((tag, type) -> guardianShield = tag.getInt() == -99 ? 16000 : tag.getInt());
            guardianFight.getTag("chaoticBypassCrystalShield")
                    .setSyncToClient()
                    .setComment("Allows chaotic weapons to destabilize the guardian crystal shields.\nThis makes it much easier to farm the guardian but only after you have chaos tier weapons.")
                    .setDefaultBoolean(true)
                    .setSyncCallback((tag, type) -> chaoticBypassCrystalShield = tag.getBoolean());
            guardianFight.getTag("chaosDropCount")
                    .setSyncToClient()
                    .setComment("Number of chaos shards dropped by the chaos crystal when broken by a player")
                    .setDefaultInt(5)
                    .setSyncCallback((tag, type) -> chaosDropCount = tag.getInt());
        }

        {
            ConfigTag deSpawner = serverTag.getTag("Stabilized Spawner");
            deSpawner.setComment("These are all config fields related to the Stabilized Spawner and mob souls");
            deSpawner.getTag("soulDropChance")
                    .setSyncToClient()
                    .setComment("Mobs have a 1 in {this number} chance to drop a soul when killed with the Reaper enchantment.  Note: This is the base value; higher enchantment levels increase this chance.")
                    .setDefaultInt(1000)
                    .setSyncCallback((tag, type) -> soulDropChance = tag.getInt());
            deSpawner.getTag("passiveSoulDropChance")
                    .setSyncToClient()
                    .setComment("Passive (Animals) Mobs have a 1 in {this number} chance to drop a soul when killed with the Reaper enchantment.  Note: This is the base value; higher enchantment levels increase this chance.")
                    .setDefaultInt(800)
                    .setSyncCallback((tag, type) -> passiveSoulDropChance = tag.getInt());
            deSpawner.getTag("spawnerList")
                    .setSyncToClient()
                    .setComment("By default, any entities added to this list will not drop their souls and will not be spawnable by the Stabilized Spawner.")
                    .setDefaultStringList(Collections.emptyList())
                    .setSyncCallback((tag, type) -> spawnerList = tag.getStringList().toArray(new String[0]));
            deSpawner.getTag("spawnerListWhiteList")
                    .setSyncToClient()
                    .setComment("Changes the spawner list to a whitelist instead of a blacklist.")
                    .setDefaultBoolean(false)
                    .setSyncCallback((tag, type) -> spawnerListWhiteList = tag.getBoolean());
            deSpawner.getTag("allowBossSouls")
                    .setSyncToClient()
                    .setComment("Enabling this allows boss souls to drop. Use with caution!")
                    .setDefaultBoolean(false)
                    .setSyncCallback((tag, type) -> allowBossSouls = tag.getBoolean());
            deSpawner.getTag("spawnerDelays")
                    .setSyncToClient()
                    .setComment("Sets the min and max spawn delay in ticks for each spawner tier. Order is as follows.\\nBasic MIN, MAX, Wyvern MIN, MAX, Draconic MIN, MAX, Chaotic MIN MAX")
                    .setDefaultIntList(Lists.newArrayList(200, 800, 100, 400, 50, 200, 25, 100))
                    .setSyncCallback((tag, type) -> spawnerDelays = tag.getIntList().toArray(new Integer[0]));
        }

        serverTag.getTag("dislocatorMaxFuel")
                .setSyncToClient()
                .setComment("Sets the maximum fuel that can be added to an Advanced Dislocator.")
                .setDefaultInt(1024)
                .setSyncCallback((tag, type) -> dislocatorMaxFuel = tag.getInt());

        serverTag.getTag("portalMaxArea")
                .setSyncToClient()
                .setComment("Sets maximum area (in blocks) for a DE portal, The default value 65536 is equivalent to a 256x256 portal")
                .setDefaultInt(65536)
                .setSyncCallback((tag, type) -> portalMaxArea = tag.getInt());

        serverTag.getTag("portalMaxDistance")
                .setSyncToClient()
                .setComment("This is more for sanity than actually limiting portal size. Sets the max distance a portal block can be from the receptacle")
                .setDefaultInt(256)
                .setSyncCallback((tag, type) -> portalMaxDistanceSq = tag.getInt() * tag.getInt());

        {
            ConfigTag reactor = serverTag.getTag("Reactor");
            reactor.setComment("These are all (server side) config fields related to the reactor");

            reactor.getTag("reactorOutputMultiplier")
                    .setSyncToClient()
                    .setComment("Adjusts the energy output multiplier of the reactor.")
                    .setDefaultDouble(1)
                    .setSyncCallback((tag, type) -> reactorOutputMultiplier = tag.getDouble());
            reactor.getTag("reactorFuelUsageMultiplier")
                    .setSyncToClient()
                    .setComment("Adjusts the fuel usage multiplier of the reactor.")
                    .setDefaultDouble(1)
                    .setSyncCallback((tag, type) -> reactorFuelUsageMultiplier = tag.getDouble());
            reactor.getTag("reactorExplosionScale")
                    .setSyncToClient()
                    .setComment("Allows you to adjust the overall scale of the reactor explosion. Use \\\"disableLargeReactorBoom\\\" to disable explosion completely.")
                    .setDefaultDouble(1)
                    .setSyncCallback((tag, type) -> reactorExplosionScale = tag.getDouble());
            reactor.getTag("disableLargeReactorBoom")
                    .setSyncToClient()
                    .setComment("If true, this will disable the massive reactor explosion and replace it with a much smaller one.")
                    .setDefaultBoolean(false)
                    .setSyncCallback((tag, type) -> disableLargeReactorBoom = tag.getBoolean());
        }

        serverTag.getTag("dragonDustLootModifier")
                .setSyncToClient()
                .setComment("This can be used to adjust the amount of Draconium Dust the Ender Dragon drops when killed.\nThe amount dropped will be this number +/- 10%%")
                .setDefaultInt(64)
                .setSyncCallback((tag, type) -> dragonDustLootModifier = tag.getInt());
        serverTag.getTag("dragonEggSpawnOverride")
                .setSyncToClient()
                .setComment("By default, the dragon egg only ever spawns once. This forces it to spawn every time the dragon is killed.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> dragonEggSpawnOverride = tag.getBoolean());
    }

    //Client properties
    public static boolean configUiShowUnavailable;
    public static boolean configUiEnableSnapping;
    public static boolean configUiEnableVisualization;
    public static boolean configUiEnableAddGroupButton;
    public static boolean configUiEnableDeleteZone;
    public static boolean configUiEnableAdvancedXOver;
    public static boolean fancyToolModels;
    public static boolean toolShaders;
    public static boolean crystalShaders;
    public static boolean reactorShaders;
    public static boolean guardianShaders;
    public static boolean otherShaders;
    public static boolean itemDislocatorSound;
    public static boolean creativeWarning;

    private static void loadClient() {
        clientTag = config.getTag("Client");
        clientTag.setComment("These are client side config properties.");

        clientTag.getTag("fancyToolModels")
                .setComment("Set this to false to disable the fancy 3D tool models. (Requires restart)")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> fancyToolModels = tag.getBoolean());
        clientTag.getTag("toolShaders")
                .setComment("Set this to false to disable tool shaders.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> toolShaders = tag.getBoolean());
        clientTag.getTag("crystalShaders")
                .setComment("Set this to false to disable crystal shaders.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> crystalShaders = tag.getBoolean());
        clientTag.getTag("reactorShaders")
                .setComment("Set this to false to disable reactor shaders.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> reactorShaders = tag.getBoolean());
        clientTag.getTag("guardianShaders")
                .setComment("Set this to false to disable chaos guardian shaders. (May visually break some stuff but could be useful if you are experiencing gl crashes.)")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> guardianShaders = tag.getBoolean());
        clientTag.getTag("otherShaders")
                .setComment("Set this to false to disable all other shaders.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> otherShaders = tag.getBoolean());

        ConfigTag itemConfigGui = clientTag.getTag("itemConfigGUI");
        itemConfigGui.setComment("These settings is accessible in game via the \"Configure Equipment\" gui.");
        itemConfigGui.getTag("showUnavailable")
                .setComment("Setting this to false will prevent properties from being displayed if their associated item is not in your inventory.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> configUiShowUnavailable = tag.getBoolean());
        itemConfigGui.getTag("enableSnapping")
                .setComment("Setting this to false will disable property window snapping.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> configUiEnableSnapping = tag.getBoolean());
        itemConfigGui.getTag("enableVisualization")
                .setComment("Setting this to false will disable the highlight/animation that occurs over a properties associated item when hovering over or editing a property.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> configUiEnableVisualization = tag.getBoolean());
        itemConfigGui.getTag("enableAddGroupButton")
                .setComment("Setting this to false will hide the \"Add Group\" button.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> configUiEnableAddGroupButton = tag.getBoolean());
        itemConfigGui.getTag("enableDeleteZone")
                .setComment("Setting this to false will hide the \"Delete Zone\"")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> configUiEnableDeleteZone = tag.getBoolean());
        itemConfigGui.getTag("enableAdvancedXOver")
                .setComment("If enabled your configured properties, property groups and presets will still be accessible when in the simple configuration mode.")
                .setDefaultBoolean(false)
                .setSyncCallback((tag, type) -> configUiEnableAdvancedXOver = tag.getBoolean());

        clientTag.getTag("itemDislocatorSound")
                .setComment("Enable / Disable item dislocator pickup sound")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> itemDislocatorSound = tag.getBoolean());

        clientTag.getTag("creativeWarning")
                .setComment("Set to false to disable the warning that is displayed when you switch to creative mode.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> creativeWarning = tag.getBoolean());
    }

    //Common properties

    private static void modifyProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        ConfigTag parent = config;
        for (String group : groupPath) {
            parent = parent.getTag(group);
        }
        ConfigTag tag = parent.getTag(name);
        modifyCallback.accept(tag);
        tag.runSync();
        tag.save();
    }

    public static void modifyClientProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Client"}, groupPath));
    }

    public static void modifyServerProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Server"}, groupPath));
    }

    public static void modifyCommonProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Common"}, groupPath));
    }
}
