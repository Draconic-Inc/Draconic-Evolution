package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RenderTileEntityDetector implements BlockEntityRenderer<TileEntityDetector> {

    private static CCModel model;

    public RenderTileEntityDetector(BlockEntityRendererProvider.Context context) {
    }


    //    @Override
    public void render(TileEntityDetector te, double x, double y, double z, float partialTicks, int destroyStage) {
//        RenderSystem.pushMatrix();
//        RenderSystem.translated(x + 0.5, y + 0.73, z + 0.5);
//        double scale = te.isAdvanced() ? 0.5 : 0.35;
//        RenderSystem.scaled(scale, scale, scale);
//
//        float h = te.lthRot + (te.hRot - te.lthRot) * partialTicks;
//
//        RenderSystem.rotatef(-h * (180F / (float) Math.PI) - 90, 0.0F, 1.0F, 0.0F);
//        RenderSystem.rotatef(-te.yRot * (180F / (float) Math.PI) + 90, 1, 0, 0);
////        RenderSystem.rotate(180, 0, 1, 0);
//
////        renderItem(getRenderStack(te.isAdvanced()));
//
//        RenderSystem.popMatrix();
    }

    private ItemStack eye = ItemStack.EMPTY;
    private ItemStack skull = ItemStack.EMPTY;

    private ItemStack getRenderStack(boolean advanced) {
        if (advanced) {
            if (skull.isEmpty()) {
                skull = new ItemStack(Items.WITHER_SKELETON_SKULL, 1);
            }
            return skull;
        } else {
            if (eye.isEmpty()) {
                eye = new ItemStack(Items.ENDER_EYE);
            }
            return eye;
        }

    }

    @Override
    public void render(TileEntityDetector p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

    }
}
