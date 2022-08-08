package com.brandon3055.draconicevolution.integration;

import com.brandon3055.draconicevolution.common.items.armor.CustomArmorHandler.ArmorSummery;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

    private static boolean isTConInstalled;
    private static boolean isAvaritiaInstalled;
    // private static boolean isRotaryCraftInstalled;
    private static boolean isGregTechInstalled;

    private static Item cleaver;
    private static Item avaritiaSword;
    // private static Item bedrockSword;

    private static Class bwores;
    private static Class GTores;

    static {
        isTConInstalled = Loader.isModLoaded("TConstruct");
        isAvaritiaInstalled = Loader.isModLoaded("Avaritia");
        // isRotaryCraftInstalled = Loader.isModLoaded("RotaryCraft");
        isGregTechInstalled = Loader.isModLoaded("gregtech");

        if (isGregTechInstalled)
            try {
                GTores = Class.forName("gregtech.common.blocks.GT_TileEntity_Ores");
            } catch (ClassNotFoundException e) {
            }
        if (Loader.isModLoaded("bartworks"))
            try {
                bwores = Class.forName("com.github.bartimaeusnek.bartworks.system.material.BW_MetaGeneratedOreTE");
            } catch (ClassNotFoundException e) {
            }
    }

    public static boolean isHoldingCleaver(EntityPlayer player) {
        if (!isTConInstalled) return false;
        else if (cleaver == null) cleaver = GameRegistry.findItem("TConstruct", "cleaver");

        return cleaver != null
                && player.getHeldItem() != null
                && player.getHeldItem().getItem().equals(cleaver);
    }

    public static boolean isHoldingAvaritiaSword(EntityPlayer player) {
        if (!isAvaritiaInstalled) return false;
        else if (avaritiaSword == null) avaritiaSword = GameRegistry.findItem("Avaritia", "Infinity_Sword");

        return avaritiaSword != null
                && player.getHeldItem() != null
                && player.getHeldItem().getItem().equals(avaritiaSword);
    }

    //    public static boolean isHoldingBedrockSword(EntityPlayer player) {
    //        if (!isRotaryCraftInstalled) return false;
    //        else if (bedrockSword == null) bedrockSword = GameRegistry.findItem("RotaryCraft",
    // "rotarycraft_item_bedsword");
    //
    //        return bedrockSword != null && player.getHeldItem() != null &&
    // player.getHeldItem().getItem().equals(bedrockSword);
    //    }

    public static float applyModDamageAdjustments(ArmorSummery summery, LivingAttackEvent event) {
        if (summery == null) return event.ammount;
        EntityPlayer attacker =
                event.source.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.source.getEntity() : null;

        if (attacker == null) {
            return event.ammount;
        }

        if (isHoldingAvaritiaSword(attacker)) {
            event.entityLiving.hurtResistantTime = 0;
            return 300F;
        }
        //        else if (isHoldingBedrockSword(attacker)) {
        //            summery.entropy += 10;
        //
        //            if (summery.entropy > 100) {
        //                summery.entropy = 100;
        //            }
        //
        //            return Math.max(event.ammount, Math.min(50F, summery.protectionPoints));
        //        }
        else if (event.source.isUnblockable() || event.source.canHarmInCreative()) {
            summery.entropy += 3;

            if (summery.entropy > 100) {
                summery.entropy = 100;
            }

            return event.ammount * 2;
        }

        return event.ammount;
    }

    public static boolean isGregTechTileEntityOre(TileEntity te) {
        return isGregTechInstalled && GTores.isInstance(te)
                || isGregTechInstalled && Loader.isModLoaded("bartworks") && bwores.isInstance(te);
    }
}
