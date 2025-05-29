package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.internal.network.ClientConfigurationPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustomChannel;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.IPhase;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 17/12/19.
 */
public class DraconicNetwork {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static final ResourceLocation CHANNEL_NAME = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "network");
    public static final PacketCustomChannel CHANNEL = new PacketCustomChannel(CHANNEL_NAME)
            .optional()
            .versioned(BrandonsCore.container().getModInfo().getVersion().toString())
            .client(() -> ClientPacketHandler::new)
            .server(() -> ServerPacketHandler::new);


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
    public static final int S_BOOST_STATE =             13;

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

    public static void init(IEventBus modBus) {
        LOCK.lock();
        CHANNEL.init(modBus);
    }

    public static void sendToggleMagnets(RegistryAccess registryAccess) {
        new PacketCustom(CHANNEL_NAME, S_TOGGLE_DISLOCATORS, registryAccess).sendToServer();
    }

    public static void sendToolProfileChange(RegistryAccess registryAccess, boolean armor) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_TOOL_PROFILE, registryAccess);
        packet.writeBoolean(armor);
        packet.sendToServer();
    }

    public static void sendCycleDigAOE(RegistryAccess registryAccess, boolean depth) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_CYCLE_DIG_AOE, registryAccess);
        packet.writeBoolean(depth);
        packet.sendToServer();
    }

    public static void sendCycleAttackAOE(RegistryAccess registryAccess, boolean reverse) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_CYCLE_ATTACK_AOE, registryAccess);
        packet.writeBoolean(reverse);
        packet.sendToServer();
    }

    public static void sendModuleContainerClick(RegistryAccess registryAccess, ModuleGrid.GridPos cell, float mouseX, float mouseY, int mouseButton, ClickType type) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_MODULE_CONTAINER_CLICK, registryAccess);
        packet.writeByte(cell.getGridX());
        packet.writeByte(cell.getGridY());
        packet.writeFloat(mouseX);
        packet.writeFloat(mouseY);
        packet.writeByte(mouseButton);
        packet.writeEnum(type);
        packet.sendToServer();
    }

    public static void sendModuleMessage(RegistryAccess registryAccess, int gridX, int gridY, Consumer<MCDataOutput> dataConsumer) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_MODULE_ENTITY_MESSAGE, registryAccess);
        packet.writeByte(gridX);
        packet.writeByte(gridY);
        dataConsumer.accept(packet);
        packet.sendToServer();
    }

    public static void sendPropertyData(RegistryAccess registryAccess, PropertyData data) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_PROPERTY_DATA, registryAccess);
        data.write(packet);
        packet.sendToServer();
    }

    public static void sendOpenItemConfig(RegistryAccess registryAccess, boolean modules) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_ITEM_CONFIG_GUI, registryAccess);
        packet.writeBoolean(modules);
        packet.sendToServer();
    }

    public static void sendOpenModuleConfig(RegistryAccess registryAccess) {
        new PacketCustom(CHANNEL_NAME, S_MODULE_CONFIG_GUI, registryAccess).sendToServer();
    }

    public static void sendExplosionEffect(RegistryAccess registryAccess, ResourceKey<Level> dimension, BlockPos pos, int radius, boolean reload) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_EXPLOSION_EFFECT, registryAccess);
        packet.writePos(pos);
        packet.writeVarInt(radius);
        packet.writeBoolean(reload);
        packet.sendToDimension(dimension);
    }

    public static void sendImpactEffect(Level level, BlockPos position, int i) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_IMPACT_EFFECT, level.registryAccess());
        packet.writePos(position);
        packet.writeByte(i);
        packet.sendToChunk((ServerLevel) level, position);
    }

    public static void sendUndyingActivation(LivingEntity target, Item item) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_UNDYING_ACTIVATION, target.registryAccess());
        packet.writeVarInt(target.getId());
        packet.writeRegistryId(BuiltInRegistries.ITEM, item);
        packet.sendToChunk((ServerLevel) target.level(), target.blockPosition());
    }

    public static void sendDislocatorMessage(RegistryAccess registryAccess, int id, Consumer<MCDataOutput> callback) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_DISLOCATOR_MESSAGE, registryAccess);
        packet.writeByte(id);
        callback.accept(packet);
        packet.sendToServer();
    }

    public static void sendBlinkEffect(ServerPlayer player, float distance) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_BLINK, player.registryAccess());
        packet.writeVarInt(player.getId());
        packet.writeFloat(distance);
        packet.sendToChunk((ServerLevel) player.level(), player.blockPosition());
    }

    public static void sendStaffEffect(LivingEntity source, int damageType, Consumer<MCDataOutput> callback) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_STAFF_EFFECT, source.registryAccess());
        packet.writeByte(damageType);
        packet.writeVarInt(source.getId());
        callback.accept(packet);
        packet.sendToChunk((ServerLevel)source.level(), source.blockPosition());
    }

    public static void sendFusionRecipeMove(RegistryAccess registryAccess, RecipeHolder<IFusionRecipe> recipe, boolean maxTransfer) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_JEI_FUSION_TRANSFER, registryAccess);
        packet.writeResourceLocation(recipe.id());
        packet.writeBoolean(maxTransfer);
        packet.sendToServer();
    }

    public static void sendGuardianBeam(Level level, Vector3 source, Vector3 target, float power) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_GUARDIAN_BEAM, level.registryAccess());
        packet.writeVector(source);
        packet.writeVector(target);
        packet.writeFloat(power);
        packet.sendToChunk((ServerLevel) level, source.pos());
    }

    public static void sendGuardianPhasePacket(DraconicGuardianEntity entity, IPhase phase, int func, Consumer<MCDataOutput> callBack) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_GUARDIAN_PACKET, entity.registryAccess());
        packet.writeInt(entity.getId());
        packet.writeByte(phase.getType().getId());
        packet.writeByte(func);
        if (callBack != null) callBack.accept(packet);
        packet.sendToChunk((ServerLevel) entity.level(), entity.blockPosition());
    }

    public static void sendBossShieldPacket(ServerPlayer player, UUID id, int operation, Consumer<MCDataOutput> callBack) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, C_BOSS_SHIELD_INFO, player.registryAccess());
        packet.writeUUID(id);
        packet.writeByte(operation);
        if (callBack != null) callBack.accept(packet);
        packet.sendToPlayer(player);
    }

    public static void sendDislocatorTeleported(ServerPlayer player) {
        new PacketCustom(CHANNEL_NAME, C_DISLOCATOR_TELEPORTED, player.registryAccess()).sendToPlayer(player);
    }

    public static void sendPlaceItem(RegistryAccess registryAccess) {
        new PacketCustom(CHANNEL_NAME, S_PLACE_ITEM, registryAccess).sendToServer();
    }

    public static void sendChunkRelight(LevelChunk chunk) {
        new PacketCustom(CHANNEL_NAME, C_CHUNK_RELIGHT, chunk.getLevel().registryAccess())
                .writeInt(chunk.getPos().x)
                .writeInt(chunk.getPos().z)
                .sendToChunk((ServerLevel) chunk.getLevel(), chunk.getPos());
    }

    public static void sendSprintState(RegistryAccess registryAccess, boolean boosting) {
        PacketCustom packet = new PacketCustom(CHANNEL_NAME, S_BOOST_STATE, registryAccess);
        packet.writeBoolean(boosting);
        packet.sendToServer();
    }
}
