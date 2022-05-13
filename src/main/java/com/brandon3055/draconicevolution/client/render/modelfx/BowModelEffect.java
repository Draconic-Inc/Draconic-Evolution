package com.brandon3055.draconicevolution.client.render.modelfx;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 28/2/21
 */
public class BowModelEffect extends ModelEffect {
    private RenderType renderType = RenderType.create("modelEffectType", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/white_orb.png"), false, false))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
//            .setAlphaState(RenderStateShard.NO_ALPHA)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                    .createCompositeState(false)
    );

    private RenderType renderSolidType = RenderType.create("modelEffectType4", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_FAN, 256, RenderType.CompositeState.builder()
                    .setCullState(RenderStateShard.NO_CULL)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
                    .createCompositeState(false)
    );

    public float animTime = 0;
    public int colour = 0xFFFFFF;

    public BowModelEffect() {}

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    protected void doRender(VertexConsumer builder, float partialTicks, TechLevel techLevel) {
        int pCount = 50;
//        float time = TimeKeeper.getClientTick() + partialTicks;
        float velocity = 0.05F;
        float drag = 0.95F;

        float r = ((colour >> 16) & 0xFF) / 255F;
        float g = ((colour >> 8) & 0xFF) / 255F;
        float b = (colour & 0xFF) / 255F;

        setRandSeed(0);//time / 20);

        for (int i = 0; i < pCount; i++) {
            float time = animTime + i * 2048;//(Math.min(1, animTime / 5));

            float age = time % 20;
//            setRandSeed(time / 20);

            float xVel = (nextFloat() - 0.5F) * velocity;
            float yVel = (nextFloat() - 0.5F) * velocity;
            float zVel = (nextFloat() - 0.5F) * velocity;

            yVel += 0.04 * (0.05) * age;
            xVel *= Math.pow(drag, age);
            yVel *= Math.pow(drag, age);
            zVel *= Math.pow(drag, age);

            double x = xVel * age;
            double y = yVel * age;
            double z = zVel * age;
            double scaleMod = MathUtils.clampMap(age, 0, 5, 0, 1) * MathUtils.clampMap(age, 5, 20, 1, 0) * Math.min(1, animTime / 5);
            drawParticle(builder, x, y, z, 0.1 * scaleMod, r, g, b, 1F);
        }
    }
}