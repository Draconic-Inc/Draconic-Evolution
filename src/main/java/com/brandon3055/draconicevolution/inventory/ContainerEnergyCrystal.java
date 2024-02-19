package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 21/12/2016.
 */
public class ContainerEnergyCrystal extends ContainerDETile<TileCrystalBase> {

    public ContainerEnergyCrystal(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(DEContent.MENU_ENERGY_CRYSTAL.get(), windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public ContainerEnergyCrystal(@Nullable MenuType<?> type, int windowId, Inventory player, TileCrystalBase tile) {
        super(type, windowId, player, tile);
    }

//    public ContainerEnergyCrystal(PlayerEntity player, TileCrystalBase tile) {
//        super(player, tile);
//    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        tile.detectAndSendContainerChanges(containerListeners);
    }
}
