package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderTileEntityDetector extends TESRBase<TileEntityDetector> {

    private static CCModel model;

    public RenderTileEntityDetector() {
    }

    @Override
    public void renderTileEntityAt(TileEntityDetector te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.73, z + 0.5);
        double scale = te.isAdvanced() ? 0.5 : 0.35;
        GlStateManager.scale(scale, scale, scale);

        float h = te.lthRot + (te.hRot - te.lthRot) * partialTicks;

        GlStateManager.rotate(-h * (180F / (float) Math.PI) - 90, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-te.yRot * (180F / (float) Math.PI) + 90, 1, 0, 0);
        GlStateManager.rotate(180, 0, 1, 0);

        renderItem(getRenderStack(te.isAdvanced()));

        GlStateManager.popMatrix();
    }

    private ItemStack eye = null;
    private ItemStack skull = null;

    private ItemStack getRenderStack(boolean advanced) {
        if (advanced) {
            if (skull == null) {
                skull = new ItemStack(Items.SKULL, 1, 1);
            }
            return skull;
        }
        else {
            if (eye == null) {
                eye = new ItemStack(Items.ENDER_EYE);
            }
            return eye;
        }

    }
}
