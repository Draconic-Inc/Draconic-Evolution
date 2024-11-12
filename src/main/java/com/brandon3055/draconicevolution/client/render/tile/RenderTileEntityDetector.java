package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

public class RenderTileEntityDetector implements BlockEntityRenderer<TileEntityDetector> {

    private ItemStack eye = ItemStack.EMPTY;
    private ItemStack skull = ItemStack.EMPTY;

    public RenderTileEntityDetector(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TileEntityDetector te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedoverlay) {
        Minecraft mc = Minecraft.getInstance();
        boolean advanced = te.isAdvanced();
        float scale = advanced ? 0.5F : 0.35F;
        float yaw = te.lastLookYaw + (te.lookYaw - te.lastLookYaw) * partialTicks;
        float pitch = te.lastLookPitch + (te.lookPitch - te.lastLookPitch) * partialTicks;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.73, 0.5);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        mc.getItemRenderer().renderStatic(getRenderStack(advanced), ItemDisplayContext.FIXED, packedLight, packedLight, poseStack, buffers, te.getLevel(), te.posSeed());
        poseStack.popPose();
    }

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
    public AABB getRenderBoundingBox(TileEntityDetector blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).expandTowards(0, 1, 0);
    }
}
