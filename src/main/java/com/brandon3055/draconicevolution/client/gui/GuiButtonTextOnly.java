package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 17/09/2014.
 */
public class GuiButtonTextOnly extends GuiButton {

    public String LINKED_PAGE;
    public int textColour;

    public GuiButtonTextOnly(int id, int xPos, int yPos, int width, int hight, String displayString, String linkedPage,
            int colour) {
        super(id, xPos, yPos, width, hight, displayString);
        this.LINKED_PAGE = linkedPage;
        this.textColour = colour;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.mouseDragged(minecraft, mouseX, mouseY);

            String trimmedDisplayString = displayString;
            if (fontrenderer.getStringWidth(displayString) > width + 30 && !this.field_146123_n) {
                int energencyBreak = 0;
                while (fontrenderer.getStringWidth(trimmedDisplayString) * 0.7 > width - 5) {
                    trimmedDisplayString = trimmedDisplayString.substring(0, trimmedDisplayString.length() - 1);
                    energencyBreak++;
                    if (energencyBreak > 100) break;
                }
                trimmedDisplayString += "...";
            }

            if (this.field_146123_n) {
                trimmedDisplayString = EnumChatFormatting.BOLD + "" + EnumChatFormatting.ITALIC + trimmedDisplayString;
                GL11.glPushMatrix();
                GL11.glColor4f(0f, 0f, 0f, 1f);
                drawTexturedModalRect(
                        xPosition + (int) (xPosition * 0.01),
                        yPosition + (int) (yPosition * 0.01),
                        0,
                        46,
                        (int) (fontrenderer.getStringWidth(trimmedDisplayString) * 0.72) + 2,
                        8);
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 1);
            fontrenderer.drawString(
                    trimmedDisplayString,
                    (int) (xPosition * 1.45),
                    (int) ((yPosition + (height - 8) / 2) * 1.45),
                    textColour);
            GL11.glPopMatrix();
        }
    }

    public boolean getIsHovering() {
        return field_146123_n;
    }
}
