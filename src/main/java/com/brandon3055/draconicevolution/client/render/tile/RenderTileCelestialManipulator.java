package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerCelestialManipulator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class RenderTileCelestialManipulator extends TESRBase<TileCelestialManipulator> {
    @Override
    public void renderTileEntityAt(TileCelestialManipulator te, double x, double y, double z, float partialTicks, int destroyStage) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return;
        }
        EffectTrackerCelestialManipulator.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        EffectTrackerCelestialManipulator.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        EffectTrackerCelestialManipulator.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

        te.renderEffects(partialTicks);
    }

    @Override
    public boolean isGlobalRenderer(TileCelestialManipulator te) {
        return true;
    }
}
