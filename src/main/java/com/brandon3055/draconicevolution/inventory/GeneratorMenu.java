package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class GeneratorMenu extends DETileMenu<TileGenerator> {

    public final SlotGroup main = createSlotGroup(0, 1, 2);
    public final SlotGroup hotBar = createSlotGroup(0, 1, 2);
    public final SlotGroup fuel = createSlotGroup(1, 0);
    public final SlotGroup capacitor = createSlotGroup(2, 0);

    public GeneratorMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public GeneratorMenu(int windowId, Inventory playerInv, TileGenerator tile) {
        super(DEContent.MENU_GENERATOR.get(), windowId, playerInv, tile);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
        fuel.addSlots(3, 0, index -> new ModularSlot(tile.itemHandler, index)
                .setValidator(stack -> stack.getBurnTime(RecipeType.SMELTING) > 0)
        );
        capacitor.addSlot(new ModularSlot(tile.itemHandler, 3)
                .setValidator(EnergyUtils::canReceiveEnergy)
        );
    }
}
