package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;

/**
 * Created by Brandon on 27/07/2014.
 */
public class RenderTileEnergyPylon extends TileEntitySpecialRenderer {

    private static final ResourceLocation model_texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/pylon_sphere_texture.png");
    private IModelCustom model;

    public RenderTileEnergyPylon() {
        model = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/pylon_sphere.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {

        if (tile == null || !(tile instanceof TileEnergyPylon)) return;
        TileEnergyPylon pylon = (TileEnergyPylon) tile;
        if (!pylon.active) return;
        float scale = pylon.modelScale + (timeSinceLastTick *= !pylon.reciveEnergy ? -0.01F : 0.01F);
        float rotation = pylon.modelRotation + (timeSinceLastTick / 2F);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        if (pylon.getWorldObj().getBlockMetadata(pylon.xCoord, pylon.yCoord, pylon.zCoord) == 1) {
            GL11.glTranslated(0, 1, 0);
        } else {
            GL11.glTranslated(0, -1, 0);
        }

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        bindTexture(model_texture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);

        GL11.glPushMatrix();
        float scale1 = scale % 1F;
        GL11.glScalef(scale1, scale1, scale1);
        GL11.glRotatef(rotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4d(1D, 1D, 1D, 1F - (scale1));
        model.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        float scale2 = (scale + 0.25F) % 1F;
        GL11.glScalef(scale2, scale2, scale2);
        GL11.glRotatef(rotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4f(1F, 1F, 1F, 1F - (scale2));
        model.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        float scale3 = (scale + 0.5F) % 1F;
        GL11.glScalef(scale3, scale3, scale3);
        GL11.glRotatef(rotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4f(1F, 1F, 1F, 1F - (scale3));
        model.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        float scale4 = (scale + 0.75F) % 1F;
        GL11.glScalef(scale4, scale4, scale4);
        GL11.glRotatef(rotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4f(1F, 1F, 1F, 1F - (scale4));
        model.renderAll();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    // private void renderSphere()
}
