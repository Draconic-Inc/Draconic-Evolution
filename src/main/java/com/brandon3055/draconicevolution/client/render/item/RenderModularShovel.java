package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularShovel extends ToolRenderBase {

    public RenderModularShovel(TechLevel techLevel) {
        super(techLevel, "shovel");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/shovel.obj")).ignoreMtl().parse();
        baseModel = CCModel.combine(Arrays.asList(model.get("handle"), model.get("gem_holder"))).backfacedCopy();
        materialModel = model.get("blade").twoFacedCopy();
        traceModel = model.get("trace").backfacedCopy();
        gemModel = model.get("gem").backfacedCopy();

//        initBaseVBO();
//        initMaterialVBO();
//        initTraceVBO();
//        initGemVBO();
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.27, 0.27, 0.5, gui ? 1.125 : 1.15);

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
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader)));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
//        } else {
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
    }
}
