package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.render.CCModel;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * Created by brandon3055 on 17/05/2017.
 */
public class ParticleArrowShockwave extends BCParticle {
    private static CCModel model = null;
    public double size = 0;
    public double maxSize;

    public ParticleArrowShockwave(ClientLevel worldIn, Vec3D pos) {
        super(worldIn, pos);
        if (model == null) {
//            Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/reactor_core_model.obj"));
//            model = CCModel.combine(map.values());
//            model.apply(new Scale(1, 0.5, 1));
        }
    }

    public ParticleArrowShockwave(ClientLevel worldIn, Vec3D pos, Vec3D speed) {
        this(worldIn, pos);
    }

//    @Override
//    public void onUpdate() {
//        if (size == 0) {
//            for (int i = 0; i < 100; i++) {
//                double rotation = rand.nextFloat() * Math.PI * 2;
//                float renderRadius = rand.nextFloat() * 7;
//                double ox = Math.sin(rotation) * renderRadius;
//                double oy = (rand.nextGaussian() - 0.3) * 2;
//                double oz = Math.cos(rotation) * renderRadius;
//                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + ox, posY + oy, posZ + oz, ox * 0.05, oy * 0.05, oz * 0.05);
//            }
//        }
//        particleAge++;
//        size += 0.8;
//        if (size > maxSize * 1.2) {
//            setExpired();
//        }
//
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//    }
//
//    @Override
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.draw();
//
//        RenderSystem.pushMatrix();
//        RenderSystem.disableCull();
//        RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
//        float a = (float) Math.max(0D, 0.5D - (((size + partialTicks) / (maxSize)) * 0.5D));
//        RenderSystem.color(1F, 0.1F, 0F, a);
//        RenderSystem.disableTexture2D();
//        RenderSystem.depthMask(false);
//
//        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//
//
//        double baseScale = size + partialTicks;
//
//        RenderSystem.translate((double) xx + 0.5, (double) yy + 0.5, (double) zz + 0.5);
//
//        for (int i = 10; i > 0; i--) {
//            double scale = baseScale / i * 2D;
//
//            RenderSystem.color(1F - (i / 5F), 0.1F, i / 8F, a);
//
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            Matrix4 mat = RenderUtils.getMatrix(new Vector3(0, 0, 0), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 40F, 0, 1, 0), -1 * scale);
//            model.render(ccrs, mat);
//            ccrs.draw();
//
//        }
//
//
//        RenderSystem.depthMask(true);
//        RenderSystem.enableTexture2D();
//        RenderSystem.enableCull();
//        RenderSystem.popMatrix();
//
//        vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, Level world, Vec3D pos, Vec3D speed, int... args) {
            ParticleArrowShockwave arrowShockwave = new ParticleArrowShockwave((ClientLevel) world, pos, speed);

            world.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10, 0.9F + world.random.nextFloat() * 0.2F, false);
            if (args.length >= 1) {
                arrowShockwave.maxSize = args[0] / 100D;
            } else {
                arrowShockwave.maxSize = 10;
            }

            return arrowShockwave;
        }
    }
}
