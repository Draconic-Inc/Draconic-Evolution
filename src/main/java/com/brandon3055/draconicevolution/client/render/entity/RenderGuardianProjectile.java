package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.entity.EntityGuardianProjectile;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 24/8/2015.
 */
public class RenderGuardianProjectile extends EntityRenderer<EntityGuardianProjectile> {

    public RenderGuardianProjectile(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityGuardianProjectile projectile, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, (float) z);
        float height = (projectile.ticksExisted) % 8 * 1F / 8F;
        switch (projectile.type) {
            case EntityGuardianProjectile.FIREBOMB:
            case EntityGuardianProjectile.FIRE_CHASER:
                height = 0;
                break;
            case EntityGuardianProjectile.TELEPORT:
                height = 0;
                break;
            default:
                break;
        }

        bindEntityTexture(projectile);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
        GlStateManager.disableLighting();

        float f2 = projectile.type == EntityGuardianProjectile.MINI_CHAOS_CHASER ? projectile.power / 10 : projectile.power / 5;
        GlStateManager.scalef(f2 / 1.0F, f2 / 1.0F, f2 / 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f3 = 0;
        float f4 = 1;
        float f5 = height;
        float f6 = height + (1F / 8F);
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;

        GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (projectile.type == EntityGuardianProjectile.FIREBOMB || projectile.type == EntityGuardianProjectile.FIRE_CHASER) {
            GlStateManager.translated(0, 0.25, 0);
            GlStateManager.rotatef((projectile.ticksExisted * 40 + partialTicks * 40), 0, 0, 1);
            GlStateManager.translated(0, -0.25, 0);
        }
        else if (projectile.type == EntityGuardianProjectile.TELEPORT) {
            f5 = 0;
            f6 = 1;
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0.0F - f8, 0.0F - f9, 0.0D).tex(f3, f6).endVertex();
        buffer.pos(f7 - f8, 0.0F - f9, 0.0D).tex(f4, f6).endVertex();
        buffer.pos(f7 - f8, 1.0F - f9, 0.0D).tex(f4, f5).endVertex();
        buffer.pos(0.0F - f8, 1.0F - f9, 0.0D).tex(f3, f5).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();
        ;
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGuardianProjectile entity) {
        switch (entity.type) {
            case EntityGuardianProjectile.FIREBOMB:
            case EntityGuardianProjectile.FIRE_CHASER:
                return ResourceHelperDE.getResource(DETextures.PROJECTILE_FIRE);
            case EntityGuardianProjectile.TELEPORT:
                return ResourceHelperDE.getResourceRAW("textures/items/ender_pearl.png");
            case EntityGuardianProjectile.ENERGY_CHASER:
                return ResourceHelperDE.getResource(DETextures.PROJECTILE_ENERGY);
            case EntityGuardianProjectile.CHAOS_CHASER:
            case EntityGuardianProjectile.MINI_CHAOS_CHASER:
                return ResourceHelperDE.getResource(DETextures.PROJECTILE_CHAOS);
            case EntityGuardianProjectile.IGNITION_CHARGE:
                return ResourceHelperDE.getResource(DETextures.PROJECTILE_IGNITION);
            default:
                return ResourceHelperDE.getResource(DETextures.PROJECTILE_FIRE);
        }
    }

}
