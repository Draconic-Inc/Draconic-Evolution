package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularSword extends ToolRenderBase {

    public RenderModularSword(TechLevel techLevel) {
        super(techLevel, "sword");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/sword.obj")).ignoreMtl().parse();
        baseModel = CCModel.combine(Arrays.asList(model.get("handle"), model.get("handle_bauble"), model.get("hilt"))).backfacedCopy();
        materialModel = model.get("blade_core").backfacedCopy();
        traceModel = CCModel.combine(Arrays.asList(model.get("trace_top"), model.get("trace_bottom"))).backfacedCopy();
        bladeModel = model.get("blade_edge").backfacedCopy();
        gemModel = model.get("blade_gem").backfacedCopy();

        initBaseVBO();
        initMaterialVBO();
        initTraceVBO();
        initBladeVBO();
        initGemVBO();
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.29, 0.29, 0.5, gui ? 0.875 : 1.125);

        DEShaders.toolBaseUV1Override.glUniform2i(ccrs.overlay & 0xFFFF, (ccrs.overlay >> 16) & 0xFFFF);
        DEShaders.toolBaseUV2Override.glUniform2i(ccrs.brightness & 0xFFFF, (ccrs.brightness >> 16) & 0xFFFF);

        // Render Hilt, handle and guard.
        if (gui) {
            buffers.getBuffer(guiBaseVBOType.withCallback(() -> DEShaders.toolBaseModelMat.glUniformMatrix4f(mat)));
        } else {
            buffers.getBuffer(baseVBOType.withCallback(() -> DEShaders.toolBaseModelMat.glUniformMatrix4f(mat)));
        }

        // Render Sword blade material
        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
            buffers.getBuffer(materialChaosVBOType.withCallback(() -> {
                DEShaders.chaosEntityDisableLight.glUniform1b(true);
                DEShaders.chaosEntityDisableOverlay.glUniform1b(true);
                DEShaders.chaosEntityAlpha.glUniform1f(0.7F);
                DEShaders.chaosEntityModelMat.glUniformMatrix4f(mat);
            }));
        } else if (gui) {
            buffers.getBuffer(guiMaterialVBOType.withCallback(() -> DEShaders.toolBaseModelMat.glUniformMatrix4f(mat)));
        } else {
            buffers.getBuffer(materialVBOType.withCallback(() -> DEShaders.toolBaseModelMat.glUniformMatrix4f(mat)));
        }

        // Render accent strip
        buffers.getBuffer(traceVBOType.withCallback(() -> {
            glUniformBaseColor(DEShaders.toolTraceBaseColor, techLevel);
            DEShaders.toolTraceModelMat.glUniformMatrix4f(mat);
        }));
        // Render blade sides
        buffers.getBuffer(bladeVBOType.withCallback(() -> {
            glUniformBaseColor(DEShaders.toolBladeBaseColor, techLevel);
            DEShaders.toolBladeModelMat.glUniformMatrix4f(mat);
        }));
        // Render gem
        buffers.getBuffer(gemVBOType.withCallback(() -> {
            glUniformBaseColor(DEShaders.toolGemBaseColor, techLevel);
            DEShaders.toolGemModelMat.glUniformMatrix4f(mat);
        }));
    }
}
