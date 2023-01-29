package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.ModelChaosCrystal;
import com.brandon3055.draconicevolution.common.entity.EntityChaosCrystal;

/**
 * Created by brandon3055 on 30/8/2015.
 */
public class RenderChaosCrystal extends Render {

    private ModelBase model;

    public RenderChaosCrystal() {
        this.shadowSize = 0.5F;
        this.model = new ModelChaosCrystal(true);
    }

    public void doRender(EntityChaosCrystal crystal, double x, double y, double z, float f, float partialTick) {
        float rotation = (float) crystal.innerRotation + (crystal.health > 0 ? partialTick : 0);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        ResourceHandler.bindResource("textures/entity/chaosCrystal.png");
        float r2 = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        r2 += r2 * r2;
        this.model.render(crystal, 0.0F, rotation * 3.0F, r2 * 0.2F, crystal.deathAnimation, crystal.health, 0.0625F);

        GL11.glPopMatrix();

        if (crystal.shieldTime > 0) {
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5, -1.5, -0.5);
            Tessellator tessellator = Tessellator.instance;
            ResourceHandler.bindTexture(ResourceHandler.getResourceWOP("textures/entity/beacon_beam.png"));
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            float f2 = (float) crystal.ticksExisted + partialTick;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDepthMask(false);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(200, 0, 0, 32);
            double size = 0.39F;
            double d30 = 0.2D - size;
            double d4 = 0.2D - size;
            double d6 = 0.8D + size;
            double d8 = 0.2D - size;
            double d10 = 0.2D - size;
            double d12 = 0.8D + size;
            double d14 = 0.8D + size;
            double d16 = 0.8D + size;
            double d18 = (double) (256.0F);
            double d20 = 0.0D;
            double d22 = 1.0D;
            double d24 = (double) (-1.0F + f3);
            double d26 = (double) (256.0F) + d24;
            tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
            tessellator.addVertexWithUV(x + d30, y, z + d4, d22, d24);
            tessellator.addVertexWithUV(x + d6, y, z + d8, d20, d24);
            tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
            tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
            tessellator.addVertexWithUV(x + d14, y, z + d16, d22, d24);
            tessellator.addVertexWithUV(x + d10, y, z + d12, d20, d24);
            tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
            tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
            tessellator.addVertexWithUV(x + d6, y, z + d8, d22, d24);
            tessellator.addVertexWithUV(x + d14, y, z + d16, d20, d24);
            tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
            tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
            tessellator.addVertexWithUV(x + d10, y, z + d12, d22, d24);
            tessellator.addVertexWithUV(x + d30, y, z + d4, d20, d24);
            tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }

    protected ResourceLocation getEntityTexture(EntityEnderCrystal p_110775_1_) {
        return ResourceHandler.getResource("textures/entity/chaosCrystal.png");
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityChaosCrystal) p_110775_1_);
    }

    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
            float p_76986_9_) {
        this.doRender((EntityChaosCrystal) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
