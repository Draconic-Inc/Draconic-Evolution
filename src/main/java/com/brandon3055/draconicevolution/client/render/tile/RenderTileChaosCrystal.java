package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.*;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.model.ModularArmorModel;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static net.minecraft.client.renderer.RenderState.*;
import static net.minecraft.client.renderer.RenderState.CULL_DISABLED;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal extends TileEntityRenderer<TileChaosCrystal> {
    private CCModel model;

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/armor_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/armor_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("activation", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
                    .uniform("tier", UniformType.INT)
            )
            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 20))
            .build();

    public static ShaderProgram chaosShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/chaos.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/chaos.frag"))
                    .uniform("alpha", UniformType.FLOAT)
                    .uniform("yaw", UniformType.FLOAT)
                    .uniform("pitch", UniformType.FLOAT)
                    .uniform("time", UniformType.FLOAT)
            )
            .whenUsed(cache -> {
                cache.glUniform1f("alpha", 0.7F);
                Minecraft mc = Minecraft.getInstance();
                cache.glUniform1f("yaw", (float) ((mc.player.rotationYaw * 2 * Math.PI) / 360.0));
                cache.glUniform1f("pitch", -(float) ((mc.player.rotationPitch * 2 * Math.PI) / 360.0));
                cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 1);
            })
            .build();


    private static RenderType crystalType = RenderType.makeType("crystal_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/block/chaos_crystal.png"), false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .build(false));

    private static RenderType shieldType = RenderType.makeType("shieldTypse", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .lightmap(LIGHTMAP_ENABLED)
            .cull(CULL_DISABLED)
            .build(false));

    public static RenderType chaosType = RenderType.makeType("chaosShaderType", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
            .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .lightmap(LIGHTMAP_ENABLED)
            .cull(CULL_DISABLED)
            .build(false));


    public RenderTileChaosCrystal(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/chaos_crystal.obj"), GL11.GL_QUADS, null);
        model = CCModel.combine(map.values()).backfacedCopy();
    }


    @Override
    public void render(TileChaosCrystal te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        if (te.parentPos.get().getY() != -1) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, 0.5, 0.5);
        mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 180F, Vector3.Y_POS);
        mat.scale(0.75F);

        ccrs.bind(new ShaderRenderType(chaosType, chaosShader, chaosShader.pushCache()), getter);
        model.render(ccrs, mat);

        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(crystalType, getter);
        model.render(ccrs, mat);

        if (!te.guardianDefeated.get()) {
            UniformCache uniforms = shieldShader.pushCache();
            uniforms.glUniform1i("tier", 0);
            uniforms.glUniform1f("activation", 1F);
            uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 1F);
            ccrs.bind(new ShaderRenderType(shieldType, shieldShader, uniforms), getter);
            model.render(ccrs, mat);
            ((IRenderTypeBuffer.Impl) getter).finish();
        }
    }
}
