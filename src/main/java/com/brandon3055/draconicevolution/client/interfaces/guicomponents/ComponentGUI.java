package com.brandon3055.draconicevolution.client.interfaces.guicomponents;

/**
 * Created by Brandon on 25/12/2014.
 */

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

/**Base class for guis that use gui components*/
public abstract class ComponentGUI extends GuiScreen {

	List<BaseComponent> guiComponents = new ArrayList<BaseComponent>();

	public ComponentGUI(){
	}

	public abstract void addComponents();

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		for (BaseComponent b : guiComponents) b.drawScreen(x, y, f);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		for (BaseComponent b : guiComponents) b.mouseClick(x, y, button);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
	}

	@Override
	protected void actionPerformed(GuiButton button) {
	}

	@Override
	protected void keyTyped(char keyChar, int keyInt) {
		super.keyTyped(keyChar, keyInt);
	}

	@Override
	public void updateScreen() {
		for (BaseComponent b : guiComponents) b.updateScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	public abstract int getSizeX();

	public abstract int getSizeY();
}
