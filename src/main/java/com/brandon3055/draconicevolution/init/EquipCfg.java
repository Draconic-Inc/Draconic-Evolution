package com.brandon3055.draconicevolution.init;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigTag;
import com.brandon3055.brandonscore.api.TechLevel;

//EquipmentConfig. Shortened to make the tool constructor super calls a little less bloated
public class EquipCfg {

    //@formatter:off
    //Default Values
    private static final double DRACONIUM_HARVEST_SPEED   = 10D;
    private static final double WYVERN_HARVEST_SPEED      = 15D;
    private static final double DRACONIC_HARVEST_SPEED    = 25D;
    private static final double CHAOTIC_HARVEST_SPEED     = 50D;

    private static final double DRACONIUM_DAMAGE          = 1.125D;
    private static final double WYVERN_DAMAGE             = 1.250D;
    private static final double DRACONIC_DAMAGE           = 1.750D;
    private static final double CHAOTIC_DAMAGE            = 2.500D;

    private static final double DRACONIUM_SWING_SPEED     = 1.125D;
    private static final double WYVERN_SWING_SPEED        = 1.250D;
    private static final double DRACONIC_SWING_SPEED      = 1.500D;
    private static final double CHAOTIC_SWING_SPEED       = 2.000D;

    private static final int DRACONIUM_ENCHANT            = 12;
    private static final int WYVERN_ENCHANT               = 15;
    private static final int DRACONIC_ENCHANT             = 25;
    private static final int CHAOTIC_ENCHANT              = 35;

    //These ratios are based on diamond tools
    private static final double STAFF_DMG_MULT            = 9.0D;
    private static final double SWORD_DMG_MULT            = 7.0D;
    private static final double AXE_DMG_MULT              = 9.0D;
    private static final double PICKAXE_DMG_MULT          = 5.0D;
    private static final double SHOVEL_DMG_MULT           = 5.5D;
    private static final double HOE_DMG_MULT              = 1.0D;

    //These ratios are based on diamond tools
    private static final double STAFF_SWING_SPEED_MULT          = 0.5D;
    private static final double SWORD_SWING_SPEED_MULT          = 1.6D;
    private static final double AXE_SWING_SPEED_MULT            = 1.0D;
    private static final double PICKAXE_SWING_SPEED_MULT        = 1.2D;
    private static final double SHOVEL_SWING_SPEED_MULT         = 1.0D;
    private static final double HOE_SWING_SPEED_MULT            = 4.0D;

    private static final double STAFF_EFF_MULT            = 3.0D;

    private static final long DRACONIUM_BASE_ENERGY       = 0;//500000;
    private static final long WYVERN_BASE_ENERGY          = 0;//2000000;
    private static final long DRACONIC_BASE_ENERGY        = 0;//4000000;
    private static final long CHAOTIC_BASE_ENERGY         = 0;//8000000;
    
    private static final double STAFF_ENERGY_MULT         = 3D;
    private static final double TOOL_ENERGY_MULT          = 1D;
    private static final double CAPACITOR_ENERGY_MULT     = 8D;
    private static final double CHESTPIECE_ENERGY_MULT    = 2D;

    private static final int ENERGY_HARVEST               = 256;
    private static final int ENERGY_ATTACK                = 1024;
    private static final int ENERGY_SHIELD_CHG            = 8192;

    private static final double SHIELD_PASSIVE_MODIFIER   = 0.0005;

    private static final int ELYTRA_FLIGHT_ENERGY         = 1024;
    private static final int CREATIVE_FLIGHT_ENERGY       = 4096;

    private static final int ELYTRA_WYVERN_ENERGY         = 1024;
    private static final int ELYTRA_DRACONIC_ENERGY       = 2048;
    private static final int ELYTRA_CHAOTIC_ENERGY        = 8192;

    private static final int BOW_BASE_ENERGY              = 1024; //Energy per calculated damage point

    private static final int ENDER_MODULE_PER_ITEM_ENERGY = 32;

    private static final int NIGHT_VISION_ENERGY          = 20;

    //Static Access values
    public static double draconiumHarvestSpeed;
    public static double wyvernHarvestSpeed;
    public static double draconicHarvestSpeed;
    public static double chaoticHarvestSpeed;

    public static double draconiumDamage;
    public static double wyvernDamage;
    public static double draconicDamage;
    public static double chaoticDamage;

    public static double draconiumSwingSpeed;
    public static double wyvernSwingSpeed;
    public static double draconicSwingSpeed;
    public static double chaoticSwingSpeed;

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

    public static double staffSwingSpeedMultiplier;
    public static double swordSwingSpeedMultiplier;
    public static double axeSwingSpeedMultiplier;
    public static double pickaxeSwingSpeedMultiplier;
    public static double shovelSwingSpeedMultiplier;
    public static double hoeSwingSpeedMultiplier;

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

    public static int enderModulePerItemEnergy;

    public static int nightVisionEnergy;

    //@formatter:on

    public static void loadConfig(ConfigCategory configTag) {
        ConfigCategory equipTag = configTag.getCategory("Equipment");
        equipTag.setComment("These settings allow you to override the base stats for DE's equipment.",
                "Please note the generated default values \"-99\" is actually a marker that tells DE to use the actual internal default value.",
                "This value is listed in each properties description but may not be valid if this config was generated by a previous version of DE.");

        equipTag.getValue("draconiumHarvestSpeed")
                .setComment("Internal Default Value: " + DRACONIUM_HARVEST_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconiumHarvestSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_HARVEST_SPEED);
        equipTag.getValue("wyvernHarvestSpeed")
                .setComment("Internal Default Value: " + WYVERN_HARVEST_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> wyvernHarvestSpeed = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_HARVEST_SPEED);
        equipTag.getValue("draconicHarvestSpeed")
                .setComment("Internal Default Value: " + DRACONIC_HARVEST_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconicHarvestSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_HARVEST_SPEED);
        equipTag.getValue("chaoticHarvestSpeed")
                .setComment("Internal Default Value: " + CHAOTIC_HARVEST_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> chaoticHarvestSpeed = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_HARVEST_SPEED);

        equipTag.getValue("draconiumDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + DRACONIUM_DAMAGE)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconiumDamage = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_DAMAGE);
        equipTag.getValue("wyvernDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + WYVERN_DAMAGE)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> wyvernDamage = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_DAMAGE);
        equipTag.getValue("draconicDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + DRACONIC_DAMAGE)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconicDamage = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_DAMAGE);
        equipTag.getValue("chaoticDamage")
                .setComment("Base Attack Damage\nInternal Default Value: " + CHAOTIC_DAMAGE)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> chaoticDamage = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_DAMAGE);

        equipTag.getValue("draconiumSwingSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + DRACONIUM_SWING_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconiumSwingSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIUM_SWING_SPEED);
        equipTag.getValue("wyvernSwingSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + WYVERN_SWING_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> wyvernSwingSpeed = tag.getDouble() != -99 ? tag.getDouble() : WYVERN_SWING_SPEED);
        equipTag.getValue("draconicSwingSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + DRACONIC_SWING_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> draconicSwingSpeed = tag.getDouble() != -99 ? tag.getDouble() : DRACONIC_SWING_SPEED);
        equipTag.getValue("chaoticSwingSpeed")
                .setComment("Base Attack Speed (How many times you can attack at full power per second)\nInternal Default Value: " + CHAOTIC_SWING_SPEED)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> chaoticSwingSpeed = tag.getDouble() != -99 ? tag.getDouble() : CHAOTIC_SWING_SPEED);

        equipTag.getValue("draconiumEnchantability")
                .setComment("Internal Default Value: " + DRACONIUM_ENCHANT)
                .setDefaultInt(-99)
                .onSync((tag, type) -> draconiumEnchantability = tag.getInt() != -99 ? tag.getInt() : DRACONIUM_ENCHANT);
        equipTag.getValue("wyvernEnchantability")
                .setComment("Internal Default Value: " + WYVERN_ENCHANT)
                .setDefaultInt(-99)
                .onSync((tag, type) -> wyvernEnchantability = tag.getInt() != -99 ? tag.getInt() : WYVERN_ENCHANT);
        equipTag.getValue("draconicEnchantability")
                .setComment("Internal Default Value: " + DRACONIC_ENCHANT)
                .setDefaultInt(-99)
                .onSync((tag, type) -> draconicEnchantability = tag.getInt() != -99 ? tag.getInt() : DRACONIC_ENCHANT);
        equipTag.getValue("chaoticEnchantability")
                .setComment("Internal Default Value: " + CHAOTIC_ENCHANT)
                .setDefaultInt(-99)
                .onSync((tag, type) -> chaoticEnchantability = tag.getInt() != -99 ? tag.getInt() : CHAOTIC_ENCHANT);

        equipTag.getValue("staffDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + STAFF_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> staffDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_DMG_MULT);
        equipTag.getValue("swordDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + SWORD_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> swordDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SWORD_DMG_MULT);
        equipTag.getValue("axeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + AXE_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> axeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : AXE_DMG_MULT);
        equipTag.getValue("pickaxeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + PICKAXE_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> pickaxeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : PICKAXE_DMG_MULT);
        equipTag.getValue("shovelDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + SHOVEL_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> shovelDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SHOVEL_DMG_MULT);
        equipTag.getValue("hoeDamageMultiplier")
                .setComment("This is a multiplier that is applied to the base attack damage\nInternal Default Value: " + HOE_DMG_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> hoeDamageMultiplier = tag.getDouble() != -99 ? tag.getDouble() : HOE_DMG_MULT);

        equipTag.getValue("staffSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + STAFF_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> staffSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_SWING_SPEED_MULT);
        equipTag.getValue("swordSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + SWORD_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> swordSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SWORD_SWING_SPEED_MULT);
        equipTag.getValue("axeSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + AXE_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> axeSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : AXE_SWING_SPEED_MULT);
        equipTag.getValue("pickaxeSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + PICKAXE_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> pickaxeSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : PICKAXE_SWING_SPEED_MULT);
        equipTag.getValue("shovelSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + SHOVEL_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> shovelSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : SHOVEL_SWING_SPEED_MULT);
        equipTag.getValue("hoeSwingSpeedMultiplier")
                .setComment("This is a multiplier that is applied to the base attack speed\nInternal Default Value: " + HOE_SWING_SPEED_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> hoeSwingSpeedMultiplier = tag.getDouble() != -99 ? tag.getDouble() : HOE_SWING_SPEED_MULT);

        equipTag.getValue("staffEfficiencyMultiplier")
                .setComment("This is an efficiency multiplier specifically for the staff of power.\nThe staff gets its own multiplier because its a \"special tool\"\nInternal Default Value: " + STAFF_EFF_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> staffEffMultiplier = tag.getDouble() != -99 ? tag.getDouble() : STAFF_EFF_MULT);

        equipTag.getValue("draconiumBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + DRACONIUM_BASE_ENERGY)
                .setDefaultLong(-99)
                .onSync((tag, type) -> draconiumBaseEnergy = tag.getLong() != -99 ? tag.getLong() : DRACONIUM_BASE_ENERGY);
        equipTag.getValue("wyvernBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + WYVERN_BASE_ENERGY)
                .setDefaultLong(-99)
                .onSync((tag, type) -> wyvernBaseEnergy = tag.getLong() != -99 ? tag.getLong() : WYVERN_BASE_ENERGY);
        equipTag.getValue("draconicBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + DRACONIC_BASE_ENERGY)
                .setDefaultLong(-99)
                .onSync((tag, type) -> draconicBaseEnergy = tag.getLong() != -99 ? tag.getLong() : DRACONIC_BASE_ENERGY);
        equipTag.getValue("chaoticBaseEnergy")
                .setComment("This is the base energy value for each tier before the type multiplier is applied.\nInternal Default Value: " + CHAOTIC_BASE_ENERGY)
                .setDefaultLong(-99)
                .onSync((tag, type) -> chaoticBaseEnergy = tag.getLong() != -99 ? tag.getLong() : CHAOTIC_BASE_ENERGY);

        equipTag.getValue("staffEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + STAFF_ENERGY_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> staffEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : STAFF_ENERGY_MULT);
        equipTag.getValue("toolEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + TOOL_ENERGY_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> toolEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : TOOL_ENERGY_MULT);
        equipTag.getValue("capacitorEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + CAPACITOR_ENERGY_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> capacitorEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : CAPACITOR_ENERGY_MULT);
        equipTag.getValue("chestpieceEnergyMult")
                .setComment("This is a multiplier that is applied to the base energy value.\nInternal Default Value: " + CHESTPIECE_ENERGY_MULT)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> chestpieceEnergyMult = tag.getDouble() != -99 ? tag.getDouble() : CHESTPIECE_ENERGY_MULT);

        equipTag.getValue("energyHarvest")
                .setComment("This is the per block energy requirement of all mining tools.\nInternal Default Value: " + ENERGY_HARVEST)
                .setDefaultInt(-99)
                .onSync((tag, type) -> energyHarvest = tag.getInt() != -99 ? tag.getInt() : ENERGY_HARVEST);
        equipTag.getValue("energyAttack")
                .setComment("This is the energy requirement for weapons. This is multiplied by the weapons attack damage.\nInternal Default Value: " + ENERGY_ATTACK)
                .setDefaultInt(-99)
                .onSync((tag, type) -> energyAttack = tag.getInt() != -99 ? tag.getInt() : ENERGY_ATTACK);
        equipTag.getValue("energyShieldChg")
                .setComment("Shield recharge base energy per shield point.\nInternal Default Value: " + ENERGY_SHIELD_CHG)
                .setDefaultInt(-99)
                .onSync((tag, type) -> energyShieldChg = tag.getInt() != -99 ? tag.getInt() : ENERGY_SHIELD_CHG);

        equipTag.getValue("shieldPassiveModifier")
                .setComment("This controls the shield's passive power usage. The formula is: passiveDraw = (shieldPoints^2 * shieldPassiveModifier) OP/t\nInternal Default Value: " + SHIELD_PASSIVE_MODIFIER)
                .setDefaultDouble(-99)
                .onSync((tag, type) -> shieldPassiveModifier = tag.getDouble() != -99 ? tag.getDouble() : SHIELD_PASSIVE_MODIFIER);

//        equipTag.getValue("wyvernShieldCoolDown")
//                .setComment("This is the delay in ticks before the Wyvern shield will start to regenerate after blocking damage.\nInternal Default Value: " + WYVERN_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .onSync((tag, type) -> wyvernShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : WYVERN_SHIELD_COOL_DOWN);
//        equipTag.getValue("draconicShieldCoolDown")
//                .setComment("This is the delay in ticks before the Draconic shield will start to regenerate after blocking damage.\nInternal Default Value: " + DRACONIC_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .onSync((tag, type) -> draconicShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : DRACONIC_SHIELD_COOL_DOWN);
//        equipTag.getValue("chaoticShieldCoolDown")
//                .setComment("This is the delay in ticks before the Chaotic shield will start to regenerate after blocking damage.\nInternal Default Value: " + CHAOTIC_SHIELD_COOL_DOWN)
//                .setDefaultInt(-99)
//                .onSync((tag, type) -> chaoticShieldCoolDown = tag.getInt() != -99 ? tag.getInt() : CHAOTIC_SHIELD_COOL_DOWN);

        equipTag.getValue("elytraFlightEnergy")
                .setComment("Elytra flight energy use per tick.\nInternal Default Value: " + ELYTRA_FLIGHT_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> elytraFlightEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_FLIGHT_ENERGY);

        equipTag.getValue("creativeFlightEnergy")
                .setComment("Creative flight energy use per tick.\nInternal Default Value: " + CREATIVE_FLIGHT_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> creativeFlightEnergy = tag.getInt() != -99 ? tag.getInt() : CREATIVE_FLIGHT_ENERGY);


        equipTag.getValue("elytraWyvernEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_WYVERN_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> elytraWyvernEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_WYVERN_ENERGY);
        equipTag.getValue("elytraDraconicEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_DRACONIC_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> elytraDraconicEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_DRACONIC_ENERGY);
        equipTag.getValue("elytraChaoticEnergy")
                .setComment("Elytra boost energy per tick, Internal Default Value: " + ELYTRA_CHAOTIC_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> elytraChaoticEnergy = tag.getInt() != -99 ? tag.getInt() : ELYTRA_CHAOTIC_ENERGY);

        equipTag.getValue("bowBaseEnergy")
                .setComment("Bow base energy per calculated damage point, per shot, Internal Default Value: " + BOW_BASE_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> bowBaseEnergy = tag.getInt() != -99 ? tag.getInt() : BOW_BASE_ENERGY);

        equipTag.getValue("enderModulePerItemEnergy")
                .setComment("Energy required for the ender storage module to transfer one (single) item into storage, Internal Default Value: " + ENDER_MODULE_PER_ITEM_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> enderModulePerItemEnergy = tag.getInt() != -99 ? tag.getInt() : ENDER_MODULE_PER_ITEM_ENERGY);

        equipTag.getValue("nightVisionEnergy")
                .setComment("Night vision module energy consumption while operation. (OP per tick), Internal Default Value: " + NIGHT_VISION_ENERGY)
                .setDefaultInt(-99)
                .onSync((tag, type) -> nightVisionEnergy = tag.getInt() != -99 ? tag.getInt() : NIGHT_VISION_ENERGY);

        equipTag.syncTagToClient();
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
