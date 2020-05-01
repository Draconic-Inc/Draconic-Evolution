package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class RenderTileFusionCraftingCore extends TESRBase<TileCraftingCore> {

    public RenderTileFusionCraftingCore(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

//    @Override
    public void render(TileCraftingCore te, double x, double y, double z, float partialTicks, int destroyStage) {
//        if (MinecraftForgeClient.getRenderPass() == 0) {
            ItemStack stack = !te.getStackInCore(1).isEmpty() ? te.getStackInCore(1) : te.getStackInCore(0);
            if (!stack.isEmpty()) {
                RenderSystem.pushMatrix();
                RenderSystem.translated(x + 0.5, y + 0.5, z + 0.5);
                RenderSystem.scalef(0.5F, 0.5F, 0.5F);
                RenderSystem.rotatef((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
//                renderItem(stack);
                RenderSystem.popMatrix();
            }
//        }
//        else {
//            ClientPlayerEntity player = Minecraft.getInstance().player;
//            if (player == null) {
//                return;
//            }
//            EffectTrackerFusionCrafting.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
//            EffectTrackerFusionCrafting.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
//            EffectTrackerFusionCrafting.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
//
//            te.renderEffects(partialTicks);
//
//        }
    }
}
