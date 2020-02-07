package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.ResourceHelperBC;
import com.brandon3055.brandonscore.client.gui.GuiButtonAHeight;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 7/4/2016.
 */
public class GuiEnergyCore extends GuiContainer {

    public EntityPlayer player;
    public TileEnergyStorageCore tile;
    private GuiButton activate;
    private GuiButton tierUp;
    private GuiButton tierDown;
    private GuiButton toggleGuide;
    private GuiButton assembleCore;
    private GuiButton layerPlus;
    private GuiButton layerMinus;
    public static int layer = -1;

    //Charge/Discharge time
    private long ticks;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;
    private long years;

    public GuiEnergyCore(EntityPlayer player, TileEnergyStorageCore tile) {
        super(new ContainerBCBase<TileEnergyStorageCore>(player, tile).addPlayerSlots(10, 116 + 10));
        this.tile = tile;
        this.xSize = 180;
        this.ySize = 210;
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(activate = new GuiButtonAHeight(0, guiLeft + 9, guiTop + 99 + 10, 162, 12, "Activate-L"));
        buttonList.add(tierUp = new GuiButtonAHeight(1, guiLeft + 91, guiTop + 86 + 10, 80, 12, I18n.format("button.de.tierUp.txt")));
        buttonList.add(tierDown = new GuiButtonAHeight(2, guiLeft + 9, guiTop + 86 + 10, 80, 12, I18n.format("button.de.tierDown.txt")));
        buttonList.add(toggleGuide = new GuiButtonAHeight(3, guiLeft + 9, guiTop + 73 + 10, 162, 12, I18n.format("button.de.buildGuide.txt")));
        buttonList.add(assembleCore = new GuiButtonAHeight(4, guiLeft + 9, guiTop + 99 + 10, 162, 12, I18n.format("button.de.assembleCore.txt")));

        buttonList.add(layerMinus = new GuiButtonAHeight(5, guiLeft + 5, guiTop - 13, 70, 12, "Layer-"));
        buttonList.add(layerPlus = new GuiButtonAHeight(6, guiLeft + 105, guiTop - 13, 70, 12, "Layer+"));
        layerPlus.visible = tile.buildGuide.value;
        layerMinus.visible = tile.buildGuide.value;

        updateButtonStates();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115 + 10, true);
        drawCenteredString(fontRenderer, I18n.format("gui.de.energyStorageCore.name", tile.tier.toString()), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);

        if (tile.active.value) {
            GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.capacity.txt"), guiLeft + xSize / 2, guiTop + 16, 0xFFAA00, true);
            String capText = tile.tier.value == 8 ? I18n.format("gui.de.almostInfinite.txt") : Utils.formatNumber(tile.getExtendedCapacity());
            GuiHelper.drawCenteredString(fontRenderer, capText, guiLeft + xSize / 2, guiTop + 27, 0x555555, false);

            DecimalFormat energyValue = new DecimalFormat("###.###");
            double percent = (double) tile.getExtendedStorage() / (double) tile.getExtendedCapacity() * 100D;
            GuiHelper.drawCenteredString(fontRenderer, I18n.format("info.bc.charge.txt"), guiLeft + xSize / 2, guiTop + 38, 0xFFAA00, true);
            GuiHelper.drawCenteredString(fontRenderer, Utils.formatNumber(tile.getExtendedStorage()) + "RF [" + energyValue.format(percent) + "%]", guiLeft + xSize / 2, guiTop + 49, 0x555555, false);

            int coreColour = tile.transferRate.value > 0 ? 0x00FF00 : tile.transferRate.value < 0 ? 0xFF0000 : 0x222222;
            String transfer = (tile.transferRate.value > 0 ? "+" : tile.transferRate.value < 0 ? "-" : "") + Utils.formatNumber(Math.abs(tile.transferRate.value)) + " RF/t";
            GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.transfer" + (tile.transferRate.value > 0 ? ".charge" : tile.transferRate.value < 0 ? ".discharge" : "") + ".txt"), guiLeft + xSize / 2, guiTop + 59, 0xFFAA00, true);
            GuiHelper.drawCenteredString(fontRenderer, transfer, guiLeft + xSize / 2, guiTop + 70, coreColour, tile.transferRate.value > 0);

            if (tile.transferRate.value != 0) {
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

                GuiHelper.drawCenteredString(fontRenderer, time, guiLeft + xSize / 2, guiTop + 70 + 10, 0x555555, false);
            }
        } else {
            int stabColour = tile.stabilizersOK.value ? 0x00FF00 : 0xFF0000;
            String stabText = I18n.format("gui.de.stabilizers.txt") + ": " + (tile.stabilizersOK.value ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
            GuiHelper.drawCenteredString(fontRenderer, stabText, guiLeft + xSize / 2, guiTop + 18, stabColour, tile.stabilizersOK.value);
            if (tile.tier.value >= 5) {
                GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.de.advancedStabilizersRequired.txt"), guiLeft + xSize / 2, guiTop + 28, 0x777777, false);
            }

            int coreColour = tile.coreValid.value ? 0x00FF00 : 0xFF0000;
            String coreText = I18n.format("gui.de.core.txt") + ": " + (tile.coreValid.value ? I18n.format("gui.de.valid.txt") : I18n.format("gui.de.invalid.txt"));
            GuiHelper.drawCenteredString(fontRenderer, coreText, guiLeft + xSize / 2, guiTop + 36, coreColour, tile.coreValid.value);
            if (!tile.coreValid.value) {
                GuiHelper.drawCenteredSplitString(fontRenderer, tile.invalidMessage.value, guiLeft + xSize / 2, guiTop + 46, 180, coreColour, tile.coreValid.value);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (tile.active.value) {
            drawEnergyBar(this, guiLeft + 5, guiTop + 82 + 10, 170, true, tile.getExtendedStorage(), tile.getExtendedCapacity(), true, mouseX, mouseY);

            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 27, xSize - 80, 8, mouseX, mouseY)) {
                List<String> list = new ArrayList<String>();
                list.add(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedCapacity()) + " RF]");
                drawHoveringText(list, mouseX, mouseY);
            }

            if (GuiHelper.isInRect(guiLeft + 40, guiTop + 48, xSize - 80, 8, mouseX, mouseY)) {
                List<String> list = new ArrayList<String>();
                list.add(TextFormatting.GRAY + "[" + Utils.addCommas(tile.getExtendedStorage()) + " RF]");
                drawHoveringText(list, mouseX, mouseY);
            }

            if (tile.transferRate.value != 0 && GuiHelper.isInRect(guiLeft + 40, guiTop + 70 + 10, xSize - 80, 8, mouseX, mouseY)) {
                List<String> list = new ArrayList<String>();
                list.add(TextFormatting.AQUA + "ETA");
                if (years > 0) {
                    list.add(TextFormatting.GOLD + formatYear(years));
                }
                if (days > 0) {
                    list.add(TextFormatting.GOLD + "" + (days % 365) + " Days");
                }

                String time = (hours % 24 < 10 ? "0" : "") + hours % 24 + ":";
                time += (minutes % 60 < 10 ? "0" : "") + minutes % 60 + ":";
                time += (seconds % 60 < 10 ? "0" : "") + seconds % 60 + "." + (ticks % 20 < 10 ? "0" : "") + ticks % 20;
                list.add(TextFormatting.GOLD + "" + time);
                drawHoveringText(list, mouseX, mouseY);
//                LogHelper.dev(mouseX+" " + mouseY);
            }
        }

        if (tile.buildGuide.value) {
            drawCenteredString(fontRenderer, layer == -1 ? "All" : layer + "", guiLeft + (xSize / 2), guiTop - 10, 0xFFFFFF);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtonStates();

        if (tile.transferRate.value != 0) {
            long space = tile.transferRate.value > 0 ? tile.getExtendedCapacity() - tile.getExtendedStorage() : tile.getExtendedStorage();
            ticks = Math.abs(space / tile.transferRate.value);
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
        if (tile.active.value) {
            activate.displayString = I18n.format("button.de.deactivate.txt");
        } else {
            activate.displayString = I18n.format("button.de.activate.txt");
            toggleGuide.displayString = I18n.format("button.de.buildGuide.txt") + " " + (tile.buildGuide.value ? I18n.format("gui.de.active.txt") : I18n.format("gui.de.inactive.txt"));
            tierUp.enabled = tile.tier.value < 8;
            tierDown.enabled = tile.tier.value > 1;
        }

        tierUp.visible = tierDown.visible = toggleGuide.visible = !tile.active.value;
        assembleCore.visible = !tile.coreValid.value;
        activate.visible = tile.coreValid.value;

        layerPlus.visible = tile.buildGuide.value;
        layerMinus.visible = tile.buildGuide.value;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id < 5) {
            tile.sendPacketToServer(output -> {
            }, button.id);
        } else {
            if (button == layerPlus) {
                layer++;
            } else {
                layer--;
            }
            layer = MathHelper.clip(layer, -1, 6);
        }
    }

    public static void drawEnergyBar(Gui gui, int posX, int posY, int size, boolean horizontal, long energy, long maxEnergy, boolean toolTip, int mouseX, int mouseY) {
        ResourceHelperBC.bindTexture("textures/gui/energy_gui.png");
        int draw = (int) ((double) energy / (double) maxEnergy * (size - 2));

        boolean inRect = GuiHelper.isInRect(posX, posY, size, 14, mouseX, mouseY);

        if (horizontal) {
            int x = posY;
            posY = posX;
            posX = x;
            GlStateManager.pushMatrix();
            GlStateManager.translate(size + (posY * 2), 0, 0);
            GlStateManager.rotate(90, 0, 0, 1);
        }

        GlStateManager.color(1F, 1F, 1F);
        gui.drawTexturedModalRect(posX, posY, 0, 0, 14, size);
        gui.drawTexturedModalRect(posX, posY + size - 1, 0, 255, 14, 1);
        gui.drawTexturedModalRect(posX + 1, posY + size - draw - 1, 14, size - draw, 12, draw);

        if (horizontal) {
            GlStateManager.popMatrix();
        }

        if (toolTip && inRect) {
            List<String> list = new ArrayList<String>();
            list.add(InfoHelper.ITC() + net.minecraft.util.text.translation.I18n.translateToLocal("gui.de.energyStorage.txt"));
            list.add(InfoHelper.HITC() + Utils.formatNumber(energy) + " / " + Utils.formatNumber(maxEnergy));
            list.add(TextFormatting.GRAY + "[" + Utils.addCommas(energy) + " RF]");
            GuiHelper.drawHoveringText(list, mouseX, mouseY, Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }
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
}
