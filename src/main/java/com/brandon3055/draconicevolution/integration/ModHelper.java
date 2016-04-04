package com.brandon3055.draconicevolution.integration;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

    public static boolean isTConInstalled;
    public static boolean isAvaritiaInstalled;
    private static Item cleaver;
    private static Item avaritiaSword;

	public static void init(){
		isTConInstalled = Loader.isModLoaded("TConstruct");
        isAvaritiaInstalled = Loader.isModLoaded("Avaritia");
	}

	public static boolean isHoldingCleaver(EntityPlayer player){
		if (!isTConInstalled) return false;
		else if (cleaver == null) cleaver = GameRegistry.findItem("TConstruct", "cleaver");

		return cleaver != null && player.getHeldItem() != null && player.getHeldItem().getItem().equals(cleaver);
	}

    public static boolean isHoldingAvaritiaSword(EntityPlayer player){
        if (!isAvaritiaInstalled) return false;
        else if (avaritiaSword == null) avaritiaSword = GameRegistry.findItem("Avaritia", "Infinity_Sword");

        return avaritiaSword != null && player.getHeldItem() != null && player.getHeldItem().getItem().equals(avaritiaSword);
    }

    public static float applyModDamageAdjustments(LivingAttackEvent event){
        EntityPlayer attacker = event.source.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.source.getEntity() : null;

        if (attacker != null && isHoldingAvaritiaSword(attacker)){
            event.entityLiving.hurtResistantTime = 0;
            return 300F;
        }


        return event.ammount;
    }

}
