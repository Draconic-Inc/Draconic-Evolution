package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.MultiBlockRenderers;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.multiblock.MultiBlockDefinition;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Created by brandon3055 on 2/4/2016.
 */
public class RenderTileEnergyCore implements BlockEntityRenderer<TileEnergyCore> {
    private static final double[] SCALES = {1.1, 1.7, 2.3, 3.6, 5.5, 7.1, 8.6, 10.2};

    private static final RenderType innerCoreType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/energy_core_base.png"));


    private static final RenderType outerCoreType = RenderType.create("outer_core", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/energy_core_overlay.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    );

    private static final RenderType innerStabType = RenderType.create("inner_stab", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_sphere.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .createCompositeState(false)
    );
    private static final RenderType outerStabType = RenderType.create("outer_stab", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_sphere.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    );

    private static final RenderType beamType = RenderType.create("inner_beam", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_beam.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .createCompositeState(false)
    );

    private static final RenderType outerBeamType = RenderType.create("outer_beam", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.TRIANGLE_STRIP, 256, false, false, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_beam.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false)
    );

    private static RenderType coreShaderType = RenderType.create("test_shader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/energy_core_overlay.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.energyCoreShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false)
    );

    private final CCModel modelStabilizerSphere;
    private final CCModel modelEnergyCore;

    public RenderTileEnergyCore(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/energy_core/stabilizer_sphere.obj")).quads().ignoreMtl().parse();
        modelStabilizerSphere = CCModel.combine(map.values());
        modelStabilizerSphere.computeNormals();

        map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/energy_core/energy_core_model.obj")).quads().ignoreMtl().parse();
        modelEnergyCore = CCModel.combine(map.values());
        modelEnergyCore.computeNormals();
    }



    @Override
    public void render(TileEnergyCore te, float partialTicks, PoseStack poseStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (te.buildGuide.get()) {
            MultiBlockDefinition def = te.getMultiBlockDef();
            if (def != null) {
                MultiBlockRenderers.renderBuildGuide(te.getLevel(), te.getBlockPos(), poseStack, getter, def, 200, partialTicks);
            }
        }

        if (!te.active.get()) {
            return;
        }

        Matrix4 mat = new Matrix4(poseStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        float rotation = (ClientEventHandler.elapsedTicks + partialTicks) / 2F;

        double scale = SCALES[te.tier.get() - 1];

        renderInnerCore(te, ccrs, mat, getter, partialTicks, rotation, scale);
        renderFancyOuterCore(te, ccrs, mat, getter, partialTicks, rotation, scale);
//        renderLegacyOuterCore(te, ccrs, mat, getter, partialTicks, rotation, scale);



    }

    public void renderInnerCore(TileEnergyCore te, CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, float partialTicks, float rotation, double scale) {
        int brightness = (int) Math.abs(Math.sin((float) ClientEventHandler.elapsedTicks / 100f) * 100f);
        ccrs.baseColour = 0xFFFFFFFF;//te.getColour();
        ccrs.brightness = 140 + brightness;
        ccrs.bind(innerCoreType, getter);
        Matrix4 coreMat = mat.copy();
        coreMat.translate(Vector3.CENTER);
        coreMat.scale(scale * -0.65, scale * -0.65, scale * -0.65);
        coreMat.rotate(rotation * MathHelper.torad, new Vector3(0F, 1F, 0.5F).normalize());
        modelEnergyCore.render(ccrs, coreMat);
    }

    public void renderFancyOuterCore(TileEnergyCore te, CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, float partialTicks, float rotation, double scale) {
        DEShaders.energyCoreActivation.glUniform1f(1);
        DEShaders.energyCoreFrameColour.glUniform3f(0.1F, 0.1F, 0.1F);
        DEShaders.energyCoreRotTriColour.glUniform3f(0.4F, 0F, 0.6F);
        DEShaders.energyCoreEffectColour.glUniform3f(0.0F, 0.95F, 0.95F);

        ccrs.bind(coreShaderType, getter);
        Matrix4 overlayMat = mat.copy();
        overlayMat.translate(Vector3.CENTER);
        overlayMat.scale(scale * -0.7, scale * -0.7, scale * -0.7);
        overlayMat.rotate(rotation * 0.5F * MathHelper.torad, new Vector3(0F, -1F, -0.5F).normalize());
        modelEnergyCore.render(ccrs, overlayMat);
    }

    public void renderLegacyOuterCore(TileEnergyCore te, CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, float partialTicks, float rotation, double scale) {
        if (te.tier.get() == 8) {
            ccrs.baseColour = Colour.packRGBA(0.95F, 0.45F, 0F, 1F);
        } else {
            ccrs.baseColour = Colour.packRGBA(0.2F, 1F, 1F, 1F);
        }
        ccrs.bind(outerCoreType, getter);
        Matrix4 overlayMatRef = mat.copy();
        overlayMatRef.translate(0.5, 0.5, 0.5);
        overlayMatRef.scale(scale * -0.7, scale * -0.7, scale * -0.7);
        overlayMatRef.rotate(rotation * 0.5F * MathHelper.torad, new Vector3(0F, -1F, -0.5F).normalize());
        modelEnergyCore.render(ccrs, overlayMatRef);
    }













    public void oldRender(TileEnergyCore te, float partialTicks, PoseStack poseStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
//        if (te.buildGuide.get()) {
//            MultiBlockDefinition def = te.getMultiBlockDef();
//            if (def != null) {
//                MultiBlockRenderers.renderBuildGuide(te.getLevel(), te.getBlockPos(), poseStack, getter, def, 200, partialTicks);
//            }
//        }
//


//        renderStructure();

//        BlockState state = DEContent.generator.defaultBlockState();
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, getter, packedLight, OverlayTexture.NO_OVERLAY);


//        if (!te.active.get()) return;

//        coreShaderType = RenderType.create("test_shaders", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
////                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/energy_core_overlay.png"), false, false))
//                        .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
//                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.testShader))
//                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
////                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                        .setCullState(RenderStateShard.NO_CULL)
//                        .createCompositeState(false)
//        );

//        Player player = Minecraft.getInstance().player;
//        DEShaders.testInB.glUniform1f((float) (player.getYRot() * MathHelper.torad));
//        DEShaders.testInC.glUniform1f((float) -(player.getXRot() * MathHelper.torad));

//        Matrix4 mat = new Matrix4(poseStack);
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.reset();
//        ccrs.brightness = packedLight;
//        ccrs.overlay = packedOverlay;

//        poseStack.pushPose();
//        try {
//            poseStack.translate(-1, -1, -6);
//            TransformingVertexConsumer consumer = new TransformingVertexConsumer(getter.getBuffer(coreShaderType), poseStack);
//
//            consumer.vertex(0, 3, 0).color(1F, 1F, 1F, 1F).uv(0, 1).uv2(240, 240).endVertex();
//            consumer.vertex(3, 3, 0).color(1F, 1F, 1F, 1F).uv(1, 1).uv2(240, 240).endVertex();
//            consumer.vertex(3, 0, 0).color(1F, 1F, 1F, 1F).uv(1, 0).uv2(240, 240).endVertex();
//            consumer.vertex(0, 0, 0).color(1F, 1F, 1F, 1F).uv(0, 0).uv2(240, 240).endVertex();
//
//            RenderUtils.endBatch(getter);
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        poseStack.popPose();

//        if (true) return;


        //region Do Calculations
//        float rotation = (ClientEventHandler.elapsedTicks + partialTicks) / 2F;
//        int brightness = (int) Math.abs(Math.sin((float) ClientEventHandler.elapsedTicks / 100f) * 100f);
//        double scale = 5;//SCALES[te.tier.get() - 1];

//        ccrs.baseColour = 0xFFFFFFFF;//te.getColour();
//        ccrs.brightness = 140 + brightness;
//        ccrs.bind(innerCoreType, getter);
//        Matrix4 coreMat = mat.copy();
//        coreMat.translate(Vector3.CENTER);
//        coreMat.scale(scale * -0.65, scale * -0.65, scale * -0.65);
//        coreMat.rotate(rotation * MathHelper.torad, new Vector3(0F, 1F, 0.5F).normalize());
//        modelEnergyCore.render(ccrs, coreMat);

//        if (te.tier.get() == 8) {
//            ccrs.baseColour = Colour.packRGBA(0.95F, 0.45F, 0F, 1F);
//        } else {
//            ccrs.baseColour = Colour.packRGBA(0.2F, 1F, 1F, 1F);
//        }

//        DEShaders.energyCoreActivation.glUniform1f(-0.2F + (((TimeKeeper.getClientTick() + partialTicks) / 100F) % 1.4F));
//        DEShaders.energyCoreActivation.glUniform1f(1);
//
//        DEShaders.energyCoreFrameColour.glUniform3f(0.1F, 0.1F, 0.1F);
//        DEShaders.energyCoreRotTriColour.glUniform3f(0.4F, 0F, 0.6F);
//        DEShaders.energyCoreEffectColour.glUniform3f(0.0F, 0.95F, 0.95F);
//
////        ccrs.bind(outerCoreType, getter);
//        ccrs.bind(coreShaderType, getter);
//        Matrix4 overlayMat = mat.copy();
//        overlayMat.translate(Vector3.CENTER);
//        overlayMat.scale(scale * -0.7, scale * -0.7, scale * -0.7);
//        overlayMat.rotate(rotation * 0.5F * MathHelper.torad, new Vector3(0F, -1F, -0.5F).normalize());
//        modelEnergyCore.render(ccrs, overlayMat);

//        ccrs.bind(outerCoreType, getter);
//        Matrix4 overlayMatRef = mat.copy();
//        overlayMatRef.translate(6.5, 0.5, 0.5);
//        overlayMatRef.scale(scale * -0.7, scale * -0.7, scale * -0.7);
//        overlayMatRef.rotate(rotation * 0.5F * MathHelper.torad, new Vector3(0F, -1F, -0.5F).normalize());
//        modelEnergyCore.render(ccrs, overlayMatRef);

//        renderStabilizers(te, ccrs, mat, getter, partialTicks);
    }


//    private void renderStabilizers(TileEnergyCore te, CCRenderState ccrs, Matrix4 matrix4, MultiBufferSource getter, float partialTick) {
//        if (!te.stabilizersOK.get()) {
//            return;
//        }
//
//        for (ManagedVec3I vec3I : te.stabOffsets) {
//            Matrix4 mat = matrix4.copy();
//            mat.translate(-vec3I.get().x + 0.5, -vec3I.get().y + 0.5, -vec3I.get().z + 0.5);
//
//            Direction facing = Direction.getNearest(vec3I.get().x, vec3I.get().y, vec3I.get().z);//Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, te.multiBlockAxis);
//            if (facing.getAxis() == Direction.Axis.X || facing.getAxis() == Direction.Axis.Y) {
//                mat.rotate(-90F * MathHelper.torad, new Vector3(-facing.getStepY(), facing.getStepX(), 0).normalize());
//            } else if (facing == Direction.SOUTH) {
//                mat.rotate(180F * MathHelper.torad, new Vector3(0, 1, 0).normalize());
//            }
//
//            mat.rotate(90F * MathHelper.torad, new Vector3(1, 0, 0).normalize());
//
//            ccrs.baseColour = 0xFFFFFFFF;
//            renderStabilizerBeam(te, mat, getter, vec3I.get(), partialTick);
//            if (te.tier.get() >= 5) {
//                mat.scale(-1.2F, -0.5F, -1.2F);
//            } else {
//                mat.scale(-0.45, -0.45, -0.45);
//            }
//
//            Matrix4 innerMat = mat.copy();
//            innerMat.scale(0.9F, 0.9F, 0.9F);
//            ccrs.baseColour = 0x00FFFFFF;
//            ccrs.brightness = 240;
//            innerMat.rotate((ClientEventHandler.elapsedTicks + partialTick) * MathHelper.torad, new Vector3(0, -1, 0));
//            ccrs.bind(innerStabType, getter);
//            modelStabilizerSphere.render(ccrs, innerMat);
//
//            mat.scale(1.1F, 1.1F, 1.1F);
//            ccrs.baseColour = 0x00FFFF7F;
//            ccrs.brightness = 240;
//            mat.rotate((ClientEventHandler.elapsedTicks + partialTick) * 0.5F * MathHelper.torad, new Vector3(0, 1, 0));
//            ccrs.bind(outerStabType, getter);
//            modelStabilizerSphere.render(ccrs, mat);
//        }
//    }

    private void renderStabilizerBeam(TileEnergyCore te, Matrix4 matrix4, MultiBufferSource getter, Vec3I vec, float partialTick) {
        Matrix4 innerMat = matrix4.copy();
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(beamType), innerMat);
        innerMat.rotate(180 * MathHelper.torad, new Vector3(0, 0, 1));

        float beamLength = Math.abs(vec.x + vec.y + vec.z) - 0.5F;
        float time = ClientEventHandler.elapsedTicks + partialTick;
        double rotation = (double) time * 0.025D * -1.5D;
        float beamMotion = -time * 0.2F - (float) MathHelper.floor(-time * 0.1F);

        //region Render Inner Beam
        float scale = 0.2F;
        float d7 = 0.5F + (float) Math.cos(rotation + 2.356194490192345F) * scale;  //x point 1
        float d9 = 0.5F + (float) Math.sin(rotation + 2.356194490192345F) * scale;  //z point 1
        float d11 = 0.5F + (float) Math.cos(rotation + (Math.PI / 4F)) * scale;        //x point 2
        float d13 = 0.5F + (float) Math.sin(rotation + (Math.PI / 4F)) * scale;     //z point 2
        float d15 = 0.5F + (float) Math.cos(rotation + 3.9269908169872414F) * scale;//Dist from x-3
        float d17 = 0.5F + (float) Math.sin(rotation + 3.9269908169872414F) * scale;
        float d19 = 0.5F + (float) Math.cos(rotation + 5.497787143782138F) * scale;
        float d21 = 0.5F + (float) Math.sin(rotation + 5.497787143782138F) * scale;
        float texXMin = 0.0F;
        float texXMax = 1.0F;
        float d28 = (-1.0F + beamMotion);
        float texHeight = beamLength * (0.5F / scale) + d28;

        if (te.tier.get() >= 5) {
            innerMat.scale(3.5, 1, 3.5);
        }
        innerMat.translate(-0.5, 0, -0.5);

        builder.vertex(d7, beamLength, d9).uv(texXMax, texHeight).endVertex();
        builder.vertex(d7, 0, d9).uv(texXMax, d28).endVertex();
        builder.vertex(d11, 0, d13).uv(texXMin, d28).endVertex();
        builder.vertex(d11, beamLength, d13).uv(texXMin, texHeight).endVertex();

        builder.vertex(d19, beamLength, d21).uv(texXMax, texHeight).endVertex();
        builder.vertex(d19, 0, d21).uv(texXMax, d28).endVertex();
        builder.vertex(d15, 0, d17).uv(texXMin, d28).endVertex();
        builder.vertex(d15, beamLength, d17).uv(texXMin, texHeight).endVertex();

        builder.vertex(d11, beamLength, d13).uv(texXMax, texHeight).endVertex();
        builder.vertex(d11, 0, d13).uv(texXMax, d28).endVertex();
        builder.vertex(d19, 0, d21).uv(texXMin, d28).endVertex();
        builder.vertex(d19, beamLength, d21).uv(texXMin, texHeight).endVertex();

        builder.vertex(d15, beamLength, d17).uv(texXMax, texHeight).endVertex();
        builder.vertex(d15, 0, d17).uv(texXMax, d28).endVertex();
        builder.vertex(d7, 0, d9).uv(texXMin, d28).endVertex();
        builder.vertex(d7, beamLength, d9).uv(texXMin, texHeight).endVertex();

        rotation += 0.77f;
        d7 = 0.5F + (float) Math.cos(rotation + 2.356194490192345F) * scale;
        d9 = 0.5F + (float) Math.sin(rotation + 2.356194490192345F) * scale;
        d11 = 0.5F + (float) Math.cos(rotation + (Math.PI / 4F)) * scale;
        d13 = 0.5F + (float) Math.sin(rotation + (Math.PI / 4F)) * scale;
        d15 = 0.5F + (float) Math.cos(rotation + 3.9269908169872414F) * scale;
        d17 = 0.5F + (float) Math.sin(rotation + 3.9269908169872414F) * scale;
        d19 = 0.5F + (float) Math.cos(rotation + 5.497787143782138F) * scale;
        d21 = 0.5F + (float) Math.sin(rotation + 5.497787143782138F) * scale;

        d28 = (-1F + (beamMotion * 1));
        texHeight = beamLength * (0.5F / scale) + d28;

        builder.vertex(d7, beamLength, d9).uv(texXMax, texHeight).endVertex();
        builder.vertex(d7, 0, d9).uv(texXMax, d28).endVertex();
        builder.vertex(d11, 0, d13).uv(texXMin, d28).endVertex();
        builder.vertex(d11, beamLength, d13).uv(texXMin, texHeight).endVertex();

        builder.vertex(d19, beamLength, d21).uv(texXMax, texHeight).endVertex();
        builder.vertex(d19, 0, d21).uv(texXMax, d28).endVertex();
        builder.vertex(d15, 0, d17).uv(texXMin, d28).endVertex();
        builder.vertex(d15, beamLength, d17).uv(texXMin, texHeight).endVertex();

        builder.vertex(d11, beamLength, d13).uv(texXMax, texHeight).endVertex();
        builder.vertex(d11, 0, d13).uv(texXMax, d28).endVertex();
        builder.vertex(d19, 0, d21).uv(texXMin, d28).endVertex();
        builder.vertex(d19, beamLength, d21).uv(texXMin, texHeight).endVertex();

        builder.vertex(d15, beamLength, d17).uv(texXMax, texHeight).endVertex();
        builder.vertex(d15, 0, d17).uv(texXMax, d28).endVertex();
        builder.vertex(d7, 0, d9).uv(texXMin, d28).endVertex();
        builder.vertex(d7, beamLength, d9).uv(texXMin, texHeight).endVertex();
        //endregion

        Matrix4 outerMat = matrix4.copy();
        builder = new TransformingVertexConsumer(getter.getBuffer(outerBeamType), outerMat);
        outerMat.rotate(180 * MathHelper.torad, new Vector3(0, 0, 1));

        //region Render Outer Beam
        outerMat.rotate(90 * MathHelper.torad, new Vector3(-1, 0, 0));
        outerMat.rotate(45 * MathHelper.torad, new Vector3(0, 0, 1));
        outerMat.translate(0, 0, 0.4);

        int sides = 4;
        float enlarge = 0.35F;
        if (te.tier.get() >= 5) {
            sides = 12;
            enlarge = 0.5F + ((te.tier.get() - 5) * 0.1F);
            outerMat.rotate((ClientEventHandler.elapsedTicks + partialTick) * 0.6F * MathHelper.torad, new Vector3(0, 0, -1));
            outerMat.scale(3.5, 3.5, 1);
        }

        for (int i = 0; i <= sides; i++) {
            float verX = (float) Math.sin((float) (i % sides) * (float) Math.PI * 2F / (float) sides) * 1F;
            float verY = (float) Math.cos((float) (i % sides) * (float) Math.PI * 2F / (float) sides) * 1F;
            builder.vertex(verX * 0.35F, verY * 0.35F, 0.0D).color(255, 255, 255, 32).uv(i, (beamMotion * 2)).endVertex();
            builder.vertex(verX * enlarge, verY * enlarge, beamLength).color(255, 255, 255, 32).uv(i, beamLength + (beamMotion * 2)).endVertex();
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(TileEnergyCore p_188185_1_) {
        return true;
    }
}
