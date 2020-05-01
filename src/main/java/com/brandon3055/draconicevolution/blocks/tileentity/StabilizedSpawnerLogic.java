package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public void setEntityType(EntityType<?> type) {}


    @Override
    public void tick() {
        if (!this.isActivated()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            BlockPos blockpos = this.getSpawnerPosition();
            World world = this.getWorld();
            if (world.isRemote) {
                double d3 = (float) blockpos.getX() + world.rand.nextFloat();
                double d4 = (float) blockpos.getY() + world.rand.nextFloat();
                double d5 = (float) blockpos.getZ() + world.rand.nextFloat();
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
                        double spawnX = (double) blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
                        double spawnY = blockpos.getY() + world.rand.nextInt(3) - 1;
                        double spawnZ = (double) blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
                        entity.setPositionAndRotation(spawnX, spawnY, spawnZ, 0, 0);
                    } while (entity.getPosition().getX() == tile.getPos().getX() && entity.getPosition().getZ() == tile.getPos().getZ());

                    int nearbyCount = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1)).grow(this.spawnRange)).size();
                    if (nearbyCount >= tier.getMaxCluster()) {
                        this.resetTimer();
                        return;
                    }

                    LivingEntity entityliving = entity instanceof LivingEntity ? (LivingEntity) entity : null;
                    entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || !(entityliving instanceof MobEntity) || canEntitySpawnSpawner((MobEntity)entityliving, getWorld(), (float) entity.posX, (float) entity.posY, (float) entity.posZ, this)) {
                        if (!tier.requiresPlayer() && entity instanceof MobEntity) {
                            ((MobEntity) entity).enablePersistence();
                            entity.getPersistentData().putLong("DESpawnedMob", System.currentTimeMillis());
                            DEEventHandler.onMobSpawnedBySpawner((MobEntity) entity);
                        }

                        func_221409_a(entity);
                        world.playEvent(2004, blockpos, 0);

                        if (entityliving != null) {
                            if (entity instanceof MobEntity) {
                                ((MobEntity)entity).spawnExplosionParticle();
                            }

                            if (tier == TileStabilizedSpawner.SpawnerTier.CHAOTIC) {
                                double velocity = 2.5;
                                entity.setMotion((world.rand.nextDouble() - 0.5) * velocity,
                                        world.rand.nextDouble() * velocity,
                                        (world.rand.nextDouble() - 0.5) * velocity);
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
            return (tile.spawnerTier.get().ignoreSpawnReq() || entity.canSpawn(world, SpawnReason.SPAWNER)) && entity.isNotColliding(world);
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
            tile.spawnDelay.set((short) (tier.getMinDelay() + this.getWorld().rand.nextInt(i)));
        }

        this.broadcastEvent(1);
    }

    @Override
    public boolean setDelayToMin(int delay) {
        return false;
    }

    @Override
    public Entity getCachedEntity() {
        return tile.getRenderEntity();
    }

    @Override
    public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {}

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getMobRotation() {
        return mobRotation;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getPrevMobRotation() {
        return prevMobRotation;
    }

    @Nullable
    @Override
    public Entity getSpawnerEntity() {
        return super.getSpawnerEntity();
    }

    @Override
    public boolean isActivated() {
        return tile.isActive();
    }

    @Override
    public void broadcastEvent(int id) {
        tile.getWorld().addBlockEvent(tile.getPos(), Blocks.SPAWNER, id, 0);
    }

    @Override
    public World getWorld() {
        return tile.getWorld();
    }

    @Override
    public BlockPos getSpawnerPosition() {
        return tile.getPos();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return compound;
    }

    @Override
    public void read(CompoundNBT nbt) {}
}
