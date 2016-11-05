package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.VertexBuffer;

/**
 * Created by brandon3055 on 31/10/2016.
 */
public class ModelBoxFace extends ModelBox {

    public final int face;

    public ModelBoxFace(ModelRenderer renderer, int textureOffsetX, int textureOffsetY, float offX, float offY, float offZ, int width, int height, int depth, int face) {
        super(renderer, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, 0);
        this.face = face;
    }

    @Override
    public void render(VertexBuffer renderer, float scale) {
        quadList[face].draw(renderer, scale);
    }
}
