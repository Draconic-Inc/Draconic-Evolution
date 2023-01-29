package com.brandon3055.draconicevolution.common.lib;

public final class References {

    public static final String MODID = "DraconicEvolution";
    public static final String MODNAME = "Draconic Evolution";
    public static final String VERSION = VersionHandler.FULL_VERSION;
    public static final String MCVERSION = VersionHandler.MCVERSION;
    public static final String CLIENTPROXYLOCATION = "com.brandon3055.draconicevolution.client.ClientProxy";
    public static final String SERVERPROXYLOCATION = "com.brandon3055.draconicevolution.common.CommonProxy";
    public static final String GUIFACTORY = "com.brandon3055.draconicevolution.client.gui.DEGUIFactory";
    public static final String RESOURCESPREFIX = MODID.toLowerCase() + ":";

    // ======================Render IDs========================//
    public static int idTeleporterStand = -1;
    public static int idPortal = -1;

    // ======================Data Types========================//

    public static final byte BYTE_ID = 0;
    public static final byte SHORT_ID = 1;
    public static final byte INT_ID = 2;
    public static final byte LONG_ID = 3;
    public static final byte FLOAT_ID = 4;
    public static final byte DOUBLE_ID = 5;
    public static final byte BOOLEAN_ID = 6;
    public static final byte CHAR_ID = 7;
    public static final byte STRING_ID = 8;
    public static final byte INT_PAIR_ID = 9;

    // ======================Tags========================//

    public static final String DIG_SPEED_MULTIPLIER = "ToolDigMultiplier";
    public static final String DIG_AOE = "ToolDigAOE";
    public static final String DIG_DEPTH = "ToolDigDepth";
    public static final String ATTACK_AOE = "WeaponAttackAOE";
    public static final String OBLITERATE = "ToolVoidJunk";
    public static final String TREE_MODE = "AxeTreeMode";
    public static final String BASE_SAFE_AOE = "BaseSafeAOE";
}
