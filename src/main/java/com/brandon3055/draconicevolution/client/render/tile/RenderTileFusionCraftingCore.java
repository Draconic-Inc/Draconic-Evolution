package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.EffectLib;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler;
import com.brandon3055.draconicevolution.client.render.tile.fxhandlers.FusionTileFXHandler.IngredFX;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class RenderTileFusionCraftingCore extends TileEntityRenderer<TileFusionCraftingCore> {

    private static Random rand = new Random();

    private RenderType particleType = RenderType.create("particle_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.builder()
            .setTextureState(new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS, false, false))
            .setAlphaState(RenderState.DEFAULT_ALPHA)
            .setWriteMaskState(RenderState.COLOR_DEPTH_WRITE)
            .setTexturingState(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false)
    );

    public RenderTileFusionCraftingCore(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileFusionCraftingCore te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packetLight, int packetOverlay) {
        renderContent(te, partialTicks, mStack, getter, packetLight, packetOverlay);
        FusionTileFXHandler handler = (FusionTileFXHandler) te.fxHandler;
        if (handler.renderActive()) {
            renderEffects(te, handler, partialTicks, mStack, getter, packetLight, packetOverlay);
        }
    }

    private void renderContent(TileFusionCraftingCore te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packetLight, int packetOverlay) {
        ItemStack stack = !te.getOutputStack().isEmpty() && !te.isCrafting() ? te.getOutputStack() : te.getCatalystStack();
        Minecraft mc = Minecraft.getInstance();
        if (!stack.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.5, 0.5);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternion(0, (ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0, true));
            mc.getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, packetLight, packetOverlay, mStack, getter);
            mStack.popPose();
        }
    }

    private void renderEffects(TileFusionCraftingCore core, FusionTileFXHandler handler, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packetLight, int packetOverlay) {
        Minecraft mc = Minecraft.getInstance();
        ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
        mStack.translate(0.5, 0.5, 0.5);

        ParticleStatus pStatus = mc.options.particles;
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
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(particleType), mStack);
        if (handler.injectTime > 0) {
            rand.setSeed(3055);
            double anim = handler.getRotationAnim(partialTicks);
            int chargePCount = 64;
            float pScale = 0.125F/2;
            for (i = 0; i < chargePCount; i++) {
                anim += rand.nextGaussian();
                float scale = MathHelper.clamp((handler.injectTime * chargePCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
                if (scale <= 0) break;
                float rotX = (float) (rand.nextFloat() * Math.PI * 2 + anim / 10);
                float rotY = (float) (rand.nextFloat() * Math.PI * 2 + anim / 15);
                double radius = 0.35 * MathUtils.clampMap(core.craftAnimProgress.get(), 0.95F, 1F, 1F, 0F);
                double x = radius * MathHelper.cos(rotX) * MathHelper.sin(rotY);
                double y = radius * MathHelper.sin(rotX) * MathHelper.sin(rotY);
                double z = radius * MathHelper.cos(rotY);
                EffectLib.drawParticle(cameraRotation, builder, DETextures.MIXED_PARTICLE[(TimeKeeper.getClientTick() + rand.nextInt(6423)) % DETextures.MIXED_PARTICLE.length], 1F, 0, 0, x, y, z, scale, 240);
            }
        }

        //Outer Loopy Effects
        if (handler.chargeState > 0) {
            for (i = 0; i < 4; i++) {
                float loopOffset = ((i / 4F) * ((float) Math.PI * 2F)) + (TimeKeeper.getClientTick() / 100F);
                for (int j = 0; j < 8; j++) {
                    float rot = ((j / 64F) * (float) Math.PI * 2F) + (TimeKeeper.getClientTick() / 10F) + loopOffset;
                    if (j > handler.chargeState * 8F) continue;
                    double x = MathHelper.sin(rot) * 2;
                    double z = MathHelper.cos(rot) * 2;
                    double y = MathHelper.cos(rot + loopOffset) * 1;
                    float scale = 0.1F * (j / 8F);
                    EffectLib.drawParticle(cameraRotation, builder, DETextures.ENERGY_PARTICLE[(TimeKeeper.getClientTick() + j) % DETextures.ENERGY_PARTICLE.length], 106/255F, 13/255F, 173/255F, x, y, z, scale, 240);
                }
            }
            if (handler.injectTime > 0 && TimeKeeper.getClientTick() % 5 == 0) {
                int pos = rand.nextInt(4);
                for (i = 0; i < 4; i++) {
                    if (i != pos) continue;
                    float loopOffset = ((i / 4F) * ((float) Math.PI * 2F)) + (TimeKeeper.getClientTick() / 100F);
                    float rot = ((7 / 64F) * (float) Math.PI * 2F) + (TimeKeeper.getClientTick() / 10F) + loopOffset;
                    double x = MathHelper.sin(rot) * 2;
                    double z = MathHelper.cos(rot) * 2;
                    double y = MathHelper.cos(rot + loopOffset) * 1;
                    EffectLib.renderLightningP2PRotate(mStack, getter, new Vector3(x, y, z), Vector3.ZERO, 8, ((TimeKeeper.getClientTick()) / 2), 0.06F, 0.04F, false, 0, 0x6300BD);
                }
            }
        }
    }

    private void renderIngredientEffect(ActiveRenderInfo renderInfo, MatrixStack mStack, IRenderTypeBuffer getter, float partialTicks, long randSeed, IngredFX ingred, int totalParticles) {
        Rotation cameraRotation = new Rotation(new Quat(renderInfo.rotation()));
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(particleType), mStack);

        //Charge particle ball
        rand.setSeed(randSeed);
        double anim = ingred.getChargeAnim(partialTicks);
        int chargePCount = Math.min(64, totalParticles / 3);
        float pScale = 0.025F;
        for (int i = 0; i < chargePCount; i++) {
            anim += rand.nextGaussian();
            float scale = MathHelper.clamp(((ingred.getCharge() * ingred.dieOut) * chargePCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
            if (scale <= 0) break;
            float rotX = (float) (rand.nextFloat() * Math.PI * 2 + anim / 10);
            float rotY = (float) (rand.nextFloat() * Math.PI * 2 + anim / 15);
            double radius = 0.25;
            double x = ingred.pos.x + radius * MathHelper.cos(rotX) * MathHelper.sin(rotY);
            double y = ingred.pos.y + radius * MathHelper.sin(rotX) * MathHelper.sin(rotY);
            double z = ingred.pos.z + radius * MathHelper.cos(rotY);
            EffectLib.drawParticle(cameraRotation, builder, DETextures.ENERGY_PARTICLE[(TimeKeeper.getClientTick() + rand.nextInt(6423)) % DETextures.ENERGY_PARTICLE.length], 0F, 0.8F + (rand.nextFloat() * 0.2F), 1F, x, y, z, scale, 240);
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
            float scale = MathHelper.clamp(((ingred.coreAnim * ingred.dieOut) * itemPCount) - i, 0F, 1F);
            if (scale <= 0) break;
            scale *= 1 - Math.sin(pulse * Math.PI * 2);
            pos.set(MathUtils.nextFloat(), MathUtils.nextFloat(), MathUtils.nextFloat());
            pos.subtract(0.5);
            pos.normalize();
            pos.multiply(MathUtils.nextFloat() * 0.1875);
            pos.add(ingred.pos);
            EffectLib.drawParticle(cameraRotation, builder, DETextures.SPELL_PARTICLE[(rand.nextInt(DETextures.SPELL_PARTICLE.length) + TimeKeeper.getClientTick()) % DETextures.SPELL_PARTICLE.length], 0.7F, 0F, 0F, pos.x, pos.y, pos.z, scale * pScale, 240);
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
                float scale = MathHelper.clamp(((Math.min(1, ingred.beamAnim / 60) * ingred.dieOut) * beamPCount) - i, 0F, 1F) * pScale * (0.7F + (rand.nextFloat() * 0.3F));
                if (scale <= 0) break;
                Vector3 start = ingred.pos.copy().add((0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2));
                Vector3 end = new Vector3((0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2), (0.5 - rand.nextDouble()) * (randOffset * 2));
                pos = MathUtils.interpolateVec3(start, end, (anim / 10) % 1D);
                EffectLib.drawParticle(cameraRotation, builder, DETextures.SPARK_PARTICLE[(rand.nextInt(DETextures.SPARK_PARTICLE.length) + TimeKeeper.getClientTick()) % DETextures.SPARK_PARTICLE.length], 0.7F + ((((float) anim / 10F) % 1F) * 0.3F), 0F, 0F, pos.x, pos.y, pos.z, scale, 240);
            }
        }
    }

    /*
     * Calculate point on surface of sphere based on input x and y rotation
     * double x = center.x + radius * Math.cos(rotX) * Math.sin(rotY);
     * double y = center.y + radius * Math.sin(rotX) * Math.sin(rotY);
     * double z = center.z + radius * Math.cos(rotY);
     * */
}