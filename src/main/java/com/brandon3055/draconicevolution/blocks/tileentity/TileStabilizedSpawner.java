package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.IDataRetainerTile;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.network.wrappers.*;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.items.ItemCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileStabilizedSpawner extends TileBCBase implements ITickable, IActivatableTile, IChangeListener, IDataRetainerTile {

    public SyncableEnum<SpawnerTier> spawnerTier = new SyncableEnum<>(SpawnerTier.BASIC, true, false);
    public SyncableStack mobSoul = new SyncableStack(null, true, false);
    public SyncableBool isPowered = new SyncableBool(false, true, false);
    public SyncableShort spawnDelay = new SyncableShort((short) 100, true, false);
    public SyncableInt startSpawnDelay = new SyncableInt(100, true, false);

    private int activatingRangeFromPlayer = 24;
    private int spawnRange = 4;

    //region Render Fields

    public double mobRotation;

    //endregion

    public TileStabilizedSpawner() {
        registerSyncableObject(spawnerTier, true, true);
        registerSyncableObject(mobSoul, true, true);
        registerSyncableObject(isPowered);
        registerSyncableObject(spawnDelay);
        registerSyncableObject(startSpawnDelay);
    }

    @Override
    public void update() {
        detectAndSendChanges();

        if (!isActive()) {
            return;
        }

        if (worldObj.isRemote) {
            mobRotation += getRotationSpeed();

            double d3 = (double)((float)pos.getX() + worldObj.rand.nextFloat());
            double d4 = (double)((float)pos.getY() + worldObj.rand.nextFloat());
            double d5 = (double)((float)pos.getZ() + worldObj.rand.nextFloat());
            worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);
            worldObj.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);
        }
        else {
            if (spawnDelay.value == -1) {
                resetTimer();
            }

            if (spawnDelay.value > 0) {
                spawnDelay.value--;
                return;
            }

            boolean spawnedMob = false;

            for (int i = 0; i < spawnerTier.value.getSpawnCount(); i++) {
                double spawnX = pos.getX() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
                double spawnY = pos.getY() + worldObj.rand.nextInt(3) - 1;
                double spawnZ = pos.getZ() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
                Entity entity = DEFeatures.mobSoul.createEntity(worldObj, mobSoul.value);
                entity.setPositionAndRotation(spawnX, spawnY, spawnZ, 0, 0);

                int nearby = worldObj.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1))).expandXyz(spawnRange)).size();

                if (nearby >= spawnerTier.value.getMaxCluster()) {
                    this.resetTimer();
                    return;
                }

                EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
                entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);

                boolean canSpawn;
                if (spawnerTier.value.ignoreSpawnReq) {
                    Event.Result result = ForgeEventFactory.canEntitySpawn(entityliving, worldObj, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
                    canSpawn = isNotColliding(entity) && (result == Event.Result.DEFAULT || result == Event.Result.ALLOW);
                }
                else {
                    canSpawn = ForgeEventFactory.canEntitySpawnSpawner(entityliving, worldObj, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }

                if (canSpawn) {
                    if (!spawnerTier.value.requiresPlayer && entity instanceof EntityLiving) {
                        ((EntityLiving) entity).enablePersistence();
                        entity.getEntityData().setLong("DESpawnedMob", System.currentTimeMillis()); //Leaving this in case some mod wants to use it.
                        DEEventHandler.onMobSpawnedBySpawner((EntityLiving) entity);
                    }
                    AnvilChunkLoader.spawnEntity(entity, worldObj);
                    worldObj.playEvent(2004, pos, 0);
                    if (entityliving != null) {
                        entityliving.spawnExplosionParticle();

                        if (spawnerTier.value == SpawnerTier.CHAOTIC) {
                            double velocity = 2.5;
                            entity.motionX = (worldObj.rand.nextDouble() - 0.5) * velocity;
                            entity.motionY = worldObj.rand.nextDouble() * velocity;
                            entity.motionZ = (worldObj.rand.nextDouble() - 0.5) * velocity;
                        }
                    }

                    spawnedMob = true;
                }
            }

            if (spawnedMob) {
                resetTimer();
            }
        }

    }

    public boolean isNotColliding(Entity entity)
    {
        return !worldObj.containsAnyLiquid(entity.getEntityBoundingBox()) && worldObj.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && worldObj.checkNoEntityCollision(entity.getEntityBoundingBox(), entity);
    }

    private void resetTimer() {
        spawnDelay.value = (short) Math.min(spawnerTier.value.getRandomSpawnDelay(worldObj.rand), Short.MAX_VALUE);
    }

    private boolean isActive() {
        if (isPowered.value || mobSoul.value == null) {
            return false;
        }
        else if (spawnerTier.value.requiresPlayer && !worldObj.isAnyPlayerWithinRangeAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, (double) this.activatingRangeFromPlayer)) {
            return false;
        }
        return true;
    }

    @Override
    public void onNeighborChange() {
        isPowered.value = worldObj.isBlockPowered(pos);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (stack != null && stack.getItem() == DEFeatures.mobSoul) {
            if (!worldObj.isRemote) {
                (mobSoul.value = stack.copy()).stackSize = 1;
                if (!player.isCreative()) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
            return true;
        }
        else if (stack != null && stack.getItem() == Items.SPAWN_EGG) {
            NBTTagCompound compound = stack.getSubCompound("EntityTag", false);
            if (compound != null && compound.hasKey("id")) {
                String name = compound.getString("id");
                ItemStack soul = new ItemStack(DEFeatures.mobSoul);
                DEFeatures.mobSoul.setEntityString(name, soul);
                mobSoul.value = soul;
                if (!player.isCreative()) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
            return true;
        }
        else if (stack != null) {
            SpawnerTier prevTier = spawnerTier.value;
            if (stack.getItem() == DEFeatures.draconicCore) {
                if (spawnerTier.value == SpawnerTier.BASIC) return false;
                spawnerTier.value = SpawnerTier.BASIC;
            }
            else if (stack.getItem() == DEFeatures.wyvernCore) {
                if (spawnerTier.value == SpawnerTier.WYVERN) return false;
                spawnerTier.value = SpawnerTier.WYVERN;
            }
            else if (stack.getItem() == DEFeatures.awakenedCore) {
                if (spawnerTier.value == SpawnerTier.DRACONIC) return false;
                spawnerTier.value = SpawnerTier.DRACONIC;
            }
            else if (stack.getItem() == DEFeatures.chaoticCore) {
                if (spawnerTier.value == SpawnerTier.CHAOTIC) return false;
                spawnerTier.value = SpawnerTier.CHAOTIC;
            }
            else {
                return false;
            }

            ItemStack dropStack = null;
            switch (prevTier) {
                case BASIC:
                    dropStack = new ItemStack(DEFeatures.draconicCore);
                    break;
                case WYVERN:
                    dropStack = new ItemStack(DEFeatures.wyvernCore);
                    break;
                case DRACONIC:
                    dropStack = new ItemStack(DEFeatures.awakenedCore);
                    break;
                case CHAOTIC:
                    dropStack = new ItemStack(DEFeatures.chaoticCore);
                    break;
            }
            if (!worldObj.isRemote) {
                EntityItem entityItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, dropStack);
                entityItem.motionY = 0.2;
                worldObj.spawnEntityInWorld(entityItem);
                InventoryUtils.consumeHeldItem(player, stack, hand);
            }
        }

        return false;
    }

    @Override
    public void onHarvested(ItemStack stack) {
        mobSoul.value = null;
    }

    //region Render

    public Entity getRenderEntity() {
        if (mobSoul.value == null) {
            return null;
        }
        return DEFeatures.mobSoul.getRenderEntity(mobSoul.value);
    }

    public double getRotationSpeed() {
        return isActive() ? 0.5 + (1D - ((double) spawnDelay.value / (double) startSpawnDelay.value)) * 4.5 : 0;
    }

    //endregion

    //region Spawner Tier

    public enum SpawnerTier {
        BASIC(4, true, false), WYVERN(6, false, false), DRACONIC(8, false, true), CHAOTIC(12, false, true);

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

        public int getMaxCluster() {
            return (int) (spawnCount * 2.5D);
        }

        public static SpawnerTier getTierFromCore(ItemCore core) {
            return core == DEFeatures.chaoticCore ? CHAOTIC : core == DEFeatures.wyvernCore ? WYVERN : core == DEFeatures.awakenedCore ? DRACONIC : BASIC;
        }
    }
    //endregion
}
