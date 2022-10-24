package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;

/**
 * Created by brandon3055 on 26/2/20.
 */
public class GuiLayoutFactories {
    public static final ContainerSlotLayout.LayoutFactory<TileGenerator>            GENERATOR_LAYOUT             = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileGrinder>              GRINDER_LAYOUT               = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileEnergyCore>           ENERGY_CORE_LAYOUT           = (player, tile) -> new ContainerSlotLayout().playerMain(player);
    public static final ContainerSlotLayout.LayoutFactory<TileBCore>                MODULAR_ITEM_LAYOUT          = (player, noOp) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player).playerEquipMod(player);
    public static final ContainerSlotLayout.LayoutFactory<Object>                   CONFIGURABLE_ITEM_LAYOUT     = (player, noOp) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player).playerEquipMod(player);
    public static final ContainerSlotLayout.LayoutFactory<TileEnergyTransfuser>     TRANSFUSER_LAYOUT            = (player, tile) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player).allTile(tile.itemsCombined);
    public static final ContainerSlotLayout.LayoutFactory<TileFusionCraftingCore>   FUSION_CRAFTING_CORE         = (player, tile) -> new ContainerSlotLayout().playerMain(player).playerArmor(player).playerOffHand(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileDraconiumChest>       DRACONIUM_CHEST              = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.mainInventory).allTile(tile.craftingItems).allTile(tile.furnaceItems);
    public static final ContainerSlotLayout.LayoutFactory<TileDisenchanter>         DISENCHANTER_LAYOUT          = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
    public static final ContainerSlotLayout.LayoutFactory<TileCelestialManipulator> CELESTIAL_MANIPULATOR_LAYOUT = (player, noOp) -> new ContainerSlotLayout().playerMain(player);
    
    public static final ContainerSlotLayout.LayoutFactory<TileBCore>        PLAYER_ONLY_LAYOUT          = (player, tile) -> new ContainerSlotLayout().playerMain(player);
}
