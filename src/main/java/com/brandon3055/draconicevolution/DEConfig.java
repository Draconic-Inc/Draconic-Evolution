package com.brandon3055.draconicevolution;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;

import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class DEConfig {

    private static ConfigTag config;
    public static String serverID;


    public static void load() {
        config = new StandardConfigFile(Paths.get("./config/brandon3055/DraconicEvolution.cfg")).load();
        ConfigTag serverIDTag = config.getTag("serverID")
                .setSyncToClient()
                .setComment("This is a randomly generated id that clients will use to map their tool config settings to this server.")
                .setDefaultString(UUID.randomUUID().toString());
        serverIDTag.setSyncCallback((configTag, syncType) -> serverID = configTag.getString());


        config.save();
        serverIDTag.runSync();
    }

}
