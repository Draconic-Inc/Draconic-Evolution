package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Created by brandon3055 on 15/10/2021
 */
public class PlayerTarget extends DislocatorTarget {

    private UUID playerID;

    public PlayerTarget(Player player) {
        super(player.level.dimension());
        this.playerID = player.getUUID();
    }

    public PlayerTarget(ResourceKey<Level> world) {
        super(world);
    }

    @Override
    public TargetPos getTargetPos(MinecraftServer server, UUID linkID, UUID sourceDislocatorID) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerID);
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
    protected ServerLevel getTargetWorld(MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerID);
        if (player != null) {
            return (ServerLevel) player.level;
        }
        return super.getTargetWorld(server);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putUUID("player_id", playerID);
        return super.save(nbt);
    }

    @Override
    protected void loadInternal(CompoundTag nbt) {
        super.loadInternal(nbt);
        playerID = nbt.getUUID("player_id");
    }

    @Override
    public TargetType getType() {
        return TargetType.PLAYER;
    }
}
