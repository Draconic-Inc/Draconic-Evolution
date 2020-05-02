package com.brandon3055.draconicevolution.client;

import codechicken.lib.texture.TextureUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

public class ModuleSpriteUploader extends SpriteUploader {
   public static final ResourceLocation LOCATION_MODULE_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/atlas/modules.png");

   public ModuleSpriteUploader() {
      super(Minecraft.getInstance().textureManager, LOCATION_MODULE_TEXTURE, "module");
      IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
      resourceManager.addReloadListener(this);
   }

   @Override
   protected Stream<ResourceLocation> getResourceLocations() {
      return DEModules.MODULE_REGISTRY.getKeys().stream();
   }

   public TextureAtlasSprite getSprite(Module<?> module) {
      ResourceLocation location = DEModules.MODULE_REGISTRY.getKey(module);
      if (location == null) {
         return TextureUtils.getMissingSprite();
      }
      return this.getSprite(location);
   }
}