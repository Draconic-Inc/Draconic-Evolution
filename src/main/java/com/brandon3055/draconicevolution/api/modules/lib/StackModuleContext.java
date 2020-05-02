package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class StackModuleContext extends ModuleContext {
    private final ItemStack stack;
    private final PlayerEntity player;

    public StackModuleContext(ModuleHost moduleHost, ItemStack stack, PlayerEntity player) {
        super(moduleHost);
        this.stack = stack;
        this.player = player;
    }

    @Override
    public Type getType() {
        return Type.TILE_ENTITY;
    }

    /**
     * @return The ItemStack this module is installed in.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * @return The player who's possesses the ItemStack containing this module.
     */
    public PlayerEntity getPlayer() {
        return player;
    }
}
