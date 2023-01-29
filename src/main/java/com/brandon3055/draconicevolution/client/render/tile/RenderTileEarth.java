package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEarth;
import cpw.mods.fml.client.FMLClientHandler;

/**
 * Created by Brandon on 27/07/2014.
 */
public class RenderTileEarth extends TileEntitySpecialRenderer {

    private static final ResourceLocation iner_model_texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/Earth.png");
    public static IModelCustom modelCustom;

    public RenderTileEarth() {
        modelCustom = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/Earth.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (!(tile instanceof TileEarth)) return;

        TileEarth tileEarth = (TileEarth) tile;
        float scale = 0.01f * tileEarth.getSize();

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(iner_model_texture);

        float brightness = (float) Math.abs(Math.sin((float) Minecraft.getSystemTime() / 3000f) * 100f);

        GL11.glScalef(scale, scale, scale);
        GL11.glPushMatrix();
        GL11.glRotatef(-180, 1F, 0F, 1F);
        GL11.glRotatef(90, 1F, 0F, 0F);
        if (tileEarth.getRotationSpeed() != 0) {
            GL11.glRotatef((System.currentTimeMillis() / tileEarth.getRotationSpeed()) % 360, 0F, 0F, 1F);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 80f + brightness, 80f + brightness);
        modelCustom.renderAll();
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
