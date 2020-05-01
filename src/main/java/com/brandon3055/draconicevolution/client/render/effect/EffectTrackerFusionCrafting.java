package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class EffectTrackerFusionCrafting {
    public static double interpPosX = 0;
    public static double interpPosY = 0;
    public static double interpPosZ = 0;
    private Random rand = new Random();
    private final Vec3D corePos;
    public final IFusionCraftingInventory craftingInventory;
    private final int effectCount;
    private int renderBolt = 0;
    private float rotation;
    private float rotationSpeed = 1;
    private float aRandomFloat = 0;
    public boolean positionLocked = false;
    public Vec3D startPos;
    public Vec3D pos;
    public Vec3D prevPos = new Vec3D();
    public Vec3D circlePosition = new Vec3D();
    private World world;
    private long boltSeed = 0;

    public float alpha = 0F;
    public float scale = 1F;
    public float red = 0F;
    public float green = 1F;
    public float blue = 1F;

    public EffectTrackerFusionCrafting(World world, Vec3D pos, Vec3D corePos, IFusionCraftingInventory craftingInventory, int effectCount) {
        this.world = world;
        this.corePos = corePos;
        this.craftingInventory = craftingInventory;
        this.effectCount = effectCount;
        this.rotation = rand.nextInt(1000);
        this.aRandomFloat = rand.nextFloat();
        this.pos = pos.copy();
        this.startPos = pos.copy();
        this.prevPos.set(pos);
    }

    public void onUpdate(boolean isMoving) {
        prevPos.set(pos);

        //region Movement
        if (isMoving) {
            if (pos.equals(startPos)) {
                world.playSound(pos.x, pos.y, pos.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 0.5F, 0.5F, false);
            }

            double distance = Utils.getDistanceAtoB(circlePosition, pos);
            if (distance > 0.1 && !positionLocked) {
                if (scale > 1) {
                    scale -= 0.05F;
                }

                Vec3D dir = Vec3D.getDirectionVec(pos, circlePosition);
                double speed = 0.1D + (aRandomFloat * 0.1D);
                dir.multiply(speed, speed, speed);
                pos.add(dir.x, dir.y, dir.z);
            }
            else {
                if (!positionLocked) {
                    world.playSound(pos.x, pos.y, pos.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 2F, 0.5F, false);
                }
                positionLocked = true;
                pos.set(circlePosition);
            }
        }
        else {
            scale = 1.5F;
        }

        //endregion

        //region Render Logic

        int chance = 22 - (int) ((craftingInventory.getCraftingStage() / 2000D) * 22);
        if (chance < 1) {
            chance = 1;
        }

        if (rand.nextInt(chance) == 0) {
            //TODO Particle stuff
//            BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, pos));
        }

        if (renderBolt > 0) {
            renderBolt--;
        }

        if (rand.nextInt((chance * 2) + (int) (effectCount * 1.5)) == 0) {
            renderBolt = 1;
            boltSeed = rand.nextLong();
            Vec3D pos = corePos.copy().add(0.5, 0.5, 0.5);
//            BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, pos));
            world.playSound(pos.x, pos.y, pos.z, DESoundHandler.energyBolt, SoundCategory.BLOCKS, 1F, 0.9F + rand.nextFloat() * 0.2F, false);
        }

        if (craftingInventory.getCraftingStage() < 1000) {
            TileEntity tile = world.getTileEntity(pos.getPos());
            if (tile instanceof TileCraftingInjector && craftingInventory.getIngredientEnergyCost() > 0) {
                alpha = (float) (((TileCraftingInjector) tile).getInjectorCharge() / (double) craftingInventory.getIngredientEnergyCost());
            }
        }
        else {
            alpha = 1;
        }
//
//        alpha = craftingInventory.getCraftingStage() / 1000F;

        rotationSpeed = 1 + (craftingInventory.getCraftingStage() / 1000F) * 10;
        if (alpha > 1) {
            alpha = 1;
        }

        rotation += rotationSpeed;

        //endregion
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

        RenderSystem.pushMatrix();
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200, 200);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(red, green, blue, alpha);
        RenderSystem.translatef(relativeX, relativeY, relativeZ);
        RenderSystem.rotatef(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
        RenderSystem.translatef(-relativeX, -relativeY, -relativeZ);
        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, vertexbuffer); //Was pos tex normal
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * scale);
        ccrs.bind(vertexbuffer);
        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
        tessellator.draw();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        //endregion

        RenderSystem.pushMatrix();
        RenderSystem.translatef(relativeX, relativeY, relativeZ);

        if (renderBolt > 0) {
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), corePos.copy().subtract(correctX - 0.5, correctY - 0.5, correctZ - 0.5), 0.05, 1, 10, boltSeed, true);
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableLighting();
        RenderSystem.popMatrix();
    }

//    public static class SubParticle extends BCParticle {
//
//        public SubParticle(World worldIn, Vec3D pos) {
//            super(worldIn, pos);
//
//            double speed = 0.1;
//            this.motionX = (-0.5 + rand.nextDouble()) * speed;
//            this.motionY = (-0.5 + rand.nextDouble()) * speed;
//            this.motionZ = (-0.5 + rand.nextDouble()) * speed;
//
//            this.maxAge = 10 + rand.nextInt(10);
//            this.baseScale = 1F;
////            this.particleTextureIndexY = 1;
//
//            this.particleRed = 0;
//        }
//
////        @Override
////        public boolean shouldDisableDepth() {
////            return true;
////        }
//
//        @Override
//        public void tick() {
//            this.prevPosX = this.posX;
//            this.prevPosY = this.posY;
//            this.prevPosZ = this.posZ;
//
////            particleTextureIndexX = rand.nextInt(5);
//            int ttd = maxAge - age;
//            if (ttd < 10) {
//                baseScale = ttd / 10F;
//            }
//
//            moveEntityNoClip(motionX, motionY, motionZ);
//
//            if (age++ > maxAge) {
//                setExpired();
//            }
//        }
//
//
//        @Override
//        public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
////            float minU = (float) this.particleTextureIndexX / 8.0F;
////            float maxU = minU + 0.125F;
////            float minV = (float) this.particleTextureIndexY / 8.0F;
////            float maxV = minV + 0.125F;
////            float scale = 0.1F * this.particleScale;
////
////            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
////            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
////            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
////            int brightnessForRender = this.getBrightnessForRender(partialTicks);
////            int j = brightnessForRender >> 16 & 65535;
////            int k = brightnessForRender & 65535;
////            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
////            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        }
//    }
}
