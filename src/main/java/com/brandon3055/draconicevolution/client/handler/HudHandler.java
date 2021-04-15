package com.brandon3055.draconicevolution.client.handler;


import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.items.equipment.ModularChestpiece;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;

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
    private static double shieldPoints = 0F;
    private static double maxShieldPoints = 0F;
    private static double shieldEntropy = 0F;
    private static int rfCharge = 0;
    private static long rfTotal = 0;

    private static int width;
    private static int height;

    public static void drawHUD(RenderGameOverlayEvent.Post event) {
        //LogHelper.info(event.getType());
        Minecraft mc = Minecraft.getInstance();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.options.renderDebug || mc.screen instanceof ChatScreen) {
            return;
        }

//        GlStateTracker.pushState();
        RenderSystem.pushMatrix();
        width = mc.getWindow().getGuiScaledWidth();
        height = mc.getWindow().getGuiScaledHeight();
        FontRenderer fontRenderer = mc.font;

        if (DEOldConfig.hudSettings[10] == 1 && hudList != null && toolTipFadeOut > 0) {
            int x = (int) (((float) DEOldConfig.hudSettings[0] / 1000F) * (float) width);
            int y = (int) (((float) DEOldConfig.hudSettings[1] / 1000F) * (float) height);

            GuiHelper.drawHoveringTextScaled(hudList, x, y, fontRenderer, toolTipFadeOut > 1 ? 1 : toolTipFadeOut, DEOldConfig.hudSettings[4] / 100D, width, height);
            RenderSystem.disableLighting();
        }

        if (DEOldConfig.hudSettings[11] == 1 && showShieldHud) {
            int x = (int) (((float) DEOldConfig.hudSettings[2] / 1000F) * (float) width);
            int y = (int) (((float) DEOldConfig.hudSettings[3] / 1000F) * (float) height);

            drawArmorHUD(event.getMatrixStack(), x, y, DEOldConfig.hudSettings[8] == 1, DEOldConfig.hudSettings[5] / 100D);
        }

        RenderSystem.popMatrix();
//        GlStateTracker.popState();
    }


    //TODO Clean up this entire system and re write the actual renderer to look cleaner and be more flexible!
    //x, y, x, y, scale, scale, fademode, fademode, rotateArmor, armorText
    public static void clientTick() {
        if (DEOldConfig.hudSettings[6] > 0 && toolTipFadeOut > 1F - ((float) DEOldConfig.hudSettings[6] * 0.25F)) {
            toolTipFadeOut -= 0.1F;
        }
        if (hudList != null && (ltHudList == null || !hudList.equals(ltHudList))) {
            toolTipFadeOut = 5F;
        }
        if (DEOldConfig.hudSettings[7] > 0 && armorStatsFadeOut > 1F - ((float) DEOldConfig.hudSettings[7] * 0.25F)) {
            armorStatsFadeOut -= 0.1F;
            if (armorStatsFadeOut < 0) armorStatsFadeOut = 0;
        }

        ltHudList = hudList;

        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.player == null) {
            return;
        }

        hudList = new ArrayList<String>();

        if (mc.screen != null) {
//            if (mc.currentScreen instanceof GuiHudConfig) {//TODO Gui Stuff
//                hudList.add(I18n.format("info.de.hudDisplayConfigTxt1.txt"));
//                hudList.add("");
//                hudList.add("");
//                hudList.add("");
//                hudList.add(I18n.format("info.de.hudDisplayConfigTxt3.txt"));
//                toolTipFadeOut = 1F;
//                armorStatsFadeOut = 1F;
//            }
        } else {

            RayTraceResult traceResult = mc.player.pick(5, 0, false);
            BlockState state = null;

            if (traceResult instanceof BlockRayTraceResult) {
                if (traceResult != null) {
                    state = mc.level.getBlockState(((BlockRayTraceResult) traceResult).getBlockPos());
                }

                if (state != null && state.getBlock() instanceof IHudDisplay) {
                    ((IHudDisplay) state.getBlock()).addDisplayData(null, mc.level, ((BlockRayTraceResult) traceResult).getBlockPos(), hudList);
                } else {
                    ItemStack stack = mc.player.getMainHandItem();

                    if (stack.isEmpty() || !(stack.getItem() instanceof IHudDisplay)) {
                        stack = mc.player.getOffhandItem();
                    }

                    if (!stack.isEmpty() && stack.getItem() instanceof IHudDisplay) {
                        ((IHudDisplay) stack.getItem()).addDisplayData(stack, mc.level, null, hudList);
                    }
                }
            }
        }


        ItemStack chestStack = ModularChestpiece.getChestpiece(mc.player);//mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        LazyOptional<IOPStorage> optionalStorage = chestStack.getCapability(DECapabilities.OP_STORAGE);
        if (chestStack.isEmpty() || !optionalHost.isPresent() || !optionalStorage.isPresent()) {
            showShieldHud = false;
            return;
        }
        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
        IOPStorage opStorage = optionalStorage.orElseThrow(IllegalStateException::new);
        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null || (shieldControl.getShieldCapacity() <= 0 && shieldControl.getMaxShieldBoost() <= 0)) {
            showShieldHud = false;
            return;
        }


        armorStatsFadeOut = 1;
        showShieldHud = armorStatsFadeOut > 0F;

//        if (maxShieldPoints != summery.maxProtectionPoints || shieldPoints != summery.protectionPoints || shieldEntropy != summery.entropy || rfTotal != summery.totalEnergyStored) armorStatsFadeOut = 5F;

        maxShieldPoints = shieldControl.getShieldCapacity() == 0 ? shieldControl.getMaxShieldBoost() : shieldControl.getShieldCapacity();
        shieldPoints = shieldControl.getShieldPoints();
        shieldPercentCharge = (int) ((shieldPoints / maxShieldPoints) * 100D);
        if (!shieldControl.isShieldEnabled()) {
            maxShieldPoints = shieldPoints = shieldPercentCharge = 0;
        }

        shieldEntropy = (shieldControl.getShieldCoolDown() / (double) shieldControl.getMaxShieldCoolDown()) * 100D;
        rfTotal = opStorage.getOPStored();
        rfCharge = (int) ((double) rfTotal / Math.max((double) opStorage.getMaxOPStored(), 1D) * 100D);
    }

    private static void drawArmorHUD(MatrixStack mStack, int x, int y, boolean rotated, double scale) {
        RenderSystem.pushMatrix();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        ResourceHelperDE.bindTexture(DETextures.GUI_HUD);

        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(scale, scale, 1);
        RenderSystem.translated(-x, -y, 0);
        RenderSystem.color4f(1F, 1F, 1F, Math.min(armorStatsFadeOut, 1F));

        if (rotated) {
            GuiHelper.drawTexturedRect(x - 15, y + 1, 14, 16, 2, 0, 13, 15, 0, GuiHelper.PXL128);
            x += 104;
            RenderSystem.translated(x, y, 0);
            RenderSystem.rotatef(-90, 0, 0, -1);
            RenderSystem.translated(-x, -y, 0);
        } else GuiHelper.drawTexturedRect(x + 1, y + 105, 15, 17, 2, 0, 13, 15, 0, GuiHelper.PXL128);

        GuiHelper.drawTexturedRect(x, y, 17, 104, 0, 15, 17, 104, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 2, y + 2 + (100 - shieldPercentCharge), 7, shieldPercentCharge, 17, 100 - shieldPercentCharge, 7, shieldPercentCharge, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 10, y + 2 + 100 - (int) shieldEntropy, 2, (int) shieldEntropy, 25, 100 - (int) shieldEntropy, 2, (int) shieldEntropy, 0, GuiHelper.PXL128);
        GuiHelper.drawTexturedRect(x + 13, y + 2 + 100 - rfCharge, 2, rfCharge, 28, 100 - rfCharge, 2, rfCharge, 0, GuiHelper.PXL128);


        if (DEOldConfig.hudSettings[9] == 1) {
            FontRenderer fontRenderer = Minecraft.getInstance().font;
            RenderSystem.translated(x, y, 0);
            if (rotated) RenderSystem.rotatef(90, 0, 0, -1);
            RenderSystem.translated(-x, -y, 0);
            String shield = (int) shieldPoints + "/" + (int) maxShieldPoints;
//            String entropy = "EN: " + (int) shieldEntropy + "%";
            String energy = "OP: " + formatNumber(rfTotal);
            float fade = Math.min(armorStatsFadeOut, 1F);
            if (!rotated) {
                fontRenderer.drawShadow(mStack, shield, x + 18, y + 74, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
                fontRenderer.drawShadow(mStack, energy, x + 18, y + 84, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
//                fontRenderer.drawStringWithShadow(entropy, x + 18, y + 94, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
            } else {
                fontRenderer.draw(mStack, shield, x - 52 - fontRenderer.width(shield) / 2, y + 2, ((int) (fade * 240F) + 0x10 << 24) | 0x000000FF);
//                fontRenderer.drawStringWithShadow(entropy, x - fontRenderer.getStringWidth(entropy), y + 18, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
                fontRenderer.drawShadow(mStack, energy, x - 102, y + 18, ((int) (fade * 240F) + 0x10 << 24) | 0x00FFFFFF);
            }
        }

        ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("minecraft:textures/gui/icons.png"));
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.popMatrix();
    }

    public static String formatNumber(long value) {
        if (value < 1000L) return String.valueOf(value);
        else if (value < 1000000L) return Utils.addCommas(value); //I mean whats the ploint of displaying 1.235K instead of 1,235?
        else if (value < 1000000000L) return String.valueOf(Math.round(value / 100000L) / 10D) + "M";
        else if (value < 1000000000000L) return String.valueOf(Math.round(value / 100000000L) / 10D) + "G";
        else if (value < 1000000000000000L) return String.valueOf(Math.round(value / 1000000000L) / 1000D) + "T";
        else if (value < 1000000000000000000L) return String.valueOf(Math.round(value / 1000000000000L) / 1000D) + "P";
        else if (value <= Long.MAX_VALUE) return String.valueOf(Math.round(value / 1000000000000000L) / 1000D) + "E";
        else return "Something is very broken!!!!";
    }
}
