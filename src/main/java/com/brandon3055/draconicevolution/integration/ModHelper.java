package com.brandon3055.draconicevolution.integration;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.integration.jei.DEJEIPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
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
    public static boolean isBaublesInstalled;
    private static Item cleaver;
    private static Item avaritiaSword;
    private static Item bedrockSword;

    public static void init() {
        isTConInstalled = Loader.isModLoaded("tconstruct");
        isAvaritiaInstalled = Loader.isModLoaded("avaritia");
        isRotaryCraftInstalled = Loader.isModLoaded("rotarycraft");
        isJEIInstalled = Loader.isModLoaded("jei");
        isBaublesInstalled = Loader.isModLoaded("baubles");
    }

    public static boolean isHoldingCleaver(EntityPlayer player) {
        if (!isTConInstalled) {
            return false;
        }
        else if (cleaver == null) {
            cleaver = Item.REGISTRY.getObject(new ResourceLocation("tconstruct", "cleaver"));
        }
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
            bedrockSword = Item.REGISTRY.getObject(new ResourceLocation("rotarycraft", "rotarycraft_item_bedsword"));
        }

        return bedrockSword != null && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem().equals(bedrockSword);
    }

    public static boolean canRemoveEnchants(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation registry = stack.getItem().getRegistryName();
        if (registry == null || registry.getResourceDomain().equals("tconstruct")) {
            return false;
        }

        return true;
    }

    public static float applyModDamageAdjustments(ArmorSummery summery, LivingAttackEvent event) {
        if (summery == null) return event.getAmount();
        EntityPlayer attacker = event.getSource().getTrueSource() instanceof EntityPlayer ? (EntityPlayer) event.getSource().getTrueSource() : null;

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
            DEJEIPlugin.reloadJEI();
        }
    }

    public static boolean isWrench(ItemStack stack) {
        String name = String.valueOf(stack.getItem().getRegistryName()).toLowerCase(Locale.ENGLISH);
        return name.contains("wrench") || name.contains("binder") || name.contains("hammer");
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
