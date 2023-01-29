package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.tileentities.TileDissEnchanter;

public class RenderTileDissEnchanter extends TileEntitySpecialRenderer {

    // private final ResourceLocation texture = new ResourceLocation(References.MODID.toLowerCase(),
    // "textures/models/EnergyInfuserTextureSheet.png");

    // private static float pxl = 1F / 256F;

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y, (float) z);
        TileDissEnchanter tile = (TileDissEnchanter) tileEntity;
        renderBlock(tile, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, f);

        GL11.glPopMatrix();
    }

    public void renderBlock(TileDissEnchanter tile, World world, int x, int y, int z, float f) {
        Tessellator tessellator = Tessellator.instance;
        // bindTexture(texture);

        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.setBrightness(200);
        int l = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        tessellator.setColorOpaque_F(1f, 1f, 1f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

        GL11.glPushMatrix();
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glTranslated(0, 1.2, -0.27);
        GL11.glRotatef(180, 1, 0, 0);
        renderItem(tile, 1, f, false);
        GL11.glPopMatrix();
        if (Minecraft.isFancyGraphicsEnabled()) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.4, 0);
            renderItem(tile, 0, f, true);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glTranslated(0, 1.2, -0.37);
            GL11.glRotatef(180, 1, 0, 0);
            renderItem(tile, 0, f, false);
            GL11.glPopMatrix();
        }
    }

    public void renderItem(TileDissEnchanter tile, int i, float f, boolean rotate) {
        if (tile.getStackInSlot(i) != null) {
            GL11.glPushMatrix();

            ItemStack stack = tile.getStackInSlot(i).copy();
            stack.stackSize = 1;
            EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, stack);
            itemEntity.hoverStart = 0.0F;

            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glScalef(1F, 1F, 1F);
            if (rotate) GL11.glRotatef(tile.timer + f, 0F, -1F, 0F);
            if (stack.getItem() instanceof ItemBlock) {
                GL11.glScalef(1F, 1F, 1F);
                GL11.glTranslatef(0F, 0.045F, 0.0f);
            }

            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(itemEntity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;

            GL11.glPopMatrix();
        }
    }
}
