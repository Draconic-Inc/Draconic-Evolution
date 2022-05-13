package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderTileDissEnchanter implements BlockEntityRenderer<TileDissEnchanter> {

    public RenderTileDissEnchanter(BlockEntityRendererProvider.Context context) {
    }

//    @Override
    public void render(TileDissEnchanter te, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack input = te.itemHandler.getStackInSlot(0);

//        if (!input.isEmpty()) {
//            RenderSystem.pushMatrix();
//            RenderSystem.translated(x + 0.5, y + 0.75 + 0.3, z + 0.5);
//            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
//            RenderSystem.rotatef((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
////            renderItem(input);
//            RenderSystem.popMatrix();
//        }
//
//        ItemStack books = te.itemHandler.getStackInSlot(1);
//
//        if (!books.isEmpty()) {
//            RenderSystem.pushMatrix();
//            RenderSystem.translated(x + 0.5, y + 0.76, z + 0.5);
//            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
//            RenderSystem.rotatef(90, 1F, 0F, 0F);
////            renderItem(books);
//            RenderSystem.popMatrix();
//        }
    }

    @Override
    public void render(TileDissEnchanter p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

    }
}
