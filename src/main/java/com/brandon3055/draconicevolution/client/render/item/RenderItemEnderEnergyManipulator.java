package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemEnderEnergyManipulator implements IItemRenderer {

    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
//    private static ItemStack stack = new ItemStack(Items.SKULL, 1, 1);

    private static ShaderProgram shaderProgram;


    public RenderItemEnderEnergyManipulator() {
    }

    //region Unused
    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    //endregion

    @Override
    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);

        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            GlStateManager.rotate(180, 0, 1, 0);
        }

        renderSkull();

        if (DEShaders.useShaders()) {
            if (shaderProgram == null) {
                shaderProgram = new ShaderProgram();
                shaderProgram.attachShader(DEShaders.reactorShield);
            }
            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
                cache.glUniform1F("intensity", 0.09F);
            });
            renderSkull();
            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
                cache.glUniform1F("intensity", 0.02F);
            });
            renderSkull();
            shaderProgram.releaseShader();
        }

        GlStateManager.popMatrix();
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_ITEM;
    }

    private void renderSkull() {
        GlStateManager.pushMatrix();
        ResourceHelperDE.bindTexture(WITHER_SKELETON_TEXTURES);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0, 0.25, 0);
        skeletonHead.render(null, 0, 0, 0, 180, 0, 0.0625F);
        GlStateManager.popMatrix();
    }
}
