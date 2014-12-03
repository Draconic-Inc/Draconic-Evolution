package com.brandon3055.draconicevolution.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is copied from Thaumic Tinkerer
 */
public class ToolHudHandler {

	private static String currentTooltip;
	private static int tooltipDisplayTicks;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawDislocationFocusHUD(RenderGameOverlayEvent.Post event) {
		if (event.type == RenderGameOverlayEvent.ElementType.ALL && tooltipDisplayTicks > 0 && !MathHelper.stringNullOrLengthZero(currentTooltip)) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution var5 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int var6 = var5.getScaledWidth();
			int var7 = var5.getScaledHeight();
			FontRenderer var8 = mc.fontRenderer;

			int tooltipStartX = (var6 - var8.getStringWidth(currentTooltip)) / 2;
			int tooltipStartY = var7 - 72;

			int opacity = (int) (tooltipDisplayTicks * 256.0F / 10.0F);

			if (opacity > 160)
				opacity = 160;

			if (opacity > 0) {
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				int color = Color.getHSBColor((float) Math.cos(ClientEventHandler.elapsedTicks / 250D), 0.6F, 1F).getRGB();
				var8.drawStringWithShadow(currentTooltip, tooltipStartX, tooltipStartY, color + (opacity << 24));
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
		}
	}

	public static void setTooltip(String tooltip) {
		if (!tooltip.equals(currentTooltip)) {
			currentTooltip = tooltip;
			tooltipDisplayTicks = 500;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientTick() {
		if (tooltipDisplayTicks > 0)
			--tooltipDisplayTicks;
	}

}
