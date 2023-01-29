package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.tileentities.gates.TileGate;

/**
 * Created by Brandon on 30/6/2015.
 */
public class GUIFlowGate extends GuiScreen {

    public TileGate tile;

    public GUIFlowGate(TileGate gate) {
        this.tile = gate;
    }

    @Override
    public void initGui() {
        int guiLeft = (width / 2) - (197 / 2);
        int guiTop = (height / 2) - (88 / 2);
        buttonList.clear();
        if (tile.flowOverridden) return;
        buttonList.add(new GuiTextureButton(0, guiLeft + 20, guiTop + 20, 0, 108, 18, 18, ""));
        buttonList.add(new GuiTextureButton(1, guiLeft + 159, guiTop + 20, 0, 54, 18, 18, ""));

        buttonList.add(new GuiTextureButton(2, guiLeft + 20, guiTop + 50, 0, 108, 18, 18, ""));
        buttonList.add(new GuiTextureButton(3, guiLeft + 159, guiTop + 50, 0, 54, 18, 18, ""));
    }

    @Override
    public void drawScreen(int x, int y, float pt) {
        int guiLeft = (width / 2) - (197 / 2);
        int guiCrt = (width / 2);
        int guiTop = (height / 2) - (88 / 2);

        drawDefaultBackground();
        ResourceHandler.bindResource("textures/gui/ToolConfig.png");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 197, 88);
        fontRendererObj.drawString(
                StatCollector.translateToLocal("gui.de.flowGate.name"),
                guiCrt - (fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.de.flowGate.name")) / 2),
                guiTop + 5,
                0x2c2c2c);

        if (!tile.flowOverridden) {
            String flowRSLow = tile.getFlowSetting(0);
            String flowRSHeigh = tile.getFlowSetting(1);
            fontRendererObj.drawString(
                    StatCollector.translateToLocal("gui.de.flowGateRSHigh.name"),
                    guiCrt - (fontRendererObj
                            .getStringWidth(StatCollector.translateToLocal("gui.de.flowGateRSHigh.name")) / 2),
                    guiTop + 20,
                    0xff0000);
            fontRendererObj.drawString(
                    flowRSHeigh,
                    guiCrt - (fontRendererObj.getStringWidth(flowRSHeigh) / 2),
                    guiTop + 31,
                    0x2c2c2c);
            fontRendererObj.drawString(
                    StatCollector.translateToLocal("gui.de.flowGateRSLow.name"),
                    guiCrt - (fontRendererObj
                            .getStringWidth(StatCollector.translateToLocal("gui.de.flowGateRSLow.name")) / 2),
                    guiTop + 50,
                    0x660000);
            fontRendererObj.drawString(
                    flowRSLow,
                    guiCrt - (fontRendererObj.getStringWidth(flowRSLow) / 2),
                    guiTop + 61,
                    0x2c2c2c);
        } else drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.flowGateOverridden.txt"),
                width / 2,
                height / 2 - 30,
                0x00FF00);

        String flow = StatCollector.translateToLocal("gui.de.flowGateCurrentFlow.name") + ": "
                + Utills.addCommas(tile.getActualFlow());
        fontRendererObj.drawString(flow, guiCrt - (fontRendererObj.getStringWidth(flow) / 2), guiTop + 76, 0x2c2c2c);

        if (!tile.flowOverridden) {
            fontRendererObj.drawSplitString(
                    StatCollector.translateToLocal("gui.de.flowGatePartialSignal.name"),
                    guiLeft + 5,
                    guiTop + 90,
                    197,
                    0xFFFFFF);
            fontRendererObj.drawSplitString(
                    StatCollector.translateToLocal("gui.de.ctrlAndShift.name"),
                    guiLeft + 200,
                    guiTop + 5,
                    100,
                    0xFFFFFF);

            ResourceHandler.bindResource("textures/gui/Widgets.png");
            super.drawScreen(x, y, pt);

            List<String> hoverText = new ArrayList<String>();
            boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
            boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

            if (GuiHelper.isInRect(guiLeft + 20, guiTop + 20, 18, 18, x, y)) {
                hoverText.add(
                        StatCollector.translateToLocal("gui.de.decrement.name") + " "
                                + tile.getToolTip(1, shift, ctrl));
            }
            if (GuiHelper.isInRect(guiLeft + 159, guiTop + 20, 18, 18, x, y)) {
                hoverText.add(
                        StatCollector.translateToLocal("gui.de.increment.name") + " "
                                + tile.getToolTip(1, shift, ctrl));
            }

            if (GuiHelper.isInRect(guiLeft + 20, guiTop + 50, 18, 18, x, y)) {
                hoverText.add(
                        StatCollector.translateToLocal("gui.de.decrement.name") + " "
                                + tile.getToolTip(0, shift, ctrl));
            }
            if (GuiHelper.isInRect(guiLeft + 159, guiTop + 50, 18, 18, x, y)) {
                hoverText.add(
                        StatCollector.translateToLocal("gui.de.increment.name") + " "
                                + tile.getToolTip(0, shift, ctrl));
            }

            if (!hoverText.isEmpty()) {
                hoverText.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("gui.de.pressOrScroll.name"));
                drawHoveringText(
                        hoverText,
                        x - (x < width / 2 ? fontRendererObj.getStringWidth(
                                StatCollector.translateToLocal("gui.de.decrement.name") + " "
                                        + tile.getToolTip(1, shift, ctrl))
                                + 25 : 0),
                        y,
                        fontRendererObj);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        int guiLeft = (width / 2) - (197 / 2);
        int guiTop = (height / 2) - (88 / 2);
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        if (GuiHelper.isInRect(guiLeft + 20, guiTop + 20, 18, 18, x, y)) {
            tile.incrementFlow(1, ctrl, shift, false, button);
        }
        if (GuiHelper.isInRect(guiLeft + 159, guiTop + 20, 18, 18, x, y)) {
            tile.incrementFlow(1, ctrl, shift, true, button);
        }

        if (GuiHelper.isInRect(guiLeft + 20, guiTop + 50, 18, 18, x, y)) {
            tile.incrementFlow(0, ctrl, shift, false, button);
        }
        if (GuiHelper.isInRect(guiLeft + 159, guiTop + 50, 18, 18, x, y)) {
            tile.incrementFlow(0, ctrl, shift, true, button);
        }

        super.mouseClicked(x, y, button);
    }

    @Override
    public void handleMouseInput() {
        int guiLeft = (width / 2) - (197 / 2);
        int guiTop = (height / 2) - (88 / 2);
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        int i = org.lwjgl.input.Mouse.getEventDWheel();

        if (i != 0 && GuiHelper.isInRect(guiLeft + 20, guiTop + 20, 18, 18, x, y)) {
            tile.incrementFlow(1, ctrl, shift, i < 0, 0);
        }
        if (i != 0 && GuiHelper.isInRect(guiLeft + 159, guiTop + 20, 18, 18, x, y)) {
            tile.incrementFlow(1, ctrl, shift, i > 0, 0);
        }

        if (i != 0 && GuiHelper.isInRect(guiLeft + 20, guiTop + 50, 18, 18, x, y)) {
            tile.incrementFlow(0, ctrl, shift, i < 0, 0);
        }
        if (i != 0 && GuiHelper.isInRect(guiLeft + 159, guiTop + 50, 18, 18, x, y)) {
            tile.incrementFlow(0, ctrl, shift, i > 0, 0);
        }

        super.handleMouseInput();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char p_73869_1_, int key) {
        if (key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }
}
