package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustomChannelBuilder;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

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
    public static final int S_MODULE_CONTAINER_CLICK =  5;
    public static final int S_PROPERTY_DATA =           6;
    public static final int S_ITEM_CONFIG_GUI =         7;
    public static final int S_MODULE_CONFIG_GUI =       8;

    //Server to client
    public static final int C_CRYSTAL_UPDATE =          1;
    public static final int C_SHIELD_HIT =              2;
    public static final int C_EXPLOSION_EFFECT =        3;


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

    public static void sendModuleContainerClick(ModuleGrid.GridPos cell, int mouseButton, ClickType type) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_MODULE_CONTAINER_CLICK);
        packet.writeByte(cell.getGridX());
        packet.writeByte(cell.getGridY());
        packet.writeByte(mouseButton);
        packet.writeEnum(type);
        packet.sendToServer();
    }

    public static void sendPropertyData(PropertyData data) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_PROPERTY_DATA);
        data.write(packet);
        packet.sendToServer();
    }

    public static void sendOpenItemConfig(boolean modules) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_ITEM_CONFIG_GUI);
        packet.writeBoolean(modules);
        packet.sendToServer();
    }

    public static void sendOpenModuleConfig() {
        PacketCustom packet = new PacketCustom(CHANNEL, S_MODULE_CONFIG_GUI);
        packet.sendToServer();
    }

    public static void sendExplosionEffect(DimensionType dimension, BlockPos pos, int radius, boolean reload) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_EXPLOSION_EFFECT);
        packet.writePos(pos);
        packet.writeVarInt(radius);
        packet.writeBoolean(reload);
        packet.sendToDimension(dimension);
    }

//    public static void sendShieldHit(LivingEntity shieldedEntity) {
//        PacketCustom packet = new PacketCustom(CHANNEL, C_SHIELD_HIT);
//        packet.writeInt(shieldedEntity.getEntityId());
//        packet.sendToChunk(shieldedEntity.world, shieldedEntity.getPosition());
//    }









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
