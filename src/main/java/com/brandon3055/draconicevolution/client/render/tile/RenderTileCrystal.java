package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyRelay;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileRemoteEnergyBase;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileWirelessEnergyTransceiver;

/**
 * Created by Brandon on 31/01/2015.
 */
public class RenderTileCrystal extends TileEntitySpecialRenderer {

    private static ResourceLocation texrure = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/CrystalPurpleTransparent.png");
    private static ResourceLocation crystalBlue = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/CrystalBlue.png");
    private static ResourceLocation crystalBlueAlpha = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/CrystalBlueAlpha.png");
    private static ResourceLocation crystalRed = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/CrystalRed.png");
    private static ResourceLocation crystalRedAlpha = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/CrystalRedAlpha.png");

    private IModelCustom modelCrystal;
    private IModelCustom modelCrystalTransceiver;

    public RenderTileCrystal() {
        modelCrystal = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/CCrystal.obj"));
        modelCrystalTransceiver = AdvancedModelLoader
                .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/CrystalTransceiver.obj"));
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
        if (tileEntity instanceof TileEnergyRelay || tileEntity instanceof TileWirelessEnergyTransceiver)
            renderEnergyRelay((TileRemoteEnergyBase) tileEntity, x, y, z, partialTick);
        else if (tileEntity instanceof TileEnergyTransceiver)
            renderTransceiver((TileEnergyTransceiver) tileEntity, x, y, z, partialTick);
    }

    public void renderEnergyRelay(TileRemoteEnergyBase tileEntity, double x, double y, double z, float partialTick) {
        // --- Pre Render ---//
        tileEntity.inView = 10;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RenderHelper.disableStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        float innerLight = 100f;
        float outerLight = 140f + ClientEventHandler.energyCrystalAlphaValue * 40F;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, innerLight, innerLight);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        // --- Render Bottom Layer ---//
        if (tileEntity.getPowerTier() == 0) bindTexture(crystalBlue);
        else bindTexture(crystalRed);

        modelCrystal.renderAll();

        // --- Render Alpha Overlay ---//
        if (tileEntity.getPowerTier() == 0) bindTexture(crystalBlueAlpha);
        else bindTexture(crystalRedAlpha);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f + ClientEventHandler.energyCrystalAlphaValue * 0.5f);

        modelCrystal.renderAll();

        GL11.glAlphaFunc(GL11.GL_GREATER, 0f);
        // GL11.glDisable(GL11.GL_ALPHA_TEST);

        // --- Render Overlay ---//
        bindTexture(texrure);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, outerLight, outerLight);

        modelCrystal.renderAll();

        // --- Post Render ---//
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
    }

    public void renderTransceiver(TileEnergyTransceiver tileEntity, double x, double y, double z, float partialTick) {
        // --- Pre Render ---//

        tileEntity.inView = 10;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        switch (tileEntity.facing) {
            case 0:
                GL11.glRotatef(180, 0, 0, 1);
                break;
            case 3:
                GL11.glRotatef(90, 1, 0, 0);
                break;
            case 2:
                GL11.glRotatef(90, -1, 0, 0);
                break;
            case 4:
                GL11.glRotatef(90, 0, 0, 1);
                break;
            case 5:
                GL11.glRotatef(90, 0, 0, -1);
                break;
        }

        GL11.glTranslated(0, -1, 0);

        GL11.glDisable(GL11.GL_LIGHTING);
        // RenderHelper.disableStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        float innerLight = 100f;
        float outerLight = 140f + ClientEventHandler.energyCrystalAlphaValue * 40F;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, innerLight, innerLight);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        // --- Render Bottom Layer ---//
        if (tileEntity.getPowerTier() == 0) bindTexture(crystalBlue);
        else bindTexture(crystalRed);

        modelCrystalTransceiver.renderAll();

        // --- Render Alpha Overlay ---//
        if (tileEntity.getPowerTier() == 0) bindTexture(crystalBlueAlpha);
        else bindTexture(crystalRedAlpha);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f + ClientEventHandler.energyCrystalAlphaValue * 0.5f);

        modelCrystalTransceiver.renderAll();

        GL11.glDisable(GL11.GL_ALPHA_TEST);

        // --- Render Overlay ---//
        bindTexture(texrure);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, outerLight, outerLight);

        modelCrystalTransceiver.renderAll();

        // --- Post Render ---//
        GL11.glDisable(GL11.GL_BLEND);
        // RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPopMatrix();
    }
}
