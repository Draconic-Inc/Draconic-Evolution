package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class FlowGateMenu extends DETileMenu<TileFlowGate> {

    public final SlotGroup main = createSlotGroup(0);
    public final SlotGroup hotBar = createSlotGroup(0);

    public FlowGateMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public FlowGateMenu(int windowId, Inventory playerInv, TileFlowGate tile) {
        super(DEContent.MENU_FLOW_GATE.get(), windowId, playerInv, tile);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
    }
}
