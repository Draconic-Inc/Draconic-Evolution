package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.uv.IconTransformation;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 2/4/2016.
 */
public class RenderTileEnergyCore extends TESRBase<TileEnergyCore> {
    private static final double[] SCALES = {1.1, 1.7, 2.3, 3.6, 5.5, 7.1, 8.6, 10.2};

    private static CCModel modelStabilizerSphere;
    private static CCModel modelEnergyCore;

    public RenderTileEnergyCore() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/stabilizer_sphere.obj"));
        modelStabilizerSphere = CCModel.combine(map.values());
//        modelStabilizerSphere.apply(new Scale(0.35, 0.35, 0.35));
        modelStabilizerSphere.computeNormals();

        map = OBJParser.parseModels(ResourceHelperDE.getResource("models/energy_core_model.obj"));
        modelEnergyCore = CCModel.combine(map.values());
//        modelEnergyCore.apply(new Scale(0.65, 0.65, 0.65));
        modelEnergyCore.computeNormals();
    }

    @Override
    public void render(TileEnergyCore te, double x, double y, double z, float partialTicks, int destroyStage) {
        //region Build Guide

        if (te.buildGuide.get() /*&& MinecraftForgeClient.getRenderPass() == 0*/) {
            GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);
            te.coreStructure.renderTier(te.tier.get());
            GlStateManager.popMatrix();
        }
        if (!te.active.get()) return;

        //endregion

        //region Do Calculations
        float rotation = (ClientEventHandler.elapsedTicks + partialTicks) / 2F;
        float brightness = (float) Math.abs(Math.sin((float) ClientEventHandler.elapsedTicks / 100f) * 100f);
        double scale = SCALES[te.tier.get() - 1];

        double colour = 1D - ((double) te.getExtendedStorage() / (double) te.getExtendedCapacity());
        float red = 1F;
        float green = (float) colour * 0.3f;
        float blue = (float) colour * 0.7f;

        if (te.tier.get() == 8) {
            red = 1F;
            green = 0.28F;
            blue = 0.05F;
        }
        //endregion

        //region Render Core
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();

        GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.color4f(red, green, blue, 1F);

        //Render Solid Layer
//        if (MinecraftForgeClient.getRenderPass() == 0) {
//        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        setLighting(80f + brightness);
        GlStateManager.pushMatrix();
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.scaled(scale * 0.65, scale * 0.65, scale * 0.65);
        GlStateManager.rotated(rotation, 0F, 1F, 0.5F);

        IconTransformation iconTransform = new IconTransformation(DETextures.getDETexture("models/energy_core_base"));
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        modelEnergyCore.render(ccrs, iconTransform, new Scale(-1));
        ccrs.draw();

        GlStateManager.popMatrix();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        setLighting(200F);
        renderStabilizers(te, false, partialTicks);


        //Render Transparent Layer
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
        if (te.tier.get() == 8) {
            GlStateManager.color4f(0.95F, 0.45F, 0F, 1F);
        } else {
            GlStateManager.color4f(0.2F, 1F, 1F, 1F);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.scaled(scale * 0.7, scale * 0.7, scale * 0.7);
        GlStateManager.rotated(rotation * 0.5F, 0F, -1F, -0.5F);

        iconTransform = new IconTransformation(DETextures.getDETexture("models/energy_core_overlay"));
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        modelEnergyCore.render(ccrs, iconTransform, new Scale(-1));
        ccrs.draw();
        GlStateManager.popMatrix();

//        }

//        GlStateManager.enableTexture();
//        GlStateManager.depthMask(true);
        renderStabilizers(te, true, partialTicks);
        GlStateManager.disableBlend();

        resetLighting();
        GlStateManager.popMatrix();


        //endregion
    }

    private void renderStabilizers(TileEnergyCore te, boolean renderStage, float partialTick) {
        if (!te.stabilizersOK.get()) {
            return;
        }

        for (ManagedVec3I vec3I : te.stabOffsets) {
            GlStateManager.pushMatrix();
            GlStateManager.translated(-vec3I.get().x + 0.5, -vec3I.get().y + 0.5, -vec3I.get().z + 0.5);

            Direction facing = Direction.getFacingFromVector(vec3I.get().x, vec3I.get().y, vec3I.get().z);//Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, te.multiBlockAxis);
            if (facing.getAxis() == Direction.Axis.X || facing.getAxis() == Direction.Axis.Y) {
                GlStateManager.rotatef(-90F, -facing.getYOffset(), facing.getXOffset(), 0);
            } else if (facing == Direction.SOUTH) {
                GlStateManager.rotatef(180F, 0, 1, 0);
            }

            GlStateManager.rotatef(90, 1, 0, 0);
            GlStateManager.color3f(1, 1, 1);
            renderStabilizerBeam(te, vec3I.get(), renderStage, partialTick);
            if (te.tier.get() >= 5) {
                GlStateManager.scalef(1.2F, 0.5F, 1.2F);
            } else {
                GlStateManager.scaled(0.45, 0.45, 0.45);
            }
//            LogHelper.dev(vec3I);
            renderStabilizer(renderStage, partialTick);
            GlStateManager.popMatrix();
        }
    }

    private void renderStabilizer(boolean renderStage, float partialTick) {
        CCRenderState ccrs = CCRenderState.instance();
        GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
        IconTransformation iconTransform = new IconTransformation(DETextures.getDETexture("models/stabilizer_sphere"));

        if (!renderStage) {
            GlStateManager.scalef(0.9F, 0.9F, 0.9F);
            GlStateManager.rotated(ClientEventHandler.elapsedTicks + partialTick, 0, -1, 0);
            GlStateManager.color3f(0, 1, 1);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
            modelStabilizerSphere.render(ccrs, iconTransform, new Scale(-1));
            ccrs.draw();
        }
        else {
            GlStateManager.rotated((ClientEventHandler.elapsedTicks + partialTick) * 0.5F, 0, 1, 0);
            GlStateManager.scalef(1.1F, 1.1F, 1.1F);
            GlStateManager.color4f(0, 1, 1, 0.5F);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
            modelStabilizerSphere.render(ccrs, iconTransform, new Scale(-1));
            ccrs.draw();
        }
    }

    private void renderStabilizerBeam(TileEnergyCore te, Vec3I vec, boolean renderStage, float partialTick) {
        ResourceHelperDE.bindTexture(DETextures.STABILIZER_BEAM);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        GL11.glPushMatrix();
        GlStateManager.rotated(180, 0, 0, 1);

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

            if (te.tier.get() >= 5) {
                GlStateManager.scaled(3.5, 1, 3.5);
            }
            GlStateManager.translated(-0.5, 0, -0.5);

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
        } else {
            //region Render Outer Beam
            GlStateManager.rotated(90, -1, 0, 0);
            GlStateManager.rotated(45, 0, 0, 1);
            GlStateManager.translated(0, 0, 0.4);
//            GlStateManager.depthMask(true);

            int sides = 4;
            float enlarge = 0.35F;
            if (te.tier.get() >= 5) {
                sides = 12;
                enlarge = 0.5F + ((te.tier.get() - 5) * 0.1F);
                GlStateManager.rotatef((ClientEventHandler.elapsedTicks + partialTick) * 0.6F, 0, 0, -1);
                GlStateManager.scaled(3.5, 3.5, 1);
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

//            GlStateManager.depthMask(true);
            //endregion
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEnergyCore p_188185_1_) {
        return true;
    }
}
