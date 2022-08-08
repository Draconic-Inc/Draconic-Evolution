package com.brandon3055.draconicevolution.common.entity;

import com.brandon3055.brandonscore.common.utills.Utills;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderArrow extends EntityDraconicArrow {
    private int field_145791_d = -1;
    private int field_145792_e = -1;
    private int field_145789_f = -1;
    private Block field_145790_g;
    private int inData;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;
    private double damage = 2.0D;
    /**
     * The amount of knockback an arrow applies when it hits a mob.
     */
    private int knockbackStrength;

    public boolean ignorSpeed = false;
    public boolean explosive = false;

    public EntityEnderArrow(World p_i1753_1_) {
        super(p_i1753_1_);
    }

    public EntityEnderArrow(World p_i1754_1_, double p_i1754_2_, double p_i1754_4_, double p_i1754_6_) {
        super(p_i1754_1_, p_i1754_2_, p_i1754_4_, p_i1754_6_);
    }

    public EntityEnderArrow(
            World p_i1755_1_,
            EntityLivingBase p_i1755_2_,
            EntityLivingBase p_i1755_3_,
            float p_i1755_4_,
            float p_i1755_5_) {
        super(p_i1755_1_, p_i1755_2_, p_i1755_3_, p_i1755_4_, p_i1755_5_);
    }

    public EntityEnderArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    @Override
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
        par3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
        par5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
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
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double par1, double par3, double par5) {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < 10; i++)
            this.worldObj.spawnParticle(
                    "portal",
                    this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                    this.posY + this.rand.nextDouble() * (double) this.height - 0.25D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                    (this.rand.nextDouble() - 0.5D) * 2.0D,
                    -this.rand.nextDouble(),
                    (this.rand.nextDouble() - 0.5D) * 2.0D);

        super.onEntityUpdate();
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw =
                    this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI);
        }

        Block block = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);

        if (block.getMaterial() != Material.air) {
            onImpact(null);
            block.setBlockBoundsBasedOnState(
                    this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(
                    this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);

            if (axisalignedbb != null
                    && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            this.setDead();
        } else {
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

            if (entity == shootingEntity) entity = null;

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

            float f2;
            float f4;

            if (movingobjectposition != null) {
                //				if (movingobjectposition.entityHit != null && movingobjectposition.entityHit != shootingEntity) {
                //					onImpact(movingobjectposition.entityHit);
                //					int k;
                //					if (!ignorSpeed) {
                //						f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY +
                // this.motionZ * this.motionZ);
                //						k = MathHelper.ceiling_double_int(f2 * this.damage);
                //
                //						if (this.getIsCritical()) {
                //							k += this.rand.nextInt(k / 2 + 2);
                //						}
                //					} else {
                //						k = (int) this.damage;
                //						if (this.getIsCritical()) {
                //							k += this.rand.nextInt(k / 2 + 2);
                //						}
                //					}
                //
                //					DamageSource damagesource = null;
                //
                //					if (this.shootingEntity == null) {
                //						damagesource = DamageSource.causeArrowDamage(this, this);
                //					} else {
                //						damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
                //					}
                //					if (movingobjectposition.entityHit instanceof EntityEnderman)
                //						damagesource = DamageSource.magic;
                //
                //					if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                //						movingobjectposition.entityHit.setFire(5);
                //					}
                //
                //					movingobjectposition.entityHit.hurtResistantTime = 0;
                //
                //					if (movingobjectposition.entityHit.attackEntityFrom(damagesource, k)) {
                //						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                //							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;
                //
                //							if (!this.worldObj.isRemote) {
                //								entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                //							}
                //
                //							if (this.knockbackStrength > 0) {
                //								f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                //
                //								if (f4 > 0.0F) {
                //									movingobjectposition.entityHit.addVelocity(this.motionX * this.knockbackStrength *
                // 0.6000000238418579D / f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
                //								}
                //							}
                //
                //							if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
                //								EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
                //								EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity, entitylivingbase);
                //							}
                //
                //							if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity &&
                // movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof
                // EntityPlayerMP) {
                //								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new
                // S2BPacketChangeGameState(6, 0.0F));
                //							}
                //						}
                //
                //						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                //
                //						// if (!(movingobjectposition.entityHit instanceof EntityEnderman))
                //						//{
                //						this.setDead();
                //						//}
                //					} else {
                //						this.motionX *= -0.10000000149011612D;
                //						this.motionY *= -0.10000000149011612D;
                //						this.motionZ *= -0.10000000149011612D;
                //						this.rotationYaw += 180.0F;
                //						this.prevRotationYaw += 180.0F;
                //						this.ticksInAir = 0;
                //					}
                //				} else
                if (movingobjectposition.entityHit == null) {
                    this.field_145791_d = movingobjectposition.blockX;
                    this.field_145792_e = movingobjectposition.blockY;
                    this.field_145789_f = movingobjectposition.blockZ;
                    this.field_145790_g = block;
                    this.inData = this.worldObj.getBlockMetadata(
                            this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.motionX = ((float) (movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = ((float) (movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
                    f2 = MathHelper.sqrt_double(
                            this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / f2 * 0.05000000074505806D;
                    this.posY -= this.motionY / f2 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / f2 * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);

                    if (this.field_145790_g.getMaterial() != Material.air) {
                        this.field_145790_g.onEntityCollidedWithBlock(
                                this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
                    }
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, f2) * 180.0D / Math.PI);
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
            f1 = 0.05F;

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
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setShort("xTile", (short) this.field_145791_d);
        par1NBTTagCompound.setShort("yTile", (short) this.field_145792_e);
        par1NBTTagCompound.setShort("zTile", (short) this.field_145789_f);
        par1NBTTagCompound.setShort("life", (short) this.ticksInGround);
        par1NBTTagCompound.setByte("inTile", (byte) Block.getIdFromBlock(this.field_145790_g));
        par1NBTTagCompound.setByte("inData", (byte) this.inData);
        par1NBTTagCompound.setByte("shake", (byte) this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        par1NBTTagCompound.setByte("pickup", (byte) this.canBePickedUp);
        par1NBTTagCompound.setDouble("damage", this.damage);
        // par1NBTTagCompound.setString("shooter", getShooter() instanceof EntityPlayer ? ((EntityPlayer)
        // getShooter()).getCommandSenderName() : "");
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        this.field_145791_d = par1NBTTagCompound.getShort("xTile");
        this.field_145792_e = par1NBTTagCompound.getShort("yTile");
        this.field_145789_f = par1NBTTagCompound.getShort("zTile");
        this.ticksInGround = par1NBTTagCompound.getShort("life");
        this.field_145790_g = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        this.inData = par1NBTTagCompound.getByte("inData") & 255;
        this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
        // dataWatcher.updateObject(SHOOTER_DATAWATCHER_INDEX, par1NBTTagCompound.hasKey("shooter") ?
        // par1NBTTagCompound.getString("shooter") : "");

        if (par1NBTTagCompound.hasKey("damage", 99)) {
            this.damage = par1NBTTagCompound.getDouble("damage");
        }

        if (par1NBTTagCompound.hasKey("pickup", 99)) {
            this.canBePickedUp = par1NBTTagCompound.getByte("pickup");
        } else if (par1NBTTagCompound.hasKey("player", 99)) {
            this.canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1 : 0;
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

    protected void onImpact(Entity entityHit) {
        if (shootingEntity == null) return;
        double startX = shootingEntity.posX;
        double startY = shootingEntity.posY;
        double startZ = shootingEntity.posZ;

        for (int i = 0; i < 32; ++i) {
            this.worldObj.spawnParticle(
                    "portal",
                    this.posX,
                    this.posY + this.rand.nextDouble() * 2.0D,
                    this.posZ,
                    this.rand.nextGaussian(),
                    0.0D,
                    this.rand.nextGaussian());
        }

        if (!(shootingEntity instanceof EntityPlayer) || shootingEntity.isDead) return;
        if (entityHit != null) {
            entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), 0.0F);
        }

        if (!this.worldObj.isRemote) {
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityPlayerMP) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) this.shootingEntity;

                if (entityplayermp.playerNetServerHandler.func_147362_b().isChannelOpen()
                        && entityplayermp.worldObj == this.worldObj) {
                    int x = (int) Math.floor(posX);
                    int y = (int) Math.floor(posY);
                    int z = (int) Math.floor(posZ);

                    double travelDist = Utills.getDistanceAtoB(startX, startY, startZ, x, y, z);
                    float travelDmg = (float) (travelDist / 5D);
                    EnderTeleportEvent event =
                            new EnderTeleportEvent(entityplayermp, x + 0.5, y + 0.5, z + 0.5, travelDmg);
                    if (!MinecraftForge.EVENT_BUS.post(event)) {
                        if (this.shootingEntity.isRiding()) {
                            shootingEntity.mountEntity((Entity) null);
                        }
                        ((EntityPlayer) shootingEntity)
                                .setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
                        shootingEntity.fallDistance = 0.0F;
                        shootingEntity.attackEntityFrom(DamageSource.fall, (float) (travelDist / 5D));
                    }
                }
            }
        }
        this.setDead();
    }
}
