package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustomChannelBuilder;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import sun.net.NetHooks;

/**
 * Created by brandon3055 on 17/12/19.
 */
public class DraconicNetwork {

    public static final ResourceLocation CHANNEL = new ResourceLocation(DraconicEvolution.MODID + ":network");
    public static EventNetworkChannel netChannel;

    //@formatter:off
    //Client to server
    public static final int S_TOGGLE_DISLOCATORS =      1;
    public static final int S_TOOL_PROFILE =            2;
    public static final int S_CYCLE_DIG_AOE =           3;
    public static final int S_CYCLE_ATTACK_AOE =        4;

    //Server to client
    public static final int C_CRYSTAL_UPDATE =          1;


    //@formatter:on

    public static void sendToggleDislocators() {
        PacketCustom packet = new PacketCustom(CHANNEL, S_TOGGLE_DISLOCATORS);
        packet.sendToServer();
    }

    public static void sendToolProfileChange(boolean armor) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_TOOL_PROFILE);
        packet.writeBoolean(armor);
        packet.sendToServer();
    }

    public static void sendCycleDigAOE(boolean depth) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_CYCLE_DIG_AOE);
        packet.writeBoolean(depth);
        packet.sendToServer();
    }

    public static void sendCycleAttackAOE(boolean reverse) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_CYCLE_ATTACK_AOE);
        packet.writeBoolean(reverse);
        packet.sendToServer();
    }















    public static void init() {
        netChannel = PacketCustomChannelBuilder.named(CHANNEL)
                .networkProtocolVersion(() -> "1")//
                .clientAcceptedVersions(e -> true)//
                .serverAcceptedVersions(e -> true)//
                .assignClientHandler(() -> ClientPacketHandler::new)//
                .assignServerHandler(() -> ServerPacketHandler::new)//
                .build();
    }
}
