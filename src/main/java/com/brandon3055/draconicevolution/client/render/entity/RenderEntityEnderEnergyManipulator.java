package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.render.shader.ShaderProgram;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.entity.EntityEnderEnergyManipulator;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by Brandon on 21/11/2014.
 */
public class RenderEntityEnderEnergyManipulator extends Render<EntityEnderEnergyManipulator> {

    private static ItemStack stack = new ItemStack(Items.SKULL, 1, 1);

    protected RenderEntityEnderEnergyManipulator(RenderManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityEnderEnergyManipulator entity, double x, double y, double z, float entityYaw, float partialTicks) {

        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (Math.cos((ClientEventHandler.elapsedTicks + partialTicks) / 20D) * 0.1), z);
        GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks), 0, 1, 0);
//        GlStateManager.translate(0.5, 0.5, 0.5);

        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        if (DEShaders.useShaders()) {
            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
            DEShaders.reactorOp.setIntensity(0.09F);
            DEShaders.reactorShield.freeBindShader();
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
            DEShaders.reactorOp.setIntensity(0.02F);
            DEShaders.reactorShield.freeBindShader();
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            ShaderProgram.unbindShader();
        }

        GlStateManager.popMatrix();

//        GlStateManager.pushMatrix();
//        GlStateManager.translate(x, y + (Math.cos((ClientEventHandler.elapsedTicks + partialTicks) / 20D) * 0.1) - 0.5, z);
//        GlStateManager.rotate((entity.rotation + (entity.rotationInc * partialTicks)) * 70, 0, 1, 0);
//        GlStateManager.scale(2F, 2F, 2F);
//        RenderUtils.renderItemUniform(entity.renderStack);
//
//
//        float sine = (float) Math.abs(Math.cos(ClientEventHandler.elapsedTicks / 100D) - 0.4F);
//
//        bindEntityTexture(entity);
//
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200f, 200f);
//        GlStateManager.disableCull();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GlStateManager.enableBlend();
//        GlStateManager.disableLighting();
//        GlStateManager.color(2f - sine * 1.3f, 1f - sine / 1.5f, 1f - sine / 1.8f, 1F - sine / 1.6f);
//
//        Tessellator tess = Tessellator.getInstance();
//        VertexBuffer buffer = tess.getBuffer();
//
//        GlStateManager.scale(0.55f, 0.55f, 0.55f);
//        GlStateManager.translate(-0.5, 0.47, 0.061);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(0, 1, 0).tex(0, 0).endVertex();
//        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
//        buffer.pos(1, 0, 0).tex(1, 1).endVertex();
//        buffer.pos(1, 1, 0).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.translate(0, 0, -0.12);
//
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        buffer.pos(0, 1, 0).tex(0, 0).endVertex();
//        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
//        buffer.pos(1, 0, 0).tex(1, 1).endVertex();
//        buffer.pos(1, 1, 0).tex(1, 0).endVertex();
//        tess.draw();
//
//        GlStateManager.disableBlend();
//        GlStateManager.enableLighting();
//
//        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEnderEnergyManipulator entity) {
        return ResourceHelperDE.getResource(DETextures.DRAGON_HEART);
    }

    public static class Factory implements IRenderFactory<EntityEnderEnergyManipulator> {
        @Override
        public Render<? super EntityEnderEnergyManipulator> createRenderFor(RenderManager manager) {
            return new RenderEntityEnderEnergyManipulator(manager);
        }
    }
}
