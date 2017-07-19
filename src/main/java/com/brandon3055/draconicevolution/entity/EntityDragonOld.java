package com.brandon3055.draconicevolution.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 27/03/2017.
 * This is a temporary hack to fix the chaos guardian until i get time to re write it.
 */
public class EntityDragonOld extends EntityLiving implements IEntityMultiPart, IMob {
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
    public EntityEnderCrystal healingEnderCrystal;

    public EntityDragonOld(World p_i1700_1_) {
        super(p_i1700_1_);
        this.dragonPartArray = new MultiPartEntityPart[]{this.dragonPartHead = new MultiPartEntityPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new MultiPartEntityPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F)};
        this.setHealth(this.getMaxHealth());
        this.setSize(16.0F, 8.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.targetY = 100.0D;
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
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

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate() {
//        float f;
//        float f1;
//
//        if (this.world.isRemote) {
//            f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
//            f1 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
//
//            if (f1 <= -0.3F && f >= -0.3F) {
//                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.HOSTILE, 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
//            }
//        }
//
//        this.prevAnimTime = this.animTime;
//        float f2;
//
//        if (this.getHealth() <= 0.0F) {
//            f = (this.rand.nextFloat() - 0.5F) * 8.0F;
//            f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
//            f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
//            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
//        }
//        else {
//            this.updateDragonEnderCrystal();
//            f = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
//            f *= (float) Math.pow(2.0D, this.motionY);
//
//            if (this.slowed) {
//                this.animTime += f * 0.5F;
//            }
//            else {
//                this.animTime += f;
//            }
//
//            this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
//
//            if (this.ringBufferIndex < 0) {
//                for (int i = 0; i < this.ringBuffer.length; ++i) {
//                    this.ringBuffer[i][0] = (double) this.rotationYaw;
//                    this.ringBuffer[i][1] = this.posY;
//                }
//            }
//
//            if (++this.ringBufferIndex == this.ringBuffer.length) {
//                this.ringBufferIndex = 0;
//            }
//
//            this.ringBuffer[this.ringBufferIndex][0] = (double) this.rotationYaw;
//            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
//            double d0;
//            double d1;
//            double d2;
//            double d10;
//            float f12;
//
//            if (this.world.isRemote) {
//                if (this.newPosRotationIncrements > 0) {
//                    d10 = this.posX + (this.interpTargetX - this.posX) / (double) this.newPosRotationIncrements;
//                    d0 = this.posY + (this.interpTargetY - this.posY) / (double) this.newPosRotationIncrements;
//                    d1 = this.posZ + (this.interpTargetZ - this.posZ) / (double) this.newPosRotationIncrements;
//                    d2 = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
//                    this.rotationYaw = (float) ((double) this.rotationYaw + d2 / (double) this.newPosRotationIncrements);
//                    this.rotationPitch = (float) ((double) this.rotationPitch + (this.interpTargetPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
//                    --this.newPosRotationIncrements;
//                    this.setPosition(d10, d0, d1);
//                    this.setRotation(this.rotationYaw, this.rotationPitch);
//                }
//            }
//            else {
//                d10 = this.targetX - this.posX;
//                d0 = this.targetY - this.posY;
//                d1 = this.targetZ - this.posZ;
//                d2 = d10 * d10 + d0 * d0 + d1 * d1;
//
//                if (this.target != null) {
//                    this.targetX = this.target.posX;
//                    this.targetZ = this.target.posZ;
//                    double d3 = this.targetX - this.posX;
//                    double d5 = this.targetZ - this.posZ;
//                    double d7 = Math.sqrt(d3 * d3 + d5 * d5);
//                    double d8 = 0.4000000059604645D + d7 / 80.0D - 1.0D;
//
//                    if (d8 > 10.0D) {
//                        d8 = 10.0D;
//                    }
//
//                    this.targetY = this.target.getEntityBoundingBox().minY + d8;
//                }
//                else {
//                    this.targetX += this.rand.nextGaussian() * 2.0D;
//                    this.targetZ += this.rand.nextGaussian() * 2.0D;
//                }
//
//                if (this.forceNewTarget || d2 < 100.0D || d2 > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically) {
//                    this.setNewTarget();
//                }
//
//                d0 /= (double) MathHelper.sqrt_double(d10 * d10 + d1 * d1);
//                f12 = 0.6F;
//
//                if (d0 < (double) (-f12)) {
//                    d0 = (double) (-f12);
//                }
//
//                if (d0 > (double) f12) {
//                    d0 = (double) f12;
//                }
//
//                this.motionY += d0 * 0.10000000149011612D;
//                this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
//                double d4 = 180.0D - Math.atan2(d10, d1) * 180.0D / Math.PI;
//                double d6 = MathHelper.wrapDegrees(d4 - (double) this.rotationYaw);
//
//                if (d6 > 50.0D) {
//                    d6 = 50.0D;
//                }
//
//                if (d6 < -50.0D) {
//                    d6 = -50.0D;
//                }
//
//                Vec3d vec3 = (new Vec3d(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ)).normalize();
//                Vec3d vec32 = (new Vec3d((double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, (double) (-MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F)))).normalize();
//                float f5 = (float) (vec32.dotProduct(vec3) + 0.5D) / 1.5F;
//
//                if (f5 < 0.0F) {
//                    f5 = 0.0F;
//                }
//
//                this.randomYawVelocity *= 0.8F;
//                float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
//                double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;
//
//                if (d9 > 40.0D) {
//                    d9 = 40.0D;
//                }
//
//                this.randomYawVelocity = (float) ((double) this.randomYawVelocity + d6 * (0.699999988079071D / d9 / (double) f6));
//                this.rotationYaw += this.randomYawVelocity * 0.1F;
//                float f7 = (float) (2.0D / (d9 + 1.0D));
//                float f8 = 0.06F;
//                this.moveRelative(0.0F, -1.0F, f8 * (f5 * f7 + (1.0F - f7)));
//
//                if (this.slowed) {
//                    this.moveEntity(this.motionX * 0.800000011920929D, this.motionY * 0.800000011920929D, this.motionZ * 0.800000011920929D);
//                }
//                else {
//                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
//                }
//
//                Vec3d vec31 = (new Vec3d(this.motionX, this.motionY, this.motionZ)).normalize();
//                float f9 = (float) (vec31.dotProduct(vec32) + 1.0D) / 2.0F;
//                f9 = 0.8F + 0.15F * f9;
//                this.motionX *= (double) f9;
//                this.motionZ *= (double) f9;
//                this.motionY *= 0.9100000262260437D;
//            }
//
//            this.renderYawOffset = this.rotationYaw;
//            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
//            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
//            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
//            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
//            this.dragonPartBody.height = 3.0F;
//            this.dragonPartBody.width = 5.0F;
//            this.dragonPartWing1.height = 2.0F;
//            this.dragonPartWing1.width = 4.0F;
//            this.dragonPartWing2.height = 3.0F;
//            this.dragonPartWing2.width = 4.0F;
//            f1 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
//            f2 = MathHelper.cos(f1);
//            float f10 = -MathHelper.sin(f1);
//            float f3 = this.rotationYaw * (float) Math.PI / 180.0F;
//            float f11 = MathHelper.sin(f3);
//            float f4 = MathHelper.cos(f3);
//            this.dragonPartBody.onUpdate();
//            this.dragonPartBody.setLocationAndAngles(this.posX + (double) (f11 * 0.5F), this.posY, this.posZ - (double) (f4 * 0.5F), 0.0F, 0.0F);
//            this.dragonPartWing1.onUpdate();
//            this.dragonPartWing1.setLocationAndAngles(this.posX + (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ + (double) (f11 * 4.5F), 0.0F, 0.0F);
//            this.dragonPartWing2.onUpdate();
//            this.dragonPartWing2.setLocationAndAngles(this.posX - (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ - (double) (f11 * 4.5F), 0.0F, 0.0F);
//
//            if (!this.world.isRemote && this.hurtTime == 0) {
//                this.collideWithEntities(this.world.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
//                this.collideWithEntities(this.world.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
//                this.attackEntitiesInList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0D, 1.0D, 1.0D)));
//            }
//
//            double[] adouble1 = this.getMovementOffsets(5, 1.0F);
//            double[] adouble = this.getMovementOffsets(0, 1.0F);
//            f12 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
//            float f13 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
//            this.dragonPartHead.onUpdate();
//            this.dragonPartHead.setLocationAndAngles(this.posX + (double) (f12 * 5.5F * f2), this.posY + (adouble[1] - adouble1[1]) * 1.0D + (double) (f10 * 5.5F), this.posZ - (double) (f13 * 5.5F * f2), 0.0F, 0.0F);
//
//            for (int j = 0; j < 3; ++j) {
//                EntityDragonPart entitydragonpart = null;
//
//                if (j == 0) {
//                    entitydragonpart = this.dragonPartTail1;
//                }
//
//                if (j == 1) {
//                    entitydragonpart = this.dragonPartTail2;
//                }
//
//                if (j == 2) {
//                    entitydragonpart = this.dragonPartTail3;
//                }
//
//                double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
//                float f14 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble1[0]) * (float) Math.PI / 180.0F * 1.0F;
//                float f15 = MathHelper.sin(f14);
//                float f16 = MathHelper.cos(f14);
//                float f17 = 1.5F;
//                float f18 = (float) (j + 1) * 2.0F;
//                entitydragonpart.onUpdate();
//                entitydragonpart.setLocationAndAngles(this.posX - (double) ((f11 * f17 + f15 * f18) * f2), this.posY + (adouble2[1] - adouble1[1]) * 1.0D - (double) ((f18 + f17) * f10) + 1.5D, this.posZ + (double) ((f4 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
//            }
//
//            if (!this.world.isRemote) {
//                this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
//            }
//        }
    }

    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void updateDragonEnderCrystal() {
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
                if (!this.world.isRemote) {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.causeExplosionDamage((Explosion) null), 10.0F);
                }

                this.healingEnderCrystal = null;
            }
            else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.rand.nextInt(10) == 0) {
            float f = 32.0F;
            List list = this.world.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().grow((double) f, (double) f, (double) f));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.getDistanceSqToEntity(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.healingEnderCrystal = entityendercrystal;
        }
    }

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
    private void collideWithEntities(List p_70970_1_) {
        double d0 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0D;
        double d1 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0D;
        Iterator iterator = p_70970_1_.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLivingBase) {
                double d2 = entity.posX - d0;
                double d3 = entity.posZ - d1;
                double d4 = d2 * d2 + d3 * d3;
                entity.addVelocity(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
            }
        }
    }

    /**
     * Attacks all entities inside this list, dealing 5 hearts of damage.
     */
    private void attackEntitiesInList(List p_70971_1_) {
        for (int i = 0; i < p_70971_1_.size(); ++i) {
            Entity entity = (Entity) p_70971_1_.get(i);

            if (entity instanceof EntityLivingBase) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            }
        }
    }

    /**
     * Sets a new target for the flight AI. It can be a random coordinate or a nearby player.
     */
    private void setNewTarget() {
        this.forceNewTarget = false;

        if (this.rand.nextInt(2) == 0 && !this.world.playerEntities.isEmpty()) {
            this.target = (Entity) this.world.playerEntities.get(this.rand.nextInt(this.world.playerEntities.size()));
        }
        else {
            boolean flag = false;

            do {
                this.targetX = 0.0D;
                this.targetY = (double) (70.0F + this.rand.nextFloat() * 50.0F);
                this.targetZ = 0.0D;
                this.targetX += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
                this.targetZ += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
                double d0 = this.posX - this.targetX;
                double d1 = this.posY - this.targetY;
                double d2 = this.posZ - this.targetZ;
                flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
            } while (!flag);

            this.target = null;
        }
    }

    /**
     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
     */
    private float simplifyAngle(double p_70973_1_) {
        return (float) MathHelper.wrapDegrees(p_70973_1_);
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
        int i = MathHelper.floor(p_70972_1_.minX);
        int j = MathHelper.floor(p_70972_1_.minY);
        int k = MathHelper.floor(p_70972_1_.minZ);
        int l = MathHelper.floor(p_70972_1_.maxX);
        int i1 = MathHelper.floor(p_70972_1_.maxY);
        int j1 = MathHelper.floor(p_70972_1_.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPos pos = new BlockPos(k1, l1, i2);
                    IBlockState state = world.getBlockState(pos);
//                    Block block = this.world.getBlock(k1, l1, i2);

                    if (!world.isAirBlock(pos)) {
                        if (state.getBlock().canEntityDestroy(state, world, pos, this) && this.world.getGameRules().getBoolean("mobGriefing")) {
                            flag1 = this.world.setBlockToAir(pos) || flag1;
                        }
                        else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            double d1 = p_70972_1_.minX + (p_70972_1_.maxX - p_70972_1_.minX) * (double) this.rand.nextFloat();
            double d2 = p_70972_1_.minY + (p_70972_1_.maxY - p_70972_1_.minY) * (double) this.rand.nextFloat();
            double d0 = p_70972_1_.minZ + (p_70972_1_.maxZ - p_70972_1_.minZ) * (double) this.rand.nextFloat();
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d1, d2, d0, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

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
        this.targetX = this.posX + (double) (f2 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.targetY = this.posY + (double) (this.rand.nextFloat() * 3.0F) + 1.0D;
        this.targetZ = this.posZ - (double) (f3 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.target = null;

        if (p_70965_2_.getTrueSource() instanceof EntityPlayer || p_70965_2_.isExplosion()) {
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

        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.world.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                i = 1000;

                while (i > 0) {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }

            if (this.deathTicks == 1) {
                this.world.playBroadcastSound(1018, new BlockPos(this), 0);
            }
        }

        this.move(MoverType.SELF, 0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.world.isRemote) {
            i = 2000;

            while (i > 0) {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }

            this.createEnderPortal(MathHelper.floor(this.posX), MathHelper.floor(this.posZ));
            this.setDead();
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
    protected void despawnEntity() {
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    @Override
    public Entity[] getParts() {
        return this.dragonPartArray;
    }

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
        return SoundEvents.ENTITY_ENDERDRAGON_AMBIENT;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ENTITY_ENDERDRAGON_HURT;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }
}