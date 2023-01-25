package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.shader.BCShaders;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.brandonscore.client.shader.ChaosEntityShader;
import com.brandon3055.brandonscore.client.shader.BCShader;
import com.brandon3055.draconicevolution.client.shader.ToolShader;
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

    protected final TechLevel techLevel;
    protected final String tool;

    public ToolRenderBase(TechLevel techLevel, String tool) {
        this.techLevel = techLevel;
        this.tool = tool;
    }

    @Override
    public void renderItem(ItemStack stack, TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        DEShaders.TOOL_BASE_SHADER.getUv1OverrideUniform().glUniform2i(packedOverlay & 0xFFFF, (packedOverlay >> 16) & 0xFFFF);
        DEShaders.TOOL_BASE_SHADER.getUv2OverrideUniform().glUniform2i(packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

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

    public static void glUniformBaseColor(BCShader<?> shader, TechLevel techLevel, float pulse) {
        if (!(shader instanceof ToolShader toolShader) || !toolShader.hasBaseColorUniform()) return;
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
        toolShader.getBaseColorUniform().glUniform4f(r, g, b, a);
    }

    //These parts will always be rendered solid using the model texture.
    protected ToolPart basePart(CCModel model) {
        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType baseType = RenderType.create(MODID + ":base", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_BASE_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true));

        RenderType guiType = RenderType.create(MODID + ":base_gui", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_BASE_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/" + levelName + "_" + tool + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        return new BaseToolPart(model, baseType, guiType, DEShaders.TOOL_BASE_SHADER);
    }

    //These are parts like the head that are made out of the base material and will have the chaos shader applied if tech level is chaos.
    protected ToolPart materialPart(CCModel model) {
        if (techLevel != TechLevel.CHAOTIC) return basePart(model);

        RenderType chaoticType = RenderType.create(MODID + ":tool_chaos", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(BCShaders.CHAOS_ENTITY_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/chaos_shader.png"), true, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );
        return new ChaoticToolPart(model, chaoticType, BCShaders.CHAOS_ENTITY_SHADER);
    }

    protected ToolPart gemPart(CCModel model) {
        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType gemType = RenderType.create(MODID + ":tool_gem", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_GEM_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        return new SimpleToolPart(model, gemType, DEShaders.TOOL_GEM_SHADER);
    }

    //These are the shaded model "inlays" on the handles of most tools
    protected ToolPart tracePart(CCModel model) {
        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType gemType = RenderType.create(MODID + ":tool_trace", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_TRACE_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        return new SimpleToolPart(model, gemType, DEShaders.TOOL_TRACE_SHADER);
    }

    protected ToolPart bladePart(CCModel model) {
        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType gemType = RenderType.create(MODID + ":tool_blade", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.TOOL_BLADE_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        return new SimpleToolPart(model, gemType, DEShaders.TOOL_BLADE_SHADER);
    }

    protected abstract static class ToolPart {

        protected final BCShader<?> shader;

        protected ToolPart(BCShader<?> shader) {
            this.shader = shader;
        }

        public final void render(TransformType transformType, MultiBufferSource buffers, Matrix4 mat) {
            render(transformType, buffers, mat, 1F);
        }

        public abstract void render(TransformType transformType, MultiBufferSource buffers, Matrix4 mat, float pulse);
    }

    protected static class BaseToolPart extends ToolPart {

        private final VBORenderType vboType;
        private final VBORenderType guiVboType;

        public BaseToolPart(CCModel model, RenderType type, RenderType guiType, BCShader<?> shader) {
            super(shader);
            vboType = new VBORenderType(type, (format, builder) -> {
                CCRenderState ccrs = CCRenderState.instance();
                ccrs.reset();
                ccrs.bind(builder, format);
                model.render(ccrs);
            });
            guiVboType = new VBORenderType(guiType, (format, builder) -> {
                CCRenderState ccrs = CCRenderState.instance();
                ccrs.reset();
                ccrs.bind(builder, format);
                model.render(ccrs);
            });
        }

        @Override
        public void render(TransformType transformType, MultiBufferSource buffers, Matrix4 mat, float pulse) {
            if (transformType == TransformType.GUI) {
                buffers.getBuffer(guiVboType.withCallback(() -> shader.getModelMatUniform().glUniformMatrix4f(mat)));
            } else {
                buffers.getBuffer(vboType.withCallback(() -> shader.getModelMatUniform().glUniformMatrix4f(mat)));
            }
        }
    }

    protected class SimpleToolPart extends ToolPart {

        protected final VBORenderType vboType;

        public SimpleToolPart(CCModel model, RenderType baseType, BCShader<?> shader) {
            super(shader);
            vboType = new VBORenderType(baseType, (format, builder) -> {
                CCRenderState ccrs = CCRenderState.instance();
                ccrs.reset();
                ccrs.bind(builder, format);
                model.render(ccrs);
            });
        }

        @Override
        public void render(TransformType transformType, MultiBufferSource buffers, Matrix4 mat, float pulse) {
            buffers.getBuffer(vboType.withCallback(() -> {
                glUniformBaseColor(shader, techLevel, pulse);
                shader.getModelMatUniform().glUniformMatrix4f(mat);
            }));
        }

    }

    protected class ChaoticToolPart extends SimpleToolPart {

        private final ChaosEntityShader shader;

        public ChaoticToolPart(CCModel model, RenderType baseType, ChaosEntityShader shader) {
            super(model, baseType, shader);
            this.shader = shader;
        }

        @Override
        public void render(TransformType transformType, MultiBufferSource buffers, Matrix4 mat, float pulse) {
            buffers.getBuffer(vboType.withCallback(() -> {
                shader.getDisableLightUniform().glUniform1b(true);
                shader.getDisableOverlayUniform().glUniform1b(true);
                shader.getAlphaUniform().glUniform1f(0.7F);
                shader.getModelMatUniform().glUniformMatrix4f(mat);
            }));
        }
    }
}
