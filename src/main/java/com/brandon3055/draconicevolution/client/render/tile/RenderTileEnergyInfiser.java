package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;

public class RenderTileEnergyInfiser extends TileEntitySpecialRenderer {

    private final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/EnergyInfuserTextureSheet.png");

    private static float pxl = 1F / 256F;

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y, (float) z);
        TileEnergyInfuser tile = (TileEnergyInfuser) tileEntity;
        renderBlock(tile);

        GL11.glPopMatrix();
    }

    public void renderBlock(TileEnergyInfuser tile) {
        Tessellator tessellator = Tessellator.instance;
        bindTexture(texture);

        tessellator.setColorRGBA(255, 255, 255, 255);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        drawBase(tessellator);
        drawWings(tessellator, tile);
        renderChargingItem(tile);
    }

    private void drawBase(Tessellator tess) {
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

    private void drawWings(Tessellator tess, TileEnergyInfuser tile) {
        GL11.glPushMatrix();
        float srcXMin = 0F; // iicon.getMinU();
        float srcYMin = 64F * pxl; // iicon.getMaxU();
        float srcXMax = 92F * pxl; // iicon.getMinV();
        float srcYMax = 115F * pxl; // iicon.getMaxV();

        GL11.glTranslatef(-0.64F, 0.365F, 0.51F);
        GL11.glScalef(0.7F, 0.7F, 0.7F);
        float xTrans = 0.025F;

        {
            GL11.glTranslatef(1.62F, 0F, -xTrans);
            GL11.glRotatef(tile.rotation, 0F, 1F, 0F);
            GL11.glTranslatef(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GL11.glTranslatef(1.62F, 0F, -xTrans);
            GL11.glRotatef(90, 0F, 1F, 0F);
            GL11.glTranslatef(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GL11.glTranslatef(1.62F, 0F, -xTrans);
            GL11.glRotatef(90, 0F, 1F, 0F);
            GL11.glTranslatef(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GL11.glTranslatef(1.62F, 0F, -xTrans);
            GL11.glRotatef(90, 0F, 1F, 0F);
            GL11.glTranslatef(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            srcXMin = 64F * pxl; // iicon.getMinU();
            srcYMin = 24F * pxl; // iicon.getMaxU();
            srcXMax = 96F * pxl; // iicon.getMinV();
            srcYMax = 56F * pxl; // iicon.getMaxV();
            GL11.glTranslatef(1.26F, 0F, 0.31F);
            GL11.glTranslatef(0F, 0F, 0F);
            GL11.glRotatef(90, 0F, 1F, 0F);
            GL11.glRotatef(90, 1F, 0F, 0F);
            GL11.glTranslatef(-0F, 0F, 0F);
            GL11.glScalef(1.4F, 1.4F, 1.4F);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 32, 32, 0.0625F);
        }
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

    public void renderChargingItem(TileEnergyInfuser tile) {
        if (tile.getStackInSlot(0) != null) {
            GL11.glPushMatrix();

            ItemStack stack = tile.getStackInSlot(0);
            EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, tile.getStackInSlot(0));
            itemEntity.hoverStart = 0.0F;

            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glScalef(1F, 1F, 1F);
            if (tile.getWorldObj().getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("doDaylightCycle"))
                GL11.glRotatef(tile.getWorldObj().getWorldTime(), 0F, -1F, 0F);
            else GL11.glRotatef(tile.rotation, 0F, -1F, 0F);
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
