package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class StackModuleContext extends ModuleContext {
    private final ItemStack stack;
    private final LivingEntity entity;
    private final EquipmentSlot slot;
    private boolean inEquipModSlot = false;

    public StackModuleContext(/*ModuleHost moduleHost, */ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
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
    @Nullable
    public IOPStorage getOpStorage() {
        return stack.getCapability(CapabilityOP.ITEM);
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
    public EquipmentSlot getSlot() {
        return slot;
    }

    public boolean isEquipped() {
        return getItem().isEquipped(getStack(), getSlot(), inEquipModSlot);
    }
}
