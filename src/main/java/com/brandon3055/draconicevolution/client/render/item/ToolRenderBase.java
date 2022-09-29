package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.CCUniform;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 22/5/20.
 */
public abstract class ToolRenderBase implements IItemRenderer {

    public RenderType modelType;
    public RenderType modelGuiType;
    public RenderType chaosType;
    public RenderType gemType;
    public RenderType traceType;
    public RenderType bladeType;

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
        modelType = RenderType.create("modelType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.toolBaseShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true));

        modelGuiType = RenderType.create("modelGuiType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.toolBaseShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        chaosType = RenderType.create(MODID + ":tool_chaos", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.chaosEntityShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/chaos_shader.png"), true, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        gemType = RenderType.create(MODID + ":tool_gem", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.toolGemShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        traceType = RenderType.create(MODID + ":tool_trace", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.toolTraceShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        bladeType = RenderType.create(MODID + ":tool_trace", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.toolBladeShader))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
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
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        renderTool(ccrs, stack, transformType, mat, getter, transformType == TransformType.GUI);
    }

    public abstract void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui);

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

    protected static float[][] baseColours = {
            { 0.0F, 0.5F, 0.8F, 1F },
            { 0.55F, 0.0F, 0.65F, 1F },
            { 0.8F, 0.5F, 0.1F, 1F },
            { 0.75F, 0.05F, 0.05F, 0.2F }
    };

    protected static void glUniformBaseColor(CCUniform uniform, TechLevel techlevel) {
        glUniformBaseColor(uniform, techlevel, 1F);
    }

    protected static void glUniformBaseColor(CCUniform uniform, TechLevel techLevel, float pulse) {
        float[] baseColour = baseColours[techLevel.index];
        float r = baseColour[0];
        float g = baseColour[1];
        float b = baseColour[2];
        float a = baseColour[3];
        switch (techLevel) {
            case DRACONIUM, WYVERN, DRACONIC -> a *= 1F + pulse;
            case CHAOTIC -> {
                r += pulse * 0.2F;
                g += pulse * 0.2F;
                b += pulse * 0.2F;
            }
        }
        uniform.glUniform4f(r, g, b, a);
    }

    public void initBaseVBO() {
        baseVBOType = new VBORenderType(modelType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseModel.render(ccrs);
        });

        guiBaseVBOType = new VBORenderType(modelGuiType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseModel.render(ccrs);
        });
    }

    public void initMaterialVBO() {
        materialVBOType = new VBORenderType(modelType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });

        guiMaterialVBOType = new VBORenderType(modelGuiType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });

        materialChaosVBOType = new VBORenderType(chaosType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialModel.render(ccrs);
        });
    }

    public void initTraceVBO() {
        traceVBOType = new VBORenderType(traceType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            traceModel.render(ccrs);
        });
    }

    public void initBladeVBO() {
        bladeVBOType = new VBORenderType(bladeType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            bladeModel.render(ccrs);
        });
    }

    public void initGemVBO() {
        gemVBOType = new VBORenderType(gemType, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            gemModel.render(ccrs);
        });
    }
}
