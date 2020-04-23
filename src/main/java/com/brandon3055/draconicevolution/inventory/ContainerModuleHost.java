package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ContainerModuleHost<T> extends ContainerBCore<T> {

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        super(type, windowId, playerInv, extraData);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, PacketBuffer extraData, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, extraData, factory);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player) {
        super(type, windowId, player);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, factory);
    }
}
