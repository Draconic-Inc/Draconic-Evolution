package com.brandon3055.draconicevolution.common.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class BalanceConfigHandler
{
    private static Configuration config;
    public static int wyvernCapacitorBaseStorage = 80000000;
    public static int wyvernCapacitorStoragePerUpgrade = 50000000;
    public static int wyvernCapacitorMaxReceive = 1000000;
    public static int wyvernCapacitorMaxExtract = 10000000;
    public static int draconicCapacitorBaseStorage = 250000000;
    public static int draconicCapacitorStoragePerUpgrade = 50000000;
    public static int draconicCapacitorMaxReceive = 10000000;
    public static int draconicCapacitorMaxExtract = 100000000;
    public static void init(File modConfigurationDirectory)
    {
        if (config == null)
        {
            config = new Configuration(new File(modConfigurationDirectory, "DraconicEvolution.Balance.cfg"));
            config.load();
            syncConfig();
        }
    }
    private static void syncConfig()
    {
        wyvernCapacitorBaseStorage = getInteger("energy.tools", "Wyvern Flux Capacitor: Base energy storage",
                                                wyvernCapacitorBaseStorage);
        wyvernCapacitorStoragePerUpgrade = getInteger("energy.tools", "Wyvern Flux Capacitor: " +
                                                                      "Additional capacity per upgrade installed",
                                                      wyvernCapacitorStoragePerUpgrade);
        wyvernCapacitorMaxReceive = getInteger("energy.tools", "Wyvern Flux Capacitor: Maximum energy reception rate",
                                               wyvernCapacitorMaxReceive);
        wyvernCapacitorMaxExtract = getInteger("energy.tools", "Wyvern Flux Capacitor: Maximum energy extraction rate",
                                               wyvernCapacitorMaxExtract);
        draconicCapacitorBaseStorage = getInteger("energy.tools", "Draconic Flux Capacitor: Base energy storage",
                                                  draconicCapacitorBaseStorage);
        draconicCapacitorStoragePerUpgrade = getInteger("energy.tools", "Draconic Flux Capacitor: " +
                                                                        "Additional capacity per upgrade installed",
                                                        draconicCapacitorStoragePerUpgrade);
        draconicCapacitorMaxReceive = getInteger("energy.tools", "Draconic Flux Capacitor: Maximum energy reception rate",
                                                 draconicCapacitorMaxReceive);
        draconicCapacitorMaxExtract = getInteger("energy.tools", "Draconic Flux Capacitor: Maximum energy extraction rate",
                                                 draconicCapacitorMaxExtract);
        if (config.hasChanged())
        {
            config.save();
        }
    }
    private static int getInteger(String categoty, String propertyName, int defaultValue)
    {
        return config.get(categoty, propertyName, defaultValue).getInt(defaultValue);
    }
}
