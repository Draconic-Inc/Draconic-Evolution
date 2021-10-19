package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class PlayerTarget extends DislocatorTarget {

    private UUID playerID;

    public PlayerTarget(PlayerEntity player) {
        super(player.level.dimension());
        this.playerID = player.getUUID();
    }

    public PlayerTarget(RegistryKey<World> world) {
        super(world);
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerPlayerEntity player = server.getPlayerList().getPlayer(playerID);
        if (player != null) {
            for (ItemStack stack : player.inventory.items) {
                if (BoundDislocator.isValid(stack) && !sourceDislocatorID.equals(BoundDislocator.getDislocatorId(stack))) {
                    return new TargetPos(player);
                }
            }
        }
        return null;
    }

    @Override
    protected ServerWorld getTargetWorld(MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerList().getPlayer(playerID);
        if (player != null) {
            return (ServerWorld) player.level;
        }
        return super.getTargetWorld(server);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putUUID("player_id", playerID);
        return super.save(nbt);
    }

    @Override
    protected void loadInternal(CompoundNBT nbt) {
        super.loadInternal(nbt);
        playerID = nbt.getUUID("player_id");
    }

    @Override
    public TargetType getType() {
        return TargetType.PLAYER;
    }
}
