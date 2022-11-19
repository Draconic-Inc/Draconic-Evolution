package com.brandon3055.draconicevolution.integration.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;

/**
 * Created by brandon3055 on 6/1/21
 * Will pass though more functions as needed
 */
public class CurioWrapper implements ICurio {
    private IDEEquipment item;
    private ItemStack stack;

    public CurioWrapper(ItemStack stack) {
        this.item = (IDEEquipment) stack.getItem();
        this.stack = stack;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
        item.equipmentTick(stack, livingEntity);
    }

    @Override
    public boolean canEquip(SlotContext slotContext) {
        return item.canEquip(stack, slotContext.entity(), slotContext.identifier());
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips) {
        return item.getTagsTooltip(stack, tooltips);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return item.canRightClickEquip(stack, slotContext.entity(), slotContext.identifier());
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
}
