package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.entity.EntityCustomArrow;

/**
 * Created by brandon3055 on 3/3/2016.
 */
public class RenderEntityCustomArrow extends Render {

    private IModelCustom arrow = AdvancedModelLoader
            .loadModel(ResourceHandler.getResource("models/tools/ArrowCommon.obj"));

    public void doRender(EntityCustomArrow entityArrow, double x, double y, double z, float f1, float f2) {
        this.bindEntityTexture(entityArrow);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(
                entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * f2 - 90.0F,
                0.0F,
                1.0F,
                0.0F);
        GL11.glRotatef(
                entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * f2,
                0.0F,
                0.0F,
                1.0F);

        float f10 = 0.3F;
        float f11 = (float) entityArrow.arrowShake - f2;

        if (f11 > 0.0F) {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // arrow.renderAll();
        if (entityArrow.bowProperties != null && entityArrow.bowProperties.energyBolt) {
            arrow.renderAll();

            GL11.glTranslated(0, -0.025, 0);
            GL11.glColor4f(1F, 1F, 1F, 0.6F);
            GL11.glScaled(1.05, 1.05, 1.05);

            GL11.glColor4f(1F, 1F, 1F, 0.4F);
            GL11.glScaled(1.05, 1.05, 1.05);
            arrow.renderAll();
        } else {
            arrow.renderAll();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCustomArrow arrow) {
        return arrow.bowProperties.energyBolt ? ResourceHandler.getResource("textures/models/reactorCore.png")
                : ResourceHandler.getResource("textures/models/tools/ArrowCommon.png");
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityCustomArrow) p_110775_1_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
            float p_76986_9_) {
        this.doRender((EntityCustomArrow) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
