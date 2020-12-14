package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.*;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.*;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularBow extends ToolRenderBase {

    public static ShaderProgram stringShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/common.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/bow_string.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("tier", UniformType.INT)
            )
            .whenUsed(cache -> {
                cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 20);
            })
            .build();

    private RenderType bowStringType;

    public RenderModularBow(TechLevel techLevel) {
        super(techLevel, "bow");
        Map<String, CCModel> model = OBJParser.parseModels(new ResourceLocation(MODID, "models/item/equipment/bow.obj"), GL11.GL_TRIANGLES, null);
        baseModel = model.get("bow_handle").backfacedCopy();
        materialModel = model.get("bow_arm").backfacedCopy();
        gemModel = model.get("bow_gem").backfacedCopy();

        bowStringType = RenderType.makeType("shaderStringType", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(new ResourceLocation(MODID, "textures/item/equipment/bow_string.png"), true, false))
                .transparency(RenderState.LIGHTNING_TRANSPARENCY)
                .cull(RenderState.CULL_DISABLED)
                .writeMask(RenderState.WriteMaskState.COLOR_WRITE)
                .alpha(RenderState.AlphaState.DEFAULT_ALPHA)
                .build(false)
        );

        initBaseVBO();
        initMaterialVBO();
        initGemVBO();
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MatrixStack mStack, IRenderTypeBuffer getter, boolean gui, int packedLight) {
        transform(mat, 0.46, 0.54, 0.5, gui ? 0.9 : 1.125);
        double drawAngle = getDrawAngle(stack);

        if (gui) {
            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
        } else {
            getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
        }

        Matrix4 bottomMat = mat.copy();
        bottomMat.rotate(MathHelper.torad * 180, Vector3.Y_POS);
        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));
            getter.getBuffer(materialChaosVBOType.withMatrix(bottomMat).withLightMap(packedLight).withState(getShaderType(chaosType, chaosShader)));

        } else {
            if (gui) {
                getter.getBuffer(guiMaterialVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(guiMaterialVBOType.withMatrix(bottomMat).withLightMap(packedLight));
            } else {
                getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
                getter.getBuffer(materialVBOType.withMatrix(bottomMat).withLightMap(packedLight));
            }
        }

        drawStrings(ccrs, mat, bottomMat, getter, drawAngle, packedLight);


//        if (gui) {
//            getter.getBuffer(guiBaseVBOType.withMatrix(mat).withLightMap(packedLight));
//        } else {
//            getter.getBuffer(baseVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
//
//        if (techLevel == TechLevel.CHAOTIC && DEConfig.toolShaders) {
//            getter.getBuffer(materialChaosVBOType.withMatrix(mat).withLightMap(packedLight).withShader(getShaderType(chaosType, chaosShader)));
//        } else {
//            getter.getBuffer(materialVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
//
//        if (DEConfig.toolShaders) {
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight).withShader(getShaderType(shaderParentType, techLevel, traceShader)));
//            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight).withShader(getShaderType(shaderParentType, techLevel, bladeShader)));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight).withShader(getShaderType(shaderParentType, techLevel, gemShader)));
//        } else {
//            getter.getBuffer(traceVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(bladeVBOType.withMatrix(mat).withLightMap(packedLight));
//            getter.getBuffer(gemVBOType.withMatrix(mat).withLightMap(packedLight));
//        }
    }

    private void drawStrings(CCRenderState ccrs, Matrix4 topMat, Matrix4 bottomMat, IRenderTypeBuffer getter, double drawAngle, int packedLight) {
        RenderType bowStringType = this.bowStringType;
        if (DEConfig.toolShaders) {
            UniformCache uniforms = stringShader.pushCache();
            uniforms.glUniform1i("tier", techLevel.index);
            bowStringType = new ShaderRenderType(bowStringType, stringShader, uniforms);
        }
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(bowStringType), topMat);
//        drawAngle = Math.abs(Math.sin(ClientEventHandler.elapsedTicks / 100F)) * 45;

        double crystalX = 12.508D * 0.01D;
        double crystalY = 67.844D * 0.01D;
        double A = 180 - 90 - drawAngle;
        double c = crystalY * (Math.sin(drawAngle * MathHelper.torad) / Math.sin(A * MathHelper.torad));

        float[] r = {0.0F, 0.55F, 1.0F, 0.5F};
        float[] g = {0.35F, 0.3F, 0.572F, 0F};
        float[] b = {0.65F, 0.9F, 0.172F, 0F};

        renderBeam(builder, new Vector3(0, -crystalX, crystalY), new Vector3(0, -(crystalX + c), 0), r[techLevel.index], g[techLevel.index], b[techLevel.index]);
        renderBeam(builder, new Vector3(0, -crystalX, -crystalY), new Vector3(0, -(crystalX + c), 0), r[techLevel.index], g[techLevel.index], b[techLevel.index]);

        if (drawAngle > 0) {
            Matrix4 arrowMat = topMat.copy();
            arrowMat.translate(0.055, 0.325 - c, 0);
            arrowMat.rotate(90 * MathHelper.torad, Vector3.Z_POS);
            renderArrow(arrowMat, getter, packedLight);
        }

        topMat.apply(new Rotation(drawAngle * MathHelper.torad, 1, 0, 0).at(new Vector3(0, -crystalX, -crystalY)));
        bottomMat.apply(new Rotation(drawAngle * MathHelper.torad, 1, 0, 0).at(new Vector3(0, -crystalX, -crystalY)));
        if (DEConfig.toolShaders) {
            getter.getBuffer(gemVBOType.withMatrix(topMat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
            getter.getBuffer(gemVBOType.withMatrix(bottomMat).withLightMap(packedLight).withState(getShaderType(shaderParentType, techLevel, gemShader)));
        } else {
            getter.getBuffer(gemVBOType.withMatrix(topMat).withLightMap(packedLight));
            getter.getBuffer(gemVBOType.withMatrix(bottomMat).withLightMap(packedLight));
        }
    }

    private void renderBeam(IVertexBuilder buffer, Vector3 source, Vector3 target, float r, float g, float b) {
        double scale = 0.03;
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();

        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        float dist = 0.2F * (float) Utils.getDistanceAtoB(new Vec3D(source), new Vec3D(target));
        float anim = DEConfig.toolShaders ? 0F : (BCClientEventHandler.elapsedTicks + partialTicks) / -15F;

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferShaderQuad(buffer, p1, p2, p3, p4, anim, dist, r, g, b);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferShaderQuad(buffer, p1, p2, p3, p4, anim, dist, r, g, b);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferShaderQuad(buffer, p1, p2, p3, p4, anim, dist, r, g, b);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferShaderQuad(buffer, p1, p2, p3, p4, anim, dist, r, g, b);
    }

    private void bufferShaderQuad(IVertexBuilder buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float anim, float dist, float r, float g, float b) {
        if (!DEConfig.toolShaders) {
            bufferQuad(buffer, p1, p2, p3, p4, anim, dist, r, g, b);
            return;
        }
        buffer.pos(p1.x, p1.y, p1.z).color(r, g, b, 1F).tex(0.0F, 0F).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).color(r, g, b, 1F).tex(0.0F, 1F).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).color(r, g, b, 1F).tex(1F, 1F).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).color(r, g, b, 1F).tex(1F, 0F).endVertex();
    }

    private void bufferQuad(IVertexBuilder buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float anim, float dist, float r, float g, float b) {
        buffer.pos(p1.x, p1.y, p1.z).color(r, g, b, 1F).tex(0.5F, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).color(r, g, b, 1F).tex(0.5F, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).color(r, g, b, 1F).tex(1.0F, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).color(r, g, b, 1F).tex(1.0F, anim).endVertex();
    }

    private void renderArrow(Matrix4 mat, IRenderTypeBuffer getter, int packedLight) {
        mat.scale(0.05625F, 0.05625F, 0.05625F);
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(RenderType.getEntityCutout(TippedArrowRenderer.RES_ARROW)), mat);
        this.buggerVertex(builder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        this.buggerVertex(builder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

        for (int j = 0; j < 4; ++j) {
            mat.rotate(90 * MathHelper.torad, Vector3.X_POS);//Vector3f.XP.rotationDegrees(90.0F));
            this.buggerVertex(builder, -8.5F, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            this.buggerVertex(builder, 8.5F, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            this.buggerVertex(builder, 8.5F, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            this.buggerVertex(builder, -8.5F, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
        }
    }

    public void buggerVertex(IVertexBuilder builder, float x, float y, float z, float u, float v, int normX, int normZ, int normY, int light) {
        builder.pos(x, y, z).color(255, 255, 255, 255).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) normX, (float) normY, (float) normZ).endVertex();
    }

    @Override
    public ImmutableMap<TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BOW;
    }

    private double getDrawAngle(ItemStack stack) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.getActiveItemStack() == stack) {
            int maxCount = player.getItemInUseMaxCount();
            return BowItem.getArrowVelocity(maxCount) * 45F;
        }
        return 0;
    }
}
