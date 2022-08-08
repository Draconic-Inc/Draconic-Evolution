package com.brandon3055.draconicevolution.common.entity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.weapons.BowHandler;
import com.brandon3055.draconicevolution.common.network.GenericParticlePacket;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EntityCustomArrow extends EntityArrow {
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block blockHit;
    private int inData;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;

    // Arrow Properties
    public boolean ignorSpeed = false; // old
    public boolean explosive = false; // old
    public int knockbackStrength = 0;

    public BowHandler.BowProperties bowProperties = new BowHandler.BowProperties();

    public EntityCustomArrow(World p_i1753_1_) {
        super(p_i1753_1_);
        renderDistanceWeight = 40;
    }

    public EntityCustomArrow(World p_i1754_1_, double p_i1754_2_, double p_i1754_4_, double p_i1754_6_) {
        super(p_i1754_1_, p_i1754_2_, p_i1754_4_, p_i1754_6_);
        renderDistanceWeight = 40;
    }

    public EntityCustomArrow(
            World p_i1755_1_,
            EntityLivingBase p_i1755_2_,
            EntityLivingBase p_i1755_3_,
            float p_i1755_4_,
            float p_i1755_5_) {
        super(p_i1755_1_, p_i1755_2_, p_i1755_3_, p_i1755_4_, p_i1755_5_);
        renderDistanceWeight = 40;
    }

    public EntityCustomArrow(World par1World, EntityLivingBase par2EntityLivingBase, float velocity) {
        super(par1World, par2EntityLivingBase, velocity);
        renderDistanceWeight = 40;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(17, Byte.valueOf((byte) 0));
        // this.dataWatcher.updateObject(17, Byte.valueOf((byte)(bowProperties.energyBolt ? 1 : 0)));
    }

    @Override
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, f3) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void onUpdate() {
        // region Entity Update And motion
        super.onEntityUpdate();
        if (worldObj.isRemote) {
            bowProperties.energyBolt = dataWatcher.getWatchableObjectByte(17) == 1;
        } else dataWatcher.updateObject(17, (byte) (bowProperties.energyBolt ? 1 : 0));

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw =
                    this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI);
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        // endregion

        // region Block Collision Detection
        Block block = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ);

        if (block.getMaterial() != Material.air) {
            onHitAnything();
            block.setBlockBoundsBasedOnState(this.worldObj, this.blockX, this.blockY, this.blockZ);
            AxisAlignedBB axisalignedbb =
                    block.getCollisionBoundingBoxFromPool(this.worldObj, this.blockX, this.blockY, this.blockZ);

            if (axisalignedbb != null
                    && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }
        // endregion

        if (this.inGround) {
            int j = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);

            if (block == this.blockHit && j == this.inData) {
                ++this.ticksInGround;

                if (this.ticksInGround == 1200
                        || bowProperties
                                .energyBolt) { // Delete the entity when it hits the ground if it is an energy bolt
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            // region Detect Entity Hit
            ++this.ticksInAir;
            Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec3 = Vec3.createVectorHelper(
                    this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
            vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec3 = Vec3.createVectorHelper(
                    this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null) {
                vec3 = Vec3.createVectorHelper(
                        movingobjectposition.hitVec.xCoord,
                        movingobjectposition.hitVec.yCoord,
                        movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                    this,
                    this.boundingBox
                            .addCoord(this.motionX, this.motionY, this.motionZ)
                            .expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int i;
            float f1;

            for (i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5)) {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

                    if (movingobjectposition1 != null) {
                        double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null
                    && movingobjectposition.entityHit != null
                    && movingobjectposition.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

                if (entityplayer.capabilities.disableDamage
                        || this.shootingEntity instanceof EntityPlayer
                                && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
                    movingobjectposition = null;
                }
            }
            // endregion

            float velocity;
            float f4;

            // region Process Entity Hit
            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    onHitAnything();
                    if (isDead) return;
                    int actualDamage;
                    // Calculate Damage
                    velocity = MathHelper.sqrt_double(
                            this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    actualDamage = MathHelper.ceiling_double_int(velocity * bowProperties.arrowDamage);

                    if (bowProperties.energyBolt) actualDamage *= 1.1F;

                    if (this.getIsCritical()) {
                        actualDamage += this.rand.nextInt(actualDamage / 2 + 2);
                    }

                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        movingobjectposition.entityHit.setFire(5);
                    }

                    if (bowProperties.energyBolt) movingobjectposition.entityHit.hurtResistantTime = 0;
                    if (movingobjectposition.entityHit instanceof EntityDragonPart
                            && ((EntityDragonPart) movingobjectposition.entityHit).entityDragonObj
                                    instanceof EntityDragon
                            && bowProperties.energyBolt) {
                        ((EntityDragon) ((EntityDragonPart) movingobjectposition.entityHit).entityDragonObj)
                                .hurtResistantTime = 0;
                    }

                    if (movingobjectposition.entityHit.attackEntityFrom(getDamageSource(), actualDamage)) {
                        if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                            EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;

                            if (!this.worldObj.isRemote) {
                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                            }

                            if (this.knockbackStrength > 0) {
                                f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (f4 > 0.0F) {
                                    movingobjectposition.entityHit.addVelocity(
                                            this.motionX * this.knockbackStrength * 0.6000000238418579D / f4,
                                            0.1D,
                                            this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
                                }
                            }

                            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
                                EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
                                EnchantmentHelper.func_151385_b(
                                        (EntityLivingBase) this.shootingEntity, entitylivingbase);
                            }

                            if (this.shootingEntity != null
                                    && movingobjectposition.entityHit != this.shootingEntity
                                    && movingobjectposition.entityHit instanceof EntityPlayer
                                    && this.shootingEntity instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) this.shootingEntity)
                                        .playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                        this.setDead();

                    } else {
                        if (!(worldObj.isRemote
                                && ticksInAir
                                        < 5)) { // Fix arrow wobble on fire due to client side collision with shooting
                            // entity
                            this.motionX *= -0.10000000149011612D;
                            this.motionY *= -0.10000000149011612D;
                            this.motionZ *= -0.10000000149011612D;
                            this.rotationYaw += 180.0F;
                            this.prevRotationYaw += 180.0F;
                            this.ticksInAir = 0;
                        }
                    }
                } else {
                    this.blockX = movingobjectposition.blockX;
                    this.blockY = movingobjectposition.blockY;
                    this.blockZ = movingobjectposition.blockZ;
                    block = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ);
                    this.blockHit = block;
                    this.inData = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
                    this.motionX = ((float) (movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = ((float) (movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
                    velocity = MathHelper.sqrt_double(
                            this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / velocity * 0.05000000074505806D;
                    this.posY -= this.motionY / velocity * 0.05000000074505806D;
                    this.posZ -= this.motionZ / velocity * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;

                    if (this.blockHit.getMaterial() != Material.air) {
                        this.blockHit.onEntityCollidedWithBlock(
                                this.worldObj, this.blockX, this.blockY, this.blockZ, this);
                    }
                }
            }
            // endregion

            // region Motion
            if ((this.getIsCritical() || bowProperties.energyBolt) && worldObj.isRemote) {
                for (i = 0; i < 4; ++i) {
                    if (bowProperties.energyBolt) {
                        spawnArrowParticles();
                    } else {
                        this.worldObj.spawnParticle(
                                "crit",
                                this.posX + this.motionX * i / 4.0D,
                                this.posY + this.motionY * i / 4.0D,
                                this.posZ + this.motionZ * i / 4.0D,
                                -this.motionX,
                                -this.motionY + 0.2D,
                                -this.motionZ);
                    }
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, velocity) * 180.0D / Math.PI);
                    this.rotationPitch - this.prevRotationPitch < -180.0F;
                    this.prevRotationPitch -= 360.0F) {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f3 = 0.99F;
            f1 = bowProperties.energyBolt ? 0.025F : 0.05F;

            if (this.isInWater()) {
                for (int l = 0; l < 4; ++l) {
                    f4 = 0.25F;
                    this.worldObj.spawnParticle(
                            "bubble",
                            this.posX - this.motionX * f4,
                            this.posY - this.motionY * f4,
                            this.posZ - this.motionZ * f4,
                            this.motionX,
                            this.motionY,
                            this.motionZ);
                }

                f3 = 0.8F;
            }

            if (this.isWet()) {
                this.extinguish();
            }

            this.motionX *= f3;
            this.motionY *= f3;
            this.motionZ *= f3;
            this.motionY -= f1;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
            // endregion
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnArrowParticles() {
        Particles.ArrowParticle particle = new Particles.ArrowParticle(
                worldObj,
                posX - 0.25 + rand.nextDouble() * 0.5,
                posY + rand.nextDouble() * 0.5,
                posZ - 0.25 + rand.nextDouble() * 0.5,
                0xff6000,
                0.2F + rand.nextFloat() * 0.5f);
        double mm = 0.2;
        particle.motionX = (rand.nextDouble() - 0.5) * mm;
        particle.motionY = (rand.nextDouble() - 0.5) * mm;
        particle.motionZ = (rand.nextDouble() - 0.5) * mm;
        ParticleHandler.spawnCustomParticle(particle, 64);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("xTile", (short) this.blockX);
        compound.setShort("yTile", (short) this.blockY);
        compound.setShort("zTile", (short) this.blockZ);
        compound.setShort("life", (short) this.ticksInGround);
        compound.setByte("inTile", (byte) Block.getIdFromBlock(this.blockHit));
        compound.setByte("inData", (byte) this.inData);
        compound.setByte("shake", (byte) this.arrowShake);
        compound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        compound.setByte("pickup", (byte) this.canBePickedUp);
        if (bowProperties != null) bowProperties.writeToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.blockX = compound.getShort("xTile");
        this.blockY = compound.getShort("yTile");
        this.blockZ = compound.getShort("zTile");
        this.ticksInGround = compound.getShort("life");
        this.blockHit = Block.getBlockById(compound.getByte("inTile") & 255);
        this.inData = compound.getByte("inData") & 255;
        this.arrowShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;

        if (compound.hasKey("pickup", 99)) {
            this.canBePickedUp = compound.getByte("pickup");
        } else if (compound.hasKey("player", 99)) {
            this.canBePickedUp = compound.getBoolean("player") ? 1 : 0;
        }

        if (bowProperties != null) bowProperties.readFromNBT(compound);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {

            boolean flag =
                    this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;

            if (this.canBePickedUp == 1
                    && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1))) {
                flag = false;
            }

            if (flag) {
                this.playSound(
                        "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    public void onHitEntityLiving(EntityLivingBase entityLivingBase) {}

    public void onHitAnything() {
        if (bowProperties.explosionPower > 0 && !worldObj.isRemote) {
            worldObj.createExplosion(
                    this, prevPosX, prevPosY, prevPosZ, bowProperties.explosionPower, ConfigHandler.bowBlockDamage);
            setDead();
        }
        if (bowProperties.shockWavePower > 0 && !worldObj.isRemote) {
            DraconicEvolution.network.sendToAllAround(
                    new GenericParticlePacket(GenericParticlePacket.ARROW_SHOCK_WAVE, posX, posY, posZ, (int)
                            (bowProperties.shockWavePower * 100)),
                    new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 256));
            worldObj.playSoundEffect(
                    posX,
                    posY,
                    posZ,
                    "random.explode",
                    4.0F,
                    (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

            double range = (double) bowProperties.shockWavePower + 5;
            List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, boundingBox.expand(range, range, range));

            float damage = 40F * bowProperties.shockWavePower;

            for (Entity e : list) {
                if (e instanceof EntityLivingBase) {
                    Entity entity = e;
                    float distanceModifier = 1F - (entity.getDistanceToEntity(this) / bowProperties.shockWavePower);

                    if (e instanceof EntityDragon) {
                        entity = ((EntityDragon) entity).dragonPartBody;
                        distanceModifier = 1F - (entity.getDistanceToEntity(this) / (bowProperties.shockWavePower * 4));
                    }

                    if (distanceModifier > 0) entity.attackEntityFrom(getDamageSource(), distanceModifier * damage);
                }
            }

            setDead();
        }
    }

    private DamageSource getDamageSource() {
        if (bowProperties.energyBolt) {
            return new EntityDamageSourceIndirect(
                            "customArrowEnergy", this, shootingEntity != null ? shootingEntity : this)
                    .setProjectile()
                    .setDamageIsAbsolute();
        } else return DamageSource.causeArrowDamage(this, shootingEntity != null ? shootingEntity : this);
    }
}
