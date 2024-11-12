package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 7/2/20.
 */
public class StabilizedSpawnerLogic extends BaseSpawner {

    private TileStabilizedSpawner tile;
    private double mobRotation;
    private double prevMobRotation;
    private int spawnRange = 4;

    public StabilizedSpawnerLogic(TileStabilizedSpawner tile) {
        this.tile = tile;
    }

    @Override
    public void clientTick(Level level, BlockPos pos) {
        if (!this.isNearPlayer(level, pos)) {
            this.prevMobRotation = this.mobRotation;
        } else {
            double d3 = (float) pos.getX() + level.random.nextFloat();
            double d4 = (float) pos.getY() + level.random.nextFloat();
            double d5 = (float) pos.getZ() + level.random.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

            if (tile.spawnDelay.get() > 0) {
                tile.spawnDelay.dec();
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) tile.spawnDelay.get() + 200.0F))) % 360.0D;
        }
    }

    @Override
    public void serverTick(ServerLevel level, BlockPos pos) {
        if (this.isNearPlayer(level, pos)) {
            if (tile.spawnDelay.get() == -1) {
                this.resetTimer();
            }

            if (tile.spawnDelay.get() > 0) {
                tile.spawnDelay.dec();
                return;
            }

            TileStabilizedSpawner.SpawnerTier tier = tile.spawnerTier.get();
            boolean flag = false;
            int successCount = 0;
            for (int i = 0; i < tier.getSpawnCount() + tier.ordinal() + 3; ++i) {
                Entity entity = DEContent.MOB_SOUL.get().createEntity(level, tile.mobSoul.get());

                do {
                    double spawnX = (double) pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double spawnY = pos.getY() + level.random.nextInt(3) - 1;
                    double spawnZ = (double) pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    entity.absMoveTo(spawnX, spawnY, spawnZ, 0, 0);
                } while (entity.blockPosition().getX() == tile.getBlockPos().getX() && entity.blockPosition().getZ() == tile.getBlockPos().getZ());

                int nearbyCount = level.getEntitiesOfClass(entity.getClass(), (new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).inflate(this.spawnRange)).size();
                if (nearbyCount >= tier.getMaxCluster()) {
                    this.resetTimer();
                    return;
                }

                LivingEntity entityliving = entity instanceof LivingEntity ? (LivingEntity) entity : null;
                entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), level.random.nextFloat() * 360.0F, 0.0F);

                if (entityliving == null || !(entityliving instanceof Mob) || canEntitySpawnSpawner((Mob) entityliving, (ServerLevel) tile.getLevel(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), this)) {
                    if (!tier.requiresPlayer() && entity instanceof Mob) {
                        ((Mob) entity).setPersistenceRequired();
                        entity.getPersistentData().putLong("DESpawnedMob", System.currentTimeMillis());
                        DEEventHandler.onMobSpawnedBySpawner((Mob) entity);
                    }

                    if (!level.tryAddFreshEntityWithPassengers(entity)) {
                        this.resetTimer();
                        return;
                    }

                    level.levelEvent(2004, pos, 0);

                    if (entityliving != null) {
                        if (entity instanceof Mob) {
                            ((Mob) entity).spawnAnim();
                        }

                        if (tier == TileStabilizedSpawner.SpawnerTier.CHAOTIC) {
                            double velocity = 2.5;
                            entity.setDeltaMovement((level.random.nextDouble() - 0.5) * velocity,
                                    level.random.nextDouble() * velocity,
                                    (level.random.nextDouble() - 0.5) * velocity);
                        }
                    }

                    flag = true;
                    successCount++;
                }
                if (successCount >= tier.getSpawnCount()) {
                    break;
                }
            }

            if (flag) {
                this.resetTimer();
            }
        }
    }

    public boolean canEntitySpawnSpawner(Mob entity, ServerLevel level, float x, float y, float z, BaseSpawner spawner) {
        var event = new MobSpawnEvent.PositionCheck(entity, level, MobSpawnType.SPAWNER, null);
        NeoForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DEFAULT) {
            return (tile.spawnerTier.get().ignoreSpawnReq() || entity.checkSpawnRules(level, MobSpawnType.SPAWNER)) && entity.checkSpawnObstruction(level);
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    private void resetTimer() {
        TileStabilizedSpawner.SpawnerTier tier = tile.spawnerTier.get();
        if (tier.getMaxDelay() <= tier.getMinDelay()) {
            tile.spawnDelay.set((short) tier.getMinDelay());
        } else {
            int i = tier.getMaxDelay() - tier.getMinDelay();
            tile.spawnDelay.set((short) (tier.getMinDelay() + tile.getLevel().random.nextInt(i)));
        }

        this.broadcastEvent(tile.getLevel(), tile.getBlockPos(), 1);
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public double getSpin() {
        return mobRotation;
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public double getoSpin() {
        return prevMobRotation;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        return compound;
    }

    @Override
    public void load(@Nullable Level level, BlockPos pos, CompoundTag tag) {
    }

    @Nullable
    @Override
    public BlockEntity getSpawnerBlockEntity() {
        return tile;
    }

    @Override
    public boolean onEventTriggered(Level level, int delay) {
        return false;
    }

    @Nullable
    @Override
    public Entity getOrCreateDisplayEntity(Level p_254323_, BlockPos p_254313_) {
        return tile.getRenderEntity();
    }

    @Override
    public void broadcastEvent(Level level, BlockPos blockPos, int event) {
        level.blockEvent(blockPos, Blocks.SPAWNER, event, 0);
    }

    @Override
    public boolean isNearPlayer(Level p_151344_, BlockPos p_151345_) {
        return tile.isActive();
    }
}
