package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.renderer.GlStateManager;

public class RenderTileFusionCraftingCore extends TESRBase<TileFusionCraftingCore>
{
    @Override
    public void renderTileEntityAt(TileFusionCraftingCore te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getStackInSlot(0) != null) {
            GlStateManager.pushMatrix();

            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);

            GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);

            renderItem(te.getStackInSlot(0));

            GlStateManager.popMatrix();
        }
	}
}
