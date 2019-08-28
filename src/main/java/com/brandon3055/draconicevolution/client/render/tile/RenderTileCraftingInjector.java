package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class RenderTileCraftingInjector extends TESRBase<TileCraftingInjector> {
    @Override
    public void render(TileCraftingInjector te, double x, double y, double z, float partialTicks, int destroyStage, float a) {
        if (te.currentCraftingInventory != null && te.currentCraftingInventory.getCraftingStage() > 1000) {
            return;
        }


        if (te.getStackInSlot(0) != null) {
            GlStateManager.pushMatrix();

            EnumFacing facing = EnumFacing.VALUES[MathHelper.abs(te.facing.value % EnumFacing.VALUES.length)];
            GlStateManager.translate(x + 0.5 + (facing.getXOffset() * 0.45), y + 0.5 + (facing.getYOffset() * 0.45), z + 0.5 + (facing.getZOffset() * 0.45));
            GlStateManager.scale(0.5F, 0.5F, 0.5F);

            if (facing.getAxis() == EnumFacing.Axis.Y) {
                if (facing == EnumFacing.DOWN) {
                    GlStateManager.rotate(180, 1, 0, 0);
                }
            }
            else {
                GlStateManager.rotate(90, facing.getZOffset(), 0, facing.getXOffset() * -1);
            }

            GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);

            renderItem(te.getStackInSlot(0));

            GlStateManager.popMatrix();
        }
    }
}
