package draconicevolution.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import draconicevolution.common.lib.References;
import draconicevolution.common.tileentities.TileParticleGenerator;

public class ParticleGenRenderer extends TileEntitySpecialRenderer
{

	private final ResourceLocation texture = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/ParticleGenTextureSheet.png");

	private float pxl = 1F / 64;

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float) x, (float) y, (float) z);
		TileParticleGenerator tileEntityGen = (TileParticleGenerator) tileEntity;
		renderBlock(tileEntityGen, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getWorldObj().getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
		
		GL11.glPopMatrix();
	}

	//And this method actually renders your tile entity
	public void renderBlock(TileParticleGenerator tl, World world, int x, int y, int z, Block block)
	{
		Tessellator tessellator = Tessellator.instance;
		 
		bindTexture(texture);
		boolean inverted = tl.inverted;
		

		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(255, 255, 255, 255);
		//tessellator.setBrightness(200);
		
	     int l = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
	     int l1 = l % 65536;
	     int l2 = l / 65536;
	     tessellator.setColorOpaque_F(1f, 1f, 1f);
	     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)l1, (float)l2); 
	     
		 //tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1F);
		
	
	     
		{//Draw Corners
			float f = 0.4F;
			drawCornerCube(tessellator, f, f, f, 1F - f, inverted);
			drawCornerCube(tessellator, f, -f, f, 1F - f, inverted);
			drawCornerCube(tessellator, -f, f, -f, 1F - f, inverted);
			drawCornerCube(tessellator, -f, -f, -f, 1F - f, inverted);
			drawCornerCube(tessellator, -f, f, f, 1F - f, inverted);
			drawCornerCube(tessellator, f, f, -f, 1F - f, inverted);
			drawCornerCube(tessellator, f, -f, -f, 1F - f, inverted);
			drawCornerCube(tessellator, -f, -f, f, 1F - f, inverted);
		}
		{//Draw Beams
			float f = 0.45F;
			float f2 = 0.4F;
			drawBeamX(tessellator, 0, f2, f2, 1F - f);
			drawBeamX(tessellator, 0, -f2, f2, 1F - f);
			drawBeamX(tessellator, 0, f2, -f2, 1F - f);
			drawBeamX(tessellator, 0, -f2, -f2, 1F - f);
			
			drawBeamY(tessellator, f2, 0, f2, 1F - f);
			drawBeamY(tessellator, -f2, 0, f2, 1F - f);
			drawBeamY(tessellator, f2, 0, -f2, 1F - f);
			drawBeamY(tessellator, -f2, 0, -f2, 1F - f);
			
			drawBeamZ(tessellator, f2, f2, 0, 1F - f);
			drawBeamZ(tessellator, -f2, f2, 0, 1F - f);
			drawBeamZ(tessellator, f2, -f2, 0, 1F - f);
			drawBeamZ(tessellator, -f2, -f2, 0, 1F - f);
		}

		tessellator.draw();

		
	}

	private void drawCornerCube(Tessellator tess, float x, float y, float z, float FP, boolean inverted)
	{
		float srcXMin = inverted ? 38F * pxl: 32F * pxl;
		float srcYMin = 0F;
		float srcXMax = inverted ? 44F * pxl : 38F * pxl;
		float srcYMax = 6 * pxl;
		//float FP = 0.6F; //Scale
		float FN = 1F - FP;

		//X+
		tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMin, srcYMax);

		//X-
		tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMax);

		//Y+
		tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMin, srcYMax);

		//Y-
		tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMax);

		//Z+
		tess.addVertexWithUV(FP + x, FN + y, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FN + x, FN + y, FP + z, srcXMin, srcYMax);

		//Z-
		tess.addVertexWithUV(FN + x, FN + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, FN + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, FN + y, FN + z, srcXMin, srcYMax);
	}

	private void drawBeamX(Tessellator tess, float x, float y, float z, float FP)
	{
		float srcXMin = 0;
		float srcYMin = 0F;
		float srcXMax = 32F * pxl;
		float srcYMax = 4 * pxl;
		float FN = 1F - FP;
		
		float XX = 0.9F;
		float XM = 0.1F;

		//Y+
		tess.addVertexWithUV(XM, FP + y, FN + z, srcXMin, srcYMax);
		tess.addVertexWithUV(XM, FP + y, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(XX, FP + y, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(XX, FP + y, FN + z, srcXMax, srcYMax);

		//Y-
		tess.addVertexWithUV(XM, FN + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(XX, FN + y, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(XX, FN + y, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(XM, FN + y, FP + z, srcXMin, srcYMax);

		//Z+
		tess.addVertexWithUV(XX, FN + y, FP + z, srcXMin, srcYMax);
		tess.addVertexWithUV(XX, FP + y, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(XM, FP + y, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(XM, FN + y, FP + z, srcXMax, srcYMax);

		//Z-
		tess.addVertexWithUV(XM, FN + y, FN + z, srcXMin, srcYMax);
		tess.addVertexWithUV(XM, FP + y, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(XX, FP + y, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(XX, FN + y, FN + z, srcXMax, srcYMax);
	}

	private void drawBeamY(Tessellator tess, float x, float y, float z, float FP)
	{
		float srcXMin = 0;
		float srcYMin = 0F;
		float srcXMax = 32F * pxl;
		float srcYMax = 4 * pxl;
		float FN = 1F - FP;
		
		float XX = 0.9F;
		float XM = 0.1F;

		//X+
		tess.addVertexWithUV(FP + x, XM, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, XX, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, XX, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, XM, FP + z, srcXMin, srcYMax);

		//X-
		tess.addVertexWithUV(FN + x, XM, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, XX, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, XX, FN + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FN + x, XM, FN + z, srcXMin, srcYMax);

		//Z+
		tess.addVertexWithUV(FP + x, XM, FP + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, XX, FP + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, XX, FP + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FN + x, XM, FP + z, srcXMin, srcYMax);
		
		//Z-
		tess.addVertexWithUV(FN + x, XM, FN + z, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, XX, FN + z, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, XX, FN + z, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, XM, FN + z, srcXMin, srcYMax);
		
	}

	private void drawBeamZ(Tessellator tess, float x, float y, float z, float FP)
	{
		float srcXMin = 0;
		float srcYMin = 0F;
		float srcXMax = 32F * pxl;
		float srcYMax = 4 * pxl;
		float FN = 1F - FP;
		
		float XX = 0.9F;
		float XM = 0.1F;

		//X+
		tess.addVertexWithUV(FP + x, FN + y, XM, srcXMin, srcYMax);
		tess.addVertexWithUV(FP + x, FP + y, XM, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, XX, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FN + y, XX, srcXMax, srcYMax);

		//X-
		tess.addVertexWithUV(FN + x, FN + y, XX, srcXMin, srcYMax);
		tess.addVertexWithUV(FN + x, FP + y, XX, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, XM, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, FN + y, XM, srcXMax, srcYMax);

		//Y+
		tess.addVertexWithUV(FN + x, FP + y, XM, srcXMin, srcYMin);
		tess.addVertexWithUV(FN + x, FP + y, XX, srcXMax, srcYMin);
		tess.addVertexWithUV(FP + x, FP + y, XX, srcXMax, srcYMax);
		tess.addVertexWithUV(FP + x, FP + y, XM, srcXMin, srcYMax);

		//Y-
		tess.addVertexWithUV(FN + x, FN + y, XM, srcXMin, srcYMax);
		tess.addVertexWithUV(FP + x, FN + y, XM, srcXMin, srcYMin);
		tess.addVertexWithUV(FP + x, FN + y, XX, srcXMax, srcYMin);
		tess.addVertexWithUV(FN + x, FN + y, XX, srcXMax, srcYMax);

	}
}