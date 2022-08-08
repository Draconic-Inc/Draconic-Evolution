package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTileParticleGen extends TileEntitySpecialRenderer {

    private final ResourceLocation texture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/ParticleGenTextureSheet.png");
    private final ResourceLocation beamTexture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/stabilizer_beam.png");
    private static final ResourceLocation modelTexture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/stabilizer_sphere.png");
    private IModelCustom stabilizerSphereModel;

    private float pxl = 1F / 64;

    public RenderTileParticleGen() {
        stabilizerSphereModel = AdvancedModelLoader.loadModel(
                new ResourceLocation(References.MODID.toLowerCase(), "models/stabilizer_sphere.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y, (float) z);
        TileParticleGenerator tileEntityGen = (TileParticleGenerator) tileEntity;
        renderBlock(tileEntityGen, f);

        GL11.glPopMatrix();
    }

    public void renderBlock(TileParticleGenerator tl, float f3) {
        Tessellator tessellator = Tessellator.instance;

        boolean inverted = tl.inverted;
        boolean stabilizerMode = tl.stabalizerMode;

        GL11.glPushMatrix();

        // GL11.glDisable(GL11.GL_LIGHTING);
        bindTexture(texture);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.setColorRGBA(255, 255, 255, 255);

        { // Draw Corners
            float f = 0.4F;
            drawCornerCube(tessellator, f, f, f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, f, -f, f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, -f, f, -f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, -f, -f, -f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, -f, f, f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, f, f, -f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, f, -f, -f, 1F - f, inverted, stabilizerMode);
            drawCornerCube(tessellator, -f, -f, f, 1F - f, inverted, stabilizerMode);
        }
        { // Draw Beams
            float f = 0.45F;
            float f2 = 0.4F;
            drawBeamX(tessellator, 0, f2, f2, 1F - f);
            drawBeamX(tessellator, 0, -f2, f2, 1F - f);
            drawBeamX(tessellator, 0, f2, -f2, 1F - f);
            drawBeamX(tessellator, 0, -f2, -f2, 1F - f);

            drawBeamY(tessellator, f2, 0, f2, 1F - f);
            drawBeamY(tessellator, -f2, 0, f2, 1F - f);
            drawBeamY(tessellator, f2, 0, -f2, 1F - f);
            drawBeamY(tessellator, -f2, 0, -f2, 1F - f);

            drawBeamZ(tessellator, f2, f2, 0, 1F - f);
            drawBeamZ(tessellator, -f2, f2, 0, 1F - f);
            drawBeamZ(tessellator, f2, -f2, 0, 1F - f);
            drawBeamZ(tessellator, -f2, -f2, 0, 1F - f);
        }

        tessellator.draw();
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        if (stabilizerMode) {
            drawEnergyBeam(tessellator, tl, f3);
        }

        if (tl.beam_enabled) preRenderBeam(tessellator, tl, f3);
    }

    private void drawEnergyBeam(Tessellator tess, TileParticleGenerator gen, float f) {
        TileEnergyStorageCore master = gen.getMaster();
        if (master == null) return;
        float length = 0;

        GL11.glPushMatrix();

        if (master.xCoord > gen.xCoord) {
            GL11.glRotatef(-90F, 0F, 0F, 1F);
            GL11.glTranslated(-1, 0.5, 0);
            length = master.xCoord - gen.xCoord - 0.2F;
        } else if (master.xCoord < gen.xCoord) {
            GL11.glRotatef(90F, 0F, 0F, 1F);
            GL11.glTranslated(0, -0.5, 0);
            length = gen.xCoord - master.xCoord - 0.2F;
        } else if (master.zCoord > gen.zCoord) {
            GL11.glRotatef(90F, 1F, 0F, 0F);
            GL11.glTranslated(0, 0.5, -1);
            length = master.zCoord - gen.zCoord - 0.2F;
        } else if (master.zCoord < gen.zCoord) {
            GL11.glRotatef(-90F, 1F, 0F, 0F);
            GL11.glTranslated(0, -0.5, 0);
            length = gen.zCoord - master.zCoord - 0.2F;
        }

        renderStabilizerSphere(gen);
        renderEnergyBeam(tess, gen, length, f);

        GL11.glPopMatrix();
    }

    public void renderStabilizerSphere(TileParticleGenerator tile) {
        GL11.glPushMatrix();
        // GL11.glColor4f(0.5F, 0.0F, 0.0F, 1F);
        GL11.glColor4f(0.0F, 2.0F, 0.0F, 1F);
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glScalef(0.4F, 0.4F, 0.4F);
        if (!tile.stabalizerMode) {
            float red = (float) tile.beam_red / 255F;
            float green = (float) tile.beam_green / 255F;
            float blue = (float) tile.beam_blue / 255F;
            GL11.glColor4f(red, green, blue, 1F);
            GL11.glScalef(tile.beam_scale, tile.beam_scale, tile.beam_scale);
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(modelTexture);

        GL11.glRotatef(tile.rotation, 0F, 1F, 0F);
        stabilizerSphereModel.renderAll();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glRotatef(tile.rotation * 2, 0F, -1F, 0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glColor4f(0.0F, 1.0F, 1.0F, 0.5F);
        if (!tile.stabalizerMode) {
            float red = (float) tile.beam_red / 255F;
            float green = (float) tile.beam_green / 255F;
            float blue = (float) tile.beam_blue / 255F;
            GL11.glColor4f(red, green, blue, 0.5F);
        }
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glScalef(1.3F, 1.3F, 1.3F);
        stabilizerSphereModel.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
    }

    private void renderEnergyBeam(Tessellator tess, TileParticleGenerator tile, float length, float f) {
        int x = 0;
        int y = 0;
        int z = 0;

        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        bindTexture(beamTexture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);

        // float time = (float)tile.getWorldObj().getTotalWorldTime() + f;
        float time = tile.rotation + f;
        float upMot = -time * 0.2F - (float) MathHelper.floor_float(-time * 0.1F);
        byte scaleMult = 1;
        double rotation = (double) time * 0.025D * (1.0D - (double) (scaleMult & 1) * 2.5D);

        tess.startDrawingQuads();
        tess.setColorRGBA(255, 255, 255, 32);

        double scale = (double) scaleMult * 0.2D;
        double d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale; // x point 1
        double d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale; // z point 1
        double d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale; // x point 2
        double d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale; // z point 2
        double d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale; // Dist from x-3
        double d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
        double d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
        double d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;
        double height = (double) (length);
        double texXMin = 0.0D;
        double texXMax = 1.0D;
        double d28 = (double) (-1.0F + upMot);
        double texHeight = (double) (length) * (0.5D / scale) + d28;

        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMax, texHeight);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMax, d28);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMin, d28);
        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMin, texHeight);

        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMax, texHeight);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMax, d28);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMin, d28);
        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMin, texHeight);

        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMax, texHeight);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMax, d28);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMin, d28);
        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMin, texHeight);

        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMax, texHeight);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMax, d28);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMin, d28);
        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMin, texHeight);

        rotation += 0.77f;
        d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale;
        d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale;
        d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale;
        d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale;
        d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale;
        d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
        d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
        d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;

        d28 = (-1F + (upMot * 1));
        texHeight = (double) (length) * (0.5D / scale) + d28;

        tess.setColorRGBA_F(1.0F, 1.0f, 1.0f, 1f);

        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMax, texHeight);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMax, d28);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMin, d28);
        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMin, texHeight);

        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMax, texHeight);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMax, d28);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMin, d28);
        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMin, texHeight);

        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMax, texHeight);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMax, d28);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMin, d28);
        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMin, texHeight);

        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMax, texHeight);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMax, d28);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMin, d28);
        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMin, texHeight);

        tess.draw();
        GL11.glPushMatrix();

        GL11.glTranslated(0, 0.4, 0);
        length -= 0.5F;

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        tess.startDrawingQuads();
        tess.setColorRGBA(255, 255, 255, 32);
        double d30 = 0.2D;
        double d4 = 0.2D;
        double d6 = 0.8D;
        double d8 = 0.2D;
        double d10 = 0.2D;
        double d12 = 0.8D;
        double d14 = 0.8D;
        double d16 = 0.8D;
        double d18 = (double) (length);
        double d20 = 0.0D;
        double d22 = 1.0D;
        double d24 = (double) (-1.0F + upMot);
        double d26 = (double) (length) + d24;
        tess.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
        tess.addVertexWithUV(x + d30, y, z + d4, d22, d24);
        tess.addVertexWithUV(x + d6, y, z + d8, d20, d24);
        tess.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
        tess.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
        tess.addVertexWithUV(x + d14, y, z + d16, d22, d24);
        tess.addVertexWithUV(x + d10, y, z + d12, d20, d24);
        tess.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
        tess.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
        tess.addVertexWithUV(x + d6, y, z + d8, d22, d24);
        tess.addVertexWithUV(x + d14, y, z + d16, d20, d24);
        tess.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
        tess.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
        tess.addVertexWithUV(x + d10, y, z + d12, d22, d24);
        tess.addVertexWithUV(x + d30, y, z + d4, d20, d24);
        tess.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
        tess.draw();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawCornerCube(
            Tessellator tess, float x, float y, float z, float FP, boolean inverted, boolean stabalizerMode) {
        float srcXMin = inverted ? 38F * pxl : 32F * pxl;
        float srcYMin = 0F;
        float srcXMax = inverted ? 44F * pxl : 38F * pxl;
        float srcYMax = 6 * pxl;
        // float FP = 0.6F; //Scale
        float FN = 1F - FP;

        if (stabalizerMode) {
            srcXMin = 44F * pxl;
            srcXMax = 50F * pxl;
        }

        // X+
        tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMin, srcYMax);

        // X-
        tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMax);

        // Y+
        tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMin, srcYMax);

        // Y-
        tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMax);

        // Z+
        tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMax);

        // Z-
        tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMin, srcYMax);
    }

    private void drawBeamX(Tessellator tess, float x, float y, float z, float FP) {
        float srcXMin = 0;
        float srcYMin = 0F;
        float srcXMax = 32F * pxl;
        float srcYMax = 4 * pxl;
        float FN = 1F - FP;

        float XX = 0.9F;
        float XM = 0.1F;

        // Y+
        tess.addVertexWithUV(XM, FP + y, FN + z, srcXMin, srcYMax);
        tess.addVertexWithUV(XM, FP + y, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(XX, FP + y, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(XX, FP + y, FN + z, srcXMax, srcYMax);

        // Y-
        tess.addVertexWithUV(XM, FN + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(XX, FN + y, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(XX, FN + y, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(XM, FN + y, FP + z, srcXMin, srcYMax);

        // Z+
        tess.addVertexWithUV(XX, FN + y, FP + z, srcXMin, srcYMax);
        tess.addVertexWithUV(XX, FP + y, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(XM, FP + y, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(XM, FN + y, FP + z, srcXMax, srcYMax);

        // Z-
        tess.addVertexWithUV(XM, FN + y, FN + z, srcXMin, srcYMax);
        tess.addVertexWithUV(XM, FP + y, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(XX, FP + y, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(XX, FN + y, FN + z, srcXMax, srcYMax);
    }

    private void drawBeamY(Tessellator tess, float x, float y, float z, float FP) {
        float srcXMin = 0;
        float srcYMin = 0F;
        float srcXMax = 32F * pxl;
        float srcYMax = 4 * pxl;
        float FN = 1F - FP;

        float XX = 0.9F;
        float XM = 0.1F;

        // X+
        tess.addVertexWithUV(FP + x, XM, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, XX, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, XX, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, XM, FP + z, srcXMin, srcYMax);

        // X-
        tess.addVertexWithUV(FN + x, XM, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, XX, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, XX, FN + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FN + x, XM, FN + z, srcXMin, srcYMax);

        // Z+
        tess.addVertexWithUV(FP + x, XM, FP + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, XX, FP + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, XX, FP + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FN + x, XM, FP + z, srcXMin, srcYMax);

        // Z-
        tess.addVertexWithUV(FN + x, XM, FN + z, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, XX, FN + z, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, XX, FN + z, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, XM, FN + z, srcXMin, srcYMax);
    }

    private void drawBeamZ(Tessellator tess, float x, float y, float z, float FP) {
        float srcXMin = 0;
        float srcYMin = 0F;
        float srcXMax = 32F * pxl;
        float srcYMax = 4 * pxl;
        float FN = 1F - FP;

        float XX = 0.9F;
        float XM = 0.1F;

        // X+
        tess.addVertexWithUV(FP + x, FN + y, XM, srcXMin, srcYMax);
        tess.addVertexWithUV(FP + x, FP + y, XM, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, XX, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FN + y, XX, srcXMax, srcYMax);

        // X-
        tess.addVertexWithUV(FN + x, FN + y, XX, srcXMin, srcYMax);
        tess.addVertexWithUV(FN + x, FP + y, XX, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, XM, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, FN + y, XM, srcXMax, srcYMax);

        // Y+
        tess.addVertexWithUV(FN + x, FP + y, XM, srcXMin, srcYMin);
        tess.addVertexWithUV(FN + x, FP + y, XX, srcXMax, srcYMin);
        tess.addVertexWithUV(FP + x, FP + y, XX, srcXMax, srcYMax);
        tess.addVertexWithUV(FP + x, FP + y, XM, srcXMin, srcYMax);

        // Y-
        tess.addVertexWithUV(FN + x, FN + y, XM, srcXMin, srcYMax);
        tess.addVertexWithUV(FP + x, FN + y, XM, srcXMin, srcYMin);
        tess.addVertexWithUV(FP + x, FN + y, XX, srcXMax, srcYMin);
        tess.addVertexWithUV(FN + x, FN + y, XX, srcXMax, srcYMax);
    }

    private void preRenderBeam(Tessellator tess, TileParticleGenerator gen, float f) {
        GL11.glPushMatrix();

        GL11.glTranslated(0, 0.5, 0.5);
        GL11.glRotatef(90F + gen.beam_pitch, 1F, 0F, 0F);
        GL11.glTranslated(0, 0, -0.5);

        GL11.glTranslated(0.5, 0, 0);
        GL11.glRotatef(gen.beam_yaw, 0F, 0F, 1F);
        GL11.glTranslated(-0.5, 0, 0);

        renderBeam(tess, gen, f);
        GL11.glPopMatrix();

        if (gen.render_core) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0.5, 0);
            renderStabilizerSphere(gen);
            GL11.glPopMatrix();
        }
    }

    private void renderBeam(Tessellator tess, TileParticleGenerator tile, float f) {
        int x = 0;
        int y = 0;
        int z = 0;
        double length = tile.beam_length;
        float red = (float) tile.beam_red / 255F;
        float green = (float) tile.beam_green / 255F;
        float blue = (float) tile.beam_blue / 255F;

        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        bindTexture(beamTexture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);

        // float time = (float)tile.getWorldObj().getTotalWorldTime() + f;
        float time = tile.rotation + f;
        float upMot = -time * 0.2F - (float) MathHelper.floor_float(-time * 0.1F);
        float rotValue = tile.beam_rotation * (tile.rotation + f * 0.5F);
        double rotation = rotValue;

        tess.startDrawingQuads();
        tess.setBrightness(200);
        tess.setColorRGBA(tile.beam_red, tile.beam_green, tile.beam_blue, 32);

        double scale = (double) tile.beam_scale * 0.2D;
        double d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale; // x point 1
        double d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale; // z point 1
        double d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale; // x point 2
        double d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale; // z point 2
        double d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale; // Dist from x-3
        double d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
        double d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
        double d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;
        double height = (double) (length);
        double texXMin = 0.0D;
        double texXMax = 1.0D;
        double d28 = (double) (-1.0F + upMot);
        double texHeight = (double) (length) * (0.5D / scale) + d28;

        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMax, texHeight);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMax, d28);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMin, d28);
        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMin, texHeight);

        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMax, texHeight);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMax, d28);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMin, d28);
        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMin, texHeight);

        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMax, texHeight);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMax, d28);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMin, d28);
        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMin, texHeight);

        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMax, texHeight);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMax, d28);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMin, d28);
        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMin, texHeight);

        rotation += 0.77f;
        d7 = 0.5D + Math.cos(rotation + 2.356194490192345D) * scale;
        d9 = 0.5D + Math.sin(rotation + 2.356194490192345D) * scale;
        d11 = 0.5D + Math.cos(rotation + (Math.PI / 4D)) * scale;
        d13 = 0.5D + Math.sin(rotation + (Math.PI / 4D)) * scale;
        d15 = 0.5D + Math.cos(rotation + 3.9269908169872414D) * scale;
        d17 = 0.5D + Math.sin(rotation + 3.9269908169872414D) * scale;
        d19 = 0.5D + Math.cos(rotation + 5.497787143782138D) * scale;
        d21 = 0.5D + Math.sin(rotation + 5.497787143782138D) * scale;

        d28 = (-1F + (upMot * 1));
        texHeight = (double) (length) * (0.5D / scale) + d28;

        tess.setColorRGBA_F(red, green, blue, 1f);

        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMax, texHeight);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMax, d28);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMin, d28);
        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMin, texHeight);

        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMax, texHeight);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMax, d28);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMin, d28);
        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMin, texHeight);

        tess.addVertexWithUV(x + d11, y + height, z + d13, texXMax, texHeight);
        tess.addVertexWithUV(x + d11, y, z + d13, texXMax, d28);
        tess.addVertexWithUV(x + d19, y, z + d21, texXMin, d28);
        tess.addVertexWithUV(x + d19, y + height, z + d21, texXMin, texHeight);

        tess.addVertexWithUV(x + d15, y + height, z + d17, texXMax, texHeight);
        tess.addVertexWithUV(x + d15, y, z + d17, texXMax, d28);
        tess.addVertexWithUV(x + d7, y, z + d9, texXMin, d28);
        tess.addVertexWithUV(x + d7, y + height, z + d9, texXMin, texHeight);

        tess.draw();
        GL11.glPushMatrix();

        // GL11.glTranslated(0, 0.4, 0);
        // length -= 0.5F;

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glScalef(tile.beam_scale, 1f, tile.beam_scale);
        GL11.glTranslated(-0.5, -0, -0.5);
        tess.startDrawingQuads();
        tess.setColorRGBA(tile.beam_red, tile.beam_green, tile.beam_blue, 32);
        double d30 = 0.2D;
        double d4 = 0.2D;
        double d6 = 0.8D;
        double d8 = 0.2D;
        double d10 = 0.2D;
        double d12 = 0.8D;
        double d14 = 0.8D;
        double d16 = 0.8D;
        double d18 = (double) (length);
        double d20 = 0.0D;
        double d22 = 1D;
        double d24 = (double) (-1.0F + upMot);
        double d26 = (double) (length) + d24;
        tess.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
        tess.addVertexWithUV(x + d30, y, z + d4, d22, d24);
        tess.addVertexWithUV(x + d6, y, z + d8, d20, d24);
        tess.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
        tess.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
        tess.addVertexWithUV(x + d14, y, z + d16, d22, d24);
        tess.addVertexWithUV(x + d10, y, z + d12, d20, d24);
        tess.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
        tess.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
        tess.addVertexWithUV(x + d6, y, z + d8, d22, d24);
        tess.addVertexWithUV(x + d14, y, z + d16, d20, d24);
        tess.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
        tess.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
        tess.addVertexWithUV(x + d10, y, z + d12, d22, d24);
        tess.addVertexWithUV(x + d30, y, z + d4, d20, d24);
        tess.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
        tess.draw();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        //
        //		GL11.glEnable(GL11.GL_LIGHTING);
        //		GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
