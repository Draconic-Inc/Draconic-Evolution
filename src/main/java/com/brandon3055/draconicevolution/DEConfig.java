package com.brandon3055.draconicevolution;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class DEConfig {

    private static ConfigTag config;
    private static ConfigTag clientTag;
    private static ConfigTag serverTag;
    private static ConfigTag commonTag;

    public static void load() {
        config = new StandardConfigFile(Paths.get("./config/brandon3055/DraconicEvolution.cfg")).load();
        loadServer();
        loadClient();
        loadCommon();
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
    public static boolean otherShaders;

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
                .setComment("Set this to false to disable reactorShaders shaders.")
                .setDefaultBoolean(true)
                .setSyncCallback((tag, type) -> reactorShaders = tag.getBoolean());
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
    }

    //Common properties

    private static void loadCommon() {
        commonTag = config.getTag("Common");
    }

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
