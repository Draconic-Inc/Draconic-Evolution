package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class RenderTileEnergyInfuser extends TESRBase<TileEnergyInfuser> {

//    @Override
//    public void renderTileEntityFast(TileEnergyInfuser te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
////
//        CCRenderState.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
//        CCRenderState.bind(buffer);
//
//        //CCModel.newModel(GL11.GL_QUADS, 80);
//
//
//        CCRenderState.draw();
//    }


    //private final ResourceLocation texture = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/EnergyInfuserTextureSheet.png");

    private static float pxl = 1F / 128F;

    @Override
    public void render(TileEnergyInfuser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();

        GlStateManager.translate((float) x, (float) y, (float) z);

        if (!te.running.get()) {
            partialTicks = 0;
        }

        renderBlock(te, partialTicks);

        GlStateManager.popMatrix();
    }


    public void renderBlock(TileEnergyInfuser tile, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        bindTexture(ResourceHelperDE.getResource(DETextures.ENERGY_INFUSER_DECORATION));

//        tessellator.setColorRGBA(255, 255, 255, 255);

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        drawWings(tessellator, tile, partialTicks);
        renderChargingItem(tile, partialTicks);

    }


    private void drawWings(Tessellator tess, TileEnergyInfuser tile, float partialTicks) {
        GlStateManager.pushMatrix();
        float srcXMin = 0F;        //iicon.getMinU();
        float srcYMin = 0F * pxl;  //iicon.getMaxU();
        float srcXMax = 92F * pxl;  //iicon.getMinV();
        float srcYMax = 51F * pxl; //iicon.getMaxV();

        GlStateManager.translate(-0.64F, 0.365F, 0.51F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
        float xTrans = 0.025F;


        {
            GlStateManager.translate(1.62F, 0F, -xTrans);
            GlStateManager.rotate(tile.rotation + partialTicks, 0F, 1F, 0F);
            GlStateManager.translate(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GlStateManager.translate(1.62F, 0F, -xTrans);
            GlStateManager.rotate(90, 0F, 1F, 0F);
            GlStateManager.translate(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GlStateManager.translate(1.62F, 0F, -xTrans);
            GlStateManager.rotate(90, 0F, 1F, 0F);
            GlStateManager.translate(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            GlStateManager.translate(1.62F, 0F, -xTrans);
            GlStateManager.rotate(90, 0F, 1F, 0F);
            GlStateManager.translate(-1.62F, 0F, xTrans);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 92, 51, 0.0625F);
        }
        {
            srcXMin = 0F * pxl;        //iicon.getMinU();
            srcYMin = 51F * pxl;  //iicon.getMaxU();
            srcXMax = 33F * pxl;  //iicon.getMinV();
            srcYMax = 83F * pxl; //iicon.getMaxV();
            GlStateManager.translate(1.26F, 0F, 0.31F);
            GlStateManager.translate(0F, 0F, 0F);
            GlStateManager.rotate(90, 0F, 1F, 0F);
            GlStateManager.rotate(90, 1F, 0F, 0F);
            GlStateManager.translate(-0F, 0F, 0F);
            GlStateManager.scale(1.4F, 1.4F, 1.4F);
            render2DWithThicness(tess, srcXMax, srcYMin, srcXMin, srcYMax, 32, 32, 0.0625F);
        }
        GlStateManager.popMatrix();
    }

    public static void render2DWithThicness(Tessellator tess, float maxU, float minV, float minU, float maxV, int width, int height, float thickness) {
        double pix = 1D / 64D;
        BufferBuilder buffer = tess.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0.0D, 0.0D, 0.0D).tex(maxU, maxV).endVertex();
        buffer.pos(width * pix, 0.0D, 0.0D).tex(minU, maxV).endVertex();
        buffer.pos(width * pix, height * pix, 0.0D).tex(minU, minV).endVertex();
        buffer.pos(0.0D, height * pix, 0.0D).tex(maxU, minV).endVertex();

        buffer.pos(0.0D, height * pix, (double) (0.0F - thickness)).tex(maxU, minV).endVertex();
        buffer.pos(width * pix, height * pix, (double) (0.0F - thickness)).tex(minU, minV).endVertex();
        buffer.pos(width * pix, 0.0D, (double) (0.0F - thickness)).tex(minU, maxV).endVertex();
        buffer.pos(0.0D, 0.0D, (double) (0.0F - thickness)).tex(maxU, maxV).endVertex();

        float f5 = 0.5F * (maxU - minU) / (float) width;
        float f6 = 0.5F * (maxV - minV) / (float) height;
        int k;
        float f7;
        float f8;
        double d;

        for (k = 0; k < width; ++k) {
            d = k * pix;
            f7 = (float) k / (float) width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            buffer.pos(d, 0.0D, (double) (0.0F - thickness)).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, 0.0D, 0.0D).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, height * pix, 0.0D).tex((double) f8, (double) minV).endVertex();
            buffer.pos(d, height * pix, (double) (0.0F - thickness)).tex((double) f8, (double) minV).endVertex();
        }

        for (k = 0; k < width; ++k) {
            d = (k + 1) * pix;
            f7 = (float) k / (float) width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            buffer.pos(d, height * pix, (double) (0.0F - thickness)).tex((double) f8, (double) minV).endVertex();
            buffer.pos(d, height * pix, 0.0D).tex((double) f8, (double) minV).endVertex();
            buffer.pos(d, 0.0D, 0.0D).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, 0.0D, (double) (0.0F - thickness)).tex((double) f8, (double) maxV).endVertex();
        }

        for (k = 0; k < height; ++k) {
            d = (k + 1) * pix;
            f7 = (float) k / (float) height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            buffer.pos(0.0D, d, 0.0D).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(width * pix, d, 0.0D).tex((double) minU, (double) f8).endVertex();
            buffer.pos(width * pix, d, (double) (0.0F - thickness)).tex((double) minU, (double) f8).endVertex();
            buffer.pos(0.0D, d, (double) (0.0F - thickness)).tex((double) maxU, (double) f8).endVertex();
        }


        for (k = 0; k < height; ++k) {
            d = k * pix;
            f7 = (float) k / (float) height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            buffer.pos(width * pix, d, 0.0D).tex((double) minU, (double) f8).endVertex();
            buffer.pos(0.0D, d, 0.0D).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(0.0D, d, (double) (0.0F - thickness)).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(width * pix, d, (double) (0.0F - thickness)).tex((double) minU, (double) f8).endVertex();
        }

        tess.draw();

    }

    public void renderChargingItem(TileEnergyInfuser tile, float partialTicks) {
        if (tile.getStackInSlot(0) != null) {
            GlStateManager.pushMatrix();
//
            ItemStack stack = tile.getStackInSlot(0);

//
            GlStateManager.translate(0.5F, 0.7F, 0.5F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
//            if (tile.getWorldObj().getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("doDaylightCycle"))
//                GL11.rotate(tile.getWorldObj().getWorldTime(), 0F, -1F, 0F);
//            else
            GlStateManager.rotate(tile.rotation + partialTicks, 0F, -1F, 0F);
//            if (stack.getItem() instanceof ItemBlock)
//            {
//                GL11.scale(1F, 1F, 1F);
//                GL11.translate(0F, 0.045F, 0.0f);
//            }
//
            renderItem(stack);
            GlStateManager.popMatrix();
        }
    }
}
