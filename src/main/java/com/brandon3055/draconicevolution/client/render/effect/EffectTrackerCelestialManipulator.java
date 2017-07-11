package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class EffectTrackerCelestialManipulator {
    public static double interpPosX = 0;
    public static double interpPosY = 0;
    public static double interpPosZ = 0;
    private Random rand = new Random();
    public Vec3D effectFocus;
    public Vec3D linkPos = null;
    private int renderBolt = 0;
    private float rotation;
    private float rotationSpeed = 2;
    private float aRandomFloat = 0;
    public boolean positionLocked = false;
    public Vec3D startPos;
    public Vec3D pos;
    public Vec3D prevPos = new Vec3D();
    public Vec3D circlePosition = new Vec3D();
    private World world;
    private long boltSeed = 0;
    public boolean renderBolts = true;

    public float alpha = 0F;
    public float scale = 1F;
    public float red = 0F;
    public float green = 1F;
    public float blue = 1F;

    public EffectTrackerCelestialManipulator(World world, Vec3D pos, Vec3D effectFocus) {
        this.world = world;
        this.effectFocus = effectFocus;
        this.rotation = rand.nextInt(1000);
        this.aRandomFloat = rand.nextFloat();
        this.pos = pos.copy();
        this.startPos = pos.copy();
        this.prevPos.set(pos);
        red = 0.1F;
        green = 0.1F;
        alpha = 1F;
    }

    public void onUpdate() {
        prevPos.set(pos);

        if (renderBolt > 0) {
            renderBolt--;
        }

        renderBolt = 1;
        boltSeed = rand.nextLong();
        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, effectFocus), 128, true);

        rotationSpeed = -1F;
        rotation += rotationSpeed;
    }

    public void renderEffect(Tessellator tessellator, float partialTicks) {
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        CCRenderState ccrs = CCRenderState.instance();
        //region Icosahedron

        float relativeX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks - interpPosX);
        float relativeY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks - interpPosY);
        float relativeZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks - interpPosZ);
        float correctX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks);
        float correctY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks);
        float correctZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks);

        GlStateManager.pushMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.translate(relativeX, relativeY, relativeZ);
        GlStateManager.rotate(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
        GlStateManager.translate(-relativeX, -relativeY, -relativeZ);
        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL, vertexbuffer);
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * scale);
        ccrs.bind(vertexbuffer);
        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);

        //endregion

        GlStateManager.pushMatrix();
        GlStateManager.translate(relativeX, relativeY, relativeZ);

        int segments = Math.max(4, (int) (8 * scale));
        if (renderBolt > 0 && scale > 0 && renderBolts) {
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), effectFocus.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
        }

        if (linkPos != null && scale > 0) {
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), linkPos.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    public static class SubParticle extends BCParticle {

        public SubParticle(World worldIn, Vec3D pos) {
            super(worldIn, pos);

            double speed = 0.1;
            this.motionX = (-0.5 + rand.nextDouble()) * speed;
            this.motionY = (-0.5 + rand.nextDouble()) * speed;
            this.motionZ = (-0.5 + rand.nextDouble()) * speed;

            this.particleMaxAge = 10 + rand.nextInt(10);
            this.particleScale = 1F;
            this.particleTextureIndexY = 1;

            this.particleRed = 0;
        }

        @Override
        public BCParticle setScale(float scale) {
            super.setScale(scale);

            double speed = 0.1 * scale;
            this.motionX = (-0.5 + rand.nextDouble()) * speed;
            this.motionY = (-0.5 + rand.nextDouble()) * speed;
            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
            return this;
        }

        @Override
        public boolean shouldDisableDepth() {
            return true;
        }

        @Override
        public void onUpdate() {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            particleTextureIndexX = rand.nextInt(5);
            int ttd = particleMaxAge - particleAge;
            if (ttd < 10) {
                particleScale = ttd / 10F;
            }

            moveEntityNoClip(motionX, motionY, motionZ);

            if (particleAge++ > particleMaxAge) {
                setExpired();
            }
        }

        @Override
        public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            if (particleAge == 0) {
                return;
            }
            float minU = (float) this.particleTextureIndexX / 8.0F;
            float maxU = minU + 0.125F;
            float minV = (float) this.particleTextureIndexY / 8.0F;
            float maxV = minV + 0.125F;
            float scale = 0.1F * this.particleScale;

            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
            int brightnessForRender = this.getBrightnessForRender(partialTicks);
            int j = brightnessForRender >> 16 & 65535;
            int k = brightnessForRender & 65535;
            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }

    public static class SubParticle2 extends BCParticle {

        private final EffectTrackerCelestialManipulator target;
        public boolean targetMode = false;

        public SubParticle2(World worldIn, Vec3D pos, EffectTrackerCelestialManipulator target) {
            super(worldIn, pos);
            this.target = target;

            double speed = 1;
            this.motionX = (-0.5 + rand.nextDouble()) * speed;
            this.motionY = (-0.5 + rand.nextDouble()) * speed;
            this.motionZ = (-0.5 + rand.nextDouble()) * speed;

            this.particleMaxAge = 150 + rand.nextInt(10);
            this.particleScale = 1F;
            this.particleTextureIndexY = 1;

            this.particleRed = 0;
        }

        @Override
        public BCParticle setScale(float scale) {
            super.setScale(scale);

            double speed = 1;
            this.motionX = (-0.5 + rand.nextDouble()) * speed;
            this.motionY = (-0.5 + rand.nextDouble()) * speed;
            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
            return this;
        }

        @Override
        public boolean shouldDisableDepth() {
            return true;
        }

        @Override
        public void onUpdate() {
//            float b = 1F - (world.getSunBrightness(0) - 0.2F) * 1.2F;
//            particleGreen = particleBlue = b;

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            Vec3D thisPos = new Vec3D(posX, posY, posZ);
            Vec3D dir = Vec3D.getDirectionVec(thisPos, target.pos);
            double distance = Utils.getDistanceAtoB(thisPos, target.pos) * 0.8;
            double speed = 0.01 * distance;
            if (distance > 2 && rand.nextInt(90) == 0) {
                targetMode = true;
            }

            double dragModifier = 0.95 / distance;
            motionX *= dragModifier;
            motionY *= dragModifier;
            motionZ *= dragModifier;

            motionX += dir.x * speed;
            motionY += dir.y * speed;
            motionZ += dir.z * speed;

            if (targetMode) {
                motionX = dir.x * 0.1;
                motionY = dir.y * 0.1;
                motionZ = dir.z * 0.1;
            }

            particleTextureIndexX = rand.nextInt(5);
            int ttd = particleMaxAge - particleAge;
            if (ttd < 10) {
                particleScale = ttd / 10F;
            }

            moveEntityNoClip(motionX, motionY, motionZ);

            if (distance < 0.5) {
                particleAge += 4;
            }

            if (particleAge++ > particleMaxAge) {
                setExpired();
            }
        }

        @Override
        public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            if (particleAge == 0) {
                return;
            }
            float minU = (float) this.particleTextureIndexX / 8.0F;
            float maxU = minU + 0.125F;
            float minV = (float) this.particleTextureIndexY / 8.0F;
            float maxV = minV + 0.125F;
            float scale = 0.1F * this.particleScale;

            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
            int brightnessForRender = this.getBrightnessForRender(partialTicks);
            int j = brightnessForRender >> 16 & 65535;
            int k = brightnessForRender & 65535;
            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }
}
