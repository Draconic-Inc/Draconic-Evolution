package com.brandon3055.draconicevolution.integration;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.integration.jei.DraconicEvolutionPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.brandon3055.draconicevolution.handlers.CustomArmorHandler.ArmorSummery;

/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

    private static Map<String, String> loadedMods = null;

    public static boolean isTConInstalled;
    public static boolean isAvaritiaInstalled;
    public static boolean isRotaryCraftInstalled;
    public static boolean isJEIInstalled;
	private static Item cleaver;
    private static Item avaritiaSword;
    private static Item bedrockSword;

	public static void init(){
        isTConInstalled = Loader.isModLoaded("TConstruct");
        isAvaritiaInstalled = Loader.isModLoaded("Avaritia");
        isRotaryCraftInstalled = Loader.isModLoaded("RotaryCraft");
        isJEIInstalled = Loader.isModLoaded("JEI");
	}

	public static boolean isHoldingCleaver(EntityPlayer player){
		if (!isTConInstalled) return false;
//		else if (cleaver == null) cleaver = GameRegistry.findItem("TConstruct", "cleaver");

		return cleaver != null && HandHelper.getItem(player, cleaver) != null;
	}

    public static boolean isHoldingAvaritiaSword(EntityPlayer player) {
        if (!isAvaritiaInstalled) {
            return false;
        }
        else if (avaritiaSword == null) {
            avaritiaSword = Item.REGISTRY.getObject(new ResourceLocation("Avaritia", "Infinity_Sword"));
        }

        return avaritiaSword != null && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem().equals(avaritiaSword);
    }

    public static boolean isHoldingBedrockSword(EntityPlayer player) {
        if (!isRotaryCraftInstalled) {
            return false;
        }
        else if (bedrockSword == null) {
            bedrockSword = Item.REGISTRY.getObject(new ResourceLocation("RotaryCraft", "rotarycraft_item_bedsword"));
        }

        return bedrockSword != null && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem().equals(bedrockSword);
    }

    public static float applyModDamageAdjustments(ArmorSummery summery, LivingAttackEvent event) {
        if (summery == null) return event.getAmount();
        EntityPlayer attacker = event.getSource().getEntity() instanceof EntityPlayer ? (EntityPlayer) event.getSource().getEntity() : null;

        if (attacker == null) {
            return event.getAmount();
        }

        if (isHoldingAvaritiaSword(attacker)) {
            event.getEntityLiving().hurtResistantTime = 0;
            return 300F;
        }
        else if (isHoldingBedrockSword(attacker)) {
            summery.entropy += 10;

            if (summery.entropy > 100) {
                summery.entropy = 100;
            }

            return Math.max(event.getAmount(), Math.min(50F, summery.protectionPoints));
        }
        else if (event.getSource().isUnblockable() || event.getSource().canHarmInCreative()) {
            summery.entropy += 3;

            if (summery.entropy > 100) {
                summery.entropy = 100;
            }

            return event.getAmount() * 2;
        }

        return event.getAmount();
    }

    public static void reloadJEI() {
        if (isJEIInstalled) {
            DraconicEvolutionPlugin.reloadJEI();
        }
    }

    /**
     * @return a map of Modid to Mod Name for all loaded mods
     */
    public static Map<String, String> getLoadedMods() {
        if (loadedMods == null) {
            loadedMods = Collections.synchronizedMap(new HashMap<String, String>());
            for (ModContainer mod : Loader.instance().getModList()) {
                loadedMods.put(mod.getModId(), mod.getName());
            }
        }
        return loadedMods;
    }

}
