package com.brandon3055.draconicevolution.handlers.dislocator;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class GroundTarget extends DislocatorTarget {

    private Vector3 entityPos;
    private UUID entityUUID;

    public GroundTarget(ItemEntity entity) {
        super(entity.level.dimension());
        this.entityUUID = entity.getUUID();
        this.entityPos = Vector3.fromEntity(entity);
    }

    public GroundTarget(RegistryKey<World> world) {
        super(world);
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerWorld targetWorld = getTargetWorld(server);
        Entity entity = targetWorld.getEntity(entityUUID);
        if (!(entity instanceof ItemEntity)) {
            AxisAlignedBB bb = new AxisAlignedBB(entityPos.pos().offset(-1, -1, -1), entityPos.pos().offset(1, 1, 1));
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
    protected void loadInternal(CompoundNBT nbt) {
        super.loadInternal(nbt);
        entityPos = Vector3.fromNBT(nbt);
        entityUUID = nbt.getUUID("entity_uuid");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        entityPos.writeToNBT(nbt);
        nbt.putUUID("entity_uuid", entityUUID);
        return super.save(nbt);
    }

    @Override
    public TargetType getType() {
        return TargetType.ON_GROUND;
    }
}
