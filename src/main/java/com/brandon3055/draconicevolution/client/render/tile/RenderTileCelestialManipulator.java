package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerCelestialManipulator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

public class RenderTileCelestialManipulator implements BlockEntityRenderer<TileCelestialManipulator> {

    public RenderTileCelestialManipulator(BlockEntityRendererProvider.Context context) {
    }

    //    @Override
    public void render(TileCelestialManipulator te, double x, double y, double z, float partialTicks, int destroyStage) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        EffectTrackerCelestialManipulator.interpPosX = player.xOld + (player.getX() - player.xOld) * (double) partialTicks;
        EffectTrackerCelestialManipulator.interpPosY = player.yOld + (player.getY() - player.yOld) * (double) partialTicks;
        EffectTrackerCelestialManipulator.interpPosZ = player.zOld + (player.getZ() - player.zOld) * (double) partialTicks;

        te.renderEffects(partialTicks);
    }

    @Override
    public void render(TileCelestialManipulator te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }

    @Override
    public boolean shouldRenderOffScreen(TileCelestialManipulator te) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileCelestialManipulator blockEntity) {
        return INFINITE_EXTENT_AABB;
    }
}
