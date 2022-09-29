package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.ChargeUpPhase;
import com.brandon3055.draconicevolution.entity.guardian.control.IPhase;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class DraconicGuardianRenderer extends EntityRenderer<DraconicGuardianEntity> {
    public static final ResourceLocation ENDERCRYSTAL_BEAM_TEXTURES = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/chaos_guardian.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType dragonCutoutType = RenderType.entityCutoutNoCull(GUARDIAN_TEXTURE);
    private static final RenderType dragonDeathType = RenderType.entityDecal(GUARDIAN_TEXTURE);
    private static final RenderType eyesType = RenderType.eyes(EYES_TEXTURE);
    private static final RenderType beamType = RenderType.entitySmoothCutout(ENDERCRYSTAL_BEAM_TEXTURES);
    private static RenderType beamType2 = RenderType.create("beam_type_2", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ENDERCRYSTAL_BEAM_TEXTURES, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false));

    public static final RenderType shieldType = RenderType.create("shield_type", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
//            .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
            .setCullState(RenderStateShard.NO_CULL)
//            .setAlphaState(RenderStateShard.DEFAULT_ALPHA)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .createCompositeState(false));

    public static RenderType beamShaderType = RenderType.create("beam_shader_type", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ENDERCRYSTAL_BEAM_TEXTURES, true, false))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(false)
    );


    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(ShaderObject.StandardShaderType.VERTEX)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/guardian_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(ShaderObject.StandardShaderType.FRAGMENT)
                    .source(new ResourceLocation(DraconicEvolution.MODID, "shaders/guardian_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
                    .uniform("activation", UniformType.FLOAT)

            )
//            .whenUsed(cache -> {
//                cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getFrameTime()) / 20);
//                cache.glUniform1f("activation", 1F);
//            })
            .build();

    private static final float sqrt3div2 = (float) (Math.sqrt(3.0D) / 2.0D);
    private final DraconicGuardianRenderer.EnderDragonModel model = new DraconicGuardianRenderer.EnderDragonModel();


    public DraconicGuardianRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(DraconicGuardianEntity guardian, float entityYaw, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight) {
        mStack.pushPose();
        float f = (float) guardian.getMovementOffsets(7, partialTicks)[0];
        float f1 = (float) (guardian.getMovementOffsets(5, partialTicks)[1] - guardian.getMovementOffsets(10, partialTicks)[1]);
        mStack.mulPose(Vector3f.YP.rotationDegrees(-f));
        mStack.mulPose(Vector3f.XP.rotationDegrees(f1 * 10.0F));
        mStack.translate(0.0D, 0.0D, 1.0D);
        mStack.scale(-1.0F, -1.0F, 1.0F);
        mStack.translate(0.0D, -1.501F, 0.0D);
        boolean flag = guardian.hurtTime > 0;
        this.model.prepareMobModel(guardian, 0.0F, 0.0F, partialTicks);

        if (guardian.deathTicks > 0) {
            float progress = (float) guardian.deathTicks / 200.0F;
//            VertexConsumer builder = getter.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_TEXTURES, progress));
//            this.model.renderToBuffer(mStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//            builder = getter.getBuffer(dragonDeathType);
//            this.model.renderToBuffer(mStack, builder, packedLight, OverlayTexture.pack(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            VertexConsumer builder = getter.getBuffer(dragonCutoutType);
            this.model.renderToBuffer(mStack, builder, packedLight, OverlayTexture.pack(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        }
        VertexConsumer builder;

        boolean isImmune = guardian.getPhaseManager().getCurrentPhase().isInvulnerable();
        float shieldState = guardian.getEntityData().get(DraconicGuardianEntity.SHIELD_POWER) / (float) DEConfig.guardianShield;
        if (shieldState > 0 || isImmune) {
//            UniformCache uniforms = shieldShader.pushCache();
//            if (isImmune) {
//                uniforms.glUniform4f("baseColour", 0F, 1F, 1F, 2);
//            } else {
//                uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 1.5F * shieldState);
//            }
//            builder = getter.getBuffer(new ShaderRenderType(shieldType, shieldShader, uniforms));
//            this.model.renderToBuffer(mStack, builder, packedLight, OverlayTexture.pack(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        builder = getter.getBuffer(eyesType);
        this.model.renderToBuffer(mStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (guardian.deathTicks > 0) {
            float f5 = ((float) guardian.deathTicks + partialTicks) / 200.0F;
            float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);
            Random random = new Random(432L);
            builder = getter.getBuffer(RenderType.lightning());
            mStack.pushPose();
            mStack.translate(0.0D, -1.0D, -2.0D);

            for (int i = 0; (float) i < (f5 + f5 * f5) / 2.0F * 60.0F; ++i) {
                mStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F + f5 * 90.0F));
                float f3 = random.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
                float f4 = random.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
                Matrix4f matrix4f = mStack.last().pose();
                int j = (int) (255.0F * (1.0F - f7));
                deathAnimA(builder, matrix4f, j);
                deathAnimB(builder, matrix4f, f3, f4);
                deathAnimC(builder, matrix4f, f3, f4);
                deathAnimA(builder, matrix4f, j);
                deathAnimC(builder, matrix4f, f3, f4);
                deathAnimD(builder, matrix4f, f3, f4);
                deathAnimA(builder, matrix4f, j);
                deathAnimD(builder, matrix4f, f3, f4);
                deathAnimB(builder, matrix4f, f3, f4);
            }

            mStack.popPose();
        }

        mStack.popPose();
        if (guardian.closestGuardianCrystal != null) {
            mStack.pushPose();
            float relX = (float) (guardian.closestGuardianCrystal.getX() - Mth.lerp(partialTicks, guardian.xo, guardian.getX()));
            float relY = (float) (guardian.closestGuardianCrystal.getY() - Mth.lerp(partialTicks, guardian.yo, guardian.getY()));
            float relZ = (float) (guardian.closestGuardianCrystal.getZ() - Mth.lerp(partialTicks, guardian.zo, guardian.getZ()));
            renderBeam(relX, relY + GuardianCrystalRenderer.getY(guardian.closestGuardianCrystal, partialTicks), relZ, partialTicks, guardian.tickCount, mStack, getter, packedLight);
            mStack.popPose();
        }

        IPhase iPhase = guardian.getPhaseManager().getCurrentPhase();
        if (iPhase instanceof ChargeUpPhase && guardian.getArenaOrigin() != null) {
            ChargeUpPhase phase = (ChargeUpPhase) iPhase;
            if (phase.animState() != 0) {
                BlockPos origin = guardian.getArenaOrigin();
//                float beamSin = MathHelper.sin((Math.min(1, phase.animState() + 0.3F)) * (float) Math.PI);
                float beamSin = Mth.sin(phase.animState() * (float) Math.PI);
                mStack.pushPose();
                float relX = (float) ((origin.getX() + 0.5) - Mth.lerp(partialTicks, guardian.xo, guardian.getX()));
                float relY = (float) ((origin.getY() + 0.5) - Mth.lerp(partialTicks, guardian.yo, guardian.getY()));
                float relZ = (float) ((origin.getZ() + 0.5) - Mth.lerp(partialTicks, guardian.zo, guardian.getZ()));
                renderChargingBeam(relX, relY, relZ, partialTicks, guardian.tickCount, mStack, getter, packedLight, beamSin);
                mStack.popPose();
            }
        }

        super.render(guardian, entityYaw, partialTicks, mStack, getter, packedLight);
    }

    private static void deathAnimA(VertexConsumer builder, Matrix4f mat, int alpha) {
        builder.vertex(mat, 0.0F, 0.0F, 0.0F).color(255, 0, 0, alpha).endVertex();
        builder.vertex(mat, 0.0F, 0.0F, 0.0F).color(255, 0, 0, alpha).endVertex();
    }

    private static void deathAnimB(VertexConsumer builder, Matrix4f mat, float p_229060_2_, float p_229060_3_) {
        builder.vertex(mat, -sqrt3div2 * p_229060_3_, p_229060_2_, -0.5F * p_229060_3_).color(255, 0, 0, 0).endVertex();
    }

    private static void deathAnimC(VertexConsumer builder, Matrix4f mat, float p_229062_2_, float p_229062_3_) {
        builder.vertex(mat, sqrt3div2 * p_229062_3_, p_229062_2_, -0.5F * p_229062_3_).color(255, 0, 255, 0).endVertex();
    }

    private static void deathAnimD(VertexConsumer builder, Matrix4f mat, float p_229063_2_, float p_229063_3_) {
        builder.vertex(mat, 0.0F, p_229063_2_, 1.0F * p_229063_3_).color(255, 0, 0, 0).endVertex();
    }

    public static void renderBeam(float crystalRelX, float crystalRelY, float crystalRelZ, float partialTicks, int animTicks, PoseStack mStack, MultiBufferSource getter, int packedLight) {
        float xzDistance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
        float distance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
        mStack.pushPose();
        mStack.translate(0.0D, 2.0D, 0.0D);
        mStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
        mStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
        VertexConsumer builder = getter.getBuffer(beamType);
        float f2 = 0.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f3 = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;
        PoseStack.Pose stackLast = mStack.last();
        Matrix4f lastMatrix = stackLast.pose();
        Matrix3f lastNormal = stackLast.normal();

        for (int j = 1; j <= 8; ++j) {
            float rSin = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float rCos = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float indexDecimal = (float) j / 8.0F;
            builder.vertex(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, f4, f5, distance).color(255, 255, 255, 255).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin, rCos, distance).color(255, 255, 255, 255).uv(indexDecimal, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(0, 0, 0, 255).uv(indexDecimal, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = rSin;
            f5 = rCos;
            f6 = indexDecimal;
        }

        mStack.popPose();
//
//        int shieldState = 500;//guardian.getEntityData().get(DraconicGuardianEntity.SHIELD_STATE);
//        float hit = (shieldState / 500F) - 1F;
//        Color color = Color.getHSBColor(hit / 8F, 1, 1);
//        UniformCache uniforms = DraconicGuardianRenderer.shieldShader.pushCache();
//        uniforms.glUniform4f("baseColour", color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.5F * (shieldState / 500F));
//        builder = getter.getBuffer(new ShaderRenderType(DraconicGuardianRenderer.shieldType, DraconicGuardianRenderer.shieldShader, uniforms));
//
//        xzDistance = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
//        distance = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
//        mStack.pushPose();
//        mStack.translate(0.0D, 2.0D, 0.0D);
//        mStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
//        mStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
//        f2 = 0;//0.0F - ((float) animTicks + partialTicks) * 0.01F;
//        f3 = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);//MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F - ((float) animTicks + partialTicks) * 0.01F;
//        f4 = 0.0F;
//        f5 = 0.75F;
//        f6 = 0.0F;
//        stackLast = mStack.last();
//        lastMatrix = stackLast.pose();
//        lastNormal = stackLast.normal();
//
//        for (int j = 1; j <= 8; ++j) {
//            float rSin = MathHelper.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
//            float rCos = MathHelper.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
//            float indexDecimal = (float) j / 8.0F;
////            builder.vertex(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
////            builder.vertex(lastMatrix, f4, f5, distance).color(255, 255, 255, 255).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
////            builder.vertex(lastMatrix, rSin, rCos, distance).color(255, 255, 255, 255).uv(indexDecimal, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
////            builder.vertex(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(0, 0, 0, 255).uv(indexDecimal, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
//            builder.vertex(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(f6, f2).endVertex();
//            builder.vertex(lastMatrix, f4, f5, distance).color(255, 255, 255, 255).uv(f6, f3).endVertex();
//            builder.vertex(lastMatrix, rSin, rCos, distance).color(255, 255, 255, 255).uv(indexDecimal, f3).endVertex();
//            builder.vertex(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(0, 0, 0, 255).uv(indexDecimal, f2).endVertex();
//            f4 = rSin;
//            f5 = rCos;
//            f6 = indexDecimal;
//        }
//
//        mStack.popPose();
    }

    public static void renderBeam(float crystalRelX, float crystalRelY, float crystalRelZ, float partialTicks, int animTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, float alpha) {
        float xzDistance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
        float distance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
        mStack.pushPose();
        mStack.translate(0.0D, 2.0D, 0.0D);
        mStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
        mStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
        VertexConsumer builder = getter.getBuffer(beamType2);
        float f2 = 0.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f3 = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;
        PoseStack.Pose stackLast = mStack.last();
        Matrix4f lastMatrix = stackLast.pose();
        Matrix3f lastNormal = stackLast.normal();

        for (int j = 1; j <= 8; ++j) {
            float rSin = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float rCos = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float indexDecimal = (float) j / 8.0F;
            builder.vertex(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(1F, 1F, 1F, alpha).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, f4, f5, distance).color(1F, 1F, 1F, alpha).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin, rCos, distance).color(1F, 1F, 1F, alpha).uv(indexDecimal, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(1F, 1F, 1F, alpha).uv(indexDecimal, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = rSin;
            f5 = rCos;
            f6 = indexDecimal;
        }

        mStack.popPose();
    }

    public static void renderChargingBeam(float crystalRelX, float crystalRelY, float crystalRelZ, float partialTicks, int animTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, float alpha) {
        float xzDistance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
        float distance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
        mStack.pushPose();
        mStack.translate(0.0D, 2.0D, 0.0D);
        mStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
        mStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
        VertexConsumer builder = getter.getBuffer(beamType2);
        float vMin =  ((float) animTicks + partialTicks) * 0.01F;
        float vMax = (Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F) + (((float) animTicks + partialTicks) * 0.01F);
        float f4 = 0.0F;
        float f5 = 0.1F;
        float texU = 0.0F;
        PoseStack.Pose stackLast = mStack.last();
        Matrix4f lastMatrix = stackLast.pose();
        Matrix3f lastNormal = stackLast.normal();

        float taperOffset = 10F;//0.2F;

        for (int j = 1; j <= 8; ++j) {
//            int shell = j / 8;
            float rSin = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.1F;
            float rCos = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.1F;
            float indexDecimal = (float) j / 8.0F;
            builder.vertex(lastMatrix, f4 * taperOffset, f5 * taperOffset, 0.0F).color(1F, 1F, 1F, alpha).uv(texU, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, f4, f5, distance).color(1F, 1F, 1F, alpha).uv(texU, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin, rCos, distance).color(1F, 1F, 1F, alpha).uv(indexDecimal, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin * taperOffset, rCos * taperOffset, 0.0F).color(1F, 1F, 1F, alpha).uv(indexDecimal, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = rSin;
            f5 = rCos;
            texU = indexDecimal;
        }

        mStack.popPose();
    }

    //Original
    public static void renderShaderBeam(float crystalRelX, float crystalRelY, float crystalRelZ, float partialTicks, int animTicks, PoseStack mStack, MultiBufferSource getter, int packedLight) {
        float xzDistance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
        float distance = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
        mStack.pushPose();
        mStack.translate(0.0D, 2.0D, 0.0D);
        mStack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
        mStack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
        VertexConsumer builder = getter.getBuffer(beamType);
        float f2 = 0.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f3 = Mth.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;
        PoseStack.Pose stackLast = mStack.last();
        Matrix4f lastMatrix = stackLast.pose();
        Matrix3f lastNormal = stackLast.normal();

        for (int j = 1; j <= 8; ++j) {
            float rSin = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float rCos = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float indexDecimal = (float) j / 8.0F;
            builder.vertex(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, f4, f5, distance).color(255, 255, 255, 255).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin, rCos, distance).color(255, 255, 255, 255).uv(indexDecimal, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            builder.vertex(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(0, 0, 0, 255).uv(indexDecimal, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = rSin;
            f5 = rCos;
            f6 = indexDecimal;
        }

        mStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DraconicGuardianEntity entity) {
        return DRAGON_TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class EnderDragonModel extends EntityModel<DraconicGuardianEntity> {
        private /*final*/ ModelPart head;
        private /*final*/ ModelPart spine;
        private /*final*/ ModelPart jaw;
        private /*final*/ ModelPart body;
        private ModelPart leftProximalWing;
        private ModelPart leftDistalWing;
        private ModelPart leftForeThigh;
        private ModelPart leftForeLeg;
        private ModelPart leftForeFoot;
        private ModelPart leftHindThigh;
        private ModelPart leftHindLeg;
        private ModelPart leftHindFoot;
        private ModelPart rightProximalWing;
        private ModelPart rightDistalWing;
        private ModelPart rightForeThigh;
        private ModelPart rightForeLeg;
        private ModelPart rightForeFoot;
        private ModelPart rightHindThigh;
        private ModelPart rightHindLeg;
        private ModelPart rightHindFoot;
        @Nullable
        private DraconicGuardianEntity dragonInstance;
        private float partialTicks;

        public EnderDragonModel() {
//            this.texWidth = 256;
//            this.texHeight = 256;
//            float f = -16.0F;
//            this.head = new ModelPart(this);
//            this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
//            this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
//            this.head.mirror = true;
//            this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
//            this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
//            this.head.mirror = false;
//            this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
//            this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
//            this.jaw = new ModelPart(this);
//            this.jaw.setPos(0.0F, 4.0F, -8.0F);
//            this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
//            this.head.addChild(this.jaw);
//            this.spine = new ModelPart(this);
//            this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
//            this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
//            this.body = new ModelPart(this);
//            this.body.setPos(0.0F, 4.0F, 8.0F);
//            this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
//            this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
//            this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
//            this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
//            this.leftProximalWing = new ModelPart(this);
//            this.leftProximalWing.mirror = true;
//            this.leftProximalWing.setPos(12.0F, 5.0F, 2.0F);
//            this.leftProximalWing.addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
//            this.leftProximalWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
//            this.leftDistalWing = new ModelPart(this);
//            this.leftDistalWing.mirror = true;
//            this.leftDistalWing.setPos(56.0F, 0.0F, 0.0F);
//            this.leftDistalWing.addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
//            this.leftDistalWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
//            this.leftProximalWing.addChild(this.leftDistalWing);
//            this.leftForeThigh = new ModelPart(this);
//            this.leftForeThigh.setPos(12.0F, 20.0F, 2.0F);
//            this.leftForeThigh.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
//            this.leftForeLeg = new ModelPart(this);
//            this.leftForeLeg.setPos(0.0F, 20.0F, -1.0F);
//            this.leftForeLeg.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
//            this.leftForeThigh.addChild(this.leftForeLeg);
//            this.leftForeFoot = new ModelPart(this);
//            this.leftForeFoot.setPos(0.0F, 23.0F, 0.0F);
//            this.leftForeFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
//            this.leftForeLeg.addChild(this.leftForeFoot);
//            this.leftHindThigh = new ModelPart(this);
//            this.leftHindThigh.setPos(16.0F, 16.0F, 42.0F);
//            this.leftHindThigh.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
//            this.leftHindLeg = new ModelPart(this);
//            this.leftHindLeg.setPos(0.0F, 32.0F, -4.0F);
//            this.leftHindLeg.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
//            this.leftHindThigh.addChild(this.leftHindLeg);
//            this.leftHindFoot = new ModelPart(this);
//            this.leftHindFoot.setPos(0.0F, 31.0F, 4.0F);
//            this.leftHindFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
//            this.leftHindLeg.addChild(this.leftHindFoot);
//            this.rightProximalWing = new ModelPart(this);
//            this.rightProximalWing.setPos(-12.0F, 5.0F, 2.0F);
//            this.rightProximalWing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
//            this.rightProximalWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
//            this.rightDistalWing = new ModelPart(this);
//            this.rightDistalWing.setPos(-56.0F, 0.0F, 0.0F);
//            this.rightDistalWing.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
//            this.rightDistalWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
//            this.rightProximalWing.addChild(this.rightDistalWing);
//            this.rightForeThigh = new ModelPart(this);
//            this.rightForeThigh.setPos(-12.0F, 20.0F, 2.0F);
//            this.rightForeThigh.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
//            this.rightForeLeg = new ModelPart(this);
//            this.rightForeLeg.setPos(0.0F, 20.0F, -1.0F);
//            this.rightForeLeg.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
//            this.rightForeThigh.addChild(this.rightForeLeg);
//            this.rightForeFoot = new ModelPart(this);
//            this.rightForeFoot.setPos(0.0F, 23.0F, 0.0F);
//            this.rightForeFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
//            this.rightForeLeg.addChild(this.rightForeFoot);
//            this.rightHindThigh = new ModelPart(this);
//            this.rightHindThigh.setPos(-16.0F, 16.0F, 42.0F);
//            this.rightHindThigh.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
//            this.rightHindLeg = new ModelPart(this);
//            this.rightHindLeg.setPos(0.0F, 32.0F, -4.0F);
//            this.rightHindLeg.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
//            this.rightHindThigh.addChild(this.rightHindLeg);
//            this.rightHindFoot = new ModelPart(this);
//            this.rightHindFoot.setPos(0.0F, 31.0F, 4.0F);
//            this.rightHindFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
//            this.rightHindLeg.addChild(this.rightHindFoot);
        }

        public void prepareMobModel(DraconicGuardianEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
            this.dragonInstance = entityIn;
            this.partialTicks = partialTick;
        }

        public void setupAnim(DraconicGuardianEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        public void renderToBuffer(PoseStack mStack, VertexConsumer getter, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//            mStack.pushPose();
//            float f = Mth.lerp(this.partialTicks, this.dragonInstance.prevAnimTime, this.dragonInstance.animTime);
//            this.jaw.xRot = (float) (Math.sin(f * ((float) Math.PI * 2F)) + 1.0D) * 0.2F;
//            float f1 = (float) (Math.sin(f * ((float) Math.PI * 2F) - 1.0F) + 1.0D);
//            f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
//            mStack.translate(0.0D, f1 - 2.0F, -3.0D);
//            mStack.mulPose(Vector3f.XP.rotationDegrees(f1 * 2.0F));
//            float f2 = 0.0F;
//            float f3 = 20.0F;
//            float f4 = -12.0F;
//            float f5 = 1.5F;
//            double[] adouble = this.dragonInstance.getMovementOffsets(6, this.partialTicks);
//            float f6 = Mth.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] - this.dragonInstance.getMovementOffsets(10, this.partialTicks)[0]);
//            float f7 = Mth.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] + (double) (f6 / 2.0F));
//            float f8 = f * ((float) Math.PI * 2F);
//
//            for (int i = 0; i < 5; ++i) {
//                double[] adouble1 = this.dragonInstance.getMovementOffsets(5 - i, this.partialTicks);
//                float f9 = (float) Math.cos((float) i * 0.45F + f8) * 0.15F;
//                this.spine.yRot = Mth.rotWrap(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F) * 1.5F;
//                this.spine.xRot = f9 + this.dragonInstance.getHeadPartYOffset(i, adouble, adouble1) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
//                this.spine.zRot = -Mth.rotWrap(adouble1[0] - (double) f7) * ((float) Math.PI / 180F) * 1.5F;
//                this.spine.y = f3;
//                this.spine.z = f4;
//                this.spine.x = f2;
//                f3 = (float) ((double) f3 + Math.sin(this.spine.xRot) * 10.0D);
//                f4 = (float) ((double) f4 - Math.cos(this.spine.yRot) * Math.cos(this.spine.xRot) * 10.0D);
//                f2 = (float) ((double) f2 - Math.sin(this.spine.yRot) * Math.cos(this.spine.xRot) * 10.0D);
//                this.spine.render(mStack, getter, packedLight, packedOverlay);
//            }
//
//            this.head.y = f3;
//            this.head.z = f4;
//            this.head.x = f2;
//            double[] adouble2 = this.dragonInstance.getMovementOffsets(0, this.partialTicks);
//            this.head.yRot = Mth.rotWrap(adouble2[0] - adouble[0]) * ((float) Math.PI / 180F);
//            this.head.xRot = Mth.rotWrap(this.dragonInstance.getHeadPartYOffset(6, adouble, adouble2)) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
//            this.head.zRot = -Mth.rotWrap(adouble2[0] - (double) f7) * ((float) Math.PI / 180F);
//            this.head.render(mStack, getter, packedLight, packedOverlay);
//            mStack.pushPose();
//            mStack.translate(0.0D, 1.0D, 0.0D);
//            mStack.mulPose(Vector3f.ZP.rotationDegrees(-f6 * 1.5F));
//            mStack.translate(0.0D, -1.0D, 0.0D);
//            this.body.zRot = 0.0F;
//            this.body.render(mStack, getter, packedLight, packedOverlay);
//            float f10 = f * ((float) Math.PI * 2F);
//            this.leftProximalWing.xRot = 0.125F - (float) Math.cos(f10) * 0.2F;
//            this.leftProximalWing.yRot = -0.25F;
//            this.leftProximalWing.zRot = -((float) (Math.sin(f10) + 0.125D)) * 0.8F;
//            this.leftDistalWing.zRot = (float) (Math.sin(f10 + 2.0F) + 0.5D) * 0.75F;
//            this.rightProximalWing.xRot = this.leftProximalWing.xRot;
//            this.rightProximalWing.yRot = -this.leftProximalWing.yRot;
//            this.rightProximalWing.zRot = -this.leftProximalWing.zRot;
//            this.rightDistalWing.zRot = -this.leftDistalWing.zRot;
//            this.renderSide(mStack, getter, packedLight, packedOverlay, f1, this.leftProximalWing, this.leftForeThigh, this.leftForeLeg, this.leftForeFoot, this.leftHindThigh, this.leftHindLeg, this.leftHindFoot);
//            this.renderSide(mStack, getter, packedLight, packedOverlay, f1, this.rightProximalWing, this.rightForeThigh, this.rightForeLeg, this.rightForeFoot, this.rightHindThigh, this.rightHindLeg, this.rightHindFoot);
//            mStack.popPose();
//            float f11 = -((float) Math.sin(f * ((float) Math.PI * 2F))) * 0.0F;
//            f8 = f * ((float) Math.PI * 2F);
//            f3 = 10.0F;
//            f4 = 60.0F;
//            f2 = 0.0F;
//            adouble = this.dragonInstance.getMovementOffsets(11, this.partialTicks);
//
//            for (int j = 0; j < 12; ++j) {
//                adouble2 = this.dragonInstance.getMovementOffsets(12 + j, this.partialTicks);
//                f11 = (float) ((double) f11 + Math.sin((float) j * 0.45F + f8) * (double) 0.05F);
//                this.spine.yRot = (Mth.rotWrap(adouble2[0] - adouble[0]) * 1.5F + 180.0F) * ((float) Math.PI / 180F);
//                this.spine.xRot = f11 + (float) (adouble2[1] - adouble[1]) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
//                this.spine.zRot = Mth.rotWrap(adouble2[0] - (double) f7) * ((float) Math.PI / 180F) * 1.5F;
//                this.spine.y = f3;
//                this.spine.z = f4;
//                this.spine.x = f2;
//                f3 = (float) ((double) f3 + Math.sin(this.spine.xRot) * 10.0D);
//                f4 = (float) ((double) f4 - Math.cos(this.spine.yRot) * Math.cos(this.spine.xRot) * 10.0D);
//                f2 = (float) ((double) f2 - Math.sin(this.spine.yRot) * Math.cos(this.spine.xRot) * 10.0D);
//                this.spine.render(mStack, getter, packedLight, packedOverlay);
//            }
//
//            mStack.popPose();
        }

        private void renderSide(PoseStack p_229081_1_, VertexConsumer p_229081_2_, int p_229081_3_, int p_229081_4_, float p_229081_5_, ModelPart p_229081_6_, ModelPart p_229081_7_, ModelPart p_229081_8_, ModelPart p_229081_9_, ModelPart p_229081_10_, ModelPart p_229081_11_, ModelPart p_229081_12_) {
            p_229081_10_.xRot = 1.0F + p_229081_5_ * 0.1F;
            p_229081_11_.xRot = 0.5F + p_229081_5_ * 0.1F;
            p_229081_12_.xRot = 0.75F + p_229081_5_ * 0.1F;
            p_229081_7_.xRot = 1.3F + p_229081_5_ * 0.1F;
            p_229081_8_.xRot = -0.5F - p_229081_5_ * 0.1F;
            p_229081_9_.xRot = 0.75F + p_229081_5_ * 0.1F;
            p_229081_6_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_7_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_10_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
        }
    }
}
