package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.PacketCustom;

import static com.brandon3055.draconicevolution.network.DraconicNetwork.CHANNEL;

/**
 * Created by brandon3055 on 12/06/2017.
 */
public class PacketDispatcher {
    //@formatter:off
    //Client to server
    public static final int S_TOGGLE_DISLOCATORS =      1;
    public static final int S_TOOL_PROFILE =            2;
    public static final int S_CYCLE_DIG_AOE =           3;
    public static final int S_CYCLE_ATTACK_AOE =        4;

    //Server to client


    //@formatter:on

    public static void dispatchToggleDislocators() {
        PacketCustom packet = new PacketCustom(CHANNEL, S_TOGGLE_DISLOCATORS);
        packet.sendToServer();
    }

    public static void dispatchToolProfileChange(boolean armor) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_TOOL_PROFILE);
        packet.writeBoolean(armor);
        packet.sendToServer();
    }

    public static void dispatchCycleDigAOE(boolean depth) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_CYCLE_DIG_AOE);
        packet.writeBoolean(depth);
        packet.sendToServer();
    }

    public static void dispatchCycleAttackAOE(boolean reverse) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_CYCLE_ATTACK_AOE);
        packet.writeBoolean(reverse);
        packet.sendToServer();
    }
}
