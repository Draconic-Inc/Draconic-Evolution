package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;


/**
 * Created by brandon3055 on 27/1/23.
 */
public class LimitedModuleContext extends ModuleContext {
    private final ItemStack stack;
    private final LivingEntity entity;
    private final Level level;
    private final EquipmentSlot slot;
    private boolean inEquipModSlot = false;

    public LimitedModuleContext(/*ModuleHost moduleHost, */ItemStack stack, LivingEntity entity, Level level, EquipmentSlot slot) {
        super(/*moduleHost*/);
        this.stack = stack;
        this.entity = entity;
        this.level = level;
        this.slot = slot;
    }

    public LimitedModuleContext setInEquipModSlot(boolean inEquipModSlot) {
        this.inEquipModSlot = inEquipModSlot;
        return this;
    }

    @Override
    @Nullable
    public IOPStorage getOpStorage() {
        LazyOptional<IOPStorage> optional = stack.getCapability(DECapabilities.OP_STORAGE);
        if (optional.isPresent()) {
            return optional.orElseThrow(IllegalStateException::new);
        }
        return null;
    }

    @Override
    public Type getType() {
        return Type.LIMITED;
    }

    /**
     * @return The ItemStack this module is installed in.
     */
    @Nullable
    public ItemStack getStack() {
        return stack;
    }

    @Nullable
    public Level getLevel() {
        return level;
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
    @Nullable
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
