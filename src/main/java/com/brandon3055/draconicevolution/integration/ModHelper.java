package com.brandon3055.draconicevolution.integration;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.integration.jei.DEJEIPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

    private static Map<String, String> loadedMods = null;

    public static final OptionalMod<?> TINKERS_CONSTRUCT = OptionalMod.of("tconstruct");
    public static final OptionalMod<?> AVARITIA = OptionalMod.of("avaritia");
    public static final OptionalMod<?> ROTARYCRAFT = OptionalMod.of("rotarycraft");
    public static final OptionalMod<?> JEI = OptionalMod.of("jei");
    public static final OptionalMod<?> BAUBLES = OptionalMod.of("baubles");
    public static final OptionalMod<?> ENDERSTORAGE = OptionalMod.of("enderstorage");
    private static Item cleaver;
    private static Item avaritiaSword;
    private static Item bedrockSword;



    public static boolean isHoldingCleaver(Player player) {
        if (!TINKERS_CONSTRUCT.isPresent()) {
            return false;
        }
        else if (cleaver == null) {
            cleaver = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tconstruct", "cleaver"));
        }
        return cleaver != null && HandHelper.getItem(player, cleaver) != null;
    }

    public static boolean isHoldingAvaritiaSword(Player player) {
        if (!AVARITIA.isPresent()) {
            return false;
        }
        else if (avaritiaSword == null) {
            avaritiaSword = ForgeRegistries.ITEMS.getValue(new ResourceLocation("avaritia", "infinity_sword"));
        }

        return avaritiaSword != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem().equals(avaritiaSword);
    }

    public static boolean isHoldingBedrockSword(Player player) {
        if (!ROTARYCRAFT.isPresent()) {
            return false;
        }
        else if (bedrockSword == null) {
            bedrockSword =  ForgeRegistries.ITEMS.getValue(new ResourceLocation("rotarycraft", "rotarycraft_item_bedsword"));
        }

        return bedrockSword != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem().equals(bedrockSword);
    }

    public static boolean canRemoveEnchants(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation registry = stack.getItem().getRegistryName();
        if (registry == null || registry.getNamespace().equals("tconstruct")) {
            return false;
        }

        return true;
    }

    public static float applyModDamageAdjustments(LivingAttackEvent event, ModuleHost host) {
        Player attacker = event.getSource().getEntity() instanceof Player ? (Player) event.getSource().getEntity() : null;
        if (attacker == null) {
            return event.getAmount();
        }

        if (isHoldingAvaritiaSword(attacker)) {
            event.getEntityLiving().invulnerableTime = 0;
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
        else if (event.getSource().isBypassArmor() || event.getSource().isBypassInvul()) {
//            summery.entropy += 3;
//
//            if (summery.entropy > 100) {
//                summery.entropy = 100;
//            }

            return event.getAmount() * 2;
        }

        return event.getAmount();
    }

    public static void reloadJEI() {
        if (JEI.isPresent()) {
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
            loadedMods = Collections.synchronizedMap(new HashMap<>());
            for (IModInfo mod : ModList.get().getMods()) {
                loadedMods.put(mod.getModId(), mod.getDisplayName());
            }
        }
        return loadedMods;
    }

}
