package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class DisenchanterMenu extends DETileMenu<TileDisenchanter> {

    public final SlotGroup main = createSlotGroup(0);
    public final SlotGroup hotBar = createSlotGroup(0);
    public final SlotGroup armor = createSlotGroup(0);

    public final SlotGroup input = createSlotGroup(1);
    public final SlotGroup books = createSlotGroup(2);
    public final SlotGroup output = createSlotGroup(3);

    public DisenchanterMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public DisenchanterMenu(int windowId, Inventory playerInv, TileDisenchanter tile) {
        super(DEContent.MENU_DISENCHANTER.get(), windowId, playerInv, tile);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
        armor.addPlayerArmor(inventory);

        input.addSlot(new ModularSlot(tile.itemHandler, 0));
        books.addSlot(new ModularSlot(tile.itemHandler, 1));
        output.addSlot(new ModularSlot(tile.itemHandler, 2));
    }
}
