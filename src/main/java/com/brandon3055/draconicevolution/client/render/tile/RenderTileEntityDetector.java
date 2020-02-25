package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RenderTileEntityDetector extends TESRBase<TileEntityDetector> {

    private static CCModel model;

    public RenderTileEntityDetector() {
    }

    @Override
    public void render(TileEntityDetector te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.73, z + 0.5);
        double scale = te.isAdvanced() ? 0.5 : 0.35;
        GlStateManager.scaled(scale, scale, scale);

        float h = te.lthRot + (te.hRot - te.lthRot) * partialTicks;

        GlStateManager.rotatef(-h * (180F / (float) Math.PI) - 90, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotated(-te.yRot * (180F / (float) Math.PI) + 90, 1, 0, 0);
//        GlStateManager.rotate(180, 0, 1, 0);

        renderItem(getRenderStack(te.isAdvanced()));

        GlStateManager.popMatrix();
    }

    private ItemStack eye = ItemStack.EMPTY;
    private ItemStack skull = ItemStack.EMPTY;

    private ItemStack getRenderStack(boolean advanced) {
        if (advanced) {
            if (skull.isEmpty()) {
                skull = new ItemStack(Items.WITHER_SKELETON_SKULL, 1);
            }
            return skull;
        }
        else {
            if (eye.isEmpty()) {
                eye = new ItemStack(Items.ENDER_EYE);
            }
            return eye;
        }

    }
}
