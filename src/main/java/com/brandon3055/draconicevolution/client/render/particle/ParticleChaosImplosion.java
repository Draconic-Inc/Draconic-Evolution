package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Scale;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Created by brandon3055 on 6/07/2016.
 */
public class ParticleChaosImplosion extends BCParticle {
    private static CCModel model = null;
    private final Vec3D target;
    public double size = 0;
    public boolean isOrigin = false;
    public boolean isTracer = true;
    public boolean contract = false;
    public boolean explosion = false;

    public ParticleChaosImplosion(World worldIn, Vec3D pos, Vec3D target) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.texturesPerRow = 8F;
        this.target = target;

        this.motionX = this.motionY = this.motionZ = 0;

        if (model == null) {
            Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/reactor_core_model.obj"));
            model = CCModel.combine(map.values());
            model.apply(new Scale(1, 0.5, 1));
        }
    }

    @Override
    public boolean isRawGLParticle() {
        return true;
    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }
//
//    @Override
//    public void onUpdate() {
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//        particleAge++;
//
//        if (isOrigin) {
//            if (particleAge % 5 == 0) {
//
//                BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, new Vec3D(posX, posY, posZ), target, 512D, contract ? 4 : 3);
//            }
//            if (particleAge > 20) {
//                setExpired();
//            }
//        }
//        else if (isTracer) {
//            double dist = Utils.getDistanceAtoB(target, new Vec3D(posX, posY, posZ));
//            if (particleAge > 200 || dist < 0.1) {
//                setExpired();
//            }
//
//            Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), target);
//            double speed = 0.5D;
//            motionX = dir.x * speed;
//            motionY = dir.y * speed;
//            motionZ = dir.z * speed;
//
//            moveEntityNoClip(motionX, motionY, motionZ);
//        }
//        else if (explosion) {
//            int max = 1000;
//            if (particleAge > max) {
//                setExpired();
//            }
//
//            particleAlpha = 0.4F;
//
//            float fadeOut = 400F;
//            if (particleAge > max - fadeOut) {
//                particleAlpha *= 1F - (particleAge - fadeOut) / fadeOut;
//            }
//
//            size++;
//        }
//        else {
//            if (size > 50) {
//                setExpired();
//            }
//            particleAlpha = contract ? (((float) size / 50F) * 0.5F) : 0.5F - (((float) size / 50F) * 0.5F);
//            size += 1;
//        }
//    }
//
//    @Override
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        CCRenderState ccrs = CCRenderState.instance();
//        if (isTracer) {
//            ResourceHelperDE.bindTexture(DEParticles.DE_SHEET);
//            vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//            super.renderParticle(vertexbuffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
//            ccrs.draw();
//            return;
//        }
//
//        GlStateManager.pushMatrix();
//        GlStateManager.disableCull();
//        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
//        GlStateManager.color(particleRed, particleGreen, particleBlue, particleAlpha);
//        GlStateManager.disableTexture2D();
//        GlStateManager.depthMask(false);
//
//        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//
//        if (!explosion && !isOrigin) {
//
//            double scale = (contract ? 50D - (size + partialTicks) : size + partialTicks) * 8D;
//            GlStateManager.translate((double) xx + 0.5, (double) yy + 0.5, (double) zz + 0.5);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            Matrix4 mat = RenderUtils.getMatrix(new Vector3(0, 0, 0), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 40F, 0, 1, 0), -1 * scale);
//            model.render(ccrs, mat);
//            ccrs.draw();
//        }
//        else if (explosion) {
//            double baseScale = size + partialTicks;
//
//            GlStateManager.translate((double) xx + 0.5, (double) yy + 0.5, (double) zz + 0.5);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//
//            for (int i = 40; i > 0; i--) {
//                double scale = baseScale / i * 4D;
//
//                Matrix4 mat = RenderUtils.getMatrix(new Vector3(0, 0, 0), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 40F, 0, 1, 0), -1 * scale);
//                model.render(ccrs, mat);
//
//            }
//
//            ccrs.draw();
//        }
//
//        GlStateManager.depthMask(true);
//        GlStateManager.enableTexture2D();
//        GlStateManager.enableCull();
//        GlStateManager.popMatrix();
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleChaosImplosion particle = new ParticleChaosImplosion(world, pos, speed);

            if (args.length > 0) {
                if (args[0] == 0) {              //0 Tracer
                    particle.isTracer = true;
                }
                else if (args[0] == 1) {         //1 Origin Expand
                    particle.isOrigin = true;
                    particle.isTracer = false;
                }
                else if (args[0] == 2) {         //2 Origin Contract
                    particle.isOrigin = true;
                    particle.contract = true;
                    particle.isTracer = false;
                }
                else if (args[0] == 3) {         //3 Expanding Wave
                    particle.isTracer = false;
                }
                else if (args[0] == 4) {         //4 Contracting Wave
                    particle.contract = true;
                    particle.isTracer = false;
                }
                else if (args[0] == 5) {         //5 The final boom!
                    particle.isTracer = false;
                    particle.explosion = true;
                }
            }

            return particle;
        }
    }
}
