package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIXED;
import static net.minecraft.util.Direction.*;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class RenderTileEnergyTransfuser extends TileEntityRenderer<TileEnergyTransfuser> {

    private static final Style GALACTIC_STYLE = Style.EMPTY.setFontId(Minecraft.standardGalacticFontRenderer);
    public static final ITextComponent[] TEXT = {
            new StringTextComponent("N").mergeStyle(GALACTIC_STYLE),
            new StringTextComponent("E").mergeStyle(GALACTIC_STYLE),
            new StringTextComponent("S").mergeStyle(GALACTIC_STYLE),
            new StringTextComponent("W").mergeStyle(GALACTIC_STYLE)
    };
    private static final Direction[] DIR_MAP = {NORTH, EAST, SOUTH, WEST};

    public RenderTileEnergyTransfuser(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEnergyTransfuser tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        mStack.translate(0.5, 0.5, 0.5);
        for (int i = 0; i < 4; i++) {
            Direction dir = DIR_MAP[i];
            mStack.push();
            mStack.translate(0.5 * dir.getXOffset(), 0, 0.5 * dir.getZOffset());
            mStack.rotate(dir.getRotation());

            mStack.push();
            mStack.rotate(new Quaternion(90, 0, 0, true));
            double xOffset = dir == NORTH ? 2 : dir == SOUTH ? 1 : 2.5;
            mStack.translate(0.0625 * -xOffset, 0.0625 * -3.5, 0.0625 * 1.375);
            mStack.scale(0.0625F, 0.0625F, 0.0625F);
            mc.fontRenderer.func_238422_b_(mStack, TEXT[i].func_241878_f(), 0, 0, tile.ioModes[i].get().getColour());
            mStack.pop();

            ItemStack stack = tile.itemsCombined.getStackInSlot(i);
            if (!stack.isEmpty()) {
                mStack.rotate(new Quaternion(90, 0, 180, true));
                mStack.translate(0, 0, 0.0625 * (1.5 / 2));
                mStack.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, FIXED, packedLight, OverlayTexture.NO_OVERLAY, mStack, getter);
            }

            mStack.pop();
        }
    }
}
