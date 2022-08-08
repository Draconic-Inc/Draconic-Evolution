package com.brandon3055.draconicevolution.common.handler;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem.EnumUpgrade;
import cpw.mods.fml.common.registry.GameRegistry;
import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class BalanceConfigHandler {
    public static final int wyvernArmorMinShieldRecovery = 5;
    public static final int draconicArmorMinShieldRecovery = 5;
    public static final int wyvernToolsMinDigAOEUpgradePoints = 1;
    public static final int wyvernToolsMaxDigAOEUpgradePoints = 2;
    public static final int wyvernToolsMinDigSpeedUpgradePoints = 4;
    public static final int wyvernToolsMaxDigSpeedUpgradePoints = 16;
    public static final int draconicToolsMinDigAOEUpgradePoints = 2;
    public static final int draconicToolsMaxDigAOEUpgradePoints = 4;
    public static final int draconicToolsMinDigSpeedUpgradePoints = 5;
    public static final int draconicToolsMaxDigSpeedUpgradePoints = 32;
    public static final int draconicToolsMinDigDepthUpgradePoints = 1;
    public static final int draconicToolsMaxDigDepthUpgradePoints = 5;
    public static final int wyvernWeaponsMinAttackAOEUpgradePoints = 1;
    public static final int wyvernWeaponsMaxAttackAOEUpgradePoints = 3;
    public static final int wyvernWeaponsMinAttackDamageUpgradePoints = 0;
    public static final int wyvernWeaponsMaxAttackDamageUpgradePoints = 8;
    public static final int wyvernBowMinDrawSpeedUpgradePoints = 3;
    public static final int wyvernBowMaxDrawSpeedUpgradePoints = 5;
    public static final int wyvernBowMinArrowSpeedUpgradePoints = 1;
    public static final int wyvernBowMaxArrowSpeedUpgradePoints = 10;
    public static final int wyvernBowMinArrowDamageUpgradePoints = 2;
    public static final int wyvernBowMaxArrowDamageUpgradePoints = 10;
    public static final int draconicWeaponsMinAttackAOEUpgradePoints = 2;
    public static final int draconicWeaponsMaxAttackAOEUpgradePoints = 5;
    public static final int draconicWeaponsMinAttackDamageUpgradePoints = 0;
    public static final int draconicWeaponsMaxAttackDamageUpgradePoints = 16;
    public static final int draconicBowMinDrawSpeedUpgradePoints = 4;
    public static final int draconicBowMaxDrawSpeedUpgradePoints = 6;
    public static final int draconicBowMinArrowSpeedUpgradePoints = 3;
    public static final int draconicBowMaxArrowSpeedUpgradePoints = 10;
    public static final int draconicBowMinArrowDamageUpgradePoints = 3;
    public static final int draconicBowMaxArrowDamageUpgradePoints = 20;
    public static final int draconicStaffMinDigAOEUpgradePoints = 3;
    public static final int draconicStaffMaxDigAOEUpgradePoints = 5;
    public static final int draconicStaffMinDigDepthUpgradePoints = 7;
    public static final int draconicStaffMaxDigDepthUpgradePoints = 11;
    public static final int draconicStaffMinAttackAOEUpgradePoints = 3;
    public static final int draconicStaffMaxAttackAOEUpgradePoints = 13;
    public static final int draconicStaffMinAttackDamageUpgradePoints = 0;
    public static final int draconicStaffMaxAttackDamageUpgradePoints = 64;
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
    public static long energyStorageTier1Storage = 45500000L;
    public static long energyStorageTier2Storage = 273000000L;
    public static long energyStorageTier3Storage = 1640000000L;
    public static long energyStorageTier4Storage = 9880000000L;
    public static long energyStorageTier5Storage = 59300000000L;
    public static long energyStorageTier6Storage = 356000000000L;
    public static long energyStorageTier7Storage = 2140000000000L;
    public static int grinderInternalEnergyBufferSize = 20000;
    public static int grinderExternalEnergyBufferSize = 100000;
    public static int grinderMaxReceive = 32000;
    public static int grinderEnergyPerKill = 1000;
    public static int energyRelayBasicStorage = 50000;
    public static int energyRelayAdvancedStorage = 4550000;
    public static int energyRelayBasicMaxReceive = 50000;
    public static int energyRelayAdvancedMaxReceive = 4550000;
    public static int energyRelayBasicMaxExtract = 50000;
    public static int energyRelayAdvancedMaxExtract = 4550000;
    public static int energyTransceiverBasicStorage = 50000;
    public static int energyTransceiverAdvancedStorage = 10000000;
    public static int energyTransceiverBasicMaxReceive = 50000;
    public static int energyTransceiverAdvancedMaxReceive = 10000000;
    public static int energyTransceiverBasicMaxExtract = 50000;
    public static int energyTransceiverAdvancedMaxExtract = 10000000;
    public static int energyWirelessTransceiverBasicStorage = 50000;
    public static int energyWirelessTransceiverAdvancedStorage = 4550000;
    public static int energyWirelessTransceiverBasicMaxReceive = 50000;
    public static int energyWirelessTransceiverAdvancedMaxReceive = 4550000;
    public static int energyWirelessTransceiverBasicMaxExtract = 50000;
    public static int energyWirelessTransceiverAdvancedMaxExtract = 4550000;
    public static int energyWirelessTransceiverBasicMaxSend = 5000;
    public static int energyWirelessTransceiverAdvancedMaxSend = 50000;
    public static int wyvernArmorMaxCapacityUpgradePoints = 50;
    public static int wyvernArmorMaxUpgrades = 3;
    public static int wyvernArmorMaxUpgradePoints = 50;
    public static int draconicArmorMaxCapacityUpgradePoints = 50;
    public static int draconicArmorMaxUpgrades = 6;
    public static int draconicArmorMaxUpgradePoints = 50;
    public static int wyvernToolsMaxCapacityUpgradePoints = 50;
    public static int wyvernToolsMaxUpgrades = 3;
    public static int wyvernToolsMaxUpgradePoints = 50;
    public static int draconicToolsMaxCapacityUpgradePoints = 50;
    public static int draconicToolsMaxUpgrades = 6;
    public static int draconicToolsMaxUpgradePoints = 50;
    public static int wyvernWeaponsMaxCapacityUpgradePoints = 50;
    public static int wyvernWeaponsMaxUpgrades = 3;
    public static int wyvernWeaponsMaxUpgradePoints = 50;
    public static int draconicWeaponsMaxCapacityUpgradePoints = 50;
    public static int draconicWeaponsMaxUpgrades = 6;
    public static int draconicWeaponsMaxUpgradePoints = 50;
    public static int wyvernBowMaxCapacityUpgradePoints = 50;
    public static int wyvernBowMaxUpgrades = 3;
    public static int wyvernBowMaxUpgradePoints = 50;
    public static int draconicBowMaxCapacityUpgradePoints = 50;
    public static int draconicBowMaxUpgrades = 6;
    public static int draconicBowMaxUpgradePoints = 50;
    public static int draconicStaffMaxCapacityUpgradePoints = 50;
    public static int draconicStaffMaxUpgrades = 12;
    public static int draconicStaffMaxUpgradePoints = 50;
    public static int wyvernCapacitorMaxUpgradePoints = 50;
    public static int wyvernCapacitorMaxCapacityUpgradePoints = 50;
    public static int wyvernCapacitorMaxUpgrades = 3;
    public static int draconicCapacitorMaxUpgradePoints = 50;
    public static int draconicCapacitorMaxCapacityUpgradePoints = 50;
    public static int draconicCapacitorMaxUpgrades = 6;
    public static Block energyStorageStructureBlock = null;
    public static int energyStorageStructureBlockMetadata = 0;
    public static Block energyStorageStructureOuterBlock = null;
    public static int energyStorageStructureOuterBlockMetadata = 0;
    public static boolean grinderShouldUseLooting = false;
    public static int energyDeviceBasicLinkingRange = 25;
    public static int energyDeviceAdvancedLinkingRange = 50;
    private static Configuration config;

    public static void init(File modConfigurationDirectory) {
        if (config == null) {
            config = new Configuration(new File(modConfigurationDirectory, "DraconicEvolution.Balance.cfg"));
            config.load();
            config.setCategoryRequiresMcRestart("tweaks", true);
            config.setCategoryComment(
                    "tweaks.armor", "Values in this category may be replaced automatically to prevent problems");
            config.setCategoryComment(
                    "tweaks.tools", "Values in this category may be replaced automatically to prevent problems");
            config.setCategoryComment(
                    "tweaks.weapons", "Values in this category may be replaced automatically to prevent problems");
            syncConfig();
        }
    }

    private static void syncConfig() {
        wyvernArmorBaseStorage =
                getInteger("energy.armor", "Wyvern Armor: Base energy storage (RF)", wyvernArmorBaseStorage);
        wyvernArmorStoragePerUpgrade = getInteger(
                "energy.armor",
                "Wyvern Armor: Additional energy storage per upgrade installed (RF)",
                wyvernArmorStoragePerUpgrade);
        wyvernArmorMaxTransfer =
                getInteger("energy.armor", "Wyvern Armor: Maximum energy transfer rate (RF/t)", wyvernArmorMaxTransfer);
        wyvernArmorEnergyPerProtectionPoint = getInteger(
                "energy.armor",
                "Wyvern Armor: Amount of energy required to restore protection point (RF)",
                wyvernArmorEnergyPerProtectionPoint);
        draconicArmorBaseStorage =
                getInteger("energy.armor", "Draconic Armor: Base energy storage (RF)", draconicArmorBaseStorage);
        draconicArmorStoragePerUpgrade = getInteger(
                "energy.armor",
                "Draconic Armor: Additional energy storage per upgrade installed (RF)",
                draconicArmorStoragePerUpgrade);
        draconicArmorMaxTransfer = getInteger(
                "energy.armor", "Draconic Armor: Maximum energy transfer rate (RF/t)", draconicArmorMaxTransfer);
        draconicArmorEnergyPerProtectionPoint = getInteger(
                "energy.armor",
                "Draconic Armor: Amount of energy required to restore protection point (RF)",
                draconicArmorEnergyPerProtectionPoint);
        draconicArmorEnergyToRemoveEffects = getInteger(
                "energy.armor",
                "Draconic Armor: Amount of energy required to remove negative effects (RF)",
                draconicArmorEnergyToRemoveEffects);
        wyvernToolsBaseStorage =
                getInteger("energy.tools", "Wyvern Tools: Base energy storage (RF)", wyvernToolsBaseStorage);
        wyvernToolsStoragePerUpgrade = getInteger(
                "energy.tools",
                "Wyvern Tools: Additional energy storage per upgrade installed (RF)",
                wyvernToolsStoragePerUpgrade);
        wyvernToolsMaxTransfer =
                getInteger("energy.tools", "Wyvern Tools: Maximum energy transfer rate (RF/t)", wyvernToolsMaxTransfer);
        wyvernToolsEnergyPerAction = getInteger(
                "energy.tools",
                "Wyvern Tools: Amount of energy required to perform action (RF)",
                wyvernToolsEnergyPerAction);
        draconicToolsBaseStorage =
                getInteger("energy.tools", "Draconic Tools: Base energy storage (RF)", draconicToolsBaseStorage);
        draconicToolsStoragePerUpgrade = getInteger(
                "energy.tools",
                "Draconic Tools: Additional energy storage per upgrade installed (RF)",
                draconicToolsStoragePerUpgrade);
        draconicToolsMaxTransfer = getInteger(
                "energy.tools", "Draconic Tools: Maximum energy transfer rate (RF/t)", draconicToolsMaxTransfer);
        draconicToolsEnergyPerAction = getInteger(
                "energy.tools",
                "Draconic Tools: Amount of energy required to perform action (RF)",
                draconicToolsEnergyPerAction);
        wyvernCapacitorBaseStorage = getInteger(
                "energy.tools", "Wyvern Flux Capacitor: Base energy storage (RF)", wyvernCapacitorBaseStorage);
        wyvernCapacitorStoragePerUpgrade = getInteger(
                "energy.tools",
                "Wyvern Flux Capacitor: Additional energy storage per upgrade installed (RF)",
                wyvernCapacitorStoragePerUpgrade);
        wyvernCapacitorMaxReceive = getInteger(
                "energy.tools",
                "Wyvern Flux Capacitor: Maximum energy reception rate (RF/t)",
                wyvernCapacitorMaxReceive);
        wyvernCapacitorMaxExtract = getInteger(
                "energy.tools",
                "Wyvern Flux Capacitor: Maximum energy extraction rate (RF/t)",
                wyvernCapacitorMaxExtract);
        draconicCapacitorBaseStorage = getInteger(
                "energy.tools", "Draconic Flux Capacitor: Base energy storage (RF)", draconicCapacitorBaseStorage);
        draconicCapacitorStoragePerUpgrade = getInteger(
                "energy.tools",
                "Draconic Flux Capacitor: Additional energy storage per upgrade installed (RF)",
                draconicCapacitorStoragePerUpgrade);
        draconicCapacitorMaxReceive = getInteger(
                "energy.tools",
                "Draconic Flux Capacitor: Maximum energy reception rate (RF/t)",
                draconicCapacitorMaxReceive);
        draconicCapacitorMaxExtract = getInteger(
                "energy.tools",
                "Draconic Flux Capacitor: Maximum energy extraction rate (RF/t)",
                draconicCapacitorMaxExtract);
        wyvernWeaponsBaseStorage =
                getInteger("energy.weapons", "Wyvern Weapons: Base energy storage (RF)", wyvernWeaponsBaseStorage);
        wyvernWeaponsStoragePerUpgrade = getInteger(
                "energy.weapons",
                "Wyvern Weapons: Additional energy storage per upgrade installed (RF)",
                wyvernWeaponsStoragePerUpgrade);
        wyvernWeaponsMaxTransfer = getInteger(
                "energy.weapons", "Wyvern Weapons: Maximum energy transfer rate (RF/t)", wyvernWeaponsMaxTransfer);
        wyvernWeaponsEnergyPerAttack = getInteger(
                "energy.weapons",
                "Wyvern Weapons: Amount of energy required to perform attack (RF)",
                wyvernWeaponsEnergyPerAttack);
        wyvernBowEnergyPerShot = getInteger(
                "energy.weapons", "Wyvern Bow: Amount of energy required to shoot (RF)", wyvernBowEnergyPerShot);
        draconicWeaponsBaseStorage =
                getInteger("energy.weapons", "Draconic Weapons: Base energy storage (RF)", draconicWeaponsBaseStorage);
        draconicWeaponsStoragePerUpgrade = getInteger(
                "energy.weapons",
                "Draconic Weapons: Additional energy storage per upgrade installed (RF)",
                draconicWeaponsStoragePerUpgrade);
        draconicWeaponsMaxTransfer = getInteger(
                "energy.weapons", "Draconic Weapons: Maximum energy transfer rate (RF/t)", draconicWeaponsMaxTransfer);
        draconicWeaponsEnergyPerAttack = getInteger(
                "energy.weapons",
                "Draconic Weapons: Amount of energy required to perform attack (RF)",
                draconicWeaponsEnergyPerAttack);
        draconicBowEnergyPerShot = getInteger(
                "energy.weapons", "Draconic Bow: Amount of energy required to shoot (RF)", draconicBowEnergyPerShot);
        draconicFireEnergyCostMultiptier = getInteger(
                "energy.weapons", "Arrow of Draconic Fire: Energy cost multiplier", draconicFireEnergyCostMultiptier);
        draconiumBlockEnergyToChange = getInteger(
                "energy.misc",
                "Draconium Block: Amount of energy required to charge (RF)",
                draconiumBlockEnergyToChange);
        draconiumBlockChargingSpeed = getInteger(
                "energy.misc", "Draconium Block: Maximum charging speed (RF/t)", draconiumBlockChargingSpeed);
        energyInfuserStorage =
                getInteger("energy.machines", "Energy Infuser: Energy buffer size (RF)", energyInfuserStorage);
        energyInfuserMaxTransfer = getInteger(
                "energy.machines", "Energy Infuser: Maximum energy transfer rate (RF/t)", energyInfuserMaxTransfer);
        energyStorageTier1Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 1: Energy buffer size (RF)",
                energyStorageTier1Storage);
        energyStorageTier2Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 2: Energy buffer size (RF)",
                energyStorageTier2Storage);
        energyStorageTier3Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 3: Energy buffer size (RF)",
                energyStorageTier3Storage);
        energyStorageTier4Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 4: Energy buffer size (RF)",
                energyStorageTier4Storage);
        energyStorageTier5Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 5: Energy buffer size (RF)",
                energyStorageTier5Storage);
        energyStorageTier6Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 6: Energy buffer size (RF)",
                energyStorageTier6Storage);
        energyStorageTier7Storage = getLong(
                "energy.machines",
                "Multiblock Energy Storage Tier 7: Energy buffer size (RF)",
                energyStorageTier7Storage);
        grinderInternalEnergyBufferSize = getInteger(
                "energy.machines", "Mob Grinder: Internal energy buffer size (RF)", grinderInternalEnergyBufferSize);
        grinderExternalEnergyBufferSize = getInteger(
                "energy.machines", "Mob Grinder: Main energy buffer size (RF)", grinderExternalEnergyBufferSize);
        grinderMaxReceive =
                getInteger("energy.machines", "Mob Grinder: Maximum energy reception rate (RF/t)", grinderMaxReceive);
        grinderEnergyPerKill = getInteger(
                "energy.machines", "Mob Grinder: Amount of energy required to kill entity (RF)", grinderEnergyPerKill);
        energyRelayBasicStorage =
                getInteger("energy.machines", "Energy Relay: Energy buffer size (RF)", energyRelayBasicStorage);
        energyRelayAdvancedStorage = getInteger(
                "energy.machines", "Advanced Energy Relay: Energy buffer size (RF)", energyRelayAdvancedStorage);
        energyRelayBasicMaxReceive = getInteger(
                "energy.machines", "Energy Relay: Maximum energy reception rate (RF/t)", energyRelayBasicMaxReceive);
        energyRelayAdvancedMaxReceive = getInteger(
                "energy.machines",
                "Advanced Energy Relay: Maximum energy reception rate (RF/t)",
                energyRelayAdvancedMaxReceive);
        energyRelayBasicMaxExtract = getInteger(
                "energy.machines", "Energy Relay: Maximum energy extraction rate (RF/t)", energyRelayBasicMaxExtract);
        energyRelayAdvancedMaxExtract = getInteger(
                "energy.machines",
                "Advanced Energy Relay: Maximum energy extraction rate (RF/t)",
                energyRelayAdvancedMaxExtract);
        energyTransceiverBasicStorage = getInteger(
                "energy.machines", "Energy Transceiver: Energy buffer size (RF)", energyTransceiverBasicStorage);
        energyTransceiverAdvancedStorage = getInteger(
                "energy.machines",
                "Advanced Energy Transceiver: Energy buffer size (RF)",
                energyTransceiverAdvancedStorage);
        energyTransceiverBasicMaxReceive = getInteger(
                "energy.machines",
                "Energy Transceiver: Maximum energy reception rate (RF/t)",
                energyTransceiverBasicMaxReceive);
        energyTransceiverAdvancedMaxReceive = getInteger(
                "energy.machines",
                "Advanced Energy Transceiver: Maximum energy reception rate (RF/t)",
                energyTransceiverAdvancedMaxReceive);
        energyTransceiverBasicMaxExtract = getInteger(
                "energy.machines",
                "Energy Transceiver: Maximum energy extraction rate (RF/t)",
                energyTransceiverBasicMaxExtract);
        energyTransceiverAdvancedMaxExtract = getInteger(
                "energy.machines",
                "Advanced Energy Transceiver: Maximum energy extraction rate (RF/t)",
                energyTransceiverAdvancedMaxExtract);
        energyWirelessTransceiverBasicStorage = getInteger(
                "energy.machines",
                "Wireless Energy Transceiver: Energy buffer size (RF)",
                energyWirelessTransceiverBasicStorage);
        energyWirelessTransceiverAdvancedStorage = getInteger(
                "energy.machines",
                "Advanced Wireless Energy Transceiver: Energy buffer size (RF)",
                energyWirelessTransceiverAdvancedStorage);
        energyWirelessTransceiverBasicMaxReceive = getInteger(
                "energy.machines",
                "Wireless Energy Transceiver: Maximum energy reception rate (RF/t)",
                energyWirelessTransceiverBasicMaxReceive);
        energyWirelessTransceiverAdvancedMaxReceive = getInteger(
                "energy.machines",
                "Advanced Wireless Energy Transceiver: Maximum energy reception rate (RF/t)",
                energyWirelessTransceiverAdvancedMaxReceive);
        energyWirelessTransceiverBasicMaxExtract = getInteger(
                "energy.machines",
                "Wireless Energy Transceiver: Maximum energy extraction rate (RF/t)",
                energyWirelessTransceiverBasicMaxExtract);
        energyWirelessTransceiverAdvancedMaxExtract = getInteger(
                "energy.machines",
                "Advanced Wireless Energy Transceiver: Maximum energy extraction rate (RF/t)",
                energyWirelessTransceiverAdvancedMaxExtract);
        energyWirelessTransceiverBasicMaxSend = getInteger(
                "energy.machines",
                "Wireless Energy Transceiver: Maximum energy sending rate " + "for each linked device (RF/t)",
                energyWirelessTransceiverBasicMaxSend);
        energyWirelessTransceiverAdvancedMaxSend = getInteger(
                "energy.machines",
                "Advanced Wireless Energy Transceiver: Maximum energy " + "sending rate for each linked device (RF/t)",
                energyWirelessTransceiverAdvancedMaxSend);
        wyvernArmorMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - wyvernArmorBaseStorage)
                        / (double) Math.max(wyvernArmorStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        wyvernArmorMaxUpgrades =
                getInteger("tweaks.armor", "Wyvern Armor: Maximum amount of upgrades", wyvernArmorMaxUpgrades);
        wyvernArmorMaxUpgradePoints = getInteger(
                "tweaks.armor",
                "Wyvern Armor: Maximum amount of upgrade points",
                wyvernArmorMaxUpgradePoints,
                wyvernArmorMaxUpgrades,
                Integer.MAX_VALUE);
        wyvernArmorMaxCapacityUpgradePoints =
                Math.max(Math.min(wyvernArmorMaxUpgradePoints, wyvernArmorMaxCapacityUpgradePoints), 0);
        draconicArmorMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - draconicArmorBaseStorage)
                        / Math.max(draconicArmorStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicArmorMaxUpgrades =
                getInteger("tweaks.armor", "Draconic Armor: Maximum amount of upgrades", draconicArmorMaxUpgrades);
        draconicArmorMaxUpgradePoints = getInteger(
                "tweaks.armor",
                "Draconic Armor: Maximum amount of upgrade points",
                draconicArmorMaxUpgradePoints,
                draconicArmorMaxUpgrades,
                Integer.MAX_VALUE);
        draconicArmorMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicArmorMaxUpgradePoints, draconicArmorMaxCapacityUpgradePoints), 0);
        wyvernToolsMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - wyvernToolsBaseStorage)
                        / (double) Math.max(wyvernToolsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        wyvernToolsMaxUpgrades = getInteger(
                "tweaks.tools",
                "Wyvern Tools: Maximum amount of upgrades",
                wyvernToolsMaxUpgrades,
                0,
                (wyvernToolsMaxDigAOEUpgradePoints - wyvernToolsMinDigAOEUpgradePoints)
                                * EnumUpgrade.DIG_AOE.pointConversion
                        + (wyvernToolsMaxDigSpeedUpgradePoints - wyvernToolsMinDigSpeedUpgradePoints)
                                * EnumUpgrade.DIG_SPEED.pointConversion
                        + wyvernToolsMaxCapacityUpgradePoints);
        wyvernToolsMaxUpgradePoints = getInteger(
                "tweaks.tools",
                "Wyvern Tools: Maximum amount of upgrade points",
                wyvernToolsMaxUpgradePoints,
                wyvernToolsMaxUpgrades,
                Integer.MAX_VALUE);
        wyvernToolsMaxCapacityUpgradePoints =
                Math.max(Math.min(wyvernToolsMaxUpgradePoints, wyvernToolsMaxCapacityUpgradePoints), 0);
        draconicToolsMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - draconicToolsBaseStorage)
                        / (double) Math.max(draconicToolsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicToolsMaxUpgrades = getInteger(
                "tweaks.tools",
                "Draconic Tools: Maximum amount of upgrades",
                draconicToolsMaxUpgrades,
                0,
                (draconicToolsMaxDigAOEUpgradePoints - draconicToolsMinDigAOEUpgradePoints)
                                * EnumUpgrade.DIG_AOE.pointConversion
                        + (draconicToolsMaxDigSpeedUpgradePoints - draconicToolsMinDigSpeedUpgradePoints)
                                * EnumUpgrade.DIG_SPEED.pointConversion
                        + (draconicToolsMaxDigDepthUpgradePoints - draconicToolsMinDigDepthUpgradePoints)
                                * EnumUpgrade.DIG_DEPTH.pointConversion
                        + draconicToolsMaxCapacityUpgradePoints);
        draconicToolsMaxUpgradePoints = getInteger(
                "tweaks.tools",
                "Draconic Tools: Maximum amount of upgrade points",
                draconicToolsMaxUpgradePoints,
                draconicToolsMaxUpgrades,
                Integer.MAX_VALUE);
        draconicToolsMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicToolsMaxUpgradePoints, draconicToolsMaxCapacityUpgradePoints), 0);
        wyvernWeaponsMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - wyvernWeaponsBaseStorage)
                        / (double) Math.max(wyvernWeaponsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        wyvernWeaponsMaxUpgrades = getInteger(
                "tweaks.weapons",
                "Wyvern Weapons: Maximum amount of upgrades",
                wyvernWeaponsMaxUpgrades,
                0,
                (wyvernWeaponsMaxAttackAOEUpgradePoints - wyvernWeaponsMinAttackAOEUpgradePoints)
                                * EnumUpgrade.ATTACK_AOE.pointConversion
                        + (wyvernWeaponsMaxAttackDamageUpgradePoints - wyvernWeaponsMinAttackDamageUpgradePoints)
                                * EnumUpgrade.ATTACK_DAMAGE.pointConversion
                        + wyvernWeaponsMaxCapacityUpgradePoints);
        wyvernWeaponsMaxUpgradePoints = getInteger(
                "tweaks.weapons",
                "Wyvern Weapons: Maximum amount of upgrade points",
                wyvernWeaponsMaxUpgradePoints,
                wyvernWeaponsMaxUpgrades,
                Integer.MAX_VALUE);
        wyvernWeaponsMaxCapacityUpgradePoints =
                Math.max(Math.min(wyvernWeaponsMaxUpgradePoints, wyvernWeaponsMaxCapacityUpgradePoints), 0);
        draconicWeaponsMaxCapacityUpgradePoints =
                (int) Math.floor((double) (Integer.MAX_VALUE - draconicWeaponsBaseStorage)
                                / (double) Math.max(draconicWeaponsStoragePerUpgrade, 1))
                        * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicWeaponsMaxUpgrades = getInteger(
                "tweaks.weapons",
                "Draconic Weapons: Maximum amount of upgrades",
                draconicWeaponsMaxUpgrades,
                0,
                (draconicWeaponsMaxAttackAOEUpgradePoints - draconicWeaponsMinAttackAOEUpgradePoints)
                                * EnumUpgrade.ATTACK_AOE.pointConversion
                        + (draconicWeaponsMaxAttackDamageUpgradePoints - draconicWeaponsMinAttackDamageUpgradePoints)
                                * EnumUpgrade.ARROW_DAMAGE.pointConversion
                        + draconicWeaponsMaxCapacityUpgradePoints);
        draconicWeaponsMaxUpgradePoints = getInteger(
                "tweaks.weapons",
                "Draconic Weapons: Maximum amount of upgrade points",
                draconicWeaponsMaxUpgradePoints,
                draconicWeaponsMaxUpgrades,
                Integer.MAX_VALUE);
        draconicWeaponsMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicWeaponsMaxUpgradePoints, draconicWeaponsMaxCapacityUpgradePoints), 0);
        wyvernBowMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - wyvernWeaponsBaseStorage)
                        / (double) Math.max(wyvernWeaponsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        wyvernBowMaxUpgrades = getInteger(
                "tweaks.weapons",
                "Wyvern Bow: Maximum amount of upgrades",
                wyvernBowMaxUpgrades,
                0,
                (wyvernBowMaxDrawSpeedUpgradePoints - wyvernBowMinDrawSpeedUpgradePoints)
                                * EnumUpgrade.DRAW_SPEED.pointConversion
                        + (wyvernBowMaxArrowSpeedUpgradePoints - wyvernBowMinArrowSpeedUpgradePoints)
                                * EnumUpgrade.ARROW_SPEED.pointConversion
                        + (wyvernBowMaxArrowDamageUpgradePoints - wyvernBowMinArrowDamageUpgradePoints)
                                * EnumUpgrade.ARROW_DAMAGE.pointConversion
                        + wyvernBowMaxCapacityUpgradePoints);
        wyvernBowMaxUpgradePoints = getInteger(
                "tweaks.weapons",
                "Wyvern Bow: Maximum amount of upgrade points",
                wyvernBowMaxUpgradePoints,
                wyvernBowMaxUpgrades,
                Integer.MAX_VALUE);
        wyvernBowMaxCapacityUpgradePoints =
                Math.max(Math.min(wyvernBowMaxUpgradePoints, wyvernBowMaxCapacityUpgradePoints), 0);
        draconicBowMaxCapacityUpgradePoints = (int) Math.floor((double) (Integer.MAX_VALUE - draconicWeaponsBaseStorage)
                        / (double) Math.max(draconicWeaponsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicBowMaxUpgrades = getInteger(
                "tweaks.weapons",
                "Draconic Bow: Maximum amount of upgrades",
                draconicBowMaxUpgrades,
                0,
                (draconicBowMaxDrawSpeedUpgradePoints - draconicBowMinDrawSpeedUpgradePoints)
                                * EnumUpgrade.DRAW_SPEED.pointConversion
                        + (draconicBowMaxArrowSpeedUpgradePoints - draconicBowMinArrowSpeedUpgradePoints)
                                * EnumUpgrade.ARROW_SPEED.pointConversion
                        + (draconicBowMaxArrowDamageUpgradePoints - draconicBowMinArrowDamageUpgradePoints)
                                * EnumUpgrade.ARROW_DAMAGE.pointConversion
                        + draconicBowMaxCapacityUpgradePoints);
        draconicBowMaxUpgradePoints = getInteger(
                "tweaks.weapons",
                "Draconic Bow: Maximum amount of upgrade points",
                draconicBowMaxUpgradePoints,
                draconicBowMaxUpgrades,
                Integer.MAX_VALUE);
        draconicBowMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicBowMaxUpgradePoints, draconicBowMaxCapacityUpgradePoints), 0);
        draconicStaffMaxCapacityUpgradePoints = (int) Math.floor((double)
                                (Integer.MAX_VALUE - draconicToolsBaseStorage * 2 - draconicWeaponsBaseStorage)
                        / (double) Math.max(draconicToolsStoragePerUpgrade + draconicWeaponsStoragePerUpgrade, 1))
                * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicStaffMaxUpgrades = draconicToolsMaxUpgrades + draconicWeaponsMaxUpgrades;
        draconicStaffMaxUpgradePoints = draconicToolsMaxUpgradePoints + draconicWeaponsMaxUpgradePoints;
        draconicStaffMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicStaffMaxUpgradePoints, draconicStaffMaxCapacityUpgradePoints), 0);
        wyvernCapacitorMaxCapacityUpgradePoints =
                (int) Math.floor((double) (Integer.MAX_VALUE - wyvernCapacitorBaseStorage)
                                / (double) Math.max(wyvernCapacitorStoragePerUpgrade, 1))
                        * EnumUpgrade.RF_CAPACITY.pointConversion;
        wyvernCapacitorMaxUpgrades = getInteger(
                "tweaks.tools",
                "Wyvern Flux Capacitor: Maximum amount of upgrades",
                wyvernCapacitorMaxUpgrades,
                0,
                wyvernCapacitorMaxCapacityUpgradePoints);
        wyvernCapacitorMaxUpgradePoints = getInteger(
                "tweaks.tools",
                "Wyvern Flux Capacitor: Maximum amount of upgrade points",
                wyvernCapacitorMaxUpgradePoints,
                wyvernCapacitorMaxUpgrades,
                Integer.MAX_VALUE);
        wyvernCapacitorMaxCapacityUpgradePoints =
                Math.max(Math.min(wyvernCapacitorMaxUpgradePoints, wyvernCapacitorMaxCapacityUpgradePoints), 0);
        draconicCapacitorMaxCapacityUpgradePoints =
                (int) Math.floor((double) (Integer.MAX_VALUE - draconicCapacitorBaseStorage)
                                / (double) Math.max(draconicCapacitorStoragePerUpgrade, 1))
                        * EnumUpgrade.RF_CAPACITY.pointConversion;
        draconicCapacitorMaxUpgrades = getInteger(
                "tweaks.tools",
                "Draconic Flux Capacitor: Maximum amount of upgrades",
                draconicCapacitorMaxUpgrades,
                0,
                draconicCapacitorMaxCapacityUpgradePoints);
        draconicCapacitorMaxUpgradePoints = getInteger(
                "tweaks.tools",
                "Draconic Flux Capacitor: Maximum amount of upgrade points",
                draconicCapacitorMaxUpgradePoints,
                draconicCapacitorMaxUpgrades,
                Integer.MAX_VALUE);
        draconicCapacitorMaxCapacityUpgradePoints =
                Math.max(Math.min(draconicCapacitorMaxUpgradePoints, draconicCapacitorMaxCapacityUpgradePoints), 0);
        grinderShouldUseLooting =
                getBoolean("tweaks.machines", "Mob Grinder: Use Looting enchantment", grinderShouldUseLooting);
        energyDeviceBasicLinkingRange = getInteger(
                "tweaks.machines",
                "Energy Device (Relay/Transceiver): Linking range",
                energyDeviceBasicLinkingRange,
                8,
                32);
        energyDeviceAdvancedLinkingRange = getInteger(
                "tweaks.machines",
                "Advanced Energy Device (Relay/Transceiver): Linking range",
                energyDeviceAdvancedLinkingRange,
                16,
                64);
        if (config.hasChanged()) {
            config.save();
        }
    }

    // This method should be loaded after all mods add blocks => after pre-init
    public static void finishLoading() {
        if (config == null) {
            return;
        }
        energyStorageStructureBlock = getBlock(
                "tweaks.machines",
                "Multiblock Energy Storage: Main block of structure",
                Blocks.redstone_block,
                "WARNING! Changing of this value will replace blocks of all existing Energy Storage Multiblocks!");
        energyStorageStructureBlockMetadata = getInteger(
                "tweaks.machines",
                "Multiblock Energy Storage: Metadata of main block of structure",
                energyStorageStructureBlockMetadata,
                "WARNING! Changing of this value will replace blocks of all existing Energy Storage " + "Multiblocks!");
        energyStorageStructureOuterBlock = getBlock(
                "tweaks.machines",
                "Multiblock Energy Storage: Outer block of structure",
                ModBlocks.draconiumBlock,
                "WARNING! Changing of this value will replace blocks of all existing Energy Storage Multiblocks!");
        energyStorageStructureOuterBlockMetadata = getInteger(
                "tweaks.machines",
                "Multiblock Energy Storage: Metadata of outer block of structure",
                energyStorageStructureBlockMetadata,
                "WARNING! Changing of this value will replace blocks of all existing Energy Storage " + "Multiblocks!");
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static Block getBlock(String category, String propertyName, Block defaultValue, String comment) {
        String defaultName = Block.blockRegistry.getNameForObject(defaultValue);
        Property property = config.get(category, propertyName, defaultName, comment);
        String value = property.getString();
        if (value == null || !value.contains(":")) {
            property.set(defaultName);
            return defaultValue;
        }
        String modId = value.substring(0, value.indexOf(":"));
        String name = value.substring(value.indexOf(":") + 1);
        Block block = GameRegistry.findBlock(modId, name);
        if (block == null || block instanceof ITileEntityProvider) {
            property.set(defaultName);
            return defaultValue;
        }
        return block;
    }

    private static boolean getBoolean(String category, String propertyName, boolean defaultValue) {
        return config.get(category, propertyName, defaultValue).getBoolean(defaultValue);
    }

    private static int getInteger(String categoty, String propertyName, int defaultValue) {
        return config.get(categoty, propertyName, defaultValue).getInt(defaultValue);
    }

    private static int getInteger(String categoty, String propertyName, int defaultValue, String comment) {
        return config.get(categoty, propertyName, defaultValue, comment).getInt(defaultValue);
    }

    private static int getInteger(String category, String propertyName, int defaultValue, int minValue, int maxValue) {
        Property property = config.get(category, propertyName, defaultValue, "", minValue, maxValue);
        int value = property.getInt(defaultValue);
        if (value < minValue) {
            property.set(minValue);
            return minValue;
        }
        if (value > maxValue) {
            property.set(maxValue);
            return maxValue;
        }
        return value;
    }

    private static long getLong(String category, String propertyName, long defaultValue) {
        return (long) config.get(category, propertyName, (double) defaultValue, "", 0D, (double) Long.MAX_VALUE)
                .getDouble((double) defaultValue);
    }
}
