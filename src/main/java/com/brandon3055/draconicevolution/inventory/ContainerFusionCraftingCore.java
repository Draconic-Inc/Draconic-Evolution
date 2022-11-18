package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerFusionCraftingCore extends ContainerDETile<TileFusionCraftingCore> {

    public ContainerFusionCraftingCore(int windowId, Inventory player, FriendlyByteBuf extraData, ContainerSlotLayout.LayoutFactory<TileFusionCraftingCore> factory) {
        super(DEContent.container_fusion_crafting_core, windowId, player, extraData, factory);
    }

    public ContainerFusionCraftingCore(int windowId, Inventory player, TileFusionCraftingCore tile, ContainerSlotLayout.LayoutFactory<TileFusionCraftingCore> factory) {
        super(DEContent.container_fusion_crafting_core, windowId, player, tile, factory);
    }
}
