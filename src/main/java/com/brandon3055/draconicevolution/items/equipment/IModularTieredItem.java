package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Created by brandon3055 on 16/6/20
 * <p>
 * This is for any modular items that extend vanilla's {@link net.minecraft.world.item.TieredItem}
 */
public interface IModularTieredItem extends IModularItem {

    DETier getItemTier();

    @Override
    default ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();

        if (stack.getCapability(DECapabilities.Host.ITEM) != null) {
            DETier tier = getItemTier();

            ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
            double damage = getAttackDamage(host, stack);
            double speed = 1 + host.getModuleData(ModuleTypes.SPEED, new SpeedData(0)).speedMultiplier();

            builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (tier.getAttackSpeed() * getSwingSpeedMultiplier() * speed) - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }

        return builder.build();
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
