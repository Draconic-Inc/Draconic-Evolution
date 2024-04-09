package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.elements.GuiManipulable;
import codechicken.lib.gui.modular.elements.GuiTexture;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.EntityDetectorMenu;
import com.brandon3055.draconicevolution.inventory.TransfuserMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EntityDetectorGui extends ContainerGuiProvider<EntityDetectorMenu> {
	private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.entity_detector");
	public static final int GUI_WIDTH = 300;
	public static final int GUI_HEIGHT = 180;

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
//		TileDisenchanter tile = menu.tile;
		GuiElement<?> root = gui.getRoot();
		TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

		ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
		buttonRow.addButton(TOOLKIT::createThemeButton);
	}

//	private TileEntityDetector tile;
//
//	protected GuiToolkit<GuiEntityDetector> toolkit = new GuiToolkit<>(this, 300, 180).setTranslationPrefix("gui." + DraconicEvolution.MODID + ".entity_detector");
//
//	public GuiEntityDetector(ContainerBCTile<TileEntityDetector> container, Inventory playerInventory, Component title) {
//		super(container, playerInventory, title);
//		this.tile = container.tile;
//	}
//
//	@Override
//	public void addElements(GuiElementManager manager) {
//		TBasicMachine template = new TModularMachine(this, tile, false);
//		template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiTextures.getThemed("background_dynamic"));
//		template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
//		toolkit.loadTemplate(template);
//		GuiElement bg = template.background;
//
//		GuiEntityFilter filterUI = new GuiEntityFilter(tile.entityFilter);
//		filterUI.setNodeBackgroundBuilder(() -> new GuiBorderedRect().set3DGetters(() -> Palette.Ctrl.fill(false), () -> Palette.Ctrl.accentLight(false), () -> Palette.Ctrl.accentDark(false)));
//		filterUI.setScrollBarCustomizer(bar -> bar.setSliderElement(new GuiBorderedRect().setGetters(Palette.SubItem::accentDark, () -> 0)).getBackgroundElement().setEnabled(false));
//		filterUI.setNodeTitleColour(Palette.Slot::text);
//		filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, bg.maxYPos() - 30, true);
//		bg.addChild(filterUI);
//
//		GuiBorderedRect filterBG = new GuiBorderedRect();
//		filterBG.set3DGetters(Palette.Slot::fill, Palette.Slot::accentDark, Palette.Slot::accentLight);
//		filterBG.setBorderColourL(Palette.Slot::border3D);
//		filterBG.setPosAndSize(filterUI);
//		filterUI.addBackGroundChild(filterBG);
//
//		//Large/Popout view
//		PopoutDialog popOutDialog = new PopoutDialog(bg);
//		popOutDialog.onReload(e -> e.setPosAndSize(bg));
//		popOutDialog.addChild(filterUI);
//		popOutDialog.addChild(new GuiLabel(I18n.get("gui_tkt.brandonscore.click_out_close")).onReload(e -> e.setYPos(bg.maxYPos()).setXPos(bg.xPos()).setSize(200, 12)).setAlignment(GuiAlign.LEFT));
//
//		GuiButton largeView = toolkit.createResizeButton(bg);
//		largeView.setPos(filterBG.maxXPos() + 1, filterBG.maxYPos() - 12);
//
//		//Remove the filterUI from the main background, Update its pos and size then display the dialog.
//		largeView.onPressed(() -> {
//			bg.removeChild(filterUI);
//			popOutDialog.setPosAndSize(bg);
//			filterUI.setRelPos(bg, 3, 3).setSize(bg.xSize() - 6, bg.ySize() - 6);
//			filterBG.setPosAndSize(filterUI);
//			popOutDialog.show(100);
//		});
//
//		//Return the filterUI to the main background and reset its pos and size
//		popOutDialog.setCloseCallback(() -> {
//			filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, bg.maxYPos() - 30, true);
//			filterBG.setPosAndSize(filterUI);
//			bg.addChild(filterUI);
//		});
//
//		//Power
//        template.energyBar = toolkit.createEnergyBar(template.background, tile.opStorage);
//    	template.energyBar.setPos(template.background.xPos() + 6, template.background.yPos() + 6);
//        template.energyBar.setXSize(14).setMaxYPos(filterUI.yPos() - 4, true);
//		template.energyBar.setYPos(filterUI.yPos()).setMaxYPos(filterUI.maxYPos(), true).setXPos(bg.xPos() + 7);
//
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
