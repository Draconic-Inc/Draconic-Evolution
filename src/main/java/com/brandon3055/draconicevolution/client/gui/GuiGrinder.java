package com.brandon3055.draconicevolution.client.gui;

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
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.GuiLayout.EXTRA_WIDE_TALL;
import static com.brandon3055.brandonscore.inventory.ContainerSlotLayout.SlotType.TILE_INV;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class GuiGrinder extends ModularGuiContainer<ContainerBCTile<TileGrinder>> {

    private TileGrinder tile;

    protected GuiToolkit<GuiGrinder> toolkit = new GuiToolkit<>(this, EXTRA_WIDE_TALL);

    public GuiGrinder(ContainerBCTile<TileGrinder> container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(this, tile, container.getSlotLayout()));
        GuiElement bg = template.background;

        GuiEntityFilter filterUI = new GuiEntityFilter(tile.entityFilter);
        filterUI.setNodeBackgroundBuilder(() -> new GuiBorderedRect().set3DGetters(() -> Palette.Ctrl.fill(false), () -> Palette.Ctrl.accentLight(false), () -> Palette.Ctrl.accentDark(false)));
        filterUI.setScrollBarCustomizer(bar -> bar.setSliderElement(new GuiBorderedRect().setGetters(Palette.SubItem::accentDark, () -> 0)).getBackgroundElement().setEnabled(false));
        filterUI.setNodeTitleColour(Palette.Slot::text);
        filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, template.playerSlots.yPos() - 4, true);
        bg.addChild(filterUI);

        GuiBorderedRect filterBG = new GuiBorderedRect();
        filterBG.set3DGetters(Palette.Slot::fill, Palette.Slot::accentDark, Palette.Slot::accentLight);
        filterBG.setBorderColourL(Palette.Slot::border3D);
        filterBG.setPosAndSize(filterUI);
        filterUI.addBackGroundChild(filterBG);

        template.playerSlots.setMaxXPos(bg.maxXPos() - 7, false);
        GuiBorderedRect invBG = new GuiBorderedRect().set3DGetters(Palette.SubItem::fill, Palette.SubItem::accentLight, Palette.SubItem::accentDark);
        invBG.setRelPos(template.playerSlots, -2, -2).setSize(template.playerSlots.xSize() + 4, template.playerSlots.ySize() + 4);
        template.playerSlots.addBackGroundChild(invBG);

        //Power
        template.addEnergyBar(tile.opStorage);
        template.addEnergyItemSlot(false, true, container.getSlotLayout().getSlotData(TILE_INV, 0));
        template.powerSlot.setMaxYPos(filterUI.maxYPos(), false).setXPos(bg.xPos() + 5);
        template.energyBar.setYPos(filterUI.yPos()).setMaxYPos(template.powerSlot.yPos() - 12, true).setXPos(template.powerSlot.xPos() + 2);

        //Large/Popout view
        PopoutDialog popOutDialog = new PopoutDialog(bg);
        popOutDialog.onReload(e -> e.setPosAndSize(bg));
        popOutDialog.addChild(filterUI);
        popOutDialog.addChild(new GuiLabel(I18n.format("gui_tkt.brandonscore.click_out_close")).onReload(e -> e.setYPos(bg.maxYPos()).setXPos(bg.xPos()).setSize(200, 12)).setAlignment(GuiAlign.LEFT));

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
            filterUI.setRelPos(bg, 25, 14).setMaxPos(bg.maxXPos() - 16, template.playerSlots.yPos() - 4, true);
            filterBG.setPosAndSize(filterUI);
            bg.addChild(filterUI);
        });

        //UI Buttons
        GuiButton aoeSize = toolkit.createButton("", bg);
        aoeSize.setHoverText(I18n.format("gui.draconicevolution.grinder.aoe.info"));
        aoeSize.setDisplaySupplier(() -> I18n.format("gui.draconicevolution.grinder.aoe") + " " + getAOEString());
        aoeSize.onButtonPressed((bitton) -> modifyAOE(bitton == 1 || hasShiftDown()));
        aoeSize.setPos(template.powerSlot.xPos(), invBG.yPos()).setYSize(14).setMaxXPos(invBG.xPos() - 2, true);

        GuiButton showAOE = toolkit.createButton("gui.draconicevolution.grinder.show_aoe", bg);
        showAOE.onPressed(tile.showAOE::invert);
        showAOE.setToggleMode(true).setToggleStateSupplier(tile.showAOE::get);
        showAOE.setPos(template.powerSlot.xPos(), aoeSize.maxYPos() + 2).setSize(aoeSize);

        GuiButton collectItems = toolkit.createButton("gui.draconicevolution.grinder.collect.items", bg);
        collectItems.setHoverText(I18n.format("gui.draconicevolution.grinder.collect.items.info"));
        collectItems.onPressed(tile.collectItems::invert);
        collectItems.setToggleMode(true).setToggleStateSupplier(tile.collectItems::get);
        collectItems.setPos(template.powerSlot.xPos(), showAOE.maxYPos() + 2).setSize(aoeSize);

        GuiButton collectXP = toolkit.createButton("gui.draconicevolution.grinder.collect.xp", bg);
        collectXP.setHoverText(I18n.format("gui.draconicevolution.grinder.collect.xp.info"));
        collectXP.onPressed(tile.collectXP::invert);
        collectXP.setToggleMode(true).setToggleStateSupplier(tile.collectXP::get);
        collectXP.setPos(template.powerSlot.xPos(), collectItems.maxYPos() + 2).setSize(aoeSize);

        GuiButton claimXP = toolkit.createButton("gui.draconicevolution.grinder.claim.xp", bg);
        claimXP.onPressed(() -> tile.sendPacketToServer(output -> output.writeByte(0), 1));
        claimXP.setHoverText(I18n.format("gui.draconicevolution.grinder.claim.xp.info"));
        claimXP.setPos(template.powerSlot.xPos(), collectXP.maxYPos() + 2).setSize(aoeSize).setYSize(14);

        GuiButton level = toolkit.createButton("1L", bg);
        level.onPressed(() -> tile.sendPacketToServer(output -> output.writeByte(1), 1));
        level.setSize(claimXP.xSize() / 3, 12).setTrim(false);
        level.setHoverText(I18n.format("gui.draconicevolution.grinder.claim.xp.level.info"));
        toolkit.placeOutside(level, claimXP, GuiToolkit.LayoutPos.BOTTOM_LEFT, level.xSize(), 0);

        GuiButton level5 = toolkit.createButton("5L", bg);
        level5.onPressed(() -> tile.sendPacketToServer(output -> output.writeByte(2), 1));
        level5.setSize(claimXP.xSize() / 3, 12).setTrim(false);
        level5.setHoverText(I18n.format("gui.draconicevolution.grinder.claim.xp.levels.info", "5"));
        toolkit.placeOutside(level5, claimXP, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, 0);

        GuiButton level10 = toolkit.createButton("10L", bg);
        level10.onPressed(() -> tile.sendPacketToServer(output -> output.writeByte(3), 1));
        level10.setSize(claimXP.xSize() / 3, 12).setTrim(false);
        level10.setHoverText(I18n.format("gui.draconicevolution.grinder.claim.xp.levels.info", "10"));
        toolkit.placeOutside(level10, claimXP, GuiToolkit.LayoutPos.BOTTOM_RIGHT, -level10.xSize(), 0);

        //Info Panel
        template.infoPanel.addLabeledValue(GOLD + I18n.format("gui.draconicevolution.grinder.stored_xp"), 6, 11, () -> GRAY + "" + tile.storedXP.get() + " " + I18n.format("gui.draconicevolution.grinder.stored_xp.raw"), true);
    }

    private String getAOEString() {
        int aoe = 1 + (tile.aoe.get() - 1) * 2;
        return aoe + "x" + aoe;
    }

    private void modifyAOE(boolean dec) {
        byte aoe = tile.aoe.get();
        tile.aoe.set((byte) (dec ? aoe == 1 ? tile.getMaxAOE() : aoe - 1 : aoe == tile.getMaxAOE() ? 1 : aoe + 1));
    }
}
