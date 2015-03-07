package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.client.Minecraft;

/**
 * Created by Brandon on 7/03/2015.
 */
public class ComponentIndexButton extends ComponentScrollingBase {

	public ComponentIndexButton(int x, int y, GUIScrollingBase gui) {
		super(x, y, gui);
	}

	@Override
	public void handleScrollInput(int direction) {
		//this.y += direction * 10;
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		int sy = y - gui.scrollOffset;
		if (sy > 1 && sy + getHeight() < gui.getYSize())
		{
			fontRendererObj.drawString("!!!!!!!!!!! Test !!!!!!!!!! " + y/12, x, sy, 0xffffff);
		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
	}
}
