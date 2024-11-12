package com.brandon3055.draconicevolution.handlers.dislocator;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class GroundTarget extends DislocatorTarget {

    private Vector3 entityPos;
    private UUID entityUUID;

    public GroundTarget(ItemEntity entity) {
        super(entity.level().dimension());
        this.entityUUID = entity.getUUID();
        this.entityPos = Vector3.fromEntity(entity);
    }

    public GroundTarget(ResourceKey<Level> world) {
        super(world);
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerLevel targetWorld = getTargetWorld(server);
        Entity entity = targetWorld.getEntity(entityUUID);
        if (!(entity instanceof ItemEntity)) {
            AABB bb = new AABB(entityPos.pos());
            bb.inflate(5);
            List<ItemEntity> items = targetWorld.getEntitiesOfClass(ItemEntity.class, bb);
            for (ItemEntity item : items) {
                ItemStack stack = item.getItem();
                UUID id = BoundDislocator.getLinkId(stack);
                if (id != null && id.equals(linkID)) {
                    return new TargetPos(item);
                }
            }
        } else {
            ItemStack stack = ((ItemEntity) entity).getItem();
            UUID id = BoundDislocator.getLinkId(stack);
            if (id != null && id.equals(linkID)) {
                return new TargetPos(entity);
            }
        }
        return null;
    }

    @Override
    protected void loadInternal(CompoundTag nbt) {
        super.loadInternal(nbt);
        entityPos = Vector3.fromNBT(nbt);
        entityUUID = nbt.getUUID("entity_uuid");
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        entityPos.writeToNBT(nbt);
        nbt.putUUID("entity_uuid", entityUUID);
        return super.save(nbt);
    }

    @Override
    public TargetType getType() {
        return TargetType.ON_GROUND;
    }
}
