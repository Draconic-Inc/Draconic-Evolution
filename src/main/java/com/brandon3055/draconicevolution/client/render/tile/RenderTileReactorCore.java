package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 16/6/2015.
 */
public class RenderTileReactorCore extends TileEntitySpecialRenderer {
	public static IModelCustom reactorModel;

	public RenderTileReactorCore(){
		reactorModel = AdvancedModelLoader.loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/reactorCoreModel.obj"));
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
		TileReactorCore tile = (TileReactorCore) tileEntity;
//		tile.renderList.clear();

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

		renderReactorCore(tile, partialTick);
//		for (MultiblockHelper.TileOffset offset : tile.stabilizerLocations){
//			if (!(offset.getTileEntity(tileEntity) instanceof TileReactorStabilizer)) continue;
//			GL11.glPushMatrix();
//
//			TileReactorStabilizer stabilizer = (TileReactorStabilizer)offset.getTileEntity(tileEntity);
//			GL11.glTranslated(-offset.offsetX, -offset.offsetY, -offset.offsetZ);
//			//RenderTileReactorStabilizer.renderCore(stabilizer, partialTick);
//			//RenderTileReactorStabilizer.renderEffects(stabilizer, partialTick);
//
//			GL11.glPopMatrix();
//		}

		GL11.glPopMatrix();
	}


	public void renderReactorCore(TileReactorCore tile, float partialTick) {
		float rotation = tile.renderRotation + (partialTick * 0.2F);
		double ff = 1;
		double r = ff < 0.5 ? 1 - (ff*2) : 0;
		double g = ff > 0.5 ? (ff-0.5)*2 : 0;
		double b = ff * 2;
		double a = ff < 0.1 ? (ff * 10) + 0.5 : 1;

		//Pre Render
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);

		//Render inner
		ResourceHandler.bindResource("textures/models/reactorCore.png");
		ResourceHandler.bindResource("textures/blocks/draconic_block_blank.png");
		if (tile.reactorFuel + tile.convertedFuel < 144){
			GL11.glPushMatrix();
			GL11.glScaled(0.1, 0.1, 0.1);
			reactorModel.renderAll();
			GL11.glPopMatrix();
		}

		GL11.glRotatef(rotation, 0.5F, 1F, 0.5F);
		GL11.glScaled(0.5, 0.5, 0.5);
		double r3 = tile.getCoreDiameter();
		GL11.glScaled(r3, r3, r3);
		reactorModel.renderAll();

		//Mid Render
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0F);

		//Render Outer
		ResourceHandler.bindResource("textures/models/reactorShieldPlate.png");
		GL11.glColor4d(r, g, b, a);
		GL11.glScaled(1.03F, 1.03F, 1.03F);
		GL11.glRotatef(2343, 0.5F, 1F, 0.5F);
		GL11.glRotatef(-rotation*2, 0.5F, 1F, 0.5F);
		reactorModel.renderAll();

		//Post Render
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
		GL11.glPopMatrix();
	}

}
