package com.brandon3055.draconicevolution.client.gui.manual;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 17/09/2014.
 */
public class IntroPage extends BasePage {
	public static final ResourceLocation logo = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/ManualIntro.png");
	public IntroPage(String name, PageCollection collection) {
		super(name, collection);
	}

	@Override
	public void renderOverlayComponents(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlayComponents(minecraft, offsetX, offsetY, mouseX, mouseY);

	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
		//ResourceLocation logo = new ResourceLocation(References.RESOURCESPREFIX +"oHRx1.png");

		minecraft.renderEngine.bindTexture(logo);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glScalef(1F, 0.5F, 1F);
		drawTexturedModalRect(offsetX + 1, offsetY + 10, 0, 0, 256, 256);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(2.5F, 2.5F, 1F);
		drawCenteredString(fontRendererObj, ttl("manual.de.userGuide.txt"), (int) ((offsetX + 128) / 2.5), (int) ((offsetY + 128) / 2.5), 0x00b400);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		fontRendererObj.drawString("This gui is a work in progress", (offsetX + 4)*2, (offsetY + 194)*2, 0x000000);
		GL11.glPopMatrix();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButtonAHeight(0, getXMin()+88, getYMin()+181, 80, 16, ttl("button.de.goToIndex.txt")));
	}
}
