package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class RenderTilePlacedItem implements BlockEntityRenderer<TilePlacedItem> {

    public RenderTilePlacedItem(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TilePlacedItem tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        List<ItemStack> stackList = tile.getStacksInOrder();
        float scale = stackList.size() == 1 && tile.toolMode.get() ? 14 / 16F : 7 / 16F;

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        Direction direction = tile.getBlockState().getValue(PlacedItem.FACING);
        rotateToSide(direction, mStack);

        int posLong = (int)tile.getBlockPos().asLong();
        for (int i = 0; i < stackList.size(); i++) {
            ItemStack stack = stackList.get(i);
            mStack.pushPose();
            if (stack.getItem() instanceof BlockItem) {
                mStack.translate(PlacedItem.getXOffset(i, stackList.size()), -0.5 + ((3 / 16D)), PlacedItem.getZOffset(i, stackList.size()));
                mStack.mulPose(new Quaternion(0, tile.rotation[i].get() * -22.5F, 0, true));
                mStack.mulPose(new Quaternion(90, 0, 0, true));
                mStack.scale(6/8F, 6/8F, 6/8F);
            } else {
                mStack.translate(PlacedItem.getXOffset(i, stackList.size()), -0.5 + ((0.55 / 16D) * scale), PlacedItem.getZOffset(i, stackList.size()));
                mStack.mulPose(new Quaternion(0, tile.rotation[i].get() * -22.5F, 0, true));
                mStack.mulPose(new Quaternion(90, 0, 0, true));
                mStack.scale(scale, scale, scale);
            }
            mc.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, mStack, getter, posLong + i);
            mStack.popPose();
            mStack.translate(0, 0.00005F, 0); //Adds a slight offset to avoid z-fighting when items overlap
        }

        mStack.popPose();
    }

    private void rotateToSide(Direction direction, PoseStack mStack) {
        switch (direction) {
            case DOWN:
                mStack.mulPose(new Quaternion(180, 0, 0, true));
                break;
            case NORTH:
                mStack.mulPose(new Quaternion(-90, 0, 0, true));
                break;
            case SOUTH:
                mStack.mulPose(new Quaternion(-90, 0, 180, true));
                break;
            case WEST:
                mStack.mulPose(new Quaternion(-90, 0, 90, true));
                break;
            case EAST:
                mStack.mulPose(new Quaternion(-90, 0, -90, true));
                break;
        }
    }
}
