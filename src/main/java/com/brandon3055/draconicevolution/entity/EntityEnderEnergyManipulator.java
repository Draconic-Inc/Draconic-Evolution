package com.brandon3055.draconicevolution.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by brandon3055 on 10/05/2017.
 */
public class EntityEnderEnergyManipulator extends LivingEntity {

    private int soulsCollected = 0;
    private int stageTime = 0;
    public EnderDragonEntity dragon = null;
    private DragonFightManager fightManager = null;
    private LinkedList<BlockPos> deadCrystals = new LinkedList<>();
    public static final DataParameter<Integer> STAGE = EntityDataManager.createKey(EntityEnderEnergyManipulator.class, DataSerializers.VARINT);
    private BlockPos exitPortalLocation = null;

    public EntityEnderEnergyManipulator(EntityType<? extends LivingEntity> type, World p_i48577_2_) {
        super(type, p_i48577_2_);
    }

//    public EntityEnderEnergyManipulator(World worldIn) {
//        super(worldIn);
////        this.setEntityInvulnerable(true);
//        this.noClip = true;
//    }

//    @Override
//    protected void entityInit() {
//        super.entityInit();
//        dataManager.register(STAGE, 0);
//    }

    public Stage getStage() {
        return Stage.getStageByID(dataManager.get(STAGE));
    }

    public void setStage(Stage stage) {
        dataManager.set(STAGE, stage.getStageID());
    }

//    @Override
//    public void onUpdate() {
////        cancel();
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (exitPortalLocation == null) {
//            for (exitPortalLocation = new BlockPos(0, 100, 0); world.getBlockState(this.exitPortalLocation).getBlock() != Blocks.BEDROCK && exitPortalLocation.getY() > 30; this.exitPortalLocation = this.exitPortalLocation.down()) ;
//            if (exitPortalLocation.getY() <= 30) {
//                cancel();
//                return;
//            }
//            LogHelper.dev(exitPortalLocation);
//            exitPortalLocation = exitPortalLocation.up(1);
//        }
//
//        if (!world.isRemote) {
//            updateStage();
//        }
//
//        if (getStage() == Stage.POSITION) {
//            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
//        }
//        else {
//            setPosition(0.5, exitPortalLocation.getY() + 0.5, 0.5);
//            motionX = motionY = motionZ = 0;
//        }
//
//        if (!world.isRemote) {
//            //region Update Dragon
//            if (dragon != null) {
//                dragon.setAttackTarget(null);
//                if (!dragon.isEntityAlive()) {
//                    dragon = null;
//                    LogHelper.dev("Re-Acquire Dragon");
//
//                    List<EntityDragon> list = world.getEntities(EntityDragon.class, EntitySelectors.IS_ALIVE);
//                    for (EntityDragon listItem : list) {
//                        if (dragon == null || listItem.getDistance(0, exitPortalLocation.getY() + 0.5, 0) < dragon.getDistance(0, exitPortalLocation.getY() + 0.5, 0)) {
//                            dragon = listItem;
//                        }
//                    }
//                    if (dragon == null) {
//                        cancel();
//                        return;
//                    }
//                }
//
//                if (getStage() != Stage.EXTRACT) {
//                    IPhase phase = dragon.getPhaseManager().getCurrentPhase();
//                    if (phase instanceof PhaseLandingApproach || phase instanceof PhaseLanding || phase instanceof PhaseSittingBase) {
//                        dragon.getPhaseManager().setPhase(PhaseList.HOLDING_PATTERN);
//                    }
//                }
//                else if (stageTime < 300) {
//                    IPhase phase = dragon.getPhaseManager().getCurrentPhase();
//                    if (phase instanceof PhaseTakeoff) {
//                        dragon.getPhaseManager().setPhase(PhaseList.SITTING_SCANNING);
//                    }
//                    else if (!(phase instanceof PhaseLandingApproach || phase instanceof PhaseLanding || phase instanceof PhaseSittingBase || phase instanceof PhaseHover)) {
//                        dragon.getPhaseManager().setPhase(PhaseList.LANDING);
//                    }
//                }
//            }
//            //endregion
//        }
//    }

//    @Override
//    public void onEntityUpdate() {
//        if (getStage() != Stage.POSITION) {
//            setPosition(0, 67, 3);
//            motionX = motionY = motionZ = 0;
//        }
//
//        super.onEntityUpdate();
//    }

//    private void updateStage() {
//        switch (getStage()) {
//            //region Position
//            case POSITION: {
//                if (getDistance(0.5, exitPortalLocation.getY() + 0.5, 0.5) > 0.1) {
//                    ((ServerWorld) world).spawnParticle(EnumParticleTypes.END_ROD, true, posX, posY, posZ, 1, 0, 0, 0, 0.01, 0);
//                    double speed = 0.1;
//                    Vec3D dirVec = Vec3D.getDirectionVec(new Vec3D(this), new Vec3D(0.5, exitPortalLocation.getY() + 0.5, 0.5));
//                    motionX = dirVec.x * speed;
//                    motionY = dirVec.y * speed;
//                    motionZ = dirVec.z * speed;
//                    stageTime++;
//                }
//                else {
//                    motionX = motionY = motionZ = 0;
//                    setPosition(0.5, exitPortalLocation.getY() + 0.5, 0.5);
//                    setStage(Stage.ACQUIRE_DRAGON);
//                    stageTime = 0;
//                }
//                break;
//            } //endregion
//            //region Acquire Dragon and spawn crystals
//            case ACQUIRE_DRAGON: {
//                if (stageTime == 0) {
//                    if (!(world.provider instanceof WorldProviderEnd)) {
//                        cancel();
//                        return;
//                    }
//
//                    fightManager = ((WorldProviderEnd) world.provider).getDragonFightManager();
//                    List<EntityDragon> list = world.getEntities(EntityDragon.class, EntitySelectors.IS_ALIVE);
//                    if (fightManager.dragonKilled || list.isEmpty()) {
//                        cancel();
//                        return;
//                    }
//
//                    for (EntityDragon listItem : list) {
//                        if (dragon == null || listItem.getDistance(0, exitPortalLocation.getY() + 0.5, 0) < dragon.getDistance(0, exitPortalLocation.getY() + 0.5, 0)) {
//                            dragon = listItem;
//                        }
//                    }
//
//                    for (WorldGenSpikes.EndSpike genSpike : BiomeEndDecorator.getSpikesForWorld(world)) {
//                        boolean crystalFound = false;
//                        for (EntityEnderCrystal entityendercrystal : world.getEntitiesWithinAABB(EntityEnderCrystal.class, genSpike.getTopBoundingBox())) {
//                            entityendercrystal.setBeamTarget(exitPortalLocation.down(2));
//                            entityendercrystal.setEntityInvulnerable(true);
//                            crystalFound = true;
//                        }
//
//                        if (!crystalFound) {
//                            deadCrystals.add(new BlockPos(genSpike.getCenterX() + 0.5, genSpike.getHeight() + 1, genSpike.getCenterZ() + 0.5));
//                        }
//                    }
//                }
//                else if (deadCrystals.size() > 0 && stageTime > 50) {
//                    //Respawn next crystal
//                    BlockPos pos = deadCrystals.remove(rand.nextInt(deadCrystals.size()));
//                    world.createExplosion((Entity) null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 5.0F, true);
//                    EntityEnderCrystal crystal = new EntityEnderCrystal(world);
//                    crystal.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rand.nextFloat() * 360.0F, 0.0F);
//                    world.spawnEntity(crystal);
//                    crystal.setEntityInvulnerable(true);
//                    crystal.setBeamTarget(exitPortalLocation.down(2));
//                    stageTime = 0;
//                }
//
//                if (stageTime > 50) {
//                    setStage(Stage.COLLECT_SOULS);
//                    stageTime = 0;
//                    return;
//                }
//
//                stageTime++;
//                break;
//            }//endregion
//            //region Collect Souls
//            case COLLECT_SOULS: {
//                if (stageTime % 20 == 0) {
//                    List<EntityEnderman> list = world.getEntitiesWithinAABB(EntityEnderman.class, getEntityBoundingBox().grow(300, 300, 300), EntitySelectors.IS_ALIVE);
//
//                    if (list.size() < 20) {
//                        spawnNewEnderman();
//                    }
//
//                    //region Update Enderman AI targeting
//                    int targeting = 0;
//                    for (EntityEnderman enderman : list) {
//                        if (enderman.getDistance(0, 64, 0) > 60) {
//                            enderman.setDead();
//                            continue;
//                        }
//
//                        targeting++;
//                        if (targeting > 5 && enderman.getAttackTarget() == this && rand.nextInt(20) == 0) {
//                            PlayerEntity player = Utils.getClosestPlayer(world, enderman.posX, enderman.posY, enderman.posZ, 50, false);
//                            if (player != null) {
//                                enderman.setAttackTarget(player);
//                            }
//                            else {
//                                enderman.setAttackTarget(this);
//                            }
//                        }
//                        else if (targeting > 5 && enderman.getAttackTarget() instanceof PlayerEntity) {
//                            continue;
//                        }
//                        else {
//                            enderman.setAttackTarget(this);
//                        }
//                    }
//                    //endregion
//
//                    //Collect Soul
//                    for (int i = rand.nextInt(4); i >= 0; i--) {
//                        killnextEnderman();
//                    }
//
//                }
//
//                if (soulsCollected > 100) {
//                    stageTime = 0;
//                    setStage(Stage.EXTRACT);
//                }
//
//                stageTime++;
//                break;
//            }//endregion
//            case EXTRACT: {
//                if (stageTime % 20 == 0) {
//                    List<EntityEnderman> list = world.getEntitiesWithinAABB(EntityEnderman.class, getEntityBoundingBox().grow(300, 300, 300), EntitySelectors.IS_ALIVE);
//                    for (EntityEnderman enderman : list) {
//                        enderman.setAttackTarget(this);
//                    }
//                }
//
//                double dragonDist = dragon == null ? 100 : dragon.getDistance(posX, posY, posZ);
//
//                if (dragon != null && dragonDist < 10) {
//                    BCEffectHandler.spawnFX(DEParticles.SOUL_EXTRACTION, world, new Vec3D(dragon).add(0, 2, 0), new Vec3D(this), 512D, 3);
//                }
//
//                if (stageTime == 300) {
//                    List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(300, 300, 300), EntitySelectors.IS_ALIVE);
//                    for (Entity entity : list) {
//                        if (entity instanceof EntityEnderman || (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == Items.ENDER_PEARL) || entity instanceof EntityAreaEffectCloud) {
//                            entity.setDead();
//                        }
//                    }
//
//                    this.world.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 40.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
//
//                    PacketCustom.sendToAllAround(new SPacketExplosion(posX, posY, posZ, 505, Collections.<BlockPos>emptyList(), new Vec3d(0, 0, 0)), posX, posY, posZ, 512, 1);
//
//                    EntityPersistentItem entityItem = new EntityPersistentItem(world, posX, posY, posZ, new ItemStack(Blocks.DRAGON_EGG));
//                    world.spawnEntity(entityItem);
//                    entityItem.motionX = entityItem.motionY = entityItem.motionZ = 0;
//                    cleanup();
//                    if (dragon != null) {
//                        dragon.getPhaseManager().setPhase(PhaseList.CHARGING_PLAYER);
//                    }
//                }
//
//                if (stageTime > 320) {
//                    world.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_ENDERMEN_STARE, SoundCategory.MASTER, 10, 0.7F);
//                    world.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_ENDERMEN_STARE, SoundCategory.MASTER, 10, 1F);
//                    world.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_ENDERMEN_STARE, SoundCategory.MASTER, 10, 1.3F);
//                    setDead();
//                }
//
//                if (dragon != null && (dragonDist < 10 || stageTime >= 300)) {
//                    stageTime++;
//                }
//                break;
//            }
//        }
//    }
//
//    private void cancel() {
//        if (!world.isRemote) {
//            cleanup();
//            world.spawnEntity(new ItemEntity(world, posX, posY, posZ, new ItemStack(DEFeatures.enderEnergyManipulator)));
//            setDead();
//        }
//    }
//
//    private void cleanup() {
//        for (WorldGenSpikes.EndSpike worldgenspikes$endspike : BiomeEndDecorator.getSpikesForWorld(world)) {
//            for (EntityEnderCrystal entityendercrystal : world.getEntitiesWithinAABB(EntityEnderCrystal.class, worldgenspikes$endspike.getTopBoundingBox())) {
//                entityendercrystal.setBeamTarget(null);
//                entityendercrystal.setEntityInvulnerable(false);
//            }
//        }
//    }
//
//    private void spawnNewEnderman() {
//        if (world.isRemote) {
//            return;
//        }
//        for (int i = 0; i < 10; i++) {
//            BlockPos spawnPos = world.getTopSolidOrLiquidBlock(new BlockPos(posX + rand.nextInt(50) - 25, 255, posZ + rand.nextInt(50) - 25));
//            if (!world.isAirBlock(spawnPos)) {
//                EntityEnderman enderman = new EntityEnderman(world);
//                enderman.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5);
//                world.spawnEntity(enderman);
//            }
//        }
//    }
//
//    private void killnextEnderman() {
//        if (world.isRemote) {
//            return;
//        }
//        List<EntityEnderman> list = world.getEntitiesWithinAABB(EntityEnderman.class, getEntityBoundingBox().grow(8, 15, 8), EntitySelectors.IS_ALIVE);
//        if (!list.isEmpty()) {
//            EntityEnderman enderman = list.get(rand.nextInt(list.size()));
//            enderman.captureDrops = true;
//            Vec3D dirVec = Vec3D.getDirectionVec(new Vec3D(enderman), new Vec3D(0.5, exitPortalLocation.getY() + 0.5, 0.5));
//            enderman.motionX = dirVec.x;
//            enderman.motionY = dirVec.y;
//            enderman.motionZ = dirVec.z;
//            enderman.attackEntityFrom(DamageSource.MAGIC, 10000F);
//            soulsCollected++;
//
//            BCEffectHandler.spawnFX(DEParticles.SOUL_EXTRACTION, world, new Vec3D(enderman), new Vec3D(this), 100);
//        }
//    }


    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        soulsCollected = compound.getInt("SoulsCollected");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("SoulsCollected", soulsCollected);
    }

    //region Entity Inventory

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, @Nullable ItemStack stack) {

    }

    //endregion

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    @Override
    protected boolean isMovementBlocked() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    private enum Stage {
        POSITION(0), ACQUIRE_DRAGON(1), COLLECT_SOULS(2), EXTRACT(3);

        public final int stageID;

        Stage(int stageID) {
            this.stageID = stageID;
        }

        public int getStageID() {
            return stageID;
        }

        public static Stage getStageByID(int stageID) {
            if (stageID < 0 || stageID >= values().length) {
                return POSITION;
            }
            return values()[stageID];
        }
    }
}
