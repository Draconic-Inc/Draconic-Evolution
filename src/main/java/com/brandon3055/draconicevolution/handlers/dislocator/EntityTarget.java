package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.brandonscore.utils.TargetPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
@Deprecated //May implement this later
public class EntityTarget extends DislocatorTarget {

    public EntityTarget(RegistryKey<World> world) {
        super(world);
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        return null;
    }

    @Override
    public TargetType getType() {
        return TargetType.ENTITY_INVENTORY;
    }
}
