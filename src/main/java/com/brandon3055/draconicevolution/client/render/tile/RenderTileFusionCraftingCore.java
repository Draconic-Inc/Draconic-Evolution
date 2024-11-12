package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.AtlasTextureHelper;
import com.brandon3055.draconicevolution.client.render.EffectLib;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler.IngredFX;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.client.AtlasTextureHelper.*;

public class RenderTileFusionCraftingCore implements BlockEntityRenderer<TileFusionCraftingCore> {

    private static Random rand = new Random();

    private RenderType particleType = RenderType.create("particle_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
            .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .createCompositeState(false)
    );

    public RenderTileFusionCraftingCore(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileFusionCraftingCore te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packetLight, int packetOverlay) {
        renderContent(te, partialTicks, mStack, getter, packetLight, packetOverlay);
        FusionTileFXHandler handler = (FusionTileFXHandler) te.fxHandler;
        if (handler.renderActive()) {
            renderEffects(te, handler, partialTicks, mStack, getter, packetLight, packetOverlay);
        }
    }

    private void renderContent(TileFusionCraftingCore te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packetLight, int packetOverlay) {
        ItemStack stack = !te.getOutputStack().isEmpty() && !te.isCrafting() ? te.getOutputStack() : te.getCatalystStack();
        Minecraft mc = Minecraft.getInstance();
        if (!stack.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.5, 0.5);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(Axis.YP.rotationDegrees((TimeKeeper.getClientTick() + partialTicks) * 0.8F));
            mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packetLight, packetOverlay, mStack, getter, te.getLevel(), te.posSeed());
            mStack.popPose();
        }
    }

    private void renderEffects(TileFusionCraftingCore core, FusionTileFXHandler handler, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packetLight, int packetOverlay) {
        Minecraft mc = Minecraft.getInstance();
        Camera renderInfo = mc.gameRenderer.getMainCamera();
        mStack.translate(0.5, 0.5, 0.5);

        ParticleStatus pStatus = mc.options.particles().get();
        double particleSetting = pStatus == ParticleStatus.ALL ? 1 : pStatus == ParticleStatus.DECREASED ? 2 / 3D : 1 / 3D;
        //Total particle allocation for ingredient effects
        int maxParticles = (int) (1000 * particleSetting);
        List<IngredFX> ingredFXList = handler.getIngredients(partialTicks);
        int i = 0;
        for (IngredFX ingred : ingredFXList) {
            renderIngredientEffect(renderInfo, mStack, getter, partialTicks, i++, ingred, maxParticles / ingredFXList.size());
            if (ingred.arcPos != null) {
                EffectLib.renderLightningP2PRotate(mStack, getter, ingred.pos, ingred.arcPos, 8, (TimeKeeper.getClientTick() / 2), 0.06F, 0.04F, false, 0, 0x6300BD);
            }
        }

        Rotation cameraRotation = new Rotation(new Quat(renderInfo.rotation()));
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(particleType), mStack);
        if (handler.injectTime > 0) {
            rand.setSeed(3055);
            double anim = handler.getRotationAnim(partialTicks);
            int chargePCount = 64;
            float pScale = 0.125F / 2;
            for (i = 0; i < chargePCount; i++) {
                anim += rand.nextGaussian();
                float scale = Mth.clamp((handler.injectTime * chargePCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
                if (scale <= 0) break;
                float rotX = (float) (rand.nextFloat() * Math.PI * 2 + anim / 10);
                float rotY = (float) (rand.nextFloat() * Math.PI * 2 + anim / 15);
                double radius = 0.35 * MathUtils.clampMap(core.craftAnimProgress.get(), 0.95F, 1F, 1F, 0F);
                double x = radius * Mth.cos(rotX) * Mth.sin(rotY);
                double y = radius * Mth.sin(rotX) * Mth.sin(rotY);
                double z = radius * Mth.cos(rotY);
                EffectLib.drawParticle(cameraRotation, builder, MIXED_PARTICLE[(TimeKeeper.getClientTick() + rand.nextInt(6423)) % MIXED_PARTICLE.length], 1F, 0, 0, x, y, z, scale, 240);
            }
        }

        //Outer Loopy Effects
        if (handler.chargeState > 0) {
            for (i = 0; i < 4; i++) {
                float loopOffset = ((i / 4F) * ((float) Math.PI * 2F)) + (TimeKeeper.getClientTick() / 100F);
                for (int j = 0; j < 8; j++) {
                    float rot = ((j / 64F) * (float) Math.PI * 2F) + (TimeKeeper.getClientTick() / 10F) + loopOffset;
                    if (j > handler.chargeState * 8F) continue;
                    double x = Mth.sin(rot) * 2;
                    double z = Mth.cos(rot) * 2;
                    double y = Mth.cos(rot + loopOffset) * 1;
                    float scale = 0.1F * (j / 8F);
                    EffectLib.drawParticle(cameraRotation, builder, ENERGY_PARTICLE[(TimeKeeper.getClientTick() + j) % ENERGY_PARTICLE.length], 106 / 255F, 13 / 255F, 173 / 255F, x, y, z, scale, 240);
                }
            }
            if (handler.injectTime > 0 && TimeKeeper.getClientTick() % 5 == 0) {
                int pos = rand.nextInt(4);
                for (i = 0; i < 4; i++) {
                    if (i != pos) continue;
                    float loopOffset = ((i / 4F) * ((float) Math.PI * 2F)) + (TimeKeeper.getClientTick() / 100F);
                    float rot = ((7 / 64F) * (float) Math.PI * 2F) + (TimeKeeper.getClientTick() / 10F) + loopOffset;
                    double x = Mth.sin(rot) * 2;
                    double z = Mth.cos(rot) * 2;
                    double y = Mth.cos(rot + loopOffset) * 1;
                    EffectLib.renderLightningP2PRotate(mStack, getter, new Vector3(x, y, z), Vector3.ZERO, 8, ((TimeKeeper.getClientTick()) / 2), 0.06F, 0.04F, false, 0, 0x6300BD);
                }
            }
        }
    }

    private void renderIngredientEffect(Camera renderInfo, PoseStack mStack, MultiBufferSource getter, float partialTicks, long randSeed, IngredFX ingred, int totalParticles) {
        Rotation cameraRotation = new Rotation(new Quat(renderInfo.rotation()));
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(particleType), mStack);

        //Charge particle ball
        rand.setSeed(randSeed);
        double anim = ingred.getChargeAnim(partialTicks);
        int chargePCount = Math.min(64, totalParticles / 3);
        float pScale = 0.025F;
        for (int i = 0; i < chargePCount; i++) {
            anim += rand.nextGaussian();
            float scale = Mth.clamp(((ingred.getCharge() * ingred.dieOut) * chargePCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
            if (scale <= 0) break;
            float rotX = (float) (rand.nextFloat() * Math.PI * 2 + anim / 10);
            float rotY = (float) (rand.nextFloat() * Math.PI * 2 + anim / 15);
            double radius = 0.25;
            double x = ingred.pos.x + radius * Mth.cos(rotX) * Mth.sin(rotY);
            double y = ingred.pos.y + radius * Mth.sin(rotX) * Mth.sin(rotY);
            double z = ingred.pos.z + radius * Mth.cos(rotY);
            EffectLib.drawParticle(cameraRotation, builder, ENERGY_PARTICLE[(TimeKeeper.getClientTick() + rand.nextInt(6423)) % ENERGY_PARTICLE.length], 0F, 0.8F + (rand.nextFloat() * 0.2F), 1F, x, y, z, scale, 240);
        }

        //Charge Conversion Particles
        int itemPCount = Math.min(48, totalParticles / 3);
        rand.setSeed(randSeed);
        anim = ingred.getChargeAnim(partialTicks);
        pScale = 0.0125F;
        Vector3 pos = new Vector3();
        for (int i = 0; i < itemPCount; i++) {
            anim += rand.nextDouble() * 69420;
            int seed = (int) Math.floor(anim / 20D);
            MathUtils.setRandSeed(seed);
            float pulse = ((float) anim / 20F) % 1F;
            float scale = Mth.clamp(((ingred.coreAnim * ingred.dieOut) * itemPCount) - i, 0F, 1F);
            if (scale <= 0) break;
            scale *= 1 - Math.sin(pulse * Math.PI * 2);
            pos.set(MathUtils.nextFloat(), MathUtils.nextFloat(), MathUtils.nextFloat());
            pos.subtract(0.5);
            pos.normalize();
            pos.multiply(MathUtils.nextFloat() * 0.1875);
            pos.add(ingred.pos);
            EffectLib.drawParticle(cameraRotation, builder, SPELL_PARTICLE[(rand.nextInt(SPELL_PARTICLE.length) + TimeKeeper.getClientTick()) % SPELL_PARTICLE.length], 0.7F, 0F, 0F, pos.x, pos.y, pos.z, scale * pScale, 240);
        }


        //Beam Effect
        double randOffset = 0.125;
        rand.setSeed(randSeed);
        anim = ingred.beamAnim;
        pScale = 0.025F;
        int beamPCount = Math.min(32, totalParticles / 3);
        if (ingred.beamAnim > 0) {
            for (int i = 0; i < beamPCount; i++) {
                anim += rand.nextDouble() * 64;
                float scale = Mth.clamp(((Math.min(1, ingred.beamAnim / 60) * ingred.dieOut) * beamPCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
                if (scale <= 0) break;
                Vector3 start = ingred.pos.copy().add((0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2));
                Vector3 end = new Vector3((0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2));
                pos = MathUtils.interpolateVec3(start, end, (anim / 10) % 1D);
                EffectLib.drawParticle(cameraRotation, builder, SPARK_PARTICLE[(rand.nextInt(SPARK_PARTICLE.length) + TimeKeeper.getClientTick()) % SPARK_PARTICLE.length], 0.7F + ((((float) anim / 10F) % 1F) * 0.3F), 0F, 0F, pos.x, pos.y, pos.z, scale, 240);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(TileFusionCraftingCore blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(16);
    }

    /*
     * Calculate point on surface of sphere based on input x and y rotation
     * double x = center.x + radius * Math.cos(rotX) * Math.sin(rotY);
     * double y = center.y + radius * Math.sin(rotX) * Math.sin(rotY);
     * double z = center.z + radius * Math.cos(rotY);
     * */
}