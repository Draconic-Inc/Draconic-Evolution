package com.brandon3055.draconicevolution.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * Created by Brandon on 25/10/2014.
 */
public class ModelTeleporterStand extends ModelBase {

    ModelRenderer plate;
    ModelRenderer holders;
    ModelRenderer support;
    ModelRenderer collum;
    ModelRenderer baseTop;
    ModelRenderer baseBottom;

    public ModelTeleporterStand() {
        textureWidth = 64;
        textureHeight = 64;

        plate = new ModelRenderer(this, 0, 0);
        plate.addBox(0f, 1f, 0f, 7, 1, 7);
        plate.setRotationPoint(-3.5F, -7.8F, -2F);
        plate.setTextureSize(64, 64);
        setRotationDegree(plate, -30, 0, 0);

        holders = new ModelRenderer(this, 0, 0);
        holders.addBox(0f, 0f, 0f, 1, 1, 1);
        holders.addBox(5.55f, 0f, 0f, 1, 1, 1);
        holders.setRotationPoint(-3.3F, -4.7F, 2.86F);
        holders.setTextureSize(64, 64);
        setRotationDegree(holders, -30, 0, 0);

        support = new ModelRenderer(this, 0, 8);
        support.addBox(0f, 0f, 0.1f, 4, 1, 1);
        support.setRotationPoint(-2F, -5F, -2F);
        support.setTextureSize(64, 64);
        setRotation(support, 0F, 0F, 0F);

        collum = new ModelRenderer(this, 28, 0);
        collum.addBox(0f, 0f, 0f, 4, 12, 4);
        collum.setRotationPoint(-2F, -4F, -2F);
        collum.setTextureSize(64, 64);
        setRotation(collum, 0F, 0F, 0F);

        baseTop = new ModelRenderer(this, 0, 27);
        baseTop.addBox(0f, 0f, 0f, 6, 1, 6);
        baseTop.setRotationPoint(-3F, 6F, -3F);
        baseTop.setTextureSize(64, 64);
        setRotation(baseTop, 0F, 0F, 0F);

        baseBottom = new ModelRenderer(this, 0, 16);
        baseBottom.addBox(0f, 0f, 0f, 10, 1, 10);
        baseBottom.setRotationPoint(-5F, 7F, -5F);
        baseBottom.setTextureSize(64, 64);
        setRotationDegree(baseBottom, 0, 0, 0);
    }

    public void render() {
        final float scale = 1F / 16;

        plate.render(scale);
        holders.render(scale);
        support.render(scale);
        collum.render(scale);
        baseTop.render(scale);
        baseBottom.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    /**
     * Sets rotation in degrees (0-360)
     */
    private void setRotationDegree(ModelRenderer model, int x, int y, int z) {
        model.rotateAngleX = x / (180F / (float) Math.PI);
        model.rotateAngleY = y / (180F / (float) Math.PI);
        model.rotateAngleZ = z / (180F / (float) Math.PI);
    }
}
