package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import java.util.function.Function;

/**
 * Created by Werechang on 29/6/21
 */
public class ModelDraconiumChest extends Model {

    public ModelRenderer chestBottom;
    public ModelRenderer chestLid;
    public ModelRenderer chestLock;
    public ModelRenderer chest;
    public ModelRenderer chestTop;

    private int directionRotation = 0;
    
    public ModelDraconiumChest(Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
        super.texWidth = 64;
        super.texHeight = 64;

        this.chestBottom = new ModelRenderer(this, 0, 19);
        this.chestBottom.setPos(1, 6, 1);
        this.chestBottom.addBox(0, 0, 0, 14, 10, 14, 0);

        this.chestLid = new ModelRenderer(this, 0, 0);
        this.chestLid.setPos(1, -5, -14);
        this.chestLid.addBox(0, 0, 0, 14, 5, 14, 0);

        this.chestLock = new ModelRenderer(this, 0, 0);
        this.chestLock.setPos(7, -2, -15);
        this.chestLock.addBox(0, 0, 0, 2, 4, 1, 0);

        this.chestTop = new ModelRenderer(this, 0, 0);
        this.chestTop.setPos(0, 7, 15);
        this.chestTop.addChild(chestLid);
        this.chestTop.addChild(chestLock);

        this.chest = new ModelRenderer(this, 0, 0);
        this.chest.setPos(-8, -8, -8);
        this.chest.addChild(chestBottom);
        this.chest.addChild(chestTop);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrix.mulPose(new Quaternion(0, 0, 180, true));
        matrix.translate(-0.5, -0.5, 0.5);
        matrix.mulPose(Vector3f.YN.rotationDegrees(directionRotation));
        chest.render(matrix, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void setLidAngle(float angle) {
        chestTop.xRot = -angle;
    }

    public void setFacingDirection(int angle) {
        directionRotation = angle;
    }
}
