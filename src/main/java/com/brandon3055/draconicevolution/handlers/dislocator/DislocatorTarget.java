package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.brandonscore.utils.TargetPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by brandon3055 on 15/10/2021
 */
public abstract class DislocatorTarget {

    protected ResourceKey<Level> worldKey;

    public DislocatorTarget(ResourceKey<Level> worldKey) {
        this.worldKey = worldKey;
    }

    /**
     * The position the teleporting entity should be teleported to.
     * */
    @Nullable
    public abstract TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID);

    public void preTeleport(MinecraftServer server, Entity entity) {

    }

//    public void postTeleport(ServerWorld targetWorld, Entity entity) {
//
//    }

    protected ServerLevel getTargetWorld(MinecraftServer server) {
        return server.getLevel(worldKey);
    }

    public CompoundTag save(CompoundTag nbt) {
        nbt.putByte("target_type", (byte) getType().ordinal());
        nbt.putString("world_key", worldKey.location().toString());
        return nbt;
    }

    protected void loadInternal(CompoundTag nbt) {

    }

    public ResourceKey<Level> getWorldKey() {
        return worldKey;
    }

    public static DislocatorTarget load(CompoundTag nbt) {
        try {
            TargetType type = TargetType.values()[nbt.getByte("target_type")];
            ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("world_key")));
            DislocatorTarget target = type.createInstance(worldKey);
            target.loadInternal(nbt);
            return target;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract TargetType getType();

    enum TargetType {
        TILE(TileTarget::new),
        PLAYER(PlayerTarget::new),
        ENTITY_INVENTORY(EntityTarget::new),
        ON_GROUND(GroundTarget::new);

        private Function<ResourceKey<Level>, DislocatorTarget> factory;
        TargetType(Function<ResourceKey<Level>, DislocatorTarget> factory) {
            this.factory = factory;
        }

        public DislocatorTarget createInstance(ResourceKey<Level> worldKey) {
            return factory.apply(worldKey);
        }
    }
}
