package com.brandon3055.draconicevolution.client;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
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
        locations.add(new ResourceLocation(DraconicEvolution.MOD_PREFIX + "models/pylon_sphere_texture"));
        locations.add(new ResourceLocation(DraconicEvolution.MOD_PREFIX + "items/tools/obj/arrow_common"));
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        textureCache.clear();
        for (ResourceLocation location : locations) {
            textureCache.put(location, textureMap.registerSprite(location));
        }
    }


    public static TextureAtlasSprite getDETexture(String texture) {
        return textureCache.get(new ResourceLocation(DraconicEvolution.MOD_PREFIX + texture));
    }

}
