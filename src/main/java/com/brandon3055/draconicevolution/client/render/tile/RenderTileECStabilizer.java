package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCoreStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelLargeECStabilizer;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 19/4/2016.
 */
public class RenderTileECStabilizer extends TESRBase<TileCoreStabilizer> {

    private static ModelLargeECStabilizer largeModel;

    public RenderTileECStabilizer() {
        largeModel = new ModelLargeECStabilizer();
    }

    @Override
    public void render(TileCoreStabilizer te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.disableBlend();
        setLighting(200F);

        //region Rotate Renderer
        Direction facing;
        if (te.isCoreActive.get()) {
            facing = te.coreDirection;
        }
        else {
            facing = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, te.multiBlockAxis);
        }

        if (facing.getAxis() == Direction.Axis.X || facing.getAxis() == Direction.Axis.Y) {
            GlStateManager.rotatef(-90F, -facing.getYOffset(), facing.getXOffset(), 0);
        }
        else if (facing == Direction.SOUTH) {
            GlStateManager.rotatef(180F, 0, 1, 0);
        }
        //endregion

        renderRing(te, partialTicks);

        resetLighting();
        GlStateManager.popMatrix();
    }

    private void renderRing(TileCoreStabilizer te, float partialTicks) {
        if (!te.isValidMultiBlock.get()) {
            return;
        }

        GlStateManager.pushMatrix();
        ResourceHelperDE.bindTexture(DETextures.STABILIZER_LARGE);
        GlStateManager.rotatef(te.rotation + (te.isCoreActive.get() ? partialTicks : 0), 0, 0, 1);
        largeModel.render(1F / 16F);
        GlStateManager.popMatrix();
    }
}
