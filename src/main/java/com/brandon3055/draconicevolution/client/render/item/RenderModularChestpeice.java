package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularChestpeice extends ToolRenderBase {

    public static ShaderProgram coreShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(ShaderObject.StandardShaderType.VERTEX)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/crystal_core.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/crystal_core.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC3)
                    .uniform("tier", UniformType.INT)
            )
//            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20))
            .build();

    private CCModel centralGemModel;
//    private VBORenderType centralGemVBOType;

    public RenderModularChestpeice(TechLevel techLevel) {
        super(techLevel, "chestpeice");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj")).ignoreMtl().parse();
        baseModel = CCModel.combine(Collections.singletonList(model.get("base_model"))).backfacedCopy();
        materialModel = model.get("chevrons").backfacedCopy();
        gemModel = model.get("power_crystals").backfacedCopy();
        centralGemModel = model.get("crystal_core").backfacedCopy();

//        initBaseVBO();
//        initMaterialVBO();
//        initGemVBO();

//        centralGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//            ccrs.bind(builder, format);
//            centralGemModel.render(ccrs);
//        });
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
//        mat.translate(0.5, 1.05, 0.5);
//        mat.rotate(MathHelper.torad * 180, Vector3.Z_POS);
//        mat.scale(1.95);
//
//        if (gui) {
//            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
//        } else {
//            getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
//
//        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
//            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
//        } else {
//            if (gui) {
//                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
//            } else {
//                getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
//            }
//        }
//
//        if (DEConfig.toolShaders) {
//            int shieldColour = 0xFFFFFFFF;
//            LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
//            if (!stack.isEmpty() && optionalHost.isPresent()) {
//                ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
//                ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
//                if (shieldControl != null) {
//                    shieldColour = shieldControl.getShieldColour();
//                }
//            }
//
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
//            getter.getBuffer(centralGemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, shieldColour, coreShader)));
//        } else {
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(centralGemVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_BLOCK;
    }

//    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, int colour, ShaderProgram shader) {
//        UniformCache uniforms = shader.pushCache();
//        uniforms.glUniform1i("tier", techLevel.index);
//        uniforms.glUniform3f("baseColour", ((colour >> 16) & 0xFF) / 255F, ((colour >> 8) & 0xFF) / 255F, (colour & 0xFF) / 255F);
//        return new ShaderRenderType(parent, shader, uniforms);
//    }
}
