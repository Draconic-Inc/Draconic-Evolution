package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.client.model.ModelGuardianCrystal;
import com.brandon3055.draconicevolution.entity.EntityGuardianCrystal;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 30/8/2015.
 */
public class RenderGuardianCrystal extends EntityRenderer<EntityGuardianCrystal> {
    private ModelGuardianCrystal model;

    public RenderGuardianCrystal(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
//        this.model = new ModelGuardianCrystal(true);
    }

//    @Override
    public void doRender(EntityGuardianCrystal crystal, double x, double y, double z, float entityYaw, float partialTicks) {
        float rotation = (float) crystal.innerRotation + (crystal.health > 0 ? partialTicks : 0);
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y, z);
        ResourceHelperDE.bindTexture(DETextures.CHAOS_GUARDIAN_CRYSTAL);
        float r2 = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        r2 += r2 * r2;
//        this.model.render(crystal, 0.0F, rotation * 3.0F, r2 * 0.2F, crystal.deathAnimation, crystal.health, 0.0625F);

        GL11.glPopMatrix();

        if (crystal.shieldTime > 0) {
            RenderSystem.pushMatrix();
            RenderSystem.translated(-0.5, -1.5, -0.5);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            ResourceHelperDE.bindTexture(ResourceHelperDE.getResourceRAW("textures/entity/beacon_beam.png"));
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497);
            RenderSystem.disableLighting();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.depthMask(false);

            float f2 = (float) crystal.ticksExisted + partialTicks;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor(-f2 * 0.1F);
            float size = 0.39F;
            float d30 = 0.2F - size;
            float d4 = 0.2F - size;
            float d6 = 0.8F + size;
            float d8 = 0.2F - size;
            float d10 = 0.2F - size;
            float d12 = 0.8F + size;
            float d14 = 0.8F + size;
            float d16 = 0.8F + size;
            float d18 = 256.0F;
            float d20 = 0.0F;
            float d22 = 1.0F;
            float d24 = -1.0F + f3;
            float d26 = 256.0F + d24;

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
            RenderSystem.enableLighting();
            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.popMatrix();
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityGuardianCrystal entity) {
        return ResourceHelperDE.getResource(DETextures.CHAOS_GUARDIAN_CRYSTAL);
    }

}
