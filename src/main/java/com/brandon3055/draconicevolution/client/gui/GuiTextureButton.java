package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 30/6/2015.
 */
public class GuiTextureButton extends GuiButton {

    public int textureXPos;
    public int textureYPos;

    public GuiTextureButton(int id, int xPos, int yPos, int textureXPos, int textureYPos, int xSise, int ySise,
            String text) {
        super(id, xPos, yPos, xSise, ySise, text);
        this.textureXPos = textureXPos;
        this.textureYPos = textureYPos;
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition
                    && x < this.xPosition + this.width
                    && y < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(
                    this.xPosition,
                    this.yPosition,
                    textureXPos,
                    textureYPos + k * height,
                    this.width,
                    this.height);

            this.mouseDragged(mc, x, y);
            int l = 14737632;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.field_146123_n) {
                l = 16777120;
            }

            this.drawCenteredString(
                    fontrenderer,
                    this.displayString,
                    this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2,
                    l);
        }
    }
}
