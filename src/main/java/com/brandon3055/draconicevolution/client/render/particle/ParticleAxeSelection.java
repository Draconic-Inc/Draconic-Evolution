package com.brandon3055.draconicevolution.client.render.particle;

import codechicken.lib.colour.ColourARGB;
import codechicken.lib.texture.TextureUtils;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.ModelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 25/08/2016.
 */
public class ParticleAxeSelection extends BCParticle {

    private IBakedModel model;
    private IBlockState state;

    public ParticleAxeSelection(World worldIn, Vec3D pos) {
        super(worldIn, pos);
        this.particleMaxAge = 50;
        state = worldIn.getBlockState(pos.getPos());
        if (state.getBlock() == Blocks.AIR) {
            setExpired();
        }
        model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        particleScale = 0.2F;
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

        if (particleAge++ > particleMaxAge) {
            setExpired();
        }

        float modifier = (float) particleAge / (float) particleMaxAge;

        particleScale = 0.2F + (modifier * 0.8F);
        particleAlpha = 1F - modifier;

        if (particleAlpha < 0) {
            particleAlpha = 0;
        }

    }

    @Override
    //@SideOnly(Side.CLIENT)
    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (isExpired) {
            return;
        }

        Tessellator.getInstance().draw();

        GlStateManager.pushMatrix();
        TextureUtils.bindBlockTexture();
//        GlStateManager.disableCull();
//        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
//        GlStateManager.color(particleRed, particleGreen, particleBlue, particleAlpha);
//        GlStateManager.disableTexture2D();
//        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        GlStateManager.translate((double) xx + 0.5, (double) yy + 0.5, (double) zz + 0.5);
        GlStateManager.scale(particleScale, particleScale, particleScale);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        for (EnumFacing facing : EnumFacing.VALUES) ModelUtils.renderQuadsARGB(model.getQuads(state, facing, 0), new ColourARGB((int) (particleAlpha * 255), 255, 255, 255).argb());

//        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
//        GlStateManager.enableTexture2D();
//        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleAxeSelection particle = new ParticleAxeSelection(world, pos);
            return particle;
        }
    }
}
