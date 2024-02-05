package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;

import java.util.List;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class RenderTileDislocatorPedestal implements BlockEntityRenderer<TileDislocatorPedestal> {

    public static List<BakedQuad> modelQuads = null;

    public RenderTileDislocatorPedestal(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileDislocatorPedestal tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedoverlay) {
        if (modelQuads == null) {
            modelQuads = Minecraft.getInstance().getBlockRenderer().getBlockModel(DEContent.DISLOCATOR_PEDESTAL.get().defaultBlockState()).getQuads(DEContent.DISLOCATOR_PEDESTAL.get().defaultBlockState(), null, tile.getLevel().random);
        }

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(Axis.YP.rotationDegrees(-tile.rotation.get() * 22.5F));
        mStack.translate(-0.5, -0.5, -0.5);

        VertexConsumer builder = getter.getBuffer(RenderType.solid());
        int i = 0;
        for (int j = modelQuads.size(); i < j; ++i) {
            BakedQuad bakedquad = modelQuads.get(i);
            builder.putBulkData(mStack.last(), bakedquad, 1F, 1F, 1F, 1F, packedLight, packedLight, true);
        }

        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = tile.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.79, 0.52);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(Axis.XP.rotationDegrees(-67.5F));
            mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedoverlay, mStack, getter, tile.getLevel(), tile.posSeed());
            mStack.popPose();
        }
        RenderUtils.endBatch(getter);
        mStack.popPose();
        if (!stack.isEmpty()) {
            drawName(tile, stack, mStack, getter, partialTicks);
        }
    }

    private void drawName(TileDislocatorPedestal tile, ItemStack item, PoseStack mStack, MultiBufferSource getter, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        HitResult hitResult = player.pick(10, partialTicks, true);
        boolean isCursorOver = hitResult.getType() == HitResult.Type.BLOCK && ((BlockHitResult) hitResult).getBlockPos().equals(tile.getBlockPos());
        boolean isSneaking = player.isShiftKeyDown();

        if (!isCursorOver && (isSneaking != DEOldConfig.invertDPDSB)) {
            return;
        }

        String name = item.hasCustomHoverName() ? item.getHoverName().getString() : "";
        if (item.getItem() instanceof DislocatorAdvanced) {
            DislocatorAdvanced.DislocatorTarget location = ((DislocatorAdvanced) item.getItem()).getTargetPos(item, tile.getLevel());
            if (location != null) {
                name = location.getName();
            }
        }
        if (name.isEmpty()) {
            return;
        }

        mStack.pushPose();
        mStack.translate(0.5, 1.125, 0.5);
        mStack.scale(0.02F, 0.02F, 0.02F);
        mStack.mulPose(new Quaternionf().rotationXYZ(0, -90 * (float) MathHelper.torad, 180 * (float) MathHelper.torad));

        double xDiff = player.getX() - (tile.getBlockPos().getX() + 0.5);
        double yDiff = (player.getY() + player.getEyeHeight()) - (tile.getBlockPos().getY() + 1.125);
        double zDiff = player.getZ() - (tile.getBlockPos().getZ() + 0.5);
        double yawAngle = Math.toDegrees(Math.atan2(zDiff, xDiff));
        double pitchAngle = Math.toDegrees(Math.atan2(yDiff, Utils.getDistance(player.getX(), player.getY(), player.getZ(), tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5)));

        mStack.mulPose(Axis.YP.rotationDegrees((float) yawAngle));
        mStack.mulPose(Axis.XP.rotationDegrees((float) -pitchAngle));

        int textWidth = mc.font.width(name);
        mStack.translate(0, 0, -0.0125);
        mc.font.drawInBatch(name, -(textWidth / 2F), 0, 0xffffff, true, mStack.last().pose(), getter, Font.DisplayMode.NORMAL, 0, 15728880);
        mStack.popPose();
    }
}
