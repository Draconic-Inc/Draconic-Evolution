package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderTileDisenchanter implements BlockEntityRenderer<TileDisenchanter> {

    public RenderTileDisenchanter(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileDisenchanter te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packetLight, int packetOverlay) {
        ItemStack input = te.itemHandler.getStackInSlot(0);
        Minecraft mc = Minecraft.getInstance();

        if (!input.isEmpty()) {
        	mStack.pushPose();
        	mStack.translate(0.5, 1.15 + Math.sin((ClientEventHandler.elapsedTicks + partialTicks) / 40) / 10, 0.5);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternion(0, (ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0, true));
            mc.getItemRenderer().renderStatic(input, ItemTransforms.TransformType.FIXED, packetLight, packetOverlay, mStack, getter, te.posSeed());
            mStack.popPose();
        }

        ItemStack books = te.itemHandler.getStackInSlot(1);

        if (!books.isEmpty()) {
        	mStack.pushPose();
        	mStack.translate(0.5, 0.76, 0.5);
        	mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternion(270, 0, 0, true));
            mc.getItemRenderer().renderStatic(books, ItemTransforms.TransformType.FIXED, packetLight, packetOverlay, mStack, getter, te.posSeed());
        	mStack.popPose();
        }
    }
}
