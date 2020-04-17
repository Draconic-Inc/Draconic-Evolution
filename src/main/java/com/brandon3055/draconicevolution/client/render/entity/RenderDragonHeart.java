package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.render.RenderUtils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.entity.EntityDragonHeart;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 21/11/2014.
 */
public class RenderDragonHeart extends EntityRenderer<EntityDragonHeart> {

    public RenderDragonHeart(EntityRendererManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityDragonHeart entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y + (Math.cos((ClientEventHandler.elapsedTicks + partialTicks) / 20D) * 0.1) - 0.5, z);
        GlStateManager.rotated((entity.rotation + (entity.rotationInc * partialTicks)) * 70, 0, 1, 0);
        GlStateManager.scalef(2F, 2F, 2F);
        RenderUtils.renderItemUniform(entity.renderStack);


        float sine = (float) Math.abs(Math.cos(ClientEventHandler.elapsedTicks / 100D) - 0.4F);

        bindEntityTexture(entity);

        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200f, 200f);
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.color4f(2f - sine * 1.3f, 1f - sine / 1.5f, 1f - sine / 1.8f, 1F - sine / 1.6f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        GlStateManager.scalef(0.55f, 0.55f, 0.55f);
        GlStateManager.translated(-0.5, 0.47, 0.061);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 1, 0).tex(0, 0).endVertex();
        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
        buffer.pos(1, 0, 0).tex(1, 1).endVertex();
        buffer.pos(1, 1, 0).tex(1, 0).endVertex();
        tess.draw();

        GlStateManager.translated(0, 0, -0.12);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 1, 0).tex(0, 0).endVertex();
        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
        buffer.pos(1, 0, 0).tex(1, 1).endVertex();
        buffer.pos(1, 1, 0).tex(1, 0).endVertex();
        tess.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDragonHeart entity) {
        return ResourceHelperDE.getResource(DETextures.DRAGON_HEART);
    }
}
