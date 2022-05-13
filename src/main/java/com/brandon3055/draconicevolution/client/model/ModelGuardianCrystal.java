package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * ModelChaosCrystal - Mojang-(Original) modified by brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelGuardianCrystal extends Model {
    public ModelPart base;
    public ModelPart glass;
    public ModelPart cube;
    public ModelPart ringBase1;
    public ModelPart RingSegment1;
    public ModelPart RingSegment2;
    public ModelPart RingSegment3;
    public ModelPart RingSegment4;
    public ModelPart ringBase2;
    public ModelPart RingSegment1_1;
    public ModelPart RingSegment2_1;
    public ModelPart RingSegment3_1;
    public ModelPart RingSegment4_1;

    public ModelGuardianCrystal(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
//        this.texWidth = 64;
//        this.texHeight = 64;
//        this.RingSegment3_1 = new ModelPart(this, 12, 38);
//        this.RingSegment3_1.setPos(0.0F, 0.0F, -10.0F);
//        this.RingSegment3_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.setRotateAngle(RingSegment3_1, 0.0F, 1.5707963267948966F, 0.0F);
//        this.RingSegment3 = new ModelPart(this, 12, 32);
//        this.RingSegment3.setPos(0.0F, 0.0F, -10.0F);
//        this.RingSegment3.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.setRotateAngle(RingSegment3, 0.0F, 1.5707963267948966F, 0.0F);
//        this.cube = new ModelPart(this, 32, 0);
//        this.cube.setPos(0.0F, 0.0F, 0.0F);
//        this.cube.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.RingSegment2_1 = new ModelPart(this, 24, 38);
//        this.RingSegment2_1.setPos(-10.0F, 0.0F, 0.0F);
//        this.RingSegment2_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.ringBase2 = new ModelPart(this, 0, 0);
//        this.ringBase2.setPos(0.0F, 0.0F, 0.0F);
//        this.ringBase2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
//        this.setRotateAngle(ringBase2, 0.0F, 0.7853981633974483F, 0.0F);
//        this.base = new ModelPart(this, 0, 16);
//        this.base.setPos(0.0F, 0.0F, 0.0F);
//        this.base.addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12, 0.0F);
//        this.ringBase1 = new ModelPart(this, 0, 0);
//        this.ringBase1.setPos(0.0F, 0.0F, 0.0F);
//        this.ringBase1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
//        this.RingSegment1 = new ModelPart(this, 0, 32);
//        this.RingSegment1.setPos(10.0F, 0.0F, 0.0F);
//        this.RingSegment1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.glass = new ModelPart(this, 0, 0);
//        this.glass.setPos(0.0F, 0.0F, 0.0F);
//        this.glass.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
//        this.RingSegment4_1 = new ModelPart(this, 36, 38);
//        this.RingSegment4_1.setPos(0.0F, 0.0F, 10.0F);
//        this.RingSegment4_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.setRotateAngle(RingSegment4_1, 0.0F, 1.5707963267948966F, 0.0F);
//        this.RingSegment4 = new ModelPart(this, 36, 32);
//        this.RingSegment4.setPos(0.0F, 0.0F, 10.0F);
//        this.RingSegment4.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.setRotateAngle(RingSegment4, 0.0F, 1.5707963267948966F, 0.0F);
//        this.RingSegment2 = new ModelPart(this, 24, 32);
//        this.RingSegment2.setPos(-10.0F, 0.0F, 0.0F);
//        this.RingSegment2.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.RingSegment1_1 = new ModelPart(this, 0, 38);
//        this.RingSegment1_1.setPos(10.0F, 0.0F, 0.0F);
//        this.RingSegment1_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
//        this.ringBase2.addChild(this.RingSegment3_1);
//        this.ringBase1.addChild(this.RingSegment3);
//        this.ringBase2.addChild(this.RingSegment2_1);
//        this.ringBase1.addChild(this.ringBase2);
//        this.ringBase1.addChild(this.RingSegment1);
//        this.ringBase2.addChild(this.RingSegment4_1);
//        this.ringBase1.addChild(this.RingSegment4);
//        this.ringBase1.addChild(this.RingSegment2);
//        this.ringBase2.addChild(this.RingSegment1_1);
    }

//    public void render(Entity entity, float f1, float f2, float f3, float deathAnimation, float health, float scale) {
//        RenderSystem.pushMatrix();
//        RenderSystem.scalef(2.0F, 2.0F, 2.0F);
//        RenderSystem.translatef(0.0F, -0.5F, 0.0F);
//
//        if (this.base != null) {
//            this.base.render(scale);
//        }
//
//        RenderSystem.pushMatrix();
//        float s = 1.1F + (float) Math.sin(f2 / 20) * 0.1F;
//        RenderSystem.translatef(0.0F, 0.1F + deathAnimation * 0.9F, 0.0F);
//        RenderSystem.rotatef(f2 * 2, 0.0F, 1.0F, 0.1F);
//        RenderSystem.scalef(s, s, s);
//        this.ringBase1.render(scale);
//        RenderSystem.popMatrix();
//
//        RenderSystem.rotatef(f2, 0.0F, 1.0F, 0.0F);
//        RenderSystem.translatef(0.0F, 0.7F + deathAnimation * (0.1F + f3), 0.0F);
//        RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
//        this.glass.render(scale);
//        float f6 = 0.875F;
//        RenderSystem.scalef(f6, f6, f6);
//        RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
//        RenderSystem.rotatef(f2, 0.0F, 1.0F, 0.0F);
//        this.glass.render(scale);
//        RenderSystem.scalef(f6, f6, f6);
//        RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
//        RenderSystem.rotatef(f2, 0.0F, 1.0F, 0.0F);
//        this.cube.render(scale);
//        RenderSystem.popMatrix();
//    }

    /**
     * This is a helper function from Tabula to set the ROTATION of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

    }
}
