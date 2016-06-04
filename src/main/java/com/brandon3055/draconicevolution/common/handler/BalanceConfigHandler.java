package com.brandon3055.draconicevolution.common.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class BalanceConfigHandler
{
    public static int wyvernToolsBaseStorage = 1000000;
    public static int wyvernToolsStoragePerUpgrade = 500000;
    public static int wyvernToolsMaxTransfer = 50000;
    public static int wyvernToolsEnergyPerAction = 80;
    public static int draconicToolsBaseStorage = 10000000;
    public static int draconicToolsStoragePerUpgrade = 5000000;
    public static int draconicToolsMaxTransfer = 500000;
    public static int draconicToolsEnergyPerAction = 80;
    public static int wyvernCapacitorBaseStorage = 80000000;
    public static int wyvernCapacitorStoragePerUpgrade = 50000000;
    public static int wyvernCapacitorMaxReceive = 1000000;
    public static int wyvernCapacitorMaxExtract = 10000000;
    public static int draconicCapacitorBaseStorage = 250000000;
    public static int draconicCapacitorStoragePerUpgrade = 50000000;
    public static int draconicCapacitorMaxReceive = 10000000;
    public static int draconicCapacitorMaxExtract = 100000000;
    public static int wyvernWeaponsBaseStorage = 1000000;
    public static int wyvernWeaponsStoragePerUpgrade = 500000;
    public static int wyvernWeaponsMaxTransfer = 50000;
    public static int wyvernWeaponsEnergyPerAttack = 250;
    public static int draconicWeaponsBaseStorage = 10000000;
    public static int draconicWeaponsStoragePerUpgrade = 5000000;
    public static int draconicWeaponsMaxTransfer = 500000;
    public static int draconicWeaponsEnergyPerAttack = 250;
    public static int draconicFireEnergyCostMultiptier = 30;
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
        draconicToolsBaseStorage =
            getInteger("energy.tools", "Draconic Tools: Base energy storage (RF)", draconicToolsBaseStorage);
        draconicToolsStoragePerUpgrade =
            getInteger("energy.tools", "Draconic Tools: Additional energy storage per upgrade installed (RF)",
                       draconicToolsStoragePerUpgrade);
        draconicToolsMaxTransfer =
            getInteger("energy.tools", "Draconic Tools: Maximum energy transfer rate (RF/t)", draconicToolsMaxTransfer);
        draconicToolsEnergyPerAction =
            getInteger("energy.tools", "Draconic Tools: Amount of energy required to perform action (RF)",
                       draconicToolsEnergyPerAction);
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
        wyvernWeaponsBaseStorage =
            getInteger("energy.weapons", "Wyvern Weapons: Base energy storage (RF)", wyvernWeaponsBaseStorage);
        wyvernWeaponsStoragePerUpgrade =
            getInteger("energy.weapons", "Wyvern Weapons: Additional energy storage per upgrade installed (RF)",
                       wyvernWeaponsStoragePerUpgrade);
        wyvernWeaponsMaxTransfer = getInteger("energy.weapons", "Wyvern Weapons: Maximum energy transfer rate (RF/t)",
                                              wyvernWeaponsMaxTransfer);
        wyvernWeaponsEnergyPerAttack =
            getInteger("energy.weapons", "Wyvern Weapons: Amount of energy required to perform attack (RF)",
                       wyvernWeaponsEnergyPerAttack);
        draconicWeaponsBaseStorage =
            getInteger("energy.weapons", "Draconic Weapons: Base energy storage (RF)", draconicWeaponsBaseStorage);
        draconicWeaponsStoragePerUpgrade =
            getInteger("energy.weapons", "Draconic Weapons: Additional energy storage per upgrade installed (RF)",
                       draconicWeaponsStoragePerUpgrade);
        draconicWeaponsMaxTransfer =
            getInteger("energy.weapons", "Draconic Weapons: Maximum energy transfer rate (RF/t)",
                       draconicWeaponsMaxTransfer);
        draconicWeaponsEnergyPerAttack =
            getInteger("energy.weapons", "Draconic Weapons: Amount of energy required to perform attack (RF)",
                       draconicWeaponsEnergyPerAttack);
        draconicFireEnergyCostMultiptier =
            getInteger("energy.weapons", "Arrow of Draconic Fire: Energy cost multiplier",
                       draconicFireEnergyCostMultiptier);
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
