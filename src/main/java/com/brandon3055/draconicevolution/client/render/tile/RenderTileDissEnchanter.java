package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class RenderTileDissEnchanter extends TESRBase<TileDissEnchanter> {

    public RenderTileDissEnchanter(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

//    @Override
    public void render(TileDissEnchanter te, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack input = te.itemHandler.getStackInSlot(0);

        if (!input.isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.translated(x + 0.5, y + 0.75 + 0.3, z + 0.5);
            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
            RenderSystem.rotatef((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
//            renderItem(input);
            RenderSystem.popMatrix();
        }

        ItemStack books = te.itemHandler.getStackInSlot(1);

        if (!books.isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.translated(x + 0.5, y + 0.76, z + 0.5);
            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
            RenderSystem.rotatef(90, 1F, 0F, 0F);
//            renderItem(books);
            RenderSystem.popMatrix();
        }
    }
}
