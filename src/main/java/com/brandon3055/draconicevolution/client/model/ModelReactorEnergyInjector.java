package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

/**
 * ModelReactorPowerInjector - brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelReactorEnergyInjector extends Model {
    public ModelRenderer BasePlate;
    public ModelRenderer shape14;
    public ModelRenderer coreSupports;
    public ModelRenderer element1;
    public ModelRenderer support1;
    public ModelRenderer element7;
    public ModelRenderer element6;
    public ModelRenderer element5;
    public ModelRenderer element8;
    public ModelRenderer element2;
    public ModelRenderer element3;
    public ModelRenderer element4;
    public ModelRenderer support2;
    public ModelRenderer support3;
    public ModelRenderer support4;
    public ModelRenderer connector1;
    public ModelRenderer connector2;
    public ModelRenderer connector3;
    public ModelRenderer connector4;
    public ModelRenderer connector5;
    public ModelRenderer connector6;
    public ModelRenderer connector7;
    public ModelRenderer support8;
    public ModelRenderer connector8;
    public ModelRenderer support5;
    public ModelRenderer support6;
    public ModelRenderer support7;
    public float brightness;


    public ModelReactorEnergyInjector(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.connector5 = new ModelRenderer(this, 4, 19);
        this.connector5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector5.addBox(-3.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
        this.support4 = new ModelRenderer(this, 6, 21);
        this.support4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support4.addBox(3.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
        this.support6 = new ModelRenderer(this, 2, 26);
        this.support6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support6.addBox(4.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
        this.element2 = new ModelRenderer(this, 0, 12);
        this.element2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element2.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element2, 0.0F, 1.5707963267948966F, 0.0F);
        this.element4 = new ModelRenderer(this, 0, 12);
        this.element4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element4.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element4, 0.0F, 3.141592653589793F, 0.0F);
        this.element6 = new ModelRenderer(this, 0, 1);
        this.element6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element6.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element6, 0.0F, -1.5707963267948966F, 0.0F);
        this.connector2 = new ModelRenderer(this, 8, 19);
        this.connector2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector2.addBox(-4.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
        this.element5 = new ModelRenderer(this, 0, 1);
        this.element5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element5.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element5, 0.0F, 1.5707963267948966F, 0.0F);
        this.support7 = new ModelRenderer(this, 2, 26);
        this.support7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support7.addBox(-0.5F, 3.0F, -5.5F, 1, 4, 1, 0.0F);
        this.connector8 = new ModelRenderer(this, 4, 19);
        this.connector8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector8.addBox(-0.5F, 3.5F, 2.5F, 1, 1, 1, 0.0F);
        this.element7 = new ModelRenderer(this, 0, 1);
        this.element7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element7.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.support1 = new ModelRenderer(this, 42, 23);
        this.support1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.support1.addBox(-1.5F, 0.0F, 3.5F, 3, 7, 1, 0.0F);
        this.connector7 = new ModelRenderer(this, 4, 19);
        this.connector7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector7.addBox(-0.5F, 3.5F, -3.5F, 1, 1, 1, 0.0F);
        this.support5 = new ModelRenderer(this, 2, 26);
        this.support5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support5.addBox(-5.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
        this.support8 = new ModelRenderer(this, 2, 26);
        this.support8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support8.addBox(-0.5F, 3.0F, 4.5F, 1, 4, 1, 0.0F);
        this.shape14 = new ModelRenderer(this, 0, 17);
        this.shape14.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape14.addBox(-7.0F, 6.0F, -7.0F, 14, 1, 14, 0.0F);
        this.BasePlate = new ModelRenderer(this, 0, 0);
        this.BasePlate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BasePlate.addBox(-8.0F, 7.0F, -8.0F, 16, 1, 16, 0.0F);
        this.element8 = new ModelRenderer(this, 0, 1);
        this.element8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element8.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element8, 0.0F, 3.141592653589793F, 0.0F);
        this.element1 = new ModelRenderer(this, 0, 12);
        this.element1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.element1.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.connector4 = new ModelRenderer(this, 3, 21);
        this.connector4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector4.addBox(-0.5F, -1.0F, -4.5F, 1, 1, 2, 0.0F);
        this.connector6 = new ModelRenderer(this, 4, 19);
        this.connector6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector6.addBox(2.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
        this.support2 = new ModelRenderer(this, 42, 23);
        this.support2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support2.addBox(-1.5F, 0.0F, -4.5F, 3, 7, 1, 0.0F);
        this.support3 = new ModelRenderer(this, 6, 21);
        this.support3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support3.addBox(-4.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
        this.connector3 = new ModelRenderer(this, 3, 21);
        this.connector3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector3.addBox(-0.5F, -1.0F, 2.5F, 1, 1, 2, 0.0F);
        this.element3 = new ModelRenderer(this, 0, 12);
        this.element3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element3.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element3, 0.0F, -1.5707963267948966F, 0.0F);
        this.connector1 = new ModelRenderer(this, 8, 19);
        this.connector1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector1.addBox(2.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
        this.coreSupports = new ModelRenderer(this, 42, 17);
        this.coreSupports.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.coreSupports.addBox(-2.5F, 6.0F, -2.5F, 5, 1, 5, 0.0F);
        this.support1.addChild(this.connector5);
        this.support1.addChild(this.support4);
        this.support1.addChild(this.support6);
        this.element1.addChild(this.element2);
        this.element1.addChild(this.element4);
        this.element1.addChild(this.element6);
        this.support1.addChild(this.connector2);
        this.element1.addChild(this.element5);
        this.support1.addChild(this.support7);
        this.support1.addChild(this.connector8);
        this.element1.addChild(this.element7);
        this.support1.addChild(this.connector7);
        this.support1.addChild(this.support5);
        this.support1.addChild(this.support8);
        this.BasePlate.addChild(this.shape14);
        this.element1.addChild(this.element8);
        this.support1.addChild(this.connector4);
        this.support1.addChild(this.connector6);
        this.support1.addChild(this.support2);
        this.support1.addChild(this.support3);
        this.support1.addChild(this.connector3);
        this.element1.addChild(this.element3);
        this.support1.addChild(this.connector1);
    }

    @Override
    public void render(MatrixStack matrix, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        int light = Math.max((int)(brightness * 240), packedLightIn % 240);
        matrix.rotate(new Quaternion(90, 0, 0, true));
        this.support1.render(matrix, buffer, packedLightIn, packedOverlayIn);
        this.BasePlate.render(matrix, buffer, packedLightIn, packedOverlayIn);
        this.element1.render(matrix, buffer, light, packedOverlayIn);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
