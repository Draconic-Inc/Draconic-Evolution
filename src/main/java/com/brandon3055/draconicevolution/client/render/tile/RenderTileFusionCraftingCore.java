package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.vec.Matrix4;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerFusionCrafting;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIXED;

public class RenderTileFusionCraftingCore extends TileEntityRenderer<TileCraftingCore> {

    public RenderTileFusionCraftingCore(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileCraftingCore te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packetLight, int packetOverlay) {
        ItemStack stack = !te.getStackInCore(1).isEmpty() ? te.getStackInCore(1) : te.getStackInCore(0);
        Minecraft mc = Minecraft.getInstance();
        if (!stack.isEmpty()) {
            mStack.pushPose();
            mStack.translate(0.5, 0.5, 0.5);
            mStack.scale(0.5F, 0.5F, 0.5F);
            mStack.mulPose(new Quaternion(0, (ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0, true));
            mc.getItemRenderer().renderStatic(stack, FIXED, packetLight, packetOverlay, mStack, getter);
            mStack.popPose();
        }

        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
//        mStack.scale(5, 5, 5);
        mStack.translate(0.5, -1, 0.5);
        RenderSystem.pushMatrix();
        new Matrix4(mStack).glApply();
        EffectTrackerFusionCrafting.interpPosX = player.xOld + (player.getX() - player.xOld) * (double) partialTicks;
        EffectTrackerFusionCrafting.interpPosY = player.yOld + (player.getY() - player.yOld) * (double) partialTicks;
        EffectTrackerFusionCrafting.interpPosZ = player.zOld + (player.getZ() - player.zOld) * (double) partialTicks;
//        te.renderEffects(partialTicks);
        RenderSystem.popMatrix();
    }
}