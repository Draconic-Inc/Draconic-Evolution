package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIXED;

public class RenderTileCraftingInjector extends TileEntityRenderer<TileCraftingInjector> {

    public RenderTileCraftingInjector(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileCraftingInjector te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packetLight, int packetOverlay) {
        if (te.currentCraftingInventory != null && te.currentCraftingInventory.getCraftingStage() > 1000) {
            return;
        }

        if (!te.itemHandler.getStackInSlot(0).isEmpty()) {
            BlockState state = te.getLevel().getBlockState(te.getBlockPos());
            if (state.isAir()) return; //Turns out this may be an optifine issue???
            Direction facing = state.getValue(CraftingInjector.FACING);
            mStack.translate(0.5 + (facing.getStepX() * 0.45), 0.5 + (facing.getStepY() * 0.45), 0.5 + (facing.getStepZ() * 0.45));
            mStack.scale(0.5F, 0.5F, 0.5F);

            if (facing.getAxis() == Direction.Axis.Y) {
                if (facing == Direction.DOWN) {
                    mStack.mulPose(new Quaternion(180, 0, 0, true));
                }
            }
            else {
                mStack.mulPose(new Quaternion(facing.getStepZ() * 90, 0, facing.getStepX() * -90, true));
            }

            mStack.mulPose(new Quaternion(0, (ClientEventHandler.elapsedTicks + partialTicks) * -0.8F, 0, true));

            ItemStack stack = te.itemHandler.getStackInSlot(0);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, FIXED, packetLight, packetOverlay, mStack, getter);
        }
    }
}
