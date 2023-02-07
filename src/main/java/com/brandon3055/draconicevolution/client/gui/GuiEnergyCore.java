package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.SlotDisableable;
import com.brandon3055.brandonscore.inventory.SlotMover;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.client.gui.modular.TModularMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends ModularGuiContainer<ContainerBCTile<TileEnergyCore>> {
    private GuiToolkit<GuiEnergyCore> toolkit = new GuiToolkit<>(this, 180, 200).setTranslationPrefix("gui.draconicevolution.energy_core");

    public Player player;
    public TileEnergyCore tile;

    private GuiPickColourDialog frameColourDialog;
    private GuiPickColourDialog triangleColourDialog;
    private GuiPickColourDialog effectColourDialog;
    public Supplier<Boolean> hideJEI = () -> false;

    public GuiEnergyCore(ContainerBCTile<TileEnergyCore> container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.player = playerInventory.player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine temp = new TModularMachine(this, tile);
        temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(temp);
        temp.title.setDisplaySupplier(() -> toolkit.i18n("title", tile.tier.get()));

        container.slots.stream()
                .filter(e -> e instanceof SlotDisableable)
                .map(e -> (SlotDisableable) e)
                .forEach(e -> e.setEnabled(() -> temp.background.isEnabled()));

        hideJEI = () -> !temp.background.isEnabled();

        //Controls

        GuiButton activate = toolkit.createButton(() -> tile.active.get() ? "deactivate" : "activate", temp.background)
                .setSize(temp.playerSlots.xSize(), 14)
                .setEnabledCallback(() -> !tile.active.get() && tile.isStructureValid())
                .onPressed(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_TOGGLE_ACTIVATION));
        toolkit.placeOutside(activate, temp.playerSlots, GuiToolkit.LayoutPos.TOP_CENTER, 0, -3);

        GuiButton tierDown = toolkit.createButton("tier_down", temp.background)
                .setSize((temp.playerSlots.xSize() / 2) - 1, 14)
                .setXPos(activate.xPos())
                .setMaxYPos(activate.yPos() - 1, false)
                .setEnabledCallback(() -> !tile.active.get())
                .setDisabledStateSupplier(() -> tile.tier.get() <= 1)
                .onPressed(() -> tile.tier.dec());

        GuiButton tierUp = toolkit.createButton("tier_up", temp.background)
                .setSize((temp.playerSlots.xSize() / 2) - 1, 14)
                .setMaxXPos(activate.maxXPos(), false)
                .setMaxYPos(activate.yPos() - 1, false)
                .setEnabledCallback(() -> !tile.active.get())
                .setDisabledStateSupplier(() -> tile.tier.get() >= TileEnergyCore.MAX_TIER)
                .onPressed(() -> tile.tier.inc());

        GuiButton buildGuide = toolkit.createButton("build_guide", temp.background)
                .setToggleStateSupplier(() -> tile.buildGuide.get())
                .onPressed(() -> tile.buildGuide.invert())
                .setEnabledCallback(() -> !tile.active.get())
                .setSize(temp.playerSlots.xSize(), 14)
                .setXPos(tierDown.xPos())
                .setMaxYPos(tierDown.yPos() - 1, false);

        GuiButton assemble = toolkit.createButton("assemble", temp.background)
                .setPosAndSize(activate)
                .setEnabledCallback(() -> !tile.isStructureValid())
                .onPressed(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_BUILD_CORE));

        GuiButton disable = toolkit.createThemedIconButton(temp.background, "pwr_btn")
                .setEnabledCallback(() -> tile.active.get())
                .onPressed(() -> tile.sendPacketToServer(e -> {}, TileEnergyCore.MSG_TOGGLE_ACTIVATION))
                .setHoverText(toolkit.i18n("deactivate"));
        toolkit.placeInside(disable, temp.background, GuiToolkit.LayoutPos.TOP_LEFT, 3, 3);

        GuiLabel coreInvalidLabel = toolkit.createHeading("", temp.background).setLabelText(toolkit.i18n("core_invalid"))
                .setAlignment(GuiAlign.CENTER)
                .setTextColour(RED)
                .setSize(temp.playerSlots.xSize(), 8)
                .setPos(temp.playerSlots.xPos(), temp.title.maxYPos() + 5)
                .setEnabledCallback(() -> !tile.active.get() && !tile.coreValid.get());

        GuiLabel stabInvalidLabel = toolkit.createHeading("", temp.background).setLabelText(toolkit.i18n("stabilizers_invalid"))
                .setAlignment(GuiAlign.CENTER)
                .setTextColour(RED)
                .setSize(temp.playerSlots.xSize(), 8)
                .setPos(temp.playerSlots.xPos(), coreInvalidLabel.maxYPos() + 5)
                .setEnabledCallback(() -> !tile.active.get() && !tile.stabilizersValid.get());

        GuiLabel stabAdvLabel = toolkit.createHeading("", temp.background).setLabelText(toolkit.i18n("stabilizers_advanced"))
                .setAlignment(GuiAlign.CENTER)
                .setSize(temp.playerSlots.xSize(), 8)
                .setPos(temp.playerSlots.xPos(), stabInvalidLabel.maxYPos() + 5)
                .setEnabledCallback(() -> !tile.active.get() && !tile.stabilizersValid.get() && tile.reqAdvStabilizers());

        //Display

        GuiTooltipBackground display = temp.background.addChild(new GuiTooltipBackground())
                .setBorderColor(() -> tile.tier.get() == 8 ? 0xFFFF5500 : 0xFF8800FF)
                .setBackgroundColor(() -> 0xF0100010)
                .setPos(temp.playerSlots.xPos(), temp.title.maxYPos() + 3)
                .setXSize(temp.playerSlots.xSize())
                .setMaxYPos(temp.playerSlots.yPos() - 2, true)
                .setEnabledCallback(() -> tile.active.get());

        String pfx = "mod_gui.brandonscore.energy_bar.";
        GuiEnergyBar energyBar = display.addChild(new GuiEnergyBar())
                .setCapacitySupplier(() -> 1000000L)
                .setEnergySupplier(() -> (long) (getEnergyDouble() * 1000000))
                .setSize(activate.xSize() - 6, 14)
                .setDrawHoveringText(false)
                .setHoverText(e -> {
                    int p1000 = (int) (getEnergyDouble() * 100000);
                    return GOLD + I18n.get(pfx + "stored") + ": " + GRAY + tile.energy.getScientific() + " (" + (p1000 / 1000D) + "%)";
                });
        toolkit.placeInside(energyBar, display, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -3);

        GuiLabel opLabel = display.addChild(new GuiLabel(I18n.get(pfx + "operational_potential")))
                .setSize(display.xSize(), 8)
                .setRelPos(display, 0, 6)
                .setTextColour(DARK_AQUA)
                .setAlignment(GuiAlign.CENTER);

        GuiLabel energy = display.addChild(new GuiLabel())
                .setDisplaySupplier(() -> tile.energy.getReadable() + (tile.energy.getEnergyStored() < 1000000 ? " " : "") + I18n.get(pfx + "op"))
                .setSize(display.xSize(), 8)
                .setPos(display.xPos(), opLabel.maxYPos() + 3)
                .setTextColour(GOLD)
                .setAlignment(GuiAlign.CENTER);

        GuiLabel capLabel = display.addChild(new GuiLabel())
                .setDisplaySupplier(() -> I18n.get(pfx + "capacity"))
                .setSize(display.xSize(), 8)
                .setPos(display.xPos(), energy.maxYPos() + 4)
                .setTextColour(DARK_AQUA)
                .setAlignment(GuiAlign.CENTER)
                .setEnabledCallback(() -> !tile.energy.isUnlimited());

        GuiLabel capacity = display.addChild(new GuiLabel())
                .setDisplaySupplier(() -> tile.energy.getReadableCapacity() + I18n.get(pfx + "op"))
                .setSize(display.xSize(), 8)
                .setPos(display.xPos(), capLabel.maxYPos() + 3)
                .setTextColour(GOLD)
                .setAlignment(GuiAlign.CENTER)
                .setEnabledCallback(() -> !tile.energy.isUnlimited());

        GuiLabel ioLabel = display.addChild(new GuiLabel())
                .setDisplaySupplier(() -> I18n.get(pfx + "io"))
                .setSize(display.xSize(), 8)
                .setXPos(display.xPos())
                .setYPosMod(() -> (tile.energy.isUnlimited() ? energy : capacity).maxYPos() + 4)
                .setTextColour(DARK_AQUA)
                .setAlignment(GuiAlign.CENTER);

        GuiLabel io = display.addChild(new GuiLabel())
                .setDisplaySupplier(this::genIOText)
                .setSize(display.xSize(), 8)
                .setPos(display.xPos(), ioLabel.maxYPos() + 3)
                .setTextColour(GOLD)
                .setAlignment(GuiAlign.CENTER);

        GuiTextField target = toolkit.createTextField(display, false)
                .addBackground(e -> 0xF0300000, e -> 0xFFa8a8a8)
                .setMaxLength(64)
                .setTextColor(0xe1e3e5)
                .setSuggestion(toolkit.i18n("energy_target"))
                .setHoverText(toolkit.i18n("energy_target_info").replace("\n", "\n" + GRAY))
                .setSize(energyBar.xSize(), 12)
                .setXPos(energyBar.xPos())
                .setMaxYPos(energyBar.yPos() - 4, false)
                .setValue(tile.energyTarget.get())
                .onValueChanged(s -> tile.energyTarget.set(s))
                .setFilter(s -> validBigInt(sanitizeNumStr(s)))
                .setEnabledCallback(() -> tile.energy.isUnlimited());

        //Render Config
        GuiButton legacy = toolkit.createIconButton(temp.background, 12, 12, "legacy")
                .onPressed(() -> tile.legacyRender.invert())
                .setHoverText(e -> tile.legacyRender.get() ? toolkit.i18n("legacy_true") : toolkit.i18n("legacy_false"))
                .setToggleStateSupplier(() -> tile.legacyRender.get())
                .setToggleMode(true);
        toolkit.placeOutside(legacy, display, GuiToolkit.LayoutPos.BOTTOM_RIGHT, -12, 0);

        GuiButton customColour = toolkit.createIconButton(temp.background, 12, 12, "rgb_checker")
                .onPressed(() -> tile.customColour.invert())
                .setHoverText(e -> tile.customColour.get() ? toolkit.i18n("custom_colour_true") : toolkit.i18n("custom_colour_false"))
                .setToggleStateSupplier(() -> tile.customColour.get())
                .setToggleMode(true)
                .setEnabledCallback(() -> !legacy.getToggleState());
        toolkit.placeOutside(customColour, legacy, GuiToolkit.LayoutPos.MIDDLE_LEFT, 0, 0);

        GuiButton setColour = toolkit.createIconButton(temp.background, 12, 12, "color_picker")
                .setHoverText(e -> toolkit.i18n("config_colour"))
                .setEnabledCallback(() -> customColour.getToggleState() && !legacy.getToggleState())
                .onPressed(() -> colourEditMode(temp.background, true));
        toolkit.placeOutside(setColour, customColour, GuiToolkit.LayoutPos.MIDDLE_LEFT, 0, 0);

        setupColourPickers(temp.background);
    }

    private void setupColourPickers(GuiElement<?> bgElement) {
        frameColourDialog = new GuiPickColourDialog(bgElement)
                .setCloseOnOutsideClick(false)
                .setBackgroundElement(new GuiTooltipBackground())
                .setColourChangeListener(tile.frameColour::set)
                .setIncludeAlpha(false)
                .setCloseCallback(() -> colourEditMode(bgElement, false))
                .setCancelEnabled(true)
                .setEnabledCallback(() -> !bgElement.isEnabled());
        toolkit.jeiExclude(frameColourDialog);

        triangleColourDialog = new GuiPickColourDialog(bgElement)
                .setCloseOnOutsideClick(false)
                .setBackgroundElement(new GuiTooltipBackground())
                .setColourChangeListener(tile.innerColour::set)
                .setIncludeAlpha(false)
                .setCloseCallback(() -> colourEditMode(bgElement, false))
                .setCancelEnabled(true)
                .setEnabledCallback(() -> !bgElement.isEnabled());
        toolkit.jeiExclude(triangleColourDialog);

        effectColourDialog = new GuiPickColourDialog(bgElement)
                .setCloseOnOutsideClick(false)
                .setBackgroundElement(new GuiTooltipBackground())
                .setColourChangeListener(tile.effectColour::set)
                .setIncludeAlpha(false)
                .setCloseCallback(() -> colourEditMode(bgElement, false))
                .setCancelEnabled(true)
                .setEnabledCallback(() -> !bgElement.isEnabled());
        toolkit.jeiExclude(effectColourDialog);
    }

    private void colourEditMode(GuiElement<?> bgElement, boolean enabled) {
        bgElement.setEnabled(!enabled);
        enableDefaultBackground = !enabled;

        if (enabled) {
            frameColourDialog.setColour(tile.frameColour.get())
                    .setYPos(5).setMaxXPos(width - 50, false)
                    .normalizePosition()
                    .show(200);
            frameColourDialog.cancelButton
                    .onPressed(() -> frameColourDialog.updateColour(tile.tier.get() == 8 ? TileEnergyCore.DEFAULT_FRAME_COLOUR_T8 : TileEnergyCore.DEFAULT_FRAME_COLOUR))
                    .setInsets(0, 0, 0, 0)
                    .setText(toolkit.i18n("reset"));

            triangleColourDialog.setColour(tile.innerColour.get())
                    .setYPos(frameColourDialog.maxYPos() + 2).setMaxXPos(width - 50, false)
                    .normalizePosition()
                    .show(200);
            triangleColourDialog.cancelButton
                    .onPressed(() -> triangleColourDialog.updateColour(tile.tier.get() == 8 ? TileEnergyCore.DEFAULT_TRIANGLE_COLOUR_T8 : TileEnergyCore.DEFAULT_TRIANGLE_COLOUR))
                    .setInsets(0, 0, 0, 0)
                    .setText(toolkit.i18n("reset"));

            effectColourDialog.setColour(tile.effectColour.get())
                    .setYPos(triangleColourDialog.maxYPos() + 2).setMaxXPos(width - 50, false)
                    .normalizePosition()
                    .show(200);
            effectColourDialog.cancelButton
                    .onPressed(() -> effectColourDialog.updateColour(tile.tier.get() == 8 ? TileEnergyCore.DEFAULT_EFFECT_COLOUR_T8 : TileEnergyCore.DEFAULT_EFFECT_COLOUR))
                    .setInsets(0, 0, 0, 0)
                    .setText(toolkit.i18n("reset"));
        }
    }

    private double getEnergyDouble() {
        if (tile.tier.get() < 8) {
            return tile.energy.getOPStored() / (double) tile.energy.getMaxOPStored();
        }
        if (!validBigInt(sanitizeNumStr(tile.energyTarget.get()))) {
            return 0;
        }
        BigDecimal target = new BigDecimal(sanitizeNumStr(tile.energyTarget.get()));
        if (target.equals(BigDecimal.ZERO)) {
            return 0;
        }
        double val = new BigDecimal(tile.energy.getStoredBig()).divide(target, 6, RoundingMode.HALF_EVEN).doubleValue();
        return MathHelper.clip(val, 0, 1);
    }

    private String genIOText() {
        IOInfo ioInfo = tile.energy.getIOInfo();
        if (ioInfo == null) return "[Not Available]"; //Should never hit this
        String pfx = "mod_gui.brandonscore.energy_bar.";
        StringBuilder builder = new StringBuilder();
        if (hasShiftDown()) {
            builder.append(GREEN).append("+").append(Utils.formatNumber(ioInfo.currentInput()));
            builder.append(" ").append(I18n.get(pfx + "op")).append("/t, ");

            builder.append(RED).append("-").append(Utils.formatNumber(ioInfo.currentOutput()));
            builder.append(" ").append(I18n.get(pfx + "op")).append("/t");
        } else {
            long io = ioInfo.currentInput() - ioInfo.currentOutput();
            builder.append(io > 0 ? GREEN + "+" : io < 0 ? RED : GRAY);
            builder.append(Utils.formatNumber(io)).append(" ").append(I18n.get(pfx + "op")).append("/t");
        }
        return builder.toString();
    }

    /**
     * This is just limits the target value the user can specify.
     * Things get incredibly slow when using numbers bigger then this.
     *
     * This does not limit the core's capacity.
     * I think i'm just going to have to accept that after around 10^10^5 years at an input rate of 2^64 * 2^32
     * things may start to get a bit laggy....
     * */
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
}
