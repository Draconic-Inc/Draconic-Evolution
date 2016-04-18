package com.brandon3055.draconicevolution.client.tile;

import com.brandon3055.brandonscore.utills.ModelUtills;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.EnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 *  Created by brandon3055 on 2/4/2016.
 */
public class RenderTileEnergyStorageCore extends TileEntitySpecialRenderer<TileEnergyStorageCore> {
    private static final double[] scales = {1.1, 1.7, 2.3, 3.6, 5.5, 7.1, 8.6, 10.2};

    public RenderTileEnergyStorageCore() {
    }

    @Override
    public void renderTileEntityAt(TileEnergyStorageCore te, double x, double y, double z, float partialTicks, int destroyStage) {

        //region Build Guide

        if (te.buildGuide.value){
            GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            te.coreStructure.renderTier(te.tier.value);
            GlStateManager.popMatrix();
        }
        if (!te.active.value) return;

        //endregion

        float rotation = (ClientEventHandler.elapsedTicks + (partialTicks)) / 2F;
        float brightness = (float) Math.abs(Math.sin((float) ClientEventHandler.elapsedTicks / 100f) * 100f);
        double scale = scales[te.tier.value-1];

        double colour = 1D - ((double)te.getExtendedStorage() / (double)te.getExtendedCapacity());
        float red = 1F;
        float green = (float)colour * 0.3f;
        float blue = (float)colour * 0.7f;

        if (te.tier.value == 8){
            red = 1F;
            green = 0.28F;
            blue = 0.05F;
        }

        //region Render Core

        Tessellator tessellator = Tessellator.getInstance();

        GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);

        double innerTrans = 0.5D;

        GlStateManager.translate(innerTrans, innerTrans, innerTrans);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-innerTrans, -innerTrans, -innerTrans);

        GlStateManager.pushMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 80f + brightness, 80f + brightness);
        GlStateManager.translate(innerTrans, innerTrans, innerTrans);
        GlStateManager.rotate(rotation, 0F, 1F, 0.5F);
        GlStateManager.translate(-innerTrans, -innerTrans, -innerTrans);

        List<BakedQuad> innerQuads = ModelUtills.getModelQuads(DEFeatures.energyStorageCore.getDefaultState().withProperty(EnergyStorageCore.RENDER_TYPE, 1));
        ModelUtills.renderQuadsRGB(tessellator, innerQuads, red, green, blue);

        GlStateManager.popMatrix();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate(innerTrans, innerTrans, innerTrans);
        GlStateManager.rotate(rotation, 0F, -1F, -0.5F);
        GlStateManager.translate(-innerTrans, -innerTrans, -innerTrans);

        List<BakedQuad> outerQuads = ModelUtills.getModelQuads(DEFeatures.energyStorageCore.getDefaultState().withProperty(EnergyStorageCore.RENDER_TYPE, 2));
        if (te.tier.value == 8){
            ModelUtills.renderQuadsRGB(tessellator, outerQuads, 0.95F, 0.45F, 0F);
        }
        else{
            ModelUtills.renderQuadsRGB(tessellator, outerQuads, 0.2F, 1F, 1F);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        //endregion
    }
}
