package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.tileentities.TileUpgradeModifier;

public class RenderTileUpgradeModifier extends TileEntitySpecialRenderer {

    private static float pxl = 1F / 256F;

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        TileUpgradeModifier tile = (TileUpgradeModifier) tileEntity;
        renderBlock(tile, f);

        GL11.glPopMatrix();
    }

    public void renderBlock(TileUpgradeModifier tile, float pt) {
        Tessellator tess = Tessellator.instance;
        tess.setColorRGBA(255, 255, 255, 255);

        ResourceHandler.bindResource("textures/models/upgradeModifierGear.png");
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glScaled(0.8, 0.8, 0.8);
        GL11.glRotatef(90, 1F, 0F, 0F);
        GL11.glTranslatef(-0.5F * 0.75F, -0.5F * 0.75F, -0.47F);
        GL11.glTranslated(1, 1, 0);
        GL11.glRotatef(tile.rotation + (pt * tile.rotationSpeed), 0F, 0F, 1F);
        GL11.glTranslated(-1, -1, 0);
        render2DWithThicness(tess, 1, 0, 0, 1, 128, 128, 0.0625F);

        GL11.glPopMatrix();

        GL11.glColor4f(0, 1F, 1F, 1F);

        GL11.glPushMatrix();

        GL11.glScaled(0.4, 0.4, 0.4);
        GL11.glRotatef(90, 1F, 0F, 0F);
        GL11.glTranslatef(0.25F, 0.25F, -0.945F);
        GL11.glTranslated(1, 1, 0);
        GL11.glRotatef(-tile.rotation + (pt * -tile.rotationSpeed), 0F, 0F, 1F);
        GL11.glTranslated(-1, -1, 0);
        render2DWithThicness(tess, 1, 0, 0, 1, 128, 128, 0.0625F);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glPopMatrix();

        drawBase(tess);
        renderChargingItem(tile, pt);
    }

    private void drawBase(Tessellator tess) {
        ResourceHandler.bindResource("textures/models/EnergyInfuserTextureSheet.png");
        GL11.glPushMatrix();
        tess.startDrawingQuads();
        tess.setNormal(0F, 0F, 1.0F);

        double srcXMin = 0D;
        double srcYMin = 0D;
        double srcXMax = 64D * pxl;
        double srcYMax = 64D * pxl;

        tess.addVertexWithUV(0, 0.0005D, 0, srcXMin, srcYMin);
        tess.addVertexWithUV(1, 0.0005D, 0, srcXMax, srcYMin);
        tess.addVertexWithUV(1, 0.0005D, 1, srcXMax, srcYMax);
        tess.addVertexWithUV(0, 0.0005D, 1, srcXMin, srcYMax);

        srcXMin = 128D * pxl;
        srcYMin = 0D;
        srcXMax = 192D * pxl;
        srcYMax = 64D * pxl;

        tess.addVertexWithUV(0, 0.3745D, 0, srcXMin, srcYMin);
        tess.addVertexWithUV(0, 0.3745D, 1, srcXMax, srcYMin);
        tess.addVertexWithUV(1, 0.3745D, 1, srcXMax, srcYMax);
        tess.addVertexWithUV(1, 0.3745D, 0, srcXMin, srcYMax);

        srcXMin = 64D * pxl;
        srcYMin = 0D;
        srcXMax = 128D * pxl;
        srcYMax = 24D * pxl;

        tess.addVertexWithUV(1, 0, 0, srcXMin, srcYMin);
        tess.addVertexWithUV(0, 0, 0, srcXMax, srcYMin);
        tess.addVertexWithUV(0, 0.375D, 0, srcXMax, srcYMax);
        tess.addVertexWithUV(1, 0.375D, 0, srcXMin, srcYMax);

        tess.addVertexWithUV(1, 0, 1, srcXMin, srcYMin);
        tess.addVertexWithUV(1, 0.375D, 1, srcXMin, srcYMax);
        tess.addVertexWithUV(0, 0.375D, 1, srcXMax, srcYMax);
        tess.addVertexWithUV(0, 0, 1, srcXMax, srcYMin);

        tess.addVertexWithUV(0, 0.375D, 1, srcXMin, srcYMin);
        tess.addVertexWithUV(0, 0.375D, 0, srcXMax, srcYMin);
        tess.addVertexWithUV(0, 0, 0, srcXMax, srcYMax);
        tess.addVertexWithUV(0, 0, 1, srcXMin, srcYMax);

        tess.addVertexWithUV(1, 0.375D, 1, srcXMin, srcYMin);
        tess.addVertexWithUV(1, 0, 1, srcXMin, srcYMax);
        tess.addVertexWithUV(1, 0, 0, srcXMax, srcYMax);
        tess.addVertexWithUV(1, 0.375D, 0, srcXMax, srcYMin);

        tess.draw();
        GL11.glPopMatrix();
    }

    public static void render2DWithThicness(Tessellator tess, float maxU, float minV, float minU, float maxV, int width,
            int height, float thickness) {
        double pix = 1D / 64D;
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, 1.0F);
        tess.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) maxU, (double) maxV);
        tess.addVertexWithUV(width * pix, 0.0D, 0.0D, (double) minU, (double) maxV);
        tess.addVertexWithUV(width * pix, height * pix, 0.0D, (double) minU, (double) minV);
        tess.addVertexWithUV(0.0D, height * pix, 0.0D, (double) maxU, (double) minV);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, -1.0F);
        tess.addVertexWithUV(0.0D, height * pix, (double) (0.0F - thickness), (double) maxU, (double) minV);
        tess.addVertexWithUV(width * pix, height * pix, (double) (0.0F - thickness), (double) minU, (double) minV);
        tess.addVertexWithUV(width * pix, 0.0D, (double) (0.0F - thickness), (double) minU, (double) maxV);
        tess.addVertexWithUV(0.0D, 0.0D, (double) (0.0F - thickness), (double) maxU, (double) maxV);
        tess.draw();
        float f5 = 0.5F * (maxU - minU) / (float) width;
        float f6 = 0.5F * (maxV - minV) / (float) height;
        tess.startDrawingQuads();
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;
        double d;

        for (k = 0; k < width; ++k) {
            d = k * pix;
            f7 = (float) k / (float) width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            tess.addVertexWithUV(d, 0.0D, (double) (0.0F - thickness), (double) f8, (double) maxV);
            tess.addVertexWithUV(d, 0.0D, 0.0D, (double) f8, (double) maxV);
            tess.addVertexWithUV(d, height * pix, 0.0D, (double) f8, (double) minV);
            tess.addVertexWithUV(d, height * pix, (double) (0.0F - thickness), (double) f8, (double) minV);
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(1.0F, 0.0F, 0.0F);

        for (k = 0; k < width; ++k) {
            d = (k + 1) * pix;
            f7 = (float) k / (float) width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            tess.addVertexWithUV(d, height * pix, (double) (0.0F - thickness), (double) f8, (double) minV);
            tess.addVertexWithUV(d, height * pix, 0.0D, (double) f8, (double) minV);
            tess.addVertexWithUV(d, 0.0D, 0.0D, (double) f8, (double) maxV);
            tess.addVertexWithUV(d, 0.0D, (double) (0.0F - thickness), (double) f8, (double) maxV);
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < height; ++k) {
            d = (k + 1) * pix;
            f7 = (float) k / (float) height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            tess.addVertexWithUV(0.0D, d, 0.0D, (double) maxU, (double) f8);
            tess.addVertexWithUV(width * pix, d, 0.0D, (double) minU, (double) f8);
            tess.addVertexWithUV(width * pix, d, (double) (0.0F - thickness), (double) minU, (double) f8);
            tess.addVertexWithUV(0.0D, d, (double) (0.0F - thickness), (double) maxU, (double) f8);
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < height; ++k) {
            d = k * pix;
            f7 = (float) k / (float) height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            tess.addVertexWithUV(width * pix, d, 0.0D, (double) minU, (double) f8);
            tess.addVertexWithUV(0.0D, d, 0.0D, (double) maxU, (double) f8);
            tess.addVertexWithUV(0.0D, d, (double) (0.0F - thickness), (double) maxU, (double) f8);
            tess.addVertexWithUV(width * pix, d, (double) (0.0F - thickness), (double) minU, (double) f8);
        }

        tess.draw();
    }

    public void renderChargingItem(TileUpgradeModifier tile, float pt) {
        if (tile.getStackInSlot(0) != null) {
            GL11.glPushMatrix();

            ItemStack stack = tile.getStackInSlot(0);
            EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, tile.getStackInSlot(0));
            itemEntity.hoverStart = 0.0F;

            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glScalef(1F, 1F, 1F);

            GL11.glRotatef((tile.rotation + (pt * tile.rotationSpeed)) * 0.2F, 0F, -1F, 0F);
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
