package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.entity.EntityChaosVortex;
import com.brandon3055.draconicevolution.common.lib.References;

/**
 * Created by brandon3055 on 3/10/2015.
 */
public class RenderEntityChaosVortex extends Render {

    public static IModelCustom uvSphere;

    public RenderEntityChaosVortex() {
        uvSphere = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/reactorCoreModel.obj"));
    }

    public void doRender(EntityChaosVortex entity, double x, double y, double z, float f1, float tick) {
        if (entity.ticksExisted < 100) return;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
        ResourceHandler.bindResource("textures/models/white.png");

        float scale = ((float) entity.ticksExisted - 100F) + tick;
        scale /= 500F;
        if (scale > 0.5F) scale = 0.5F;

        GL11.glScaled(scale, scale, scale);
        uvSphere.renderAll();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f1, float f2) {
        doRender((EntityChaosVortex) entity, x, y, z, f1, f2);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
