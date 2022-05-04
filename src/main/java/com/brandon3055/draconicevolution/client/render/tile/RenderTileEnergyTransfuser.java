package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class RenderTileEnergyTransfuser extends TileEntityRenderer<TileEnergyTransfuser> {

    private static final Style GALACTIC_STYLE = Style.EMPTY.withFont(Minecraft.ALT_FONT);
    public static final ITextComponent[] TEXT = {
            new StringTextComponent("N").withStyle(GALACTIC_STYLE),
            new StringTextComponent("E").withStyle(GALACTIC_STYLE),
            new StringTextComponent("S").withStyle(GALACTIC_STYLE),
            new StringTextComponent("W").withStyle(GALACTIC_STYLE)
    };
    private static final Direction[] DIR_MAP = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public RenderTileEnergyTransfuser(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEnergyTransfuser tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        mStack.translate(0.5, 0.5, 0.5);
        for (int i = 0; i < 4; i++) {
            Direction dir = DIR_MAP[i];
            mStack.pushPose();
            mStack.translate(0.5 * dir.getStepX(), 0, 0.5 * dir.getStepZ());
            mStack.mulPose(dir.getRotation());

            mStack.pushPose();
            mStack.mulPose(new Quaternion(90, 0, 0, true));
            double xOffset = dir == Direction.NORTH ? 2 : dir == Direction.SOUTH ? 1 : 2.5;
            mStack.translate(0.0625 * -xOffset, 0.0625 * -3.5, 0.0625 * 1.375);
            mStack.scale(0.0625F, 0.0625F, 0.0625F);
            mc.font.draw(mStack, TEXT[i].getVisualOrderText(), 0, 0, tile.ioModes[i].get().getColour());
            mStack.popPose();

            ItemStack stack = tile.itemsCombined.getStackInSlot(i);
            if (!stack.isEmpty()) {
                mStack.mulPose(new Quaternion(90, 0, 180, true));
                mStack.translate(0, 0, 0.0625 * (1.5 / 2));
                mStack.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, mStack, getter);
            }

            mStack.popPose();
        }
    }
}
