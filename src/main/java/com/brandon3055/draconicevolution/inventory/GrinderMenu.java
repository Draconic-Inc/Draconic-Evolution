package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class GrinderMenu extends ContainerBCTile<TileGrinder> {

    public final SlotGroup main = createSlotGroup(0, 1, 2);
    public final SlotGroup hotBar = createSlotGroup(0, 1, 2);
    public final SlotGroup weapon = createSlotGroup(1, 0);
    public final SlotGroup capacitor = createSlotGroup(2, 0);

    public GrinderMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public GrinderMenu(int windowId, Inventory playerInv, TileGrinder tile) {
        super(DEContent.MENU_GRINDER.get(), windowId, playerInv, tile);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
        capacitor.addSlot(new ModularSlot(tile.itemHandler, 0));
        weapon.addSlot(new ModularSlot(tile.itemHandler, 1));
    }
}
