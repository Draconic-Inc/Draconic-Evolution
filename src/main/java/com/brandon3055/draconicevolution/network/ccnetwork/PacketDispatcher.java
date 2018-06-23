package com.brandon3055.draconicevolution.network.ccnetwork;

import codechicken.lib.packet.PacketCustom;

/**
 * Created by brandon3055 on 12/06/2017.
 */
public class PacketDispatcher {
    public static final String NET_CHANNEL = "DEPCChannel";

    public static void dispatchToggleDislocators() {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 1);
        packet.sendToServer();
    }
}
