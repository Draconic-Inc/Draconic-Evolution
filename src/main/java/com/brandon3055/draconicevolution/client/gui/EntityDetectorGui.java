package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiEntityFilter;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.EntityDetectorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EntityDetectorGui extends ContainerGuiProvider<EntityDetectorMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.entity_detector");
    public static final int GUI_WIDTH = 308;
    public static final int GUI_HEIGHT = 180;
    private boolean largeView = false;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("entity_detector"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }


    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<EntityDetectorMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        EntityDetectorMenu menu = screenAccess.getMenu();
        TileEntityDetector tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);

        GuiButton largeViewButton = TOOLKIT.createResizeButton(root)
                .onPress(() -> largeView = !largeView);
        Constraints.placeInside(largeViewButton, root, Constraints.LayoutPos.BOTTOM_RIGHT, -3, -30);

        GuiButton closeLarge = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("close_large_view"))
                .setEnabled(() -> largeView)
                .onPress(() -> largeView = !largeView)
                .jeiExclude();
        Constraints.size(closeLarge, 140, 14);
        Constraints.placeOutside(closeLarge, root, Constraints.LayoutPos.BOTTOM_CENTER, 0, 1);

        var energyBar = TOOLKIT.createEnergyBar(root, tile.opStorage);

        //Must be added after everything else to ensure it renders on top in "large view" mode.
        GuiRectangle filterBG = new GuiRectangle(root)
                .setOpaque(true)
                .shadedRect(GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight, GuiToolkit.Palette.Slot::fill)
                .constrain(TOP, relative(root.get(TOP), () -> largeView ? 4 : 14D))
                .constrain(LEFT, relative(root.get(LEFT), () -> largeView ? 4 : 23D))
                .constrain(RIGHT, relative(root.get(RIGHT), () -> largeView ? -4 : -15D))
                .constrain(BOTTOM, relative(root.get(BOTTOM), () -> largeView ? -4 : -30D));

        GuiEntityFilter filterUI = new GuiEntityFilter(filterBG, tile.entityFilter)
                .setNodeBgBuilder(e -> new GuiRectangle(e).shadedRect(() -> GuiToolkit.Palette.Ctrl.accentLight(false), () -> GuiToolkit.Palette.Ctrl.accentDark(false), () -> GuiToolkit.Palette.Ctrl.fill(false)))
                .setTitleTextColour(GuiToolkit.Palette.Slot::text)
                .setScrollBarCustomizer(guiSlider -> Constraints.bind(new GuiRectangle(guiSlider).fill(GuiToolkit.Palette.SubItem::accentDark), guiSlider.getSlider()))
                .initFilter();
        Constraints.bind(filterUI, filterBG, 2);

        energyBar.container()
                .constrain(TOP, match(filterBG.get(TOP)))
                .constrain(LEFT, relative(root.get(LEFT), 6))
                .constrain(HEIGHT, match(filterBG.get(HEIGHT)))
                .constrain(WIDTH, literal(14));

        int ctrlHeight = 23;

        //Controls
        GuiElement<?> range = new GuiElement<>(root)
                .constrain(WIDTH, dynamic(() -> (root.xSize() - 8) / 5D))
                .constrain(HEIGHT, literal(ctrlHeight))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -4));
        Constraints.placeInside(new GuiText(range, TOOLKIT.translate("range"))
                        .setTooltip(TOOLKIT.translate("range.info"))
                        .setTooltipDelay(0)
                        .constrain(WIDTH, match(range.get(WIDTH)))
                        .constrain(HEIGHT, literal(8))
                        .setScroll(false),
                range, Constraints.LayoutPos.TOP_CENTER);
        Constraints.placeInside(new GuiText(range, () -> Component.literal(String.valueOf(tile.range.get())))
                        .setTooltip(TOOLKIT.translate("range.info"))
                        .constrain(WIDTH, match(range.get(WIDTH)))
                        .constrain(HEIGHT, literal(12))
                        .setScroll(false),
                range, Constraints.LayoutPos.BOTTOM_CENTER);

        GuiButton rangeMinus = TOOLKIT.createBorderlessButton(range, Component.literal("-")).onPress(() -> tile.adjustRange(true, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(rangeMinus, 12, 12);
        Constraints.placeInside(rangeMinus, range, Constraints.LayoutPos.BOTTOM_LEFT, 1, 0);
        GuiButton rangePlus = TOOLKIT.createBorderlessButton(range, Component.literal("+")).onPress(() -> tile.adjustRange(false, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(rangePlus, 12, 12);
        Constraints.placeInside(rangePlus, range, Constraints.LayoutPos.BOTTOM_RIGHT, -1, 0);


        GuiElement<?> minStr = new GuiElement<>(root)
                .constrain(WIDTH, dynamic(() -> (root.xSize() - 8) / 5D))
                .constrain(HEIGHT, literal(ctrlHeight))
                .constrain(LEFT, match(range.get(RIGHT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -4));
        Constraints.placeInside(new GuiText(minStr, TOOLKIT.translate("rsmin"))
                        .setTooltip(TOOLKIT.translate("rsmin.info"))
                        .setTooltipDelay(0)
                        .constrain(WIDTH, match(minStr.get(WIDTH)))
                        .constrain(HEIGHT, literal(8))
                        .setScroll(false),
                minStr, Constraints.LayoutPos.TOP_CENTER);
        Constraints.placeInside(new GuiText(minStr, () -> Component.literal(String.valueOf(tile.rsMinDetection.get())))
                        .setTooltip(TOOLKIT.translate("rsmin.info"))
                        .constrain(WIDTH, match(minStr.get(WIDTH)))
                        .constrain(HEIGHT, literal(12))
                        .setScroll(false),
                minStr, Constraints.LayoutPos.BOTTOM_CENTER);

        GuiButton minMinus = TOOLKIT.createBorderlessButton(minStr, Component.literal("-")).onPress(() -> tile.adjustRSMin(true, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(minMinus, 12, 12);
        Constraints.placeInside(minMinus, minStr, Constraints.LayoutPos.BOTTOM_LEFT, 1, 0);
        GuiButton minPlus = TOOLKIT.createBorderlessButton(minStr, Component.literal("+")).onPress(() -> tile.adjustRSMin(false, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(minPlus, 12, 12);
        Constraints.placeInside(minPlus, minStr, Constraints.LayoutPos.BOTTOM_RIGHT, -1, 0);

        GuiElement<?> maxStr = new GuiElement<>(root)
                .constrain(WIDTH, dynamic(() -> (root.xSize() - 8) / 5D))
                .constrain(HEIGHT, literal(ctrlHeight))
                .constrain(LEFT, match(minStr.get(RIGHT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -4));
        Constraints.placeInside(new GuiText(maxStr, TOOLKIT.translate("rsmax"))
                        .setTooltip(TOOLKIT.translate("rsmax.info"))
                        .setTooltipDelay(0)
                        .constrain(WIDTH, match(maxStr.get(WIDTH)))
                        .constrain(HEIGHT, literal(8))
                        .setScroll(false),
                maxStr, Constraints.LayoutPos.TOP_CENTER);
        Constraints.placeInside(new GuiText(maxStr, () -> Component.literal(String.valueOf(tile.rsMaxDetection.get())))
                        .setTooltip(TOOLKIT.translate("rsmax.info"))
                        .constrain(WIDTH, match(maxStr.get(WIDTH)))
                        .constrain(HEIGHT, literal(12))
                        .setScroll(false),
                maxStr, Constraints.LayoutPos.BOTTOM_CENTER);

        GuiButton maxMinus = TOOLKIT.createBorderlessButton(maxStr, Component.literal("-")).onPress(() -> tile.adjustRSMax(true, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(maxMinus, 12, 12);
        Constraints.placeInside(maxMinus, maxStr, Constraints.LayoutPos.BOTTOM_LEFT, 1, 0);
        GuiButton maxPlus = TOOLKIT.createBorderlessButton(maxStr, Component.literal("+")).onPress(() -> tile.adjustRSMax(false, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(maxPlus, 12, 12);
        Constraints.placeInside(maxPlus, maxStr, Constraints.LayoutPos.BOTTOM_RIGHT, -1, 0);

        GuiElement<?> rate = new GuiElement<>(root)
                .constrain(WIDTH, dynamic(() -> (root.xSize() - 8) / 5D))
                .constrain(HEIGHT, literal(ctrlHeight))
                .constrain(LEFT, match(maxStr.get(RIGHT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -4));
        Constraints.placeInside(new GuiText(rate, TOOLKIT.translate("pulse_rate"))
                        .setTooltip(TOOLKIT.translate("pulse_rate.info"))
                        .setTooltipDelay(0)
                        .constrain(WIDTH, match(rate.get(WIDTH)))
                        .constrain(HEIGHT, literal(8))
                        .setScroll(false),
                rate, Constraints.LayoutPos.TOP_CENTER);
        Constraints.placeInside(new GuiText(rate, () -> Component.literal(String.valueOf(tile.pulseRate.get())))
                        .setTooltip(TOOLKIT.translate("pulse_rate.info"))
                        .constrain(WIDTH, match(rate.get(WIDTH)))
                        .constrain(HEIGHT, literal(12))
                        .setScroll(false),
                rate, Constraints.LayoutPos.BOTTOM_CENTER);

        GuiButton rateMinus = TOOLKIT.createBorderlessButton(rate, Component.literal("-")).onPress(() -> tile.adjustPulseRate(true, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(rateMinus, 12, 12);
        Constraints.placeInside(rateMinus, rate, Constraints.LayoutPos.BOTTOM_LEFT, 1, 0);
        GuiButton ratePlus = TOOLKIT.createBorderlessButton(rate, Component.literal("+")).onPress(() -> tile.adjustPulseRate(false, Screen.hasShiftDown())).setOpaque(true);
        Constraints.size(ratePlus, 12, 12);
        Constraints.placeInside(ratePlus, rate, Constraints.LayoutPos.BOTTOM_RIGHT, -1, 0);

        GuiElement<?> mode = new GuiElement<>(root)
                .constrain(WIDTH, dynamic(() -> (root.xSize() - 8) / 5D))
                .constrain(HEIGHT, literal(ctrlHeight))
                .constrain(LEFT, match(rate.get(RIGHT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -4));
        Constraints.placeInside(new GuiText(mode, TOOLKIT.translate("pulse_mode"))
                        .setTooltip(TOOLKIT.translate("pulse_mode.info"))
                        .setTooltipDelay(0)
                        .constrain(WIDTH, match(mode.get(WIDTH)))
                        .constrain(HEIGHT, literal(8))
                        .setScroll(false),
                mode, Constraints.LayoutPos.TOP_CENTER);
        Constraints.placeInside(TOOLKIT.createBorderlessButton(mode, () -> TOOLKIT.translate("pulse_mode." + (tile.pulseRsMode.get() ? "on" : "off")))
                        .setTooltip(TOOLKIT.translate("pulse_mode.info"))
                        .onPress(tile::togglePulsemode)
                        .constrain(WIDTH, match(mode.get(WIDTH)))
                        .constrain(HEIGHT, literal(12)),
                mode, Constraints.LayoutPos.BOTTOM_CENTER);

    }

//		GuiLabel rangeLabel = template.background.addChild(new GuiLabel(guiLeft() + 4, filterUI.maxYPos() + 4, (xSize() - 4) / 5, 8, toolkit.i18n("range"))).setShadow(false);
//		rangeLabel.addChild(new GuiButton(rangeLabel.xPos(), rangeLabel.maxYPos(), 16, 16, "-")).onPressed(() -> tile.adjustRange(true, hasShiftDown()));
//		rangeLabel.addChild(new GuiLabel(rangeLabel.xPos() + rangeLabel.xSize() / 2 - 8, rangeLabel.maxYPos(), 16, 16)).setTrim(false).setDisplaySupplier(() -> Short.toString(tile.range.get()));
//		rangeLabel.addChild(new GuiButton(rangeLabel.maxXPos() - 16, rangeLabel.maxYPos(), 16, 16, "+")).onPressed(() -> tile.adjustRange(false, hasShiftDown()));
//
//		GuiLabel rsMinLabel = template.background.addChild(new GuiLabel(rangeLabel.maxXPos(), filterUI.maxYPos() + 4, (xSize() - 4) / 5, 8, toolkit.i18n("rsmin"))).setShadow(false);
//		rsMinLabel.addChild(new GuiButton(rsMinLabel.xPos(), rsMinLabel.maxYPos(), 16, 16, "-")).onPressed(() -> tile.adjustRSMin(true, hasShiftDown()));
//		rsMinLabel.addChild(new GuiLabel(rsMinLabel.xPos() + rsMinLabel.xSize() / 2 - 8, rsMinLabel.maxYPos(), 16, 16)).setTrim(false).setDisplaySupplier(() -> Integer.toString(tile.rsMinDetection.get()));
//		rsMinLabel.addChild(new GuiButton(rsMinLabel.maxXPos() - 16, rsMinLabel.maxYPos(), 16, 16, "+")).onPressed(() -> tile.adjustRSMin(false, hasShiftDown()));
//
//		GuiLabel rsMaxLabel = template.background.addChild(new GuiLabel(rsMinLabel.maxXPos(), filterUI.maxYPos() + 4, (xSize() - 4) / 5, 8, toolkit.i18n("rsmax"))).setShadow(false);
//		rsMaxLabel.addChild(new GuiButton(rsMaxLabel.xPos(), rsMaxLabel.maxYPos(), 16, 16, "-")).onPressed(() -> tile.adjustRSMax(true, hasShiftDown()));
//		rsMaxLabel.addChild(new GuiLabel(rsMaxLabel.xPos() + rsMaxLabel.xSize() / 2 - 8, rsMaxLabel.maxYPos(), 16, 16)).setTrim(false).setDisplaySupplier(() -> Integer.toString(tile.rsMaxDetection.get()));
//		rsMaxLabel.addChild(new GuiButton(rsMaxLabel.maxXPos() - 16, rsMaxLabel.maxYPos(), 16, 16, "+")).onPressed(() -> tile.adjustRSMax(false, hasShiftDown()));
//
//		GuiLabel pulseRateLabel = template.background.addChild(new GuiLabel(rsMaxLabel.maxXPos(), filterUI.maxYPos() + 4, (xSize() - 4) / 5, 8, toolkit.i18n("pulse_rate"))).setShadow(false);
//		pulseRateLabel.addChild(new GuiButton(pulseRateLabel.xPos(), pulseRateLabel.maxYPos(), 16, 16, "-")).onPressed(() -> tile.adjustPulseRate(true, hasShiftDown())).setDisabledStateSupplier(() -> !tile.pulseRsMode.get());
//		pulseRateLabel.addChild(new GuiLabel(pulseRateLabel.xPos() + pulseRateLabel.xSize() / 2 - 8, pulseRateLabel.maxYPos(), 16, 16)).setTrim(false).setDisplaySupplier(() -> Short.toString(tile.pulseRate.get()));
//		pulseRateLabel.addChild(new GuiButton(pulseRateLabel.maxXPos() - 16, pulseRateLabel.maxYPos(), 16, 16, "+")).onPressed(() -> tile.adjustPulseRate(false, hasShiftDown())).setDisabledStateSupplier(() -> !tile.pulseRsMode.get());
//
//		GuiLabel pulseModeLabel = template.background.addChild(new GuiLabel(pulseRateLabel.maxXPos(), filterUI.maxYPos() + 4, (xSize() - 4) / 5, 8, toolkit.i18n("pulse_mode"))).setShadow(false);
//		pulseModeLabel.addChild(new GuiButton(pulseModeLabel.xPos(), pulseModeLabel.maxYPos(), pulseModeLabel.xSize(), 16))
//			.onPressed(() -> tile.togglePulsemode())
//			.setDisplaySupplier(() -> toolkit.i18n("pulse_mode." + (tile.pulseRsMode.get() ? "on" : "off")));
//	}

    public static class Screen extends ModularGuiContainer<EntityDetectorMenu> {
        public Screen(EntityDetectorMenu menu, Inventory inv, Component title) {
            super(menu, inv, new EntityDetectorGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
