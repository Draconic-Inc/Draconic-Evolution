package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.client.model.ModelGuardianCrystal;
import com.brandon3055.draconicevolution.entity.EntityGuardianCrystal;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 30/8/2015.
 */
public class RenderGuardianCrystal extends Render<EntityGuardianCrystal> {
    private ModelBase model;

    protected RenderGuardianCrystal(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
        this.model = new ModelGuardianCrystal(true);
    }

    @Override
    public void doRender(EntityGuardianCrystal crystal, double x, double y, double z, float entityYaw, float partialTicks) {
        float rotation = (float) crystal.innerRotation + (crystal.health > 0 ? partialTicks : 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        ResourceHelperDE.bindTexture("textures/entity/guardianCrystal.png");
        float r2 = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        r2 += r2 * r2;
        this.model.render(crystal, 0.0F, rotation * 3.0F, r2 * 0.2F, crystal.deathAnimation, crystal.health, 0.0625F);

        GL11.glPopMatrix();

        if (crystal.shieldTime > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5, -1.5, -0.5);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/beacon_beam.png"));
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GlStateManager.depthMask(false);

            float f2 = (float) crystal.ticksExisted + partialTicks;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
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

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x + d30, y + d18, z + d4).tex(d22, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d22, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d20, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d20, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d22, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d22, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d20, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d20, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d6, y + d18, z + d8).tex(d22, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d6, y, z + d8).tex(d22, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d14, y, z + d16).tex(d20, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d14, y + d18, z + d16).tex(d20, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d10, y + d18, z + d12).tex(d22, d26).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d10, y, z + d12).tex(d22, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d30, y, z + d4).tex(d20, d24).color(200, 0, 0, 32).endVertex();
            buffer.pos(x + d30, y + d18, z + d4).tex(d20, d26).color(200, 0, 0, 32).endVertex();
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGuardianCrystal entity) {
        return ResourceHelperDE.getResource("textures/entity/guardianCrystal.png");
    }

    public static class Factory implements IRenderFactory<EntityGuardianCrystal> {
        @Override
        public Render<? super EntityGuardianCrystal> createRenderFor(RenderManager manager) {
            return new RenderGuardianCrystal(manager);
        }
    }
}
