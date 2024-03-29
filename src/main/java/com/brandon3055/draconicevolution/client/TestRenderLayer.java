package com.brandon3055.draconicevolution.client;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.model.OBJParser;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by brandon3055 on 20/5/20.
 */
public class TestRenderLayer extends RenderLayer<LivingEntity, EntityModel<LivingEntity>> {

    EntityModel<LivingEntity> model;
    ModelPart renderOn;
    ModelPart.Cube box;

    private static RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/block/pylon_sphere_texture.png"));
    private CCModel trackerModel;

    public TestRenderLayer(RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> entityRenderer) {
        super(entityRenderer);
        Random rand = new Random();

        model = entityRenderer.getModel();
        List<ModelPart> rendererList = null;

        //Pretty sure "AgeableModel" is actually "LivingModel" because it seems to apply to all living entities regardless of whether or not that entity is actually ageable
        if (model instanceof AgeableListModel) {
            //This code could be converted to something a little smarter. II just wanted to retrieve a random box from the model to test if its possible to render on a random part of the entity.
//            rendererList = Lists.newArrayList(((AgeableModel<LivingEntity>)model).getBodyParts());
//            rendererList = Lists.newArrayList(((AgeableModel<LivingEntity>) model).getHeadParts());
        } else if (model instanceof ListModel) { //Because parrots have to be special...
            rendererList = Lists.newArrayList(((ListModel<LivingEntity>) model).parts());
        }
        if (rendererList != null && !rendererList.isEmpty()) {
            renderOn = rendererList.get(rand.nextInt(rendererList.size()));
            if (!renderOn.cubes.isEmpty()) {
                box = renderOn.cubes.get(rand.nextInt(renderOn.cubes.size()));
            }
        }

        //I just needed something to render
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/pylon_sphere.obj")).quads().ignoreMtl().parse();
        trackerModel = CCModel.combine(map.values()).backfacedCopy();
//        trackerModel.apply(new Scale(-0.35, -0.35, -0.35));
        trackerModel.computeNormals();
    }

    //
//    private boolean isAtEntityCenter(ModelRenderer.ModelBox box) {
//        float x = 0, y = 0, z = 0; //
//        return box.posX1 <= x && box.posY1 <= y && box.posZ1 <= z && box.posX2 >= x && box.posY2 >= y && box.posZ2 >= z;
//    }

    int i = 0;

    @Override
    public void render(PoseStack mStack, MultiBufferSource getter, int packedLightIn, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        if (!entity.getPersistentData().contains("wr:trackers")) return;
//        ListNBT trackers = entity.getPersistentData().getList("wr:trackers", 10);


//        for (INBT inbt : trackers) {
//            CompoundNBT nbt = (CompoundNBT) inbt;
//            Vector3 vec = Vector3.fromNBT(nbt.getCompound("vec"));

//            Matrix4 mat = new Matrix4(mStack);
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.brightness = 240;
//            ccrs.bind(modelType, getter);
//
////            mat.translate(0, entity., 0);
//
//            mat.scale(0.1);
//
//            trackerModel.render(ccrs, mat);


//        }


//        LogHelper.dev(trackers);

//        if (box == null) return;
//
//
//
//        Matrix4 mat = new Matrix4(mStack);
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.reset();
//        ccrs.brightness = 240;
//        ccrs.bind(modelType, getter);
//
//        //Translate and rotate to the ModelRenderer's reference frame... i think "reference frame" is the correct term.
//        mat.translate(renderOn.rotationPointX / 16.0F, renderOn.rotationPointY / 16.0F, renderOn.rotationPointZ / 16.0F);
//        mat.rotate(renderOn.rotateAngleZ, Vector3.Z_POS);
//        mat.rotate(renderOn.rotateAngleY, Vector3.Y_POS);
//        mat.rotate(renderOn.rotateAngleX, Vector3.X_POS);
//
//
//        mat.translate(box.posX1 / 16F, box.posY2 / 16F, box.posZ1 / 16F);
//        mat.scale(0.1);
//
////        mat.translate(0.5, (te.sphereOnTop.get() ? 1.5 : -0.5), 0.5);
////        mat.rotate(((ClientEventHandler.elapsedTicks + partialTicks) * 2F) * MathHelper.torad, new Vector3(0, 1, 0.5).normalize());
//        trackerModel.render(ccrs, mat);


    }
}
