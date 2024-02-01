package com.brandon3055.draconicevolution.client;

import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.gui.modular.sprite.ModAtlasHolder;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.BrandonsCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class DEGuiTextures {

    private static final ModAtlasHolder ATLAS_HOLDER = new ModAtlasHolder(MODID, "textures/atlas/gui.png", "gui");
    private static final Map<String, codechicken.lib.gui.modular.sprite.Material> MATERIAL_CACHE = new HashMap<>();

    /**
     * The returned AtlasLoader needs to be registered as a resource reload listener using the appropriate NeoForge / Fabric event.
     */
    public static ModAtlasHolder getAtlasHolder() {
        return ATLAS_HOLDER;
    }

    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(getAtlasHolder());
    }

    /**
     * Returns a cached Material for the specified gui texture.
     * Warning: Do not use this if you intend to use the material with multiple render types.
     * The material will cache the first render type it is used with.
     * Instead use {@link #getUncached(String)}
     *
     * @param texture The texture path relative to "modid:gui/"
     */
    public static Material get(String texture) {
        return MATERIAL_CACHE.computeIfAbsent(BrandonsCore.MODID + ":" + texture, e -> getUncached(texture));
    }

    public static Supplier<Material> getter(Supplier<String> texture) {
        return () -> get(texture.get());
    }

    public static Supplier<Material> getter(String texture) {
        return () -> get(texture);
    }

    public static Material getUncached(String texture) {
        return new Material(ATLAS_HOLDER.atlasLocation(), new ResourceLocation(BrandonsCore.MODID, "gui/" + texture), ATLAS_HOLDER::getSprite);
    }

    public static Material getThemed(String location) {
        return get((BCConfig.darkMode ? "dark/" : "light/") + location);
    }

    public static Supplier<Material> themedGetter(String location) {
        return () -> get((BCConfig.darkMode ? "dark/" : "light/") + location);
    }
}
