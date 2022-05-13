package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.ItemCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileStabilizedSpawner extends TileBCore implements IInteractTile, IChangeListener {

    public ManagedEnum<SpawnerTier> spawnerTier = register(new ManagedEnum<>("spawner_tier", SpawnerTier.BASIC, DataFlags.SAVE_BOTH_SYNC_TILE));
    public ManagedStack mobSoul = register(new ManagedStack("mob_soul", DataFlags.SAVE_BOTH_SYNC_TILE));
    public ManagedBool isPowered = register(new ManagedBool("is_powered", DataFlags.SAVE_NBT_SYNC_TILE));
    public ManagedShort spawnDelay = register(new ManagedShort("spawn_delay", (short) 100, DataFlags.SAVE_NBT_SYNC_TILE));
    public ManagedInt startSpawnDelay = register(new ManagedInt("start_spawn_delay", 100, DataFlags.SAVE_NBT_SYNC_TILE));
    public StabilizedSpawnerLogic spawnerLogic = new StabilizedSpawnerLogic(this);

    private int activatingRangeFromPlayer = 24;

    public TileStabilizedSpawner(BlockPos pos, BlockState state) {
        super(DEContent.tile_stabilized_spawner, pos, state);
    }


    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel){
            spawnerLogic.serverTick((ServerLevel) level, worldPosition);
        } else {
            spawnerLogic.clientTick(level, worldPosition);
        }
    }


    public boolean isActive() {
        if (isPowered.get() || mobSoul.get().isEmpty()) {
            return false;
        } else if (spawnerTier.get().requiresPlayer && !level.hasNearbyAlivePlayer(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, (double) this.activatingRangeFromPlayer)) {
            return false;
        }
        return true;
    }

    @Override
    public void onNeighborChange(BlockPos changePos) {
        isPowered.set(level.hasNeighborSignal(worldPosition));
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == DEContent.mob_soul) {
            if (!level.isClientSide) {
                (mobSoul.set(stack.copy())).setCount(1);
                if (!player.isCreative()) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
            return true;
        } else if (stack.getItem() instanceof SpawnEggItem) {
            EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
            ItemStack soul = new ItemStack(DEContent.mob_soul);
            DEContent.mob_soul.setEntity(type.getRegistryName(), soul);
            mobSoul.set(soul);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return true;
        } else if (!stack.isEmpty()) {
            SpawnerTier prevTier = spawnerTier.get();
            if (stack.getItem() == DEContent.core_draconium) {
                if (spawnerTier.get() == SpawnerTier.BASIC) return false;
                spawnerTier.set(SpawnerTier.BASIC);
            } else if (stack.getItem() == DEContent.core_wyvern) {
                if (spawnerTier.get() == SpawnerTier.WYVERN) return false;
                spawnerTier.set(SpawnerTier.WYVERN);
            } else if (stack.getItem() == DEContent.core_awakened) {
                if (spawnerTier.get() == SpawnerTier.DRACONIC) return false;
                spawnerTier.set(SpawnerTier.DRACONIC);
            } else if (stack.getItem() == DEContent.core_chaotic) {
                if (spawnerTier.get() == SpawnerTier.CHAOTIC) return false;
                spawnerTier.set(SpawnerTier.CHAOTIC);
            } else {
                return false;
            }

            ItemStack dropStack = ItemStack.EMPTY;
            switch (prevTier) {
                case BASIC:
                    dropStack = new ItemStack(DEContent.core_draconium);
                    break;
                case WYVERN:
                    dropStack = new ItemStack(DEContent.core_wyvern);
                    break;
                case DRACONIC:
                    dropStack = new ItemStack(DEContent.core_awakened);
                    break;
                case CHAOTIC:
                    dropStack = new ItemStack(DEContent.core_chaotic);
                    break;
            }
            if (!level.isClientSide && !player.getAbilities().instabuild) {
                ItemEntity entityItem = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, dropStack);
                entityItem.setDeltaMovement(entityItem.getDeltaMovement().x, 0.2, entityItem.getDeltaMovement().z);
                ;
                level.addFreshEntity(entityItem);
                InventoryUtils.consumeHeldItem(player, stack, hand);
            }
        }

        return false;
    }

    @Override
    public void writeToItemStack(CompoundTag compound, boolean willHarvest) {
        if (willHarvest) {
            mobSoul.set(ItemStack.EMPTY);
        }
        super.writeToItemStack(compound, willHarvest);
    }

    //region Render

    protected Entity getRenderEntity() {
        if (mobSoul.get().isEmpty()) {
            return null;
        }
        return DEContent.mob_soul.getRenderEntity(mobSoul.get());
    }

    //endregion

    //region Spawner Tier

    public enum SpawnerTier {
        BASIC(4, true, false),
        WYVERN(6, false, false),
        DRACONIC(8, false, true),
        CHAOTIC(12, false, true);

        private int spawnCount;
        private boolean requiresPlayer;
        private boolean ignoreSpawnReq;

        SpawnerTier(int spawnCount, boolean requiresPlayer, boolean ignoreSpawnReq) {
            this.spawnCount = spawnCount;
            this.requiresPlayer = requiresPlayer;
            this.ignoreSpawnReq = ignoreSpawnReq;
        }

        public int getRandomSpawnDelay(Random random) {
            int min = getMinDelay();
            int max = getMaxDelay();
            return min + random.nextInt(max - min);
        }

        public int getMinDelay() {
            return DEConfig.spawnerDelays[ordinal() * 2];
        }

        public int getMaxDelay() {
            return DEConfig.spawnerDelays[(ordinal() * 2) + 1];
        }

        public int getSpawnCount() {
            return spawnCount;
        }

        public boolean ignoreSpawnReq() {
            return ignoreSpawnReq;
        }

        public boolean requiresPlayer() {
            return requiresPlayer;
        }

        public int getMaxCluster() {
            return (int) (spawnCount * 3D);
        }

        public static SpawnerTier getTierFromCore(ItemCore core) {
            return core == DEContent.core_chaotic ? CHAOTIC : core == DEContent.core_wyvern ? WYVERN : core == DEContent.core_awakened ? DRACONIC : SpawnerTier.BASIC;
        }
    }
    //endregion
}
