package com.brandon3055.draconicevolution.client.gui.guicomponents;

/**
 * Created by Brandon on 6/03/2015.
 */
public abstract class ComponentScrollingBase extends ComponentBase {
	public ComponentScrollingBase(int x, int y) {
		super(x, y);
	}

	public abstract void handleScrollInput(int direction);
}
