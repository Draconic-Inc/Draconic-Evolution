package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.gui.GuiHudConfig;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.armor.IShieldedArmor;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayBlock;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 26/01/2015.
 */
public class HudHandler {

	private static List<String> hudList = null;
	private static List<String> ltHudList = null;
	private static float toolTipFadeOut = 0F;
	private static float armorStatsFadeOut = 0F;
	public static boolean fadeHud = false;
	public static boolean fadeArmorStats = false;
	private static boolean showArmorHud = false;
	private static int armorPoints = 0;
	private static int maxArmorPoints = 0;
	private static int armorFatigue = 0;
	private static int ltProtPoints = 0;
	private static int ltFatigue = 0;

	int width;
	int height;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawHUD(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.type != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo) return;
		ScaledResolution resolution = event.resolution;
		width = resolution.getScaledWidth();
		height = resolution.getScaledHeight();
		FontRenderer fontRenderer = mc.fontRenderer;

		if (ConfigHandler.hudSettings[10] == 1 && hudList != null) {
			int x = (int) (((float)ConfigHandler.hudSettings[0] / 1000F) * (float) width);
			int y = (int) (((float)ConfigHandler.hudSettings[1] / 1000F) * (float) height);

			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GuiHelper.drawHoveringText(hudList, x, y, fontRenderer, toolTipFadeOut > 1 ? 1 : toolTipFadeOut, ConfigHandler.hudSettings[4] / 100D, width, height);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}

		if (ConfigHandler.hudSettings[11] == 1 && showArmorHud) {
			int x = (int) (((float)ConfigHandler.hudSettings[2] / 1000F) * (float) width);
			int y = (int) (((float)ConfigHandler.hudSettings[3] / 1000F) * (float) height);

			drawArmorHUD(x, y, ConfigHandler.hudSettings[8] == 1, ConfigHandler.hudSettings[5] / 100D);
		}
	}
	//x, y, x, y, scale, scale, fademode, fademode, rotateArmor, armorText
	@SideOnly(Side.CLIENT)
	public static void clientTick() {
		if (toolTipFadeOut > 0 && fadeHud) toolTipFadeOut-=0.1F;
		if (hudList != null && (ltHudList == null || !hudList.equals(ltHudList))) toolTipFadeOut = 10F;
		if (armorStatsFadeOut > 0 && fadeArmorStats) armorStatsFadeOut-=0.1F;
		//if (hudList != null && (ltHudList == null || !hudList.equals(ltHudList))) armorStatsFadeOut = 10F;todo armor refresh condition

		ltHudList = hudList;

		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null) return;
		ItemStack[] armorSlots = mc.thePlayer.inventory.armorInventory;

		hudList = null;

		if (mc.currentScreen != null)
		{
			if (mc.currentScreen instanceof GuiHudConfig)
			{
				hudList = new ArrayList<String>();
				hudList.add(StatCollector.translateToLocal("info.de.hudDisplayConfigTxt1.txt"));
				hudList.add("");
				hudList.add("");
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

		showArmorHud = false;
		maxArmorPoints = 0;
		armorPoints = 0;
		armorFatigue = 0;
		int peaces = 0;
		for (ItemStack stack : armorSlots){
			if (stack == null || !(stack.getItem() instanceof IShieldedArmor)) continue;
			peaces++;
			showArmorHud = true;
			IShieldedArmor armor = (IShieldedArmor) stack.getItem();
			maxArmorPoints += armor.getProtectionPoints(stack);
			armorPoints += ItemNBTHelper.getInteger(stack, "ProtectionPoints", 0);
			armorFatigue += ItemNBTHelper.getInteger(stack, "ArmorFatigue", 0);
		}
		if (armorPoints > 0 && maxArmorPoints > 0) armorPoints = (int)((armorPoints / (double)maxArmorPoints) * 100D);
		if (armorFatigue > 0 && peaces > 0) armorFatigue /= peaces;

	}


	private void drawArmorHUD(int x, int y, boolean rotated, double scale){
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		ResourceHandler.bindResource("textures/gui/HUD.png");

		if (rotated){
			GuiHelper.drawTexturedRect(x-(12*scale), y+scale, (int)(11*scale), (int)(13*scale), 2, 0, 11, 13, 0, GuiHelper.PXL128);
			x+=(104*scale);
			GL11.glTranslated(x, y, 0);
			GL11.glRotated(-90, 0, 0, -1);
			GL11.glTranslated(-x, -y, 0);
		}
		else GuiHelper.drawTexturedRect(x+(1*scale), y+(104*scale)+1, (int)(13*scale), (int)(15*scale), 2, 0, 11, 13, 0, GuiHelper.PXL128);

		GuiHelper.drawTexturedRect(x, y, (int)(15*scale), (int)(104*scale), 0, 14, 15, 104, 0, GuiHelper.PXL128);
		GuiHelper.drawTexturedRect(x+2, y+2+(100- armorPoints), (int)(8*scale), (int)(armorPoints *scale), 15, 100- armorPoints, 8, armorPoints, 0, GuiHelper.PXL128);
		GuiHelper.drawTexturedRect(x+11, y+2+(100- armorFatigue), (int)(2*scale), (int)(armorFatigue *scale), 24, 100- armorFatigue, 2, armorFatigue, 0, GuiHelper.PXL128);

		if (ConfigHandler.hudSettings[9] == 1) {
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			GL11.glTranslated(x, y, 0);
			GL11.glScaled(scale, scale, 1);
			if (rotated) GL11.glRotated(90, 0, 0, -1);
			GL11.glTranslated(-x, -y, 0);
			if (!rotated) {
				fontRenderer.drawStringWithShadow(armorPoints + "/" + maxArmorPoints, x + 16, y + 84, 0xFFFFFF);
				fontRenderer.drawStringWithShadow(armorFatigue + "%", x + 16, y + 94, 0xFFFFFF);
			}else {
				fontRenderer.drawStringWithShadow(armorPoints + "/" + maxArmorPoints, x - 102, y + 16, 0xFFFFFF);
				fontRenderer.drawStringWithShadow(armorFatigue + "%", x - fontRenderer.getStringWidth(armorFatigue + "%"), y + 16, 0xFFFFFF);
			}
		}

		ResourceHandler.bindTexture(ResourceHandler.getResourceWOP("minecraft:textures/gui/icons.png"));
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}
}
