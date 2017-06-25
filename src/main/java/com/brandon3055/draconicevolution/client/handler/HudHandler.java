package com.brandon3055.draconicevolution.client.handler;

import codechicken.lib.render.state.GlStateTracker;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiHudConfig;
import com.brandon3055.draconicevolution.handlers.CustomArmorHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 4/07/2016.
 */
public class HudHandler {

    private static List<String> hudList = null;
    private static List<String> ltHudList = null;
    private static float toolTipFadeOut = 0F;
    private static float armorStatsFadeOut = 0F;
    private static boolean showShieldHud = false;
    private static int shieldPercentCharge = 0;
    private static float shieldPoints = 0F;
    private static float maxShieldPoints = 0F;
    private static float shieldEntropy = 0F;
    private static int rfCharge = 0;
    private static long rfTotal = 0;

    private static int width;
    private static int height;

    public static void drawHUD(RenderGameOverlayEvent.Post event) {
        //LogHelper.info(event.getType());
        Minecraft mc = Minecraft.getMinecraft();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo || mc.currentScreen instanceof GuiChat) {
            return;
        }

        GlStateTracker.pushState();
        GlStateManager.pushMatrix();
        ScaledResolution resolution = event.getResolution();
        width = resolution.getScaledWidth();
        height = resolution.getScaledHeight();
        FontRenderer fontRenderer = mc.fontRendererObj;

        if (DEConfig.hudSettings[10] == 1 && hudList != null && toolTipFadeOut > 0) {
            int x = (int) (((float) DEConfig.hudSettings[0] / 1000F) * (float) width);
            int y = (int) (((float) DEConfig.hudSettings[1] / 1000F) * (float) height);

            GuiHelper.drawHoveringTextScaled(hudList, x, y, fontRenderer, toolTipFadeOut > 1 ? 1 : toolTipFadeOut, DEConfig.hudSettings[4] / 100D, width, height);
            GlStateManager.disableLighting();
        }

        if (DEConfig.hudSettings[11] == 1 && showShieldHud) {
            int x = (int) (((float) DEConfig.hudSettings[2] / 1000F) * (float) width);
            int y = (int) (((float) DEConfig.hudSettings[3] / 1000F) * (float) height);

            drawArmorHUD(x, y, DEConfig.hudSettings[8] == 1, DEConfig.hudSettings[5] / 100D);
        }

        GlStateManager.popMatrix();
        GlStateTracker.popState();
    }


    //TODO Clean up this entire system and re write the actual renderer to look cleaner and be more flexible!
    //x, y, x, y, scale, scale, fademode, fademode, rotateArmor, armorText
    public static void clientTick() {
        if (DEConfig.hudSettings[6] > 0 && toolTipFadeOut > 1F - ((float) DEConfig.hudSettings[6] * 0.25F)) {
            toolTipFadeOut -= 0.1F;
        }
        if (hudList != null && (ltHudList == null || !hudList.equals(ltHudList))) {
            toolTipFadeOut = 5F;
        }
        if (DEConfig.hudSettings[7] > 0 && armorStatsFadeOut > 1F - ((float) DEConfig.hudSettings[7] * 0.25F)) {
            armorStatsFadeOut -= 0.1F;
            if (armorStatsFadeOut < 0) armorStatsFadeOut = 0;
        }

        ltHudList = hudList;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc == null || mc.player == null) {
            return;
        }

        hudList = new ArrayList<String>();

        if (mc.currentScreen != null) {
            if (mc.currentScreen instanceof GuiHudConfig) {
                hudList.add(I18n.format("info.de.hudDisplayConfigTxt1.txt"));
                hudList.add("");
                hudList.add("");
                hudList.add("");
                hudList.add(I18n.format("info.de.hudDisplayConfigTxt3.txt"));
                toolTipFadeOut = 1F;
                armorStatsFadeOut = 1F;
            }
        }
        else {

            RayTraceResult traceResult = mc.player.rayTrace(5, 0);
            IBlockState state = null;

            if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                state = mc.world.getBlockState(traceResult.getBlockPos());
            }

            if (state != null && state.getBlock() instanceof IHudDisplay) {
                ((IHudDisplay) state.getBlock()).addDisplayData(null, mc.world, traceResult.getBlockPos(), hudList);
            }
            else {
                ItemStack stack = mc.player.getHeldItemMainhand();

                if (stack.isEmpty() || !(stack.getItem() instanceof IHudDisplay)) {
                    stack = mc.player.getHeldItemOffhand();
                }

                if (!stack.isEmpty() && stack.getItem() instanceof IHudDisplay) {
                    ((IHudDisplay) stack.getItem()).addDisplayData(stack, mc.world, null, hudList);
                }
            }
        }

        CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(mc.player);

        if (summery == null) {
            showShieldHud = false;
            return;
        }
        showShieldHud = armorStatsFadeOut > 0F;

        if (maxShieldPoints != summery.maxProtectionPoints || shieldPoints != summery.protectionPoints || shieldEntropy != summery.entropy || rfTotal != summery.totalEnergyStored) armorStatsFadeOut = 5F;

        maxShieldPoints = summery.maxProtectionPoints;
        shieldPoints = summery.protectionPoints;
        shieldPercentCharge = (int) (summery.protectionPoints / summery.maxProtectionPoints * 100D);
        shieldEntropy = summery.entropy;
        rfCharge = (int) ((double) summery.totalEnergyStored / Math.max((double) summery.maxTotalEnergyStorage, 1D) * 100D);
        rfTotal = summery.totalEnergyStored;
    }

    private static void drawArmorHUD(int x, int y, boolean rotated, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        ResourceHelperDE.bindTexture(DETextures.GUI_HUD);

        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
        GlStateManager.color(1F, 1F, 1F, Math.min(armorStatsFadeOut, 1F));

        if (rotated) {
            GuiHelper.drawTexturedRect(x - 15, y + 1, 14, 16, 2, 0, 13, 15, 0, GuiHelper.PXL128);
            x += 104;
            GlStateManager.translate(x, y, 0);
            GlStateManager.rotate(-90, 0, 0, -1);
            GlStateManager.translate(-x, -y, 0);
        }
        else GuiHelper.drawTexturedRect(x + 1, y + 105, 15, 17, 2, 0, 13, 15, 0, GuiHelper.PXL128);

        GuiHelper.drawTexturedRect(x, y, 17, 104, 0, 15, 17, 104, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 2, y + 2 + (100 - shieldPercentCharge), 7, shieldPercentCharge, 17, 100 - shieldPercentCharge, 7, shieldPercentCharge, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 10, y + 2 + 100 - (int) shieldEntropy, 2, (int) shieldEntropy, 25, 100 - (int) shieldEntropy, 2, (int) shieldEntropy, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 13, y + 2 + 100 - rfCharge, 2, rfCharge, 28, 100 - rfCharge, 2, rfCharge, 0, GuiHelper.PXL128);


        if (DEConfig.hudSettings[9] == 1) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            GlStateManager.translate(x, y, 0);
            if (rotated) GlStateManager.rotate(90, 0, 0, -1);
            GlStateManager.translate(-x, -y, 0);
            String shield = (int) shieldPoints + "/" + (int) maxShieldPoints;
            String entropy = "EN: " + (int) shieldEntropy + "%";
            String energy = "RF: " + Utils.formatNumber(rfTotal);
            float fade = Math.min(armorStatsFadeOut, 1F);
            if (!rotated) {
                fontRenderer.drawStringWithShadow(shield, x + 18, y + 74, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
                fontRenderer.drawStringWithShadow(energy, x + 18, y + 84, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
                fontRenderer.drawStringWithShadow(entropy, x + 18, y + 94, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
            }
            else {
                fontRenderer.drawString(shield, x - 52 - fontRenderer.getStringWidth(shield) / 2, y + 2, ((int) (fade * 240F) + 0x10 << 24) | 0x000000FF);
                fontRenderer.drawStringWithShadow(entropy, x - fontRenderer.getStringWidth(entropy), y + 18, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
                fontRenderer.drawStringWithShadow(energy, x - 102, y + 18, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
            }
        }

        ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("minecraft:textures/gui/icons.png"));
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }
}
