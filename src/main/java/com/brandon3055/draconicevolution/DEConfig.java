package com.brandon3055.draconicevolution;

import codechicken.lib.config.*;
import com.brandon3055.draconicevolution.init.DEWorldGen;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class DEConfig {

    private static ConfigCategory config;
    private static ConfigCategory clientTag;
    public static ConfigCategory serverTag;

    public static void load() {
        config = new ConfigFile(DraconicEvolution.MODID)
                .path(Paths.get("./config/brandon3055/DraconicEvolution.cfg"))
                .load();
        loadServer();
        loadClient();
        EquipCfg.loadConfig(config);
        ModuleCfg.loadConfig(config);
        DEWorldGen.init(config);
        config.runSync(ConfigCallback.Reason.MANUAL);
        config.save();
    }


    //Server properties
    public static String serverID;
    public static double armorSpeedLimit;
    public static boolean enableElytraFlight;
    public static boolean enableCreativeFlight;
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
    public static boolean useToolTierTags;

    public static double reactorOutputMultiplier = 10;
    public static double reactorFuelUsageMultiplier = 5;
    public static double reactorExplosionScale = 1;
    public static boolean disableLargeReactorBoom = false;

    public static int grinderEnergyPerHeart;
    public static Set<String> grinderBlackList;
    public static boolean allowGrindingPlayers;

    public static int soulDropChance = 1000;
    public static int passiveSoulDropChance = 800;
    public static Set<String> spawnerList = new HashSet<>();
    public static boolean spawnerListWhiteList = false;
    public static boolean allowBossSouls = false;
    public static Integer[] spawnerDelays = new Integer[]{200, 800, 100, 400, 50, 200, 25, 100};
    public static Set<String> chestBlacklist = new HashSet<>();

    public static Long[] coreCapacity = new Long[]{45500000L, 273000000L, 1640000000L, 9880000000L, 59300000000L, 356000000000L, 2140000000000L, -1L};

    private static void loadServer() {
        serverTag = config.getCategory("Server");
        ConfigValue serverIDTag = serverTag.getValue("serverID")
                .syncTagToClient()
                .setComment("This is a randomly generated id that clients will use to map their tool config settings to this server.")
                .setDefaultString(UUID.randomUUID().toString());
        serverIDTag.onSync((tag, type) -> serverID = tag.getString());

        serverTag.getValue("armorSpeedLimit")
                .syncTagToClient()
                .setComment("This can be used to limit the maximum speed boost allowed by the modular armor.", "A value of for example 1 means a maximum boost of +100%% over default character speed.", "Set to -1 for no limit, Default: 16 (+1600%%)")
                .setDefaultDouble(16)
                .onSync((tag, type) -> armorSpeedLimit = tag.getDouble());

        serverTag.getValue("enableElytraFlight")
                .syncTagToClient()
                .setComment("Allows you to disable elytra flight supplied by DE's armor")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> enableElytraFlight = tag.getBoolean());

        serverTag.getValue("enableCreativeFlight")
                .syncTagToClient()
                .setComment("Allows you to disable creative flight supplied by DE's armor")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> enableCreativeFlight = tag.getBoolean());

        serverTag.getValue("dislocatorBlinkRange")
                .syncTagToClient()
                .setComment("Sets the maximum blink range for the Advanced Dislocator")
                .setDefaultInt(32)
                .onSync((tag, type) -> dislocatorBlinkRange = tag.getInt());

        serverTag.getValue("dislocatorBlinksPerPearl")
                .syncTagToClient()
                .setComment("Sets the blinks to regular fuel ratio. Default 1 regular fuel (1 pearl) allows 4 blinks.")
                .setDefaultInt(4)
                .onSync((tag, type) -> dislocatorBlinksPerPearl = tag.getInt());

        serverTag.getValue("fusionInjectorRange")
                .syncTagToClient()
                .setComment("Sets how far fusion crafting injectors can be from the fusion crafting core")
                .setDefaultInt(16)
                .onSync((tag, type) -> fusionInjectorRange = tag.getInt());
        serverTag.getValue("fusionInjectorMinDist")
                .syncTagToClient()
                .setComment("Sets the minimum distance a fusion injector must be from the fusion crafting core.")
                .setDefaultInt(2)
                .onSync((tag, type) -> fusionInjectorMinDist = tag.getInt());
        serverTag.getValueList("fusionChargeTime")
                .syncTagToClient()
                .setComment("Time in ticks required for charging phase of fusion crafting with each injector tier. Draconium, Wyvern, Draconic, Chaotic")
                .setDefaultInts(Lists.newArrayList(300, 220, 140, 60))
                .onSync((tag, type) -> fusionChargeTime = tag.getInts());
        serverTag.getValueList("fusionCraftTime")
                .syncTagToClient()
                .setComment("Time in ticks required for crafting phase of fusion crafting with each injector tier. Draconium, Wyvern, Draconic, Chaotic\nThe time selected is based on the lowest tier injector used in the craft.\nMax value for any of these is 32,767 (27.3 minutes)")
                .setDefaultInts(Lists.newArrayList(300, 220, 140, 60))
                .onSync((tag, type) -> fusionCraftTime = tag.getInts());

        serverTag.getValueList("projectileAntiImmuneEntities")
                .syncTagToClient()
                .setComment("This is a list of entities that the \"Projectile Immunity Cancellation\" module will work on. Add additional entities as required. (Let me know if i missed any)")
                .setDefaultStrings(Lists.newArrayList("minecraft:enderman", "minecraft:wither", "minecraft:ender_dragon", "draconicevolution:guardian_wither"))
                .onSync((tag, type) -> projectileAntiImmuneEntities = tag.getStrings());

        {
            ConfigCategory guardianFight = serverTag.getCategory("Guardian Fight");
            guardianFight.setComment("Config values related to the chaos guardian fight.\nThe default values of -99 are markers indicating the internal hard coded value should be used.\nThis allows these values to be updated between mod versions for balance adjustments. Setting them to anything other than -99 will override the internal values.");
            guardianFight.getValue("guardianCrystalShield")
                    .syncTagToClient()
                    .setComment("Sets the base shield strength for chaos guardian crystals.\nDefault: 512")
                    .setDefaultInt(-99)
                    .onSync((tag, type) -> guardianCrystalShield = tag.getInt() == -99 ? 512 : tag.getInt());
            guardianFight.getValue("guardianCrystalUnstableWindow")
                    .syncTagToClient()
                    .setComment("Sets how long the guarian crystal's shield will be unstable after receiving damage from the chaos guardian\nDefault: 200 (10 seconds)")
                    .setDefaultInt(-99)
                    .onSync((tag, type) -> guardianCrystalUnstableWindow = tag.getInt() == -99 ? 200 : tag.getInt());
            guardianFight.getValue("guardianHealth")
                    .syncTagToClient()
                    .setComment("Sets the guardians base health value (After you break through the guardians shield)\nDefault: 1000")
                    .setDefaultInt(-99)
                    .onSync((tag, type) -> guardianHealth = tag.getInt() == -99 ? 1000 : tag.getInt());
            guardianFight.getValue("guardianShield")
                    .syncTagToClient()
                    .setComment("Sets the guardians shield capacity (You will need to break through this after disabling the guardian crystals)\nKeep in mind there is no limit to how fast you can hit the guardians shield so this will melt with a high damage rapid fire bow.\nDefault: 16000")
                    .setDefaultInt(-99)
                    .onSync((tag, type) -> guardianShield = tag.getInt() == -99 ? 16000 : tag.getInt());
            guardianFight.getValue("chaoticBypassCrystalShield")
                    .syncTagToClient()
                    .setComment("Allows chaotic weapons to destabilize the guardian crystal shields.\nThis makes it much easier to farm the guardian but only after you have chaos tier weapons.")
                    .setDefaultBoolean(true)
                    .onSync((tag, type) -> chaoticBypassCrystalShield = tag.getBoolean());
            guardianFight.getValue("chaosDropCount")
                    .syncTagToClient()
                    .setComment("Number of chaos shards dropped by the chaos crystal when broken by a player")
                    .setDefaultInt(5)
                    .onSync((tag, type) -> chaosDropCount = tag.getInt());
        }

        {
            ConfigCategory deSpawner = serverTag.getCategory("Stabilized Spawner");
            deSpawner.setComment("These are all config fields related to the Stabilized Spawner and mob souls");
            deSpawner.getValue("soulDropChance")
                    .syncTagToClient()
                    .setComment("Mobs have a 1 in {this number} chance to drop a soul when killed with the Reaper enchantment.  Note: This is the base value; higher enchantment levels increase this chance.")
                    .setDefaultInt(1000)
                    .onSync((tag, type) -> soulDropChance = tag.getInt());
            deSpawner.getValue("passiveSoulDropChance")
                    .syncTagToClient()
                    .setComment("Passive (Animals) Mobs have a 1 in {this number} chance to drop a soul when killed with the Reaper enchantment.  Note: This is the base value; higher enchantment levels increase this chance.")
                    .setDefaultInt(800)
                    .onSync((tag, type) -> passiveSoulDropChance = tag.getInt());
            deSpawner.getValueList("spawnerList")
                    .syncTagToClient()
                    .setComment("By default, any entities added to this list will not drop their souls and will not be spawnable by the Stabilized Spawner. Use entity registry name. e.g. minecraft:cow")
                    .setDefaultStrings(Collections.emptyList())
                    .onSync((tag, type) -> spawnerList = new HashSet<>(tag.getStrings()));
            deSpawner.getValue("spawnerListWhiteList")
                    .syncTagToClient()
                    .setComment("Changes the spawner list to a whitelist instead of a blacklist.")
                    .setDefaultBoolean(false)
                    .onSync((tag, type) -> spawnerListWhiteList = tag.getBoolean());
            deSpawner.getValue("allowBossSouls")
                    .syncTagToClient()
                    .setComment("Enabling this allows boss souls to drop. Use with caution!")
                    .setDefaultBoolean(false)
                    .onSync((tag, type) -> allowBossSouls = tag.getBoolean());
            deSpawner.getValueList("spawnerDelays")
                    .syncTagToClient()
                    .setComment("Sets the min and max spawn delay in ticks for each spawner tier. Order is as follows.\\nBasic MIN, MAX, Wyvern MIN, MAX, Draconic MIN, MAX, Chaotic MIN MAX")
                    .setDefaultInts(Lists.newArrayList(200, 800, 100, 400, 50, 200, 25, 100))
                    .onSync((tag, type) -> spawnerDelays = tag.getInts().toArray(new Integer[0]));
        }

        serverTag.getValue("dislocatorMaxFuel")
                .syncTagToClient()
                .setComment("Sets the maximum fuel that can be added to an Advanced Dislocator.")
                .setDefaultInt(1024)
                .onSync((tag, type) -> dislocatorMaxFuel = tag.getInt());

        serverTag.getValue("portalMaxArea")
                .syncTagToClient()
                .setComment("Sets maximum area (in blocks) for a DE portal, The default value 65536 is equivalent to a 256x256 portal")
                .setDefaultInt(65536)
                .onSync((tag, type) -> portalMaxArea = tag.getInt());

        serverTag.getValue("portalMaxDistance")
                .syncTagToClient()
                .setComment("This is more for sanity than actually limiting portal size. Sets the max distance a portal block can be from the receptacle")
                .setDefaultInt(256)
                .onSync((tag, type) -> portalMaxDistanceSq = tag.getInt() * tag.getInt());

        {
            ConfigCategory reactor = serverTag.getCategory("Reactor");
            reactor.setComment("These are all (server side) config fields related to the reactor");

            reactor.getValue("reactorOutputMultiplier")
                    .syncTagToClient()
                    .setComment("Adjusts the energy output multiplier of the reactor.")
                    .setDefaultDouble(1)
                    .onSync((tag, type) -> reactorOutputMultiplier = tag.getDouble());
            reactor.getValue("reactorFuelUsageMultiplier")
                    .syncTagToClient()
                    .setComment("Adjusts the fuel usage multiplier of the reactor.")
                    .setDefaultDouble(1)
                    .onSync((tag, type) -> reactorFuelUsageMultiplier = tag.getDouble());
            reactor.getValue("reactorExplosionScale")
                    .syncTagToClient()
                    .setComment("Allows you to adjust the overall scale of the reactor explosion. Use \\\"disableLargeReactorBoom\\\" to disable explosion completely.")
                    .setDefaultDouble(1)
                    .onSync((tag, type) -> reactorExplosionScale = tag.getDouble());
            reactor.getValue("disableLargeReactorBoom")
                    .syncTagToClient()
                    .setComment("If true, this will disable the massive reactor explosion and replace it with a much smaller one.")
                    .setDefaultBoolean(false)
                    .onSync((tag, type) -> disableLargeReactorBoom = tag.getBoolean());
        }

        serverTag.getValue("dragonDustLootModifier")
                .syncTagToClient()
                .setComment("This can be used to adjust the amount of Draconium Dust the Ender Dragon drops when killed.\nThe amount dropped will be this number +/- 10%%")
                .setDefaultInt(64)
                .onSync((tag, type) -> dragonDustLootModifier = tag.getInt());
        serverTag.getValue("dragonEggSpawnOverride")
                .syncTagToClient()
                .setComment("By default, the dragon egg only ever spawns once. This forces it to spawn every time the dragon is killed.")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> dragonEggSpawnOverride = tag.getBoolean());

        serverTag.getValueList("chestBlacklist")
                .syncTagToClient()
                .setComment("This is a blacklist of key words that can be used to prevent certain storage items from being stored in a draconium chest.\nIf the items registry name contains any or these strings it will not be allowed")
                .setDefaultStrings(Lists.newArrayList("draconium_chest", "shulker_box", "pouch", "bag", "strongbox"))
                .onSync((tag, type) -> chestBlacklist = Sets.newHashSet(tag.getStrings()));

        serverTag.getValue("useToolTierTags")
                .syncTagToClient()
                .setComment("The new tag based tool tier system makes it incredibly difficult to add over powered tools that can mine blocks of any harvest level. So for Draconic and Chaotic tier i simply dont use it.\n" +
                        "This means they can mine pretty much any minable block. Setting this to true will enable the tag system on these tiers and by default put them right along side wyvern at netherite tier.\n" +
                        "This may be useful for people like pack developers who want to add custom tool tier progression.")
                .setDefaultBoolean(false)
                .onSync((tag, type) -> useToolTierTags = tag.getBoolean());

        serverTag.getValueList("coreCapacity")
                .syncTagToClient()
                .setComment("Allows you to adjust the capacity of each energy core tier.", "Warning changing the number entries in this list will crash your game.", "For tier 8 -1 = BigInteger.MAX_VALUE * Long.MAX_VALUE, Otherwise the max you can specify is 9223372036854775807")
                .setDefaultLongs(Lists.newArrayList(coreCapacity))
                .setRestriction(new ListRestriction() {
                    @Override
                    public Optional<Failure> test(ConfigValueList values) {
                        if (values.getLongs().size() != 8) {
                            return Optional.of(new Failure(0, values.getLongs().size()));
                        }
                        return Optional.empty();
                    }

                    @Override
                    public String describe() {
                        return "[Requires 8 entries]";
                    }
                })
                .onSync((tag, reason) -> coreCapacity = tag.getLongs().toArray(new Long[0]));

        serverTag.getValue("grinderEnergyPerHeart")
                .syncTagToClient()
                .setComment("Mob Grinder energy required per entity health point")
                .setDefaultInt(80)
                .onSync((tag, type) -> grinderEnergyPerHeart = tag.getInt());
        serverTag.getValueList("grinderBlackList")
                .syncTagToClient()
                .setComment("Mob Grinder entity blacklist.")
                .setDefaultStrings(Lists.newArrayList("evilcraft:vengeance_spirit"))
                .onSync((tag, type) -> grinderBlackList = new HashSet<>(tag.getStrings()));
        serverTag.getValue("allowGrindingPlayers")
                .syncTagToClient()
                .setComment("Allow mob grinder to grind players")
                .setDefaultBoolean(false)
                .onSync((tag, type) -> allowGrindingPlayers = tag.getBoolean());
    }

    //Client properties
    public static boolean configUiShowUnavailable;
    public static boolean configUiEnableSnapping;
    public static boolean configUiEnableVisualization;
    public static boolean configUiEnableAddGroupButton;
    public static boolean configUiEnableDeleteZone;
    public static boolean configUiEnableAdvancedXOver;
    public static boolean fancyToolModels;
    @Deprecated
//    public static boolean toolShaders;
//    public static boolean crystalShaders;
//    public static boolean reactorShaders;
    public static boolean guardianShaders;
    //    public static boolean otherShaders;
    public static boolean itemDislocatorSound;

    private static void loadClient() {
        clientTag = config.getCategory("Client");
        clientTag.setComment("These are client side config properties.");

        clientTag.getValue("fancyToolModels")
                .setComment("Set this to false to disable the fancy 3D tool models. (Requires restart)")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> fancyToolModels = tag.getBoolean());
//        clientTag.getValue("toolShaders")
//                .setComment("Set this to false to disable tool shaders.")
//                .setDefaultBoolean(true)
//                .onSync((tag, type) -> toolShaders = tag.getBoolean());
//        clientTag.getValue("crystalShaders")
//                .setComment("Set this to false to disable crystal shaders.")
//                .setDefaultBoolean(true)
//                .onSync((tag, type) -> crystalShaders = tag.getBoolean());
//        clientTag.getValue("reactorShaders")
//                .setComment("Set this to false to disable reactor shaders.")
//                .setDefaultBoolean(true)
//                .onSync((tag, type) -> reactorShaders = tag.getBoolean());
        clientTag.getValue("guardianShaders")
                .setComment("Set this to false to disable chaos guardian shaders. (May visually break some stuff but could be useful if you are experiencing gl crashes.)")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> guardianShaders = tag.getBoolean());
//        clientTag.getValue("otherShaders")
//                .setComment("Set this to false to disable all other shaders.")
//                .setDefaultBoolean(true)
//                .onSync((tag, type) -> otherShaders = tag.getBoolean());

        ConfigCategory itemConfigGui = clientTag.getCategory("itemConfigGUI");
        itemConfigGui.setComment("These settings is accessible in game via the \"Configure Equipment\" gui.");
        itemConfigGui.getValue("showUnavailable")
                .setComment("Setting this to false will prevent properties from being displayed if their associated item is not in your inventory.")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> configUiShowUnavailable = tag.getBoolean());
        itemConfigGui.getValue("enableSnapping")
                .setComment("Setting this to false will disable property window snapping.")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> configUiEnableSnapping = tag.getBoolean());
        itemConfigGui.getValue("enableVisualization")
                .setComment("Setting this to false will disable the highlight/animation that occurs over a properties associated item when hovering over or editing a property.")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> configUiEnableVisualization = tag.getBoolean());
        itemConfigGui.getValue("enableAddGroupButton")
                .setComment("Setting this to false will hide the \"Add Group\" button.")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> configUiEnableAddGroupButton = tag.getBoolean());
        itemConfigGui.getValue("enableDeleteZone")
                .setComment("Setting this to false will hide the \"Delete Zone\"")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> configUiEnableDeleteZone = tag.getBoolean());
        itemConfigGui.getValue("enableAdvancedXOver")
                .setComment("If enabled your configured properties, property groups and presets will still be accessible when in the simple configuration mode.")
                .setDefaultBoolean(false)
                .onSync((tag, type) -> configUiEnableAdvancedXOver = tag.getBoolean());

        clientTag.getValue("itemDislocatorSound")
                .setComment("Enable / Disable item dislocator pickup sound")
                .setDefaultBoolean(true)
                .onSync((tag, type) -> itemDislocatorSound = tag.getBoolean());
    }

    //Common properties

    private static void modifyProperty(String name, Consumer<ConfigValue> modifyCallback, String... groupPath) {
        ConfigCategory parent = config;
        for (String group : groupPath) {
            parent = parent.getCategory(group);
        }
        ConfigValue tag = parent.getValue(name);
        modifyCallback.accept(tag);
        tag.runSync(ConfigCallback.Reason.MANUAL);
        tag.save();
    }

    public static void modifyClientProperty(String name, Consumer<ConfigValue> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Client"}, groupPath));
    }

    public static void modifyServerProperty(String name, Consumer<ConfigValue> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Server"}, groupPath));
    }

    public static void modifyCommonProperty(String name, Consumer<ConfigValue> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"Common"}, groupPath));
    }
}
