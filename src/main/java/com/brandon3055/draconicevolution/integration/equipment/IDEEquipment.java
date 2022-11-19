package com.brandon3055.draconicevolution.integration.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 6/1/21
 */
public interface IDEEquipment {

    default void equipmentTick(ItemStack stack, LivingEntity livingEntity) {}

    default List<Component> getTagsTooltip(ItemStack stack, List<Component> tagTooltips) {
        return tagTooltips;
    }

    default boolean canRightClickEquip(ItemStack stack, LivingEntity livingEntity, String slotID) {
        return false;
    }

    default boolean canEquip(ItemStack stack, LivingEntity livingEntity, String slotID) {
        return true;
    }
}
