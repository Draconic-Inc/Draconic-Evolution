package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.colour.Colour;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.*;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemEnergyCrystal implements IItemRenderer {

    public static ShaderProgram crystalShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/energy_crystal.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/energy_crystal.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("mipmap", UniformType.FLOAT)
                    .uniform("type", UniformType.INT)
                    .uniform("angle", UniformType.VEC2)
            )
            .whenUsed(cache -> cache.glUniform1f("time", (ClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 50))
            .build();

    public static final RenderType fallBackType = RenderType.create("fall_back_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.builder()
            .setTextureState(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_no_shader.png"), false, false))
            .setTexturingState(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .setCullState(RenderState.NO_CULL)
            .createCompositeState(false)
    );
    public static final RenderType crystalBaseType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));
    public static final RenderType fallBackOverlayType = RenderType.entityTranslucent(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));

    private final CrystalType type;
    private final TechLevel techLevel;
    private final CCModel crystalFull;
    private final CCModel crystalHalf;
    private final CCModel crystalBase;


    public RenderItemEnergyCrystal(CrystalType type, TechLevel techLevel) {
        this.type = type;
        this.techLevel = techLevel;
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal.obj"), GL11.GL_QUADS, null);
        crystalFull = CCModel.combine(map.values());
        map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal_half.obj"), GL11.GL_QUADS, null);
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    //region Unused

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    //endregion


    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        int tier = techLevel.index;

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, type == CrystalType.CRYSTAL_IO ? 0 : 0.5, 0.5);


        if (type == CrystalType.CRYSTAL_IO) {
            ccrs.bind(crystalBaseType, getter);
            crystalBase.render(ccrs, mat);

            mat.apply(new Rotation((ClientEventHandler.elapsedTicks) / 400F, 0, 1, 0));
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            if (DEConfig.crystalShaders) {
                UniformCache uniforms = crystalShader.pushCache();
                uniforms.glUniform1f("mipmap",  0);
                uniforms.glUniform1i("type", tier);
                uniforms.glUniform2f("angle", 0, 0);
                ccrs.bind(new ShaderRenderType(fallBackType, crystalShader, uniforms), getter);
                crystalHalf.render(ccrs, mat);
            } else {
                ccrs.bind(fallBackType, getter);
                crystalHalf.render(ccrs, mat);
                ccrs.baseColour = -1;
                ccrs.bind(fallBackOverlayType, getter);
                crystalHalf.render(ccrs, mat);
            }
        } else {
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks) / 400F, 0, 1, 0));
            if (DEConfig.crystalShaders) {
                UniformCache uniforms = crystalShader.pushCache();
                uniforms.glUniform1f("mipmap", 0);
                uniforms.glUniform1i("type", tier);
                uniforms.glUniform2f("angle", 0, 0);
                ccrs.bind(new ShaderRenderType(fallBackType, crystalShader, uniforms), getter);
                crystalFull.render(ccrs, mat);
            } else {
                ccrs.bind(fallBackType, getter);
                crystalFull.render(ccrs, mat);
                ccrs.baseColour = -1;
                ccrs.bind(fallBackOverlayType, getter);
                crystalFull.render(ccrs, mat);
            }
        }

    }

    @Override
    public IModelTransform getModelTransform() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    private static float[] r = {0.0F, 0.47F, 1.0F};
    private static float[] g = {0.2F, 0.0F, 0.4F};
    private static float[] b = {0.3F, 0.58F, 0.1F};

}
