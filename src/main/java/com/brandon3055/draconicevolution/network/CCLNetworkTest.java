package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.PacketCustom;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CCLNetworkTest implements PacketCustom.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        LogHelper.dev("Decompressed Size: " + packet.readableBytes());
    }
}
