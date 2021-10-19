package com.brandon3055.draconicevolution.handlers.dislocator;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.DislocatorEndPoint;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class TileTarget extends DislocatorTarget {

    private BlockPos tilePos;

    public TileTarget(DislocatorEndPoint endPoint) {
        super(((TileEntity) endPoint).getLevel().dimension());
        tilePos = ((TileEntity) endPoint).getBlockPos();
    }

    public TileTarget(RegistryKey<World> world) {
        super(world);
    }

    private DislocatorEndPoint getEndPoint(ServerWorld world) {
        TileEntity tile = world.getBlockEntity(tilePos);
        if (tile instanceof DislocatorEndPoint) {
            return (DislocatorEndPoint) tile;
        }
        return null;
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerWorld targetWorld = getTargetWorld(server);
        DislocatorEndPoint target = getEndPoint(targetWorld);
        if (target != null) {
            Vector3d pos = target.getArrivalPos(linkID);
            if (pos != null) {
                Vector2f vec = target.getArrivalFacing(linkID);
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
        ServerWorld targetWorld = getTargetWorld(server);
        DislocatorEndPoint target = getEndPoint(targetWorld);
        if (target != null) {
            target.entityArriving(entity);
        }
    }

//    @Override
//    public void postTeleport(ServerWorld targetWorld, Entity entity) {
//        DislocatorEndPoint target = getEndPoint(targetWorld);
//        if (target != null) {
//            target.entityArrived(entity);
//        }
//    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("x", tilePos.getX());
        nbt.putInt("y", tilePos.getY());
        nbt.putInt("z", tilePos.getZ());
        return super.save(nbt);
    }

    @Override
    protected void loadInternal(CompoundNBT nbt) {
        super.loadInternal(nbt);
        tilePos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Override
    public TargetType getType() {
        return TargetType.TILE;
    }
}
