package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ItemData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 16/6/20
 * <p>
 * This is for any modular items that extend vanilla's {@link net.minecraft.world.item.TieredItem}
 */
public interface IModularTieredItem extends IModularItem {

    DETier getItemTier();

    @Override
    default void handleTick(ModuleHost host, ItemStack stack, LivingEntity entity, @Nullable EquipmentSlot slot, boolean inEquipModSlot) {
        IModularItem.super.handleTick(host, stack, entity, slot, inEquipModSlot);

        double damage = getAttackDamage(host, stack);
        double speed = 1 + host.getModuleData(ModuleTypes.SPEED, new SpeedData(0)).speedMultiplier();
        stack.set(ItemData.ATTRIBUTE_DATA, new AttributeData(damage, speed));
    }

    @Override
    default ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        AttributeData data = stack.get(ItemData.ATTRIBUTE_DATA);

        if (data != null) {
            DETier tier = getItemTier();
            builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, data.damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (tier.getAttackSpeed() * getSwingSpeedMultiplier() * data.speed) - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
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

    record AttributeData(double damage, double speed) {}
}
