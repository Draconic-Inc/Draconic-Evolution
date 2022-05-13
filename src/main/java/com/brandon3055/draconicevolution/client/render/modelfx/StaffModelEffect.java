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

    public StaffModelEffect() {}

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    private int lastProfile = 0;

    @Override
    protected void doRender(VertexConsumer builder, float partialTicks, TechLevel techLevel) {
//        long profileTime = System.nanoTime();


        boolean idle = false;
        float time = TimeKeeper.getClientTick() + partialTicks;
        double charge = 0.5 + (Math.sin(time / 10) / 2);
        int pCount = 4;//4 + (int)(charge * 60);


        Color color = techLevel == TechLevel.CHAOTIC ? new Color(0xFF2203) : new Color(0xFF4403);


//        drawCrystalParticles(builder, 8, time, 1F, idle);
        drawFeedInParticles(builder, 16, time, 1F, idle, color);


//        profileTime = System.nanoTime() - profileTime;
//        int tick = TimeKeeper.getClientTick();
//        if (tick % 20 == 0 && lastProfile != tick) {
////            lastProfile = tick;
////            //100us ish with just the core and 64 particles or 35us ish with 16 particles
////            //130-150us ish with 64 core particles and 64 feed particles
//            DraconicEvolution.LOGGER.info("Time: " + (profileTime / 1000D) + "us");
//        }
    }

    private void drawCrystalParticles(VertexConsumer builder, int pCount, float time, float scaleMod, boolean idle) {
        setRandSeed((int) time / 30);
        double gemHeight = 0.4;
        int sCount = 6;
        for (int ci = 0; ci < pCount; ci++) {
            //Max life span of the particle
            float lifeTime = idle ? 15 + (nextFloat() * 15F) : 15;//8F + (nextFloat() * 12F);
            //Current age of the particle
//            float age = ((time + ((ci / (float)pCount) * lifeTime)) % lifeTime) / lifeTime;
            float age = ((time + (nextFloat() * lifeTime)) % lifeTime) / lifeTime;
            float arcPos = (age - 0.5F) * 2F;

            //Random rotational position around the crystal
            double angle = ((Math.PI * 2) / sCount) * (ci % sCount); //Math.PI * 2 == full circle in radians
            //Adds a random(ish) time based offset to the rotation and animates rotation around the gem ar a random speed.
//            angle += ((int)(time / lifeTime) * 3055.3055) + (age * (0.1 + nextFloat()) * 6);
            angle += age * 5;

            //Cosign arc used in particle positioning.
            double cosArc = Math.cos(arcPos * (Math.PI / 2));
            double scaleCurve = Math.max(cosArc, idle ? 0 : Math.tanh((arcPos - 0.5) * (Math.PI / 2)));
            double scale = (0.015625 + (nextFloat() * 0.03125)) * scaleCurve * scaleMod;
//            double scale = (0.015625 + (0.75 * 0.03125)) * scaleCurve;
//            scale *= 0.25 + (noise(time / (lifeTime / 4)) * 1.75);
            double diameter = 0.0625 * cosArc;
            double x = Math.sin(angle) * diameter;
            double z = Math.cos(angle) * diameter;
            double y = (age * gemHeight) - (gemHeight / 2);


            float flicker = noise((time / lifeTime) * 5);
//            Color color = Color.getHSBColor(0F + (flicker * 60F) / 630F, 1F, 1F);

//            Color color = new Color(0xFF7500);
//            Color color = new Color(0xFAC000);
//            Color color = new Color(0xFF2203);

            Color color = new Color(0xFF2203); //Red / Chaos Colour
//            Color color = new Color(0xFF4403); //Orange / Fire
//            Color color = new Color(0x0044FF); //Blue
//            Color color = new Color(0x6300BD); //Purple
//            Color color = new Color(0xFFBA01); //Yellow

            float r = color.getRed();
            float g = color.getGreen();
            float b = color.getBlue();
            drawParticle(builder, x, y, z, scale, r / 255F, g / 255F, b / 255F, 1F);
        }
    }

    private void drawFeedInParticles(VertexConsumer builder, int pCount, float time, float scaleMod, boolean idle, Color color) {
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
//            z = 0;//*= 1D - age; //Even Target Dist
            double targetY = (age * gemHeight) - (gemHeight / 2);
            y = MathHelper.interpolate(y, targetY, age);

//            Color color = new Color(0xFF2203);
            float r = color.getRed();
            float g = color.getGreen();
            float b = color.getBlue();
            drawParticle(builder, 0, y, z, scale, r / 255F, g / 255F, b / 255F, 1F);
        }
    }
}


//    private void drawCrystalParticlesRandom(IVertexBuilder builder, int pCount, float time) {
//        double gemHeight = 0.4;
//        for (int ci = 0; ci < pCount; ci++) {
//            //Random rotational position around the crystal
//            double angle = nextFloat() * Math.PI * 2; //Math.PI * 2 == full circle in radians
//            //Max life span of the particle
//            float lifeTime = 8F + (nextFloat() * 12F);
//            //Current age of the particle
//            float age = (time % lifeTime) / lifeTime;
//            float arcPos = (age - 0.5F) * 2F;
//            //Adds a random(ish) time based offset to the rotation and animates rotation around the gem ar a random speed.
//            angle += ((int)(time / lifeTime) * 3055.3055) + (age * (0.1 + nextFloat()) * 6);
//
//            //Cosign arc used in particle positioning.
//            double cosArc = Math.cos(arcPos * (Math.PI / 2));
//            double scaleCurve = Math.max(cosArc, Math.tanh((arcPos - 0.5) * (Math.PI / 2)));
//            double scale = (0.015625 + (nextFloat() * 0.03125)) * scaleCurve;
////            scale *= 0.25 + (noise(time / (lifeTime / 4)) * 1.75);
//            double diameter = 0.0625 * cosArc;
//            double x = Math.sin(angle) * diameter;
//            double z = Math.cos(angle) * diameter;
//            double y = (age * gemHeight) - (gemHeight / 2);
//
//
//            float flicker = noise((time / lifeTime) * 5);
////            Color color = Color.getHSBColor(0F + (flicker * 60F) / 630F, 1F, 1F);
//
////            Color color = new Color(0xFF7500);
////            Color color = new Color(0xFAC000);
//            Color color = new Color(0xB62203);
////            Color color = new Color(0xFF2203);
//
//            float r = color.getRed();
//            float g = color.getGreen();
//            float b = color.getBlue();
//
//
//
//            drawParticle(builder, x, y, z, scale, r/255F, g/255F, b/255F, 1F);
//        }
//    }