package com.brandon3055.draconicevolution.integration.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

//    @Override
//    public void curioAnimate(String identifier, int index, LivingEntity livingEntity) {
//
//    }

//    @Override
//    public boolean canUnequip(String identifier, LivingEntity livingEntity) {
//        return false;
//    }


    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity) {
        return item.canEquip(livingEntity, identifier);
    }

    @Override
    public List<Component> getTagsTooltip(List<Component> tagTooltips) {
        return item.getTagsTooltip(stack, tagTooltips);
    }

//    @Override
//    public void playRightClickEquipSound(LivingEntity livingEntity) {
//
//    }

    @Override
    public boolean canRightClickEquip() {
        return item.canRightClickEquip(stack);
    }

//    @Override
//    public void curioBreak(ItemStack stack, LivingEntity livingEntity) {
//
//    }

//    @Nonnull
//    @Override
//    public DropRule getDropRule(LivingEntity livingEntity) {
//        return DropRule.DEFAULT;
//    }
//
//    @Override
//    public boolean showAttributesTooltip(String identifier) {
//        return false;
//    }

//    @Override
//    public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
//        return false;
//    }
//


    @Override
    public ItemStack getStack() {
        return stack;
    }
}
