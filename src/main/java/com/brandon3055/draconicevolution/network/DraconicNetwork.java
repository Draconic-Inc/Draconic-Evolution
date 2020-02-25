package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.PacketCustomChannelBuilder;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

/**
 * Created by brandon3055 on 17/12/19.
 */
public class DraconicNetwork {

    public static final ResourceLocation CHANNEL = new ResourceLocation(DraconicEvolution.MODID + ":network");
    public static EventNetworkChannel netChannel;

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
