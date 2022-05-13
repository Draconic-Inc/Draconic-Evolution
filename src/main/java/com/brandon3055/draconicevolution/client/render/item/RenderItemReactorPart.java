package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemReactorPart implements IItemRenderer {

    public static final ResourceLocation REACTOR_STABILIZER = new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_stabilizer_core.png");
    public static final ResourceLocation REACTOR_STABILIZER_RING = new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_stabilizer_ring.png");
    public static ModelReactorStabilizerCore stabilizerModel = new ModelReactorStabilizerCore(RenderType::entitySolid);
    public static ModelReactorStabilizerCore stabilizerModelCombined = new ModelReactorStabilizerCore(RenderType::entitySolid);
    public static ModelReactorStabilizerRing stabilizerRingModel = new ModelReactorStabilizerRing(RenderType::entitySolid);

    public RenderItemReactorPart() {
        stabilizerModel.brightness = 1F;
        stabilizerModelCombined.brightness = 1F;
        stabilizerRingModel.brightness = 1F;
//        stabilizerModel.rotor2R.children.clear();
//        stabilizerModel.rotor1R.children.clear();
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
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.reset();
//        ccrs.brightness = packedLight;
//        ccrs.overlay = packedOverlay;
//
//        if (stack.getItem() == DEContent.reactor_prt_stab_frame) {
//            mStack.translate(0.5, 0.5, 0.5);
//            stabilizerModel.basePlate.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//        } else if (stack.getItem() == DEContent.reactor_prt_in_rotor) {
//            mStack.translate(0.3, 0.5, 0.5);
//            mStack.scale(1.5F, 1.5F, 1.5F);
//            stabilizerModel.rotor1R.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor1R_1.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor1R_2.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor1R_3.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor1R_4.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//        } else if (stack.getItem() == DEContent.reactor_prt_out_rotor) {
//            mStack.translate(0.3, 0.5, 0.5);
//            mStack.scale(1.5F, 1.5F, 1.5F);
//            stabilizerModel.rotor2R.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor2R_1.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor2R_2.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor2R_3.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModel.rotor2R_4.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//        } else if (stack.getItem() == DEContent.reactor_prt_rotor_full) {
//            mStack.translate(0.5, 0.5, 0.5);
//            stabilizerModelCombined.rotor1R.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModelCombined.hub1.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            mStack.mulPose(new Quaternion(0, 0, -60, true));
//            stabilizerModelCombined.hub2.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//            stabilizerModelCombined.rotor2R.render(mStack, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay);
//        } else if (stack.getItem() == DEContent.reactor_prt_focus_ring) {
//            mStack.translate(0.5, 0.5, 0.5);
//            stabilizerRingModel.renderToBuffer(mStack, getter.getBuffer(stabilizerRingModel.renderType(REACTOR_STABILIZER_RING)), packedLight, packedOverlay, 1F, 1F, 1F, 1F);
//        }
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
//        RenderSystem.pushMatrix();
//        GlStateTracker.pushState();
//        RenderSystem.translate(0.5, 0.5, 0.5);
//
////        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
////        RenderSystem.rotate(90, 1, 0, 0);
////        RenderSystem.translate(0, -0.58, 0);
////        RenderSystem.scale(0.95, 0.95, 0.95);
//
////        RenderTileReactorComponent.renderStabilizer(25, 0, 1F, 0, true, -1);
//
//        switch (item.getItemDamage()) {
//            case 0:
//                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
//                modelBase.basePlate.render(0.0625F);
//                break;
//            case 1:
//                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
//                modelBaseRotors.rotor1R.render(0.0625F);
//                modelBaseRotors.rotor1R_1.render(0.0625F);
//                modelBaseRotors.rotor1R_2.render(0.0625F);
//                modelBaseRotors.rotor1R_3.render(0.0625F);
//                modelBaseRotors.rotor1R_4.render(0.0625F);
//                break;
//            case 2:
//                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
//                modelBaseRotors.rotor2R.render(0.0625F);
//                modelBaseRotors.rotor2R_1.render(0.0625F);
//                modelBaseRotors.rotor2R_2.render(0.0625F);
//                modelBaseRotors.rotor2R_3.render(0.0625F);
//                modelBaseRotors.rotor2R_4.render(0.0625F);
//                break;
//            case 3:
//                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
//                RenderSystem.rotate(30F, 0F, 0F, 1F);
//                modelBase.rotor1R.render(0.0625F);
//                modelBase.hub1.render(0.0625F);
//                RenderSystem.rotate(60F, 0F, 0F, -1F);
//                modelBase.hub2.render(0.0625F);
//                modelBase.rotor2R.render(0.0625F);
//                break;
//            case 4:
//                ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
//                RenderSystem.rotate(90F, 0F, 0F, 1F);
//                modelRing.render(null, -30, 1, 1, 0, 0, 1F / 16F);
//                break;
//        }
//        GlStateTracker.popState();
//        RenderSystem.popMatrix();
//    }
}
