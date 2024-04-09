package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.init.DEContent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by FoxMcloud5655 on 29/03/2024
 */
public class CelestialManipulatorMenu extends DETileMenu<TileCelestialManipulator> {
    public CelestialManipulatorMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, (TileCelestialManipulator)playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CelestialManipulatorMenu(int windowId, Inventory playerInv, TileCelestialManipulator tile) {
        super(DEContent.MENU_CELESTIAL_MANIPULATOR.get(), windowId, playerInv, tile);
    }
}
