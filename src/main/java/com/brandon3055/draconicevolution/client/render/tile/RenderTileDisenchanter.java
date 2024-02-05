package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

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
            mStack.mulPose(Axis.YP.rotationDegrees((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F));
            mc.getItemRenderer().renderStatic(input, ItemDisplayContext.FIXED, packetLight, packetOverlay, mStack, getter, te.getLevel(), te.posSeed());
            mStack.popPose();
        }

        ItemStack books = te.itemHandler.getStackInSlot(1);

        if (!books.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.76, 0.5);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternionf().rotationXYZ(90 * (float) MathHelper.torad, 0, 180 * (float) MathHelper.torad));
            mc.getItemRenderer().renderStatic(books, ItemDisplayContext.FIXED, packetLight, packetOverlay, mStack, getter, te.getLevel(), te.posSeed());
            mStack.popPose();
        }
    }
}
