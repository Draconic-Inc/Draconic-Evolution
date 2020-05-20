package com.brandon3055.draconicevolution;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
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
        config.runSync();
        config.save();
    }


    //Server properties
    public static String serverID;

    private static void loadServer() {
        serverTag = config.getTag("server");
        ConfigTag serverIDTag = serverTag.getTag("serverID")
                .setSyncToClient()
                .setComment("This is a randomly generated id that clients will use to map their tool config settings to this server.")
                .setDefaultString(UUID.randomUUID().toString());
        serverIDTag.setSyncCallback((tag, type) -> serverID = tag.getString());
    }


    //Client properties
    public static boolean configUiShowUnavailable;
    public static boolean configUiEnableSnapping;
    public static boolean configUiEnableVisualization;
    public static boolean configUiEnableAddGroupButton;
    public static boolean configUiEnableDeleteZone;
    public static boolean configUiEnableAdvancedXOver;

    private static void loadClient() {
        clientTag = config.getTag("client");
        clientTag.setComment("These are client side config properties.");

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
        commonTag = config.getTag("common");
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
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"client"}, groupPath));
    }

    public static void modifyServerProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"server"}, groupPath));
    }

    public static void modifyCommonProperty(String name, Consumer<ConfigTag> modifyCallback, String... groupPath) {
        modifyProperty(name, modifyCallback, ArrayUtils.addAll(new String[]{"common"}, groupPath));
    }
}
