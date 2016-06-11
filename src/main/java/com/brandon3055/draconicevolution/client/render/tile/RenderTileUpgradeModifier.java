package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileUpgradeModifier;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderTileUpgradeModifier extends TESRBase<TileUpgradeModifier>
{


	private static float pxl = 1F / 256F;

    @Override
    public void renderTileEntityAt(TileUpgradeModifier te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
		renderBlock(te, partialTicks);

        GlStateManager.popMatrix();
	}

	public void renderBlock(TileUpgradeModifier tile, float pt)
	{
		Tessellator tess = Tessellator.getInstance();

//
		ResourceHelperDE.bindTexture("textures/models/upgradeModifierGear.png");
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();

        GlStateManager.scale(0.8, 0.8, 0.8);
        GlStateManager.rotate(90, 1F, 0F, 0F);
        GlStateManager.translate(-0.5F * 0.75F, -0.5F * 0.75F, -0.47F);
        GlStateManager.translate(1, 1, 0);
        GlStateManager.rotate(tile.rotation + (pt * tile.rotationSpeed), 0F, 0F, 1F);
        GlStateManager.translate(-1, -1, 0);
		render2DWithThicness(tess, 1, 0, 0, 1, 128, 128, 0.0625F);

        GlStateManager.popMatrix();

        GlStateManager.color(0, 1F, 1F, 1F);

        GlStateManager.pushMatrix();

        GlStateManager.scale(0.4, 0.4, 0.4);
        GlStateManager.rotate(90, 1F, 0F, 0F);
        GlStateManager.translate(0.25F, 0.25F, -0.945F);
        GlStateManager.translate(1, 1, 0);
        GlStateManager.rotate(-tile.rotation + (pt * -tile.rotationSpeed), 0F, 0F, 1F);
        GlStateManager.translate(-1, -1, 0);
		render2DWithThicness(tess, 1, 0, 0, 1, 128, 128, 0.0625F);
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();


		drawBase(tess);
		renderChargingItem(tile, pt);

	}

	private void drawBase(Tessellator tess){
//		ResourceHandler.bindResource("textures/models/EnergyInfuserTextureSheet.png");
//		GLStateManager.glPushMatrix();
//		tess.startDrawingQuads();
//		tess.setNormal(0F, 0F, 1.0F);
//
//		double srcXMin = 0D;
//		double srcYMin = 0D;
//		double srcXMax = 64D * pxl;
//		double srcYMax = 64D * pxl;
//
//		tess.addVertexWithUV(0, 0.0005D, 0, srcXMin, srcYMin);
//		tess.addVertexWithUV(1, 0.0005D, 0, srcXMax, srcYMin);
//		tess.addVertexWithUV(1, 0.0005D, 1, srcXMax, srcYMax);
//		tess.addVertexWithUV(0, 0.0005D, 1, srcXMin, srcYMax);
//
//
//		srcXMin = 128D * pxl;
//		srcYMin = 0D;
//		srcXMax = 192D * pxl;
//		srcYMax = 64D * pxl;
//
//		tess.addVertexWithUV(0, 0.3745D, 0, srcXMin, srcYMin);
//		tess.addVertexWithUV(0, 0.3745D, 1, srcXMax, srcYMin);
//		tess.addVertexWithUV(1, 0.3745D, 1, srcXMax, srcYMax);
//		tess.addVertexWithUV(1, 0.3745D, 0, srcXMin, srcYMax);
//
//		srcXMin = 64D * pxl;
//		srcYMin = 0D;
//		srcXMax = 128D * pxl;
//		srcYMax = 24D * pxl;
//
//		tess.addVertexWithUV(1, 0, 0, srcXMin, srcYMin);
//		tess.addVertexWithUV(0, 0, 0, srcXMax, srcYMin);
//		tess.addVertexWithUV(0, 0.375D, 0, srcXMax, srcYMax);
//		tess.addVertexWithUV(1, 0.375D, 0, srcXMin, srcYMax);
//
//		tess.addVertexWithUV(1, 0, 1,      srcXMin, srcYMin);
//		tess.addVertexWithUV(1, 0.375D, 1, srcXMin, srcYMax);
//		tess.addVertexWithUV(0, 0.375D, 1, srcXMax, srcYMax);
//		tess.addVertexWithUV(0, 0, 1,      srcXMax, srcYMin);
//
//		tess.addVertexWithUV(0, 0.375D, 1, srcXMin, srcYMin);
//		tess.addVertexWithUV(0, 0.375D, 0, srcXMax, srcYMin);
//		tess.addVertexWithUV(0, 0, 0, srcXMax, srcYMax);
//		tess.addVertexWithUV(0, 0, 1, srcXMin, srcYMax);
//
//		tess.addVertexWithUV(1, 0.375D, 1, srcXMin, srcYMin);
//		tess.addVertexWithUV(1, 0, 1, srcXMin, srcYMax);
//		tess.addVertexWithUV(1, 0, 0, srcXMax, srcYMax);
//		tess.addVertexWithUV(1, 0.375D, 0, srcXMax, srcYMin);
//
//		tess.draw();
//		GLStateManager.glPopMatrix();

	}

	public static void render2DWithThicness(Tessellator tess, float maxU, float minV, float minU, float maxV, int width, int height, float thickness)
	{
        double pix = 1D / 64D;
        VertexBuffer buffer = tess.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0.0D, 0.0D, 0.0D).tex(maxU, maxV).endVertex();
        buffer.pos(width*pix, 0.0D, 0.0D).tex(minU, maxV).endVertex();
        buffer.pos(width*pix, height*pix, 0.0D).tex(minU, minV).endVertex();
        buffer.pos(0.0D, height*pix, 0.0D).tex(maxU, minV).endVertex();

        buffer.pos(0.0D, height*pix, (double) (0.0F - thickness)).tex(maxU, minV).endVertex();
        buffer.pos(width*pix, height*pix, (double) (0.0F - thickness)).tex(minU, minV).endVertex();
        buffer.pos(width*pix, 0.0D, (double)(0.0F - thickness)).tex(minU, maxV).endVertex();
        buffer.pos(0.0D, 0.0D, (double)(0.0F - thickness)).tex(maxU, maxV).endVertex();

        float f5 = 0.5F * (maxU - minU) / (float)width;
        float f6 = 0.5F * (maxV - minV) / (float)height;
        int k;
        float f7;
        float f8;
        double d;

        for (k = 0; k < width; ++k)
        {
            d = k*pix;
            f7 = (float)k / (float)width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            buffer.pos(d, 0.0D, (double) (0.0F - thickness)).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, 0.0D, 0.0D).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, height * pix, 0.0D).tex((double) f8, (double) minV).endVertex();
            buffer.pos(d, height * pix, (double) (0.0F - thickness)).tex((double) f8, (double) minV).endVertex();
        }

        for (k = 0; k < width; ++k)
        {
            d = (k+1)*pix;
            f7 = (float)k / (float)width;
            f8 = maxU + (minU - maxU) * f7 - f5;
            buffer.pos(d, height * pix, (double) (0.0F - thickness)).tex((double) f8, (double) minV).endVertex();
            buffer.pos(d, height * pix, 0.0D).tex( (double) f8, (double) minV).endVertex();
            buffer.pos(d, 0.0D, 0.0D).tex((double) f8, (double) maxV).endVertex();
            buffer.pos(d, 0.0D, (double) (0.0F - thickness)).tex((double) f8, (double)maxV).endVertex();
        }

        for (k = 0; k < height; ++k)
        {
            d = (k+1)*pix;
            f7 = (float)k / (float)height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            buffer.pos(0.0D, d, 0.0D).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(width * pix, d, 0.0D).tex((double) minU, (double) f8).endVertex();
            buffer.pos(width * pix, d, (double) (0.0F - thickness)).tex((double) minU, (double) f8).endVertex();
            buffer.pos(0.0D, d, (double)(0.0F - thickness)).tex((double) maxU, (double) f8).endVertex();
        }


        for (k = 0; k < height; ++k)
        {
            d = k*pix;
            f7 = (float)k / (float)height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            buffer.pos(width*pix, d, 0.0D).tex((double) minU, (double) f8).endVertex();
            buffer.pos(0.0D, d, 0.0D).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(0.0D, d, (double)(0.0F - thickness)).tex((double) maxU, (double) f8).endVertex();
            buffer.pos(width*pix, d, (double)(0.0F - thickness)).tex( (double)minU, (double)f8).endVertex();
        }

        tess.draw();

	}

	public void renderChargingItem(TileUpgradeModifier tile, float partialTicks){
        if (tile.getStackInSlot(0) != null) {
            GlStateManager.pushMatrix();
//
            ItemStack stack = tile.getStackInSlot(0);

//
            GlStateManager.translate(0.5F, 0.7F, 0.5F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);

//            if (tile.getWorldObj().getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("doDaylightCycle"))
//                GL11.glRotatef(tile.getWorldObj().getWorldTime(), 0F, -1F, 0F);
//            else

            GlStateManager.rotate((tile.rotation + (partialTicks * tile.rotationSpeed)) * 0.2F, 0F, -1F, 0F);
//            if (stack.getItem() instanceof ItemBlock)
//            {
//                GL11.glScalef(1F, 1F, 1F);
//                GL11.glTranslatef(0F, 0.045F, 0.0f);
//            }
//
            renderItem(stack);
            GlStateManager.popMatrix();
        }
	}
}
