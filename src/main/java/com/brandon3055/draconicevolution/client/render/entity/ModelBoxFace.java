package com.brandon3055.draconicevolution.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;

/**
 * Created by brandon3055 on 31/10/2016.
 */
public class ModelBoxFace extends ModelBox {

    public final int face;

    public ModelBoxFace(RendererModel renderer, int textureOffsetX, int textureOffsetY, float offX, float offY, float offZ, int width, int height, int depth, int face) {
        super(renderer, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, 0);
        this.face = face;
    }

    @Override
    public void render(BufferBuilder renderer, float scale) {
        quads[face].draw(renderer, scale);
    }
}
