package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.lib.References;

/**
 * Created by Brandon on 8/02/2015.
 */
public class ParticleEnergyBeam extends EntityFX {

    /**
     * Power flow integer Ranges from 0 to 100
     */
    private int flow;

    /**
     * Beam Target X
     */
    private double tX = 0.0D;
    /**
     * Beam Target Y
     */
    private double tY = 0.0D;
    /**
     * Beam Target Z
     */
    private double tZ = 0.0D;
    /**
     * Modified Target X
     */
    private boolean advanced;

    private boolean renderParticle = true;
    private float length = 0.0F;
    private float rotYaw = 0.0F;
    private float rotPitch = 0.0F;
    private float prevYaw = 0.0F;
    private float prevPitch = 0.0F;
    private EntityPlayer player;

    // todo make sure not dyrectly up or down on y axis, Set dead when player goes out of range, PaRTICLE eNGINE

    private static ResourceLocation beamTextureBasic = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/EnergyBeamBlue.png");
    private static ResourceLocation beamTextureAdvanced = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/EnergyBeamRed.png");

    /**
     * @param offsetMode 0 = no offset, 1 = offset target end, 2 = offset start end, 3 offset both ends
     */
    public ParticleEnergyBeam(World world, double x, double y, double z, double tX, double tY, double tZ, int maxAge,
            int flow, boolean advanced, int offsetMode) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = 1F;
        this.particleGreen = 1F;
        this.particleBlue = 1F;
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleMaxAge = maxAge;
        this.flow = flow;
        this.prevYaw = rotationYaw;
        this.prevPitch = rotPitch;
        this.setSize(0.2F, 0.2F);
        this.advanced = advanced;
        this.tX = tX;
        this.tY = tY;
        this.tZ = tZ;

        if (offsetMode > 0) {
            double dist = Utills.getDistanceAtoB(x, z, tX, tZ);
            if (dist == 0) dist = 0.1;
            double xDist = x - tX;
            double zDist = z - tZ;
            double xOff = xDist / dist;
            double zOff = zDist / dist;
            if (xOff == 0 && zOff == 0) xOff = 1;
            // LogHelper.info(xOff + " " + zOff);
            double offM = 0.4D;

            if (offsetMode == 2 || offsetMode == 3) setPosition(posX - xOff * offM, posY, posZ - zOff * offM);

            if (offsetMode == 1 || offsetMode == 3) {
                this.tX = tX + xOff * offM;
                this.tY = tY;
                this.tZ = tZ + zOff * offM;
            }
        }
    }

    public void update(int flow, boolean render) {
        this.renderParticle = render;
        for (this.flow = flow; this.particleMaxAge - this.particleAge < 4; ++this.particleMaxAge) {}
    }

    @Override
    public void onUpdate() {
        // 1.570797f
        // double sin = Math.sin(-1f + (float)(particleAge % 360) / 180f * 1.570797f);
        // LogHelper.info(sin + " " + particleAge % 360);

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        float xd = (float) (this.posX - this.tX);
        float yd = (float) (this.posY - this.tY);
        float zd = (float) (this.posZ - this.tZ);
        this.length = MathHelper.sqrt_float(xd * xd + yd * yd + zd * zd);
        double var7 = (double) MathHelper.sqrt_double((double) (xd * xd + zd * zd));
        this.rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / 3.141592653589793D);
        this.rotPitch = (float) (Math.atan2((double) yd, var7) * 180.0D / 3.141592653589793D);
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    private EntityPlayer getPlayer() {
        if (player == null) {
            player = Minecraft.getMinecraft().thePlayer;
        }
        return player;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTick, float rotX, float rotXZ, float rotZ,
            float rotYZ, float rotXY) {
        if (!renderParticle) return;
        tessellator.draw();
        GL11.glPushMatrix();
        // GL11.glPushAttrib(GL11.GL_ATTRIB_STACK_DEPTH);
        float var9 = 1.0F;
        float slide = (float) getPlayer().ticksExisted;
        float size = (float) flow / 100f * 2f; // 0.7F;
        if (advanced) Minecraft.getMinecraft().renderEngine.bindTexture(beamTextureAdvanced);
        else Minecraft.getMinecraft().renderEngine.bindTexture(beamTextureBasic);
        GL11.glTexParameterf(3553, 10242, 10497.0F);
        GL11.glTexParameterf(3553, 10243, 10497.0F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        float var11 = slide + partialTick;
        float var12 = -var11 * 0.2F - (float) MathHelper.floor_float(-var11 * 0.1F);
        GL11.glBlendFunc(770, 1);
        GL11.glDepthMask(false);
        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTick - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTick - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTick - interpPosZ);
        GL11.glTranslated((double) xx, (double) yy, (double) zz);
        float ry = (float) ((double) this.prevYaw + (double) (this.rotYaw - this.prevYaw) * (double) partialTick);
        float rp = (float) ((double) this.prevPitch + (double) (this.rotPitch - this.prevPitch) * (double) partialTick);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F + ry, 0.0F, 0.0F, -1.0F);
        GL11.glRotatef(rp, 1.0F, 0.0F, 0.0F);
        double var44 = -0.15D * (double) size;
        double var17 = 0.15D * (double) size;

        GL11.glTranslated(0.03, 0, 0);
        for (int t = 0; t < 2; ++t) {
            double var29 = (double) (this.length * var9);
            double var31 = 0D;
            double var33 = 1D;
            double var35 = (double) (-1.0F + var12 + (float) t / 3.0F);
            double var37 = (double) (this.length * var9) + var35;
            GL11.glRotatef(t * 90.0F, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.setBrightness(200);
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1f);
            tessellator.addVertexWithUV(var44, var29, 0.0D, var33, var37);
            tessellator.addVertexWithUV(var44, 0.0D, 0.0D, var33, var35);
            tessellator.addVertexWithUV(var17, 0.0D, 0.0D, var31, var35);
            tessellator.addVertexWithUV(var17, var29, 0.0D, var31, var37);
            tessellator.draw();
            GL11.glRotatef(t * 90.0F, 0.0F, -1.0F, 0.0F);
        }

        if (ClientEventHandler.playerHoldingWrench) {
            var44 = -0.15D * (double) 1;
            var17 = 0.15D * (double) 1;

            // GL11.glColor4f(1f, 1f, 1f, 1f);
            // GL11.glTranslated(0.1, 0, 0);
            for (int t = 0; t < 2; ++t) {
                double var29 = (double) (this.length * var9);
                double var31 = 0D;
                double var33 = 1D;
                double var35 = (double) (-1.0F + var12 + (float) t / 3.0F);
                double var37 = (double) (this.length * var9) + var35;
                GL11.glRotatef(t * 90.0F, 0.0F, 1.0F, 0.0F);
                tessellator.startDrawingQuads();
                tessellator.setBrightness(200);
                tessellator.setColorRGBA_F(0f, 1f, 0f, 1f);
                tessellator.addVertexWithUV(var44, var29, 0.0D, var33, var37);
                tessellator.addVertexWithUV(var44, 0.0D, 0.0D, var33, var35);
                tessellator.addVertexWithUV(var17, 0.0D, 0.0D, var31, var35);
                tessellator.addVertexWithUV(var17, var29, 0.0D, var31, var37);
                tessellator.draw();
                GL11.glRotatef(t * 90.0F, 0.0F, -1.0F, 0.0F);
            }
        }

        // GL11.glDepthMask(true);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // GL11.glPopAttrib();
        GL11.glPopMatrix();

        ResourceHandler.bindDefaultParticles();
        tessellator.startDrawingQuads();
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }
}
