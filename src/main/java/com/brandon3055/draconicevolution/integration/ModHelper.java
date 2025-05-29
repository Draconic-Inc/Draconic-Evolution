package com.brandon3055.draconicevolution.integration;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

    private static Map<String, String> loadedMods = null;

    public static final boolean TINKERS_CONSTRUCT = ModList.get().isLoaded("tconstruct");
    public static final boolean AVARITIA = ModList.get().isLoaded("avaritia");
    public static final boolean ROTARYCRAFT = ModList.get().isLoaded("rotarycraft");
    public static final boolean JEI = ModList.get().isLoaded("jei");
    public static final boolean BAUBLES = ModList.get().isLoaded("baubles");
    public static final boolean ENDERSTORAGE = ModList.get().isLoaded("enderstorage");
    private static Item cleaver;
    private static Item avaritiaSword;
    private static Item bedrockSword;



    public static boolean isHoldingCleaver(Player player) {
        if (!TINKERS_CONSTRUCT) {
            return false;
        }
        else if (cleaver == null) {
            cleaver = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tconstruct", "cleaver"));
        }
        return cleaver != null && HandHelper.getItem(player, cleaver) != null;
    }

    public static boolean isHoldingAvaritiaSword(Player player) {
        if (!AVARITIA) {
            return false;
        }
        else if (avaritiaSword == null) {
            avaritiaSword = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("avaritia", "infinity_sword"));
        }

        return avaritiaSword != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem().equals(avaritiaSword);
    }

    public static boolean isHoldingBedrockSword(Player player) {
        if (!ROTARYCRAFT) {
            return false;
        }
        else if (bedrockSword == null) {
            bedrockSword =  BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("rotarycraft", "rotarycraft_item_bedsword"));
        }

        return bedrockSword != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem().equals(bedrockSword);
    }

    public static boolean canRemoveEnchants(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation registry = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (registry == null || registry.getNamespace().equals("tconstruct")) {
            return false;
        }

        return true;
    }

    public static float applyModDamageAdjustments(LivingIncomingDamageEvent event, ModuleHost host) {
        Player attacker = event.getSource().getEntity() instanceof Player ? (Player) event.getSource().getEntity() : null;
        if (attacker == null) {
            return event.getAmount();
        }

        if (isHoldingAvaritiaSword(attacker)) {
            event.getEntity().invulnerableTime = 0;
            return 300F;
        }
//        else if (isHoldingBedrockSword(attacker)) {
//            summery.entropy += 10;
//
//            if (summery.entropy > 100) {
//                summery.entropy = 100;
//            }
//
//            return Math.max(event.getAmount(), Math.min(50F, summery.protectionPoints));
//        }
        else if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
//            summery.entropy += 3;
//
//            if (summery.entropy > 100) {
//                summery.entropy = 100;
//            }

            return event.getAmount() * 2;
        }

        return event.getAmount();
    }

//    public static void reloadJEI() {
//        if (JEI.isPresent()) {
//            DEJEIPlugin.reloadJEI();
//        }
//    }

    public static boolean isWrench(ItemStack stack) {
        String name = String.valueOf(BuiltInRegistries.ITEM.getKey(stack.getItem())).toLowerCase(Locale.ENGLISH);
        return name.contains("wrench") || name.contains("binder") || name.contains("hammer");
    }

    /**
     * @return a map of Modid to Mod Name for all loaded mods
     */
    public static Map<String, String> getLoadedMods() {
        if (loadedMods == null) {
            loadedMods = Collections.synchronizedMap(new HashMap<>());
            for (IModInfo mod : ModList.get().getMods()) {
                loadedMods.put(mod.getModId(), mod.getDisplayName());
            }
        }
        return loadedMods;
    }

}
