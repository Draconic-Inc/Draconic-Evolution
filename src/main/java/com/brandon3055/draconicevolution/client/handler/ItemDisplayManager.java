package com.brandon3055.draconicevolution.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class ItemDisplayManager {

    private final int ticks;
    private ItemStack itemStack;
    private int ticksCounter;
    private static final RenderItem renderItem = new RenderItem();
    private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    public ItemDisplayManager(int ticks) {
        this.ticks = ticks;
    }

    public void startDrawing(ItemStack itemStack) {
        this.itemStack = itemStack;
        ticksCounter = ticks;
    }

    public void tick() {
        if (--ticksCounter == 0) {
            itemStack = null;
        }
    }

    public void drawItemStack(ScaledResolution resolution) {
        if (ticksCounter > 0 && itemStack != null && itemStack.getItem() != null) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            GL11.glPushMatrix();
            final int y = resolution.getScaledHeight();
            GL11.glTranslatef(7.0f, y * 0.25f, 0);
            renderItem.renderItemAndEffectIntoGUI(fontRenderer, textureManager, itemStack, 0, 0);
            GL11.glPopMatrix();

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
