package com.brandon3055.draconicevolution.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 27/03/2017.
 * This is a temporary hack to fix the chaos guardian until i get time to re write it.
 */
@Deprecated
public class EntityDragonOld extends MobEntity implements IEntityMultiPart, IMob {
    public double targetX;
    public double targetY;
    public double targetZ;
    /**
     * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
     */
    public double[][] ringBuffer = new double[64][3];
    /**
     * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
     */
    public int ringBufferIndex = -1;
    /**
     * An array containing all body parts of this dragon
     */
    public MultiPartEntityPart[] dragonPartArray;
    /**
     * The head bounding box of a dragon
     */
    public MultiPartEntityPart dragonPartHead;
    /**
     * The body bounding box of a dragon
     */
    public MultiPartEntityPart dragonPartBody;
    public MultiPartEntityPart dragonPartTail1;
    public MultiPartEntityPart dragonPartTail2;
    public MultiPartEntityPart dragonPartTail3;
    public MultiPartEntityPart dragonPartWing1;
    public MultiPartEntityPart dragonPartWing2;
    /**
     * Animation time at previous tick.
     */
    public float prevAnimTime;
    /**
     * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
     */
    public float animTime;
    /**
     * Force selecting a new flight target at next tick if set to true.
     */
    public boolean forceNewTarget;
    /**
     * Activated if the dragon is flying though obsidian, white stone or bedrock. Slows movement and animation speed.
     */
    public boolean slowed;
    private Entity target;
    public int deathTicks;
    /**
     * The current endercrystal that is healing this dragon
     */
    public EnderCrystalEntity healingEnderCrystal;

    public EntityDragonOld(EntityType<? extends EntityDragonOld> type,  World world) {
        super(type, world);
        this.dragonPartArray = new MultiPartEntityPart[]{this.dragonPartHead = new MultiPartEntityPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new MultiPartEntityPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F)};
        this.setHealth(this.getMaxHealth());
        this.noClip = true;
        this.targetY = 100.0D;
        this.ignoreFrustumCheck = true;
    }

    /**
     * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw
     * offset, [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
     */
    public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {
        if (this.getHealth() <= 0.0F) {
            p_70974_2_ = 0.0F;
        }

        p_70974_2_ = 1.0F - p_70974_2_;
        int j = this.ringBufferIndex - p_70974_1_ * 1 & 63;
        int k = this.ringBufferIndex - p_70974_1_ * 1 - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[j][0];
        double d1 = MathHelper.wrapDegrees(this.ringBuffer[k][0] - d0);
        adouble[0] = d0 + d1 * (double) p_70974_2_;
        d0 = this.ringBuffer[j][1];
        d1 = this.ringBuffer[k][1] - d0;
        adouble[1] = d0 + d1 * (double) p_70974_2_;
        adouble[2] = this.ringBuffer[j][2] + (this.ringBuffer[k][2] - this.ringBuffer[j][2]) * (double) p_70974_2_;
        return adouble;
    }

    @Override
    public void livingTick() {

    }



    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
//    private void updateDragonEnderCrystal() {
//        if (this.healingEnderCrystal != null) {
//            if (!this.healingEnderCrystal.isAlive()) {
//                if (!this.world.isRemote) {
//                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.causeExplosionDamage((Explosion) null), 10.0F);
//                }
//
//                this.healingEnderCrystal = null;
//            }
//            else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
//                this.setHealth(this.getHealth() + 1.0F);
//            }
//        }
//
//        if (this.rand.nextInt(10) == 0) {
//            float f = 32.0F;
//            List list = this.world.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().grow((double) f, (double) f, (double) f));
//            EntityEnderCrystal entityendercrystal = null;
//            double d0 = Double.MAX_VALUE;
//            Iterator iterator = list.iterator();
//
//            while (iterator.hasNext()) {
//                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
//                double d1 = entityendercrystal1.getDistanceSq(this);
//
//                if (d1 < d0) {
//                    d0 = d1;
//                    entityendercrystal = entityendercrystal1;
//                }
//            }
//
//            this.healingEnderCrystal = entityendercrystal;
//        }
//    }

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
//    private void collideWithEntities(List p_70970_1_) {
//        double d0 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0D;
//        double d1 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0D;
//        Iterator iterator = p_70970_1_.iterator();
//
//        while (iterator.hasNext()) {
//            Entity entity = (Entity) iterator.next();
//
//            if (entity instanceof LivingEntity) {
//                double d2 = entity.getPosX() - d0;
//                double d3 = entity.getPosZ() - d1;
//                double d4 = d2 * d2 + d3 * d3;
//                entity.addVelocity(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
//            }
//        }
//    }

//    /**
//     * Attacks all entities inside this list, dealing 5 hearts of damage.
//     */
//    private void attackEntitiesInList(List p_70971_1_) {
//        for (int i = 0; i < p_70971_1_.size(); ++i) {
//            Entity entity = (Entity) p_70971_1_.get(i);
//
//            if (entity instanceof LivingEntity) {
//                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
//            }
//        }
//    }

//    /**
//     * Sets a new target for the flight AI. It can be a random coordinate or a nearby player.
//     */
//    private void setNewTarget() {
//        this.forceNewTarget = false;
//
//        if (this.rand.nextInt(2) == 0 && !this.world.playerEntities.isEmpty()) {
//            this.target = (Entity) this.world.playerEntities.get(this.rand.nextInt(this.world.playerEntities.size()));
//        }
//        else {
//            boolean flag = false;
//
//            do {
//                this.targetX = 0.0D;
//                this.targetY = (double) (70.0F + this.rand.nextFloat() * 50.0F);
//                this.targetZ = 0.0D;
//                this.targetX += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
//                this.targetZ += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
//                double d0 = this.posX - this.targetX;
//                double d1 = this.posY - this.targetY;
//                double d2 = this.posZ - this.targetZ;
//                flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
//            } while (!flag);
//
//            this.target = null;
//        }
//    }

//    /**
//     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
//     */
//    private float simplifyAngle(double p_70973_1_) {
//        return (float) MathHelper.wrapDegrees(p_70973_1_);
//    }

//    /**
//     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
//     */
//    private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
//        int i = MathHelper.floor(p_70972_1_.minX);
//        int j = MathHelper.floor(p_70972_1_.minY);
//        int k = MathHelper.floor(p_70972_1_.minZ);
//        int l = MathHelper.floor(p_70972_1_.maxX);
//        int i1 = MathHelper.floor(p_70972_1_.maxY);
//        int j1 = MathHelper.floor(p_70972_1_.maxZ);
//        boolean flag = false;
//        boolean flag1 = false;
//
//        for (int k1 = i; k1 <= l; ++k1) {
//            for (int l1 = j; l1 <= i1; ++l1) {
//                for (int i2 = k; i2 <= j1; ++i2) {
//                    BlockPos pos = new BlockPos(k1, l1, i2);
//                    BlockState state = world.getBlockState(pos);
////                    Block block = this.world.getBlock(k1, l1, i2);
//
//                    if (!world.isAirBlock(pos)) {
//                        if (state.getBlock().canEntityDestroy(state, world, pos, this) && this.world.getGameRules().getBoolean("mobGriefing")) {
//                            flag1 = this.world.setBlockToAir(pos) || flag1;
//                        }
//                        else {
//                            flag = true;
//                        }
//                    }
//                }
//            }
//        }
//
//        if (flag1) {
//            double d1 = p_70972_1_.minX + (p_70972_1_.maxX - p_70972_1_.minX) * (double) this.rand.nextFloat();
//            double d2 = p_70972_1_.minY + (p_70972_1_.maxY - p_70972_1_.minY) * (double) this.rand.nextFloat();
//            double d0 = p_70972_1_.minZ + (p_70972_1_.maxZ - p_70972_1_.minZ) * (double) this.rand.nextFloat();
//            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d1, d2, d0, 0.0D, 0.0D, 0.0D);
//        }
//
//        return flag;
//    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean attackEntityFromPart(MultiPartEntityPart p_70965_1_, DamageSource p_70965_2_, float p_70965_3_) {
        if (p_70965_1_ != this.dragonPartHead) {
            p_70965_3_ = p_70965_3_ / 4.0F + 1.0F;
        }

        float f1 = this.rotationYaw * (float) Math.PI / 180.0F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        this.targetX = this.getPosX() + (double) (f2 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.targetY = this.getPosY() + (double) (this.rand.nextFloat() * 3.0F) + 1.0D;
        this.targetZ = this.getPosZ() - (double) (f3 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.target = null;

        if (p_70965_2_.getTrueSource() instanceof PlayerEntity || p_70965_2_.isExplosion()) {
            this.attackEntityFrom(p_70965_2_, p_70965_3_);
        }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        return super.attackEntityFrom(p_70097_1_, p_70097_2_);
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    @Override
    protected void onDeathUpdate() {
        ++this.deathTicks;

        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX() + (double)f, this.getPosY() + 2.0D + (double)f1, this.getPosZ() + (double)f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.world.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                i = 1000;

                while (i > 0) {
                    j = ExperienceOrbEntity.getXPSplit(i);
                    i -= j;
                    this.dropExperience(MathHelper.floor((float)i * 0.08F));
                }
            }

            if (this.deathTicks == 1) {
                this.world.playBroadcastSound(1018, this.getPosition(), 0);
            }
        }

        this.move(MoverType.SELF, new Vector3d(0.0D, (double)0.1F, 0.0D));
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.world.isRemote) {
            i = 2000;

            while (i > 0) {
                j = ExperienceOrbEntity.getXPSplit(i);
                i -= j;
                this.dropExperience(MathHelper.floor((float)i * 0.2F));
            }

            this.createEnderPortal(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosZ()));
            this.remove();
        }
    }

    private void dropExperience(int p_184668_1_) {
        while(p_184668_1_ > 0) {
            int i = ExperienceOrbEntity.getXPSplit(p_184668_1_);
            p_184668_1_ -= i;
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), i));
        }
    }

    /**
     * Creates the ender portal leading back to the normal world after defeating the enderdragon.
     */
    private void createEnderPortal(int p_70975_1_, int p_70975_2_) {
//        byte b0 = 64;
//        BlockEndPortal.field_149948_a = true;
//        byte b1 = 4;
//
//        for (int k = b0 - 1; k <= b0 + 32; ++k)
//        {
//            for (int l = p_70975_1_ - b1; l <= p_70975_1_ + b1; ++l)
//            {
//                for (int i1 = p_70975_2_ - b1; i1 <= p_70975_2_ + b1; ++i1)
//                {
//                    double d0 = (double)(l - p_70975_1_);
//                    double d1 = (double)(i1 - p_70975_2_);
//                    double d2 = d0 * d0 + d1 * d1;
//
//                    if (d2 <= ((double)b1 - 0.5D) * ((double)b1 - 0.5D))
//                    {
//                        if (k < b0)
//                        {
//                            if (d2 <= ((double)(b1 - 1) - 0.5D) * ((double)(b1 - 1) - 0.5D))
//                            {
//                                this.world.setBlock(l, k, i1, Blocks.bedrock);
//                            }
//                        }
//                        else if (k > b0)
//                        {
//                            this.world.setBlock(l, k, i1, Blocks.air);
//                        }
//                        else if (d2 > ((double)(b1 - 1) - 0.5D) * ((double)(b1 - 1) - 0.5D))
//                        {
//                            this.world.setBlock(l, k, i1, Blocks.bedrock);
//                        }
//                        else
//                        {
//                            this.world.setBlock(l, k, i1, Blocks.end_portal);
//                        }
//                    }
//                }
//            }
//        }
//
//        this.world.setBlock(p_70975_1_, b0 + 0, p_70975_2_, Blocks.bedrock);
//        this.world.setBlock(p_70975_1_, b0 + 1, p_70975_2_, Blocks.bedrock);
//        this.world.setBlock(p_70975_1_, b0 + 2, p_70975_2_, Blocks.bedrock);
//        this.world.setBlock(p_70975_1_ - 1, b0 + 2, p_70975_2_, Blocks.torch);
//        this.world.setBlock(p_70975_1_ + 1, b0 + 2, p_70975_2_, Blocks.torch);
//        this.world.setBlock(p_70975_1_, b0 + 2, p_70975_2_ - 1, Blocks.torch);
//        this.world.setBlock(p_70975_1_, b0 + 2, p_70975_2_ + 1, Blocks.torch);
//        this.world.setBlock(p_70975_1_, b0 + 3, p_70975_2_, Blocks.bedrock);
//        this.world.setBlock(p_70975_1_, b0 + 4, p_70975_2_, Blocks.dragon_egg);
//        BlockEndPortal.field_149948_a = false;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void checkDespawn() {}

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean preventDespawn() {
        return true;
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
//    @Override
//    public Entity[] getParts() {
//        return this.dragonPartArray;
//    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }



    public static class MultiPartEntityPart extends Entity {
        public final EntityDragonOld parent;
        public final String partName;

        public MultiPartEntityPart(EntityDragonOld dragon, String partName, float width, float height) {
            super(dragon.getType(), dragon.world);
            this.recalculateSize();
            this.parent = dragon;
            this.partName = partName;
        }



        protected void registerData() {
        }

        protected void readAdditional(CompoundNBT compound) {
        }

        protected void writeAdditional(CompoundNBT compound) {
        }

        @Override
        public boolean canBeCollidedWith() {
            return true;
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            return !this.isInvulnerableTo(source) && this.parent.attackEntityFromPart(this, source, amount);
        }

        @Override
        public boolean isEntityEqual(Entity entityIn) {
            return this == entityIn || this.parent == entityIn;
        }

        public IPacket<?> createSpawnPacket() {
            throw new UnsupportedOperationException();
        }

    }
}