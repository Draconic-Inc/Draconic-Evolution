package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;

/**
 * Created by brandon3055 on 26/2/20.
 */
public class GuiLayoutFactories {
    public static final ContainerSlotLayout.LayoutFactory<TileGenerator>    GENERATOR_LAYOUT            = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileGrinder>      GRINDER_LAYOUT              = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileEnergyCore>   ENERGY_CORE_LAYOUT          = (player, tile) -> new ContainerSlotLayout().playerMain(player);
    public static final ContainerSlotLayout.LayoutFactory<TileBCore>        MODULAR_ITEM_LAYOUT         = (player, noOp) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player);
    public static final ContainerSlotLayout.LayoutFactory<Object>           CONFIGURABLE_ITEM_LAYOUT    = (player, noOp) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player);
    public static final ContainerSlotLayout.LayoutFactory<TileBCore>        PLAYER_ONLY_LAYOUT          = (player, tile) -> new ContainerSlotLayout().playerMain(player);
}
