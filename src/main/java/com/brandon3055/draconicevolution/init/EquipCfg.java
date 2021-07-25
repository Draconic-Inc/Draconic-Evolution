package com.brandon3055.draconicevolution.init;

import codechicken.lib.config.ConfigTag;
import com.brandon3055.brandonscore.api.TechLevel;

//EquipmentConfig. Shortened to make the tool constructor super calls a little less bloated
public class EquipCfg {

    //@formatter:off
    //Default Values
    private static double DRACONIUM_EFFICIENCY      = 10D;
    private static double WYVERN_EFFICIENCY         = 15D;
    private static double DRACONIC_EFFICIENCY       = 25D;
    private static double CHAOTIC_EFFICIENCY        = 50D;

    private static double DRACONIUM_DAMAGE          = 1.125D;
    private static double WYVERN_DAMAGE             = 1.250D;
    private static double DRACONIC_DAMAGE           = 1.750D;
    private static double CHAOTIC_DAMAGE            = 2.500D;

    private static double DRACONIUM_SPEED           = 1.125D;
    private static double WYVERN_SPEED              = 1.250D;
    private static double DRACONIC_SPEED            = 1.500D;
    private static double CHAOTIC_SPEED             = 2.000D;

    private static int DRACONIUM_ENCHANT            = 12;
    private static int WYVERN_ENCHANT               = 15;
    private static int DRACONIC_ENCHANT             = 25;
    private static int CHAOTIC_ENCHANT              = 35;

    //These ratios are based on diamond tools
    private static double STAFF_DMG_MULT            = 9.0D;
    private static double SWORD_DMG_MULT            = 7.0D;
    private static double AXE_DMG_MULT              = 9.0D;
    private static double PICKAXE_DMG_MULT          = 5.0D;
    private static double SHOVEL_DMG_MULT           = 5.5D;
    private static double HOE_DMG_MULT              = 1.0D;

    //These ratios are based on diamond tools
    private static double STAFF_SPEED_MULT          = 0.5D;
    private static double SWORD_SPEED_MULT          = 1.6D;
    private static double AXE_SPEED_MULT            = 1.0D;
    private static double PICKAXE_SPEED_MULT        = 1.2D;
    private static double SHOVEL_SPEED_MULT         = 1.0D;
    private static double HOE_SPEED_MULT            = 4.0D;

    private static double STAFF_EFF_MULT            = 3.0D;

    private static long DRACONIUM_BASE_ENERGY       = 0;//500000;
    private static long WYVERN_BASE_ENERGY          = 0;//2000000;
    private static long DRACONIC_BASE_ENERGY        = 0;//4000000;
    private static long CHAOTIC_BASE_ENERGY         = 0;//8000000;
    
    private static double STAFF_ENERGY_MULT         = 3D;
    private static double TOOL_ENERGY_MULT          = 1D;
    private static double CAPACITOR_ENERGY_MULT     = 8D;
    private static double CHESTPIECE_ENERGY_MULT    = 2D;

    private static int ENERGY_HARVEST               = 256;
    private static int ENERGY_ATTACK                = 1024;
    private static int ENERGY_SHIELD_CHG            = 8192;

    private static double SHIELD_PASSIVE_MODIFIER   = 0.0005;

    private static int ELYTRA_FLIGHT_ENERGY         = 1024;
    private static int CREATIVE_FLIGHT_ENERGY       = 4096;

    private static int ELYTRA_WYVERN_ENERGY         = 1024;
    private static int ELYTRA_DRACONIC_ENERGY       = 2048;
    private static int ELYTRA_CHAOTIC_ENERGY        = 8192;

    private static int BOW_BASE_ENERGY              = 1024; //Energy per calculated damage point

    //Static Access values
    public static double draconiumEfficiency;
    public static double wyvernEfficiency;
    public static double draconicEfficiency;
    public static double chaoticEfficiency;

    public static double draconiumDamage;
    public static double wyvernDamage;
    public static double draconicDamage;
    public static double chaoticDamage;

    public static double draconiumSpeed;
    public static double wyvernSpeed;
    public static double draconicSpeed;
    public static double chaoticSpeed;

    public static int draconiumEnchantability;
    public static int wyvernEnchantability;
    public static int draconicEnchantability;
    public static int chaoticEnchantability;

    public static double staffDamageMultiplier;
    public static double swordDamageMultiplier;
    public static double axeDamageMultiplier;
    public static double pickaxeDamageMultiplier;
    public static double shovelDamageMultiplier;
    public static double hoeDamageMultiplier;

    public static double staffSpeedMultiplier;
    public static double swordSpeedMultiplier;
    public static double axeSpeedMultiplier;
    public static double pickaxeSpeedMultiplier;
    public static double shovelSpeedMultiplier;
    public static double hoeSpeedMultiplier;

    public static double staffEffMultiplier;

    public static long draconiumBaseEnergy;
    public static long wyvernBaseEnergy;
    public static long draconicBaseEnergy;
    public static long chaoticBaseEnergy;

    public static double staffEnergyMult;
    public static double toolEnergyMult;
    public static double capacitorEnergyMult;
    public static double chestpieceEnergyMult;

    public static int energyHarvest;
    public static int energyAttack;
    public static int energyShieldChg;

    public static double shieldPassiveModifier;

    public static int elytraFlightEnergy;
    public static int creativeFlightEnergy;

    public static int elytraWyvernEnergy;
    public static int elytraDraconicEnergy;
    public static int elytraChaoticEnergy;

    public static int bowBaseEnergy;

    //@formatter:on

    public static void loadConfig(ConfigTag configTag) {
        ConfigTag equipTag = configTag.getTag("Equipment");
        equipTag.setComment("These settings allow you to override the base stats for DE's equipment.",
                "Please note the generated default values \"-99\" is actually a marker that tells DE to use the actual internal default value.",
                "This value is listed in each properties description but may not be valid if this config was generated by a previous version of DE.");

        equipTag.getTag("draconiumEfficiency")
                .setComment("Internal Default Value: " + DRACONIUM_EFFICIENCY)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconiumEfficiency = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_EFFICIENCY);
        equipTag.getTag("wyvernEfficiency")
                .setComment("Internal Default Value: " + WYVERN_EFFICIENCY)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> wyvernEfficiency = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_EFFICIENCY);
        equipTag.getTag("draconicEfficiency")
                .setComment("Internal Default Value: " + DRACONIC_EFFICIENCY)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconicEfficiency = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_EFFICIENCY);
        equipTag.getTag("chaoticEfficiency")
                .setComment("Internal Default Value: " + CHAOTIC_EFFICIENCY)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> chaoticEfficiency = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_EFFICIENCY);

        equipTag.getTag("draconiumDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + DRACONIUM_DAMAGE)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconiumDamage = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_DAMAGE);
        equipTag.getTag("wyvernDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + WYVERN_DAMAGE)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> wyvernDamage = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_DAMAGE);
        equipTag.getTag("draconicDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + DRACONIC_DAMAGE)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconicDamage = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_DAMAGE);
        equipTag.getTag("chaoticDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + CHAOTIC_DAMAGE)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> chaoticDamage = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_DAMAGE);

        equipTag.getTag("draconiumSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + DRACONIUM_SPEED)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconiumSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_SPEED);
        equipTag.getTag("wyvernSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + WYVERN_SPEED)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> wyvernSpeed = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_SPEED);
        equipTag.getTag("draconicSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + DRACONIC_SPEED)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> draconicSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_SPEED);
        equipTag.getTag("chaoticSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + CHAOTIC_SPEED)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> chaoticSpeed = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_SPEED);

        equipTag.getTag("draconiumEnchantability")
                .setComment("Internal Default Value: " + DRACONIUM_ENCHANT)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> draconiumEnchantability = tag.getInt() != -99 ? tag.getInt() : DRACONIUM_ENCHANT);
        equipTag.getTag("wyvernEnchantability")
                .setComment("Internal Default Value: " + WYVERN_ENCHANT)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> wyvernEnchantability = tag.getInt() != -99 ? tag.getInt() : WYVERN_ENCHANT);
        equipTag.getTag("draconicEnchantability")
                .setComment("Internal Default Value: " + DRACONIC_ENCHANT)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> draconicEnchantability = tag.getInt() != -99 ? tag.getInt() : DRACONIC_ENCHANT);
        equipTag.getTag("chaoticEnchantability")
                .setComment("Internal Default Value: " + CHAOTIC_ENCHANT)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> chaoticEnchantability = tag.getInt() != -99 ? tag.getInt() : CHAOTIC_ENCHANT);

        equipTag.getTag("staffDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + STAFF_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> staffDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_DMG_MULT);
        equipTag.getTag("swordDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + SWORD_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> swordDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SWORD_DMG_MULT);
        equipTag.getTag("axeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + AXE_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> axeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : AXE_DMG_MULT);
        equipTag.getTag("pickaxeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + PICKAXE_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> pickaxeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : PICKAXE_DMG_MULT);
        equipTag.getTag("shovelDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + SHOVEL_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> shovelDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SHOVEL_DMG_MULT);
        equipTag.getTag("hoeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + HOE_DMG_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> hoeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : HOE_DMG_MULT);

        equipTag.getTag("staffSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + STAFF_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> staffSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_SPEED_MULT);
        equipTag.getTag("swordSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + SWORD_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> swordSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SWORD_SPEED_MULT);
        equipTag.getTag("axeSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + AXE_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> axeSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : AXE_SPEED_MULT);
        equipTag.getTag("pickaxeSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + PICKAXE_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> pickaxeSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : PICKAXE_SPEED_MULT);
        equipTag.getTag("shovelSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + SHOVEL_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> shovelSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SHOVEL_SPEED_MULT);
        equipTag.getTag("hoeSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + HOE_SPEED_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> hoeSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : HOE_SPEED_MULT);

        equipTag.getTag("staffEfficiencyMultiplier")
                .setComment("This is an efficiency multiplier specifically for the staff of power.\nThe staff gets its own multiplier because its a \"special tool\"\nInternal Default Value: " + STAFF_EFF_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> staffEffMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_EFF_MULT);

        equipTag.getTag("draconiumBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + DRACONIUM_BASE_ENERGY)
                .setDefaultLong(-99)
                .setSyncCallback((tag, type) -> draconiumBaseEnergy = tag.getLong() != -99 ? tag.getLong() : DRACONIUM_BASE_ENERGY);
        equipTag.getTag("wyvernBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + WYVERN_BASE_ENERGY)
                .setDefaultLong(-99)
                .setSyncCallback((tag, type) -> wyvernBaseEnergy = tag.getLong() != -99 ? tag.getLong() : WYVERN_BASE_ENERGY);
        equipTag.getTag("draconicBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + DRACONIC_BASE_ENERGY)
                .setDefaultLong(-99)
                .setSyncCallback((tag, type) -> draconicBaseEnergy = tag.getLong() != -99 ? tag.getLong() : DRACONIC_BASE_ENERGY);
        equipTag.getTag("chaoticBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + CHAOTIC_BASE_ENERGY)
                .setDefaultLong(-99)
                .setSyncCallback((tag, type) -> chaoticBaseEnergy = tag.getLong() != -99 ? tag.getLong() : CHAOTIC_BASE_ENERGY);

        equipTag.getTag("staffEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + STAFF_ENERGY_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> staffEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : STAFF_ENERGY_MULT);
        equipTag.getTag("toolEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + TOOL_ENERGY_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> toolEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : TOOL_ENERGY_MULT);
        equipTag.getTag("capacitorEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + CAPACITOR_ENERGY_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> capacitorEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : CAPACITOR_ENERGY_MULT);
        equipTag.getTag("chestpieceEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + CHESTPIECE_ENERGY_MULT)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> chestpieceEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : CHESTPIECE_ENERGY_MULT);

        equipTag.getTag("energyHarvest")
                .setComment("This is the per block energy requirement of all mining tools.\nInternal Default Value: " + ENERGY_HARVEST)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> energyHarvest = tag.getInt() != -99 ? tag.getInt() : ENERGY_HARVEST);
        equipTag.getTag("energyAttack")
                .setComment("This is the energy requirement for weapons. This is multiplied by the weapons attack damage.\nInternal Default Value: " + ENERGY_ATTACK)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> energyAttack = tag.getInt() != -99 ? tag.getInt() : ENERGY_ATTACK);
        equipTag.getTag("energyShieldChg")
                .setComment("Shield recharge base energy per shield point.\nInternal Default Value: " + ENERGY_SHIELD_CHG)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> energyShieldChg = tag.getInt() != -99 ? tag.getInt() : ENERGY_SHIELD_CHG);

        equipTag.getTag("shieldPassiveModifier")
                .setComment("This controls the shield's passive power usage. The formula is: passiveDraw = (shieldPoints^2 * shieldPassiveModifier) OP/t\nInternal Default Value: " + SHIELD_PASSIVE_MODIFIER)
                .setDefaultDouble(-99)
                .setSyncCallback((tag, type) -> shieldPassiveModifier = tag.getDouble() != -99 ? tag.getDouble() : SHIELD_PASSIVE_MODIFIER);

//        equipTag.getTag("wyvernShieldCoolDown")
//                .setComment("This is the delay in ticks before the Wyvern shield will start to regenerate after blocking damage.\nInternal Default Value: " + WYVERN_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .setSyncCallback((tag, type) -> wyvernShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : WYVERN_SHIELD_COOL_DOWN);
//        equipTag.getTag("draconicShieldCoolDown")
//                .setComment("This is the delay in ticks before the Draconic shield will start to regenerate after blocking damage.\nInternal Default Value: " + DRACONIC_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .setSyncCallback((tag, type) -> draconicShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : DRACONIC_SHIELD_COOL_DOWN);
//        equipTag.getTag("chaoticShieldCoolDown")
//                .setComment("This is the delay in ticks before the Chaotic shield will start to regenerate after blocking damage.\nInternal Default Value: " + CHAOTIC_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .setSyncCallback((tag, type) -> chaoticShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : CHAOTIC_SHIELD_COOL_DOWN);

        equipTag.getTag("elytraFlightEnergy")
                .setComment("Elytra flight energy use per tick.\nInternal Default Value: " + ELYTRA_FLIGHT_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> elytraFlightEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_FLIGHT_ENERGY);

        equipTag.getTag("creativeFlightEnergy")
                .setComment("Creative flight energy use per tick.\nInternal Default Value: " + CREATIVE_FLIGHT_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> creativeFlightEnergy = tag.getInt() != -99 ? tag.getInt() : CREATIVE_FLIGHT_ENERGY);


        equipTag.getTag("elytraWyvernEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_WYVERN_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> elytraWyvernEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_WYVERN_ENERGY);
        equipTag.getTag("elytraDraconicEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_DRACONIC_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> elytraDraconicEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_DRACONIC_ENERGY);
        equipTag.getTag("elytraChaoticEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_CHAOTIC_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> elytraChaoticEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_CHAOTIC_ENERGY);

        equipTag.getTag("bowBaseEnergy")
                .setComment("Bow base energy per calculated damage point, per shot, Internal Default Value: " + BOW_BASE_ENERGY)
                .setDefaultInt(-99)
                .setSyncCallback((tag, type) -> bowBaseEnergy = tag.getInt() != -99 ? tag.getInt() : BOW_BASE_ENERGY);

        equipTag.setSyncToClient();
    }

    public static float getStaffDmgMult() {
        return (float) staffDamageMultiplier;
    }

    public static float getSwordDmgMult() {
        return (float) swordDamageMultiplier;
    }

    public static float getAxeDmgMult() {
        return (float) axeDamageMultiplier;
    }

    public static float getPickaxeDmgMult() {
        return (float) pickaxeDamageMultiplier;
    }

    public static float getShovelDmgMult() {
        return (float) shovelDamageMultiplier;
    }

    public static float getHoeDmgMult() {
        return (float) hoeDamageMultiplier;
    }


    public static float getStaffSpeedMult() {
        return (float) staffSpeedMultiplier;
    }

    public static float getSwordSpeedMult() {
        return (float) swordSpeedMultiplier;
    }

    public static float getAxeSpeedMult() {
        return (float) axeSpeedMultiplier;
    }

    public static float getPickaxeSpeedMult() {
        return (float) pickaxeSpeedMultiplier;
    }

    public static float getShovelSpeedMult() {
        return (float) shovelSpeedMultiplier;
    }

    public static float getHoeSpeedMult() {
        return (float) hoeSpeedMultiplier;
    }


    public static float getStaffEffMult() {
        return (float) staffEffMultiplier;
    }


    public static long getBaseEnergy(TechLevel techLevel) {
        switch (techLevel) {
            case DRACONIUM:
                return draconiumBaseEnergy;
            case WYVERN:
                return wyvernBaseEnergy;
            case DRACONIC:
                return draconicBaseEnergy;
            case CHAOTIC:
                return chaoticBaseEnergy;
        }
        return 0;
    }

    public static long getBaseToolEnergy(TechLevel techLevel) {
        return (long) (getBaseEnergy(techLevel) * toolEnergyMult);
    }

    public static long getBaseToolTransfer(TechLevel techLevel) {
        return getBaseToolEnergy(techLevel) / 64;
    }

    public static long getBaseCapEnergy(TechLevel techLevel) {
        return (long) (getBaseEnergy(techLevel) * capacitorEnergyMult);
    }

    public static long getBaseCapTransfer(TechLevel techLevel) {
        return getBaseCapEnergy(techLevel) / 64;
    }

    public static long getBaseStaffEnergy(TechLevel techLevel) {
        return (long) (getBaseEnergy(techLevel) * staffEnergyMult);
    }

    public static long getBaseStaffTransfer(TechLevel techLevel) {
        return getBaseStaffEnergy(techLevel) / 64;
    }

    public static long getBaseChestpieceEnergy(TechLevel techLevel) {
        return (long) (getBaseEnergy(techLevel) * chestpieceEnergyMult);
    }

    public static long getBaseChestpieceTransfer(TechLevel techLevel) {
        return getBaseChestpieceEnergy(techLevel) / 64;
    }

//    public static int getShieldCoolDown(TechLevel techLevel) {
//        switch (techLevel) {
//            case WYVERN:
//                return wyvernShieldCoolDown;
//            case DRACONIC:
//                return draconicShieldCoolDown;
//            case CHAOTIC:
//                return chaoticShieldCoolDown;
//        }
//        return wyvernShieldCoolDown;
//    }

    public static int getElytraEnergy(TechLevel techLevel) {
        switch (techLevel) {
            case WYVERN:
                return elytraWyvernEnergy;
            case DRACONIC:
                return elytraDraconicEnergy;
            case CHAOTIC:
                return elytraChaoticEnergy;
        }
        return 0;
    }
}
