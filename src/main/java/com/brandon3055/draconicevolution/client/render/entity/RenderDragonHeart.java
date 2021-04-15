package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.entity.EntityDragonHeart;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.systems.RenderSystem;
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


//    @Override
    public void doRender(EntityDragonHeart entity, double x, double y, double z, float entityYaw, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y + (Math.cos((ClientEventHandler.elapsedTicks + partialTicks) / 20D) * 0.1) - 0.5, z);
        RenderSystem.rotatef((entity.rotation + (entity.rotationInc * partialTicks)) * 70, 0, 1, 0);
        RenderSystem.scalef(2F, 2F, 2F);
//        RenderUtils.renderItemUniform(entity.renderStack);


        float sine = (float) Math.abs(Math.cos(ClientEventHandler.elapsedTicks / 100D) - 0.4F);

//        bindEntityTexture(entity);

//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200f, 200f);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableBlend();
        RenderSystem.disableLighting();
        RenderSystem.color4f(2f - sine * 1.3f, 1f - sine / 1.5f, 1f - sine / 1.8f, 1F - sine / 1.6f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.scalef(0.55f, 0.55f, 0.55f);
        RenderSystem.translated(-0.5, 0.47, 0.061);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(0, 1, 0).uv(0, 0).endVertex();
        buffer.vertex(0, 0, 0).uv(0, 1).endVertex();
        buffer.vertex(1, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(1, 1, 0).uv(1, 0).endVertex();
        tess.end();

        RenderSystem.translated(0, 0, -0.12);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(0, 1, 0).uv(0, 0).endVertex();
        buffer.vertex(0, 0, 0).uv(0, 1).endVertex();
        buffer.vertex(1, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(1, 1, 0).uv(1, 0).endVertex();
        tess.end();

        RenderSystem.disableBlend();
        RenderSystem.enableLighting();

        RenderSystem.popMatrix();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDragonHeart entity) {
        return ResourceHelperDE.getResource(DETextures.DRAGON_HEART);
    }
}
