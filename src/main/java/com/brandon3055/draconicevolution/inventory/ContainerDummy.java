package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

/**
 * Created by brandon3055 on 17/10/2016.
 */
public class ContainerDummy<T extends TileBCore> extends ContainerBCBase<T> {

    public ContainerDummy(ContainerType<?> type, int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        super(type, windowId, playerInv, getClientTile(extraData));
    }
    public ContainerDummy(ContainerType<?> type, int windowId, PlayerInventory player) {
        super(type, windowId, player);
    }

    public ContainerDummy(ContainerType<?> type, int windowId, PlayerInventory player, T tile) {
        super(type, windowId, player, tile);
    }

    public ContainerDummy(ContainerType<?> type, int windowId, PlayerInventory player, T tile, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, tile, factory);
    }

//    public ContainerDummy(TileBCBase tile, PlayerEntity player, int invX, int invY) {
//        super(player, tile);
//        if (invX != -1) {
//            addPlayerSlots(invX, invY);
//        }
//    }
//
//    public ContainerDummy(TileBCore tile, PlayerEntity player, int invX, int invY) {
//        super(player, tile);
//        if (invX != -1) {
//            addPlayerSlots(invX, invY);
//        }
//    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return tile instanceof IInventory ? ((IInventory) tile).isUsableByPlayer(playerIn) : tile != null;
    }
}
