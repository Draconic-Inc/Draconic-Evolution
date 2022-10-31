package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiPopUpDialogBase.PopoutDialog;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEntityFilter;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class GuiEntityDetector extends ModularGuiContainer<ContainerBCTile<TileEntityDetector>> {

	private TileEntityDetector tile;

	protected GuiToolkit<GuiEntityDetector> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.EXTRA_WIDE);

	public GuiEntityDetector(ContainerBCTile<TileEntityDetector> container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		this.tile = container.tile;
	}

	@Override
	public void addElements(GuiElementManager manager) {
		TBasicMachine template = new TBasicMachine(this, tile);
		template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
		template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
		toolkit.loadTemplate(template);
		GuiElement bg = template.background;

		if (tile.isAdvanced()) {
			GuiEntityFilter filterUI = new GuiEntityFilter(tile.entityFilter);
			filterUI.setNodeBackgroundBuilder(() -> new GuiBorderedRect().set3DGetters(() -> Palette.Ctrl.fill(false), () -> Palette.Ctrl.accentLight(false), () -> Palette.Ctrl.accentDark(false)));
			filterUI.setScrollBarCustomizer(bar -> bar.setSliderElement(new GuiBorderedRect().setGetters(Palette.SubItem::accentDark, () -> 0)).getBackgroundElement().setEnabled(false));
			filterUI.setNodeTitleColour(Palette.Slot::text);
			filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, bg.maxYPos() - 30, true);
			bg.addChild(filterUI);

			GuiBorderedRect filterBG = new GuiBorderedRect();
			filterBG.set3DGetters(Palette.Slot::fill, Palette.Slot::accentDark, Palette.Slot::accentLight);
			filterBG.setBorderColourL(Palette.Slot::border3D);
			filterBG.setPosAndSize(filterUI);
			filterUI.addBackGroundChild(filterBG);

			//Power
			template.addEnergyBar(tile.opStorage);
			template.energyBar.setYPos(filterUI.yPos()).setMaxYPos(filterUI.maxYPos(), true).setXPos(bg.xPos() + 7);

			//Large/Popout view
			PopoutDialog popOutDialog = new PopoutDialog(bg);
			popOutDialog.onReload(e -> e.setPosAndSize(bg));
			popOutDialog.addChild(filterUI);
			popOutDialog.addChild(new GuiLabel(I18n.get("gui_tkt.brandonscore.click_out_close")).onReload(e -> e.setYPos(bg.maxYPos()).setXPos(bg.xPos()).setSize(200, 12)).setAlignment(GuiAlign.LEFT));

			GuiButton largeView = toolkit.createResizeButton(bg);
			largeView.setPos(filterBG.maxXPos() + 1, filterBG.maxYPos() - 12);

			//Remove the filterUI from the main background, Update its pos and size then display the dialog.
			largeView.onPressed(() -> {
				bg.removeChild(filterUI);
				popOutDialog.setPosAndSize(bg);
				filterUI.setRelPos(bg, 3, 3).setSize(bg.xSize() - 6, bg.ySize() - 6);
				filterBG.setPosAndSize(filterUI);
				popOutDialog.show(100);
			});

			//Return the filterUI to the main background and reset its pos and size
			popOutDialog.setCloseCallback(() -> {
				filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, bg.maxYPos() - 30, true);
				filterBG.setPosAndSize(filterUI);
				bg.addChild(filterUI);
			});
		}
	}
}
