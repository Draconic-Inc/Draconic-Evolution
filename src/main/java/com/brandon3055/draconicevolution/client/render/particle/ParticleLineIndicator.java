package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/5/2016.
 * The particle used to render the beams on the Energy Core
 */
public class ParticleLineIndicator extends SpriteTexturedParticle {
    public static final IParticleRenderType PARTICLE_NO_DEPTH_NO_LIGHT = new IParticleRenderType() {
        public void begin(BufferBuilder builder, TextureManager manager) {
            RenderSystem.depthMask(false);
            manager.bind(AtlasTexture.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(516, 0.003921569F);
            builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        public void end(Tessellator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "PARTICLE_NO_DEPTH_NO_LIGHT";
        }
    };

    public ParticleLineIndicator(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super((ClientWorld)worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.xd = xSpeedIn;
        this.yd = ySpeedIn;
        this.zd = zSpeedIn;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return PARTICLE_NO_DEPTH_NO_LIGHT;
    }

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle createParticle(IntParticleType.IntParticleData data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleLineIndicator particle = new ParticleLineIndicator(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);

            if (data.get().length >= 3) {
                particle.setColor(data.get()[0] / 255F, data.get()[1] / 255F, data.get()[2] / 255F);
            }
            if (data.get().length >= 4) {
                particle.setLifetime(data.get()[3]);
            }

            return particle;
        }
    }
}
