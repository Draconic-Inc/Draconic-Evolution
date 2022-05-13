package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal implements BlockEntityRenderer<TileChaosCrystal> {
    private CCModel model;

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(ShaderObject.StandardShaderType.VERTEX)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/armor_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/armor_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("activation", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
                    .uniform("tier", UniformType.INT)
            )
//            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
            .build();

    public static ShaderProgram chaosShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(ShaderObject.StandardShaderType.VERTEX)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/chaos.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/chaos.frag"))
                    .uniform("alpha", UniformType.FLOAT)
                    .uniform("yaw", UniformType.FLOAT)
                    .uniform("pitch", UniformType.FLOAT)
                    .uniform("time", UniformType.FLOAT)
            )
//            .whenUsed(cache -> {
//                cache.glUniform1f("alpha", 0.7F);
//                Minecraft mc = Minecraft.getInstance();
//                cache.glUniform1f("yaw", (float) ((mc.player.yRot * 2 * Math.PI) / 360.0));
//                cache.glUniform1f("pitch", -(float) ((mc.player.xRot * 2 * Math.PI) / 360.0));
//                cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 1);
//            })
            .build();


    private static RenderType crystalType = RenderType.create("crystal_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/chaos_crystal.png"), false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false));

    private static RenderType shieldType = RenderType.create("shieldTypse", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
//            .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static RenderType chaosType = RenderType.create("chaosShaderType", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
//            .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));


    public RenderTileChaosCrystal(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/chaos_crystal.obj")).quads().ignoreMtl().parse();
        model = CCModel.combine(map.values()).backfacedCopy();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(TileChaosCrystal te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (te.parentPos.get().getY() != -1) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, 0.5, 0.5);
        mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 180F, Vector3.Y_POS);
        mat.scale(0.75F);

        if (DEConfig.otherShaders) {
//            ccrs.bind(new ShaderRenderType(chaosType, chaosShader, chaosShader.pushCache()), getter);
            model.render(ccrs, mat);
        }

        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(crystalType, getter);
        model.render(ccrs, mat);

        if (!te.guardianDefeated.get()) {
//            UniformCache uniforms = shieldShader.pushCache();
//            uniforms.glUniform1i("tier", 0);
//            uniforms.glUniform1f("activation", 1F);
//            uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 1F);
//            ccrs.bind(new ShaderRenderType(shieldType, shieldShader, uniforms), getter);
//            model.render(ccrs, mat);
//            ToolRenderBase.endBatch(getter);
        }
    }
}
