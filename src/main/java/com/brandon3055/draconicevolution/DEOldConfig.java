package com.brandon3055.draconicevolution;


import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 24/3/2016.
 * This class holds all of the config values for Draconic Evolution
 */
//@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Deprecated
public class DEOldConfig {

//        comments.put("items", "Allows you to disable any item in the mod. Note that disabling an item will automatically\ndisable its recipe and all recipes that use it. (Requires game restart)\nTo disable an item set its value to false");
//        comments.put("blocks", "Allows you to disable any block in the mod. Note that disabling an block will automatically\ndisable its recipe and all recipes that use it. (Requires game restart)\nTo disable a block set its value to false");
//        comments.put("World", "This category contains config properties related to world gen.");
//        comments.put("Tweaks", "Just what the name says. Tweaks. Allows you to tweak stuff.");
//        comments.put("Client Settings", "These are client side properties that have no effect server side.");
//        comments.put("Stat Tweaks", "These allow you to tweak the stats of the tools, weapons and armor.");
//        comments.put("Misc", "Just some misc settings.");

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final Server SERVER;
    public static final Common COMMON;

    static {
        final Pair<Client, ForgeConfigSpec> cSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = cSpecPair.getLeft();
        CLIENT_SPEC = cSpecPair.getRight();

        final Pair<Server, ForgeConfigSpec> sSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = sSpecPair.getLeft();
        SERVER_SPEC = sSpecPair.getRight();

        final Pair<Common, ForgeConfigSpec> comSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = comSpecPair.getLeft();
        COMMON_SPEC = comSpecPair.getRight();
    }


    /**
     * These are the underlying config fields that are managed by forge. If you need to modify a config value runtime
     * these are the values you must modify. If a value needs to be accessed frequently it should be assigned to a
     * more efficient static field in {@link BCConfig}
     * <p>
     * {@link ModConfig.Type#CLIENT}
     * (Not Synchronized)
     */
    public static class Client {
        private List<Runnable> accessAssigners = new ArrayList<>();

//        public final ForgeConfigSpec.BooleanValue use_shaders;
//        public final ForgeConfigSpec.BooleanValue dark_mode;

        public Client(ForgeConfigSpec.Builder builder) {
//            builder.push("General");
//            use_shaders = builder
//                    .comment("Set this to false if your system can not handle the awesomeness that is shaders! (Warning: Will make cool things look much less cool!)")
//                    .translation(BrandonsCore.MODID + ".config.use_shaders")
//                    .define("use_shaders", true);
//            assignStatic(use_shaders, e -> BCConfig.useShaders = e);
//            dark_mode = builder
//                    .comment("Enable gui dark mode in GUI's. (This can also be toggled in game from any gui that supports dark mode)")
//                    .translation(BrandonsCore.MODID + ".config.dark_mode")
//                    .define("dark_mode", false);
//            assignStatic(dark_mode, e -> BCConfig.darkMode = e);

//            builder.pop();
        }

        private <T, C extends ForgeConfigSpec.ConfigValue<T>> void assignStatic(C config, Consumer<T> assigner) {
            accessAssigners.add(() -> assigner.accept(config.get()));
        }
    }

    /**
     * These are the underlying config fields that are managed by forge. If you need to modify a config value runtime
     * these are the values you must modify. If a value needs to be accessed frequently it should be assigned to a
     * more efficient static field in {@link BCConfig}
     * <p>
     * {@link ModConfig.Type#SERVER}
     * (Per-Server-Instance Config. Synchronized to players on connections)
     */
    public static class Server {
        private List<Runnable> accessAssigners = new ArrayList<>();

//        public final ForgeConfigSpec.BooleanValue enable_tpx;
//        public final ForgeConfigSpec.BooleanValue disable_invasive_gui;

        public Server(ForgeConfigSpec.Builder builder) {
//            builder.push("Misc");
//            enable_tpx = builder
//                    .worldRestart()
//                    .comment("Allows you to disable the tpx command.")
//                    .translation(BrandonsCore.MODID + ".config.enable_tpx")
//                    .define("enable_tpx", !ModList.get().isLoaded("mystcraft"));
//            disable_invasive_gui = builder
//                    .comment("This disables the gui that is shown to clients if a server side config that cant be hot swapped has changed.\nIt is replaced by a chat message that has an option to open the gui")
//                    .translation(BrandonsCore.MODID + ".config.invasive_gui")
//                    .define("disable_invasive_gui", true);
//            builder.pop();
        }

        private <T, C extends ForgeConfigSpec.ConfigValue<T>> void assignStatic(C config, Consumer<T> assigner) {
            accessAssigners.add(() -> assigner.accept(config.get()));
        }
    }


    /**
     * These are the underlying config fields that are managed by forge. If you need to modify a config value runtime
     * these are the values you must modify. If a value needs to be accessed frequently it should be assigned to a
     * more efficient static field in {@link BCConfig}
     * <p>
     * {@link ModConfig.Type#COMMON}
     * (Not Synchronized)
     */
    public static class Common {
        private List<Runnable> accessAssigners = new ArrayList<>();

//        protected final ForgeConfigSpec.BooleanValue dev_log;


        public Common(ForgeConfigSpec.Builder builder) {
//            builder.push("Misc");
//            dev_log = builder
//                    .comment("Enable DEV log output.")
//                    .translation(BrandonsCore.MODID + ".config.dev_log")
//                    .define("dev_log", false);
//            assignStatic(dev_log, e -> BCConfig.devLog = e);

//            builder.pop();
        }

        private <T, C extends ForgeConfigSpec.ConfigValue<T>> void assignStatic(C config, Consumer<T> assigner) {
            accessAssigners.add(() -> assigner.accept(config.get()));
        }
    }


    /**
     * This method will be called by Forge when a config changes.
     */
//    @SubscribeEvent
//    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
//        final ModConfig config = event.getConfig();
//        // Reassign the configs when they change
//        if (config.getSpec() == CLIENT_SPEC) {
//            CLIENT.accessAssigners.forEach(Runnable::run);
//            LogHelper.debug("Assigned client config");
//        } else if (config.getSpec() == SERVER_SPEC) {
//            SERVER.accessAssigners.forEach(Runnable::run);
//            LogHelper.debug("Assigned server config");
//        } else if (config.getSpec() == COMMON_SPEC) {
//            COMMON.accessAssigners.forEach(Runnable::run);
//            LogHelper.debug("Assigned common config");
//        }
//    }


//    @Override
//    public Configuration createConfiguration(FMLPreInitializationEvent event) {
//        return new Configuration(new File(FileHandler.brandon3055Folder, "DraconicEvolution.cfg"), true);
//    }

//    @Override
//    public String getCategoryComment(String category) {
//        return comments.getOrDefault(category, "");
//    }

//    @Override
//    public void onConfigChanged(String propertyName, String propertyCategory) {
//        loadToolStats();
//    }

    //####################################################################
    // Server Stuff
    //####################################################################

    //Category World

    //    @ModConfigProperty(category = "World", name = "worldGenEnabled", comment = "Setting this to false will just completely disable ALL DE world gen!")
    public static boolean worldGenEnabled = true;

    //    @ModConfigProperty(category = "World", name = "enableRetroGen", comment = "Set this to false if you do not want ore added to chunks that have not previously been generated by DE (this can almost always be left true).")
    public static boolean enableRetroGen = true;

    //    @ModConfigProperty(category = "World", name = "disableOreSpawnOverworld", comment = "Disables draconium ore generation in the overworld.")
//    public static boolean disableOreSpawnOverworld = false;

    //    @ModConfigProperty(category = "World", name = "disableOreSpawnEnd", comment = "Disables draconium ore generation in the end.")
//    public static boolean disableOreSpawnEnd = false;

    //    @ModConfigProperty(category = "World", name = "disableOreSpawnNether", comment = "Disables draconium ore generation in the nether.")
//    public static boolean disableOreSpawnNether = false;

    //    @ModConfigProperty(category = "World", name = "generateEnderComets", comment = "Set to false to disable the generation of Ender Comets.")
    public static boolean generateEnderComets = true;

    //    @ModConfigProperty(category = "World", name = "generateChaosIslands", comment = "Set to false to disable the generation of Chaos Islands.")
    public static boolean generateChaosIslands = true;

    //    @ModConfigProperty(category = "World", name = "chaosIslandVoidMode", comment = "If true, the Chaos Guardian, Crystals, and Healing Crystals will still spawn, but NO other blocks will be placed. (This only exists because someone wanted it for some reason).")
    public static boolean chaosIslandVoidMode = false;

    //    @ModConfigProperty(category = "World", name = "cometRarity", comment = "Ender Comets have a 1 in {this number} chance to spawn in each chunk.")
//    @ModConfigProperty.MinMax(min = "500", max = "1000000")
    public static int cometRarity = 10000;

    //    @ModConfigProperty(category = "World", name = "chaosIslandSeparation", comment = "This is the distance between Chaos Islands.")
//    @ModConfigProperty.MinMax(min = "500", max = "1000000")
    public static int chaosIslandSeparation = 10000;

    //    @ModConfigProperty(category = "World", name = "chaosIslandYOffset", comment = "This allows you to offset the y position of Chaos Islands.")
//    @ModConfigProperty.MinMax(min = "-50", max = "150")
    public static int chaosIslandYOffset = 0;

    //    @ModConfigProperty(category = "World", name = "oreGenDimensionBlacklist", comment = "Add the ID of any mod's dimensions that you don't want Draconium Ore generated in.")
    public static String[] oreGenDimentionBlacklist = new String[0];

    //Category Tweak

//    @ModConfigProperty(category = "Tweaks", name = "rapidDespawnAOEMinedItems", comment = "If true, items dropped by tools in AOE mode will despawn after 5 seconds if not picked up.")
//    public static boolean rapidDespawnAOEMinedItems = false;

    //    @ModConfigProperty(category = "Tweaks", name = "disableGuardianCrystalRespawn", comment = "(Wuss mode) Setting this to true will disable the Chaos Guardian's ability to respawn healing crystals.")
    public static boolean disableGuardianCrystalRespawn = false;

    //    @ModConfigProperty(category = "Tweaks", name = "enableFlight", comment = "Set this to false if you would like to disable the draconic armors flight.", autoSync = true)
    public static boolean enableFlight = true;

    //    @ModConfigProperty(category = "Tweaks", name = "dislocatorUsesPerPearl", comment = "Sets the number of teleports you get per ender pearl with the Advanced Dislocator.")
    public static int dislocatorUsesPerPearl = 1;

    //    @ModConfigProperty(category = "Tweaks", name = "bowBlockDamage", comment = "Set to false to prevent the bows explosion effect from breaking blocks.")
    public static boolean bowBlockDamage = true;

    //    @ModConfigProperty(category = "Tweaks", name = "grinderEnergyPerHeart", comment = "Sets the energy per use per heart of damage for the grinder.")
//    public static int grinderEnergyPerHeart = 80;

    //    @ModConfigProperty(category = "Tweaks", name = "dragonEggSpawnOverride", comment = "By default, the dragon egg only ever spawns once. This forces it to spawn every time the dragon is killed.")
//    public static boolean dragonEggSpawnOverride = true;

    //    @ModConfigProperty(category = "Tweaks", name = "expensiveDragonRitual", comment = "Lets face it. The biggest issue with the new dragon ritual is it is too darn cheap! This modifies the recipe to make it a bit more expensive.")
    public static boolean expensiveDragonRitual = true;

    //    @ModConfigProperty(category = "Tweaks", name = "itemDislocatorBlacklist", comment = "A list of items of items that should be ignored by the item dislocator. Use the item's registry name (e.g. minecraft:apple) You can also add a meta value after the name (e.g. minecraft:wool|4).", autoSync = true)
    public static String[] itemDislocatorBlacklist = new String[]{"appliedenergistics2:crystal_seed"};


    //    @ModConfigProperty(category = "Tweaks", name = "dragonDustLootModifier", comment = "This can be used to adjust the amount of Draconium Dust the Ender Dragon drops when killed.\nThe amount dropped will be this number +/- 10%")
//    public static int dragonDustLootModifier = 64;

    //    @ModConfigProperty(category = "Tweaks", name = "disableChaosIslandExplosion", comment = "If true, this will disable the destruction of chaos islands after the chaos crystal is broken.")
    public static boolean disableChaosIslandExplosion = false;

    //    @ModConfigProperty(category = "Tweaks", name = "disableLootCores", comment = "This will disable loot cores (The \"Blobs\" of items dropped by the tools.).")
    public static boolean disableLootCores = false;




    //    @ModConfigProperty(category = "Tweaks", name = "oreDoublingBlacklist", comment = "Add ore names (e.g. oreIron) to this list to prevent them from being doubled by the DE chest.", autoSync = true)
    public static String[] oreDoublingBlacklist = {};

    //    @ModConfigProperty(category = "Tweaks", name = "dissenchnaterCostMultiplier", comment = "Allows you to adjust the cost of disenchanting items via the Disenchanter.", autoSync = true)
    public static double disenchnaterCostMultiplyer = 1;

    //    @ModConfigProperty(category = "Tweaks", name = "forceDroppedItemOwner", comment = "For some reason, Forge decided to not set the owner of an item when dropped from an inventory screen.\nDE overrides this and sets the stack owner when possible.\nIf this causes issues, set this value to false.", autoSync = true)
    public static boolean forceDroppedItemOwner = true;

    //    @ModConfigProperty(category = "Tweaks", name = "chaosGuardianHealth", comment = "Allows you to tweak the chaos guardians health (will only affect new guardians).")
    public static int chaosGuardianHealth = 2000;

    //    @ModConfigProperty(category = "Tweaks", name = "oreDoublingOutputPriority", comment = "When doubling ores with the Draconium Chest, the output will prioritise thermal ingots if TF is installed. This allows you to change that by specifying a different mod id to target.")
    public static String oreDoublingOutputPriority = "thermalfoundation";

    //    @ModConfigProperty(category = "Tweaks", name = "grinderBlackList", comment = "This allows you to prevent the mob grinder from attempting to kill specific entities.")
//    public static String[] grinderBlackList = {"evilcraft:vengeance_spirit"};

    //    @ModConfigProperty(category = "Tweaks", name = "flightSpeedLimit", autoSync = true, comment = "Use this to limit the max flight speed modifier a player can set on the draconic chestplate.\nSetting this to 200 for example would limit the flight speed to +200%.\nDefault -1 removes the limit and allows the full +600% flight speed.")
    public static int flightSpeedLimit = -1; //TODO will be synced from server to client

    //####################################################################
    // Common Configs
    //####################################################################


    //    @ModConfigProperty(category = "Tweaks", name = "hardMode", comment = "When true, everything is just a little harder. (Currently only effects recipes but that will probably change in the future)", requiresMCRestart = true, requiresSync = true)
    public static boolean hardMode = false; //This may end up as a data pack or something

//    @ModConfigProperty(category = "Tweaks", name = "clearDataRecipes", comment = "Adds recipes to clear all nbt data from items such as Energy Crystals and Crafting Injectors.", requiresMCRestart = true, requiresSync = true)
//    public static boolean clearDataRecipes = false; TODO ?


    //    @ModConfigProperty(category = "Tweaks", name = "chestBlackList", comment = "This allows you to prevent certain items from being placed in the draconium chest using their registry name")
    public static String[] chestBlackList = {};

    //    @ModConfigProperty(category = "Tweaks", name = "coreCapacity", comment = "Allows you to adjust the capacity of each energy core tier.\nWarning changing the number entries in this list will crash your game.")
    public static double[] coreCapacity = new double[]{45500000L, 273000000L, 1640000000L, 9880000000L, 59300000000L, 356000000000L, 2140000000000L, Long.MAX_VALUE};

    //    @ModConfigProperty(category = "Tweaks", name = "wyvernFluxCapBaseCap", comment = "This allows you to adjust the base capacity of the Wyvern Flux Capacitor.")
    public static int wyvernFluxCapBaseCap = 64000000;

    //    @ModConfigProperty(category = "Tweaks", name = "draconicFluxCapBaseCap", comment = "This allows you to adjust the base capacity of the Draconic Flux Capacitor.")
    public static int draconicFluxCapBaseCap = 256000000;

    //    @ModConfigProperty(category = "Tweaks", name = "allowGrindingPlayers", comment = "Allow the mob grinder to grind players", autoSync = true)
//    public static boolean allowGrindingPlayers = false;

    //Category Client

    //    @ModConfigProperty(category = "Client Settings", name = "hudSettings", comment = "This is where the settings for the in game hud are stored. You should not need to adjust these unless something breaks.\nWarning: Changing the number of entries in this list will crash your game.")
    public static int[] hudSettings = new int[]{996, 825, 69, 907, 90, 100, 3, 0, 1, 1, 1, 1}; //x, y, x, y, scale, scale, fademode, fademode, rotateArmor, armorText, hudEnabled, shieldEnabled

    //    @ModConfigProperty(category = "Client Settings", name = "disable3DModels", comment = "Disables the 3D tool and armor models. This is required if you want to use a 2D resource pack.")
    public static boolean disable3DModels = false;

    //    @ModConfigProperty(category = "Client Settings", name = "invertDPDSB", comment = "Invert Dislocator Pedestal display name shift behavior.")
    public static boolean invertDPDSB = false;
//
//    //    @ModConfigProperty(category = "Client Settings", name = "useCrystalShaders", comment = "This allows you to disable just the Energy Crystal shader. This shader can be a lot lagier than the reactor's shader since there are usually a lot more of them (The fallback crystal texture is not soooo bad...).")
//    public static boolean useCrystalShaders = true;
//
//    //    @ModConfigProperty(category = "Client Settings", name = "useReactorBeamShaders", comment = "Set this to false if you prefer the original look of the reactor beams.")
//    public static boolean useReactorBeamShaders = true;

    //    @ModConfigProperty(category = "Client Settings", name = "disableLoudCelestialManipulator", comment = "If true, the range of the Celestial Manipulator's sound effect will be significantly reduced.")
    public static boolean disableLoudCelestialManipulator = false;

//    @ModConfigProperty(category = "Client Settings", name = "disableDislocatorSound", comment = "If true, the item dislocator will not make the pickup sound when it collects items.")


    //    @ModConfigProperty(category = "Client Settings", name = "disableShieldHitEffect", comment = "If true, the armor shield will not render when you take damage (this is only a visual change).")
    public static boolean disableShieldHitEffect = false;

    //    @ModConfigProperty(category = "Client Settings", name = "disableShieldHitSound", comment = "If true, the armor shield hit sound will be disabled.")
    public static boolean disableShieldHitSound = false;

    //    @ModConfigProperty(category = "Client Settings", name = "disableCustomArrowModel", comment = "If true, the custom arrow models used by DE will be replaced by the vanilla model.", requiresMCRestart = true)
    public static boolean disableCustomArrowModel = false;

    //Category Misc

    //    @ModConfigProperty(category = "Misc", name = "devLog", comment = "This enables dev log output. I primarily use this for development purposes, so it won't be very useful to regular users.")
    public static boolean devLog = false;

    //    @ModConfigProperty(category = "Misc", name = "chaosGuardianLoading", comment = "Set this to false to disable the Chaos Guardian's chunkloading ability.\nNote. The chaos guardian is ONLY loaded when a player is within a couple hundred blocks.\nThis setting is here to avoid issues where the guardian would fly out of the loaded chunks \nand freeze; especially an issue on servers with reduced render distance.")
    public static boolean chaosGuardianLoading = true;

    public static Map<String, Integer> itemDislocatorBlacklistMap = new HashMap<String, Integer>();
//    public static Set<String> oreBlacklist = new HashSet<>();
//    public static Set<String> grinderBlacklist = new HashSet<>();
    public static Set<String> chestBlacklist = new HashSet<>();

//    @Override
//    public void onConfigLoaded() {
//        itemDislocatorBlacklistMap.clear();
//        grinderBlacklist.clear();
//        for (String s : itemDislocatorBlacklist) {
//            if (s.contains("|")) {
//                itemDislocatorBlacklistMap.put(s.substring(0, s.indexOf("|")), Integer.parseInt(s.substring(s.indexOf("|") + 1)));
//            }
//            else {
//                itemDislocatorBlacklistMap.put(s, -1);
//            }
//        }
//
//        oreBlacklist = Sets.newHashSet(oreDoublingBlacklist);
//        grinderBlacklist = Sets.newHashSet(grinderBlackList);
//        chestBlacklist = Sets.newHashSet(chestBlackList);
//        loadToolStats();
//    }

    private void loadToolStats() {
//        DEContent.draconicAxe.loadStatConfig();
//        DEContent.draconicBow.loadStatConfig();
//        DEContent.draconicHoe.loadStatConfig();
//        DEContent.draconicPick.loadStatConfig();
//        DEContent.draconicShovel.loadStatConfig();
//        DEContent.draconicStaffOfPower.loadStatConfig();
//        DEContent.draconicSword.loadStatConfig();
//        DEContent.wyvernAxe.loadStatConfig();
//        DEContent.wyvernBow.loadStatConfig();
//        DEContent.wyvernPick.loadStatConfig();
//        DEContent.wyvernShovel.loadStatConfig();
//        DEContent.wyvernSword.loadStatConfig();
    }
}


//todo energy core storage values and ability to disable tier 8
//todo tool and armor stats and energy
//todo
//todo
