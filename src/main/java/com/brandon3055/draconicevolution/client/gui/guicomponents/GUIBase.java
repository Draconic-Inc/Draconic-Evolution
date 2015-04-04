package com.brandon3055.draconicevolution.client.gui.guicomponents;

/**
 * Created by Brandon on 25/12/2014.
 * This class is copied from open blocks
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class GUIBase extends GuiContainer {

	protected ComponentCollection collection;
	/**used to prevent multiple buttons being pressed wen overlapping components are changed*/
	protected boolean buttonPressed = false;

	public GUIBase(Container container, int xSize, int ySize) {
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		mc = Minecraft.getMinecraft();
		collection = assembleComponents();
	}

	protected abstract ComponentCollection assembleComponents();

	protected void addDependentComponents(){}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (collection.isMouseOver(x - this.guiLeft, y - this.guiTop)) {
			collection.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
		}
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		if (collection.isMouseOver(x - this.guiLeft, y - this.guiTop)) {
			//if (button >= 0) collection.mouseUp(x - this.guiLeft, y - this.guiTop, button);
		}
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		//if (collection.isMouseOver(x - this.guiLeft, y - this.guiTop)) collection.mouseDrag(x - this.guiLeft, y - this.guiTop, button, time);
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		//collection.keyTyped(par1, par2);
	}

	public void preRender(float mouseX, float mouseY) {}

	public void postRender(int mouseX, int mouseY) {}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		prepareRenderState();
		preRender(mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		collection.renderBackground(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
		postRender(mouseX, mouseY);
		restoreRenderState();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//prepareRenderState();
		GL11.glPushMatrix();
		collection.renderForground(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
		//restoreRenderState();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		prepareRenderState();
		GL11.glPushMatrix();
		collection.renderFinal(this.mc, this.guiLeft, this.guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
		restoreRenderState();
	}

	protected void prepareRenderState() {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	protected void restoreRenderState() {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		collection.setWorldAndResolution(mc, width, height);

	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		collection.removeScheduled();
		collection.updateScreen();
		buttonPressed = false;
	}

	public void buttonClicked(int id){buttonPressed = true;}

	public int getXSize() { return xSize; }

	public int getYSize() { return ySize; }
}
