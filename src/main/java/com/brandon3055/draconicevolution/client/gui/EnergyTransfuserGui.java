package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.ForegroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette.Ctrl;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyTransfuser;
import com.brandon3055.draconicevolution.inventory.TransfuserMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class EnergyTransfuserGui extends ContainerGuiProvider<TransfuserMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.transfuser");
    public static final int GUI_WIDTH = 218;
    public static final int GUI_HEIGHT = 215;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("transfuser"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<TransfuserMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        TransfuserMenu menu = screenAccess.getMenu();
		TileEnergyTransfuser tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        GuiText title = TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);
        buttonRow.addButton(e -> TOOLKIT.createRSSwitch(e, screenAccess.getMenu().tile));

        var playInv = GuiSlots.playerAllSlots(root, screenAccess, menu.main, menu.hotBar, menu.armor, menu.offhand);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);
        GuiText invTitle = TOOLKIT.playerInvTitle(playInv.main());

        int padding = 3;
        int columnWidth = 24;
        for (int i = 0; i < 4; i++) {
            int fi = i;
            GuiRectangle column = new GuiRectangle(root)
                    .shadedRect(() -> Ctrl.accentLight(false), () -> Ctrl.accentDark(false), () -> Ctrl.fill(false))
                    .constrain(WIDTH, literal(columnWidth))
                    .constrain(BOTTOM, relative(invTitle.get(TOP), -padding))
                    .constrain(TOP, relative(title.get(BOTTOM), padding))
                    .constrain(LEFT, between(playInv.main().get(LEFT), playInv.main().get(RIGHT), i / 3D, -(columnWidth * i / 3D)));

            GuiSlots slot = GuiSlots.singleSlot(root, screenAccess, menu.slotGroups[i], 0)
                    .setSlotTexture(e -> BCGuiTextures.getThemed("slot"))
                    .constrain(LEFT, relative(column.get(LEFT), 3))
                    .constrain(BOTTOM, relative(column.get(BOTTOM), -3));

            Constraints.bind(new SlotLetter(slot, i, tile).setEnabled(() -> !menu.slotGroups[fi].getSlot(0).hasItem()), slot);

            GuiButton button = TOOLKIT.createBorderlessButton(column)
                    .setResetHoverOnPress(false)
                    .setTooltipSingle(() -> TOOLKIT.translate(tile.ioModes[fi].get().getName()))
                    .onPress(() -> tile.ioModes[fi].set(tile.ioModes[fi].get().nextMode(Screen.hasShiftDown())), GuiButton.LEFT_CLICK)
                    .onPress(() -> tile.ioModes[fi].set(tile.ioModes[fi].get().nextMode(true)), GuiButton.RIGHT_CLICK);
            Constraints.size(button, 16, 16);
            Constraints.placeOutside(button, slot, Constraints.LayoutPos.TOP_CENTER, 0, -1);
            Constraints.bind(new GuiTexture(button, () -> DEGuiTextures.get(tile.ioModes[fi].get().getSpriteName())), button);

            var energyBar = TOOLKIT.createEnergyBar(root, null);
            energyBar.container()
                    .constrain(TOP, relative(column.get(TOP), 3))
                    .constrain(LEFT, relative(column.get(LEFT), 3))
                    .constrain(RIGHT, relative(column.get(RIGHT), -3))
                    .constrain(BOTTOM, relative(button.get(TOP), -1));
            energyBar.bar()
                    .setShaderEnabled(() -> tile.itemsCombined.getStackInSlot(fi).getCapability(CapabilityOP.ITEM) != null)
                    .setItemSupplier(() -> tile.itemsCombined.getStackInSlot(fi))
                    .setDisabled(() -> !EnergyUtils.isEnergyItem(tile.itemsCombined.getStackInSlot(fi)));
        }

        GuiButton modeButton = TOOLKIT.createBorderlessButton(root)
                .setResetHoverOnPress(false)
                .constrain(LEFT, relative(root.get(LEFT), 6))
                .constrain(BOTTOM, relative(invTitle.get(TOP), -3))
                .setTooltipSingle(() -> TOOLKIT.translate(tile.balancedMode.get() ? "balanced_charge" : "sequential_charge"))
                .onPress(() -> tile.balancedMode.invert());
        Constraints.size(modeButton, 20, 20);
        Constraints.bind(new GuiTexture(modeButton, () -> DEGuiTextures.get("transfuser/" + (tile.balancedMode.get() ? "balanced_charge" : "sequential_charge"))), modeButton, 1);
    }

    private static class SlotLetter extends GuiElement<SlotLetter> implements ForegroundRender {
        private final int index;
        private final TileEnergyTransfuser tile;

        public SlotLetter(@NotNull GuiParent<?> parent, int index, TileEnergyTransfuser tile) {
            super(parent);
            this.index = index;
            this.tile = tile;
        }

        @Override
        public void renderForeground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            render.pose().pushPose();
            render.pose().translate(xMin() + (index == 0 ? 5 : index == 2 ? 7 : 4), yMin() + 2, 0);
            render.pose().scale(2, 2, 2);
            render.drawString(RenderTileEnergyTransfuser.TEXT[index].getVisualOrderText(), 0, 0, tile.ioModes[index].get().getColour(), false);
            render.pose().popPose();
        }
    }

    public static class Screen extends ModularGuiContainer<TransfuserMenu> {
        public Screen(TransfuserMenu menu, Inventory inv, Component title) {
            super(menu, inv, new EnergyTransfuserGui());
            getModularGui().setGuiTitle(title);
        }
    }
}


/* I/O modes.
Input       Charge Item
Output      Discharge Item
Buffer      Item is a buffer
*/