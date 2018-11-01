package com.brandon3055.draconicevolution.network.ccnetwork;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        switch (packet.getType()) {
            case 1: //Portal Arrival
                TileEntity tile = mc.world.getTileEntity(packet.readPos());
                if (tile instanceof TileDislocatorReceptacle) {
                    ((TileDislocatorReceptacle) tile).setHidden();
                }
                break;
        }
    }
}