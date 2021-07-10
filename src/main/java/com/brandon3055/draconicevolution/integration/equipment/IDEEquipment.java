package com.brandon3055.draconicevolution.integration.equipment;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * Created by brandon3055 on 6/1/21
 */
public interface IDEEquipment {

    default void equipmentTick(ItemStack stack, LivingEntity livingEntity) {}

    default List<ITextComponent> getTagsTooltip(ItemStack stack, List<ITextComponent> tagTooltips) {
        return tagTooltips;
    }

    default boolean canRightClickEquip(ItemStack stack) {
        return false;
    }

    default boolean canEquip(LivingEntity livingEntity, String slotID) {
        return true;
    }
}
