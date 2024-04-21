package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class TransfuserMenu extends DETileMenu<TileEnergyTransfuser> {

    public final SlotGroup main = createSlotGroup(0, 1);
    public final SlotGroup hotBar = createSlotGroup(0, 1);
    public final SlotGroup armor = createSlotGroup(0, 1);
    public final SlotGroup offhand = createSlotGroup(0, 1);

    public final SlotGroup northSlot = createSlotGroup(1, 0);
    public final SlotGroup eastSlot = createSlotGroup(1, 0);
    public final SlotGroup southSlot = createSlotGroup(1, 0);
    public final SlotGroup westSlot = createSlotGroup(1, 0);
    public final SlotGroup[] slotGroups = new SlotGroup[]{northSlot, eastSlot, southSlot, westSlot};

    public TransfuserMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public TransfuserMenu(int windowId, Inventory playerInv, TileEnergyTransfuser tile) {
        super(DEContent.MENU_ENERGY_TRANSFUSER.get(), windowId, playerInv, tile);
        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
        armor.addPlayerArmor(inventory);
        offhand.addPlayerOffhand(inventory);

        northSlot.addSlot(new ModularSlot(tile.itemNorth, 0));
        eastSlot.addSlot(new ModularSlot(tile.itemEast, 0));
        southSlot.addSlot(new ModularSlot(tile.itemSouth, 0));
        westSlot.addSlot(new ModularSlot(tile.itemWest, 0));
    }
}
