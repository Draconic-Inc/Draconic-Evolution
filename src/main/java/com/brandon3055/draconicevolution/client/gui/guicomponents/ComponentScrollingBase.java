package com.brandon3055.draconicevolution.client.gui.guicomponents;

/**
 * Created by Brandon on 6/03/2015.
 */
public abstract class ComponentScrollingBase extends ComponentBase {

	protected GUIScrollingBase gui;
	public int scrollOffset;
	public int componentLength;

	public ComponentScrollingBase(int x, int y, GUIScrollingBase gui) {
		super(x, y);
		this.gui = gui;
	}

	public abstract void handleScrollInput(int direction);
}
