package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.TransformUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;


/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemEnderEnergyManipulator implements IItemRenderer {

//    private final SkullModel skeletonHead = new SkullModel(0, 0, 64, 32);
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
//    private static ItemStack stack = new ItemStack(Items.SKULL, 1, 1);

    private static ShaderProgram shaderProgram;


    public RenderItemEnderEnergyManipulator() {
    }

    //region Unused
    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {

    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
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
