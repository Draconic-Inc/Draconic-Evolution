package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.blocks.machines.CraftingInjector;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

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
                    mStack.mulPose(Axis.XP.rotationDegrees(180));
                }
            } else {
                mStack.mulPose(new Quaternionf().rotationXYZ(facing.getStepZ() * 90 * (float) MathHelper.torad, 0, facing.getStepX() * -90 * (float) MathHelper.torad));
            }

            mStack.mulPose(Axis.YP.rotationDegrees((ClientEventHandler.elapsedTicks + partialTicks) * -0.8F));

            ItemStack stack = te.itemHandler.getStackInSlot(0);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packetLight, packetOverlay, mStack, getter, te.getLevel(), te.posSeed());
        }
    }
}
