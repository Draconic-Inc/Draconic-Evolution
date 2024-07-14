package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.ColourState;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.TextState;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.ShaderEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.EnergyCoreMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static com.brandon3055.brandonscore.BCConfig.darkMode;
import static net.minecraft.ChatFormatting.*;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class EnergyCoreGui extends ContainerGuiProvider<EnergyCoreMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.energy_core");
    private final AtomicBoolean colourSelectMode;
    public static final int GUI_WIDTH = 180;
    public static final int GUI_HEIGHT = 200;

    public EnergyCoreGui(AtomicBoolean hideJEI) {
        this.colourSelectMode = hideJEI;
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("energy_core"))
                .setEnabled(() -> !colourSelectMode.get());
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<EnergyCoreMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        EnergyCoreMenu menu = screenAccess.getMenu();
        TileEnergyCore tile = menu.tile;
        GuiElement<?> actualRoot = gui.getRoot();
        GuiElement<?> root = new GuiElement<>(actualRoot)
                .setEnabled(() -> !colourSelectMode.get());
        Constraints.bind(root, actualRoot);

        GuiText title = TOOLKIT.createHeading(root, gui.getGuiTitle(), true).setTextSupplier(() -> TOOLKIT.translate("title", tile.tier.get()));
        gui.renderScreenBackground(!colourSelectMode.get());

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);

        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);
        GuiText slotsTitle = TOOLKIT.playerInvTitle(playInv.container());

        //Buttons
        GuiButton activate = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate(tile.active.get() ? "deactivate" : "activate"))
                .setEnabled(() -> !tile.active.get() && tile.isStructureValid())
                .onPress(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_TOGGLE_ACTIVATION));
        Constraints.size(activate, 18 * 9, 14);
        Constraints.placeOutside(activate, playInv.container(), Constraints.LayoutPos.TOP_CENTER, 0, -14);

        GuiButton tierDown = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("tier_down"))
                .setEnabled(() -> !tile.active.get())
                .setDisabled(() -> tile.tier.get() <= 1)
                .onPress(tile.tier::dec);
        Constraints.size(tierDown, ((18 * 9) / 2D) - 1, 14);
        Constraints.placeInside(tierDown, activate, Constraints.LayoutPos.TOP_LEFT, 0, -15);

        GuiButton tierUp = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("tier_up"))
                .setEnabled(() -> !tile.active.get())
                .setDisabled(() -> tile.tier.get() >= TileEnergyCore.MAX_TIER)
                .onPress(tile.tier::inc);
        Constraints.size(tierUp, ((18 * 9) / 2D) - 1, 14);
        Constraints.placeInside(tierUp, activate, Constraints.LayoutPos.TOP_RIGHT, 0, -15);

        GuiButton buildGuide = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("build_guide"))
                .setToggleMode(tile.buildGuide::get)
                .onPress(tile.buildGuide::invert)
                .setEnabled(() -> !tile.active.get());
        Constraints.size(buildGuide, 18 * 9, 14);
        Constraints.placeOutside(buildGuide, activate, Constraints.LayoutPos.TOP_CENTER, 0, -16);

        GuiButton assemble = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("assemble"))
                .onPress(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_BUILD_CORE))
                .setEnabled(() -> !tile.isStructureValid());
        Constraints.bind(assemble, activate);

        GuiButton disable = TOOLKIT.createThemedIconButton(root, "pwr_btn")
                .setEnabled(tile.active::get)
                .onPress(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_TOGGLE_ACTIVATION))
                .setTooltip(TOOLKIT.translate("deactivate"));
        Constraints.placeInside(disable, root, Constraints.LayoutPos.TOP_LEFT, 3, 3);

        //Labels
        GuiText coreInvalidLabel = TOOLKIT.createHeading(root, TOOLKIT.translate("core_invalid").withStyle(RED))
                .setEnabled(() -> !tile.active.get() && !tile.coreValid.get());
        Constraints.size(coreInvalidLabel, playInv.container().xSize(), 8);
        Constraints.placeOutside(coreInvalidLabel, title, Constraints.LayoutPos.BOTTOM_CENTER, 0, 5);

        GuiText stabInvalidLabel = TOOLKIT.createHeading(root, TOOLKIT.translate("stabilizers_invalid").withStyle(RED))
                .setEnabled(() -> !tile.active.get() && !tile.stabilizersValid.get());
        Constraints.size(stabInvalidLabel, playInv.container().xSize(), 8);
        Constraints.placeOutside(stabInvalidLabel, coreInvalidLabel, Constraints.LayoutPos.BOTTOM_CENTER, 0, 5);

        GuiText stabAdvLabel = TOOLKIT.createHeading(root, TOOLKIT.translate("stabilizers_advanced"))
                .setEnabled(() -> !tile.active.get() && !tile.stabilizersValid.get() && tile.reqAdvStabilizers());
        Constraints.size(stabAdvLabel, playInv.container().xSize(), 8);
        Constraints.placeOutside(stabAdvLabel, stabInvalidLabel, Constraints.LayoutPos.BOTTOM_CENTER, 0, 5);

        //Display
        GuiRectangle display = toolTipBackground(root, 0xF0100010, () -> tile.tier.get() == 8 ? 0xFFFF5500 : 0xFF8800FF)
                .setEnabled(tile.active::get)
                .constrain(LEFT, match(playInv.container().get(LEFT)))
                .constrain(RIGHT, match(playInv.container().get(RIGHT)))
                .constrain(TOP, relative(title.get(BOTTOM), 3))
                .constrain(BOTTOM, relative(slotsTitle.get(TOP), -2));


        GuiRectangle barSlot = new GuiRectangle(display)
                .shadedRect(() -> darkMode ? 0xFF808080 : 0xFF505050, () -> 0xFFFFFFFF, () -> 0);
        Constraints.size(barSlot, display.xSize() - 6, 14);
        Constraints.placeInside(barSlot, display, Constraints.LayoutPos.BOTTOM_CENTER, 0, -3);

        GuiEnergyBar energyBar = new ShaderEnergyBar(display)
                .setCapacity(() -> 1000000L)
                .setEnergy(() -> (long) (getEnergyDouble(tile) * 1000000))
                .setTooltipSingle(() -> {
                    int p1000 = (int) (getEnergyDouble(tile) * 100000);
                    return Component.translatable("mod_gui.brandonscore.energy_bar.stored").withStyle(GOLD).append(": ").append(Component.literal(tile.energy.getScientific() + " (" + (p1000 / 1000D) + "%)").withStyle(GRAY));
                });
        Constraints.bind(energyBar, barSlot, 1);

        GuiText opLabel = new GuiText(display, Component.translatable("mod_gui.brandonscore.energy_bar.operational_potential").withStyle(DARK_AQUA));
        Constraints.size(opLabel, display.xSize(), 9);
        Constraints.placeInside(opLabel, display, Constraints.LayoutPos.TOP_CENTER, 0, 5);

        GuiText energy = new GuiText(display)
                .setTextSupplier(() -> Component.literal(tile.energy.getReadable() + (tile.energy.getEnergyStored() < 1000000 ? " " : ""))
                        .append(Component.translatable("mod_gui.brandonscore.energy_bar.op"))
                        .withStyle(GOLD)
                );
        Constraints.size(energy, display.xSize(), 9);
        Constraints.placeOutside(energy, opLabel, Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);

        GuiText capLabel = new GuiText(display, Component.translatable("mod_gui.brandonscore.energy_bar.capacity").withStyle(DARK_AQUA))
                .setEnabled(() -> !tile.energy.isUnlimited());
        Constraints.size(capLabel, display.xSize(), 9);
        Constraints.placeOutside(capLabel, energy, Constraints.LayoutPos.BOTTOM_CENTER, 0, 3);

        GuiText capacity = new GuiText(display)
                .setTextSupplier(() -> Component.literal(tile.energy.getReadableCapacity())
                        .append(Component.translatable("mod_gui.brandonscore.energy_bar.op"))
                        .withStyle(GOLD)
                )
                .setEnabled(() -> !tile.energy.isUnlimited());
        Constraints.size(capacity, display.xSize(), 9);
        Constraints.placeOutside(capacity, capLabel, Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);

        GuiText ioLabel = new GuiText(display, Component.translatable("mod_gui.brandonscore.energy_bar.io").withStyle(DARK_AQUA));
        Constraints.size(ioLabel, display.xSize(), 9);
        Constraints.placeOutside(ioLabel, capacity, Constraints.LayoutPos.BOTTOM_CENTER, 0, 3);
        ioLabel.constrain(TOP, dynamic(() -> (tile.energy.isUnlimited() ? energy : capacity).yMax() + 3));

        GuiText io = new GuiText(display)
                .setTextSupplier(() -> genIOText(tile));
        Constraints.size(io, display.xSize(), 9);
        Constraints.placeOutside(io, ioLabel, Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);

        var target = GuiTextField.create(display, 0xF0300000, 0xFFa8a8a8, 0xe1e3e5);
        target.container().setEnabled(() -> tile.energy.isUnlimited());
        target.field()
                .setSuggestion(TOOLKIT.translate("energy_target"))
                .setMaxLength(64)
                .setTooltipSingle(TOOLKIT.translate("energy_target_info"))
                .setFilter(s -> validBigInt(sanitizeNumStr(s)))
                .setTextState(TextState.simpleState(tile.energyTarget.get(), tile.energyTarget::set));
        Constraints.size(target.container(), barSlot.xSize(), 12);
        Constraints.placeOutside(target.container(), energyBar, Constraints.LayoutPos.TOP_CENTER, 0, -4);

        //Render Config
        GuiButton legacy = TOOLKIT.createThemedIconButton(root, 12, BCGuiTextures.getter("legacy"))
                .onPress(tile.legacyRender::invert)
                .setToggleMode(tile.legacyRender::get)
                .setTooltipSingle(() -> tile.customColour.get() ? TOOLKIT.translate("legacy_true") : TOOLKIT.translate("legacy_false"));
        Constraints.placeOutside(legacy, display, Constraints.LayoutPos.BOTTOM_RIGHT, -12, 0);

        GuiButton customColour = TOOLKIT.createThemedIconButton(root, 12, BCGuiTextures.getter("rgb_checker"))
                .setEnabled(() -> !legacy.toggleState())
                .onPress(tile.customColour::invert)
                .setToggleMode(tile.customColour::get)
                .setTooltipSingle(() -> tile.customColour.get() ? TOOLKIT.translate("custom_colour_true") : TOOLKIT.translate("custom_colour_false"));
        Constraints.placeOutside(customColour, legacy, Constraints.LayoutPos.MIDDLE_LEFT, 0, 0);

        GuiButton setColour = TOOLKIT.createThemedIconButton(root, 12, BCGuiTextures.getter("color_picker"))
                .setEnabled(() -> customColour.toggleState() && !legacy.toggleState())
                .onPress(() -> setColourSelectMode(root, true))
                .setTooltipSingle(() -> TOOLKIT.translate("config_colour"));
        Constraints.placeOutside(setColour, customColour, Constraints.LayoutPos.MIDDLE_LEFT, 0, 0);

        setupColourPickers(gui.getDirectRoot(), tile);
    }

    private void setColourSelectMode(GuiElement<?> access, boolean enabled) {
        colourSelectMode.set(enabled);
        access.getModularGui().renderScreenBackground(!enabled);
    }

    private void setupColourPickers(GuiElement<?> root, TileEnergyCore tile) {
        GuiColourPicker frameColourDialog = GuiColourPicker.create(root, ColourState.create(tile.frameColour::get, tile.frameColour::set), false);
        frameColourDialog.constrain(TOP, literal(0));
        frameColourDialog.constrain(LEFT, literal(0));
        frameColourDialog.addMoveHandle((int) frameColourDialog.ySize());
        frameColourDialog.setEnabled(colourSelectMode::get);
        frameColourDialog.getCancelButton().setEnabled(false);
        frameColourDialog.getOkButton().onPress(() -> setColourSelectMode(root, false));

        GuiColourPicker triangleColourDialog = GuiColourPicker.create(root, ColourState.create(tile.innerColour::get, tile.innerColour::set), false);
        triangleColourDialog.constrain(TOP, literal(100));
        triangleColourDialog.constrain(LEFT, literal(0));
        triangleColourDialog.addMoveHandle((int) triangleColourDialog.ySize());
        triangleColourDialog.setEnabled(colourSelectMode::get);
        triangleColourDialog.getCancelButton().setEnabled(false);
        triangleColourDialog.getOkButton().onPress(() -> setColourSelectMode(root, false));

        GuiColourPicker effectColourDialog = GuiColourPicker.create(root, ColourState.create(tile.effectColour::get, tile.effectColour::set), false);
        effectColourDialog.constrain(TOP, literal(200));
        effectColourDialog.constrain(LEFT, literal(0));
        effectColourDialog.addMoveHandle((int) effectColourDialog.ySize());
        effectColourDialog.setEnabled(colourSelectMode::get);
        effectColourDialog.getCancelButton().setEnabled(false);
        effectColourDialog.getOkButton().onPress(() -> setColourSelectMode(root, false));
    }

    private double getEnergyDouble(TileEnergyCore tile) {
        if (tile.tier.get() < 8) {
            return tile.energy.getOPStored() / (double) tile.energy.getMaxOPStored();
        }
        if (!validBigInt(sanitizeNumStr(tile.energyTarget.get()))) {
            return 0;
        }
        BigDecimal target = new BigDecimal(sanitizeNumStr(tile.energyTarget.get()));
        if (target.compareTo(BigDecimal.ONE) < 0) {
            return 0;
        }
        double val = new BigDecimal(tile.energy.getStoredBig()).divide(target, 6, RoundingMode.HALF_EVEN).doubleValue();
        return MathHelper.clip(val, 0, 1);
    }

    private Component genIOText(TileEnergyCore tile) {
        IOInfo ioInfo = tile.energy.getIOInfo();
        if (ioInfo == null) return Component.literal("[Not Available]"); //Should never hit this
        String pfx = "mod_gui.brandonscore.energy_bar.";

        if (Screen.hasShiftDown()) {
            return Component.empty().copy()
                    .append(Component.literal("+")
                            .withStyle(GREEN)
                            .append(Utils.formatNumber(ioInfo.currentInput()))
                            .append(" ")
                            .append(Component.translatable(pfx + "op"))
                            .append("/t, ")
                    )
                    .append(Component.literal("-")
                            .withStyle(RED)
                            .append(Utils.formatNumber(ioInfo.currentOutput()))
                            .append(" ")
                            .append(Component.translatable(pfx + "op"))
                            .append("/t, ")
                    );
        } else {
            long io = ioInfo.currentInput() - ioInfo.currentOutput();
            return Component.empty().copy()
                    .append(io > 0 ? "+" : "")
                    .append(Component.literal(Utils.formatNumber(io)))
                    .append(" ")
                    .append(Component.translatable(pfx + "op"))
                    .append("/t")
                    .withStyle(io > 0 ? GREEN : io < 0 ? RED : GRAY);
        }
    }

    /**
     * This is just limits the target value the user can specify.
     * Things get incredibly slow when using numbers bigger then this.
     * <p>
     * This does not limit the core's capacity.
     * I think i'm just going to have to accept that after around 10^10^5 years at an input rate of 2^64 * 2^32
     * things may start to get a bit laggy....
     */
    private static final BigDecimal MAX_BIGINT = new BigDecimal("9999e9999");

    public static boolean validBigInt(String value) {
        try {
            BigDecimal val = new BigDecimal(value);
            return val.compareTo(BigDecimal.ZERO) >= 0 && val.compareTo(MAX_BIGINT) <= 0;
        } catch (Throwable e) {
            return false;
        }
    }

    public static String sanitizeNumStr(String value) {
        if (value.isEmpty() || value.toLowerCase(Locale.ENGLISH).endsWith("e")) {
            value += "0";
        }
        return value;
    }

    public static GuiRectangle toolTipBackground(@NotNull GuiParent<?> parent, int backgroundColour, Supplier<Integer> borderColour) {
        Supplier<Integer> borderEndColor = () -> (borderColour.get() & 0xFEFEFE) >> 1 | borderColour.get() & 0xFF000000;
        return new GuiRectangle(parent) {
            @Override
            public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
                render.toolTipBackground(xMin(), yMin(), xSize(), ySize(), backgroundColour, backgroundColour, borderColour.get(), borderEndColor.get(), false);
            }
        };
    }

    public static class Screen extends ModularGuiContainer<EnergyCoreMenu> {
        public static AtomicBoolean hideJEI = new AtomicBoolean(false);

        public Screen(EnergyCoreMenu menu, Inventory inv, Component title) {
            super(menu, inv, new EnergyCoreGui(hideJEI));
            getModularGui().setGuiTitle(title);
        }
    }
}
