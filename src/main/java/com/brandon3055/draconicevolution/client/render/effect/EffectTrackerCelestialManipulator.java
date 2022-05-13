package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCRenderState;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.Level;

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
    private Level world;
    private long boltSeed = 0;
    public boolean renderBolts = true;

    public float alpha = 0F;
    public float scale = 1F;
    public float red = 0F;
    public float green = 1F;
    public float blue = 1F;

    public EffectTrackerCelestialManipulator(Level world, Vec3D pos, Vec3D effectFocus) {
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
        //TODO Particles
//        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, effectFocus), 128, true);

        rotationSpeed = -1F;
        rotation += rotationSpeed;
    }

    public void renderEffect(Tesselator tessellator, float partialTicks) {
        BufferBuilder vertexbuffer = tessellator.getBuilder();
        CCRenderState ccrs = CCRenderState.instance();
        //region Icosahedron

//        float relativeX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks - interpPosX);
//        float relativeY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks - interpPosY);
//        float relativeZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks - interpPosZ);
//        float correctX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks);
//        float correctY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks);
//        float correctZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks);
//
//        RenderSystem.pushMatrix();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(red, green, blue, alpha);
//        RenderSystem.translatef(relativeX, relativeY, relativeZ);
//        RenderSystem.rotatef(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
//        RenderSystem.translatef(-relativeX, -relativeY, -relativeZ);
//        ccrs.reset();
//        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX, vertexbuffer);
//        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * scale);
//        ccrs.bind(vertexbuffer);
//        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
//        tessellator.end();
//        RenderSystem.popMatrix();
//        RenderSystem.color4f(1F, 1F, 1F, 1F);
//
//        //endregion
//
//        RenderSystem.pushMatrix();
//        RenderSystem.translatef(relativeX, relativeY, relativeZ);
//
//        int segments = Math.max(4, (int) (8 * scale));
//        if (renderBolt > 0 && scale > 0 && renderBolts) {
//            RenderEnergyBolt.renderBoltBetween(new Vec3D(), effectFocus.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
//        }
//
//        if (linkPos != null && scale > 0) {
//            RenderEnergyBolt.renderBoltBetween(new Vec3D(), linkPos.copy().subtract(correctX, correctY, correctZ), 0.05 * scale, scale * 0.5, segments, boltSeed, false);
//        }
//
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.disableLighting();
//        RenderSystem.popMatrix();
    }
}
