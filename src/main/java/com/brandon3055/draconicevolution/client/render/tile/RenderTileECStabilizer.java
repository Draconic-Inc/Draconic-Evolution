package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelLargeECStabilizer;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 19/4/2016.
 */
public class RenderTileECStabilizer extends TESRBase<TileEnergyCoreStabilizer> {

    private static ModelLargeECStabilizer largeModel;

    public RenderTileECStabilizer() {
        largeModel = new ModelLargeECStabilizer();
    }

    @Override
    public void renderTileEntityAt(TileEnergyCoreStabilizer te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        //region Rotate Renderer
        EnumFacing facing;
        if (te.isCoreActive.value) {
            facing = te.coreDirection;
        } else {
            facing = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, te.multiBlockAxis);
        }

        if (facing.getAxis() == EnumFacing.Axis.X || facing.getAxis() == EnumFacing.Axis.Y) {
            GlStateManager.rotate(-90F, -facing.getFrontOffsetY(), facing.getFrontOffsetX(), 0);
        }
        else if (facing == EnumFacing.SOUTH) {
            GlStateManager.rotate(180F, 0, 1, 0);
        }
        //endregion

        renderRing(te, partialTicks);

        GlStateManager.popMatrix();
    }

    private void renderRing(TileEnergyCoreStabilizer te, float partialTicks) {
        if (!te.isValidMultiBlock.value) {
            return;
        }

        GlStateManager.pushMatrix();
        ResourceHelperDE.bindTexture("textures/blocks/particle_gen/stabilizerLarge.png");
        GlStateManager.rotate(te.rotation + (te.isCoreActive.value ? partialTicks : 0), 0, 0, 1);
        largeModel.render(null, 0F, 0F, 0F, 0F, 0F, 1F / 16F);
        GlStateManager.popMatrix();


//        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry("texture");
//
//        //X and Y size of the hole texture in pixels
//        int holeTextureXSize = 64;
//        int holeTextureYSize = 64;
//
//        //Position and size of the sub texture in pixels (Just some example values)
//        int xPosOfTexture = 16;
//        int yPosOfTexture = 16;
//        int xSizeOfTexture = 20;
//        int ySizeOfTexture = 20;
//
//
//        double pixelXSize = (sprite.getMaxU() - sprite.getMinU()) / holeTextureXSize;
//        double pixelYSize = (sprite.getMaxV() - sprite.getMinV()) / holeTextureYSize;
//
//
//        double texU = sprite.getMinU() + (pixelXSize * xPosOfTexture);
//        double texV = sprite.getMinV() + (pixelYSize * yPosOfTexture);
//
//        double texMaxU = texU + (pixelXSize * xSizeOfTexture);
//        double texMaxV = texV + (pixelYSize * ySizeOfTexture);






    }
}
