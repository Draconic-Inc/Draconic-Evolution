package com.brandon3055.draconicevolution.client.render.modelfx;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

/**
 * Created by brandon3055 on 28/2/21
 */
public class StaffModelEffect extends ModelEffect {

    private static final RenderType renderType = RenderType.create("modelEffectType", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                    .setShaderState(RenderType.POSITION_COLOR_TEX_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/white_orb.png"), false, false))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false)
    );

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    protected void doRender(VertexConsumer builder, float partialTicks, TechLevel techLevel) {
        boolean idle = false;
        float time = TimeKeeper.getClientTick() + partialTicks;

        Color color = techLevel == TechLevel.CHAOTIC ? new Color(0xFF2203) : new Color(0xFF4403);

        drawFeedInParticles(builder, 16, time, idle, color);
    }

    private void drawFeedInParticles(VertexConsumer builder, int pCount, float time, boolean idle, Color color) {
        double gemHeight = 0;//0.35; TODO Temp to make particles target crystal center
        double minY = -0.15625f;
        double maxY = (0.03125F * 11);
        for (int ci = 0; ci < pCount; ci++) {
            setRandSeed(ci * 8);
            float lifeTime = 10 + (nextFloat() * 15F);
            float offsetTime = time + (nextFloat() * lifeTime);
            float age = (offsetTime % lifeTime) / lifeTime;
            age *= age;
            setRandSeed((int) (offsetTime / lifeTime) + (int) (nextFloat() * 128));

            double pos = -0.25 + (nextFloat() * 1.25);
            double y = minY + (pos * (maxY - minY));
            double z = 0.125f * (nextFloat() > 0.5 ? -1 : 1);

            if (pos < 0) {
                z *= 1D - pos / -0.25;
            }

            float scale;
            if (idle) {
                scale = 0.0078125f + ((0.5F - Math.abs(age - 0.5F)) * 0.03125F);
            } else {
                scale = 0.0078125f + (age * 0.03125F);
            }

            z *= 1D - age;
            double targetY = (age * gemHeight) - (gemHeight / 2);
            y = MathHelper.interpolate(y, targetY, age);

            float r = color.getRed();
            float g = color.getGreen();
            float b = color.getBlue();
            drawParticle(builder, 0, y, z, scale, r / 255F, g / 255F, b / 255F, 1F);
        }
    }
}
