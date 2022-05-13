package com.brandon3055.draconicevolution.handlers.dislocator;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.DislocatorEndPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class TileTarget extends DislocatorTarget {

    private BlockPos tilePos;

    public TileTarget(DislocatorEndPoint endPoint) {
        super(((BlockEntity) endPoint).getLevel().dimension());
        tilePos = ((BlockEntity) endPoint).getBlockPos();
    }

    public TileTarget(ResourceKey<Level> world) {
        super(world);
    }

    private DislocatorEndPoint getEndPoint(ServerLevel world) {
        BlockEntity tile = world.getBlockEntity(tilePos);
        if (tile instanceof DislocatorEndPoint) {
            return (DislocatorEndPoint) tile;
        }
        return null;
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerLevel targetWorld = getTargetWorld(server);
        DislocatorEndPoint target = getEndPoint(targetWorld);
        if (target != null) {
            Vec3 pos = target.getArrivalPos(linkID);
            if (pos != null) {
                Vec2 vec = target.getArrivalFacing(linkID);
                if (vec != null) {
                    return new TargetPos(new Vector3(pos), worldKey, vec.x, vec.y);
                }else {
                    return new TargetPos(new Vector3(pos), worldKey).setIncludeHeading(false);
                }
            }
        }
        return null;
    }

    @Override
    public void preTeleport(MinecraftServer server, Entity entity) {
        ServerLevel targetWorld = getTargetWorld(server);
        DislocatorEndPoint target = getEndPoint(targetWorld);
        if (target != null) {
            target.entityArriving(entity);
        }
    }

    public BlockPos getTilePos() {
        return tilePos;
    }

    //    @Override
//    public void postTeleport(ServerWorld targetWorld, Entity entity) {
//        DislocatorEndPoint target = getEndPoint(targetWorld);
//        if (target != null) {
//            target.entityArrived(entity);
//        }
//    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("x", tilePos.getX());
        nbt.putInt("y", tilePos.getY());
        nbt.putInt("z", tilePos.getZ());
        return super.save(nbt);
    }

    @Override
    protected void loadInternal(CompoundTag nbt) {
        super.loadInternal(nbt);
        tilePos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Override
    public TargetType getType() {
        return TargetType.TILE;
    }
}
