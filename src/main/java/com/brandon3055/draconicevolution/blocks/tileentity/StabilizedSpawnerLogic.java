package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 7/2/20.
 */
public class StabilizedSpawnerLogic extends AbstractSpawner {

    private TileStabilizedSpawner tile;
    private double mobRotation;
    private double prevMobRotation;
    private int spawnRange = 4;

    public StabilizedSpawnerLogic(TileStabilizedSpawner tile) {
        this.tile = tile;
    }

    @Nullable
    @Override
    public ResourceLocation getEntityId() {
        return new ResourceLocation(DEContent.mob_soul.getEntityString(tile.mobSoul.get()));
    }

    @Override
    public void setEntityId(EntityType<?> type) {}


    @Override
    public void tick() {
        if (!this.isNearPlayer()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            BlockPos blockpos = this.getPos();
            World world = this.getLevel();
            if (world.isClientSide) {
                double d3 = (float) blockpos.getX() + world.random.nextFloat();
                double d4 = (float) blockpos.getY() + world.random.nextFloat();
                double d5 = (float) blockpos.getZ() + world.random.nextFloat();
                world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

                if (tile.spawnDelay.get() > 0) {
                    tile.spawnDelay.dec();
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) tile.spawnDelay.get() + 200.0F))) % 360.0D;
            } else {
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
                    Entity entity = DEContent.mob_soul.createEntity(world, tile.mobSoul.get());

                    do {
                        double spawnX = (double) blockpos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                        double spawnY = blockpos.getY() + world.random.nextInt(3) - 1;
                        double spawnZ = (double) blockpos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                        entity.absMoveTo(spawnX, spawnY, spawnZ, 0, 0);
                    } while (entity.blockPosition().getX() == tile.getBlockPos().getX() && entity.blockPosition().getZ() == tile.getBlockPos().getZ());

                    int nearbyCount = world.getEntitiesOfClass(entity.getClass(), (new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1)).inflate(this.spawnRange)).size();
                    if (nearbyCount >= tier.getMaxCluster()) {
                        this.resetTimer();
                        return;
                    }

                    LivingEntity entityliving = entity instanceof LivingEntity ? (LivingEntity) entity : null;
                    entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || !(entityliving instanceof MobEntity) || canEntitySpawnSpawner((MobEntity)entityliving, getLevel(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), this)) {
                        if (!tier.requiresPlayer() && entity instanceof MobEntity) {
                            ((MobEntity) entity).setPersistenceRequired();
                            entity.getPersistentData().putLong("DESpawnedMob", System.currentTimeMillis());
                            DEEventHandler.onMobSpawnedBySpawner((MobEntity) entity);
                        }

                        if (!((ServerWorld)world).tryAddFreshEntityWithPassengers(entity)) {
                            this.resetTimer();
                            return;
                        }

                        world.levelEvent(2004, blockpos, 0);

                        if (entityliving != null) {
                            if (entity instanceof MobEntity) {
                                ((MobEntity)entity).spawnAnim();
                            }

                            if (tier == TileStabilizedSpawner.SpawnerTier.CHAOTIC) {
                                double velocity = 2.5;
                                entity.setDeltaMovement((world.random.nextDouble() - 0.5) * velocity,
                                        world.random.nextDouble() * velocity,
                                        (world.random.nextDouble() - 0.5) * velocity);
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
    }

    public boolean canEntitySpawnSpawner(MobEntity entity, World world, float x, float y, float z, AbstractSpawner spawner) {
        Event.Result result = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, spawner, SpawnReason.SPAWNER);
        if (result == Event.Result.DEFAULT) {
            return (tile.spawnerTier.get().ignoreSpawnReq() || entity.checkSpawnRules(world, SpawnReason.SPAWNER)) && entity.checkSpawnObstruction(world);
        } else {
            return result == Event.Result.ALLOW;
        }
    }

    private void resetTimer() {
        TileStabilizedSpawner.SpawnerTier tier = tile.spawnerTier.get();
        if (tier.getMaxDelay() <= tier.getMinDelay()) {
            tile.spawnDelay.set((short) tier.getMinDelay());
        } else {
            int i = tier.getMaxDelay() - tier.getMinDelay();
            tile.spawnDelay.set((short) (tier.getMinDelay() + this.getLevel().random.nextInt(i)));
        }

        this.broadcastEvent(1);
    }

    @Override
    public boolean onEventTriggered(int delay) {
        return false;
    }

    @Override
    public Entity getOrCreateDisplayEntity() {
        return tile.getRenderEntity();
    }

    @Override
    public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {}

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getSpin() {
        return mobRotation;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getoSpin() {
        return prevMobRotation;
    }

    @Nullable
    @Override
    public Entity getSpawnerEntity() {
        return super.getSpawnerEntity();
    }

    @Override
    public boolean isNearPlayer() {
        return tile.isActive();
    }

    @Override
    public void broadcastEvent(int id) {
        tile.getLevel().blockEvent(tile.getBlockPos(), Blocks.SPAWNER, id, 0);
    }

    @Override
    public World getLevel() {
        return tile.getLevel();
    }

    @Override
    public BlockPos getPos() {
        return tile.getBlockPos();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        return compound;
    }

    @Override
    public void load(CompoundNBT nbt) {}
}
