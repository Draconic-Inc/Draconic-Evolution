package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
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
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
        ((IRenderTypeBuffer.Impl) getter).finish();

//        testParticleRender(ccrs, stack, transform, mat, mStack, getter, gui);
//        if (true) return;
//
//
//        Minecraft mc = Minecraft.getInstance();
//        float partialTicks = mc.getRenderPartialTicks();
//        ActiveRenderInfo renderInfo = mc.getRenderManager().info;
//
//
//        RenderSystem.pushMatrix();
//        RenderSystem.disableCull();
//        mat.rotate(90 * MathHelper.torad, new Vector3(0, 1, 0));
//        mat.glApply();
//        IVertexBuilder builder = getter.getBuffer(GuiHelper.TRANS_TYPE);
//
//
//        Vector3d vector3d = renderInfo.getProjectedView();
//        float f = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.getX());
//        float f1 = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.y);
//        float f2 = (float) (net.minecraft.util.math.MathHelper.lerp((double) partialTicks, 0, 0) - vector3d.getZ());
//        Quaternion quaternion;
////        if (this.particleAngle == 0.0F) {
//        quaternion = renderInfo.getRotation();
////        } else {
////            quaternion = new Quaternion(renderInfo.getRotation());
////            float f3 = net.minecraft.util.math.MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
////            quaternion.multiply(Vector3f.ZP.rotation(f3));
////        }
//
//        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
//        vector3f1.transform(quaternion);
//        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
//        float f4 = 1;//this.getScale(partialTicks);
//
//        for (int i = 0; i < 4; ++i) {
//            Vector3f vector3f = avector3f[i];
//            vector3f.transform(quaternion);
//            vector3f.mul(f4);
//            vector3f.add(f, f1, f2);
//        }
//
//        float f7 = 0;//this.getMinU();
//        float f8 = 1;//this.getMaxU();
//        float f5 = 0;//this.getMinV();
//        float f6 = 1;// this.getMaxV();
//        int j = 240;//this.getBrightnessForRender(partialTicks);
//        builder.pos((double) avector3f[0].getX(), (double) avector3f[0].getY(), (double) avector3f[0].getZ())/*.tex(f8, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[1].getX(), (double) avector3f[1].getY(), (double) avector3f[1].getZ())/*.tex(f8, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[2].getX(), (double) avector3f[2].getY(), (double) avector3f[2].getZ())/*.tex(f7, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[3].getX(), (double) avector3f[3].getY(), (double) avector3f[3].getZ())/*.tex(f7, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//
//
////        new TransformationMatrix(mat.toMatrix4f()).getRotationLeft();
//
//
////        IVertexBuilder builder = getter.getBuffer(BCSprites.GUI_TEX_TYPE);
////        TextureAtlasSprite sprite = BCSprites.get("dark/gear").getSprite();
////        float x = 0;
////        float y = 0;
////        float z = 0;
////        float width = 1;
////        float height = 1;
////
//////        builder.pos(x,          y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
//////        builder.pos(x + width,  y + height, z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
//////        builder.pos(x + width,  y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
//////        builder.pos(x,          y,          z).color(1F, 1F, 1F, 1F).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
////        builder.pos(x,          y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width,  y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width,  y,          z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x,          y,          z).color(1F, 1F, 1F, 1F).endVertex();
//
//        ((IRenderTypeBuffer.Impl) getter).finish();
//
//        RenderSystem.popMatrix();
    }
//
//    private void testParticleRender(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui) {
//        Minecraft mc = Minecraft.getInstance();
//        float partialTicks = mc.getRenderPartialTicks();
//        ActiveRenderInfo renderInfo = mc.getRenderManager().info;
//
//
//        RenderSystem.pushMatrix();
//        RenderSystem.disableCull();
////        mat.translate(0, 0, -1);
//
////        mat.rotate(new Rotation(new Quat(new TransformationMatrix(mat.toMatrix4f()).getRotationLeft())));
//
//        mStack.translate(0, 1, 1);
//
//
//
////        mStack.rotate(new TransformationMatrix(mStack.getLast().getMatrix()).getRotationLeft());
//
//
////        mat.apply(new Rotation(new Quat(new TransformationMatrix(mat.toMatrix4f()).getRotationLeft())).inverse());
////        mat.glApply();
//
//        Quaternion quaternion = new TransformationMatrix(mat.toMatrix4f()).getRotationLeft();
//
//
//
////        mat.apply(new Rotation(new Quat(undoQuat)).inverse());
//
////        mat.apply(new Vector3(1, 0, 0).apply(mat));
//
////        mat.apply(new Rotation(new Quat(renderInfo.getRotation())));
//
//        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(GuiHelper.TRANS_TYPE), mat);
//
//
////        float x = 0;
////        float y = 0;
////        float z = 0;
////        float width = 1;
////        float height = 1;
////        builder.pos(x, y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width, y + height, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x + width, y, z).color(1F, 1F, 1F, 1F).endVertex();
////        builder.pos(x, y, z).color(1F, 1F, 1F, 1F).endVertex();
//
//
//        Vector3 vector3f1 = new Vector3(-1.0F, -1.0F, 0.0F);
//        vector3f1.rotate(new Quat(quaternion));
//        Vector3[] avector3f = new Vector3[]{new Vector3(-1.0F, -1.0F, 0.0F), new Vector3(-1.0F, 1.0F, 0.0F), new Vector3(1.0F, 1.0F, 0.0F), new Vector3(1.0F, -1.0F, 0.0F)};
//        float f4 = 1;//this.getScale(partialTicks);
//
//        for (int i = 0; i < 4; ++i) {
//            Vector3 vector3f = avector3f[i];
//            vector3f.rotate(new Quat(quaternion));
//            vector3f.multiply(f4);
//            vector3f.apply(mat);
////            vector3f.add(f, f1, f2);
//        }
//
//        float f7 = 0;//this.getMinU();
//        float f8 = 1;//this.getMaxU();
//        float f5 = 0;//this.getMinV();
//        float f6 = 1;// this.getMaxV();
//        int j = 240;//this.getBrightnessForRender(partialTicks);
//        builder.pos((double) avector3f[0].x, (double) avector3f[0].y, (double) avector3f[0].z)/*.tex(f8, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[1].x, (double) avector3f[1].y, (double) avector3f[1].z)/*.tex(f8, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[2].x, (double) avector3f[2].y, (double) avector3f[2].z)/*.tex(f7, f5)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//        builder.pos((double) avector3f[3].x, (double) avector3f[3].y, (double) avector3f[3].z)/*.tex(f7, f6)*/.color(1F, 1F, 1F, 1F)/*.lightmap(j)*/.endVertex();
//
//
//
//        if (getter instanceof IRenderTypeBuffer.Impl) {
//            ((IRenderTypeBuffer.Impl) getter).finish();
//        }
//        RenderSystem.popMatrix();
//    }


}