package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.tileentities.TileChaosShard;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosShard extends TileEntitySpecialRenderer {

    public static IModelCustom model = AdvancedModelLoader
            .loadModel(ResourceHandler.getResource("models/chaosCrystal.obj"));

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {
        renderTileEntityAt((TileChaosShard) tile, x, y, z, tick);
    }

    private void renderTileEntityAt(TileChaosShard tile, double x, double y, double z, float tick) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);

        GL11.glRotatef(((float) tile.tick + tick), 0, 1, 0);
        ResourceHandler.bindResource("textures/models/chaosCrystal.png");
        model.renderAll();

        GL11.glPopMatrix();

        if (!tile.guardianDefeated) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, -4.5, 0);
            Tessellator tessellator = Tessellator.instance;
            ResourceHandler.bindTexture(ResourceHandler.getResourceWOP("textures/entity/beacon_beam.png"));
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            float f2 = (float) tile.tick + tick;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDepthMask(false);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(200, 0, 0, 62);
            double size = 0.7F;
            double d30 = 0.2D - size;
            double d4 = 0.2D - size;
            double d6 = 0.8D + size;
            double d8 = 0.2D - size;
            double d10 = 0.2D - size;
            double d12 = 0.8D + size;
            double d14 = 0.8D + size;
            double d16 = 0.8D + size;
            double d18 = 10.0D; // Height
            double d20 = 0.0D;
            double d22 = 1.0D;
            double d24 = (double) (-1.0F + f3);
            double d26 = d18 + d24;
            tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
            tessellator.addVertexWithUV(x + d30, y, z + d4, d22, d24);
            tessellator.addVertexWithUV(x + d6, y, z + d8, d20, d24);
            tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
            tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
            tessellator.addVertexWithUV(x + d14, y, z + d16, d22, d24);
            tessellator.addVertexWithUV(x + d10, y, z + d12, d20, d24);
            tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
            tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
            tessellator.addVertexWithUV(x + d6, y, z + d8, d22, d24);
            tessellator.addVertexWithUV(x + d14, y, z + d16, d20, d24);
            tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
            tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
            tessellator.addVertexWithUV(x + d10, y, z + d12, d22, d24);
            tessellator.addVertexWithUV(x + d30, y, z + d4, d20, d24);
            tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }
}
