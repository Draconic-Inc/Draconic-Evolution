package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class RenderTileDissEnchanter extends TESRBase<TileDissEnchanter> {
    @Override
    public void render(TileDissEnchanter te, double x, double y, double z, float partialTicks, int destroyStage, float a) {
        ItemStack input = te.getStackInSlot(0);

        if (input != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.75 + 0.3, z + 0.5);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
            renderItem(input);
            GlStateManager.popMatrix();
        }

        ItemStack books = te.getStackInSlot(1);

        if (books != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.76, z + 0.5);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(90, 1F, 0F, 0F);
            renderItem(books);
            GlStateManager.popMatrix();
        }
    }
}
