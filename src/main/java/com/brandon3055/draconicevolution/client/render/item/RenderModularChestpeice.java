package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.render.shader.*;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularChestpeice extends ToolRenderBase {

    public static ShaderProgram coreShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/common.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/crystal_core.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC3)
                    .uniform("tier", UniformType.INT)
            )
            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 20))
            .build();

    private CCModel centralGemModel;
    private VBORenderType centralGemVBOType;

    public RenderModularChestpeice(TechLevel techLevel) {
        super(techLevel, "chestpeice");
        Map<String, CCModel> model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj"), GL11.GL_TRIANGLES, null);
        baseModel = CCModel.combine(Collections.singletonList(model.get("base_model"))).backfacedCopy();
        materialModel = model.get("chevrons").backfacedCopy();
        gemModel = model.get("power_crystals").backfacedCopy();
        centralGemModel = model.get("crystal_core").backfacedCopy();

        initBaseVBO();
        initMaterialVBO();
        initGemVBO();

        centralGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            centralGemModel.render(ccrs);
        });
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui, int packedLight) {
        mat.translate(0.5, 1.05, 0.5);
        mat.rotate(MathHelper.torad * 180, Vector3.Z_POS);
        mat.scale(1.95);

        if (gui) {
            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
        } else {
            getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
        }

        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
        } else {
            if (gui) {
                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
            } else {
                getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
            }
        }

        if (DEConfig.toolShaders) {
            int shieldColour = 0xFFFFFFFF;
            LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            if (!stack.isEmpty() && optionalHost.isPresent()) {
                ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
                ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
                if (shieldControl != null) {
                    shieldColour = shieldControl.getShieldColour();
                }
            }

            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
            getter.getBuffer(centralGemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, shieldColour, coreShader)));
        } else {
            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
            getter.getBuffer(centralGemVBOType.withMatrix(mat).withLightMap(packedLight));
        }
    }

    @Override
    public ImmutableMap<TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    public static ShaderRenderType getShaderType(RenderType parent, TechLevel techLevel, int colour, ShaderProgram shader) {
        UniformCache uniforms = shader.pushCache();
        uniforms.glUniform1i("tier", techLevel.index);
        uniforms.glUniform3f("baseColour", ((colour >> 16) & 0xFF) / 255F, ((colour >> 8) & 0xFF) / 255F, (colour & 0xFF) / 255F);
        return new ShaderRenderType(parent, shader, uniforms);
    }
}
