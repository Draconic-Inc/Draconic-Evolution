package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorComponent;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorComponent implements IItemRenderer {
    private int type;

    public RenderItemReactorComponent(int type) {
        this.type = type;
    }

    //region Unused

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        Minecraft mc = Minecraft.getInstance();

        switch (type) {
            case 0: //Core
                mat.translate(0.5, 0.5, 0.5);
                mat.scale(1.5);
                RenderTileReactorCore.renderCore(mat, ccrs, (ClientEventHandler.elapsedTicks + mc.getFrameTime()) / 100F, 0F, 0.F, 0.5F, 0, getter);
                break;
            case 1: //Stabilizer
                float coreRotation = (ClientEventHandler.elapsedTicks + mc.getFrameTime()) * 5F;
                mStack.translate(0.5, 0.5, 0.5);
                RenderTileReactorComponent.renderStabilizer(mStack, getter, coreRotation, 1F, packedLight, packedOverlay);
                break;
            case 2: //Injector
                mStack.translate(0.5, 0.5, 0.5);
                mStack.mulPose(new Quaternion(90, 0, 0, true));
                RenderTileReactorComponent.renderInjector(mStack, getter, 1F, packedLight, packedOverlay);
                break;
        }
    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}
