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

/**
 * Created by Brandon on 21/11/2014.
 */
public class RenderEntityEnderEnergyManipulator extends Render<EntityEnderEnergyManipulator> {

    private static ItemStack stack = new ItemStack(Items.SKULL, 1, 1);

    private static ShaderProgram shaderProgram;

    public RenderEntityEnderEnergyManipulator(RenderManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityEnderEnergyManipulator entity, double x, double y, double z, float entityYaw, float partialTicks) {

        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (Math.cos((ClientEventHandler.elapsedTicks + partialTicks) / 20D) * 0.1), z);
        GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks), 0, 1, 0);

        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        if (DEShaders.useShaders()) {
            if (shaderProgram == null) {
                shaderProgram = new ShaderProgram();
                shaderProgram.attachShader(DEShaders.reactorShield);
            }
            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
                cache.glUniform1F("intensity", 0.09F);
            });
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
                cache.glUniform1F("intensity", 0.02F);
            });
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            shaderProgram.releaseShader();
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEnderEnergyManipulator entity) {
        return ResourceHelperDE.getResource(DETextures.DRAGON_HEART);
    }
}
