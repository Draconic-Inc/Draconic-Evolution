package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
        switch (packet.getType()) {
//            case 1: //Portal Arrival
//                TileEntity tile = mc.world.getTileEntity(packet.readPos());
//                if (tile instanceof TileDislocatorReceptacle) {
//                    ((TileDislocatorReceptacle) tile).setHidden();
//                }
//                break;
            case DraconicNetwork.C_CRYSTAL_UPDATE:
                CrystalUpdateBatcher.handleBatchedData(packet);
                break;
            case DraconicNetwork.C_EXPLOSION_EFFECT:
                sendExplosionEffect(mc, packet.readPos(), packet.readVarInt(), packet.readBoolean());
                break;
        }
    }


    public static void sendExplosionEffect(Minecraft mc, BlockPos pos, int radius, boolean reload) {
        if (reload) {
            mc.worldRenderer.loadRenderers();
        } else {
            ExplosionFX explosionFX = new ExplosionFX((ClientWorld) BrandonsCore.proxy.getClientWorld(), Vec3D.getCenter(pos), radius);
            mc.particles.addEffect(explosionFX);
        }
    }
}