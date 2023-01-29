package com.brandon3055.draconicevolution.common.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.utills.DamageSourceChaos;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 4/07/2014.
 */
public class EntityCustomDragon extends EntityDragon {

    private Entity target;
    public int portalX = 0;
    public int portalY = 67;
    public int portalZ = 0;
    private boolean createPortal = true;
    public float attackDamage = 10F;
    protected boolean isUber = false;
    private boolean initialized = false;

    public EntityCustomDragon(World par1World) {
        super(par1World);
    }

    public EntityCustomDragon(World world, double health, float attack) {
        this(world);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(health);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(health);
        this.attackDamage = attack;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(12, (isUber ? (byte) 1 : (byte) 0));
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200D);
    }

    @Override
    public void onLivingUpdate() {
        float f;
        float f1;

        if (!initialized && !worldObj.isRemote) {
            this.addPotionEffect(new PotionEffect(10, 600, 10, false));
            initialized = true;
        }

        if (this.worldObj.isRemote) {
            f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
            f1 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);

            if (f1 <= -0.3F && f >= -0.3F) {
                this.worldObj.playSound(
                        this.posX,
                        this.posY,
                        this.posZ,
                        "mob.enderdragon.wings",
                        5.0F,
                        0.8F + this.rand.nextFloat() * 0.3F,
                        false);
            }
        }

        this.prevAnimTime = this.animTime;
        float f2;

        if (this.getHealth() <= 0.0F) {
            f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle(
                    "largeexplode",
                    this.posX + (double) f,
                    this.posY + 2.0D + (double) f1,
                    this.posZ + (double) f2,
                    0.0D,
                    0.0D,
                    0.0D);
        } else {
            this.updateDragonEnderCrystal();
            if (isUber)
                f = 0.5F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F
                        + 1.0F); // Wing Speed
            else f = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F
                    + 1.0F);
            f *= (float) Math.pow(2.0D, this.motionY);

            if (this.slowed) {
                this.animTime += f * 0.5F;
            } else {
                this.animTime += f;
            }

            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

            if (this.ringBufferIndex < 0) {
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = (double) this.rotationYaw;
                    this.ringBuffer[i][1] = this.posY;
                }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = (double) this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            double d0;
            double d1;
            double d2;
            double d10;
            float f12;

            if (this.worldObj.isRemote) {
                if (this.newPosRotationIncrements > 0) {
                    d10 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
                    d0 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
                    d1 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
                    d2 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
                    this.rotationYaw = (float) ((double) this.rotationYaw
                            + d2 / (double) this.newPosRotationIncrements);
                    this.rotationPitch = (float) ((double) this.rotationPitch
                            + (this.newRotationPitch - (double) this.rotationPitch)
                                    / (double) this.newPosRotationIncrements);
                    --this.newPosRotationIncrements;
                    this.setPosition(d10, d0, d1);
                    this.setRotation(this.rotationYaw, this.rotationPitch);
                }
            } else {
                d10 = this.targetX - this.posX;
                d0 = this.targetY - this.posY;
                d1 = this.targetZ - this.posZ;
                d2 = d10 * d10 + d0 * d0 + d1 * d1;

                if (this.target != null) {
                    this.targetX = this.target.posX;
                    this.targetZ = this.target.posZ;
                    double d3 = this.targetX - this.posX;
                    double d5 = this.targetZ - this.posZ;
                    double d7 = Math.sqrt(d3 * d3 + d5 * d5);
                    double d8 = 0.4000000059604645D + d7 / 80.0D - 1.0D;

                    if (d8 > 10.0D) {
                        d8 = 10.0D;
                    }

                    this.targetY = this.target.boundingBox.minY + d8;
                } else {
                    this.targetX += this.rand.nextGaussian() * 2.0D;
                    this.targetZ += this.rand.nextGaussian() * 2.0D;
                }

                if (this.forceNewTarget || d2 < 100.0D
                        || d2 > 22500.0D
                        || this.isCollidedHorizontally
                        || this.isCollidedVertically) {
                    this.setNewTarget();
                }

                d0 /= (double) MathHelper.sqrt_double(d10 * d10 + d1 * d1);
                if (isUber) f12 = 1.0F; // Verticle Motion Speed
                else f12 = 0.6F;

                if (d0 < (double) (-f12)) {
                    d0 = (double) (-f12);
                }

                if (d0 > (double) f12) {
                    d0 = (double) f12;
                }

                this.motionY += d0 * 0.10000000149011612D;
                this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
                double d4 = 180.0D - Math.atan2(d10, d1) * 180.0D / Math.PI;
                double d6 = MathHelper.wrapAngleTo180_double(d4 - (double) this.rotationYaw);

                if (d6 > 50.0D) {
                    d6 = 50.0D;
                }

                if (d6 < -50.0D) {
                    d6 = -50.0D;
                }

                Vec3 vec3 = Vec3.createVectorHelper(
                        this.targetX - this.posX,
                        this.targetY - this.posY,
                        this.targetZ - this.posZ).normalize();
                Vec3 vec32 = Vec3.createVectorHelper(
                        (double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F),
                        this.motionY,
                        (double) (-MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F))).normalize();
                float f5 = (float) (vec32.dotProduct(vec3) + 0.5D) / 1.5F;

                if (f5 < 0.0F) {
                    f5 = 0.0F;
                }

                this.randomYawVelocity *= 0.8F;
                float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F
                        + 1.0F;
                double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

                if (d9 > 40.0D) {
                    d9 = 40.0D;
                }

                this.randomYawVelocity = (float) ((double) this.randomYawVelocity
                        + d6 * (0.699999988079071D / d9 / (double) f6));
                this.rotationYaw += this.randomYawVelocity * 0.1F;
                float f7 = (float) (2.0D / (d9 + 1.0D));
                float f8 = 0.06F;
                this.moveFlying(0.0F, -1.0F, f8 * (f5 * f7 + (1.0F - f7)));

                if (this.slowed) {
                    this.moveEntity(
                            this.motionX * 0.800000011920929D,
                            this.motionY * 0.800000011920929D,
                            this.motionZ * 0.800000011920929D);
                } else if (isUber) {
                    this.moveEntity(this.motionX * 2.5D, this.motionY * 1.5D, this.motionZ * 2.5D);
                } else {
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                }

                Vec3 vec31 = Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ).normalize();
                float f9 = (float) (vec31.dotProduct(vec32) + 1.0D) / 2.0F;
                f9 = 0.8F + 0.15F * f9;
                this.motionX *= (double) f9;
                this.motionZ *= (double) f9;
                this.motionY *= 0.9100000262260437D;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            f1 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F
                    / 180.0F
                    * (float) Math.PI;
            f2 = MathHelper.cos(f1);
            float f10 = -MathHelper.sin(f1);
            float f3 = this.rotationYaw * (float) Math.PI / 180.0F;
            float f11 = MathHelper.sin(f3);
            float f4 = MathHelper.cos(f3);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(
                    this.posX + (double) (f11 * 0.5F),
                    this.posY,
                    this.posZ - (double) (f4 * 0.5F),
                    0.0F,
                    0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(
                    this.posX + (double) (f4 * 4.5F),
                    this.posY + 2.0D,
                    this.posZ + (double) (f11 * 4.5F),
                    0.0F,
                    0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(
                    this.posX - (double) (f4 * 4.5F),
                    this.posY + 2.0D,
                    this.posZ - (double) (f11 * 4.5F),
                    0.0F,
                    0.0F);

            if (!this.worldObj.isRemote && this.hurtTime == 0) {
                this.collideWithEntities(
                        this.worldObj.getEntitiesWithinAABBExcludingEntity(
                                this,
                                this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.collideWithEntities(
                        this.worldObj.getEntitiesWithinAABBExcludingEntity(
                                this,
                                this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.attackEntitiesInList(
                        this.worldObj.getEntitiesWithinAABBExcludingEntity(
                                this,
                                this.dragonPartHead.boundingBox.expand(1.0D, 1.0D, 1.0D)));
            }

            double[] adouble1 = this.getMovementOffsets(5, 1.0F);
            double[] adouble = this.getMovementOffsets(0, 1.0F);
            f12 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float f13 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead.setLocationAndAngles(
                    this.posX + (double) (f12 * 5.5F * f2),
                    this.posY + (adouble[1] - adouble1[1]) * 1.0D + (double) (f10 * 5.5F),
                    this.posZ - (double) (f13 * 5.5F * f2),
                    0.0F,
                    0.0F);

            for (int j = 0; j < 3; ++j) {
                EntityDragonPart entitydragonpart = null;

                if (j == 0) {
                    entitydragonpart = this.dragonPartTail1;
                }

                if (j == 1) {
                    entitydragonpart = this.dragonPartTail2;
                }

                if (j == 2) {
                    entitydragonpart = this.dragonPartTail3;
                }

                double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
                float f14 = this.rotationYaw * (float) Math.PI / 180.0F
                        + this.simplifyAngle(adouble2[0] - adouble1[0]) * (float) Math.PI / 180.0F * 1.0F;
                float f15 = MathHelper.sin(f14);
                float f16 = MathHelper.cos(f14);
                float f17 = 1.5F;
                float f18 = (float) (j + 1) * 2.0F;
                entitydragonpart.onUpdate();
                entitydragonpart.setLocationAndAngles(
                        this.posX - (double) ((f11 * f17 + f15 * f18) * f2),
                        this.posY + (adouble2[1] - adouble1[1]) * 1.0D - (double) ((f18 + f17) * f10) + 1.5D,
                        this.posZ + (double) ((f4 * f17 + f16 * f18) * f2),
                        0.0F,
                        0.0F);
            }

            if (!this.worldObj.isRemote) {
                this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.boundingBox)
                        | this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
            }
        }
    }

    @Override
    protected void onDeathUpdate() {

        if (deathTicks == 0 && !isUber) {
            for (int ix = -150; ix < 150; ix++) {
                for (int iz = -150; iz < 150; iz++) {
                    if (worldObj.getBlock(ix, portalY, iz) == Blocks.bedrock
                            && worldObj.getBlock(ix, portalY - 1, iz) == Blocks.bedrock) {
                        portalX = ix;
                        portalZ = iz;
                        createPortal = false;
                        break;
                    }
                }
            }
            if (ConfigHandler.dragonEggSpawnLocation[0] != 0 || ConfigHandler.dragonEggSpawnLocation[1] != 0
                    || ConfigHandler.dragonEggSpawnLocation[1] != 0) {
                createPortal = false;
                portalX = ConfigHandler.dragonEggSpawnLocation[0];
                portalY = ConfigHandler.dragonEggSpawnLocation[1];
                portalZ = ConfigHandler.dragonEggSpawnLocation[2];
            }
        }

        ++this.deathTicks;

        if (worldObj.rand.nextInt(5) == 2) {
            EntityLightningBolt bolt = new EntityLightningBolt(worldObj, portalX, portalY + 1, portalZ);
            bolt.ignoreFrustumCheck = true;
            worldObj.addWeatherEffect(bolt);
        }

        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle(
                    "hugeexplosion",
                    this.posX + (double) f,
                    this.posY + 2.0D + (double) f1,
                    this.posZ + (double) f2,
                    0.0D,
                    0.0D,
                    0.0D);
        }

        int i;
        int j;

        if (!this.worldObj.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                i = 1000;

                while (i > 0) {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj
                            .spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }
        } else if (this.deathTicks == 1) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.end", 50.0F, 1F, false);
            // this.worldObj.playBroadcastSound(1018, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
        }

        this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.worldObj.isRemote) {
            i = 2000;

            while (i > 0) {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            spawnEgg();
            setDead();
        }
    }

    @SuppressWarnings("rawtypes")
    private void collideWithEntities(List par1List) {
        double d0 = (this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX) / 2.0D;
        double d1 = (this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ) / 2.0D;
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLivingBase) {
                double d2 = entity.posX - d0;
                double d3 = entity.posZ - d1;
                double d4 = d2 * d2 + d3 * d3;
                if (rand.nextInt(isUber ? 3 : 10) == 0)
                    entity.addVelocity(d2 / d4 * 8.0D, 5.20000000298023224D, d3 / d4 * 8.0D);
                entity.velocityChanged = true;
                ((EntityLivingBase) entity).setLastAttacker(this);
            }
            if (entity instanceof EntityLivingBase && isUber) {
                ((EntityLivingBase) entity).setLastAttacker(this);
                entity.attackEntityFrom(new DamageSourceChaos(this), 20F);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void attackEntitiesInList(List par1List) {
        for (int i = 0; i < par1List.size(); ++i) {
            Entity entity = (Entity) par1List.get(i);

            if (entity instanceof EntityLivingBase && !isUber) {
                ((EntityLivingBase) entity).setLastAttacker(this);
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
            }
            if (entity instanceof EntityLivingBase && isUber) {
                ((EntityLivingBase) entity).setLastAttacker(this);
                entity.attackEntityFrom(new DamageSourceChaos(this), 50F);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void updateDragonEnderCrystal() {
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
                if (!this.worldObj.isRemote) {
                    this.attackEntityFromPart(
                            this.dragonPartHead,
                            DamageSource.setExplosionSource((Explosion) null),
                            10.0F);
                }

                this.healingEnderCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                if (isUber) this.setHealth(this.getHealth() + 10.0F);
                else this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.rand.nextInt(10) == 0) {
            float f = 32.0F;
            List list = this.worldObj.getEntitiesWithinAABB(
                    EntityEnderCrystal.class,
                    this.boundingBox.expand((double) f, (double) f, (double) f));
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

    private void setNewTarget() {
        this.forceNewTarget = false;

        if ((isUber || this.rand.nextInt(2) == 0) && (!isUber || 19 > this.rand.nextInt(20))
                && !this.worldObj.playerEntities.isEmpty()) {
            this.target = (Entity) this.worldObj.playerEntities
                    .get(this.rand.nextInt(this.worldObj.playerEntities.size()));
        } else {
            boolean flag = false;

            do {
                this.targetX = portalX;
                this.targetY = (double) (70.0F + this.rand.nextFloat() * 50.0F);
                this.targetZ = portalZ;
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

    private float simplifyAngle(double par1) {
        return (float) MathHelper.wrapAngleTo180_double(par1);
    }

    private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
        if (!ConfigHandler.dragonBreaksBlocks) return false;

        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    Block block = this.worldObj.getBlock(k1, l1, i2);

                    if (!block.isAir(worldObj, k1, l1, i2)) {
                        if (block.canEntityDestroy(worldObj, k1, l1, i2, this)
                                && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                            flag1 = this.worldObj.setBlockToAir(k1, l1, i2) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            double d1 = par1AxisAlignedBB.minX
                    + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double) this.rand.nextFloat();
            double d2 = par1AxisAlignedBB.minY
                    + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double) this.rand.nextFloat();
            double d0 = par1AxisAlignedBB.minZ
                    + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double) this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", d1, d2, d0, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    private void spawnEgg() {
        if (ConfigHandler.dragonEggSpawnLocation[0] != 0 || ConfigHandler.dragonEggSpawnLocation[1] != 0
                || ConfigHandler.dragonEggSpawnLocation[1] != 0 && !isUber) {
            portalX = ConfigHandler.dragonEggSpawnLocation[0];
            portalY = ConfigHandler.dragonEggSpawnLocation[1];
            portalZ = ConfigHandler.dragonEggSpawnLocation[2];
        }

        BlockEndPortal.field_149948_a = true;

        if (createPortal || isUber) {
            createEnderPortal(portalX, portalZ);
        }
        LogHelper.info("spawn egg");
        if (worldObj.getBlock(portalX, portalY + 1, portalZ) == Blocks.air) {
            worldObj.setBlock(portalX, portalY + 1, portalZ, Blocks.dragon_egg);
            LogHelper.info("spawn egg2 " + portalX + " " + portalY + " " + portalZ);
        } else {
            for (int i = portalY + 1; i < 250; i++) {
                if (worldObj.getBlock(portalX, i, portalZ) == Blocks.air) {
                    worldObj.setBlock(portalX, i, portalZ, Blocks.dragon_egg);
                    LogHelper.info("spawn egg3");
                    break;
                }
            }
        }

        for (int iX = portalX - 2; iX <= portalX + 2; iX++) {
            for (int iZ = portalZ - 2; iZ <= portalZ + 2; iZ++) {

                if (worldObj.getBlock(iX, portalY - 4, iZ) == Blocks.bedrock && !(iX == portalX && iZ == portalZ)) {
                    worldObj.setBlock(iX, portalY - 3, iZ, Blocks.end_portal);
                }
            }
        }

        worldObj.setBlock(portalX - 1, portalY - 1, portalZ, Blocks.torch);
        worldObj.setBlock(portalX + 1, portalY - 1, portalZ, Blocks.torch);
        worldObj.setBlock(portalX, portalY - 1, portalZ - 1, Blocks.torch);
        worldObj.setBlock(portalX, portalY - 1, portalZ + 1, Blocks.torch);

        BlockEndPortal.field_149948_a = false;
    }

    private void createEnderPortal(int par1, int par2) {
        int b0 = portalY - 3;
        byte b1 = 4;

        for (int k = b0 - 1; k <= b0 + 32; ++k) {
            for (int l = par1 - b1; l <= par1 + b1; ++l) {
                for (int i1 = par2 - b1; i1 <= par2 + b1; ++i1) {
                    double d0 = (double) (l - par1);
                    double d1 = (double) (i1 - par2);
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 <= ((double) b1 - 0.5D) * ((double) b1 - 0.5D)) {
                        if (k < b0) {
                            if (d2 <= ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
                                this.worldObj.setBlock(l, k, i1, Blocks.bedrock);
                            }
                        } else if (k > b0) {
                            this.worldObj.setBlock(l, k, i1, Blocks.air);
                        } else if (d2 > ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
                            this.worldObj.setBlock(l, k, i1, Blocks.bedrock);
                        }
                    }
                }
            }
        }

        this.worldObj.setBlock(par1, b0 + 0, par2, Blocks.bedrock);
        this.worldObj.setBlock(par1, b0 + 1, par2, Blocks.bedrock);
        this.worldObj.setBlock(par1, b0 + 2, par2, Blocks.bedrock);
        this.worldObj.setBlock(par1, b0 + 3, par2, Blocks.bedrock);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat("AttackDamage", attackDamage);
        compound.setBoolean("IsUber", isUber);
        compound.setBoolean("Initialized", initialized);
        if (isUber) {
            compound.setInteger("PortalX", portalX);
            compound.setInteger("PortalY", portalY);
            compound.setInteger("PortalZ", portalZ);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        attackDamage = compound.getFloat("AttackDamage");
        isUber = compound.getBoolean("IsUber");
        initialized = compound.getBoolean("Initialized");
        this.dataWatcher.updateObject(12, isUber ? (byte) 1 : (byte) 0);
        if (isUber) {
            portalX = compound.getInteger("PortalX");
            portalY = compound.getInteger("PortalY");
            portalZ = compound.getInteger("PortalZ");
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void despawnEntity() {}

    public void setUber(boolean b) {
        isUber = b;
        this.dataWatcher.updateObject(12, b ? (byte) 1 : (byte) 0);
    }

    public boolean getIsUber() {
        return isUber;
    }

    @Override
    public void travelToDimension(int p_71027_1_) {}
}
