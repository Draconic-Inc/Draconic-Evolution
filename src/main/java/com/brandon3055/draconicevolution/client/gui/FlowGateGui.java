package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.FlowGateMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class FlowGateGui extends ContainerGuiProvider<FlowGateMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.flow_gate");
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 166;

    private long ltMin = -1;
    private long ltMax = -1;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("flow_gate"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<FlowGateMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        FlowGateMenu menu = screenAccess.getMenu();
        TileFlowGate tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);

        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);
        TOOLKIT.playerInvTitle(playInv.container());

        GuiText overrideText = new GuiText(root, TOOLKIT.translate("overridden"))
                .setTooltip(TOOLKIT.translate("overridden.info"))
                .setTextColour(0xFF0000)
                .setShadow(false)
                .setEnabled(tile.flowOverridden::get);
        Constraints.size(overrideText, 60, 8);
        Constraints.placeInside(overrideText, root, Constraints.LayoutPos.TOP_CENTER, 0, 20);

        GuiText highLabel = new GuiText(root, TOOLKIT.translate("redstone_high"))
                .setTextColour(0xFF0000)
                .setEnabled(() -> !tile.flowOverridden.get())
                .setShadow(false);
        Constraints.size(highLabel, playInv.container().xSize(), 8);
        Constraints.placeInside(highLabel, root, Constraints.LayoutPos.TOP_CENTER, 0, 18);

        var highField = TOOLKIT.createTextField(root);
        highField.container()
                .setEnabled(() -> !tile.flowOverridden.get())
                .constrain(LEFT, match(highLabel.get(LEFT)))
                .constrain(TOP, relative(highLabel.get(BOTTOM), 2));
        highField.field()
                .setFilter(TOOLKIT.catchyValidator(s -> s.isEmpty() || Long.parseLong(s) >= 0));
        Constraints.size(highField.container(), highLabel.xSize() - 60, 14);

        GuiButton applyHigh = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("apply"))
                .setEnabled(() -> !tile.flowOverridden.get())
                .onPress(() -> tile.setMax(highField.field().getValue()));
        Constraints.size(applyHigh, 59, 14);
        Constraints.placeOutside(applyHigh, highField.container(), Constraints.LayoutPos.MIDDLE_RIGHT, 1, 0);


        GuiText lowLabel = new GuiText(root, TOOLKIT.translate("redstone_low"))
                .setTextColour(0x990000)
                .setEnabled(() -> !tile.flowOverridden.get())
                .setShadow(false)
                .constrain(LEFT, match(highLabel.get(LEFT)))
                .constrain(TOP, relative(highField.field().get(BOTTOM), 3));
        Constraints.size(lowLabel, playInv.container().xSize(), 8);

        var lowField = TOOLKIT.createTextField(root);
        lowField.container()
                .setEnabled(() -> !tile.flowOverridden.get())
                .constrain(LEFT, match(lowLabel.get(LEFT)))
                .constrain(TOP, relative(lowLabel.get(BOTTOM), 2));
        lowField.field()
                .setFilter(TOOLKIT.catchyValidator(s -> s.isEmpty() || Long.parseLong(s) >= 0));
        Constraints.size(lowField.container(), lowLabel.xSize() - 60, 14);

        GuiButton applyLow = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("apply"))
                .setEnabled(() -> !tile.flowOverridden.get())
                .onPress(() -> tile.setMin(lowField.field().getValue()));
        Constraints.size(applyLow, 59, 14);
        Constraints.placeOutside(applyLow, lowField.container(), Constraints.LayoutPos.MIDDLE_RIGHT, 1, 0);

        GuiText flow = new GuiText(root)
                .setTooltip(TOOLKIT.translate("flow.info"))
                .setShadow(false)
                .setAlignment(Align.RIGHT)
                .setTextColour(GuiToolkit.Palette.Slot::text)
                .setTextSupplier(() -> TOOLKIT.translate("flow").append(": " + (tile.getFlow() > 999999 ? Utils.formatNumber(tile.getFlow()) : Utils.addCommas(tile.getFlow())) + tile.getUnits()))
                .constrain(RIGHT, match(playInv.container().get(RIGHT)))
                .constrain(BOTTOM, relative(playInv.container().get(TOP), -3))
                .constrain(LEFT, match(playInv.container().get(LEFT)))
                .constrain(HEIGHT, literal(8));

        gui.onTick(() -> {
            if (tile.minFlow.get() != ltMin) {
                ltMin = tile.minFlow.get();
                lowField.field().setValue(String.valueOf(ltMin));
            }
            if (tile.maxFlow.get() != ltMax) {
                ltMax = tile.maxFlow.get();
                highField.field().setValue(String.valueOf(ltMax));
            }
            if (!highField.field().isFocused() && highField.field().getValue().isEmpty()) {
                highField.field().setValue("0");
            }
            if (!lowField.field().isFocused() && lowField.field().getValue().isEmpty()) {
                lowField.field().setValue("0");
            }
        });
    }

    public static class Screen extends ModularGuiContainer<FlowGateMenu> {
        public Screen(FlowGateMenu menu, Inventory inv, Component title) {
            super(menu, inv, new FlowGateGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
