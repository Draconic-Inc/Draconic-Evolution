package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class FusionCraftingCoreMenu extends DETileMenu<TileFusionCraftingCore> {

    public final SlotGroup main = createSlotGroup(0, 1);
    public final SlotGroup hotBar = createSlotGroup(0, 1);
    public final SlotGroup armor = createSlotGroup(0, 1);
    public final SlotGroup offhand = createSlotGroup(0, 1);

    public final SlotGroup catalyst = createSlotGroup(1, 0);
    public final SlotGroup output = createSlotGroup(2, 0);

    public FusionCraftingCoreMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public FusionCraftingCoreMenu(int windowId, Inventory playerInv, TileFusionCraftingCore tile) {
        super(DEContent.MENU_FUSION_CRAFTING_CORE.get(), windowId, playerInv, tile);
        main.addPlayerMain(playerInv);
        hotBar.addPlayerBar(playerInv);
        armor.addPlayerArmor(playerInv);
        offhand.addPlayerOffhand(playerInv);

        catalyst.addSlot(new ModularSlot(tile.itemHandler, 0));
        output.addSlot(new ModularSlot(tile.itemHandler, 1));
    }
}
