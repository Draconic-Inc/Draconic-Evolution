package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

/**
 * Created by brandon3055 on 17/10/2016.
 */
@Deprecated //This does not need to exist. Can just use ContainerBCTile
public class ContainerDummy<T extends TileBCore> extends ContainerBCTile<T> {

    public ContainerDummy(MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        super(type, windowId, playerInv, extraData);
    }
//    public ContainerDummy(ContainerType<?> type, int windowId, PlayerInventory player) {
//        super(type, windowId, player);
//    }

    public ContainerDummy(MenuType<?> type, int windowId, Inventory player, T tile) {
        super(type, windowId, player, tile);
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
    public boolean stillValid(Player playerIn) {
        return tile instanceof Container ? ((Container) tile).stillValid(playerIn) : tile != null;
    }
}
