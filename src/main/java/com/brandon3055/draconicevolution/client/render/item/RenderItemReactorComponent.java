package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorComponent;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorComponent implements IItemRenderer {
    public RenderItemReactorComponent() {}

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
        Item item = stack.getItem();

        if (item == DEContent.reactor_core.asItem()) {
            mat.translate(0.5, 0.5, 0.5);
            mat.scale(1.5);
            RenderTileReactorCore.renderCore(mat, ccrs, (ClientEventHandler.elapsedTicks + mc.getFrameTime()) / 100F, 0F, 0.F, 0.5F, 0, getter);
        } else if (item == DEContent.reactor_stabilizer.asItem()) {
            float coreRotation = (ClientEventHandler.elapsedTicks + mc.getFrameTime()) * 5F;
            mat.translate(0.5, 0, 0.5);
            RenderTileReactorComponent.renderStabilizer(ccrs, mat, getter, coreRotation, 1F, packedLight, packedOverlay);
        } else if (item == DEContent.reactor_injector.asItem()) {
            mat.translate(0.5, 0.5, 0);
            mat.rotate(90 * MathHelper.torad, Vector3.X_POS);
            RenderTileReactorComponent.renderInjector(ccrs, mat, getter, 1F, packedLight, packedOverlay);
        } else {
            RenderTileReactorComponent.renderComponent(item, ccrs, mat, getter, packedLight, packedOverlay);
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
