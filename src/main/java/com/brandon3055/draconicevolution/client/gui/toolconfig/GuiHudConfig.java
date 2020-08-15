//package com.brandon3055.draconicevolution.client.gui.toolconfig;
//
//import com.brandon3055.brandonscore.client.gui.ButtonColourRect;
//import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
//import com.brandon3055.brandonscore.client.utils.GuiHelper;
//
//import com.brandon3055.draconicevolution.DEConfig;
//import com.brandon3055.draconicevolution.DEOldConfig;
//import com.brandon3055.draconicevolution.DraconicEvolution;
//import com.brandon3055.draconicevolution.client.handler.HudHandler;
//import com.brandon3055.draconicevolution.utils.LogHelper;
//import net.minecraft.client.Minecraft;
//
//import net.minecraft.client.gui.screen.Screen;
//
//
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
//
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by brandon3055 on 11/2/2016.
// */
//public class GuiHudConfig extends Screen {
//    private GuiButton buttonHudFade;
//    private GuiButton buttonArmorFade;
//    private GuiButton buttonArmorNumeric;
//    private boolean draggingArmor = false;
//    private boolean draggingHud = false;
//    private int dragOffsetX = 0;
//    private int dragOffsetY = 0;
//
//    public GuiHudConfig(ITextComponent titleIn) {
//        super(titleIn);
//    }
//
//
//    @Override
//    protected void init() {
//        super.init();
//
////        button.clear();
//
//        int x = width / 2;
//        int y = height / 2 - 30;
////        buttonList.add(new GuiButtonAHeight(0, x - 35, y + 95, 70, 15, I18n.format("gui.back")));
////
////        buttonList.add(new GuiButtonAHeight(1, x + 2, y - 37, 80, 15, I18n.format("button.de.scaleUp.txt")));
////        buttonList.add(new GuiButtonAHeight(2, x - 81, y - 37, 80, 15, I18n.format("button.de.scaleDown.txt")));
////        buttonList.add(buttonHudFade = new GuiButtonAHeight(3, x - 81, y - 20, 163, 15, ""));
////        buttonList.add(new GuiButtonAHeight(9, x - 81, y - 3, 163, 15, I18n.format("button.de.toggleHidden.txt")));
////
////        buttonList.add(new GuiButtonAHeight(4, x + 2, y + 25, 80, 15, I18n.format("button.de.scaleUp.txt")));
////        buttonList.add(new GuiButtonAHeight(5, x - 81, y + 25, 80, 15, I18n.format("button.de.scaleDown.txt")));
////        buttonList.add(buttonArmorFade = new GuiButtonAHeight(6, x - 81, y + 42, 163, 15, ""));
////        buttonList.add(buttonArmorNumeric = new GuiButtonAHeight(7, x + 2, y + 59, 80, 15, ""));
////        buttonList.add(new GuiButtonAHeight(8, x - 81, y + 59, 80, 15, I18n.format("button.de.rotate.txt")));
////        buttonList.add(new GuiButtonAHeight(10, x - 81, y + 76, 163, 15, I18n.format("button.de.toggleHidden.txt")));
//        int c1 = 0x88000000;
////        int c2 = 0xFF880000;
//        int c2 = 0xFF440066;
//        int c3 = 0xFF009900;
//
//        addButton(new ButtonColourRect(0, I18n.format("gui.back"), x - 35, y + 95, 70, 15, c1, c2, c3));
//
//        addButton(new ButtonColourRect(1, I18n.format("button.de.scaleUp.txt"), x + 2, y - 37, 80, 15, c1, c2, c3));
//        addButton(new ButtonColourRect(2, I18n.format("button.de.scaleDown.txt"), x - 81, y - 37, 80, 15, c1, c2, c3));
//        addButton(buttonHudFade = new ButtonColourRect(3, "", x - 81, y - 20, 163, 15, c1, c2, c3));
//        addButton(new ButtonColourRect(9, I18n.format("button.de.toggleHidden.txt"), x - 81, y - 3, 163, 15, c1, c2, c3));
//
//        addButton(new ButtonColourRect(4, I18n.format("button.de.scaleUp.txt"), x + 2, y + 25, 80, 15, c1, c2, c3));
//        addButton(new ButtonColourRect(5, I18n.format("button.de.scaleDown.txt"), x - 81, y + 25, 80, 15, c1, c2, c3));
//        addButton(buttonArmorFade = new ButtonColourRect(6, "", x - 81, y + 42, 163, 15, c1, c2, c3));
//        addButton(buttonArmorNumeric = new ButtonColourRect(7, "", x + 2, y + 59, 80, 15, c1, c2, c3));
//        addButton(new ButtonColourRect(8, I18n.format("button.de.rotate.txt"), x - 81, y + 59, 80, 15, c1, c2, c3));
//        addButton(new ButtonColourRect(10, I18n.format("button.de.toggleHidden.txt"), x - 81, y + 76, 163, 15, c1, c2, c3));
//
//    }
//
//
//    @Override
//    public void tick() {
//        buttonHudFade.setText(I18n.format("button.de.fadeMode" + DEOldConfig.hudSettings[6] + ".txt"));
//        buttonArmorFade.setText(I18n.format("button.de.fadeMode" + DEOldConfig.hudSettings[7] + ".txt"));
//        buttonArmorNumeric.setText(I18n.format("button.de.numeric.txt") + " " + (DEOldConfig.hudSettings[9] == 0 ? I18n.format("gui.de.off.txt") : I18n.format("gui.de.on.txt")));
//    }
//
//
//    @Override
//    protected void actionPerformed(GuiButton button) {
//        if (button.id == 0) {
//            Minecraft.getInstance().displayGuiScreen(parent);
//            return;
//        }
//        else if (button.id == 1 && DEOldConfig.hudSettings[4] < 300) {
//            DEOldConfig.hudSettings[4] += 10;
//        }
//        else if (button.id == 2 && DEOldConfig.hudSettings[4] > 30) {
//            DEOldConfig.hudSettings[4] -= 10;
//        }
//        else if (button.id == 3) {
//            if (DEOldConfig.hudSettings[6] < 4) DEOldConfig.hudSettings[6]++;
//            else DEOldConfig.hudSettings[6] = 0;
//        }
//        else if (button.id == 4 && DEOldConfig.hudSettings[5] < 300) {
//            DEOldConfig.hudSettings[5] += 10;
//        }
//        else if (button.id == 5 && DEOldConfig.hudSettings[5] > 30) {
//            DEOldConfig.hudSettings[5] -= 10;
//        }
//        else if (button.id == 6) {
//            if (DEOldConfig.hudSettings[7] < 4) DEOldConfig.hudSettings[7]++;
//            else DEOldConfig.hudSettings[7] = 0;
//        }
//        else if (button.id == 7) {
//            if (DEOldConfig.hudSettings[9] == 0) DEOldConfig.hudSettings[9] = 1;
//            else DEOldConfig.hudSettings[9] = 0;
//        }
//        else if (button.id == 8) {
//            if (DEOldConfig.hudSettings[8] == 0) DEOldConfig.hudSettings[8] = 1;
//            else DEOldConfig.hudSettings[8] = 0;
//        }
//        else if (button.id == 9) {
//            if (DEOldConfig.hudSettings[10] == 0) DEOldConfig.hudSettings[10] = 1;
//            else DEOldConfig.hudSettings[10] = 0;
//        }
//        else if (button.id == 10) {
//            if (DEOldConfig.hudSettings[11] == 0) DEOldConfig.hudSettings[11] = 1;
//            else DEOldConfig.hudSettings[11] = 0;
//        }
//
//        Property property = ModConfigParser.findUnwrappedProperty(DraconicEvolution.MODID, "hudSettings", "Client Settings");
//        if (property != null) {
//            property.set(DEConfig.hudSettings);
//            ModConfigParser.saveModConfig(DraconicEvolution.MODID);
//        }
//        else {
//            LogHelper.error("Something went wrong when saving config values! Property could not be found");
//        }
//
////        DEConfig.config.get("Gui Stuff", "HUD Settings", new int[]{7, 874, 100, 100, 100, 100, 0, 0, 0, 0, 1, 1}, "Used to store the position of the armor ant tool HUD's. This should not be modified", Integer.MIN_VALUE, Integer.MAX_VALUE, true, 12).set(DEConfig.hudSettings);
////        DEConfig.config.save();
//    }
//
//    @Override
//    public void drawScreen(int x, int y, float partial) {
//        drawDefaultBackground();
//        HudHandler.drawHUD(new RenderGameOverlayEvent.Post(new RenderGameOverlayEvent(partial, new ScaledResolution(mc)), RenderGameOverlayEvent.ElementType.ALL));
//
//        int[] pos = DEConfig.hudSettings;
//        int hudX = (int) ((pos[0] / 1000D) * width);
//        int hudY = (int) ((pos[1] / 1000D) * height);
//        int armorX = (int) ((pos[2] / 1000D) * width);
//        int armorY = (int) ((pos[3] / 1000D) * height);
//
//        drawCenteredString(fontRenderer, I18n.format("gui.de.configureGuiElements.txt"), width / 2, height / 2 - 90, 0x00FFFF);
//        drawCenteredString(fontRenderer, I18n.format("gui.de.hudDisplaySettings.txt"), width / 2, height / 2 - 77, 0xFFFFFF);
//        drawCenteredString(fontRenderer, I18n.format("gui.de.shieldDisplaySettings.txt"), width / 2, height / 2 - 15, 0xFFFFFF);
//        drawCenteredString(fontRenderer, I18n.format("gui.de.clickAndDragPurpleBoxes.txt"), width / 2, height / 2 + 85, 0xFFFFFF);
//
//        drawCenteredString(fontRenderer, DEConfig.hudSettings[4] + "%", width / 2 + 97, height / 2 - 63, 0xFFFFFF);
//        drawCenteredString(fontRenderer, DEConfig.hudSettings[5] + "%", width / 2 + 97, height / 2 - 1, 0xFFFFFF);
//        super.drawScreen(x, y, partial);
//
//        RenderSystem.disableRescaleNormal();
//        RenderHelper.disableStandardItemLighting();
//        RenderSystem.disableLighting();
//        RenderSystem.disableDepth();
//
//        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX + 20, hudY + 20, 0x20FF00FF, 0x20FF00FF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX - 4, hudY, hudX + 5, hudY + 1, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX, hudY - 4, hudX + 1, hudY + 5, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX + 20, hudY - 18, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX - 19, hudY + 19, hudX + 20, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX - 19, hudY - 19, hudX - 18, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(hudX + 19, hudY - 19, hudX + 20, hudY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//
//        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX + 20, armorY + 20, 0x20FF00FF, 0x20FF00FF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX - 4, armorY, armorX + 5, armorY + 1, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX, armorY - 4, armorX + 1, armorY + 5, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX + 20, armorY - 18, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX - 19, armorY + 19, armorX + 20, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX - 19, armorY - 19, armorX - 18, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//        GuiHelper.drawGradientRect(armorX + 19, armorY - 19, armorX + 20, armorY + 20, 0xFFFFFFFF, 0xFFFFFFFF, 1F, 1D);
//
//        RenderSystem.enableLighting();
//        RenderSystem.enableDepth();
//        RenderHelper.enableStandardItemLighting();
//        RenderSystem.enableRescaleNormal();
//
//        if (GuiHelper.isInRect(armorX - 19, armorY - 19, 39, 39, x, y) || GuiHelper.isInRect(hudX - 19, hudY - 19, 39, 39, x, y)) {
//            drawHoveringText(new ArrayList<String>() {{
//                add(I18n.format("info.de.hudDisplayConfigTxt2.txt"));
//            }}, x, y, fontRenderer);
//        }
//
//    }
//
//    @Override
//    protected void mouseClicked(int x, int y, int button) throws IOException {
//        int[] pos = DEConfig.hudSettings;
//        int hudX = (int) ((pos[0] / 1000D) * width);
//        int hudY = (int) ((pos[1] / 1000D) * height);
//        int armorX = (int) ((pos[2] / 1000D) * width);
//        int armorY = (int) ((pos[3] / 1000D) * height);
//
//        if (GuiHelper.isInRect(hudX - 19, hudY - 19, 39, 39, x, y)) {
//            draggingHud = true;
//            dragOffsetX = hudX - x;
//            dragOffsetY = hudY - y;
//            return;
//        }
//        else if (GuiHelper.isInRect(armorX - 19, armorY - 19, 39, 39, x, y)) {
//            draggingArmor = true;
//            dragOffsetX = armorX - x;
//            dragOffsetY = armorY - y;
//            return;
//        }
//
//        super.mouseClicked(x, y, button);
//    }
//
//    @Override
//    protected void mouseReleased(int mouseX, int mouseY, int state) {
//        super.mouseReleased(mouseX, mouseY, state);
//        draggingHud = draggingArmor = false;
//
//
//        Property property = ModConfigParser.findUnwrappedProperty(DraconicEvolution.MODID, "hudSettings", "Client Settings");
//        if (property != null) {
//            property.set(DEConfig.hudSettings);
//            ModConfigParser.saveModConfig(DraconicEvolution.MODID);
//        }
//        else {
//            LogHelper.error("Something went wrong when saving config values! Property could not be found");
//        }
//
////        DEConfig.config.get("Gui Stuff", "HUD Settings", new int[]{7, 874, 100, 100, 100, 100, 0, 0, 0, 0, 1, 1}, "Used to store the position of the armor ant tool HUD's. This should not be modified", Integer.MIN_VALUE, Integer.MAX_VALUE, true, 12).set(DEConfig.hudSettings);
////        DEConfig.config.save();
//    }
//
//    @Override
//    protected void mouseClickMove(int x, int y, int action, long time) {
//        super.mouseClickMove(x, y, action, time);
//        int x1 = (int) (((float) (x + dragOffsetX) / (float) width) * 1000f);
//        int y1 = (int) (((float) (y + dragOffsetY) / (float) height) * 1000f);
//
//        if (draggingHud) {
//            DEConfig.hudSettings[0] = x1;
//            DEConfig.hudSettings[1] = y1;
//        }
//        else if (draggingArmor) {
//            DEConfig.hudSettings[2] = x1;
//            DEConfig.hudSettings[3] = y1;
//        }
//    }
//
//    @Override
//    public boolean doesGuiPauseGame() {
//        return false;
//    }
//
//    @Override
//    protected void keyTyped(char keyChar, int keyInt) {
//        if (keyInt == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyInt)) {
//            Minecraft.getInstance().displayGuiScreen(parent);
//            return;
//        }
//    }
//}
