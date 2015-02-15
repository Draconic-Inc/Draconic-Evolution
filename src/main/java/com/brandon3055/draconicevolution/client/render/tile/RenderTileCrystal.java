package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyRelay;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 31/01/2015.
 */
public class RenderTileCrystal extends TileEntitySpecialRenderer {

	private static ResourceLocation texrure = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalPurpleTransparent.png");
	private static ResourceLocation crystalBlue = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalBlue.png");
	private static ResourceLocation crystalBlueAlpha = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalBlueAlpha.png");
	private static ResourceLocation crystalPurple = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalPurple.png");
	private static ResourceLocation crystalPurpleAlpha = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalPurpleAlpha.png");
	private static ResourceLocation crystalRed = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalRed.png");
	private static ResourceLocation crystalRedAlpha = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/CrystalRedAlpha.png");
	private static ResourceLocation beamTexture = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/EnergyBeam.png");


	private IModelCustom model;

	public RenderTileCrystal(){
		model = AdvancedModelLoader.loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/Crystal.obj"));
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
		if (tileEntity instanceof TileEnergyRelay) renderTileEntityAt((TileEnergyRelay)tileEntity, x, y, z, partialTick);
	}


	public void renderTileEntityAt(TileEnergyRelay tileEntity, double x, double y, double z, float partialTick) {
		//--- Pre Render ---//
		tileEntity.inView = 10;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		//GL11.glDisable(GL11.GL_LIGHTING); //todo disable?
		RenderHelper.disableStandardItemLighting();
		GL11.glEnable(GL11.GL_BLEND);
		float innerLight = 100f;
		float outerLight = 140f + ClientEventHandler.energyCrystalAlphaValue * 40F;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, innerLight, innerLight);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		//--- Render Bottom Layer ---//
		if (tileEntity.getType() == 0) bindTexture(crystalBlue);
		else bindTexture(crystalRed);

		model.renderAll();


		//--- Render Alpha Overlay ---//
		if (tileEntity.getType() == 0) bindTexture(crystalBlueAlpha);
		else bindTexture(crystalRedAlpha);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f + ClientEventHandler.energyCrystalAlphaValue * 0.5f);

		model.renderAll();

		GL11.glDisable(GL11.GL_ALPHA_TEST);


		//--- Render Overlay ---//
		bindTexture(texrure);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, outerLight, outerLight);

		model.renderAll();


		//--- Post Render ---//
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
