package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerFusionCrafting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

public class RenderTileFusionCraftingCore extends TESRBase<TileFusionCraftingCore> {
    @Override
    public void render(TileFusionCraftingCore te, double x, double y, double z, float partialTicks, int destroyStage, float a) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            ItemStack stack = !te.getStackInCore(1).isEmpty() ? te.getStackInCore(1) : te.getStackInCore(0);
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 0.8F, 0F, -1F, 0F);
                renderItem(stack);
                GlStateManager.popMatrix();
            }
        }
        else {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player == null) {
                return;
            }
            EffectTrackerFusionCrafting.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            EffectTrackerFusionCrafting.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            EffectTrackerFusionCrafting.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

            te.renderEffects(partialTicks);

        }
    }
}
