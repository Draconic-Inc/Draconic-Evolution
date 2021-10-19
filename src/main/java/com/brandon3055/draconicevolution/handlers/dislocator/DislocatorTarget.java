package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.brandonscore.utils.TargetPos;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by brandon3055 on 15/10/2021
 */
public abstract class DislocatorTarget {

    protected RegistryKey<World> worldKey;

    public DislocatorTarget(RegistryKey<World> worldKey) {
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

    protected ServerWorld getTargetWorld(MinecraftServer server) {
        return server.getLevel(worldKey);
    }

    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putByte("target_type", (byte) getType().ordinal());
        nbt.putString("world_key", worldKey.location().toString());
        return nbt;
    }

    protected void loadInternal(CompoundNBT nbt) {

    }

    public static DislocatorTarget load(CompoundNBT nbt) {
        try {
            TargetType type = TargetType.values()[nbt.getByte("target_type")];
            RegistryKey<World> worldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("world_key")));
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

        private Function<RegistryKey<World>, DislocatorTarget> factory;
        TargetType(Function<RegistryKey<World>, DislocatorTarget> factory) {
            this.factory = factory;
        }

        public DislocatorTarget createInstance(RegistryKey<World> worldKey) {
            return factory.apply(worldKey);
        }
    }
}
