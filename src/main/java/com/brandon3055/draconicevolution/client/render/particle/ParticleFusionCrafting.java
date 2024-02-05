package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class ParticleFusionCrafting extends BCParticle {

    private final Vec3 corePos;
    private final IFusionInventory craftingInventory;
    private boolean renderBolt = false;
    private float rotation;
    private float rotationSpeed = 1;
    private boolean circleDir = false;
    private float circlePos = 0;
    private float circleSpeed = 0;
    private float rotYAngle = 0;
    private float aRandomFloat = 0;
    private boolean rotationLock = false;

    public ParticleFusionCrafting(ClientLevel worldIn, Vec3 pos, Vec3 corePos, IFusionInventory craftingInventory) {
        super(worldIn, pos, new Vec3(0, 0, 0));
        this.corePos = corePos;
        this.craftingInventory = craftingInventory;
        this.alpha = 0;
        this.rotation = random.nextInt(1000);
        this.xd = this.yd = this.zd = 0;
        this.circlePos = random.nextFloat() * 1000F;
        this.circleDir = random.nextBoolean();
        this.aRandomFloat = random.nextFloat();
        this.rotYAngle = random.nextFloat() * 1000;
//        this.particleScale = 1F;
    }

//    @Override
//    public void onUpdate() {
//        if (particleAge++ > 20 && (craftingInventory == null || !craftingInventory.craftingInProgress() || ((TileEntity) craftingInventory).isInvalid())) {
//            for (int i = 0; i < 10; i++) {
//                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, new Vec3D(posX, posY, posZ)));
//            }
//            setExpired();
//            return;
//        }
//
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//
//        //region Movement
//        if (craftingInventory.getCraftingStage() > 1000) {
//            double distFromCore = 1.2;
//
//            if (craftingInventory.getCraftingStage() > 1600) {
//                distFromCore *= 1D - (craftingInventory.getCraftingStage() - 1600) / 400D;
//            }
//
//            particleScale = 0.7F + ((float) (distFromCore / 1.2D) * 0.3F);
//            particleGreen = particleBlue = (float) (distFromCore - 0.2);
//            particleRed = 1F - (float) (distFromCore - 0.2);
//
//            double targetX = corePos.x + 0.5 + (Math.cos(circlePos) * distFromCore);
//            double targetZ = corePos.z + 0.5 + (Math.sin(circlePos) * distFromCore);
//            double targetY = corePos.y + 0.5;// + (Math.cos(circlePos + rotYAngle) * 0.7 * distFromCore);
//
//            double distance = Utils.getDistanceAtoB(targetX, targetY, targetZ, posX, posY, posZ);
//
//            if (distance > 0.1 && !rotationLock) {
//                Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), new Vec3D(targetX, targetY, targetZ));
//                double speed = 0.1D + (aRandomFloat * 0.1D);
//                dir.multiply(speed, speed, speed);
//                moveEntityNoClip(dir.x, dir.y, dir.z);
//            }
//            else {
//                float rotSpeed = (0.6F * ((craftingInventory.getCraftingStage() - 1000) / 1000F)) + (1.2F - (float) distFromCore);
//                rotationLock = true;
//                if (circleDir) {
//                    circleSpeed = rotSpeed;
//                }
//                else if (!circleDir) {
//                    circleSpeed = -rotSpeed;
//                }
//
//                setPosition(targetX, targetY, targetZ);
//                circlePos += circleSpeed;
//            }
//        }
//
//        //endregion
//
//        //region Render Logic
//
//        int chance = 22 - (int) ((craftingInventory.getCraftingStage() / 2000D) * 22);
//        if (chance < 1) {
//            chance = 1;
//        }
//
//        if (rand.nextInt(chance) == 0) {
//            BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, new Vec3D(posX, posY, posZ)));
//        }
//
//        renderBolt = rand.nextInt(chance * 2) == 0;
//        if (renderBolt) {
//            Vec3D pos = corePos.copy().add(0.5, 0.5, 0.5);
//            BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new SubParticle(world, pos));
//        }
//
//        particleAlpha = craftingInventory.getCraftingStage() / 1000F;
//        rotationSpeed = 1 + (craftingInventory.getCraftingStage() / 1000F) * 10;
//        if (particleAlpha > 1) {
//            particleAlpha = 1;
//        }
//
//        rotation += rotationSpeed;
//
//        //endregion
//    }
//
//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }
//
//    @Override
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.draw(); //End Draw
//        //region Icosahedron
//
//        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//        float correctX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
//        float correctY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);
//        float correctZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks);
//
//        RenderSystem.pushMatrix();
//        RenderSystem.enableBlend();
//        RenderSystem.color(particleRed, particleGreen, particleBlue, particleAlpha);
//        // RenderSystem.color(1, 0, 0, 1);
//        RenderSystem.translate(x, y, z);
//
//        RenderSystem.rotate(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
//        //RenderSystem.rotate((float)Math.sin((ROTATION + partialTicks) * rotationSpeed / 100F) * 20F, 1F, 0F, 0F);
//        RenderSystem.translate(-x, -y, -z);
//
//        ccrs.reset();
//        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL, vertexbuffer);
//        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x, y, z), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * particleScale);
//        ccrs.bind(vertexbuffer);
//        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
//        ccrs.draw();
//
//        RenderSystem.popMatrix();
//        RenderSystem.color(1F, 1F, 1F, 1F);
//
//        //endregion
//
//        RenderSystem.pushMatrix();
//        RenderSystem.translate(x, y, z);
//
//        if (renderBolt) {
//            renderBolt = false;
//            RenderEnergyBolt.renderBoltBetween(new Vec3D(), corePos.copy().subtract(correctX - 0.5, correctY - 0.5, correctZ - 0.5), 0.05, 1, 10, rand.nextLong(), true);
//        }
//
////        if (rand.nextInt(1) == 0 || true){
////            rand.setSeed(1);
////            Vec3D t = new Vec3D(-0.5 + rand.nextDouble(), -0.5 + rand.nextDouble(), -0.5 + rand.nextDouble());
////            double l = 1;
////            t.multiply(l, l, l);
////            RenderEnergyBolt.renderCorona(new Vec3D(), t, 0.01, 0.2, 4, world.rand.nextLong());
////        }
//
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.disableLighting();
//        RenderSystem.popMatrix();
//
//        //Restore Draw State
//        vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//    }

    public class SubParticle extends BCParticle {

        public SubParticle(ClientLevel worldIn, Vec3 pos) {
            super(worldIn, pos);

            double speed = 0.1;
            this.xd = (-0.5 + random.nextDouble()) * speed;
            this.yd = (-0.5 + random.nextDouble()) * speed;
            this.zd = (-0.5 + random.nextDouble()) * speed;

//            this.particleMaxAge = 10 + rand.nextInt(10);
//            this.particleScale = 1F;
//            this.particleTextureIndexY = 1;

            this.rCol = 0;
        }

//        @Override
//        public boolean shouldDisableDepth() {
//            return true;
//        }
//
//        @Override
//        public void onUpdate() {
//            this.prevPosX = this.posX;
//            this.prevPosY = this.posY;
//            this.prevPosZ = this.posZ;
//
//            particleTextureIndexX = rand.nextInt(5);
//            int ttd = particleMaxAge - particleAge;
//            if (ttd < 10) {
//                particleScale = ttd / 10F;
//            }
//
//            moveEntityNoClip(motionX, motionY, motionZ);
//
//            if (particleAge++ > particleMaxAge) {
//                setExpired();
//            }
//        }
//
//        @Override
//        public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//            float minU = (float) this.particleTextureIndexX / 8.0F;
//            float maxU = minU + 0.125F;
//            float minV = (float) this.particleTextureIndexY / 8.0F;
//            float maxV = minV + 0.125F;
//            float scale = 0.1F * this.particleScale;
//
//            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//            float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//            int brightnessForRender = this.getBrightnessForRender(partialTicks);
//            int j = brightnessForRender >> 16 & 65535;
//            int k = brightnessForRender & 65535;
//            vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//            vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//            vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//            vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        }
    }
}
