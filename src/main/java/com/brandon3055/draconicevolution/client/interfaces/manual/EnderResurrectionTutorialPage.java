package com.brandon3055.draconicevolution.client.interfaces.manual;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by Brandon on 20/09/2014.
 */
public class EnderResurrectionTutorialPage extends  TutorialPage {
	public static final ResourceLocation image = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Ritual_of_ender_Resurrection.png");
	public EnderResurrectionTutorialPage(String name, PageCollection collection) {
		super(name, collection, StatCollector.translateToLocal("manual.de.roerth.txt"));
		this.lastPage = 1;
		this.formattedDescription = new List[6];
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	@Override
	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		switch (page) {
			case 0:
				page0(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 1:
				page1(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
		}
	}

	private void page0(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(image);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 1f);
		drawTexturedModalRect((offsetX+64)*2, (offsetY+14)*2, 0, 0, 256, 256);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY+38, getFormattedText(fontRendererObj, ttl("manual.de.roertpage0a.txt"), 0));
		addDescription(minecraft, offsetX, offsetY+94, getFormattedText(fontRendererObj, ttl("manual.de.roertpage0b.txt"), 1));
	}

	private void page1(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		addDescription(minecraft, offsetX, offsetY-50, getFormattedText(fontRendererObj, ttl("manual.de.roertpage1a.txt"), 2));
		addDescription(minecraft, offsetX, offsetY-28, getFormattedText(fontRendererObj, ttl("manual.de.roertpage1b.txt"), 3));
		addDescription(minecraft, offsetX, offsetY-6, getFormattedText(fontRendererObj, ttl("manual.de.roertpage1c.txt"), 4));
		addDescription(minecraft, offsetX, offsetY+23, getFormattedText(fontRendererObj, ttl("manual.de.roertpage1d.txt"), 5));
	}
}
