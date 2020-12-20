package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.render.shader.*;
import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.client.BCClientEventHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.VERTEX;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static net.minecraft.client.renderer.RenderState.*;

@OnlyIn(Dist.CLIENT)
public class DraconicGuardianRenderer extends EntityRenderer<DraconicGuardianEntity> {
    public static final ResourceLocation ENDERCRYSTAL_BEAM_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/chaos_guardian.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType dragonCutoutType = RenderType.getEntityCutoutNoCull(GUARDIAN_TEXTURE);
    private static final RenderType dragonDeathType = RenderType.getEntityDecal(GUARDIAN_TEXTURE);
    private static final RenderType eyesType = RenderType.getEyes(EYES_TEXTURE);
    private static final RenderType beamType = RenderType.getEntitySmoothCutout(ENDERCRYSTAL_BEAM_TEXTURES);
    private static final RenderType shieldType = RenderType.makeType("shield_type", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
            .cull(CULL_DISABLED)
            .alpha(DEFAULT_ALPHA)
            .depthTest(DEPTH_EQUAL)
            .build(false));

    public static ShaderProgram shieldShader = ShaderProgramBuilder.builder()
            .addShader("vert", shader -> shader
                    .type(VERTEX)
                    .source(new ResourceLocation(MODID, "shaders/guardian_shield.vert"))
            )
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/guardian_shield.frag"))
                    .uniform("time", UniformType.FLOAT)
                    .uniform("baseColour", UniformType.VEC4)
            )
            .whenUsed(cache -> cache.glUniform1f("time", (BCClientEventHandler.elapsedTicks + Minecraft.getInstance().getRenderPartialTicks()) / 20))
            .build();

    private static final float sqrt3div2 = (float) (Math.sqrt(3.0D) / 2.0D);
    private final DraconicGuardianRenderer.EnderDragonModel model = new DraconicGuardianRenderer.EnderDragonModel();


    public DraconicGuardianRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    @Override
    public void render(DraconicGuardianEntity guardian, float entityYaw, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight) {
        mStack.push();
        float f = (float) guardian.getMovementOffsets(7, partialTicks)[0];
        float f1 = (float) (guardian.getMovementOffsets(5, partialTicks)[1] - guardian.getMovementOffsets(10, partialTicks)[1]);
        mStack.rotate(Vector3f.YP.rotationDegrees(-f));
        mStack.rotate(Vector3f.XP.rotationDegrees(f1 * 10.0F));
        mStack.translate(0.0D, 0.0D, 1.0D);
        mStack.scale(-1.0F, -1.0F, 1.0F);
        mStack.translate(0.0D, -1.501F, 0.0D);
        boolean flag = guardian.hurtTime > 0;
        this.model.setLivingAnimations(guardian, 0.0F, 0.0F, partialTicks);

        if (guardian.deathTicks > 0) {
            float progress = (float) guardian.deathTicks / 200.0F;
            IVertexBuilder builder = getter.getBuffer(RenderType.getEntityAlpha(DRAGON_EXPLODING_TEXTURES, progress));
            this.model.render(mStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            builder = getter.getBuffer(dragonDeathType);
            this.model.render(mStack, builder, packedLight, OverlayTexture.getPackedUV(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            IVertexBuilder builder = getter.getBuffer(dragonCutoutType);
            this.model.render(mStack, builder, packedLight, OverlayTexture.getPackedUV(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        }
        IVertexBuilder builder;

        int shieldState = guardian.getDataManager().get(DraconicGuardianEntity.SHIELD_STATE);
        if (shieldState > 0) {
            float hit = (shieldState / 500F) - 1F;
            Color color = Color.getHSBColor(hit / 8F, 1, 1);
            //Render Shield //TODO this could sue some work
            UniformCache uniforms = shieldShader.pushCache();
            uniforms.glUniform4f("baseColour", color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.5F * (shieldState / 500F));
            builder = getter.getBuffer(new ShaderRenderType(shieldType, shieldShader, uniforms));
            this.model.render(mStack, builder, packedLight, OverlayTexture.getPackedUV(0.0F, flag), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        builder = getter.getBuffer(eyesType);
        this.model.render(mStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (guardian.deathTicks > 0) {
            float f5 = ((float) guardian.deathTicks + partialTicks) / 200.0F;
            float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);
            Random random = new Random(432L);
            builder = getter.getBuffer(RenderType.getLightning());
            mStack.push();
            mStack.translate(0.0D, -1.0D, -2.0D);

            for (int i = 0; (float) i < (f5 + f5 * f5) / 2.0F * 60.0F; ++i) {
                mStack.rotate(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.rotate(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.rotate(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.rotate(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.rotate(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                mStack.rotate(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F + f5 * 90.0F));
                float f3 = random.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
                float f4 = random.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
                Matrix4f matrix4f = mStack.getLast().getMatrix();
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

            mStack.pop();
        }

        mStack.pop();
        if (guardian.closestGuardianCrystal != null) {
            mStack.push();
            float relX = (float) (guardian.closestGuardianCrystal.getPosX() - MathHelper.lerp(partialTicks, guardian.prevPosX, guardian.getPosX()));
            float relY = (float) (guardian.closestGuardianCrystal.getPosY() - MathHelper.lerp(partialTicks, guardian.prevPosY, guardian.getPosY()));
            float relZ = (float) (guardian.closestGuardianCrystal.getPosZ() - MathHelper.lerp(partialTicks, guardian.prevPosZ, guardian.getPosZ()));
            renderBeam(relX, relY + GuardianCrystalRenderer.func_229051_a_(guardian.closestGuardianCrystal, partialTicks), relZ, partialTicks, guardian.ticksExisted, mStack, getter, packedLight);
            mStack.pop();
        }

        super.render(guardian, entityYaw, partialTicks, mStack, getter, packedLight);
    }

    private static void deathAnimA(IVertexBuilder builder, Matrix4f mat, int alpha) {
        builder.pos(mat, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.pos(mat, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
    }

    private static void deathAnimB(IVertexBuilder builder, Matrix4f mat, float p_229060_2_, float p_229060_3_) {
        builder.pos(mat, -sqrt3div2 * p_229060_3_, p_229060_2_, -0.5F * p_229060_3_).color(255, 0, 255, 0).endVertex();
    }

    private static void deathAnimC(IVertexBuilder builder, Matrix4f mat, float p_229062_2_, float p_229062_3_) {
        builder.pos(mat, sqrt3div2 * p_229062_3_, p_229062_2_, -0.5F * p_229062_3_).color(255, 0, 255, 0).endVertex();
    }

    private static void deathAnimD(IVertexBuilder builder, Matrix4f mat, float p_229063_2_, float p_229063_3_) {
        builder.pos(mat, 0.0F, p_229063_2_, 1.0F * p_229063_3_).color(255, 0, 255, 0).endVertex();
    }

    public static void renderBeam(float crystalRelX, float crystalRelY, float crystalRelZ, float partialTicks, int animTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight) {
        float xzDistance = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelZ * crystalRelZ);
        float distance = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ);
        mStack.push();
        mStack.translate(0.0D, 2.0D, 0.0D);
        mStack.rotate(Vector3f.YP.rotation((float) (-Math.atan2(crystalRelZ, crystalRelX)) - ((float) Math.PI / 2F)));
        mStack.rotate(Vector3f.XP.rotation((float) (-Math.atan2(xzDistance, crystalRelY)) - ((float) Math.PI / 2F)));
        IVertexBuilder ivertexbuilder = getter.getBuffer(beamType);
        float f2 = 0.0F - ((float) animTicks + partialTicks) * 0.01F;
        float f3 = MathHelper.sqrt(crystalRelX * crystalRelX + crystalRelY * crystalRelY + crystalRelZ * crystalRelZ) / 32.0F - ((float) animTicks + partialTicks) * 0.01F;
        int i = 8;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;
        MatrixStack.Entry stackLast = mStack.getLast();
        Matrix4f lastMatrix = stackLast.getMatrix();
        Matrix3f lastNormal = stackLast.getNormal();

        for (int j = 1; j <= 8; ++j) {
            float rSin = MathHelper.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float rCos = MathHelper.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.75F;
            float indexDecimal = (float) j / 8.0F;
            ivertexbuilder.pos(lastMatrix, f4 * 0.2F, f5 * 0.2F, 0.0F).color(0, 0, 0, 255).tex(f6, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(lastMatrix, f4, f5, distance).color(255, 255, 255, 255).tex(f6, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(lastMatrix, rSin, rCos, distance).color(255, 255, 255, 255).tex(indexDecimal, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            ivertexbuilder.pos(lastMatrix, rSin * 0.2F, rCos * 0.2F, 0.0F).color(0, 0, 0, 255).tex(indexDecimal, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(lastNormal, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = rSin;
            f5 = rCos;
            f6 = indexDecimal;
        }

        mStack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(DraconicGuardianEntity entity) {
        return DRAGON_TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class EnderDragonModel extends EntityModel<DraconicGuardianEntity> {
        private final ModelRenderer head;
        private final ModelRenderer spine;
        private final ModelRenderer jaw;
        private final ModelRenderer body;
        private ModelRenderer leftProximalWing;
        private ModelRenderer leftDistalWing;
        private ModelRenderer leftForeThigh;
        private ModelRenderer leftForeLeg;
        private ModelRenderer leftForeFoot;
        private ModelRenderer leftHindThigh;
        private ModelRenderer leftHindLeg;
        private ModelRenderer leftHindFoot;
        private ModelRenderer rightProximalWing;
        private ModelRenderer rightDistalWing;
        private ModelRenderer rightForeThigh;
        private ModelRenderer rightForeLeg;
        private ModelRenderer rightForeFoot;
        private ModelRenderer rightHindThigh;
        private ModelRenderer rightHindLeg;
        private ModelRenderer rightHindFoot;
        @Nullable
        private DraconicGuardianEntity dragonInstance;
        private float partialTicks;

        public EnderDragonModel() {
            this.textureWidth = 256;
            this.textureHeight = 256;
            float f = -16.0F;
            this.head = new ModelRenderer(this);
            this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
            this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
            this.head.mirror = true;
            this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
            this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
            this.head.mirror = false;
            this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
            this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
            this.jaw = new ModelRenderer(this);
            this.jaw.setRotationPoint(0.0F, 4.0F, -8.0F);
            this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
            this.head.addChild(this.jaw);
            this.spine = new ModelRenderer(this);
            this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
            this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
            this.body = new ModelRenderer(this);
            this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
            this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
            this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
            this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
            this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
            this.leftProximalWing = new ModelRenderer(this);
            this.leftProximalWing.mirror = true;
            this.leftProximalWing.setRotationPoint(12.0F, 5.0F, 2.0F);
            this.leftProximalWing.addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
            this.leftProximalWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
            this.leftDistalWing = new ModelRenderer(this);
            this.leftDistalWing.mirror = true;
            this.leftDistalWing.setRotationPoint(56.0F, 0.0F, 0.0F);
            this.leftDistalWing.addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
            this.leftDistalWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
            this.leftProximalWing.addChild(this.leftDistalWing);
            this.leftForeThigh = new ModelRenderer(this);
            this.leftForeThigh.setRotationPoint(12.0F, 20.0F, 2.0F);
            this.leftForeThigh.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
            this.leftForeLeg = new ModelRenderer(this);
            this.leftForeLeg.setRotationPoint(0.0F, 20.0F, -1.0F);
            this.leftForeLeg.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
            this.leftForeThigh.addChild(this.leftForeLeg);
            this.leftForeFoot = new ModelRenderer(this);
            this.leftForeFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
            this.leftForeFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
            this.leftForeLeg.addChild(this.leftForeFoot);
            this.leftHindThigh = new ModelRenderer(this);
            this.leftHindThigh.setRotationPoint(16.0F, 16.0F, 42.0F);
            this.leftHindThigh.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
            this.leftHindLeg = new ModelRenderer(this);
            this.leftHindLeg.setRotationPoint(0.0F, 32.0F, -4.0F);
            this.leftHindLeg.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
            this.leftHindThigh.addChild(this.leftHindLeg);
            this.leftHindFoot = new ModelRenderer(this);
            this.leftHindFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
            this.leftHindFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
            this.leftHindLeg.addChild(this.leftHindFoot);
            this.rightProximalWing = new ModelRenderer(this);
            this.rightProximalWing.setRotationPoint(-12.0F, 5.0F, 2.0F);
            this.rightProximalWing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
            this.rightProximalWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
            this.rightDistalWing = new ModelRenderer(this);
            this.rightDistalWing.setRotationPoint(-56.0F, 0.0F, 0.0F);
            this.rightDistalWing.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
            this.rightDistalWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
            this.rightProximalWing.addChild(this.rightDistalWing);
            this.rightForeThigh = new ModelRenderer(this);
            this.rightForeThigh.setRotationPoint(-12.0F, 20.0F, 2.0F);
            this.rightForeThigh.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
            this.rightForeLeg = new ModelRenderer(this);
            this.rightForeLeg.setRotationPoint(0.0F, 20.0F, -1.0F);
            this.rightForeLeg.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
            this.rightForeThigh.addChild(this.rightForeLeg);
            this.rightForeFoot = new ModelRenderer(this);
            this.rightForeFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
            this.rightForeFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
            this.rightForeLeg.addChild(this.rightForeFoot);
            this.rightHindThigh = new ModelRenderer(this);
            this.rightHindThigh.setRotationPoint(-16.0F, 16.0F, 42.0F);
            this.rightHindThigh.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
            this.rightHindLeg = new ModelRenderer(this);
            this.rightHindLeg.setRotationPoint(0.0F, 32.0F, -4.0F);
            this.rightHindLeg.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
            this.rightHindThigh.addChild(this.rightHindLeg);
            this.rightHindFoot = new ModelRenderer(this);
            this.rightHindFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
            this.rightHindFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
            this.rightHindLeg.addChild(this.rightHindFoot);
        }

        public void setLivingAnimations(DraconicGuardianEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
            this.dragonInstance = entityIn;
            this.partialTicks = partialTick;
        }

        public void setRotationAngles(DraconicGuardianEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        public void render(MatrixStack mStack, IVertexBuilder getter, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            mStack.push();
            float f = MathHelper.lerp(this.partialTicks, this.dragonInstance.prevAnimTime, this.dragonInstance.animTime);
            this.jaw.rotateAngleX = (float) (Math.sin(f * ((float) Math.PI * 2F)) + 1.0D) * 0.2F;
            float f1 = (float) (Math.sin(f * ((float) Math.PI * 2F) - 1.0F) + 1.0D);
            f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
            mStack.translate(0.0D, f1 - 2.0F, -3.0D);
            mStack.rotate(Vector3f.XP.rotationDegrees(f1 * 2.0F));
            float f2 = 0.0F;
            float f3 = 20.0F;
            float f4 = -12.0F;
            float f5 = 1.5F;
            double[] adouble = this.dragonInstance.getMovementOffsets(6, this.partialTicks);
            float f6 = MathHelper.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] - this.dragonInstance.getMovementOffsets(10, this.partialTicks)[0]);
            float f7 = MathHelper.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] + (double) (f6 / 2.0F));
            float f8 = f * ((float) Math.PI * 2F);

            for (int i = 0; i < 5; ++i) {
                double[] adouble1 = this.dragonInstance.getMovementOffsets(5 - i, this.partialTicks);
                float f9 = (float) Math.cos((float) i * 0.45F + f8) * 0.15F;
                this.spine.rotateAngleY = MathHelper.rotWrap(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F) * 1.5F;
                this.spine.rotateAngleX = f9 + this.dragonInstance.getHeadPartYOffset(i, adouble, adouble1) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
                this.spine.rotateAngleZ = -MathHelper.rotWrap(adouble1[0] - (double) f7) * ((float) Math.PI / 180F) * 1.5F;
                this.spine.rotationPointY = f3;
                this.spine.rotationPointZ = f4;
                this.spine.rotationPointX = f2;
                f3 = (float) ((double) f3 + Math.sin(this.spine.rotateAngleX) * 10.0D);
                f4 = (float) ((double) f4 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0D);
                f2 = (float) ((double) f2 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0D);
                this.spine.render(mStack, getter, packedLight, packedOverlay);
            }

            this.head.rotationPointY = f3;
            this.head.rotationPointZ = f4;
            this.head.rotationPointX = f2;
            double[] adouble2 = this.dragonInstance.getMovementOffsets(0, this.partialTicks);
            this.head.rotateAngleY = MathHelper.rotWrap(adouble2[0] - adouble[0]) * ((float) Math.PI / 180F);
            this.head.rotateAngleX = MathHelper.rotWrap(this.dragonInstance.getHeadPartYOffset(6, adouble, adouble2)) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
            this.head.rotateAngleZ = -MathHelper.rotWrap(adouble2[0] - (double) f7) * ((float) Math.PI / 180F);
            this.head.render(mStack, getter, packedLight, packedOverlay);
            mStack.push();
            mStack.translate(0.0D, 1.0D, 0.0D);
            mStack.rotate(Vector3f.ZP.rotationDegrees(-f6 * 1.5F));
            mStack.translate(0.0D, -1.0D, 0.0D);
            this.body.rotateAngleZ = 0.0F;
            this.body.render(mStack, getter, packedLight, packedOverlay);
            float f10 = f * ((float) Math.PI * 2F);
            this.leftProximalWing.rotateAngleX = 0.125F - (float) Math.cos(f10) * 0.2F;
            this.leftProximalWing.rotateAngleY = -0.25F;
            this.leftProximalWing.rotateAngleZ = -((float) (Math.sin(f10) + 0.125D)) * 0.8F;
            this.leftDistalWing.rotateAngleZ = (float) (Math.sin(f10 + 2.0F) + 0.5D) * 0.75F;
            this.rightProximalWing.rotateAngleX = this.leftProximalWing.rotateAngleX;
            this.rightProximalWing.rotateAngleY = -this.leftProximalWing.rotateAngleY;
            this.rightProximalWing.rotateAngleZ = -this.leftProximalWing.rotateAngleZ;
            this.rightDistalWing.rotateAngleZ = -this.leftDistalWing.rotateAngleZ;
            this.func_229081_a_(mStack, getter, packedLight, packedOverlay, f1, this.leftProximalWing, this.leftForeThigh, this.leftForeLeg, this.leftForeFoot, this.leftHindThigh, this.leftHindLeg, this.leftHindFoot);
            this.func_229081_a_(mStack, getter, packedLight, packedOverlay, f1, this.rightProximalWing, this.rightForeThigh, this.rightForeLeg, this.rightForeFoot, this.rightHindThigh, this.rightHindLeg, this.rightHindFoot);
            mStack.pop();
            float f11 = -((float) Math.sin(f * ((float) Math.PI * 2F))) * 0.0F;
            f8 = f * ((float) Math.PI * 2F);
            f3 = 10.0F;
            f4 = 60.0F;
            f2 = 0.0F;
            adouble = this.dragonInstance.getMovementOffsets(11, this.partialTicks);

            for (int j = 0; j < 12; ++j) {
                adouble2 = this.dragonInstance.getMovementOffsets(12 + j, this.partialTicks);
                f11 = (float) ((double) f11 + Math.sin((float) j * 0.45F + f8) * (double) 0.05F);
                this.spine.rotateAngleY = (MathHelper.rotWrap(adouble2[0] - adouble[0]) * 1.5F + 180.0F) * ((float) Math.PI / 180F);
                this.spine.rotateAngleX = f11 + (float) (adouble2[1] - adouble[1]) * ((float) Math.PI / 180F) * 1.5F * 5.0F;
                this.spine.rotateAngleZ = MathHelper.rotWrap(adouble2[0] - (double) f7) * ((float) Math.PI / 180F) * 1.5F;
                this.spine.rotationPointY = f3;
                this.spine.rotationPointZ = f4;
                this.spine.rotationPointX = f2;
                f3 = (float) ((double) f3 + Math.sin(this.spine.rotateAngleX) * 10.0D);
                f4 = (float) ((double) f4 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0D);
                f2 = (float) ((double) f2 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0D);
                this.spine.render(mStack, getter, packedLight, packedOverlay);
            }

            mStack.pop();
        }

        private void func_229081_a_(MatrixStack p_229081_1_, IVertexBuilder p_229081_2_, int p_229081_3_, int p_229081_4_, float p_229081_5_, ModelRenderer p_229081_6_, ModelRenderer p_229081_7_, ModelRenderer p_229081_8_, ModelRenderer p_229081_9_, ModelRenderer p_229081_10_, ModelRenderer p_229081_11_, ModelRenderer p_229081_12_) {
            p_229081_10_.rotateAngleX = 1.0F + p_229081_5_ * 0.1F;
            p_229081_11_.rotateAngleX = 0.5F + p_229081_5_ * 0.1F;
            p_229081_12_.rotateAngleX = 0.75F + p_229081_5_ * 0.1F;
            p_229081_7_.rotateAngleX = 1.3F + p_229081_5_ * 0.1F;
            p_229081_8_.rotateAngleX = -0.5F - p_229081_5_ * 0.1F;
            p_229081_9_.rotateAngleX = 0.75F + p_229081_5_ * 0.1F;
            p_229081_6_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_7_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_10_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
        }
    }
}
