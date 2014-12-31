package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 29/12/2014.
 */
public class ComponentBackground extends ComponentBase {
	protected int width;
	protected int height;
	private final ResourceLocation texture;

	public ComponentBackground(int x, int y, int width, int height, ResourceLocation texture) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.texture = texture;
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
		drawTexturedModalRect(x, y, 0, 0, width, height);
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}
}
