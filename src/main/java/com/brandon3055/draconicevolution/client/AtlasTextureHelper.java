package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 02/02/2024
 */
public class AtlasTextureHelper {
    private static final Map<ResourceLocation, Function<ResourceLocation, TextureAtlasSprite>> ATLAS_CACHE = new HashMap<>();

    public static TextureAtlasSprite[] ENERGY_PARTICLE = new TextureAtlasSprite[5];
    public static TextureAtlasSprite[] SPARK_PARTICLE = new TextureAtlasSprite[7];
    public static TextureAtlasSprite[] SPELL_PARTICLE = new TextureAtlasSprite[7];
    public static TextureAtlasSprite[] MIXED_PARTICLE;

    public static TextureAtlasSprite ORB_PARTICLE;
    public static TextureAtlasSprite PORTAL_PARTICLE;

    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
        public void begin(BufferBuilder builder, TextureManager manager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tessellator) {
            tessellator.end();
        }

        public String toString() {
            return "TERRAIN_SHEET_TRANSLUCENT";
        }
    };

    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> {
            ATLAS_CACHE.clear();
            for (int i = 0; i < ENERGY_PARTICLE.length; i++) {
                ENERGY_PARTICLE[i] = getAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation(DraconicEvolution.MODID, "particle/energy_" + i));
            }
            for (int i = 0; i < SPARK_PARTICLE.length; i++) {
                SPARK_PARTICLE[i] = getAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation(DraconicEvolution.MODID, "particle/spark_" + i));
            }
            for (int i = 0; i < SPELL_PARTICLE.length; i++) {
                SPELL_PARTICLE[i] = getAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation(DraconicEvolution.MODID, "particle/spell_" + i));
            }
            MIXED_PARTICLE = Stream.concat(Arrays.stream(SPARK_PARTICLE), Arrays.stream(SPELL_PARTICLE)).toArray(TextureAtlasSprite[]::new);

            ORB_PARTICLE = getAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation(DraconicEvolution.MODID, "particle/white_orb"));
            PORTAL_PARTICLE = getAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation(DraconicEvolution.MODID, "particle/portal"));
        });
    }

    public static Function<ResourceLocation, TextureAtlasSprite> getAtlas(ResourceLocation atlas) {
        return ATLAS_CACHE.computeIfAbsent(atlas, e -> (tex) -> ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(atlas)).getSprite(tex));
    }

//    public static TextureAtlasSprite getParticle
}
