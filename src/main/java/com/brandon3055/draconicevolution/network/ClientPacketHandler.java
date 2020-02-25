package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.tileentity.TileEntity;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
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