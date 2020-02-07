package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.draconicevolution.DEFeatures;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 7/2/20.
 */
public class StabilizedSpawnerLogic extends MobSpawnerBaseLogic {

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
        return new ResourceLocation(DEFeatures.mobSoul.getEntityString(tile.mobSoul.value));
    }

    @Override
    public void setEntityId(@Nullable ResourceLocation id) {}

    @Override
    public void updateSpawner() {
        if (!this.isActivated()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            BlockPos blockpos = this.getSpawnerPosition();

            if (this.getSpawnerWorld().isRemote) {
                double d3 = (float) blockpos.getX() + this.getSpawnerWorld().rand.nextFloat();
                double d4 = (float) blockpos.getY() + this.getSpawnerWorld().rand.nextFloat();
                double d5 = (float) blockpos.getZ() + this.getSpawnerWorld().rand.nextFloat();
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

                if (tile.spawnDelay.value > 0) {
                    --tile.spawnDelay.value;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) tile.spawnDelay.value+ 200.0F))) % 360.0D;
            } else {
                if (tile.spawnDelay.value == -1) {
                    this.resetTimer();
                }

                if (tile.spawnDelay.value > 0) {
                    --tile.spawnDelay.value;
                    return;
                }

                TileStabilizedSpawner.SpawnerTier tier = tile.spawnerTier.value;
                boolean flag = false;
                int successCount = 0;
                for (int i = 0; i < tier.getSpawnCount() + tier.ordinal() + 3; ++i) {

                    World world = this.getSpawnerWorld();
                    Entity entity = DEFeatures.mobSoul.createEntity(world, tile.mobSoul.value);

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

                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
                    entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || canEntitySpawnSpawner(entityliving, getSpawnerWorld(), (float) entity.posX, (float) entity.posY, (float) entity.posZ, this)) {
                        AnvilChunkLoader.spawnEntity(entity, world);
                        world.playEvent(2004, blockpos, 0);

                        if (entityliving != null) {
                            entityliving.spawnExplosionParticle();

                            if (tier == TileStabilizedSpawner.SpawnerTier.CHAOTIC) {
                                double velocity = 2.5;
                                entity.motionX = (world.rand.nextDouble() - 0.5) * velocity;
                                entity.motionY = world.rand.nextDouble() * velocity;
                                entity.motionZ = (world.rand.nextDouble() - 0.5) * velocity;
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

    public boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner) {
        Event.Result result = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, spawner);
        if (result == Event.Result.DEFAULT) {
            return (tile.spawnerTier.value.ignoreSpawnReq() || entity.getCanSpawnHere()) && entity.isNotColliding();
        } else {
            return result == Event.Result.ALLOW;
        }
    }

    private void resetTimer() {
        TileStabilizedSpawner.SpawnerTier tier = tile.spawnerTier.value;
        if (tier.getMaxDelay() <= tier.getMinDelay()) {
            tile.spawnDelay.value = (short) tier.getMinDelay();
        } else {
            int i = tier.getMaxDelay() - tier.getMinDelay();
            tile.spawnDelay.value = (short) (tier.getMinDelay() + this.getSpawnerWorld().rand.nextInt(i));
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

    @SideOnly(Side.CLIENT)
    @Override
    public double getMobRotation() {
        return mobRotation;
    }

    @SideOnly(Side.CLIENT)
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
        tile.getWorld().addBlockEvent(tile.getPos(), Blocks.MOB_SPAWNER, id, 0);
    }

    @Override
    public World getSpawnerWorld() {
        return tile.getWorld();
    }

    @Override
    public BlockPos getSpawnerPosition() {
        return tile.getPos();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound p_189530_1_) {
        return p_189530_1_;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}
}
