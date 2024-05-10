package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.ColourState;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiListDialog;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.SmeltingLogic;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.SmeltingLogic.FeedMode;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.DraconiumChestMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static codechicken.lib.gui.modular.lib.Constraints.LayoutPos.*;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by Werechang on 27/6/21
 */
public class DraconiumChestGui extends ContainerGuiProvider<DraconiumChestMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.draconium_chest");
    public static final int GUI_WIDTH = 478;
    public static final int GUI_HEIGHT = 268;

    public GuiColourPicker colourDialog;
    public GuiTexture craftIcon;
    public GuiProgressIcon furnaceProgress;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("draconium_chest"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<DraconiumChestMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        DraconiumChestMenu menu = screenAccess.getMenu();
        TileDraconiumChest tile = menu.tile;
        GuiElement<?> root = gui.getRoot();

        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -5);

        GuiSlots chestInv = new GuiSlots(root, screenAccess, menu.chestInv, 26)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"));
        Constraints.placeInside(chestInv, root, Constraints.LayoutPos.TOP_CENTER, 0, 5);

        //Crafting grid
        GuiSlots craftIn = new GuiSlots(root, screenAccess, menu.craftIn, 3)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"));
        Constraints.placeOutside(craftIn, playInv.container(), Constraints.LayoutPos.MIDDLE_RIGHT, 12, 0);

        craftIcon = new GuiTexture(root, BCGuiTextures.themedGetter("prog_arrow_right"));
        Constraints.size(craftIcon, 22, 15);
        Constraints.placeOutside(craftIcon, craftIn, Constraints.LayoutPos.MIDDLE_RIGHT, 7, 0);

        GuiTexture craftOutTex = new GuiTexture(root, BCGuiTextures.themedGetter("slot_large"));
        Constraints.size(craftOutTex, 26, 26);
        Constraints.placeOutside(craftOutTex, craftIcon, Constraints.LayoutPos.MIDDLE_RIGHT, 7, 0);

        GuiSlots craftOut = new GuiSlots(root, screenAccess, menu.craftOut, 1)
                .setSlotTexture(slot -> null);
        Constraints.center(craftOut, craftOutTex);

        GuiText craftLabel = TOOLKIT.createHeading(root, Component.translatable("container.crafting"), false)
                .setScroll(false);
        Constraints.size(craftLabel, 50, 8);
        Constraints.placeOutside(craftLabel, craftOutTex, Constraints.LayoutPos.TOP_CENTER, 0, -6);

        //Furnace

        GuiRectangle furnaceContainer = new GuiRectangle(root)
                .shadedRect(GuiToolkit.Palette.Slot::accentLight, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::fill)
                .constrain(LEFT, relative(root.get(LEFT), 10))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -10))
                .constrain(RIGHT, relative(playInv.container().get(LEFT), -7))
                .constrain(TOP, relative(chestInv.get(BOTTOM), 7));

        GuiSlots furnaceIn = new GuiSlots(root, screenAccess, menu.furnaceInputs, 5)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"));
        Constraints.placeInside(furnaceIn, furnaceContainer, Constraints.LayoutPos.TOP_CENTER, 0, 4);

        GuiProgressIcon furnaceFlame = new GuiProgressIcon(furnaceContainer, DEGuiTextures.get("chest/fire_base"), DEGuiTextures.get("chest/fire_over"))
                .setDirection(Direction.UP)
                .setRotateToDirection(false)
                .setProgress(() -> (double) tile.smeltingLogic.smeltingPower.get());
        Constraints.size(furnaceFlame, 88, 15);
        Constraints.placeOutside(furnaceFlame, furnaceIn, Constraints.LayoutPos.BOTTOM_CENTER, 0, 5);

        furnaceProgress = new GuiProgressIcon(furnaceContainer, BCGuiTextures.themedGetter("prog_arrow_up_tall"), BCGuiTextures.themedGetter("prog_arrow_up_tall_over"))
                .setDirection(Direction.UP)
                .setRotateToDirection(false)
                .setProgress(() -> (double) tile.smeltingLogic.smeltProgress.get());
        Constraints.size(furnaceProgress, 16, 32);
        Constraints.placeInside(furnaceProgress, furnaceContainer, Constraints.LayoutPos.TOP_RIGHT, -5, 5);

        //Energy Bar
        var energyBar = TOOLKIT.createEnergyBar(furnaceContainer, tile.opStorage);
        energyBar.container()
                .constrain(TOP, relative(furnaceFlame.get(BOTTOM), 5))
                .constrain(LEFT, match(furnaceIn.get(LEFT)))
                .constrain(RIGHT, match(furnaceIn.get(RIGHT)))
                .constrain(HEIGHT, literal(14));

        GuiTexture chargeArrow = TOOLKIT.energySlotArrow(furnaceContainer, false, false)
                .setRotation(2);
        Constraints.placeOutside(chargeArrow, energyBar.container(), Constraints.LayoutPos.MIDDLE_LEFT, -3, -2);

        // Feed Mode Button
        GuiButton feedButton = TOOLKIT.createBorderlessButton(furnaceContainer)
                .setTooltipDelay(5)
                .setTooltipSingle(() -> TOOLKIT.translate("feed." + tile.smeltingLogic.feedMode.get().localKey() + ".info"));
        Constraints.size(feedButton, 18, 18);
        Constraints.placeOutside(feedButton, furnaceIn, Constraints.LayoutPos.MIDDLE_LEFT, -3, 0);
        Constraints.bind(new GuiTexture(feedButton, () -> DEGuiTextures.get(tile.smeltingLogic.feedMode.get().getSprite())), feedButton, 1);

        feedButton.onPress(() -> {
            GuiListDialog<FeedMode> dialog = GuiListDialog.<FeedMode>createNoSearch(furnaceContainer)
                    .setCloseOnItemClicked(true)
                    .addItems(FeedMode.values());

            dialog.getList().setZStacking(false);
            dialog.getList().setItemSpacing(1);

            dialog.getList().setDisplayBuilder((feedModeGuiList, feedMode) -> {
                GuiButton button = TOOLKIT.createBorderlessButton(feedModeGuiList)
                        .onPress(() -> tile.smeltingLogic.feedMode.set(feedMode))
                        .setTooltipDelay(5)
                        .constrain(HEIGHT, literal(18))
                        .setTooltip(TOOLKIT.translate("feed." + feedMode.localKey() + ".info"));
                Constraints.bind(new GuiTexture(button, DEGuiTextures.get(feedMode.getSprite())), button, 1);
                return button;
            });

            Constraints.size(dialog, 18 + 6, 72 + 9);
            Constraints.placeInside(dialog, feedButton, Constraints.LayoutPos.TOP_CENTER, 0, -10);
        });

        // Cap Slot
        GuiSlots capInv = GuiSlots.singleSlot(furnaceContainer, screenAccess, menu.capacitor, 0)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/energy"));
        Constraints.placeOutside(capInv, feedButton, Constraints.LayoutPos.BOTTOM_CENTER, 0, 3);

        // Button Row
        ButtonRow buttonRow = new ButtonRow(root, Direction.DOWN)
                .setSpacing(1);
        Constraints.placeOutside(buttonRow, chestInv, Constraints.LayoutPos.BOTTOM_RIGHT, -10, 1);
        buttonRow.addButton(e -> TOOLKIT.createThemeButton(e));
        buttonRow.addButton(e -> TOOLKIT.createIconButton(e, 12, BCGuiTextures.getter("color_picker"))
                .setTooltip(TOOLKIT.translate("color_picker"))
                .onPress(() -> {
                    colourDialog = GuiColourPicker.create(root, ColourState.create(tile.colour::get, tile.colour::set), false);
                    colourDialog.enableCursors(true);
                    colourDialog.getContentElement().jeiExclude();
                    colourDialog.setBlockOutsideClicks(true);
                    colourDialog.setCancelOnOutsideClick(true);
                    colourDialog.getCancelButton().setEnabled(false);
                    colourDialog.addMoveHandle((int) colourDialog.ySize());
                    Constraints.placeInside(colourDialog, root, BOTTOM_RIGHT, -16, -10);
                })
        );
        buttonRow.addButton(e -> TOOLKIT.createRSSwitch(e, screenAccess.getMenu().tile));
    }

    public static class Screen extends ModularGuiContainer<DraconiumChestMenu> {
        public Screen(DraconiumChestMenu menu, Inventory inv, Component title) {
            super(menu, inv, new DraconiumChestGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
