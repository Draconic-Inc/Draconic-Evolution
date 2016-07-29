package com.brandon3055.draconicevolution.client.model.toolold;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class ToolModelBakery {
    private static Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
        @Override
        public TextureAtlasSprite apply(ResourceLocation input) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
        }
    };

    public static IBakedModel bake(IModelState state, ResourceLocation toolTexture, ResourceLocation modelLocation) throws Exception{
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        TextureAtlasSprite particle = bakedTextureGetter.apply(toolTexture);
        ImmutableList<ResourceLocation> textures = ImmutableList.of(toolTexture);
        IBakedModel layerModel = new ItemLayerModel(textures).bake(state, DefaultVertexFormats.ITEM, bakedTextureGetter);
        IBakedModel objModel = OBJLoader.INSTANCE.loadModel(modelLocation).bake(state, DefaultVertexFormats.ITEM, bakedTextureGetter);
        builder.addAll(layerModel.getQuads(null, null, 0));
        return new BakedToolModel(builder.build(), particle, objModel);
    }

}
