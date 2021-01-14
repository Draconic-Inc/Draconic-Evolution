package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DESprites;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularStaff extends ToolRenderBase {

    private CCModel baseGui;
    private CCModel materialGui;
    private CCModel traceGui;
    private CCModel bladeGui;
    private CCModel gemGui;

    private VBORenderType guiBaseVBOType;
    private VBORenderType guiMaterialVBOType;
    private VBORenderType guiMaterialChaosVBOType;
    private VBORenderType guiTraceVBOType;
    private VBORenderType guiBladeVBOType;
    private VBORenderType guiGemVBOType;

    public RenderModularStaff(TechLevel techLevel) {
        super(techLevel, "staff");
        Map<String, CCModel> model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff.obj"), GL11.GL_TRIANGLES, null);
        baseModel = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"), model.get("cage_connection"))).backfacedCopy();
        materialModel = CCModel.combine(Arrays.asList(model.get("head"), model.get("crystal_cage"))).backfacedCopy();
        traceModel = model.get("trace");
        bladeModel = model.get("blade").backfacedCopy();
        gemModel = CCModel.combine(Arrays.asList(model.get("focus_gem"), model.get("energy_crystal"))).backfacedCopy();

        initBaseVBO();
        initMaterialVBO();
        initTraceVBO();
        initBladeVBO();
        initGemVBO();

        model = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/staff_gui.obj"), GL11.GL_TRIANGLES, null);
        baseGui = CCModel.combine(Arrays.asList(model.get("handle"), model.get("head_connection"))).backfacedCopy();
        materialGui = model.get("head").backfacedCopy();
        traceGui = model.get("trace");
        bladeGui = model.get("blade").backfacedCopy();
        gemGui = model.get("focus_gem").backfacedCopy();

        guiBaseVBOType = new VBORenderType(modelType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            baseGui.render(ccrs);
        });

        guiMaterialVBOType = new VBORenderType(modelGuiType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialGui.render(ccrs);
        });

        guiMaterialChaosVBOType = new VBORenderType(chaosType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            materialGui.render(ccrs);
        });

        guiTraceVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            traceGui.render(ccrs);
        });

        guiBladeVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            bladeGui.render(ccrs);
        });

        guiGemVBOType = new VBORenderType(shaderParentType, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, (format, builder) -> {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(builder, format);
            gemGui.render(ccrs);
        });
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui, int packedLight) {
        if (gui) {
            transform(mat, 0.19, 0.19, 0.5, 1.1);

            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
            if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
                getter.getBuffer(guiMaterialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
            } else {
                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
            }

            if (DEConfig.toolShaders) {
                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader)));
                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader)));
                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
            } else {
                getter.getBuffer(guiTraceVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(guiBladeVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(guiGemVBOType.withMatrix(mat).withLightMap(packedLight));
            }
            return;
        }

        if (transform == TransformType.FIXED || transform == TransformType.GROUND || transform == TransformType.NONE) {
//            transform(mat, 0.6, 0.6, 0.5, 1.125);
            transform(mat, 0.6, 0.6, 0.5, 0.75);
        } else {
            transform(mat, 0.27, 0.27, 0.5, 1.125);
        }

        getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
        } else {
            getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
        }

        if (DEConfig.toolShaders) {
            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, traceShader)));
            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, bladeShader)));
            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
        } else {
            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight));
            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight));
            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
        }
//        ((IRenderTypeBuffer.Impl) getter).finish();
//
//
//        RenderSystem.pushMatrix();
//        mat.rotate(90 * MathHelper.torad, new Vector3(0, 1, 0));
//        mat.glApply();
//        IVertexBuilder builder = getter.getBuffer(BCSprites.GUI_TEX_TYPE);
//        TextureAtlasSprite sprite = BCSprites.get("dark/gear").getSprite();
//        float x = 0;
//        float y = 0;
//        float z = 0;
//        float width = 1000;
//        float height = 1000;
//
//        builder.pos(x,          y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
//        builder.pos(x + width,  y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
//        builder.pos(x + width,  y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
//        builder.pos(x,          y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
//
//        ((IRenderTypeBuffer.Impl) getter).finish();
//
//        RenderSystem.popMatrix();
    }
}