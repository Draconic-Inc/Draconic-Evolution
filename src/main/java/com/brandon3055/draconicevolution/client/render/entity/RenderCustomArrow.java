package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.entity.EntityCustomArrow;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 3/3/2016.
 */
public class RenderCustomArrow extends EntityRenderer<EntityCustomArrow> {
    private IBakedModel arrowModel = null;
//    private TextureAtlasSprite fullSprite = new FullAtlasSprite();

    public RenderCustomArrow(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCustomArrow entity) {
        return entity.bowProperties.energyBolt ? ResourceHelperDE.getResource(DETextures.REACTOR_CORE) : ResourceHelperDE.getResource("items/tools/obj/arrow_common");
    }

//    @Override
//    public void doRender(EntityCustomArrow entityArrow, double x, double y, double z, float f1, float f2) {
//        if (arrowModel == null) { //For some reason doing this in the constructor does not work
//            try {
////                arrowModel = OBJLoader.INSTANCE.loadModel(ResourceHelperDE.getResource("models/item/tools/arrow_common.obj")).bake(TransformUtils.DEFAULT_ITEM, DefaultVertexFormats.ITEM, TextureUtils::getTexture);
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        RenderSystem.pushMatrix();
//        RenderSystem.translatef((float) x, (float) y, (float) z);
//        RenderSystem.rotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * f2 - 90.0F, 0.0F, 1.0F, 0.0F);
//        RenderSystem.rotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * f2, 0.0F, 0.0F, 1.0F);
//
//        float f10 = 0.3F;
//        float f11 = (float) entityArrow.arrowShake - f2;
//
//        if (f11 > 0.0F) {
//            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
//            RenderSystem.rotatef(f12, 0.0F, 0.0F, 1.0F);
//        }
//
//        RenderSystem.rotatef(90.0F, 0.0F, -1.0F, 0.0F);
//        RenderSystem.scalef(f10, f10, f10);
//        RenderSystem.enableBlend();
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1F, 0F, 0F, 0.6F);
//
//        if (entityArrow.bowProperties != null && entityArrow.bowProperties.energyBolt) {
//            bindEntityTexture(entityArrow);
//            ModelUtils.renderQuads(arrowModel.getQuads(null, null, ModelUtils.rand));
//
//            RenderSystem.translated(0, -0.05, 0);
//            RenderSystem.scaled(1.1, 1.1, 1.02);
//            RenderSystem.scaled(1.1, 1.1, 1.02);
//            ModelUtils.renderQuadsARGB(arrowModel.getQuads(null, null, ModelUtils.rand), 0x66FF0000);
//        }
//        else {
//            TextureUtils.bindBlockTexture();
//            ModelUtils.renderQuads(arrowModel.getQuads(null, null, ModelUtils.rand));
//        }
//
//        RenderSystem.disableRescaleNormal();
//        RenderSystem.disableBlend();
//        RenderSystem.popMatrix();
//    }
//


}
