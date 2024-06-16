package com.brandon3055.draconicevolution.client;

import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.gui.modular.sprite.ModAtlasHolder;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class ModuleTextures {
   private static final ModAtlasHolder ATLAS_HOLDER = new ModAtlasHolder(MODID, "textures/atlas/module.png", "module");
   private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();

   public static ModAtlasHolder getAtlasHolder() {
      return ATLAS_HOLDER;
   }

   protected static Material get(ResourceLocation texture) {
      return MATERIAL_CACHE.computeIfAbsent(texture.getNamespace() + ":" + texture.getPath(), e -> getUncached(texture));
   }

   protected static Material getUncached(ResourceLocation texture) {
      return new Material(ATLAS_HOLDER.atlasLocation(), new ResourceLocation(texture.getNamespace(), "module/" + texture.getPath()), ATLAS_HOLDER::getSprite);
   }

   public static Material get(Module<?> module) {
      ResourceLocation location = DEModules.REGISTRY.getKey(module);
      return get(location);
   }
}