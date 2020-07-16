package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;

/**
 * Created by brandon3055 on 16/6/20
 * <p>
 * This is for any modular items that extend vanilla's {@link net.minecraft.item.TieredItem}
 */
public interface IModularTieredItem extends IModularItem {

    DEItemTier getItemTier();

    @Override
    default Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = IModularItem.super.getAttributeModifiers(slot, stack);
        if (MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) {
            DEItemTier tier = getItemTier();
            if (slot == EquipmentSlotType.MAINHAND) {
                ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                double damage = getAttackDamage(host, stack);
                double speed = 1 + host.getModuleData(ModuleTypes.SPEED, new SpeedData(0)).getSpeedMultiplier();

                map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", damage, AttributeModifier.Operation.ADDITION));
                map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", (tier.getAttackSpeed() * speed) - 4, AttributeModifier.Operation.ADDITION));
            }
        }

        return map;
    }

    default double getAttackDamage(ModuleHost host, ItemStack stack) {
        double damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0)).getDamagePoints();
        if (getEnergyStored(stack) < EquipCfg.energyAttack * damage) {
            damage = 0;
        }
        return damage + (getItemTier().getAttackDamage() - 1);
    }
}
