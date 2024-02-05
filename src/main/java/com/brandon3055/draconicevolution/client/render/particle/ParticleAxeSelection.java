package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 25/08/2016.
 */
public class ParticleAxeSelection extends BCParticle {

//    private IBakedModel model;
//    private BlockState state;

    public ParticleAxeSelection(ClientLevel worldIn, Vec3 pos) {
        super(worldIn, pos);
//        this.particleMaxAge = 50;
//        state = worldIn.getBlockState(pos.getPos());
//        if (state.getBlock() == Blocks.AIR) {
//            setExpired();
//        }
//        model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
//        particleScale = 0.2F;
    }


//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }
//
//    @Override
//    public void onUpdate() {
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (particleAge++ > particleMaxAge) {
//            setExpired();
//        }
//
//        float modifier = (float) particleAge / (float) particleMaxAge;
//
//        particleScale = 0.2F + (modifier * 0.8F);
//        particleAlpha = 1F - modifier;
//
//        if (particleAlpha < 0) {
//            particleAlpha = 0;
//        }
//
//    }
//
//    @Override
//    //@OnlyIn(Dist.CLIENT)
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        if (isExpired) {
//            return;
//        }
//
//        Tessellator.getInstance().draw();
//
//        RenderSystem.pushMatrix();
//        TextureUtils.bindBlockTexture();
////        RenderSystem.disableCull();
////        RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
////        RenderSystem.color(particleRed, particleGreen, particleBlue, particleAlpha);
////        RenderSystem.disableTexture2D();
////        RenderSystem.depthMask(false);
//        RenderSystem.disableDepth();
//
//        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//        RenderSystem.translate((double) xx + 0.5, (double) yy + 0.5, (double) zz + 0.5);
//        RenderSystem.scale(particleScale, particleScale, particleScale);
//        RenderSystem.translate(-0.5, -0.5, -0.5);
//
//        for (Direction facing : Direction.values()) ModelUtils.renderQuadsARGB(model.getQuads(state, facing, 0), new ColourARGB((int) (particleAlpha * 255), 255, 255, 255).argb());
//
////        RenderSystem.depthMask(true);
//        RenderSystem.enableDepth();
////        RenderSystem.enableTexture2D();
////        RenderSystem.enableCull();
//        RenderSystem.popMatrix();
//
//        vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, Level world, Vec3 pos, Vec3 speed, int... args) {
            ParticleAxeSelection particle = new ParticleAxeSelection((ClientLevel)world, pos);
            return particle;
        }
    }
}
