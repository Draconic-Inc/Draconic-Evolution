package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.brandon3055.brandonscore.client.gui.guicomponents.GUIBase;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;

/**
 * Created by brandon3055 on 11/2/2016.
 */
public class GuiHudConfig extends GuiScreen {

    private GUIBase parent;
    private GuiButton buttonHudFade;
    private GuiButton buttonArmorFade;
    private GuiButton buttonArmorNumeric;
    private boolean draggingArmor = false;
    private boolean draggingHud = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public GuiHudConfig(GUIBase parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        int x = width / 2;
        int y = height / 2 - 30;

        buttonList.add(new GuiButtonAHeight(0, x - 35, y + 95, 70, 15, StatCollector.translateToLocal("gui.back")));

        buttonList.add(
                new GuiButtonAHeight(
                        1,
                        x + 2,
                        y - 37,
                        80,
                        15,
                        StatCollector.translateToLocal("button.de.scaleUp.txt")));
        buttonList.add(
                new GuiButtonAHeight(
                        2,
                        x - 81,
                        y - 37,
                        80,
                        15,
                        StatCollector.translateToLocal("button.de.scaleDown.txt")));
        buttonList.add(buttonHudFade = new GuiButtonAHeight(3, x - 81, y - 20, 163, 15, ""));
        buttonList.add(
                new GuiButtonAHeight(
                        9,
                        x - 81,
                        y - 3,
                        163,
                        15,
                        StatCollector.translateToLocal("button.de.toggleHidden.txt")));

        buttonList.add(
                new GuiButtonAHeight(
                        4,
                        x + 2,
                        y + 25,
                        80,
                        15,
                        StatCollector.translateToLocal("button.de.scaleUp.txt")));
        buttonList.add(
                new GuiButtonAHeight(
                        5,
                        x - 81,
                        y + 25,
                        80,
                        15,
                        StatCollector.translateToLocal("button.de.scaleDown.txt")));
        buttonList.add(buttonArmorFade = new GuiButtonAHeight(6, x - 81, y + 42, 163, 15, ""));
        buttonList.add(buttonArmorNumeric = new GuiButtonAHeight(7, x + 2, y + 59, 80, 15, ""));
        buttonList.add(
                new GuiButtonAHeight(
                        8,
                        x - 81,
                        y + 59,
                        80,
                        15,
                        StatCollector.translateToLocal("button.de.rotate.txt")));
        buttonList.add(
                new GuiButtonAHeight(
                        10,
                        x - 81,
                        y + 76,
                        163,
                        15,
                        StatCollector.translateToLocal("button.de.toggleHidden.txt")));
    }

    @Override
    public void updateScreen() {
        buttonHudFade.displayString = StatCollector
                .translateToLocal("button.de.fadeMode" + ConfigHandler.hudSettings[6] + ".txt");
        buttonArmorFade.displayString = StatCollector
                .translateToLocal("button.de.fadeMode" + ConfigHandler.hudSettings[7] + ".txt");
        buttonArmorNumeric.displayString = StatCollector.translateToLocal("button.de.numeric.txt") + " "
                + (ConfigHandler.hudSettings[9] == 0 ? StatCollector.translateToLocal("gui.de.off.txt")
                        : StatCollector.translateToLocal("gui.de.on.txt"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
            return;
        } else if (button.id == 1 && ConfigHandler.hudSettings[4] < 300) {
            ConfigHandler.hudSettings[4] += 10;
        } else if (button.id == 2 && ConfigHandler.hudSettings[4] > 30) {
            ConfigHandler.hudSettings[4] -= 10;
        } else if (button.id == 3) {
            if (ConfigHandler.hudSettings[6] < 4) ConfigHandler.hudSettings[6]++;
            else ConfigHandler.hudSettings[6] = 0;
        } else if (button.id == 4 && ConfigHandler.hudSettings[5] < 300) {
            ConfigHandler.hudSettings[5] += 10;
        } else if (button.id == 5 && ConfigHandler.hudSettings[5] > 30) {
            ConfigHandler.hudSettings[5] -= 10;
        } else if (button.id == 6) {
            if (ConfigHandler.hudSettings[7] < 4) ConfigHandler.hudSettings[7]++;
            else ConfigHandler.hudSettings[7] = 0;
        } else if (button.id == 7) {
            if (ConfigHandler.hudSettings[9] == 0) ConfigHandler.hudSettings[9] = 1;
            else ConfigHandler.hudSettings[9] = 0;
        } else if (button.id == 8) {
            if (ConfigHandler.hudSettings[8] == 0) ConfigHandler.hudSettings[8] = 1;
            else ConfigHandler.hudSettings[8] = 0;
        } else if (button.id == 9) {
            if (ConfigHandler.hudSettings[10] == 0) ConfigHandler.hudSettings[10] = 1;
            else ConfigHandler.hudSettings[10] = 0;
        } else if (button.id == 10) {
            if (ConfigHandler.hudSettings[11] == 0) ConfigHandler.hudSettings[11] = 1;
            else ConfigHandler.hudSettings[11] = 0;
        }

        ConfigHandler.config.get(
                "Gui Stuff",
                "HUD Settings",
                new int[] { 7, 874, 100, 100, 100, 100, 0, 0, 0, 0, 1, 1 },
                "Used to store the position of the armor ant tool HUD's. This should not be modified",
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                true,
                12).set(ConfigHandler.hudSettings);
        ConfigHandler.config.save();
    }

    @Override
    public void drawScreen(int x, int y, float partial) {
        int[] pos = ConfigHandler.hudSettings;
        int hudX = (int) ((pos[0] / 1000D) * width);
        int hudY = (int) ((pos[1] / 1000D) * height);
        int armorX = (int) ((pos[2] / 1000D) * width);
        int armorY = (int) ((pos[3] / 1000D) * height);

        drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.configureGuiElements.txt"),
                width / 2,
                height / 2 - 90,
                0x00FFFF);
        drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.hudDisplaySettings.txt"),
                width / 2,
                height / 2 - 77,
                0xFFFFFF);
        drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.shieldDisplaySettings.txt"),
                width / 2,
                height / 2 - 15,
                0xFFFFFF);
        drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.clickAndDragPurpleBoxes.txt"),
                width / 2,
                height / 2 + 85,
                0xFFFFFF);

        drawCenteredString(
                fontRendererObj,
                ConfigHandler.hudSettings[4] + "%",
                width / 2 + 97,
                height / 2 - 63,
                0xFFFFFF);
        drawCenteredString(
                fontRendererObj,
                ConfigHandler.hudSettings[5] + "%",
                width / 2 + 97,
                height / 2 - 1,
                0xFFFFFF);
        super.drawScreen(x, y, partial);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX + 20, hudY + 20, 0x20FF00FF, 0x20FF00FF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX - 4, hudY, hudX + 5, hudY + 1, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX, hudY - 4, hudX + 1, hudY + 5, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX + 20, hudY - 18, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX - 19, hudY + 19, hudX + 20, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX - 18, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(hudX + 19, hudY - 19, hudX + 20, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);

        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX + 20, armorY + 20, 0x20FF00FF, 0x20FF00FF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX - 4, armorY, armorX + 5, armorY + 1, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX, armorY - 4, armorX + 1, armorY + 5, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX + 20, armorY - 18, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX - 19, armorY + 19, armorX + 20, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX - 18, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
        GuiHelper.drawGradientRect(armorX + 19, armorY - 19, armorX + 20, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (GuiHelper.isInRect(armorX - 19, armorY - 19, 39, 39, x, y)
                || GuiHelper.isInRect(hudX - 19, hudY - 19, 39, 39, x, y)) {
            drawHoveringText(new ArrayList<String>() {

                {
                    add(StatCollector.translateToLocal("info.de.hudDisplayConfigTxt2.txt"));
                }
            }, x, y, fontRendererObj);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        int[] pos = ConfigHandler.hudSettings;
        int hudX = (int) ((pos[0] / 1000D) * width);
        int hudY = (int) ((pos[1] / 1000D) * height);
        int armorX = (int) ((pos[2] / 1000D) * width);
        int armorY = (int) ((pos[3] / 1000D) * height);

        if (GuiHelper.isInRect(hudX - 19, hudY - 19, 39, 39, x, y)) {
            draggingHud = true;
            dragOffsetX = hudX - x;
            dragOffsetY = hudY - y;
            return;
        } else if (GuiHelper.isInRect(armorX - 19, armorY - 19, 39, 39, x, y)) {
            draggingArmor = true;
            dragOffsetX = armorX - x;
            dragOffsetY = armorY - y;
            return;
        }

        super.mouseClicked(x, y, button);
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int action) {
        super.mouseMovedOrUp(x, y, action);
        draggingHud = draggingArmor = false;
        ConfigHandler.config.get(
                "Gui Stuff",
                "HUD Settings",
                new int[] { 7, 874, 100, 100, 100, 100, 0, 0, 0, 0, 1, 1 },
                "Used to store the position of the armor ant tool HUD's. This should not be modified",
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                true,
                12).set(ConfigHandler.hudSettings);
        ConfigHandler.config.save();
    }

    @Override
    protected void mouseClickMove(int x, int y, int action, long time) {
        super.mouseClickMove(x, y, action, time);
        int x1 = (int) (((float) (x + dragOffsetX) / (float) width) * 1000f);
        int y1 = (int) (((float) (y + dragOffsetY) / (float) height) * 1000f);

        if (draggingHud) {
            ConfigHandler.hudSettings[0] = x1;
            ConfigHandler.hudSettings[1] = y1;
        } else if (draggingArmor) {
            ConfigHandler.hudSettings[2] = x1;
            ConfigHandler.hudSettings[3] = y1;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char keyChar, int keyInt) {
        if (keyInt == 1) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
            return;
        }
    }
}
