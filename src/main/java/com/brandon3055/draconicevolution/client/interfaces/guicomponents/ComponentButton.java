package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

import net.minecraft.client.Minecraft;

/**
 * Created by Brandon on 1/01/2015.
 */
public class ComponentButton extends ComponentBase {

	public int buttonId;
	public GUIBase gui;

	public ComponentButton(int x, int y) {
		super(x, y);
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

	}
}
