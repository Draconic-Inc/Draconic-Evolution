package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 16/6/20
 * <p>
 * This is for any modular items that extend vanilla's {@link net.minecraft.world.item.TieredItem}
 */
public interface IModularTieredItem extends IModularItem {

    DETier getItemTier();

    @Override
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = IModularItem.super.getAttributeModifiers(slot, stack);
        if (stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            DETier tier = getItemTier();
            if (slot == EquipmentSlot.MAINHAND) {
                ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                double damage = getAttackDamage(host, stack);
                double speed = 1 + host.getModuleData(ModuleTypes.SPEED, new SpeedData(0)).speedMultiplier();

                map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage, AttributeModifier.Operation.ADDITION));
                map.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Tool modifier", (tier.getAttackSpeed() * getSwingSpeedMultiplier() * speed) - 4, AttributeModifier.Operation.ADDITION));
            }
        }

        return map;
    }

    default double getAttackDamage(ModuleHost host, ItemStack stack) {
        double damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0)).damagePoints();
        if (getEnergyStored(stack) < EquipCfg.energyAttack * damage) {
            damage = 0;
        }
        return damage + ((getItemTier().getAttackDamageBonus() * getDamageMultiplier()) - 1);
    }

    double getSwingSpeedMultiplier();

    double getDamageMultiplier();
}
