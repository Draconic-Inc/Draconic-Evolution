package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class StackModuleContext extends ModuleContext {
    private final ItemStack stack;
    private final LivingEntity entity;

    public StackModuleContext(ModuleHost moduleHost, ItemStack stack, LivingEntity entity) {
        super(moduleHost);
        this.stack = stack;
        this.entity = entity;
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
     * @return The entity who possesses the ItemStack containing this module.
     */
    public LivingEntity getEntity() {
        return entity;
    }
}
