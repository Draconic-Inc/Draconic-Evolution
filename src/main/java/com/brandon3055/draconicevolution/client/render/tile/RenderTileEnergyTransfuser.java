package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class RenderTileEnergyTransfuser implements BlockEntityRenderer<TileEnergyTransfuser> {

    private static final Style GALACTIC_STYLE = Style.EMPTY.withFont(Minecraft.ALT_FONT);
    public static final Component[] TEXT = {
            Component.literal("N").withStyle(GALACTIC_STYLE),
            Component.literal("E").withStyle(GALACTIC_STYLE),
            Component.literal("S").withStyle(GALACTIC_STYLE),
            Component.literal("W").withStyle(GALACTIC_STYLE)
    };
    private static final Direction[] DIR_MAP = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public RenderTileEnergyTransfuser(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEnergyTransfuser tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        mStack.translate(0.5, 0.5, 0.5);
        for (int i = 0; i < 4; i++) {
            Direction dir = DIR_MAP[i];
            mStack.pushPose();
            mStack.translate(0.5 * dir.getStepX(), 0, 0.5 * dir.getStepZ());
            mStack.mulPose(dir.getRotation());

            mStack.pushPose();
            mStack.mulPose(Axis.XP.rotationDegrees(90));
            double xOffset = dir == Direction.NORTH ? 2 : dir == Direction.SOUTH ? 1 : 2.5;
            mStack.translate(0.0625 * -xOffset, 0.0625 * -3.5, 0.0625 * 1.375);
            mStack.scale(0.0625F, 0.0625F, 0.0625F);
            mc.font.drawInBatch(TEXT[i].getVisualOrderText(), 0, 0, tile.ioModes[i].get().getColour(), false, mStack.last().pose(), getter, Font.DisplayMode.NORMAL, 240, 240);
            mStack.popPose();

            ItemStack stack = tile.itemsCombined.getStackInSlot(i);
            if (!stack.isEmpty()) {
                mStack.mulPose(new Quaternionf().rotationYXZ(90 * (float) MathHelper.torad, 90 * (float) MathHelper.torad, 180 * (float) MathHelper.torad));
                mStack.translate(0, 0, 0.0625 * (1.5 / 2));
                mStack.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, mStack, getter, tile.getLevel(), tile.posSeed());
            }

            mStack.popPose();
        }
    }
}
