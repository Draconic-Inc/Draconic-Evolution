package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 16/6/20
 */
public class StackTickContext extends StackModuleContext {
    private final int itemSlot;
    private final boolean isActiveItem;

    public StackTickContext(ModuleHost moduleHost, ItemStack stack, Entity player, int itemSlot, boolean isActiveItem) {
        super(moduleHost, stack, player);
        this.itemSlot = itemSlot;
        this.isActiveItem = isActiveItem;
    }

    public boolean isActiveItem() {
        return isActiveItem;
    }

    public int getItemSlot() {
        return itemSlot;
    }
}
