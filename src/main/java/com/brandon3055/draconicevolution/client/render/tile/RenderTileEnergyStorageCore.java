package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.machines.EnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by brandon3055 on 2/4/2016.
 */
public class RenderTileEnergyStorageCore extends TESRBase<TileEnergyStorageCore> {
    private static final double[] SCALES = {1.1, 1.7, 2.3, 3.6, 5.5, 7.1, 8.6, 10.2};

    public RenderTileEnergyStorageCore() {
    }

    @Override
    public void render(TileEnergyStorageCore te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //region Build Guide

        if (te.buildGuide.value && MinecraftForgeClient.getRenderPass() == 0) {
            GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            te.coreStructure.renderTier(te.tier.value);
            GlStateManager.popMatrix();
        }
        if (!te.active.value) return;

        //endregion

        //region Do Calculations
        float rotation = (ClientEventHandler.elapsedTicks + (partialTicks)) / 2F;
        float brightness = (float) Math.abs(Math.sin((float) ClientEventHandler.elapsedTicks / 100f) * 100f);
        double scale = SCALES[te.tier.value - 1];

        double colour = 1D - ((double) te.getExtendedStorage() / (double) te.getExtendedCapacity());
        float red = 1F;
        float green = (float) colour * 0.3f;
        float blue = (float) colour * 0.7f;

        if (te.tier.value == 8) {
            red = 1F;
            green = 0.28F;
            blue = 0.05F;
        }
        //endregion

        //region Render Core

        GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        //GlStateManager.disableCull();

        if (MinecraftForgeClient.getRenderPass() == 0) {
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);

            GlStateManager.pushMatrix();
            translateScaleTranslate(0.5D, scale, scale, scale);
            setLighting(80f + brightness);
            translateRotateTranslate(0.5, rotation, 0F, 1F, 0.5F);
            List<BakedQuad> innerQuads = ModelUtils.getModelQuads(DEFeatures.energyStorageCore.getDefaultState().withProperty(EnergyStorageCore.RENDER_TYPE, 1));
            ModelUtils.renderQuadsRGB(innerQuads, red, green, blue);
            GlStateManager.popMatrix();
            setLighting(200F);
            renderStabilizers(te, false, partialTicks);
        }
        else {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            setLighting(200F);
            renderStabilizers(te, true, partialTicks);
            translateScaleTranslate(0.5D, scale, scale, scale);
            translateRotateTranslate(0.5, rotation * 0.5F, 0F, -1F, -0.5F);
            List<BakedQuad> outerQuads = ModelUtils.getModelQuads(DEFeatures.energyStorageCore.getDefaultState().withProperty(EnergyStorageCore.RENDER_TYPE, 2));
            if (te.tier.value == 8) {
                ModelUtils.renderQuadsRGB(outerQuads, 0.95F, 0.45F, 0F);
            }
            else {
                ModelUtils.renderQuadsRGB(outerQuads, 0.2F, 1F, 1F);
            }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();

        resetLighting();
        GlStateManager.popMatrix();

        //endregion
    }

    private void renderStabilizers(TileEnergyStorageCore te, boolean renderStage, float partialTick) {
        if (!te.stabilizersOK.value) {
            return;
        }

        for (ManagedVec3I vec3I : te.stabOffsets) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-vec3I.vec.x + 0.5, -vec3I.vec.y + 0.5, -vec3I.vec.z + 0.5);

            EnumFacing facing = EnumFacing.getFacingFromVector(vec3I.vec.x, vec3I.vec.y, vec3I.vec.z);//EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, te.multiBlockAxis);
            if (facing.getAxis() == EnumFacing.Axis.X || facing.getAxis() == EnumFacing.Axis.Y) {
                GlStateManager.rotate(-90F, -facing.getFrontOffsetY(), facing.getFrontOffsetX(), 0);
            }
            else if (facing == EnumFacing.SOUTH) {
                GlStateManager.rotate(180F, 0, 1, 0);
            }

            GlStateManager.rotate(90, 1, 0, 0);
            renderStabilizerBeam(te, vec3I.vec, renderStage, partialTick);
            if (te.tier.value >= 5) {
                GlStateManager.scale(1.2F, 0.5F, 1.2F);
            }
            else {
                GlStateManager.scale(0.45, 0.45, 0.45);
            }
//            LogHelper.dev(vec3I);
            renderStabilizer(renderStage, partialTick);
            GlStateManager.popMatrix();
        }
    }

    private void renderStabilizer(boolean renderStage, float partialTick) {
        IBakedModel bakedModel = ModelUtils.loadBakedModel(ResourceHelperDE.getResource("block/obj_models/stabilizer_sphere.obj"));
        List<BakedQuad> listQuads = bakedModel.getQuads(DEFeatures.energyStorageCore.getDefaultState(), null, 0);
        GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());
        if (!renderStage) {
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
            GlStateManager.rotate(ClientEventHandler.elapsedTicks + partialTick, 0, -1, 0);
            ModelUtils.renderQuadsARGB(listQuads, 0xFF00FFFF);
        }
        else {
            GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTick) * 0.5F, 0, 1, 0);
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
            ModelUtils.renderQuadsARGB(listQuads, 0x5000FFFF);
        }
    }

    private void renderStabilizerBeam(TileEnergyStorageCore te, Vec3I vec, boolean renderStage, float partialTick) {
        ResourceHelperDE.bindTexture(DETextures.STABILIZER_BEAM);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        GL11.glPushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);

        double beamLength = Math.abs(vec.x + vec.y + vec.z) - 0.5;
        float time = ClientEventHandler.elapsedTicks + partialTick;
        double rotation = (double) time * 0.025D * -1.5D;
        float beamMotion = -time * 0.2F - (float) MathHelper.floor(-time * 0.1F);

        if (!renderStage) {
            //region Render Inner Beam
            double scale = 0.2;
            double d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale;  //x point 1
            double d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale;  //z point 1
            double d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale;        //x point 2
            double d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale;     //z point 2
            double d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale;//Dist from x-3
            double d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
            double d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
            double d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;
            double texXMin = 0.0D;
            double texXMax = 1.0D;
            double d28 = (double) (-1.0F + beamMotion);
            double texHeight = beamLength * (0.5D / scale) + d28;

            if (te.tier.value >= 5) {
                GlStateManager.scale(3.5, 1, 3.5);
            }
            GlStateManager.translate(-0.5, 0, -0.5);

            vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexBuffer.pos(d7, beamLength, d9).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d7, 0, d9).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d11, 0, d13).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d11, beamLength, d13).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d19, beamLength, d21).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d19, 0, d21).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d15, 0, d17).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d15, beamLength, d17).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d11, beamLength, d13).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d11, 0, d13).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d19, 0, d21).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d19, beamLength, d21).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d15, beamLength, d17).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d15, 0, d17).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d7, 0, d9).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d7, beamLength, d9).tex(texXMin, texHeight).endVertex();

            rotation += 0.77f;
            d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale;
            d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale;
            d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale;
            d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale;
            d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale;
            d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
            d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
            d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;

            d28 = (-1F + (beamMotion * 1));
            texHeight = beamLength * (0.5D / scale) + d28;

            vertexBuffer.pos(d7, beamLength, d9).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d7, 0, d9).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d11, 0, d13).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d11, beamLength, d13).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d19, beamLength, d21).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d19, 0, d21).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d15, 0, d17).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d15, beamLength, d17).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d11, beamLength, d13).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d11, 0, d13).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d19, 0, d21).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d19, beamLength, d21).tex(texXMin, texHeight).endVertex();

            vertexBuffer.pos(d15, beamLength, d17).tex(texXMax, texHeight).endVertex();
            vertexBuffer.pos(d15, 0, d17).tex(texXMax, d28).endVertex();
            vertexBuffer.pos(d7, 0, d9).tex(texXMin, d28).endVertex();
            vertexBuffer.pos(d7, beamLength, d9).tex(texXMin, texHeight).endVertex();
            //endregion
        }
        else {
            //region Render Outer Beam
            GlStateManager.rotate(90, -1, 0, 0);
            GlStateManager.rotate(45, 0, 0, 1);
            GlStateManager.translate(0, 0, 0.4);
            GlStateManager.depthMask(true);

            int sides = 4;
            float enlarge = 0.35F;
            if (te.tier.value >= 5) {
                sides = 12;
                enlarge = 0.5F + ((te.tier.value - 5) * 0.1F);
                GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTick) * 0.6F, 0, 0, -1);
                GlStateManager.scale(3.5, 3.5, 1);
            }

            vertexBuffer.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (int i = 0; i < 4; ++i) {
                vertexBuffer.putColorRGBA(i + 1, 255, 255, 255, 32);
            }

            for (int i = 0; i <= sides; i++) {

                float verX = MathHelper.sin((float) (i % sides) * (float) Math.PI * 2F / (float) sides) * 1;
                float verY = MathHelper.cos((float) (i % sides) * (float) Math.PI * 2F / (float) sides) * 1;
                vertexBuffer.pos((double) (verX * 0.35F), (double) (verY * 0.35F), 0.0D).tex((double) i, (beamMotion * 2)).color(255, 255, 255, 32).endVertex();
                vertexBuffer.pos((double) verX * enlarge, (double) verY * enlarge, beamLength).tex((double) i, beamLength + (beamMotion * 2)).color(255, 255, 255, 32).endVertex();
            }

            GlStateManager.depthMask(false);
            //endregion
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEnergyStorageCore p_188185_1_) {
        return true;
    }
}
