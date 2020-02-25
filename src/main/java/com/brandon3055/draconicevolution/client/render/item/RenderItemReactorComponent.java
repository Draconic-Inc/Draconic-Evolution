package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.IModelState;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorComponent implements IItemRenderer {

    private static RenderTileReactorCore coreRenderer = new RenderTileReactorCore();

    public RenderItemReactorComponent() {
    }

    //region Unused

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    //endregion

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {

    }

    //    @Override
//    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
//        boolean isCore = item.getItem() == Item.getItemFromBlock(DEFeatures.reactorCore);
//        boolean isStabilizer = !isCore && item.getItemDamage() == 0;
//
//
//        GlStateManager.pushMatrix();
//        GlStateTracker.pushState();
//
//        if (isCore) {
//            coreRenderer.renderItem();
//        }
//        else if (isStabilizer) {
//            GlStateManager.translate(0.5, 0.5, 0.5);
//            float partial = Minecraft.getInstance().getRenderPartialTicks();
//            RenderTileReactorComponent.renderStabilizer(ClientEventHandler.elapsedTicks + partial, (ClientEventHandler.elapsedTicks + partial) * 0.6F, 1F, 0, true, -1);
//        }
//        else {
//            GlStateManager.translate(0.5, 0.5, 0.5);
//            GlStateManager.rotate(90, 1, 0, 0);
//            RenderTileReactorComponent.renderInjector(1F, 0, true, -1);
//        }
//
//        GlStateTracker.popState();
//        GlStateManager.popMatrix();
//    }
}
