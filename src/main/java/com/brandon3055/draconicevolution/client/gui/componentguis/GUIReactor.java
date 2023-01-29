package com.brandon3055.draconicevolution.client.gui.componentguis;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.client.gui.guicomponents.*;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.ContainerReactor;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;

/**
 * Created by brandon3055 on 30/7/2015.
 */
public class GUIReactor extends GUIBase {

    private TileReactorCore reactor;
    private ContainerReactor container;
    private static boolean showStats = false;

    public GUIReactor(EntityPlayer player, TileReactorCore reactor, ContainerReactor container) {
        super(container, 248, 222);
        this.reactor = reactor;
        this.container = container;
    }

    @Override
    protected ComponentCollection assembleComponents() {
        collection = new ComponentCollection(0, 0, 248, 222, this);
        collection.addComponent(
                new ComponentTexturedRect(0, 0, xSize, ySize, ResourceHandler.getResource("textures/gui/Reactor.png")));
        collection.addComponent(
                new ComponentTextureButton(
                        14,
                        190,
                        18,
                        162,
                        18,
                        18,
                        0,
                        this,
                        "",
                        StatCollector.translateToLocal("button.de.reactorCharge.txt"),
                        ResourceHandler.getResource("textures/gui/Widgets.png")))
                .setName("CHARGE");
        collection.addComponent(
                new ComponentTextureButton(
                        14,
                        190,
                        18,
                        54,
                        18,
                        18,
                        1,
                        this,
                        "",
                        StatCollector.translateToLocal("button.de.reactorStart.txt"),
                        ResourceHandler.getResource("textures/gui/Widgets.png")))
                .setName("ACTIVATE");
        collection.addComponent(
                new ComponentTextureButton(
                        216,
                        190,
                        18,
                        108,
                        18,
                        18,
                        2,
                        this,
                        "",
                        StatCollector.translateToLocal("button.de.reactorStop.txt"),
                        ResourceHandler.getResource("textures/gui/Widgets.png")))
                .setName("DEACTIVATE");
        collection.addComponent(
                new ComponentButton(
                        9,
                        120,
                        43,
                        15,
                        3,
                        this,
                        StatCollector.translateToLocal("button.de.stats.txt"),
                        StatCollector.translateToLocal("button.de.statsShow.txt")))
                .setName("STATS");
        return collection;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        // Draw I/O Slots
        if (reactor.reactorState == TileReactorCore.STATE_OFFLINE) {
            RenderHelper.enableGUIStandardItemLighting();
            drawTexturedModalRect(guiLeft + 14, guiTop + 139, 14, ySize, 18, 18);
            drawTexturedModalRect(guiLeft + 216, guiTop + 139, 32, ySize, 18, 18);

            fontRendererObj
                    .drawString(StatCollector.translateToLocal("gui.de.insert.txt"), guiLeft + 8, guiTop + 159, 0);
            fontRendererObj
                    .drawString(StatCollector.translateToLocal("gui.de.fuel.txt"), guiLeft + 13, guiTop + 168, 0);

            fontRendererObj
                    .drawString(StatCollector.translateToLocal("gui.de.extract.txt"), guiLeft + 206, guiTop + 159, 0);
            fontRendererObj
                    .drawString(StatCollector.translateToLocal("gui.de.fuel.txt"), guiLeft + 215, guiTop + 168, 0);
        }
        drawCenteredString(fontRendererObj, "Draconic Reactor", guiLeft + xSize / 2, guiTop + 4, 0x00FFFF);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        ResourceHandler.bindResource("textures/gui/Reactor.png");

        GL11.glColor4f(1f, 1f, 1f, 1f);
        // Draw Indicators
        double value = Math.min(reactor.reactionTemperature, reactor.maxReactTemperature) / reactor.maxReactTemperature;
        int pixOffset = (int) (value * 108);
        drawTexturedModalRect(11, 112 - pixOffset, 0, 222, 14, 5);

        value = reactor.fieldCharge / reactor.maxFieldCharge;
        pixOffset = (int) (value * 108);
        drawTexturedModalRect(35, 112 - pixOffset, 0, 222, 14, 5);

        value = (double) reactor.energySaturation / (double) reactor.maxEnergySaturation;
        pixOffset = (int) (value * 108);
        drawTexturedModalRect(199, 112 - pixOffset, 0, 222, 14, 5);

        value = ((double) reactor.convertedFuel) / ((double) reactor.reactorFuel + (double) reactor.convertedFuel);
        pixOffset = (int) (value * 108);
        drawTexturedModalRect(223, 112 - pixOffset, 0, 222, 14, 5);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        if (!showStats) {

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glTranslated(124, 71, 100);
            double scale = 100 / (reactor.getCoreDiameter());
            GL11.glScaled(scale, scale, scale);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_CULL_FACE);

            TileEntityRendererDispatcher.instance.renderTileEntityAt(reactor, -0.5D, -0.5D, -0.5D, 0.0F);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        } else {
            ResourceHandler.bindResource("textures/gui/Reactor.png");
            for (int i = 1; i <= 10; i++) drawTexturedModalRect(63, i * 12, 0, 240, 122, 16);
            drawStats();
        }

        String status = StatCollector.translateToLocal("gui.de.status.txt") + ": "
                + (reactor.reactorState == 0 ? EnumChatFormatting.DARK_GRAY
                        : reactor.reactorState == 1 ? EnumChatFormatting.RED
                                : reactor.reactorState == 2 ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.RED)
                + StatCollector.translateToLocal("gui.de.status" + reactor.reactorState + ".txt");
        if (reactor.reactorState == 1 && reactor.canStart())
            status = StatCollector.translateToLocal("gui.de.status.txt") + ": "
                    + EnumChatFormatting.DARK_GREEN
                    + StatCollector.translateToLocal("gui.de.status1_5.txt");
        if (!showStats) fontRendererObj.drawString(status, xSize - 5 - fontRendererObj.getStringWidth(status), 125, 0);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        List<String> text = new ArrayList<String>();
        if (GuiHelper.isInRect(9, 4, 18, 114, mouseX - guiLeft, mouseY - guiTop)) {
            text.add(StatCollector.translateToLocal("gui.de.reactionTemp.txt"));
            text.add((int) reactor.reactionTemperature + "C");
            drawHoveringText(text, mouseX, mouseY, fontRendererObj);
        } else if (GuiHelper.isInRect(33, 4, 18, 114, mouseX - guiLeft, mouseY - guiTop)) {
            text.add(StatCollector.translateToLocal("gui.de.fieldStrength.txt"));
            if (reactor.maxFieldCharge > 0)
                text.add(Utills.round(reactor.fieldCharge / reactor.maxFieldCharge * 100D, 100D) + "%");
            text.add(
                    Utills.addCommas((int) reactor.fieldCharge) + " / "
                            + Utills.addCommas((int) reactor.maxFieldCharge)); // todo refine or remove
            drawHoveringText(text, mouseX, mouseY, fontRendererObj);
        } else if (GuiHelper.isInRect(197, 4, 18, 114, mouseX - guiLeft, mouseY - guiTop)) {
            text.add(StatCollector.translateToLocal("gui.de.energySaturation.txt"));
            if (reactor.maxEnergySaturation > 0) text.add(
                    Utills.round((double) reactor.energySaturation / (double) reactor.maxEnergySaturation * 100D, 100D)
                            + "%");
            text.add(
                    Utills.addCommas(reactor.energySaturation) + " / " + Utills.addCommas(reactor.maxEnergySaturation)); // todo
                                                                                                                         // refine
                                                                                                                         // or
                                                                                                                         // remove
            drawHoveringText(text, mouseX, mouseY, fontRendererObj);
        } else if (GuiHelper.isInRect(221, 4, 18, 114, mouseX - guiLeft, mouseY - guiTop)) {
            text.add(StatCollector.translateToLocal("gui.de.fuelConversion.txt"));
            if (reactor.reactorFuel + reactor.convertedFuel > 0) text.add(
                    Utills.round(
                            ((double) reactor.convertedFuel + reactor.conversionUnit)
                                    / ((double) reactor.convertedFuel + (double) reactor.reactorFuel)
                                    * 100D,
                            100D) + "%");
            text.add(reactor.convertedFuel + " / " + (reactor.convertedFuel + reactor.reactorFuel)); // todo refine or
                                                                                                     // remove
            drawHoveringText(text, mouseX, mouseY, fontRendererObj);
        }

        if (showStats) {
            if (GuiHelper.isInRect(53, 15, 140, 18, mouseX - guiLeft, mouseY - guiTop)) {
                text.addAll(
                        fontRendererObj.listFormattedStringToWidth(
                                StatCollector.translateToLocal("gui.de.reacTempLoadFactor.txt"),
                                200));
                drawHoveringText(text, mouseX, mouseY, fontRendererObj);
            } else if (GuiHelper.isInRect(53, 40, 140, 18, mouseX - guiLeft, mouseY - guiTop)) {
                text.addAll(
                        fontRendererObj.listFormattedStringToWidth(
                                StatCollector.translateToLocal("gui.de.reacCoreMass.txt"),
                                200));
                drawHoveringText(text, mouseX, mouseY, fontRendererObj);
            } else if (GuiHelper.isInRect(53, 65, 140, 18, mouseX - guiLeft, mouseY - guiTop)) {
                text.addAll(
                        fontRendererObj.listFormattedStringToWidth(
                                StatCollector.translateToLocal("gui.de.reacGenRate.txt"),
                                200));
                drawHoveringText(text, mouseX, mouseY, fontRendererObj);
            } else if (GuiHelper.isInRect(53, 88, 140, 18, mouseX - guiLeft, mouseY - guiTop)) {
                text.addAll(
                        fontRendererObj.listFormattedStringToWidth(
                                StatCollector.translateToLocal("gui.de.reacInputRate.txt"),
                                200));
                drawHoveringText(text, mouseX, mouseY, fontRendererObj);
            } else if (GuiHelper.isInRect(53, 113, 140, 18, mouseX - guiLeft, mouseY - guiTop)) {
                text.addAll(
                        fontRendererObj.listFormattedStringToWidth(
                                StatCollector.translateToLocal("gui.de.reacConversionRate.txt"),
                                200));
                drawHoveringText(text, mouseX, mouseY, fontRendererObj);
            }
        }
    }

    private void drawStats() {

        double inputRate = reactor.fieldDrain / (1D - (reactor.fieldCharge / reactor.maxFieldCharge));
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.tempLoad.name"), 55, 16, 0x0000FF);
        fontRendererObj.drawString(Utills.round(reactor.tempDrainFactor * 100D, 1D) + "%", 60, 2 + 24, 0);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.mass.name"), 55, 16 + 24, 0x0000FF);
        fontRendererObj.drawString(
                Utills.round((reactor.reactorFuel + reactor.convertedFuel) / 1296D, 100) + "m^3",
                60,
                2 + 2 * 24,
                0);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.genRate.name"), 55, 16 + 2 * 24, 0x0000FF);
        fontRendererObj.drawString(Utills.addCommas((int) reactor.generationRate) + "RF/t", 60, 2 + 3 * 24, 0);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.de.fieldInputRate.name"), 55, 16 + 3 * 24, 0x0000FF);
        fontRendererObj
                .drawString(Utills.addCommas((int) Math.min(inputRate, Integer.MAX_VALUE)) + "RF/t", 60, 2 + 4 * 24, 0);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.de.fuelConversion.name"), 55, 16 + 4 * 24, 0x0000FF);
        fontRendererObj.drawString(
                Utills.addCommas((int) Math.round(reactor.fuelUseRate * 1000000D)) + "nb/t",
                60,
                2 + 5 * 24,
                0);
    }

    @Override
    public void updateScreen() {

        if (reactor.reactorState == TileReactorCore.STATE_INVALID
                || reactor.reactorState == TileReactorCore.STATE_OFFLINE
                || reactor.reactorState == TileReactorCore.STATE_STOP)
            collection.getComponent("DEACTIVATE").setEnabled(false);
        else collection.getComponent("DEACTIVATE").setEnabled(true);
        if ((reactor.reactorState == TileReactorCore.STATE_OFFLINE
                || (reactor.reactorState == TileReactorCore.STATE_STOP && !reactor.canStart())) && reactor.canCharge())
            collection.getComponent("CHARGE").setEnabled(true);
        else collection.getComponent("CHARGE").setEnabled(false);
        if ((reactor.reactorState == TileReactorCore.STATE_START || reactor.reactorState == TileReactorCore.STATE_STOP)
                && reactor.canStart())
            collection.getComponent("ACTIVATE").setEnabled(true);
        else collection.getComponent("ACTIVATE").setEnabled(false);
        super.updateScreen();
    }

    @Override
    public void buttonClicked(int id, int button) {
        super.buttonClicked(id, button);
        if (id < 3) container.sendObjectToServer(null, 20, id);
        else if (id == 3) {
            showStats = !showStats;
            ((ComponentButton) collection.getComponent("STATS")).hoverText = showStats
                    ? StatCollector.translateToLocal("button.de.statsHide.txt")
                    : StatCollector.translateToLocal("button.de.statsShow.txt");
        }
    }
}
