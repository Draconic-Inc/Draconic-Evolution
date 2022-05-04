package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class StackModuleContext extends ModuleContext {
    private final ItemStack stack;
    private final LivingEntity entity;
    private final EquipmentSlotType slot;
    private boolean inEquipModSlot = false;

    public StackModuleContext(/*ModuleHost moduleHost, */ItemStack stack, LivingEntity entity, EquipmentSlotType slot) {
        super(/*moduleHost*/);
        this.stack = stack;
        this.entity = entity;
        this.slot = slot;
    }

    public StackModuleContext setInEquipModSlot(boolean inEquipModSlot) {
        this.inEquipModSlot = inEquipModSlot;
        return this;
    }

    @Override
    public IOPStorageModifiable getOpStorage() {
        LazyOptional<IOPStorage> optional = stack.getCapability(DECapabilities.OP_STORAGE);
        if (optional.isPresent()) {
            return (IOPStorageModifiable) optional.orElseThrow(IllegalStateException::new);
        }
        return null;
    }

    @Override
    public Type getType() {
        return Type.ITEM_STACK;
    }

    /**
     * @return The ItemStack this module is installed in.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * @return The item cast to {@link IModularItem}
     * If this ever throws a ClassCastException i will smack someone!
     */
    public IModularItem getItem() {
        return (IModularItem) stack.getItem();
    }

    /**
     * @return The entity who possesses the ItemStack containing this module.
     */
    public LivingEntity getEntity() {
        return entity;
    }

    @Nullable
    public EquipmentSlotType getSlot() {
        return slot;
    }

    public boolean isEquipped() {
        return getItem().isEquipped(getStack(), getSlot(), inEquipModSlot);
    }
}
