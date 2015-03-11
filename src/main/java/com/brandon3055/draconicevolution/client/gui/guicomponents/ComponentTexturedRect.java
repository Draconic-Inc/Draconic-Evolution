package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 29/12/2014.
 */
public class ComponentTexturedRect extends ComponentBase {
	private int width;
	private int height;
	private int texX = 0;
	private int texY = 0;
	private final ResourceLocation texture;
	private boolean transparent = false;

	public ComponentTexturedRect(int x, int y, int width, int height, ResourceLocation texture) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
	public ComponentTexturedRect(int x, int y, int texX, int texY, int width, int height, ResourceLocation texture, boolean transparent) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.texX = texX;
		this.texY = texY;
		this.transparent = transparent;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		if (transparent)
		{
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glScalef(1f, 0.33f, 1f);
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		}
		GL11.glColor4f(1f, 1f, 1f, 1f);
		drawTexturedModalRect(x, y, texX, texY, width, height);
		if (transparent)
		{
			GL11.glScalef(2f, 2f, 2f);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}
}
