package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.client.interfaces.componentguis.GUIToolConfig;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayBlock;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Brandon on 26/01/2015.
 */
public class HudHandler {

	private static List<String> hudList = null;
	int width;
	int height;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawHUD(RenderGameOverlayEvent.Post event) {
		if (ConfigHandler.enableHudDisplay && event.type == RenderGameOverlayEvent.ElementType.ALL) {
			if (hudList == null) return;

			Minecraft mc = Minecraft.getMinecraft();

			if (mc.gameSettings.showDebugInfo) return;

			ScaledResolution resolution = event.resolution;
			width = resolution.getScaledWidth();
			height = resolution.getScaledHeight();
			FontRenderer fontRenderer = mc.fontRenderer;

			int x = (int) (((float)ConfigHandler.hudX / 1000F) * (float) width);
			int y = (int) (((float)ConfigHandler.hudY / 1000F) * (float) height);

			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			drawHoveringText(hudList, x, y, fontRenderer);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientTick() {

		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null) return;

		hudList = null;

		if (mc.currentScreen != null)
		{
			if (mc.currentScreen instanceof GUIToolConfig)
			{
				hudList = new ArrayList<String>();
				hudList.add(StatCollector.translateToLocal("info.de.hudDisplayConfigTxt1.txt"));
				hudList.add("");
				hudList.add(StatCollector.translateToLocal("info.de.hudDisplayConfigTxt2.txt"));
				hudList.add("");
				hudList.add(StatCollector.translateToLocal("info.de.hudDisplayConfigTxt3.txt"));
			}
		}
		else if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof IHudDisplayItem) {
			hudList = ((IHudDisplayItem) mc.thePlayer.getHeldItem().getItem()).getDisplayData( mc.thePlayer.getHeldItem());
		}

		MovingObjectPosition mop = mc.thePlayer.rayTrace(5, 0);
		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof IHudDisplayBlock)
		{
			hudList = ((IHudDisplayBlock)mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ)).getDisplayData(mc.theWorld, mop.blockX, mop.blockY, mop.blockZ);
		}
	}

	protected void drawHoveringText(List list, int x, int y, FontRenderer font)
	{
		if (!list.isEmpty())
		{
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;
			Iterator iterator = list.iterator();

			while (iterator.hasNext())
			{
				String s = (String)iterator.next();
				int l = font.getStringWidth(s);

				if (l > k)
				{
					k = l;
				}
			}

			int adjX = x + 12;
			int adjY = y - 12;
			int i1 = 8;

			if (list.size() > 1)
			{
				i1 += 2 + (list.size() - 1) * 10;
			}

			if (adjX + k > width)
			{
				adjX -= 28 + k;
			}

			if (adjY + i1 + 6 > height)
			{
				adjY = height - i1 - 6;
			}

			int j1 = -267386864;
			this.drawGradientRect(adjX - 3, adjY - 4, adjX + k + 3, adjY - 3, j1, j1);
			this.drawGradientRect(adjX - 3, adjY + i1 + 3, adjX + k + 3, adjY + i1 + 4, j1, j1);
			this.drawGradientRect(adjX - 3, adjY - 3, adjX + k + 3, adjY + i1 + 3, j1, j1);
			this.drawGradientRect(adjX - 4, adjY - 3, adjX - 3, adjY + i1 + 3, j1, j1);
			this.drawGradientRect(adjX + k + 3, adjY - 3, adjX + k + 4, adjY + i1 + 3, j1, j1);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			this.drawGradientRect(adjX - 3, adjY - 3 + 1, adjX - 3 + 1, adjY + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(adjX + k + 2, adjY - 3 + 1, adjX + k + 3, adjY + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(adjX - 3, adjY - 3, adjX + k + 3, adjY - 3 + 1, k1, k1);
			this.drawGradientRect(adjX - 3, adjY + i1 + 2, adjX + k + 3, adjY + i1 + 3, l1, l1);

			for (int i2 = 0; i2 < list.size(); ++i2)
			{
				String s1 = (String)list.get(i2);
				font.drawStringWithShadow(s1, adjX, adjY, -1);

				if (i2 == 0)
				{
					adjY += 2;
				}

				adjY += 10;
			}

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
//			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	protected void drawGradientRect(int x1, int y1, int x2, int y2, int colour1, int colour2)
	{
		float f = (float)(colour1 >> 24 & 255) / 255.0F;
		float f1 = (float)(colour1 >> 16 & 255) / 255.0F;
		float f2 = (float)(colour1 >> 8 & 255) / 255.0F;
		float f3 = (float)(colour1 & 255) / 255.0F;
		float f4 = (float)(colour2 >> 24 & 255) / 255.0F;
		float f5 = (float)(colour2 >> 16 & 255) / 255.0F;
		float f6 = (float)(colour2 >> 8 & 255) / 255.0F;
		float f7 = (float)(colour2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex((double)x2, (double)y1, 300D);
		tessellator.addVertex((double)x1, (double)y1, 300D);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex((double)x1, (double)y2, 300D);
		tessellator.addVertex((double)x2, (double)y2, 300D);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
