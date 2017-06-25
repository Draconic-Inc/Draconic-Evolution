package com.brandon3055.draconicevolution.magic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Brandon on 17/11/2014.
 */
public class EnchantmentReaper extends Enchantment {

    public static EnchantmentReaper instance;

    public EnchantmentReaper() {
        super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("draconicevolution.reaperEnchant");
        this.setRegistryName(new ResourceLocation("draconicevolution", "enchant_reaper"));
    }

    public static void init() {
        instance = new EnchantmentReaper();
        GameRegistry.register(instance);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinEnchantability(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxEnchantability(int level) {
        return super.getMinEnchantability(level) + 50;
    }
}
