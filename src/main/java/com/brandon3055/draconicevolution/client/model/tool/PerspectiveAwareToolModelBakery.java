package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.render.TextureUtils;
import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;

/**
 * Created by brandon3055 on 28/07/2016.
 */
public class PerspectiveAwareToolModelBakery {

    private final ResourceLocation simpleTexture;
    private final ResourceLocation objTexture;
    private final ResourceLocation objLocation;

    public PerspectiveAwareToolModelBakery(Set3<ResourceLocation, ResourceLocation, ResourceLocation> resourceSet) {
        this.simpleTexture = resourceSet.getA();
        this.objTexture = resourceSet.getA();
        this.objLocation = resourceSet.getC();
    }

    public PerspectiveAwareToolModel bake(IModelState state, ToolTransformOverride transformOverride) {

        TextureAtlasSprite particle = TextureUtils.bakedTextureGetter.apply(simpleTexture);
        IBakedModel model2D = new ItemLayerModel(ImmutableList.of(simpleTexture)).bake(state, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
        IBakedModel objModel;

        try {
            objModel = OBJLoader.INSTANCE.loadModel(objLocation).bake(state, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
        }
        catch (Exception e) {
            LogHelper.error("Failed to load tool model " + objLocation);
            throw new RuntimeException(e);
        }

        return new PerspectiveAwareToolModel(ImmutableList.copyOf(model2D.getQuads(null, null, 0)), objModel, particle, transformOverride);
    }

}
