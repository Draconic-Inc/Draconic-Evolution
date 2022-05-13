package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RenderTileCraftingInjector implements BlockEntityRenderer<TileFusionCraftingInjector> {

    public RenderTileCraftingInjector(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileFusionCraftingInjector te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packetLight, int packetOverlay) {
        if (te.getCore() != null && te.getCore().isCrafting() && te.getCore().getFusionState().ordinal() > 1) {
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
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packetLight, packetOverlay, mStack, getter, te.posSeed());
        }
    }
}
