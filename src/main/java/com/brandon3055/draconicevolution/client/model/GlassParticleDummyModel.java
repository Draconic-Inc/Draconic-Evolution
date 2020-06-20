//package com.brandon3055.draconicevolution.client.model;
//
//import codechicken.lib.model.DummyBakedModel;
//import codechicken.lib.texture.TextureUtils;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//
//public class GlassParticleDummyModel extends DummyBakedModel {
//    public static final GlassParticleDummyModel INSTANCE = new GlassParticleDummyModel();
//    public TextureAtlasSprite sprite;
//
//    @Override
//    public TextureAtlasSprite getParticleTexture() {
//        if (sprite == null) {
//            sprite = TextureUtils.getBlockTexture("glass");
//        }
//        return sprite;
//    }
//}