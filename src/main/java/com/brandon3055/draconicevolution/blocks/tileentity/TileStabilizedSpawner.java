package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.items.ItemCore;
import com.brandon3055.draconicevolution.items.MobSoul;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileStabilizedSpawner extends TileBCBase implements ITickable, IActivatableTile, IChangeListener {

    public ManagedEnum<SpawnerTier> spawnerTier = register("spawnerTier", new ManagedEnum<>(SpawnerTier.BASIC)).saveToTile().saveToItem().syncViaTile().finish();
    public ManagedStack mobSoul = register("mobSoul", new ManagedStack(ItemStack.EMPTY)).saveToTile().saveToItem().syncViaTile().finish();
    public ManagedBool isPowered = register("isPowered", new ManagedBool(false)).saveToTile().syncViaTile().finish();
    public ManagedShort spawnDelay = register("spawnDelay", new ManagedShort(100)).saveToTile().syncViaTile().finish();
    public ManagedInt startSpawnDelay = register("startSpawnDelay", new ManagedInt(100)).saveToTile().syncViaTile().finish();
    public StabilizedSpawnerLogic spawnerLogic = new StabilizedSpawnerLogic(this);

    private int activatingRangeFromPlayer = 24;
//    private int spawnRange = 4;

    //region Render Fields

    public double mobRotation;

    //endregion


    @Override
    public void update() {
        super.update();
        spawnerLogic.updateSpawner();
//        if (!isActive()) {
//            return;
//        }
//
//        if (world.isRemote) {
//            mobRotation += getRotationSpeed();
//
//            double d3 = (double) ((float) pos.getX() + world.rand.nextFloat());
//            double d4 = (double) ((float) pos.getY() + world.rand.nextFloat());
//            double d5 = (double) ((float) pos.getZ() + world.rand.nextFloat());
//            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
//            world.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
//        }
//        else {
//            if (spawnDelay.value == -1) {
//                resetTimer();
//            }
//
//            if (spawnDelay.value > 60) spawnDelay.value = 60;
//
//            if (spawnDelay.value > 0) {
//                spawnDelay.value--;
//                return;
//            }
//
//            boolean spawnedMob = false;
//
//            for (int i = 0; i < spawnerTier.value.getSpawnCount(); i++) {
//                double spawnX = pos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
//                double spawnY = pos.getY() + world.rand.nextInt(3) - 1;
//                double spawnZ = pos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
//                Entity entity = DEFeatures.mobSoul.createEntity(world, mobSoul.value);
//                entity.setPositionAndRotation(spawnX, spawnY, spawnZ, 0, 0);
//
//                int nearby = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1))).grow(spawnRange)).size();
//
//                if (nearby >= spawnerTier.value.getMaxCluster()) {
//                    this.resetTimer();
//                    return;
//                }
//
//                EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
//                entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
//
//                boolean canSpawn;
//                if (spawnerTier.value.ignoreSpawnReq) {
//                    Event.Result result = ForgeEventFactory.canEntitySpawn(entityliving, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ, baseLogic);
//                    canSpawn = isNotColliding(entity) && (result == Event.Result.DEFAULT || result == Event.Result.ALLOW);
//                }
//                else {
//                    canSpawn = canEntitySpawnSpawner(entityliving, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
//                }
//
//                if (canSpawn) {
//                    if (!spawnerTier.value.requiresPlayer && entity instanceof EntityLiving) {
//                        ((EntityLiving) entity).enablePersistence();
//                        entity.getEntityData().setLong("DESpawnedMob", System.currentTimeMillis()); //Leaving this in case some mod wants to use it.
//                        DEEventHandler.onMobSpawnedBySpawner((EntityLiving) entity);
//                    }
//                    AnvilChunkLoader.spawnEntity(entity, world);
//                    world.playEvent(2004, pos, 0);
//                    if (entityliving != null) {
//                        entityliving.spawnExplosionParticle();
//
//                        if (spawnerTier.value == SpawnerTier.CHAOTIC) {
//                            double velocity = 2.5;
//                            entity.motionX = (world.rand.nextDouble() - 0.5) * velocity;
//                            entity.motionY = world.rand.nextDouble() * velocity;
//                            entity.motionZ = (world.rand.nextDouble() - 0.5) * velocity;
//                        }
//                    }
//
//                    spawnedMob = true;
//                }
//            }
//
//            if (spawnedMob) {
//                resetTimer();
//            }
//        }

    }

//    private boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z) {
//        Event.Result result = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, true);
//        if (result == Event.Result.DEFAULT) {
//            boolean isSlime = entity instanceof EntitySlime;
//            return (isSlime ||entity.getCanSpawnHere()) && entity.isNotColliding();
//        }
//        else return result == Event.Result.ALLOW;
//    }

//    public boolean isNotColliding(Entity entity) {
//        return !world.containsAnyLiquid(entity.getEntityBoundingBox()) && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity);
//    }

//    private void resetTimer() {
//        spawnDelay.value = (short) Math.min(spawnerTier.value.getRandomSpawnDelay(world.rand), Short.MAX_VALUE);
//        startSpawnDelay.value = spawnDelay.value;
//    }

    public boolean isActive() {
        if (isPowered.value || mobSoul.value.isEmpty()) {
            return false;
        }
        else if (spawnerTier.value.requiresPlayer && !world.isAnyPlayerWithinRangeAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, (double) this.activatingRangeFromPlayer)) {
            return false;
        }
        return true;
    }

    @Override
    public void onNeighborChange(BlockPos changePos) {
        isPowered.value = world.isBlockPowered(pos);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == DEFeatures.mobSoul) {
            if (!world.isRemote) {
                (mobSoul.value = stack.copy()).setCount(1);
                if (!player.isCreative()) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
            return true;
        }
        else if (stack.getItem() == Items.SPAWN_EGG) {
            if (player.capabilities.isCreativeMode) {
                NBTTagCompound compound = stack.getSubCompound("EntityTag");
                if (compound != null && compound.hasKey("id")) {
                    String name = compound.getString("id");
                    ItemStack soul = new ItemStack(DEFeatures.mobSoul);
                    DEFeatures.mobSoul.setEntity(MobSoul.getCachedRegName(name), soul);
                    mobSoul.value = soul;
                    if (!player.isCreative()) {
                        InventoryUtils.consumeHeldItem(player, stack, hand);
                    }
                }
            }
            return true;
        }
        else if (!stack.isEmpty()) {
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

            ItemStack dropStack = ItemStack.EMPTY;
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
            if (!world.isRemote) {
                EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, dropStack);
                entityItem.motionY = 0.2;
                world.spawnEntity(entityItem);
                InventoryUtils.consumeHeldItem(player, stack, hand);
            }
        }

        return false;
    }

    @Override
    public void writeToItemStack(NBTTagCompound tileCompound, boolean willHarvest) {
        if (willHarvest) {
            mobSoul.value = ItemStack.EMPTY;
        }
        super.writeToItemStack(tileCompound, willHarvest);
    }

    //region Render

    protected Entity getRenderEntity() {
        if (mobSoul.value.isEmpty()) {
            return null;
        }
        return DEFeatures.mobSoul.getRenderEntity(mobSoul.value);
    }

//    public double getRotationSpeed() {
//        return isActive() ? 0.5 + (1D - ((double) spawnDelay.value / (double) startSpawnDelay.value)) * 4.5 : 0;
//    }

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
            return core == DEFeatures.chaoticCore ? CHAOTIC : core == DEFeatures.wyvernCore ? WYVERN : core == DEFeatures.awakenedCore ? DRACONIC : BASIC;
        }
    }
    //endregion
}
