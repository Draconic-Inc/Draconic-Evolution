package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustomChannelBuilder;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.IPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.UUID;
import java.util.function.Consumer;

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
    public static final int S_DISLOCATOR_MESSAGE =      9;
    public static final int S_JEI_FUSION_TRANSFER =     10;
    public static final int S_PLACE_ITEM =              11;
    public static final int S_MODULE_ENTITY_MESSAGE =   12;

    //Server to client
    public static final int C_CRYSTAL_UPDATE =          1;
    public static final int C_SHIELD_HIT =              2;
    public static final int C_EXPLOSION_EFFECT =        3;
    public static final int C_IMPACT_EFFECT =           4;
    public static final int C_UNDYING_ACTIVATION =   5;
    public static final int C_BLINK =                   6;
    public static final int C_STAFF_EFFECT =            7;
    public static final int C_GUARDIAN_BEAM =           8;
    public static final int C_GUARDIAN_PACKET =         9;
    public static final int C_BOSS_SHIELD_INFO =        10;
    public static final int C_DISLOCATOR_TELEPORTED =   11;
    public static final int C_CHUNK_RELIGHT =           12;

    //@formatter:on

    public static void sendToggleMagnets() {
        new PacketCustom(CHANNEL, S_TOGGLE_DISLOCATORS).sendToServer();
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

    public static void sendModuleContainerClick(ModuleGrid.GridPos cell, float mouseX, float mouseY, int mouseButton, ClickType type) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_MODULE_CONTAINER_CLICK);
        packet.writeByte(cell.getGridX());
        packet.writeByte(cell.getGridY());
        packet.writeFloat(mouseX);
        packet.writeFloat(mouseY);
        packet.writeByte(mouseButton);
        packet.writeEnum(type);
        packet.sendToServer();
    }

    public static void sendModuleMessage(int gridX, int gridY, Consumer<MCDataOutput> dataConsumer) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_MODULE_ENTITY_MESSAGE);
        packet.writeByte(gridX);
        packet.writeByte(gridY);
        dataConsumer.accept(packet);
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
        new PacketCustom(CHANNEL, S_MODULE_CONFIG_GUI).sendToServer();
    }

    public static void sendExplosionEffect(ResourceKey<Level> dimension, BlockPos pos, int radius, boolean reload) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_EXPLOSION_EFFECT);
        packet.writePos(pos);
        packet.writeVarInt(radius);
        packet.writeBoolean(reload);
        packet.sendToDimension(dimension);
    }

    public static void sendImpactEffect(Level world, BlockPos position, int i) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_IMPACT_EFFECT);
        packet.writePos(position);
        packet.writeByte(i);
        packet.sendToChunk(world, position);
    }

    public static void sendUndyingActivation(LivingEntity target, Item item) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_UNDYING_ACTIVATION);
        packet.writeVarInt(target.getId());
        packet.writeRegistryId(item);
        packet.sendToChunk(target.level, target.blockPosition());
    }

    public static void sendDislocatorMessage(int id, Consumer<MCDataOutput> callback) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_DISLOCATOR_MESSAGE);
        packet.writeByte(id);
        callback.accept(packet);
        packet.sendToServer();
    }

    public static void sendBlinkEffect(ServerPlayer player, float distance) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_BLINK);
        packet.writeVarInt(player.getId());
        packet.writeFloat(distance);
        packet.sendToChunk(player.level, player.blockPosition());
    }

    public static void sendStaffEffect(LivingEntity source, int damageType, Consumer<MCDataOutput> callback) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_STAFF_EFFECT);
        packet.writeByte(damageType);
        packet.writeVarInt(source.getId());
        callback.accept(packet);
        packet.sendToChunk(source.level, source.blockPosition());
    }

    public static void sendFusionRecipeMove(IFusionRecipe recipe, boolean maxTransfer) {
        PacketCustom packet = new PacketCustom(CHANNEL, S_JEI_FUSION_TRANSFER);
        packet.writeResourceLocation(recipe.getId());
        packet.writeBoolean(maxTransfer);
        packet.sendToServer();
    }

    public static void sendGuardianBeam(Level world, Vector3 source, Vector3 target, float power) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_GUARDIAN_BEAM);
        packet.writeVector(source);
        packet.writeVector(target);
        packet.writeFloat(power);
        packet.sendToChunk(world, source.pos());
    }

    public static void sendGuardianPhasePacket(DraconicGuardianEntity entity, IPhase phase, int func, Consumer<MCDataOutput> callBack) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_GUARDIAN_PACKET);
        packet.writeInt(entity.getId());
        packet.writeByte(phase.getType().getId());
        packet.writeByte(func);
        if (callBack != null) callBack.accept(packet);
        packet.sendToChunk(entity.level, entity.blockPosition());
    }

    public static void sendBossShieldPacket(ServerPlayer player, UUID id, int operation, Consumer<MCDataOutput> callBack) {
        PacketCustom packet = new PacketCustom(CHANNEL, C_BOSS_SHIELD_INFO);
        packet.writeUUID(id);
        packet.writeByte(operation);
        if (callBack != null) callBack.accept(packet);
        packet.sendToPlayer(player);
    }

    public static void sendDislocatorTeleported(ServerPlayer player) {
        new PacketCustom(CHANNEL, C_DISLOCATOR_TELEPORTED).sendToPlayer(player);
    }

    public static void sendPlaceItem() {
        new PacketCustom(CHANNEL, S_PLACE_ITEM).sendToServer();
    }

    public static void sendChunkRelight(LevelChunk chunk) {
        new PacketCustom(CHANNEL, C_CHUNK_RELIGHT)
                .writeInt(chunk.getPos().x)
                .writeInt(chunk.getPos().z)
                .sendToChunk(chunk.getLevel(), chunk.getPos());
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
