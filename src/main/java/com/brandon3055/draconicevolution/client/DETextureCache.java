package com.brandon3055.draconicevolution.client;

import codechicken.lib.texture.AtlasRegistrar;
import codechicken.lib.texture.IIconRegister;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by covers1624 on 7/25/2016.
 */
@Deprecated// Needs to be merged with DETextures.
public class DETextureCache implements IIconRegister {


    private static HashMap<ResourceLocation, TextureAtlasSprite> textureCache = new HashMap<ResourceLocation, TextureAtlasSprite>();
    private static ArrayList<ResourceLocation> locations = new ArrayList<ResourceLocation>();

    static {
        locations.add(new ResourceLocation(DraconicEvolution.MODID, "models/pylon_sphere_texture"));
        locations.add(new ResourceLocation(DraconicEvolution.MODID, "items/tools/obj/arrow_common"));
    }

    @Override
    public void registerIcons(AtlasRegistrar registrar) {
        textureCache.clear();
        for (ResourceLocation location : locations) {
//            textureCache.put(location, registrar.registerSprite(location));//TODO texture stuff
        }
    }

    public static TextureAtlasSprite getDETexture(String texture) {
        return textureCache.get(new ResourceLocation(DraconicEvolution.MODID, texture));
    }

}
