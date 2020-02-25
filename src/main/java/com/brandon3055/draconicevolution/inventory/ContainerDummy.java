package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 17/10/2016.
 */
public class ContainerDummy extends ContainerBCBase {

    public ContainerDummy(@Nullable ContainerType type, int windowId, PlayerEntity player) {
        super(type, windowId, player);
    }

    public ContainerDummy(@Nullable ContainerType type, int windowId, PlayerEntity player, TileBCore tile) {
        super(type, windowId, player, tile);
    }

    public ContainerDummy(@Nullable ContainerType type, int windowId, PlayerEntity player, TileBCore tile, ContainerSlotLayout.LayoutFactory factory) {
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
