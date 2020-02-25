package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.items.ItemCore;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

import java.util.Random;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileStabilizedSpawner extends TileBCore implements ITickableTileEntity, IActivatableTile, IChangeListener {

    public TileStabilizedSpawner() {
        super(DEContent.tile_stabilized_spawner);
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return false;
    }

    //    public ManagedEnum<SpawnerTier> spawnerTier = register(new ManagedEnum<>("spawner_tier", BASIC, SAVE_BOTH_SYNC_TILE));
//    public ManagedStack mobSoul = register(new ManagedStack("mob_soul", SAVE_BOTH_SYNC_TILE));
//    public ManagedBool isPowered = register(new ManagedBool("is_powered", SAVE_NBT_SYNC_TILE));
//    public ManagedShort spawnDelay = register(new ManagedShort("spawn_delay", (short) 100, SAVE_NBT_SYNC_TILE));
//    public ManagedInt startSpawnDelay = register(new ManagedInt("start_spawn_delay", 100, SAVE_NBT_SYNC_TILE));
//    private MobSpawnerBaseLogic dummyLogic = new MobSpawnerBaseLogic() {
//        @Override
//        public void broadcastEvent(int id) {
//
//        }
//
//        @Override
//        public World getSpawnerWorld() {
//            return world;
//        }
//
//        @Override
//        public BlockPos getSpawnerPosition() {
//            return pos;
//        }
//
//        @Nullable
//        @Override
//        public ResourceLocation getEntityId() {
//            return new ResourceLocation(DEFeatures.mobSoul.getEntityString(mobSoul.get()));
//        }
//
//        @Override
//        public void updateSpawner() {
//        }
//
//        @Override
//        public void setEntityId(@Nullable ResourceLocation id) {
//        }
//
//        @Override
//        public void readFromNBT(CompoundNBT nbt) {
//        }
//
//        @Override
//        public CompoundNBT writeToNBT(CompoundNBT p_189530_1_) {
//            return p_189530_1_;
//        }
//
//        @Override
//        public boolean setDelayToMin(int delay) {
//            return false;
//        }
//
//        @Override
//        public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
//        }
//    };
//
//    private int activatingRangeFromPlayer = 24;
//    private int spawnRange = 4;
//
//    //region Render Fields
//
//    public double mobRotation;
//
//    //endregion
//
//
//    @Override
//    public void update() {
//        super.update();
//
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
//            if (spawnDelay.get() == -1) {
//                resetTimer();
//            }
//
//            if (spawnDelay.get() > 0) {
//                spawnDelay.dec();
//                return;
//            }
//
//            boolean spawnedMob = false;
//
//            for (int i = 0; i < spawnerTier.get().getSpawnCount(); i++) {
//                double spawnX = pos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
//                double spawnY = pos.getY() + world.rand.nextInt(3) - 1;
//                double spawnZ = pos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double) this.spawnRange + 0.5D;
//                Entity entity = DEFeatures.mobSoul.createEntity(world, mobSoul.get());
//                entity.setPositionAndRotation(spawnX, spawnY, spawnZ, 0, 0);
//
//                int nearby = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1))).grow(spawnRange)).size();
//
//                if (nearby >= spawnerTier.get().getMaxCluster()) {
//                    this.resetTimer();
//                    return;
//                }
//
//                EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
//                entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
//
//                boolean canSpawn;
//                if (spawnerTier.get().ignoreSpawnReq) {
//                    Event.Result result = ForgeEventFactory.canEntitySpawn(entityliving, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ, dummyLogic);
//                    canSpawn = isNotColliding(entity) && (result == Event.Result.DEFAULT || result == Event.Result.ALLOW);
//                }
//                else {
//                    canSpawn = canEntitySpawnSpawner(entityliving, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
//                }
//
//                if (canSpawn) {
//                    if (!spawnerTier.get().requiresPlayer && entity instanceof EntityLiving) {
//                        ((EntityLiving) entity).enablePersistence();
//                        entity.getEntityData().setLong("DESpawnedMob", System.currentTimeMillis()); //Leaving this in case some mod wants to use it.
//                        DEEventHandler.onMobSpawnedBySpawner((EntityLiving) entity);
//                    }
//                    AnvilChunkLoader.spawnEntity(entity, world);
//                    world.playEvent(2004, pos, 0);
//                    if (entityliving != null) {
//                        entityliving.spawnExplosionParticle();
//
//                        if (spawnerTier.get() == SpawnerTier.CHAOTIC) {
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
//
//    }
//
//    private boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z) {
//        Event.Result result = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, true);
//        if (result == Event.Result.DEFAULT) {
//            boolean isSlime = entity instanceof EntitySlime;
//            return (isSlime || entity.getCanSpawnHere()) && entity.isNotColliding();
//        }
//        else return result == Event.Result.ALLOW;
//    }
//
//    public boolean isNotColliding(Entity entity) {
//        return !world.containsAnyLiquid(entity.getEntityBoundingBox()) && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity);
//    }
//
//    private void resetTimer() {
//        spawnDelay.set((short) Math.min(spawnerTier.get().getRandomSpawnDelay(world.rand), Short.MAX_VALUE));
//        startSpawnDelay.set(spawnDelay.get());
//    }
//
//    private boolean isActive() {
//        if (isPowered.get() || mobSoul.get().isEmpty()) {
//            return false;
//        }
//        else if (spawnerTier.get().requiresPlayer && !world.isAnyPlayerWithinRangeAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, (double) this.activatingRangeFromPlayer)) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onNeighborChange(BlockPos changePos) {
//        isPowered.set(world.isBlockPowered(pos));
//    }
//
//    @Override
//    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        ItemStack stack = player.getHeldItem(hand);
//        if (stack.getItem() == DEFeatures.mobSoul) {
//            if (!world.isRemote) {
//                (mobSoul.set(stack.copy())).setCount(1);
//                if (!player.isCreative()) {
//                    InventoryUtils.consumeHeldItem(player, stack, hand);
//                }
//            }
//            return true;
//        }
//        else if (stack.getItem() == Items.SPAWN_EGG) {
//            if (player.capabilities.isCreativeMode) {
//                CompoundNBT compound = stack.getSubCompound("EntityTag");
//                if (compound != null && compound.hasKey("id")) {
//                    String name = compound.getString("id");
//                    ItemStack soul = new ItemStack(DEFeatures.mobSoul);
//                    DEFeatures.mobSoul.setEntity(MobSoul.getCachedRegName(name), soul);
//                    mobSoul.set(soul);
//                    if (!player.isCreative()) {
//                        InventoryUtils.consumeHeldItem(player, stack, hand);
//                    }
//                }
//            }
//            return true;
//        }
//        else if (!stack.isEmpty()) {
//            SpawnerTier prevTier = spawnerTier.get();
//            if (stack.getItem() == DEFeatures.draconicCore) {
//                if (spawnerTier.get() == BASIC) return false;
//                spawnerTier.set(BASIC);
//            }
//            else if (stack.getItem() == DEFeatures.wyvernCore) {
//                if (spawnerTier.get() == SpawnerTier.WYVERN) return false;
//                spawnerTier.set(SpawnerTier.WYVERN);
//            }
//            else if (stack.getItem() == DEFeatures.awakenedCore) {
//                if (spawnerTier.get() == SpawnerTier.DRACONIC) return false;
//                spawnerTier.set(SpawnerTier.DRACONIC);
//            }
//            else if (stack.getItem() == DEFeatures.chaoticCore) {
//                if (spawnerTier.get() == SpawnerTier.CHAOTIC) return false;
//                spawnerTier.set(SpawnerTier.CHAOTIC);
//            }
//            else {
//                return false;
//            }
//
//            ItemStack dropStack = ItemStack.EMPTY;
//            switch (prevTier) {
//                case BASIC:
//                    dropStack = new ItemStack(DEFeatures.draconicCore);
//                    break;
//                case WYVERN:
//                    dropStack = new ItemStack(DEFeatures.wyvernCore);
//                    break;
//                case DRACONIC:
//                    dropStack = new ItemStack(DEFeatures.awakenedCore);
//                    break;
//                case CHAOTIC:
//                    dropStack = new ItemStack(DEFeatures.chaoticCore);
//                    break;
//            }
//            if (!world.isRemote) {
//                ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, dropStack);
//                entityItem.motionY = 0.2;
//                world.spawnEntity(entityItem);
//                InventoryUtils.consumeHeldItem(player, stack, hand);
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public void writeToItemStack(CompoundNBT compound, boolean willHarvest) {
//        if (willHarvest) {
//            mobSoul.set(ItemStack.EMPTY);
//        }
//        super.writeToItemStack(compound, willHarvest);
//    }
//
//    //region Render
//
//    public Entity getRenderEntity() {
//        if (mobSoul.get().isEmpty()) {
//            return null;
//        }
//        return DEFeatures.mobSoul.getRenderEntity(mobSoul.get());
//    }
//
//    public double getRotationSpeed() {
//        return isActive() ? 0.5 + (1D - ((double) spawnDelay.get() / (double) startSpawnDelay.get())) * 4.5 : 0;
//    }
//
//    //endregion
//
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

        public int getMaxCluster() {
            return (int) (spawnCount * 2.5D);
        }

        public static SpawnerTier getTierFromCore(ItemCore core) {
            return core == DEContent.core_chaotic ? CHAOTIC : core == DEContent.core_wyvern ? WYVERN : core == DEContent.core_awakened ? DRACONIC : BASIC;
        }
    }
    //endregion
}
