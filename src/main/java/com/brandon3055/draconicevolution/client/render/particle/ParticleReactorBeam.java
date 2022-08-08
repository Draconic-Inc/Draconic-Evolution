package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IReactorPart;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 17/7/2015.
 */
@SideOnly(Side.CLIENT)
public class ParticleReactorBeam extends EntityFX {
    /**
     * Particle Type 0 = Energy Ring, 1 = single particle
     */
    private boolean renderParticle = true;

    private TileEntity tile;
    private boolean isInjectorBeam = false;

    public ParticleReactorBeam(TileEntity tile) {
        super(tile.getWorldObj(), tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, 0.0D, 0.0D, 0.0D);
        this.tile = tile;
        this.particleRed = 1F;
        this.particleGreen = 1F;
        this.particleBlue = 1F;
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleMaxAge = 4;
        this.setSize(1F, 1F);
        isInjectorBeam = tile instanceof TileReactorEnergyInjector;
    }

    public void update(boolean render) {
        for (this.renderParticle = render; this.particleMaxAge - this.particleAge < 4; ++this.particleMaxAge) {}
    }

    @Override
    public void onUpdate() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    @Override
    public void renderParticle(
            Tessellator tessellator, float partialTick, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY) {
        if (!((IReactorPart) tile).isActive()) return;
        tessellator.draw();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTick - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTick - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTick - interpPosZ);
        GL11.glTranslated((double) xx, (double) yy, (double) zz);

        // Common Fields
        MultiblockHelper.TileLocation master = ((IReactorPart) tile).getMaster();
        float offsetX = (float) (master.posX - tile.xCoord);
        float offsetY = (float) ((double) master.posY - tile.yCoord);
        float offsetZ = (float) (master.posZ - tile.zCoord);
        float length = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);

        // Rotate beam to face target
        float f7 = MathHelper.sqrt_float(offsetX * offsetX + offsetZ * offsetZ);
        GL11.glRotatef(
                (float) (-Math.atan2((double) offsetZ, (double) offsetX)) * 180.0F / (float) Math.PI - 90.0F,
                0.0F,
                1.0F,
                0.0F);
        GL11.glRotatef(
                (float) (-Math.atan2((double) f7, (double) offsetY)) * 180.0F / (float) Math.PI - 90.0F,
                1.0F,
                0.0F,
                0.0F);

        TileReactorCore reactor = ((IReactorPart) tile).getMaster().getTileEntity(worldObj) instanceof TileReactorCore
                ? (TileReactorCore) ((IReactorPart) tile).getMaster().getTileEntity(worldObj)
                : null;

        if (tile instanceof TileReactorStabilizer && reactor != null)
            renderStabilizerEffect(tessellator, reactor, offsetX, offsetY, offsetZ, length, partialTick);
        else if (reactor != null) {

            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glTranslated(0, 0, 0.3);
            // GL11.glPushMatrix();
            GL11.glRotatef(((float) particleAge + partialTick) * 20F, 0, 0, 1);
            ResourceHandler.bindResource("textures/particle/reactorEnergyBeam.png");
            //			float sizeOrigin = 0.5F;
            //			float sizeTarget = 0.2F;
            float sizeOrigin = 0.5F;

            int color = 0xFF2200;
            int alpha = (int) (200D * reactor.renderSpeed);

            float speed = 20F;
            float texV2 = 0.0F + ((float) particleAge + partialTick) * 0.01F * speed;
            float texV1 = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ)
                    + ((float) particleAge + partialTick) * 0.01F * speed;

            tessellator.startDrawing(5);
            tessellator.setBrightness(200);

            float sizeTarget = 0.1F;

            byte b0 = 16;
            length = 0F;
            for (int i = 0; i <= b0; ++i) {
                float verX = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.3F / (float) b0) * sizeTarget;
                float verY = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.3F / (float) b0) * sizeTarget;
                float texU = (float) (i % b0) * 1.0F / (float) b0;
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);

                tessellator.addVertexWithUV(
                        (double) (verX * sizeOrigin), (double) (verY * sizeOrigin), -0.55D, (double) texU, (double)
                                texV1);
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha);
                tessellator.addVertexWithUV(
                        (double) verX, (double) verY, (double) length, (double) texU, (double) texV2);
            }

            length = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ)
                    - (float) (reactor.getCoreDiameter() / 2D)
                    - 0.3F;
            sizeTarget = (float) (reactor.getCoreDiameter() / 2 * 0.2D);
            sizeOrigin = 0.1F / sizeTarget;

            for (int i = 0; i <= b0; ++i) {
                float verX = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.13F / (float) b0) * sizeTarget;
                float verY = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.13F / (float) b0) * sizeTarget;
                float texU = (float) (i % b0) * 1.0F / (float) b0;
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha);

                tessellator.addVertexWithUV(
                        (double) (verX * sizeOrigin), (double) (verY * sizeOrigin), 0.0D, (double) texU, (double)
                                texV1);
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);
                tessellator.addVertexWithUV(
                        (double) verX, (double) verY, (double) length, (double) texU, (double) texV2);
            }

            tessellator.draw();
            // GL11.glPopMatrix();
            GL11.glRotatef(((float) particleAge + partialTick) * -30F, 0, 0, 1);

            tessellator.startDrawing(5);
            tessellator.setBrightness(200);
            color = 0xFF4400;

            sizeTarget = (float) (reactor.getCoreDiameter() / 2 * 0.6D);
            sizeOrigin = 0.1F / sizeTarget;
            length += 0.4F;
            GL11.glTranslated(0, 0, -0.1);

            for (int i = 0; i <= b0; ++i) {
                float verX = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.13F / (float) b0) * sizeTarget;
                float verY = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.13F / (float) b0) * sizeTarget;
                float texU = (float) (i % b0) * 1.0F / (float) b0;
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha / 2);

                tessellator.addVertexWithUV(
                        (double) (verX * sizeOrigin), (double) (verY * sizeOrigin), 0.0D, (double) texU, (double)
                                texV1);
                tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);
                tessellator.addVertexWithUV(
                        (double) verX, (double) verY, (double) length, (double) texU, (double) texV2);
            }

            tessellator.draw();
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        ResourceHandler.bindDefaultParticles();
        tessellator.startDrawingQuads();
    }

    private void renderStabilizerEffect(
            Tessellator tessellator,
            TileReactorCore reactor,
            float offsetX,
            float offsetY,
            float offsetZ,
            float length,
            float partialTick) {
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // Draw Beams
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, -0.35);
        ResourceHandler.bindResource("textures/particle/reactorBeam.png");
        drawBeam(
                tessellator,
                reactor,
                1F,
                0.355F,
                0.8F,
                offsetX,
                offsetY,
                offsetZ,
                particleAge,
                partialTick,
                true,
                false,
                0x00ffff);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 0.45);
        float coreSize = (float) (reactor.getCoreDiameter() / 2) * 0.9F;
        float s = 0.355F;
        drawBeam(
                tessellator,
                reactor,
                s / coreSize,
                coreSize,
                length - (float) (reactor.getCoreDiameter() / 2.5D),
                offsetX,
                offsetY,
                offsetZ,
                particleAge,
                partialTick,
                false,
                false,
                0x00ffff);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, -0.35);
        drawBeam(
                tessellator,
                reactor,
                1F,
                0.263F,
                0.8F,
                offsetX,
                offsetY,
                offsetZ,
                particleAge,
                partialTick,
                true,
                true,
                0xff6600);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 0.45);
        coreSize = (float) (reactor.getCoreDiameter() / 2) * 0.4F;
        s = 0.263F;
        drawBeam(
                tessellator,
                reactor,
                s / coreSize,
                coreSize,
                length - 0.5F,
                offsetX,
                offsetY,
                offsetZ,
                particleAge,
                partialTick,
                false,
                true,
                0xff6600);
        GL11.glPopMatrix();

        GL11.glShadeModel(GL11.GL_FLAT);
    }

    /**
     * Size Origin is the fraction of size start Target
     * So if size target is 10 and size origin is 0.5 origin will actually be 5
     */
    private void drawBeam(
            Tessellator tessellator,
            TileReactorCore reactor,
            float sizeOrigin,
            float sizeTarget,
            float length,
            float offsetX,
            float offsetY,
            float offsetZ,
            int tick,
            float partialTick,
            boolean reverseTransparency,
            boolean reverseDirection,
            int color) {
        float speed = 3F;
        float texV2 = -0.1F + ((float) tick + partialTick) * (reverseDirection ? -0.01F : 0.01F) * speed;
        float texV1 = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) / 32.0F
                + ((float) tick + partialTick) * (reverseDirection ? -0.01F : 0.01F) * speed;

        tessellator.startDrawing(5);
        tessellator.setBrightness(200);

        byte b0 = 16;
        for (int i = 0; i <= b0; ++i) {
            float verX = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.13325F / (float) b0) * sizeTarget;
            float verY = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.13325F / (float) b0) * sizeTarget;
            float texU = (float) (i % b0) * 1.0F / (float) b0;
            tessellator.setColorRGBA(
                    (color & 0xFF0000) >> 16,
                    (color & 0xFF00) >> 8,
                    (color & 0xFF),
                    reverseTransparency ? 0 : (int) (255D * reactor.renderSpeed));
            tessellator.addVertexWithUV(
                    (double) (verX * sizeOrigin), (double) (verY * sizeOrigin), 0.0D, (double) texU, (double) texV1);
            tessellator.setColorRGBA(
                    (color & 0xFF0000) >> 16,
                    (color & 0xFF00) >> 8,
                    (color & 0xFF),
                    reverseTransparency ? (int) (255D * reactor.renderSpeed) : 0);
            tessellator.addVertexWithUV((double) verX, (double) verY, (double) length, (double) texU, (double) texV2);
        }

        tessellator.draw();
    }
}

//	@Override
//	public void renderParticle(Tessellator tessellator, float partialTick, float rotX, float rotXZ, float rotZ, float
// rotYZ, float rotXY) {
//		if (!((IIsSlave)tile).isActive()) return;
//		tessellator.draw();
//		GL11.glPushMatrix();
//		GL11.glDisable(GL11.GL_CULL_FACE);
//		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
//
//		float xx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTick - interpPosX);
//		float yy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTick - interpPosY);
//		float zz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTick - interpPosZ);
//		GL11.glTranslated((double)xx, (double)yy, (double)zz);
//
//		//Common Fields
//		MultiblockHelper.TileLocation master = ((IIsSlave)tile).getMaster();
//		float offsetX = (float)(master.posX - tile.xCoord);
//		float offsetY = (float)((double)master.posY - tile.yCoord);
//		float offsetZ = (float)(master.posZ - tile.zCoord);
//		float length = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
//
//
//		//Rotate beam to face target
//		float f7 = MathHelper.sqrt_float(offsetX * offsetX + offsetZ * offsetZ);
//		GL11.glRotatef((float) (-Math.atan2((double) offsetZ, (double) offsetX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F,
// 1.0F, 0.0F);
//		GL11.glRotatef((float)(-Math.atan2((double)f7, (double)offsetY)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F,
// 0.0F);
//
//		if (tile instanceof TileReactorStabilizer) renderStabilizerEffect(tessellator, offsetX, offsetY, offsetZ, length,
// partialTick);
//		else {
//			GL11.glShadeModel(GL11.GL_SMOOTH);
//			GL11.glTranslated(0, 0, 0.3);
//			//GL11.glPushMatrix();
//			GL11.glRotatef(((float)particleAge + partialTick)*20F, 0, 0, 1);
//			ResourceHandler.bindResource("textures/particle/reactorEnergyBeam.png");
////			float sizeOrigin = 0.5F;
////			float sizeTarget = 0.2F;
//			float sizeTarget = 0.2F;
//			float sizeOrigin = 0.5F;
//
//			int color = 0xFF2200;
//			int alpha = 200;
//			length -= 1;
//
//			float speed = 20F;
//			float texV2 = 0.0F + ((float)particleAge + partialTick) * 0.01F * speed;
//			float texV1 = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) +
// ((float)particleAge + partialTick) * 0.01F * speed;
//
//			tessellator.startDrawing(5);
//			tessellator.setBrightness(200);
//
//			byte b0 = 16;
//			for (int i = 0; i <= b0; ++i)
//			{
//				float verX = MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.13F / (float)b0) * sizeTarget;
//				float verY = MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.13F / (float)b0) * sizeTarget;
//				float texU = (float)(i % b0) * 1.0F / (float)b0;
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha);
//
//				tessellator.addVertexWithUV((double)(verX * sizeOrigin), (double)(verY * sizeOrigin), 0.0D, (double)texU,
// (double)texV1);
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);
//				tessellator.addVertexWithUV((double)verX, (double)verY, (double)length, (double)texU, (double)texV2);
//			}
//
//			sizeTarget = 0.1F;
//
//			length = 0F;
//			for (int i = 0; i <= b0; ++i)
//			{
//				float verX = MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.3F / (float)b0) * sizeTarget;
//				float verY = MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.3F / (float)b0) * sizeTarget;
//				float texU = (float)(i % b0) * 1.0F / (float)b0;
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);
//
//				tessellator.addVertexWithUV((double)(verX * sizeOrigin), (double)(verY * sizeOrigin), -0.55D, (double)texU,
// (double)texV1);
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha);
//				tessellator.addVertexWithUV((double)verX, (double)verY, (double)length, (double)texU, (double)texV2);
//			}
//
//			tessellator.draw();
//			//GL11.glPopMatrix();
//			GL11.glRotatef(((float)particleAge + partialTick)*-30F, 0, 0, 1);
//
//			tessellator.startDrawing(5);
//			tessellator.setBrightness(200);
//			color = 0xFF4400;
//			sizeOrigin = 0.15F;
//			sizeTarget = 0.6F;
//			length =  MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) - 1;
//			GL11.glTranslated(0, 0, -0.1);
//
//			for (int i = 0; i <= b0; ++i)
//			{
//				float verX = MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.13F / (float)b0) * sizeTarget;
//				float verY = MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.13F / (float)b0) * sizeTarget;
//				float texU = (float)(i % b0) * 1.0F / (float)b0;
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), alpha/2);
//
//				tessellator.addVertexWithUV((double)(verX * sizeOrigin), (double)(verY * sizeOrigin), 0.0D, (double)texU,
// (double)texV1);
//				tessellator.setColorRGBA((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, (color & 0xFF), 0);
//				tessellator.addVertexWithUV((double)verX, (double)verY, (double)length, (double)texU, (double)texV2);
//			}
//
//			tessellator.draw();
//			GL11.glShadeModel(GL11.GL_FLAT);
//		}
//
//		GL11.glPopMatrix();
//		GL11.glEnable(GL11.GL_CULL_FACE);
//		ResourceHandler.bindDefaultParticles();
//		tessellator.startDrawingQuads();
//	}
