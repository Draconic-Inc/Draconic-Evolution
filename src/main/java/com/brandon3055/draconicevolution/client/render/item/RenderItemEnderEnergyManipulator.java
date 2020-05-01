package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemEnderEnergyManipulator implements IItemRenderer {

    private final GenericHeadModel skeletonHead = new GenericHeadModel(0, 0, 64, 32);
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
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {

    }

    @Override
    public ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }


//    @Override
//    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
//        Minecraft mc = Minecraft.getInstance();
//        RenderSystem.pushMatrix();
//        RenderSystem.translated(0.5, 0.5, 0.5);
//
//        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
//            RenderSystem.rotated(180, 0, 1, 0);
//        }
//
//        renderSkull();
//
//        if (DEShaders.useShaders()) {
//            if (shaderProgram == null) {
//                shaderProgram = new ShaderProgram();
//                shaderProgram.attachShader(DEShaders.reactorShield);
//            }
//            shaderProgram.useShader(cache -> {
//                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
//                cache.glUniform1F("intensity", 0.09F);
//            });
//            renderSkull();
//            shaderProgram.useShader(cache -> {
//                cache.glUniform1F("time", ((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
//                cache.glUniform1F("intensity", 0.02F);
//            });
//            renderSkull();
//            shaderProgram.releaseShader();
//        }
//
//        RenderSystem.popMatrix();
//    }
//
//    @Override
//    public IModelState getTransforms() {
//        return TransformUtils.DEFAULT_ITEM;
//    }
//
//    private void renderSkull() {
//        RenderSystem.pushMatrix();
//        ResourceHelperDE.bindTexture(WITHER_SKELETON_TEXTURES);
//        RenderSystem.scalef(-1.0F, -1.0F, 1.0F);
//        RenderSystem.translated(0, 0.25, 0);
//        skeletonHead.func_217104_a(0, 0, 0, 180, 0, 0.0625F);
//        RenderSystem.popMatrix();
//    }
}
