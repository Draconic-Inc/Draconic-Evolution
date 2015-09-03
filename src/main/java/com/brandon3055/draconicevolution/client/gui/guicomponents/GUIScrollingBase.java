package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.inventory.Container;

/**
 * Created by Brandon on 6/03/2015.
 */
public abstract class GUIScrollingBase extends GUIBase {

	public int scrollOffset;
	public int pageLength;

	public GUIScrollingBase(Container container, int xSize, int ySize) {
		super(container, xSize, ySize);
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int i = org.lwjgl.input.Mouse.getEventDWheel();
		if (i != 0) {
			handleScrollInput(i > 0 ? -1 : 1);

			for (ComponentBase c : collection.getComponents())
			{
				if (c instanceof ComponentScrollingBase) ((ComponentScrollingBase) c).handleScrollInput(i > 0 ? -1 : 1);
			}
		}
	}

	public abstract void handleScrollInput(int direction);
}
