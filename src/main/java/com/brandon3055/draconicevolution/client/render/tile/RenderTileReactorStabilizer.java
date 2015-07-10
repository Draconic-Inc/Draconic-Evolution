package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
		renderEffects((TileReactorStabilizer) tileEntity, partialTick);

		GL11.glPopMatrix();
	}

	private void renderCore(TileReactorStabilizer tile, float partialTick) {
		float scale = (1F / 16F);
		float coreRotation = tile.coreRotation + (partialTick * tile.coreSpeed);
		float ringRotation = tile.ringRotation + (partialTick * tile.ringSpeed);

		switch (tile.facingDirection){
			case 0:	GL11.glRotated(90, -1, 0, 0); break;
			case 1:	GL11.glRotated(90, 1, 0, 0); break;
			case 3:	GL11.glRotated(180, 1, 0, 0); break;
			case 4: GL11.glRotated(90, 0, 1, 0); break;
			case 5:	GL11.glRotated(90, 0, -1, 0);
		}

		ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
		modelStabilizerCore.render(null, coreRotation, tile.modelIllumination, 0F, 0F, 0F, scale);

		GL11.glPushMatrix();
		ResourceHandler.bindResource("textures/models/reactorStabilizerRing.png");
		GL11.glRotated(90, 1, 0, 0);
		GL11.glTranslated(0, -0.58, 0);
		GL11.glScaled(0.95, 0.95, 0.95);
		GL11.glRotatef(ringRotation, 0, 1, 0);
		modelStabilizerRing.render(null, 0F, tile.modelIllumination, 0F, 0F, 0F, scale);
		GL11.glPopMatrix();
	}

	private void renderEffects(TileReactorStabilizer tile, float partialTick) {
		if (tile.isValid)
		{
			float someRotation = tile.tick;
			MultiblockHelper.TileLocation master = tile.masterLocation;

			float f2 = someRotation + partialTick;
			float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
			f3 = (f3 * f3 + f3) * 0.2F;
			float f4 = (float)(master.posX - tile.xCoord - (0 - 0) * (double)(1.0F - partialTick));
			float f5 = (float)((double)f3 + master.posY - 1.0D - tile.yCoord - (0 - 0) * (double)(1.0F - partialTick));
			float f6 = (float)(master.posZ - tile.zCoord - (0 - 0) * (double)(1.0F - partialTick));
			float f7 = MathHelper.sqrt_float(f4 * f4 + f6 * f6);
			float f8 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6);
			GL11.glPushMatrix();
	//		GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_ + 2.0F, (float)p_76986_6_);
			GL11.glRotatef((float)(-Math.atan2((double)f6, (double)f4)) * 180.0F / (float)Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef((float)(-Math.atan2((double)f7, (double)f5)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
			Tessellator tessellator = Tessellator.instance;
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_CULL_FACE);
			ResourceHandler.bindTexture(new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png"));

			GL11.glShadeModel(GL11.GL_SMOOTH);
			float f9 = 0.0F - ((float)tile.tick + partialTick) * 0.01F;
			float f10 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6) / 32.0F - ((float)tile.tick + partialTick) * 0.01F;
			tessellator.startDrawing(5);
			byte b0 = 8;

			for (int i = 0; i <= b0; ++i)
			{
				float f11 = MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F;
				float f12 = MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F;
				float f13 = (float)(i % b0) * 1.0F / (float)b0;
				tessellator.setColorOpaque_I(0);
				tessellator.addVertexWithUV((double)(f11 * 0.2F), (double)(f12 * 0.2F), 0.0D, (double)f13, (double)f10);
				tessellator.setColorOpaque_I(16777215);
				tessellator.addVertexWithUV((double)f11, (double)f12, (double)f8, (double)f13, (double)f9);
			}

			tessellator.draw();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glShadeModel(GL11.GL_FLAT);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
		}
	}
}
