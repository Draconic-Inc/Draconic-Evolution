package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.state.GlStateTracker;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorPart implements IItemRenderer {

    public static ModelReactorStabilizerCore modelBase = new ModelReactorStabilizerCore();
    public static ModelReactorStabilizerCore modelBaseRotors = new ModelReactorStabilizerCore();
    public static ModelReactorStabilizerRing modelRing = new ModelReactorStabilizerRing();

    public RenderItemReactorPart() {
        modelBaseRotors.rotor1R.childModels.clear();
        modelBaseRotors.rotor2R.childModels.clear();
    }

    //region Unused

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    //endregion

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return PerspectiveMapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK, cameraTransformType);
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateTracker.pushState();
        GlStateManager.translate(0.5, 0.5, 0.5);

//        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
//        GlStateManager.rotate(90, 1, 0, 0);
//        GlStateManager.translate(0, -0.58, 0);
//        GlStateManager.scale(0.95, 0.95, 0.95);

//        RenderTileReactorComponent.renderStabilizer(25, 0, 1F, 0, true, -1);

        switch (item.getItemDamage()) {
            case 0:
                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
                modelBase.basePlate.render(0.0625F);
                break;
            case 1:
                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
                modelBaseRotors.rotor1R.render(0.0625F);
                modelBaseRotors.rotor1R_1.render(0.0625F);
                modelBaseRotors.rotor1R_2.render(0.0625F);
                modelBaseRotors.rotor1R_3.render(0.0625F);
                modelBaseRotors.rotor1R_4.render(0.0625F);
                break;
            case 2:
                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
                modelBaseRotors.rotor2R.render(0.0625F);
                modelBaseRotors.rotor2R_1.render(0.0625F);
                modelBaseRotors.rotor2R_2.render(0.0625F);
                modelBaseRotors.rotor2R_3.render(0.0625F);
                modelBaseRotors.rotor2R_4.render(0.0625F);
                break;
            case 3:
                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
                GlStateManager.rotate(30F, 0F, 0F, 1F);
                modelBase.rotor1R.render(0.0625F);
                modelBase.hub1.render(0.0625F);
                GlStateManager.rotate(60F, 0F, 0F, -1F);
                modelBase.hub2.render(0.0625F);
                modelBase.rotor2R.render(0.0625F);
                break;
            case 4:
                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
                GlStateManager.rotate(90F, 0F, 0F, 1F);
                modelRing.render(null, -30, 1, 1, 0, 0, 1F / 16F);
                break;
        }
        GlStateTracker.popState();
        GlStateManager.popMatrix();
    }
}
