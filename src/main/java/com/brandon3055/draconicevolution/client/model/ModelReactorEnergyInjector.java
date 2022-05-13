package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * ModelReactorPowerInjector - brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelReactorEnergyInjector extends Model {
//    public ModelPart BasePlate;
//    public ModelPart shape14;
//    public ModelPart coreSupports;
//    public ModelPart element1;
//    public ModelPart support1;
//    public ModelPart element7;
//    public ModelPart element6;
//    public ModelPart element5;
//    public ModelPart element8;
//    public ModelPart element2;
//    public ModelPart element3;
//    public ModelPart element4;
//    public ModelPart support2;
//    public ModelPart support3;
//    public ModelPart support4;
//    public ModelPart connector1;
//    public ModelPart connector2;
//    public ModelPart connector3;
//    public ModelPart connector4;
//    public ModelPart connector5;
//    public ModelPart connector6;
//    public ModelPart connector7;
//    public ModelPart support8;
//    public ModelPart connector8;
//    public ModelPart support5;
//    public ModelPart support6;
//    public ModelPart support7;
    public float brightness;


    public ModelReactorEnergyInjector(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
//        this.texWidth = 64;
//        this.texHeight = 32;
//        this.connector5 = new ModelPart(this, 4, 19);
//        this.connector5.setPos(0.0F, 0.0F, 0.0F);
//        this.connector5.addBox(-3.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
//        this.support4 = new ModelPart(this, 6, 21);
//        this.support4.setPos(0.0F, 0.0F, 0.0F);
//        this.support4.addBox(3.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
//        this.support6 = new ModelPart(this, 2, 26);
//        this.support6.setPos(0.0F, 0.0F, 0.0F);
//        this.support6.addBox(4.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
//        this.element2 = new ModelPart(this, 0, 12);
//        this.element2.setPos(0.0F, 0.0F, 0.0F);
//        this.element2.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
//        this.setRotateAngle(element2, 0.0F, 1.5707963267948966F, 0.0F);
//        this.element4 = new ModelPart(this, 0, 12);
//        this.element4.setPos(0.0F, 0.0F, 0.0F);
//        this.element4.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
//        this.setRotateAngle(element4, 0.0F, 3.141592653589793F, 0.0F);
//        this.element6 = new ModelPart(this, 0, 1);
//        this.element6.setPos(0.0F, 0.0F, 0.0F);
//        this.element6.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
//        this.setRotateAngle(element6, 0.0F, -1.5707963267948966F, 0.0F);
//        this.connector2 = new ModelPart(this, 8, 19);
//        this.connector2.setPos(0.0F, 0.0F, 0.0F);
//        this.connector2.addBox(-4.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
//        this.element5 = new ModelPart(this, 0, 1);
//        this.element5.setPos(0.0F, 0.0F, 0.0F);
//        this.element5.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
//        this.setRotateAngle(element5, 0.0F, 1.5707963267948966F, 0.0F);
//        this.support7 = new ModelPart(this, 2, 26);
//        this.support7.setPos(0.0F, 0.0F, 0.0F);
//        this.support7.addBox(-0.5F, 3.0F, -5.5F, 1, 4, 1, 0.0F);
//        this.connector8 = new ModelPart(this, 4, 19);
//        this.connector8.setPos(0.0F, 0.0F, 0.0F);
//        this.connector8.addBox(-0.5F, 3.5F, 2.5F, 1, 1, 1, 0.0F);
//        this.element7 = new ModelPart(this, 0, 1);
//        this.element7.setPos(0.0F, 0.0F, 0.0F);
//        this.element7.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
//        this.support1 = new ModelPart(this, 42, 23);
//        this.support1.setPos(0.0F, -1.0F, 0.0F);
//        this.support1.addBox(-1.5F, 0.0F, 3.5F, 3, 7, 1, 0.0F);
//        this.connector7 = new ModelPart(this, 4, 19);
//        this.connector7.setPos(0.0F, 0.0F, 0.0F);
//        this.connector7.addBox(-0.5F, 3.5F, -3.5F, 1, 1, 1, 0.0F);
//        this.support5 = new ModelPart(this, 2, 26);
//        this.support5.setPos(0.0F, 0.0F, 0.0F);
//        this.support5.addBox(-5.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
//        this.support8 = new ModelPart(this, 2, 26);
//        this.support8.setPos(0.0F, 0.0F, 0.0F);
//        this.support8.addBox(-0.5F, 3.0F, 4.5F, 1, 4, 1, 0.0F);
//        this.shape14 = new ModelPart(this, 0, 17);
//        this.shape14.setPos(0.0F, 0.0F, 0.0F);
//        this.shape14.addBox(-7.0F, 6.0F, -7.0F, 14, 1, 14, 0.0F);
//        this.BasePlate = new ModelPart(this, 0, 0);
//        this.BasePlate.setPos(0.0F, 0.0F, 0.0F);
//        this.BasePlate.addBox(-8.0F, 7.0F, -8.0F, 16, 1, 16, 0.0F);
//        this.element8 = new ModelPart(this, 0, 1);
//        this.element8.setPos(0.0F, 0.0F, 0.0F);
//        this.element8.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
//        this.setRotateAngle(element8, 0.0F, 3.141592653589793F, 0.0F);
//        this.element1 = new ModelPart(this, 0, 12);
//        this.element1.setPos(0.0F, -1.0F, 0.0F);
//        this.element1.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
//        this.connector4 = new ModelPart(this, 3, 21);
//        this.connector4.setPos(0.0F, 0.0F, 0.0F);
//        this.connector4.addBox(-0.5F, -1.0F, -4.5F, 1, 1, 2, 0.0F);
//        this.connector6 = new ModelPart(this, 4, 19);
//        this.connector6.setPos(0.0F, 0.0F, 0.0F);
//        this.connector6.addBox(2.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
//        this.support2 = new ModelPart(this, 42, 23);
//        this.support2.setPos(0.0F, 0.0F, 0.0F);
//        this.support2.addBox(-1.5F, 0.0F, -4.5F, 3, 7, 1, 0.0F);
//        this.support3 = new ModelPart(this, 6, 21);
//        this.support3.setPos(0.0F, 0.0F, 0.0F);
//        this.support3.addBox(-4.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
//        this.connector3 = new ModelPart(this, 3, 21);
//        this.connector3.setPos(0.0F, 0.0F, 0.0F);
//        this.connector3.addBox(-0.5F, -1.0F, 2.5F, 1, 1, 2, 0.0F);
//        this.element3 = new ModelPart(this, 0, 12);
//        this.element3.setPos(0.0F, 0.0F, 0.0F);
//        this.element3.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
//        this.setRotateAngle(element3, 0.0F, -1.5707963267948966F, 0.0F);
//        this.connector1 = new ModelPart(this, 8, 19);
//        this.connector1.setPos(0.0F, 0.0F, 0.0F);
//        this.connector1.addBox(2.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
//        this.coreSupports = new ModelPart(this, 42, 17);
//        this.coreSupports.setPos(0.0F, -1.0F, 0.0F);
//        this.coreSupports.addBox(-2.5F, 6.0F, -2.5F, 5, 1, 5, 0.0F);
//        this.support1.addChild(this.connector5);
//        this.support1.addChild(this.support4);
//        this.support1.addChild(this.support6);
//        this.element1.addChild(this.element2);
//        this.element1.addChild(this.element4);
//        this.element1.addChild(this.element6);
//        this.support1.addChild(this.connector2);
//        this.element1.addChild(this.element5);
//        this.support1.addChild(this.support7);
//        this.support1.addChild(this.connector8);
//        this.element1.addChild(this.element7);
//        this.support1.addChild(this.connector7);
//        this.support1.addChild(this.support5);
//        this.support1.addChild(this.support8);
//        this.BasePlate.addChild(this.shape14);
//        this.element1.addChild(this.element8);
//        this.support1.addChild(this.connector4);
//        this.support1.addChild(this.connector6);
//        this.support1.addChild(this.support2);
//        this.support1.addChild(this.support3);
//        this.support1.addChild(this.connector3);
//        this.element1.addChild(this.element3);
//        this.support1.addChild(this.connector1);
    }

    @Override
    public void renderToBuffer(PoseStack matrix, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        int light = Math.max((int)(brightness * 240), packedLightIn % 240);
        matrix.mulPose(new Quaternion(90, 0, 0, true));
//        this.support1.render(matrix, buffer, packedLightIn, packedOverlayIn);
//        this.BasePlate.render(matrix, buffer, packedLightIn, packedOverlayIn);
//        this.element1.render(matrix, buffer, light, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
