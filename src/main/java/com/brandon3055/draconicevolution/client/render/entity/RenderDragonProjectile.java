package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.entity.EntityDragonProjectile;

/**
 * Created by brandon3055 on 24/8/2015.
 */
public class RenderDragonProjectile extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTick) {
        doRender((EntityDragonProjectile) entity, x, y, z, f, partialTick);
    }

    private void doRender(EntityDragonProjectile entity, double x, double y, double z, float f, float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        float height = (entity.ticksExisted) % 8 * 1F / 8F;
        switch (entity.type) {
            case EntityDragonProjectile.FIREBOMB:
            case EntityDragonProjectile.FIRE_CHASER:
                ResourceHandler.bindResource("textures/entity/projectileFire.png");
                height = 0;
                break;
            case EntityDragonProjectile.TELEPORT:
                height = 0;
                ResourceHandler.bindTexture(ResourceHandler.getResourceWOP("textures/items/ender_pearl.png"));
                break;
            case EntityDragonProjectile.ENERGY_CHASER:
                ResourceHandler.bindResource("textures/entity/projectileEnergy.png");
                break;
            case EntityDragonProjectile.CHAOS_CHASER:
            case EntityDragonProjectile.MINI_CHAOS_CHASER:
                ResourceHandler.bindResource("textures/entity/projectileChaos.png");
                break;
            case EntityDragonProjectile.IGNITION_CHARGE:
                ResourceHandler.bindResource("textures/entity/projectileIgnition.png");

                break;
            default:
                ResourceHandler.bindResource("textures/entity/projectileFire.png");
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        // GL11.glDepthMask(false);
        // GL11.glEnable(GL11.GL_CULL_FACE);
        float f2 = entity.type == EntityDragonProjectile.MINI_CHAOS_CHASER ? entity.power / 10 : entity.power / 5;
        GL11.glScalef(f2 / 1.0F, f2 / 1.0F, f2 / 1.0F);
        Tessellator tessellator = Tessellator.instance;
        float f3 = 0;
        float f4 = 1;
        float f5 = height;
        float f6 = height + (1F / 8F);
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (entity.type == EntityDragonProjectile.FIREBOMB || entity.type == EntityDragonProjectile.FIRE_CHASER) {
            GL11.glTranslated(0, 0.25, 0);
            GL11.glRotatef((entity.ticksExisted * 40 + partialTick * 40), 0, 0, 1);
            GL11.glTranslated(0, -0.25, 0);
        } else if (entity.type == EntityDragonProjectile.TELEPORT) {
            f5 = 0;
            f6 = 1;
        }

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.setBrightness(200);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (0.0F - f9), 0.0D, (double) f3, (double) f6);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (0.0F - f9), 0.0D, (double) f4, (double) f6);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (1.0F - f9), 0.0D, (double) f4, (double) f5);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (1.0F - f9), 0.0D, (double) f3, (double) f5);
        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        // GL11.glDepthMask(true);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
