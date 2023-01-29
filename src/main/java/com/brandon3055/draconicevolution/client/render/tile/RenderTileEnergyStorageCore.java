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
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import cpw.mods.fml.client.FMLClientHandler;

/**
 * Created by Brandon on 27/07/2014.
 */
public class RenderTileEnergyStorageCore extends TileEntitySpecialRenderer {

    private static final ResourceLocation iner_model_texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/power_sphere_layer_1.png");
    private static final ResourceLocation outer_model_texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/power_sphere_layer_2.png");
    private IModelCustom iner_model;

    public RenderTileEnergyStorageCore() {
        iner_model = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/power_sphere_layer_1.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (tile == null || !(tile instanceof TileEnergyStorageCore)) return;
        TileEnergyStorageCore core = (TileEnergyStorageCore) tile;
        if (!core.isOnline()) return;
        float scale = 0;
        float rotation = core.modelRotation + (timeSinceLastTick / 2F);

        switch (core.getTier()) {
            case 0:
                scale = 0.7F;
                break;
            case 1:
                scale = 1.2F;
                break;
            case 2:
                scale = 1.7F;
                break;
            case 3:
                scale = 2.5F;
                break;
            case 4:
                scale = 3.5F;
                break;
            case 5:
                scale = 4.5F;
                break;
            case 6:
                scale = 5.5F;
                break;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(iner_model_texture);

        double colour = (double) ((TileEnergyStorageCore) tile).getEnergyStored()
                / (double) ((TileEnergyStorageCore) tile).getMaxEnergyStored();
        float brightness = (float) Math.abs(Math.sin((float) Minecraft.getSystemTime() / 3000f) * 100f);

        colour = 1f - colour;
        GL11.glScalef(scale, scale, scale);
        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0F, 1F, 0.5F);
        GL11.glColor4d(1F, colour * 0.3f, colour * 0.7f, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 80f + brightness, 80f + brightness);
        iner_model.renderAll();
        GL11.glPopMatrix();

        // GL11.glPushMatrix();
        // GL11.glScalef(0.8F, 0.8F, 0.8F);
        // GL11.glColor4d(1F, 1f, 0.2f, 1F);
        // GL11.glRotatef(rotation, 0F, 1F, 0.5F);
        // //iner_model.renderAll();
        // GL11.glPopMatrix();
        //
        // GL11.glPushMatrix();
        // GL11.glScalef(0.9F, 0.9F, 0.9F);
        // GL11.glColor4d(1F, 0f, 0.2f, 1F);
        // GL11.glRotatef(rotation, 0F, 1F, 0.5F);
        // //iner_model.renderAll();
        // GL11.glPopMatrix();

        GL11.glScalef(1.1F, 1.1F, 1.1F);
        GL11.glDepthMask(false);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(outer_model_texture);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glRotatef(rotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4f(0.5F, 2F, 2F, 0.7F);
        iner_model.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
