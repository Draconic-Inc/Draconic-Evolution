package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.brandonscore.client.gui.InfoPanel;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiEntityFilter;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.GrinderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class GuiGrinder extends ContainerGuiProvider<GrinderMenu> {
    public static final int GUI_WIDTH = 270;
    public static final int GUI_HEIGHT = 200;
    private static GuiToolkit toolkit = new GuiToolkit("gui.draconicevolution.grinder");
    private boolean largeView = false;
    private TileGrinder tile;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("grinder"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<GrinderMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        GrinderMenu menu = screenAccess.getMenu();
        tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        toolkit.createHeading(root, gui.getGuiTitle(), true);

        InfoPanel infoPanel = InfoPanel.create(root);
        infoPanel.labeledValue(toolkit.translate("stored_xp").withStyle(GOLD), () -> toolkit.translate("xp_value", tile.storedXP.get()).withStyle(GRAY));

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(e -> toolkit.createThemeButton(e));
        buttonRow.addButton(e -> toolkit.createInfoButton(e, infoPanel));
        buttonRow.addButton(e -> toolkit.createRSSwitch(e, screenAccess.getMenu().tile));

        //Inventory
        GuiRectangle invBG = new GuiRectangle(root)
                .shadedRect(Palette.SubItem::accentLight, Palette.SubItem::accentDark, Palette.SubItem::fill);
        var playInv = GuiSlots.player(invBG, screenAccess, menu.main, menu.hotBar);
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_RIGHT, -7, -7);
        Constraints.bind(invBG, playInv.container(), -13, -2, -2, -2);
        toolkit.playerInvTitle(playInv.container());

        //Power
        GuiSlots capInv = GuiSlots.singleSlot(root, screenAccess, menu.capacitor, 0)
                .setEmptyIcon(BCGuiTextures.get("slots/energy"))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(BOTTOM, relative(invBG.get(TOP),-2));
        var energyBar = toolkit.createEnergyBar(root, tile.opStorage);
        energyBar.container()
                .constrain(TOP, relative(root.get(TOP), 14))
                .constrain(LEFT, relative(root.get(LEFT), 7))
                .constrain(BOTTOM, relative(capInv.get(TOP), -12))
                .constrain(WIDTH, literal(14));
        Constraints.placeOutside(toolkit.energySlotArrow(root, false, true), capInv, Constraints.LayoutPos.TOP_CENTER, 0, -1);

        //Weapon Slot
        GuiTexture weaponBg = new GuiTexture(root, BCGuiTextures.getThemed("bg_dynamic_small"))
                .dynamicTexture();
        Constraints.size(weaponBg, 24, 24);
        Constraints.placeOutside(weaponBg, root, Constraints.LayoutPos.BOTTOM_LEFT, 1, -27);

        GuiSlots weaponInv = GuiSlots.singleSlot(root, screenAccess, menu.weapon, 0)
                .setTooltipSingle(toolkit.translate("weapon_slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/sword"));
        Constraints.bind(weaponInv, weaponBg, 2);

        //UI Buttons

        GuiButton aoeSize = toolkit.createFlat3DButton(root, () -> toolkit.translate("aoe").append(" " + getAOEString()))
                .setTooltipSingle(toolkit.translate("aoe.info"))
                .onPress(() -> modifyAOE(Screen.hasShiftDown()), GuiButton.LEFT_CLICK)
                .onPress(() -> modifyAOE(true), GuiButton.RIGHT_CLICK)
                .constrain(HEIGHT, literal(14))
                .constrain(TOP, match(invBG.get(TOP)))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(invBG.get(LEFT), -2));

        GuiButton showAOE = toolkit.createFlat3DButton(root, () -> toolkit.translate("show_aoe"))
                .onPress(tile.showAOE::invert)
                .setToggleMode(tile.showAOE::get)
                .constrain(HEIGHT, literal(14))
                .constrain(TOP, relative(aoeSize.get(BOTTOM), 1))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(invBG.get(LEFT), -2));

        GuiButton collectItems = toolkit.createFlat3DButton(root, () -> toolkit.translate("collect.items"))
                .setTooltip(toolkit.translate("collect.items.info"))
                .onPress(tile.collectItems::invert)
                .setToggleMode(tile.collectItems::get)
                .constrain(HEIGHT, literal(14))
                .constrain(TOP, relative(showAOE.get(BOTTOM), 1))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(invBG.get(LEFT), -2));

        GuiButton collectXP = toolkit.createFlat3DButton(root, () -> toolkit.translate("collect.xp"))
                .setTooltip(toolkit.translate("collect.xp.info"))
                .onPress(tile.collectXP::invert)
                .setToggleMode(tile.collectXP::get)
                .constrain(HEIGHT, literal(14))
                .constrain(TOP, relative(collectItems.get(BOTTOM), 1))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(invBG.get(LEFT), -2));

        GuiButton claimXP = toolkit.createFlat3DButton(root, () -> toolkit.translate("collect.xp"))
                .setTooltip(toolkit.translate("claim.xp.info"))
                .onPress(() -> tile.sendPacketToServer(output -> output.writeByte(0), 1))
                .constrain(HEIGHT, literal(14))
                .constrain(TOP, relative(collectXP.get(BOTTOM), 2))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(invBG.get(LEFT), -2));

        GuiButton level = toolkit.createFlat3DButton(root, () -> Component.literal("1L"))
                .setTooltip(toolkit.translate("claim.xp.level.info"))
                .onPress(() -> tile.sendPacketToServer(output -> output.writeByte(1), 1))
                .constrain(HEIGHT, literal(14))
                .constrain(WIDTH, dynamic(() -> claimXP.xSize() / 3D))
                .constrain(TOP, relative(claimXP.get(BOTTOM), 1))
                .constrain(LEFT, match(claimXP.get(LEFT)));

        GuiButton level5 = toolkit.createFlat3DButton(root, () -> Component.literal("5L"))
                .setTooltip(toolkit.translate("claim.xp.levels.info", 5))
                .onPress(() -> tile.sendPacketToServer(output -> output.writeByte(2), 1))
                .constrain(HEIGHT, literal(14))
                .constrain(WIDTH, dynamic(() -> claimXP.xSize() / 3D))
                .constrain(TOP, relative(claimXP.get(BOTTOM), 1))
                .constrain(LEFT, match(level.get(RIGHT)));

        GuiButton level10 = toolkit.createFlat3DButton(root, () -> Component.literal("10L"))
                .setTooltip(toolkit.translate("claim.xp.levels.info", 10))
                .onPress(() -> tile.sendPacketToServer(output -> output.writeByte(3), 1))
                .constrain(HEIGHT, literal(14))
                .constrain(WIDTH, dynamic(() -> claimXP.xSize() / 3D))
                .constrain(TOP, relative(claimXP.get(BOTTOM), 1))
                .constrain(LEFT, match(level5.get(RIGHT)));

        //Entity Filter
        GuiButton largeViewButton = toolkit.createResizeButton(root)
                .onPress(() -> largeView = !largeView);
        Constraints.placeOutside(largeViewButton, invBG, Constraints.LayoutPos.TOP_RIGHT, -10, -2);

        GuiButton closeLarge = toolkit.createFlat3DButton(root, () -> toolkit.translate("close_large_view"))
                .setEnabled(() -> largeView)
                .onPress(() -> largeView = !largeView)
                .jeiExclude();

        Constraints.size(closeLarge, 140, 14);
        Constraints.placeOutside(closeLarge, root, Constraints.LayoutPos.BOTTOM_CENTER, 0, 1);

        GuiRectangle filterBG = new GuiRectangle(root)
                .setOpaque(true)
                .shadedRect(Palette.Slot::accentDark, Palette.Slot::accentLight, Palette.Slot::fill)
                .constrain(TOP, relative(root.get(TOP), () -> largeView ? 4 : 14D))
                .constrain(LEFT, relative(root.get(LEFT), () -> largeView ? 4 : 25D))
                .constrain(RIGHT, relative(root.get(RIGHT), () -> largeView ? -4 : -16D))
                .constrain(BOTTOM, dynamic(() -> largeView ? (root.getValue(BOTTOM) -4) : (invBG.getValue(TOP) -2)));

        GuiEntityFilter filterUI = new GuiEntityFilter(filterBG, tile.entityFilter)
                .setNodeBgBuilder(e -> new GuiRectangle(e).shadedRect(() -> Palette.Ctrl.accentLight(false), () -> Palette.Ctrl.accentDark(false), () -> Palette.Ctrl.fill(false)))
                .setTitleTextColour(Palette.Slot::text)
                .setScrollBarCustomizer(guiSlider -> Constraints.bind(new GuiRectangle(guiSlider).fill(Palette.SubItem::accentDark), guiSlider.getSlider()))
                .initFilter();
        Constraints.bind(filterUI, filterBG, 2);
    }

    private String getAOEString() {
        int aoe = 1 + (tile.aoe.get() - 1) * 2;
        return aoe + "x" + aoe;
    }

    private void modifyAOE(boolean dec) {
        int aoe = tile.aoe.get();
        tile.aoe.set((byte) (dec ? aoe == 1 ? tile.getMaxAOE() : aoe - 1 : aoe == tile.getMaxAOE() ? 1 : aoe + 1));
    }

    public static class Screen extends ModularGuiContainer<GrinderMenu> {
        public Screen(GrinderMenu menu, Inventory inv, Component title) {
            super(menu, inv, new GuiGrinder());
            getModularGui().setGuiTitle(title);
        }
    }
}
