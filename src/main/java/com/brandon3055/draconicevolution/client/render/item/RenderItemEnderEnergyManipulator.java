package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 18/04/2017.
 */
public class RenderItemEnderEnergyManipulator implements IItemRenderer, IPerspectiveAwareModel {

    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private ItemCameraTransforms.TransformType transformType;

    private static ItemStack stack = new ItemStack(Items.SKULL, 1, 1);


    public RenderItemEnderEnergyManipulator() {
    }

    //region Unused
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return new ArrayList<>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    //endregion

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        this.transformType = cameraTransformType;
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_ITEM.getTransforms(), cameraTransformType);
    }

    //    @Override
//    public void renderItem(ItemStack item) {
//        Minecraft mc = Minecraft.getMinecraft();
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(0.5, 0.5, 0.5);
//
//        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
//
//        if (DEShaders.useShaders()) {
//            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
//            DEShaders.reactorOp.setIntensity(0.09F);
//            DEShaders.reactorShield.freeBindShader();
//            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
//
//            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
//            DEShaders.reactorOp.setIntensity(0.02F);
//            DEShaders.reactorShield.freeBindShader();
//            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
//            ShaderProgram.unbindShader();
//        }
//
//        GlStateManager.popMatrix();
//    }
    @Override
    public void renderItem(ItemStack item) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);

        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            GlStateManager.rotate(180, 0, 1, 0);
        }

        renderSkull();

        if (DEShaders.useShaders()) {
            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / -100F);
            DEShaders.reactorOp.setIntensity(0.09F);
            DEShaders.reactorShield.freeBindShader();
            renderSkull();
            DEShaders.reactorOp.setAnimation(((float) ClientEventHandler.elapsedTicks + mc.getRenderPartialTicks()) / 100F);
            DEShaders.reactorOp.setIntensity(0.02F);
            DEShaders.reactorShield.freeBindShader();
            renderSkull();
            ShaderProgram.unbindShader();
        }

        GlStateManager.popMatrix();
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
