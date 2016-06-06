package com.brandon3055.draconicevolution.common.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class BalanceConfigHandler
{
    public static int wyvernArmorBaseStorage = 1000000;
    public static int wyvernArmorStoragePerUpgrade = 500000;
    public static int wyvernArmorMaxTransfer = 50000;
    public static int wyvernArmorEnergyPerProtectionPoint = 1000;
    public static int draconicArmorBaseStorage = 10000000;
    public static int draconicArmorStoragePerUpgrade = 5000000;
    public static int draconicArmorMaxTransfer = 500000;
    public static int draconicArmorEnergyPerProtectionPoint = 1000;
    public static int draconicArmorEnergyToRemoveEffects = 5000;
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
    public static int wyvernBowEnergyPerShot = 80;
    public static int draconicWeaponsBaseStorage = 10000000;
    public static int draconicWeaponsStoragePerUpgrade = 5000000;
    public static int draconicWeaponsMaxTransfer = 500000;
    public static int draconicWeaponsEnergyPerAttack = 250;
    public static int draconicBowEnergyPerShot = 80;
    public static int draconicFireEnergyCostMultiptier = 30;
    public static int draconiumBlockEnergyToChange = 100000000;
    public static int draconiumBlockChargingSpeed = 10000000;
    public static int energyInfuserStorage = 10000000;
    public static int energyInfuserMaxTransfer = 10000000;
    public static int grinderInternalEnergyBufferSize = 20000;
    public static int grinderExternalEnergyBufferSize = 100000;
    public static int grinderMaxReceive = 32000;
    public static int grinderEnergyPerKill = 1000;
    public static boolean grinderShouldUseLooting = false;
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
        wyvernArmorBaseStorage =
            getInteger("energy.armor", "Wyvern Armor: Base energy storage (RF)", wyvernArmorBaseStorage);
        wyvernArmorStoragePerUpgrade =
            getInteger("energy.armor", "Wyvern Armor: Additional energy storage per upgrade installed (RF)",
                       wyvernArmorStoragePerUpgrade);
        wyvernArmorMaxTransfer =
            getInteger("energy.armor", "Wyvern Armor: Maximum energy transfer rate (RF/t)", wyvernArmorMaxTransfer);
        wyvernArmorEnergyPerProtectionPoint =
            getInteger("energy.armor", "Wyvern Armor: Amount of energy required to restore protection point (RF)",
                       wyvernArmorEnergyPerProtectionPoint);
        draconicArmorBaseStorage =
            getInteger("energy.armor", "Draconic Armor: Base energy storage (RF)", draconicArmorBaseStorage);
        draconicArmorStoragePerUpgrade =
            getInteger("energy.armor", "Draconic Armor: Additional energy storage per upgrade installed (RF)",
                       draconicArmorStoragePerUpgrade);
        draconicArmorMaxTransfer =
            getInteger("energy.armor", "Draconic Armor: Maximum energy transfer rate (RF/t)", draconicArmorMaxTransfer);
        draconicArmorEnergyPerProtectionPoint =
            getInteger("energy.armor", "Draconic Armor: Amount of energy required to restore protection point (RF)",
                       draconicArmorEnergyPerProtectionPoint);
        draconicArmorEnergyToRemoveEffects =
            getInteger("energy.armor", "Draconic Armor: Amount of energy required to remove negative effects (RF)",
                       draconicArmorEnergyToRemoveEffects);
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
        wyvernBowEnergyPerShot =
            getInteger("energy.weapons", "Wyvern Bow: Amount of energy required to shoot (RF)", wyvernBowEnergyPerShot);
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
        draconicBowEnergyPerShot = getInteger("energy.weapons", "Draconic Bow: Amount of energy required to shoot (RF)",
                                              draconicBowEnergyPerShot);
        draconicFireEnergyCostMultiptier =
            getInteger("energy.weapons", "Arrow of Draconic Fire: Energy cost multiplier",
                       draconicFireEnergyCostMultiptier);
        draconiumBlockEnergyToChange =
            getInteger("energy.misc", "Draconium Block: Amount of energy required to charge (RF)",
                       draconiumBlockEnergyToChange);
        draconiumBlockChargingSpeed =
            getInteger("energy.misc", "Draconium Block: Maximum charging speed (RF/t)", draconiumBlockChargingSpeed);
        energyInfuserStorage =
            getInteger("energy.machines", "Energy Infuser: Energy buffer size (RF)", energyInfuserStorage);
        energyInfuserMaxTransfer = getInteger("energy.machines", "Energy Infuser: Maximum energy transfer rate (RF/t)",
                                              energyInfuserMaxTransfer);
        grinderInternalEnergyBufferSize = getInteger("energy.machines", "Mob Grinder: Internal energy buffer size (RF)",
                                                     grinderInternalEnergyBufferSize);
        grinderExternalEnergyBufferSize =
            getInteger("energy.machines", "Mob Grinder: Main energy buffer size (RF)", grinderExternalEnergyBufferSize);
        grinderMaxReceive =
            getInteger("energy.machines", "Mob Grinder: Maximum energy reception rate (RF/t)", grinderMaxReceive);
        grinderEnergyPerKill =
            getInteger("energy.machines", "Mob Grinder: Amount of energy required to kill entity (RF)",
                       grinderEnergyPerKill);
        grinderShouldUseLooting =
            getBoolean("tweaks.machines", "Mob Grinder: Use Looting enchantment", grinderShouldUseLooting);
        if (config.hasChanged())
        {
            config.save();
        }
    }
    private static boolean getBoolean(String category, String propertyName, boolean defaultValue)
    {
        return config.get(category, propertyName, defaultValue).getBoolean(defaultValue);
    }
    private static int getInteger(String categoty, String propertyName, int defaultValue)
    {
        return config.get(categoty, propertyName, defaultValue).getInt(defaultValue);
    }
}
