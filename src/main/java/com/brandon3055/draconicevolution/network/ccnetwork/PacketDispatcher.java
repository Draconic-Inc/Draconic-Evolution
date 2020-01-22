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

    public static void dispatchToolProfileChange(boolean armor) {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 2);
        packet.writeBoolean(armor);
        packet.sendToServer();
    }

    public static void dispatchCycleDigAOE(boolean depth) {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 3);
        packet.writeBoolean(depth);
        packet.sendToServer();
    }

    public static void dispatchCycleAttackAOE(boolean reverse) {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 4);
        packet.writeBoolean(reverse);
        packet.sendToServer();
    }
    public static void dispatchToggleShields() {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 5);
        packet.sendToServer();
    }
}
