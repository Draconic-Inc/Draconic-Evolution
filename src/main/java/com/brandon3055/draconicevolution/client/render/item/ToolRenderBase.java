package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * Created by brandon3055 on 22/5/20.
 */
public abstract class ToolRenderBase implements IItemRenderer {

//    public static ShaderProgram chaosShader = ShaderProgramBuilder.builder()
//            .addShader("vert", shader -> shader
//                    .type(ShaderObject.StandardShaderType.VERTEX)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/chaos.vert"))
//            )
//            .addShader("frag", shader -> shader
//                    .type(ShaderObject.StandardShaderType.FRAGMENT)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/chaos.frag"))
//                    .uniform("alpha", UniformType.FLOAT)
//                    .uniform("yaw", UniformType.FLOAT)
//                    .uniform("pitch", UniformType.FLOAT)
//                    .uniform("time", UniformType.FLOAT)
//            )
////            .whenUsed(cache -> {
////                cache.glUniform1f("alpha", 0.7F);
////                Minecraft mc = Minecraft.getInstance();
////                cache.glUniform1f("yaw", (float) ((mc.player.yRot * 2 * Math.PI) / 360.0));
////                cache.glUniform1f("pitch", -(float) ((mc.player.xRot * 2 * Math.PI) / 360.0));
////                cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 1);
////            })
//            .build();
//
//    public static ShaderProgram gemShader = ShaderProgramBuilder.builder()
//            .addShader("vert", shader -> shader
//                    .type(ShaderObject.StandardShaderType.VERTEX)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/common.vert"))
//            )
//            .addShader("frag", shader -> shader
//                    .type(ShaderObject.StandardShaderType.FRAGMENT)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/tool_gem.frag"))
//                    .uniform("time", UniformType.FLOAT)
//                    .uniform("baseColour", UniformType.VEC4)
//            )
////            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
//            .build();
//
//    public static ShaderProgram bladeShader = ShaderProgramBuilder.builder()
//            .addShader("vert", shader -> shader
//                    .type(ShaderObject.StandardShaderType.VERTEX)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/common.vert"))
//            )
//            .addShader("frag", shader -> shader
//                    .type(ShaderObject.StandardShaderType.FRAGMENT)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/tool_blade.frag"))
//                    .uniform("time", UniformType.FLOAT)
//                    .uniform("baseColour", UniformType.VEC4)
//            )
////            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
//            .build();
//
//    public static ShaderProgram traceShader = ShaderProgramBuilder.builder()
//            .addShader("vert", shader -> shader
//                    .type(ShaderObject.StandardShaderType.VERTEX)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/common.vert"))
//            )
//            .addShader("frag", shader -> shader
//                    .type(ShaderObject.StandardShaderType.FRAGMENT)
//                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/tool_trace.frag"))
//                    .uniform("time", UniformType.FLOAT)
//                    .uniform("baseColour", UniformType.VEC4)
//            )
////            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
//            .build();


    public RenderType modelType;
    public RenderType modelGuiType;
    public RenderType chaosType;
    public RenderType shaderParentType;

    public CCModel baseModel;            //These parts will always be rendered solid using the model texture.
    public CCModel materialModel;        //These are parts like the head that are made out of the base material and will have the chaos shader applied if tech level is chaos.
    public CCModel traceModel;           //These are the shaded model "inlays" on the handles of most tools
    public CCModel bladeModel;
    public CCModel gemModel;

    public VBORenderType baseVBOType;
    public VBORenderType guiBaseVBOType;
    public VBORenderType materialVBOType;
    public VBORenderType materialChaosVBOType;
    public VBORenderType guiMaterialVBOType;
    public VBORenderType traceVBOType;
    public VBORenderType bladeVBOType;
    public VBORenderType gemVBOType;

    public TechLevel techLevel;

    public ToolRenderBase(TechLevel techLevel, String tool) {
        this.techLevel = techLevel;
        String levelName = techLevel.name().toLowerCase(Locale.ENGLISH);
        modelType = RenderType.create("modelType", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 256, true, false, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.customBlockShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(true));

        modelGuiType = RenderType.create("modelGuiType", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.customBlockShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.NO_OVERLAY)
                        .createCompositeState(false)
        );

        chaosType = RenderType.create("chaosShaderType", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.customBlockShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        shaderParentType = RenderType.create("shaderGemType", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.customBlockShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );
    }

    @Override
    public void renderItem(ItemStack stack, TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;//packedLight;
        ccrs.overlay = packedOverlay;
        renderTool(ccrs, stack, transformType, mat, mStack, getter, transformType == TransformType.GUI, packedLight);
    }

    public abstract void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, PoseStack mStack, MultiBufferSource getter, boolean gui, int packedLight);

    public void transform(Matrix4 mat, double x, double y, double z, double scale) {
        mat.translate(x, y, z);
        mat.rotate(MathHelper.torad * 90, Vector3.Y_NEG);
        mat.rotate(MathHelper.torad * 45, Vector3.X_POS);
        mat.scale(scale);
    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_TOOL;
    }

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

//    protected static float[][] baseColours = {
//            {0.0F, 0.5F, 0.8F, 1F},
//            {0.55F, 0.0F, 0.65F, 1F},
//            {0.8F, 0.5F, 0.1F, 1F},
//            {0.75F, 0.05F, 0.05F, 0.2F}};
//
//    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, ShaderProgram shader) {
//        return getShaderType(parent, techLevel, shader, 1F);
//    }
//
//    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, ShaderProgram shader, float pulse) {
////        techLevel = TechLevel.DRACONIUM;
//        UniformCache uniforms = shader.pushCache();
//        float[] baseColour = baseColours[techLevel.index];
//        float r = baseColour[0];
//        float g = baseColour[1];
//        float b = baseColour[2];
//        float a = baseColour[3];
//        switch (techLevel) {
//            case DRACONIUM:
//                a *= 1F + pulse;
//                break;
//            case WYVERN:
//                a *= 1F + pulse;
//                break;
//            case DRACONIC:
//                a *= 1F + pulse;
//                break;
//            case CHAOTIC:
//                r += pulse * 0.2F;
//                g += pulse * 0.2F;
//                b += pulse * 0.2F;
//                break;
//        }
//        uniforms.glUniform4f("baseColour", r, g, b, a);
//        return new ShaderRenderType(parent, shader, uniforms);
//    }
//
//    public static ShaderRenderType getShaderType(RenderType parent, ShaderProgram shader) {
//        return new ShaderRenderType(parent, shader, shader.pushCache());
//    }
//

    public void initBaseVBO() {
        baseVBOType = new VBORenderType(modelType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseModel.render(ccrs);
        });

        guiBaseVBOType = new VBORenderType(modelGuiType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseModel.render(ccrs);
        });
    }

    public void initMaterialVBO() {
        materialVBOType = new VBORenderType(modelType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });

        guiMaterialVBOType = new VBORenderType(modelGuiType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });

        materialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });
    }

    public void initTraceVBO() {
        traceVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            traceModel.render(ccrs);
        });
    }

    public void initBladeVBO() {
        bladeVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            bladeModel.render(ccrs);
        });
    }

    public void initGemVBO() {
        gemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            gemModel.render(ccrs);
        });
    }
}
