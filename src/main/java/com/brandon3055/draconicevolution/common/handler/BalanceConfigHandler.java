package com.brandon3055.draconicevolution.common.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class BalanceConfigHandler
{
    public static int wyvernToolsBaseStorage = 1000000;
    public static int wyvernToolsStoragePerUpgrade = 500000;
    public static int wyvernToolsMaxTransfer = 50000;
    public static int wyvernToolsEnergyPerAction = 80;
    public static int wyvernCapacitorBaseStorage = 80000000;
    public static int wyvernCapacitorStoragePerUpgrade = 50000000;
    public static int wyvernCapacitorMaxReceive = 1000000;
    public static int wyvernCapacitorMaxExtract = 10000000;
    public static int draconicCapacitorBaseStorage = 250000000;
    public static int draconicCapacitorStoragePerUpgrade = 50000000;
    public static int draconicCapacitorMaxReceive = 10000000;
    public static int draconicCapacitorMaxExtract = 100000000;
    private static Configuration config;
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
        wyvernToolsBaseStorage =
            getInteger("energy.tools", "Wyvern Tools: Base energy storage (RF)", wyvernToolsBaseStorage);
        wyvernToolsStoragePerUpgrade =
            getInteger("energy.tools", "Wyvern Tools: Additional energy storage per upgrade installed (RF)",
                       wyvernToolsStoragePerUpgrade);
        wyvernToolsMaxTransfer =
            getInteger("energy.tools", "Wyvern Tools: Maximum energy transfer rate (RF/t)", wyvernToolsMaxTransfer);
        wyvernToolsEnergyPerAction =
            getInteger("energy.tools", "Wyvern Tools: Amount of energy required to perform action (RF)",
                       wyvernToolsEnergyPerAction);
        wyvernCapacitorBaseStorage =
            getInteger("energy.tools", "Wyvern Flux Capacitor: Base energy storage (RF)", wyvernCapacitorBaseStorage);
        wyvernCapacitorStoragePerUpgrade =
            getInteger("energy.tools", "Wyvern Flux Capacitor: Additional energy storage per upgrade installed (RF)",
                       wyvernCapacitorStoragePerUpgrade);
        wyvernCapacitorMaxReceive =
            getInteger("energy.tools", "Wyvern Flux Capacitor: Maximum energy reception rate (RF/t)",
                       wyvernCapacitorMaxReceive);
        wyvernCapacitorMaxExtract =
            getInteger("energy.tools", "Wyvern Flux Capacitor: Maximum energy extraction rate (RF/t)",
                       wyvernCapacitorMaxExtract);
        draconicCapacitorBaseStorage = getInteger("energy.tools", "Draconic Flux Capacitor: Base energy storage (RF)",
                                                  draconicCapacitorBaseStorage);
        draconicCapacitorStoragePerUpgrade =
            getInteger("energy.tools", "Draconic Flux Capacitor: Additional energy storage per upgrade installed (RF)",
                       draconicCapacitorStoragePerUpgrade);
        draconicCapacitorMaxReceive =
            getInteger("energy.tools", "Draconic Flux Capacitor: Maximum energy reception rate (RF/t)",
                       draconicCapacitorMaxReceive);
        draconicCapacitorMaxExtract =
            getInteger("energy.tools", "Draconic Flux Capacitor: Maximum energy extraction rate (RF/t)",
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
