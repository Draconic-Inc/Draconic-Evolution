package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;

/**
 * Created by Brandon on 6/7/2015.
 */
public class RenderTileReactorStabilizer extends TileEntitySpecialRenderer {

    public static ModelReactorStabilizerRing modelStabilizerRing = new ModelReactorStabilizerRing();
    public static ModelReactorStabilizerCore modelStabilizerCore = new ModelReactorStabilizerCore();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        renderCore((TileReactorStabilizer) tileEntity, partialTick);
        // renderEffects((TileReactorStabilizer) tileEntity, partialTick);

        GL11.glPopMatrix();
    }

    public static void renderCore(TileReactorStabilizer tile, float partialTick) {
        GL11.glPushMatrix();
        float scale = (1F / 16F);
        float coreRotation = tile.coreRotation + (partialTick * tile.coreSpeed);
        float ringRotation = tile.ringRotation + (partialTick * tile.ringSpeed);

        switch (tile.facingDirection) {
            case 0:
                GL11.glRotated(90, -1, 0, 0);
                break;
            case 1:
                GL11.glRotated(90, 1, 0, 0);
                break;
            case 3:
                GL11.glRotated(180, 1, 0, 0);
                break;
            case 4:
                GL11.glRotated(90, 0, 1, 0);
                break;
            case 5:
                GL11.glRotated(90, 0, -1, 0);
        }

        ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
        modelStabilizerCore.render(null, coreRotation, tile.modelIllumination, 0F, 0F, 0F, scale);

        ResourceHandler.bindResource("textures/models/reactorStabilizerRing.png");
        GL11.glRotated(90, 1, 0, 0);
        GL11.glTranslated(0, -0.58, 0);
        GL11.glScaled(0.95, 0.95, 0.95);
        GL11.glRotatef(ringRotation, 0, 1, 0);
        modelStabilizerRing.render(null, -70F, tile.modelIllumination, 0F, 0F, 0F, scale);
        GL11.glPopMatrix();
    }

    // public static void renderEffects(TileReactorStabilizer tile, float partialTick) {
    // if (tile.isValid)
    // {
    // //Common Fields
    // MultiblockHelper.TileLocation master = tile.masterLocation;
    // float offsetX = (float)(master.posX - tile.xCoord);
    // float offsetY = (float)((double)master.posY - tile.yCoord);
    // float offsetZ = (float)(master.posZ - tile.zCoord);
    // float length = MathHelper.sqrt_float(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
    //
    // Tessellator tessellator = Tessellator.instance;
    //
    // //Pre Render
    // GL11.glPushMatrix();
    //// RenderHelper.disableStandardItemLighting();
    // GL11.glDisable(GL11.GL_CULL_FACE);
    // GL11.glShadeModel(GL11.GL_SMOOTH);
    // GL11.glEnable(GL11.GL_BLEND);
    // OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    // GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
    // GL11.glDisable(GL11.GL_LIGHTING);
    // GL11.glDepthMask(false);
    //
    // //Rotate beam to face target
    // float f7 = MathHelper.sqrt_float(offsetX * offsetX + offsetZ * offsetZ);
    // GL11.glRotatef((float) (-Math.atan2((double) offsetZ, (double) offsetX)) * 180.0F / (float) Math.PI - 90.0F,
    // 0.0F, 1.0F, 0.0F);
    // GL11.glRotatef((float)(-Math.atan2((double)f7, (double)offsetY)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F,
    // 0.0F);
    //
    //
    // //Draw Beams
    // GL11.glPushMatrix();
    // GL11.glTranslated(0, 0, -0.35);
    // ResourceHandler.bindTexture(new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png"));
    //// drawBeam(tessellator, 1F, 0.355F, 0.8F, offsetX, offsetY, offsetZ, tile.tick, partialTick, true, false);
    // GL11.glPopMatrix();
    //
    // GL11.glPushMatrix();
    // GL11.glTranslated(0, 0, 0.4526);
    // float coreSize = 1.1F;
    // float s = 0.355F;
    //// drawBeam(tessellator, s/coreSize, coreSize, length - 0.5F, offsetX, offsetY, offsetZ, tile.tick, partialTick,
    // false, false);
    // GL11.glPopMatrix();
    //
    //// GL11.glPushMatrix();
    //// GL11.glTranslated(0, 0, -0.35);
    //// ResourceHandler.bindTexture(new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png"));
    //// drawBeam(tessellator, 1F, 0.26F, 0.8F, offsetX, offsetY, offsetZ, tile.tick, partialTick, true, true);
    //// GL11.glPopMatrix();
    ////
    //// GL11.glPushMatrix();
    //// GL11.glTranslated(0, 0, 0.4526);
    //// coreSize = 1.1F;
    //// s = 0.263F;
    //// drawBeam(tessellator, s/coreSize, coreSize, length - 0.5F, offsetX, offsetY, offsetZ, tile.tick, partialTick,
    // false, true);
    //// GL11.glPopMatrix();
    //
    // //Post Render
    // GL11.glDepthMask(true);
    // GL11.glEnable(GL11.GL_LIGHTING);
    // GL11.glEnable(GL11.GL_CULL_FACE);
    // GL11.glShadeModel(GL11.GL_FLAT);
    //// RenderHelper.enableStandardItemLighting();
    // GL11.glDisable(GL11.GL_BLEND);
    // GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    // GL11.glPopMatrix();
    // }
    // }
}
