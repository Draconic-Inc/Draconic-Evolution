package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 16/6/20
 */
public class StackTickContext extends StackModuleContext {
    private final EquipmentSlotType slot;

    public StackTickContext(ModuleHost moduleHost, ItemStack stack, LivingEntity entity, EquipmentSlotType slot) {
        super(moduleHost, stack, entity);
        this.slot = slot;
    }

    @Nullable
    public EquipmentSlotType getSlot() {
        return slot;
    }
}
