package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.render.CustomSpriteUploader;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.google.common.collect.Streams;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 22/05/2022
 */
public class DEMiscSprites {
    public static final ResourceLocation ATLAS_LOCATION = new ResourceLocation(MODID, "textures/atlas/misc_sprites.png");

    private static CustomSpriteUploader customSpriteUploader;
    private static final Map<ResourceLocation, Consumer<TextureAtlasSprite>> registeredSprites = new HashMap<>();
    private static final Map<String, Material> matCache = new HashMap<>();

    public static final RenderType GUI_TYPE = RenderType.create("gui_tex", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
            .setTextureState(new RenderStateShard.TextureStateShard(ATLAS_LOCATION, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false)
    );

    public static void initialize(ColorHandlerEvent.Block event) {
        customSpriteUploader = new CustomSpriteUploader(registeredSprites, ATLAS_LOCATION);

        register("particle/white_orb", sprite -> ORB_PARTICLE = sprite);
        register("particle/portal", sprite -> PORTAL_PARTICLE = sprite);
        register("block/generator/generator_2", sprite -> GENERATOR = sprite);

        registerToArray(i -> "particle/energy_" + i, () -> ENERGY_PARTICLE);
        registerToArray(i -> "particle/spark_" + i, () -> SPARK_PARTICLE);
        registerToArray(i -> "particle/spell_" + i, () -> SPELL_PARTICLE);

        customSpriteUploader.addReloadListener(() -> MIXED_PARTICLE = Stream.concat(Arrays.stream(SPARK_PARTICLE), Arrays.stream(SPELL_PARTICLE)).toArray(TextureAtlasSprite[]::new));

        //Ender storage buttons
        if (ModHelper.ENDERSTORAGE.isPresent()) {
            register(new ResourceLocation("enderstorage:buttons"));
        }
    }

    // Static Storage
    public static TextureAtlasSprite[] ENERGY_PARTICLE = new TextureAtlasSprite[5];
    public static TextureAtlasSprite[] SPARK_PARTICLE = new TextureAtlasSprite[7];
    public static TextureAtlasSprite[] SPELL_PARTICLE = new TextureAtlasSprite[7];
    public static TextureAtlasSprite[] MIXED_PARTICLE;
    public static TextureAtlasSprite ORB_PARTICLE;
    public static TextureAtlasSprite PORTAL_PARTICLE;
    public static TextureAtlasSprite GENERATOR;

    //Equivalent to RenderType#SOLID
    public static final RenderType SOLID = RenderType.create("solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setShaderState(RenderStateShard.RENDERTYPE_SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ATLAS_LOCATION, false, false)).createCompositeState(true));

    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
        public void begin(BufferBuilder builder, TextureManager manager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderTexture(0, ATLAS_LOCATION);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tessellator) {
            tessellator.end();
        }

        public String toString() {
            return "TERRAIN_SHEET_TRANSLUCENT";
        }
    };

    //region Register / Get

    private static void register(ResourceLocation location) {
        register(location, null);
    }

    private static void register(String location) {
        register(new ResourceLocation(MODID, location));
    }

    private static void register(ResourceLocation location, Consumer<TextureAtlasSprite> onLoad) {
        registeredSprites.put(location, onLoad);
    }

    private static void register(String location, Consumer<TextureAtlasSprite> onLoad) {
        register(new ResourceLocation(MODID, location), onLoad);
    }

    private static void registerToArray(Function<Integer, String> nameFunction, Supplier<TextureAtlasSprite[]> arraySupplier) {
        for (int i = 0; i < arraySupplier.get().length; i++) {
            int finalI = i;
            register(nameFunction.apply(i), sprite -> {
                arraySupplier.get()[finalI] = sprite;
            });
        }
    }

    public static Material getMat(String modid, String location) {
        return matCache.computeIfAbsent(modid + ":" + location, s -> new CustomMat(ATLAS_LOCATION, new ResourceLocation(modid, location)));
    }

    public static Material getMat(String location) {
        return matCache.computeIfAbsent(MODID + ":" + location, s -> new CustomMat(ATLAS_LOCATION, new ResourceLocation(MODID, location)));
    }

    public static TextureAtlasSprite getSprite(String location) {
        return getMat(location).sprite();
    }

    public static Supplier<Material> matGetter(String location) {
        return () -> matCache.computeIfAbsent(MODID + ":" + location, s -> new CustomMat(ATLAS_LOCATION, new ResourceLocation(MODID, location)));
    }

    private static class CustomMat extends Material {

        public CustomMat(ResourceLocation atlasLocationIn, ResourceLocation textureLocationIn) {
            super(atlasLocationIn, textureLocationIn);
        }

        @Override
        public TextureAtlasSprite sprite() {
            return customSpriteUploader.getSprite(texture());
        }
    }

    //endregion
}
