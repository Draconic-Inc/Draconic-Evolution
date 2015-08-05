package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 31/7/2015.
 */
public class ComponentTextureButton extends ComponentButton {
	public int textureXPos;
	public int textureYPos;
	private ResourceLocation texture;

	//public ComponentTextureButton(int id, int xPos, int yPos, int textureXPos, int textureYPos, int xSise, int ySise, String text) {
	public ComponentTextureButton(int x, int y, int textureXPos, int textureYPos, int xSize, int ySize, int id, GUIBase gui, String displayString, String hoverText, ResourceLocation texture) {
		super(x, y, xSize, ySize, id, gui, displayString, hoverText);
		this.textureXPos = textureXPos;
		this.textureYPos = textureYPos;
		this.texture = texture;
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

		GL11.glPushMatrix();
		//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		minecraft.getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = isMouseOver(mouseX, mouseY) ? 2 : 1;
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this.drawTexturedModalRect(this.x, this.y, textureXPos, textureYPos + k * ySize, this.xSize, this.ySize);
		int l = 14737632;

		if (packedFGColour != 0)
		{
			l = packedFGColour;
		}
		else if (!this.enabled)
		{
			l = 10526880;
		}
		else if (isMouseOver(mouseX, mouseY))
		{
			l = 16777120;
		}
		this.drawCenteredString(fontRendererObj, this.displayString, this.x + this.xSize / 2, this.y + (this.ySize - 8) / 2, l);
		//GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
