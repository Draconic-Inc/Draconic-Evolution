package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileTestBlock;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 24/07/2014.
 */
public class RenderTileTestBlock extends TileEntitySpecialRenderer {

    private static final ResourceLocation iner_model_texture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/power_sphere_layer_1.png");
    private static final ResourceLocation outer_model_texture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/power_sphere_layer_2.png");
    private IModelCustom iner_model;
    private IModelCustom outer_model;

    public RenderTileTestBlock() {
        iner_model = AdvancedModelLoader.loadModel(
                new ResourceLocation(References.MODID.toLowerCase(), "models/power_sphere_layer_1.obj"));
        // outer_model = AdvancedModelLoader.loadModel(new ResourceLocation(References.MODID.toLowerCase(),
        // "models/power_sphere_layer_2.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8) {
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 100, (float) 100);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        // GL11.glDepthMask(false);

        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(iner_model_texture);

        GL11.glPushMatrix();
        GL11.glRotatef(((TileTestBlock) var1).modelRotation, 0F, 1F, 0.5F);
        // GL11.glColor4f(2F, 2F, 2F, 1F);
        iner_model.renderAll();
        GL11.glPopMatrix();

        GL11.glScalef(1.1F, 1.1F, 1.1F);
        GL11.glDepthMask(false);
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(outer_model_texture);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glRotatef(((TileTestBlock) var1).modelRotation * 0.5F, 0F, -1F, -0.5F);
        GL11.glColor4f(0.5F, 2F, 2F, 0.7F);
        outer_model.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
    }
}
