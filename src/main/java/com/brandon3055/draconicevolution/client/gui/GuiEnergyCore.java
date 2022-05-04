package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.gui.GuiButtonAHeight;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.*;

import java.text.DecimalFormat;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends ModularGuiContainer<ContainerBCTile<TileEnergyCore>> {

    public PlayerEntity player;
    public TileEnergyCore tile;
    private Button activate;
    private Button tierUp;
    private Button tierDown;
    private Button toggleGuide;
    private Button assembleCore;
    private Button layerPlus;
    private Button layerMinus;
    public static int layer = -1;

    //Charge/Discharge time
    private long ticks;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;
    private long years;

    public GuiEnergyCore(ContainerBCTile<TileEnergyCore> container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.imageWidth = 180;
        this.imageHeight = 200;
        this.player = playerInventory.player;
        dumbGui = true;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        //TODO
        container.slots.forEach(slot -> {
            if (slot.index < 9) {
                slot.x += 10;
                slot.y += 174;
            }
            else {
                slot.x += 10;
                slot.y += 98;
            }
        });
    }


    @Override
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);

        addButton(activate = new GuiButtonAHeight(leftPos + 9, topPos + 99, 162, 12, "Activate-L", (b) -> tile.sendPacketToServer(output -> {}, 0)));
        addButton(tierUp = new GuiButtonAHeight(leftPos + 91, topPos + 86, 80, 12, I18n.get("button.de.tierUp.txt"), (b) -> tile.sendPacketToServer(output -> {}, 1)));
        addButton(tierDown = new GuiButtonAHeight(leftPos + 9, topPos + 86, 80, 12, I18n.get("button.de.tierDown.txt"), (b) -> tile.sendPacketToServer(output -> {}, 2)));
        addButton(toggleGuide = new GuiButtonAHeight(leftPos + 9, topPos + 73, 162, 12, I18n.get("button.de.buildGuide.txt"), (b) -> tile.sendPacketToServer(output -> {}, 3)));
        addButton(assembleCore = new GuiButtonAHeight(leftPos + 9, topPos + 99, 162, 12, I18n.get("button.de.assembleCore.txt"), (b) -> tile.sendPacketToServer(output -> {}, 4)));

        addButton(layerMinus = new GuiButtonAHeight(leftPos + 5, topPos - 13, 70, 12, "Layer-", (b) -> layer(-1)));
        addButton(layerPlus = new GuiButtonAHeight(leftPos + 105, topPos - 13, 70, 12, "Layer+", (b) -> layer(1)));
        layerPlus.visible = tile.buildGuide.get();
        layerMinus.visible = tile.buildGuide.get();

        updateButtonStates();
    }

    @Override
    protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GuiHelperOld.drawGuiBaseBackground(this, leftPos, topPos, imageWidth, imageHeight);
        GuiHelperOld.drawPlayerSlots(this, leftPos + (imageWidth / 2), topPos + 115, true);
        drawCenteredString(mStack, font, I18n.get("gui.de.energyStorageCore.name", tile.tier.get()), leftPos + (imageWidth / 2), topPos + 5, InfoHelper.GUI_TITLE);

        if (tile.active.get()) {
            GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.capacity.txt"), leftPos + imageWidth / 2, topPos + 16, 0xFFAA00, true);
            String capText = tile.tier.get() == 8 ? I18n.get("gui.de.almostInfinite.txt") : Utils.formatNumber(tile.getExtendedCapacity());
            GuiHelperOld.drawCenteredString(font, capText, leftPos + imageWidth / 2, topPos + 27, 0x555555, false);

            DecimalFormat energyValue = new DecimalFormat("###.###");
            double percent = (double) tile.getExtendedStorage() / (double) tile.getExtendedCapacity() * 100D;
            GuiHelperOld.drawCenteredString(font, I18n.get("info.bc.charge.txt"), leftPos + imageWidth / 2, topPos + 38, 0xFFAA00, true);
            GuiHelperOld.drawCenteredString(font, Utils.formatNumber(tile.getExtendedStorage()) + " OP [" + energyValue.format(percent) + "%]", leftPos + imageWidth / 2, topPos + 49, 0x555555, false);

            int transferColour = tile.transferRate.get() > 0 ? 0x00FF00 : tile.transferRate.get() < 0 ? 0xFF0000 : 0x222222;
            String transfer = (tile.transferRate.get() > 0 ? "+" : tile.transferRate.get() < 0 ? "-" : "") + Utils.formatNumber(Math.abs(tile.transferRate.get())) + " OP/t";
            GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.transfer.txt"), leftPos + imageWidth / 2, topPos + 59, 0xFFAA00, true);
            GuiHelperOld.drawCenteredString(font, transfer, leftPos + imageWidth / 2, topPos + 70, transferColour, tile.transferRate.get() > 0);


            if (tile.transferRate.get() != 0) {
                String time = "";
                if (years > 0) {
                    time += formatYear(years) + ", ";
                    time += days % 365 + " Days";
                } else if (days > 0) {
                    time += days % 365 + " Days, ";
                    time += (hours % 24 < 10 ? "0" : "") + hours % 24 + ":";
                    time += (minutes % 60 < 10 ? "0" : "") + minutes % 60 + ":";
                    time += (seconds % 60 < 10 ? "0" : "") + seconds % 60 + "." + (ticks % 20 < 10 ? "0" : "") + ticks % 20;
                } else {
                    time += (hours % 24 < 10 ? "0" : "") + hours % 24 + ":";
                    time += (minutes % 60 < 10 ? "0" : "") + minutes % 60 + ":";
                    time += (seconds % 60 < 10 ? "0" : "") + seconds % 60 + "." + (ticks % 20 < 10 ? "0" : "") + ticks % 20;
                }

                GuiHelperOld.drawCenteredString(font, time, leftPos + imageWidth / 2, topPos + 70 + 10, 0x555555, false);
            }
        } else {
            int stabColour = tile.stabilizersOK.get() ? 0x00FF00 : 0xFF0000;
            String stabText = I18n.get("gui.de.stabilizers.txt") + ": " + (tile.stabilizersOK.get() ? I18n.get("gui.de.valid.txt") : I18n.get("gui.de.invalid.txt"));
            GuiHelperOld.drawCenteredString(font, stabText, leftPos + imageWidth / 2, topPos + 18, stabColour, tile.stabilizersOK.get());
            if (tile.tier.get() >= 5) {
                GuiHelperOld.drawCenteredString(font, I18n.get("gui.de.advancedStabilizersRequired.txt"), leftPos + imageWidth / 2, topPos + 28, 0x777777, false);
            }

            int coreColour = tile.coreValid.get() ? 0x00FF00 : 0xFF0000;
            String coreText = I18n.get("gui.de.core.txt") + ": " + (tile.coreValid.get() ? I18n.get("gui.de.valid.txt") : I18n.get("gui.de.invalid.txt"));
            GuiHelperOld.drawCenteredString(font, coreText, leftPos + imageWidth / 2, topPos + 36, coreColour, tile.coreValid.get());
            if (!tile.coreValid.get()) {
//                GuiHelper.drawCenteredSplitString(font, tile.invalidMessage.get(), guiLeft + xSize / 2, guiTop + 46, 180, coreColour, tile.coreValid.get());
            }
        }
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);

        if (tile.active.get()) {
//            GuiHelper.drawEnergyBar(this, guiLeft + 5, guiTop + 82, 170, true, tile.getExtendedStorage(), tile.getExtendedCapacity(), true, mouseX, mouseY);

            if (GuiHelperOld.isInRect(leftPos + 40, topPos + 27, imageWidth - 80, 8, mouseX, mouseY)) {
                renderTooltip(mStack, new StringTextComponent(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedCapacity()) + " OP]"), mouseX, mouseY);
            }

            if (GuiHelperOld.isInRect(leftPos + 40, topPos + 48, imageWidth - 80, 8, mouseX, mouseY)) {
                renderTooltip(mStack, new StringTextComponent(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedStorage()) + " OP]"), mouseX, mouseY);
            }
        }

        if (tile.buildGuide.get()) {
            drawCenteredString(mStack, font, layer == -1 ? "All" : layer + "", leftPos + (imageWidth / 2), topPos - 10, 0xFFFFFF);
        }

        if (GuiHelper.isInRect(guiLeft(), guiTop() + 59, xSize(), 24, mouseX, mouseY) && tile.active.get()){
            IFormattableTextComponent input = new StringTextComponent("IN: ").withStyle(TextFormatting.GREEN).append(new StringTextComponent(Utils.formatNumber(Math.round(tile.inputRate.get())) + " OP/t").withStyle(TextFormatting.GRAY));
            IFormattableTextComponent out = new StringTextComponent("OUT: ").withStyle(TextFormatting.DARK_RED).append(new StringTextComponent(Utils.formatNumber(Math.round(tile.outputRate.get())) + " OP/t").withStyle(TextFormatting.GRAY));

            renderTooltip(mStack, Lists.newArrayList(input.getVisualOrderText(), out.getVisualOrderText()), mouseX, mouseY);
        } else {
            this.renderTooltip(mStack, mouseX, mouseY);
        }

    }

    @Override
    public void tick() {
        super.tick();
        updateButtonStates();

        if (tile.transferRate.get() != 0) {
            long space = tile.transferRate.get() > 0 ? tile.getExtendedCapacity() - tile.getExtendedStorage() : tile.getExtendedStorage();
            ticks = Math.abs(space / tile.transferRate.get());
        }
        else {
            ticks = 0;
        }
        seconds = ticks / 20L;
        minutes = seconds / 60L;
        hours = minutes / 60L;
        days = hours / 24L;
        years = days / 365L;
    }

    private void updateButtonStates() {
        if (tile.active.get()) {
            activate.setMessage(new TranslationTextComponent("button.de.deactivate.txt"));
        } else {
            activate.setMessage(new TranslationTextComponent("button.de.activate.txt"));
            toggleGuide.setMessage(new StringTextComponent(I18n.get("button.de.buildGuide.txt") + " " + (tile.buildGuide.get() ? I18n.get("gui.de.active.txt") : I18n.get("gui.de.inactive.txt"))));
            tierUp.active = tile.tier.get() < 8;
            tierDown.active = tile.tier.get() > 1;
        }


        tierUp.visible = tierDown.visible = toggleGuide.visible = !tile.active.get();
        assembleCore.visible = !tile.coreValid.get();
        activate.visible = tile.coreValid.get();

        layerPlus.visible = tile.buildGuide.get();
        layerMinus.visible = tile.buildGuide.get();
    }

    public static String formatYear(long value) {
        if (value < 1000L) return value + " Years";
        else if (value < 1000000L) return Math.round(value / 10D) / 100D + " Thousand Years";
        else if (value < 1000000000L) return Math.round(value / 10000D) / 100D + " Million Years";
        else if (value < 1000000000000L) return Math.round(value / 10000000D) / 100D + " Billion Years";
        else if (value < 1000000000000000L) return Math.round(value / 10000000000D) / 100D + " Trillion Years";
        else if (value < 1000000000000000000L) return Math.round(value / 10000000000000D) / 100D + " Quadrillion Years";
        else if (value <= Long.MAX_VALUE) return Math.round(value / 10000000000000000D) / 100D + " Quintillion Years";
        else return "Something is very broken!!!!";
    }

    protected void layer(int add) {
        layer = MathHelper.clip(layer + add, -1, 6);
    }
}
