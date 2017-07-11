package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.handlers.BowHandler.BowProperties;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntityCustomArrow extends EntityArrow {
//    private BlockPos blockHitPos = new BlockPos(0, -1, 0);
//    private Block blockHit;
//    private int inData;
//    private boolean inGround;
//    private int ticksInGround;
//    private int ticksInAir;

    //Arrow Properties
//    public boolean ignorSpeed = false;    //old
//    public boolean explosive = false;    //old
//    public int knockbackStrength = 0;
    private static final DataParameter<Boolean> IS_ENERGY = EntityDataManager.<Boolean>createKey(EntityCustomArrow.class, DataSerializers.BOOLEAN);

    public BowProperties bowProperties = new BowProperties();

    public EntityCustomArrow(World worldIn) {
        super(worldIn);
    }

    public EntityCustomArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCustomArrow(BowProperties bowProperties, World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.bowProperties = bowProperties;
    }

    //region Arrow Stuffs

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IS_ENERGY, false);
        //this.dataWatcher.updateObject(17, Byte.valueOf((byte)(bowProperties.energyBolt ? 1 : 0)));
    }

    @Override
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
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
        float f3 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, f3) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onUpdate() {
        super.onUpdate();


        //region Entity Update And motion
        if (world.isRemote) {
            bowProperties.energyBolt = dataManager.get(IS_ENERGY);
        }
        else {
            dataManager.set(IS_ENERGY, bowProperties.energyBolt);
        }
//
//
//        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
//        {
//            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//            this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
//            this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
//        }
//
//        if (this.arrowShake > 0) {
//            --this.arrowShake;
//        }
//        //endregion
//
//        //region Block Collision Detection
//        IBlockState iblockstate = this.world.getBlockState(blockHitPos);
//        Block block = iblockstate.getBlock();
//
//        if (iblockstate.getMaterial() != Material.AIR)
//        {
//            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockHitPos);
//
//            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockHitPos).isVecInside(new Vec3d(this.posX, this.posY, this.posZ)))
//            {
//                this.inGround = true;
//            }
//        }
//        //endregion
//
//        if (this.inGround) {
//            IBlockState blockState = world.getBlockState(blockHitPos);
//            int j = blockState.getBlock().getMetaFromState(blockState);
//
//            if (block == this.blockHit && j == this.inData) {
//                ++this.ticksInGround;
//
//                if (this.ticksInGround >= 1200 || bowProperties.energyBolt) { //Delete the entity when it hits the ground if it is an energy bolt
//                    this.setDead();
//                }
//            } else {
//                this.inGround = false;
//                this.motionX *= this.rand.nextFloat() * 0.2F;
//                this.motionY *= this.rand.nextFloat() * 0.2F;
//                this.motionZ *= this.rand.nextFloat() * 0.2F;
//                this.ticksInGround = 0;
//                this.ticksInAir = 0;
//            }
//        } else {
//            //region Detect Entity Hit
//            this.timeInGround = 0;
//            ++this.ticksInAir;
//            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
//            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
//            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
//            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//
//            if (raytraceresult != null) {
//                vec3d = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
//            }

//            Entity entity = null;
//            List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
//            double d0 = 0.0D;
//            int i;
//            float f1;
//
//            for (i = 0; i < list.size(); ++i) {
//                Entity entity1 = (Entity) list.get(i);
//
//                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5)) {
//                    f1 = 0.3F;
//                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
//                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);
//
//                    if (movingobjectposition1 != null) {
//                        double d1 = vec31.distanceTo(movingobjectposition1.hitVec);
//
//                        if (d1 < d0 || d0 == 0.0D) {
//                            entity = entity1;
//                            d0 = d1;
//                        }
//                    }
//                }
//            }
//
//            if (entity != null) {
//                movingobjectposition = new MovingObjectPosition(entity);
//            }
//
//            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer) {
//                EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;
//
//                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
//                    movingobjectposition = null;
//                }
//            }
        //endregion

//            float velocity;
//            float f4;
//
//            //region Process Entity Hit
//            if (movingobjectposition != null) {
//                if (movingobjectposition.entityHit != null) {
//                    onHitAnything();
//                    if (isDead) return;
//                    int actualDamage;
//                    //Calculate Damage
//                    velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
//                    actualDamage = MathHelper.ceiling_double_int(velocity * bowProperties.arrowDamage);
//
//                    if (bowProperties.energyBolt) actualDamage *= 1.1F;
//
//                    if (this.getIsCritical()) {
//                        actualDamage += this.rand.nextInt(actualDamage / 2 + 2);
//                    }
//
//                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
//                        movingobjectposition.entityHit.setFire(5);
//                    }
//
//                    if (bowProperties.energyBolt) movingobjectposition.entityHit.hurtResistantTime = 0;
//                    if (movingobjectposition.entityHit instanceof EntityDragonPart && ((EntityDragonPart) movingobjectposition.entityHit).entityDragonObj instanceof EntityDragon && bowProperties.energyBolt) {
//                        ((EntityDragon) ((EntityDragonPart) movingobjectposition.entityHit).entityDragonObj).hurtResistantTime = 0;
//                    }
//
//                    if (movingobjectposition.entityHit.attackEntityFrom(getDamageSource(), actualDamage)) {
//                        if (movingobjectposition.entityHit instanceof EntityLivingBase) {
//                            EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;
//
//                            if (!this.world.isRemote) {
//                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
//                            }
//
//                            if (this.knockbackStrength > 0) {
//                                f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//
//                                if (f4 > 0.0F) {
//                                    movingobjectposition.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6000000238418579D / f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
//                                }
//                            }
//
//                            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
//                                EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
//                                EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity, entitylivingbase);
//                            }
//
//                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
//                                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
//                            }
//                        }
//
//                        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//                        this.setDead();
//
//                    } else {
//                        if (!(world.isRemote && ticksInAir < 5)) { //Fix arrow wobble on fire due to client side collision with shooting entity
//                            this.motionX *= -0.10000000149011612D;
//                            this.motionY *= -0.10000000149011612D;
//                            this.motionZ *= -0.10000000149011612D;
//                            this.rotationYaw += 180.0F;
//                            this.prevRotationYaw += 180.0F;
//                            this.ticksInAir = 0;
//                        }
//                    }
//                } else {
//                    this.blockX = movingobjectposition.blockX;
//                    this.blockY = movingobjectposition.blockY;
//                    this.blockZ = movingobjectposition.blockZ;
//                    block = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
//                    this.blockHit = block;
//                    this.inData = this.world.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
//                    this.motionX = ((float) (movingobjectposition.hitVec.xCoord - this.posX));
//                    this.motionY = ((float) (movingobjectposition.hitVec.yCoord - this.posY));
//                    this.motionZ = ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
//                    velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
//                    this.posX -= this.motionX / velocity * 0.05000000074505806D;
//                    this.posY -= this.motionY / velocity * 0.05000000074505806D;
//                    this.posZ -= this.motionZ / velocity * 0.05000000074505806D;
//                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//                    this.inGround = true;
//                    this.arrowShake = 7;
//
//                    if (this.blockHit.getMaterial() != Material.air) {
//                        this.blockHit.onEntityCollidedWithBlock(this.world, this.blockX, this.blockY, this.blockZ, this);
//                    }
//                }
//            }
//            //endregion
//
//            //region Motion
//            if ((this.getIsCritical() || bowProperties.energyBolt) && world.isRemote) {
//                for (i = 0; i < 4; ++i) {
//                    if (bowProperties.energyBolt) {
//                        spawnArrowParticles();
//                    } else {
//                        this.world.spawnParticle("crit", this.posX + this.motionX * i / 4.0D, this.posY + this.motionY * i / 4.0D, this.posZ + this.motionZ * i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
//                    }
//                }
//            }
//
//            this.posX += this.motionX;
//            this.posY += this.motionY;
//            this.posZ += this.motionZ;
//            velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
//
//            for (this.rotationPitch = (float) (Math.atan2(this.motionY, velocity) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
//                ;
//            }
//
//            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
//                this.prevRotationPitch += 360.0F;
//            }
//
//            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
//                this.prevRotationYaw -= 360.0F;
//            }
//
//            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
//                this.prevRotationYaw += 360.0F;
//            }
//
//            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
//            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
//            float f3 = 0.99F;
////            f1 = bowProperties.energyBolt ? 0.025F : 0.05F;
//
//            if (this.isInWater()) {
//                for (int l = 0; l < 4; ++l) {
//                    f4 = 0.25F;
//                    this.world.spawnParticle("bubble", this.posX - this.motionX * f4, this.posY - this.motionY * f4, this.posZ - this.motionZ * f4, this.motionX, this.motionY, this.motionZ);
//                }
//
//                f3 = 0.8F;
//            }
//
//            if (this.isWet()) {
//                this.extinguish();
//            }
//
//            this.motionX *= f3;
//            this.motionY *= f3;
//            this.motionZ *= f3;
//            this.motionY -= f1;
//            this.setPosition(this.posX, this.posY, this.posZ);
//            this.func_145775_I();
//            //endregion
//        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean flag = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && entityIn.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED && !entityIn.inventory.addItemStackToInventory(getArrowStack())) {
                flag = false;
            }

            if (flag) {
                this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityIn.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    //endregion

    @Override
    @Nullable
    protected Entity findEntityOnPath(Vec3d start, Vec3d end)
    {
        if (world.isRemote) {
            return null;
        }
        Entity entity = null;
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = (Entity)list.get(i);

            if (entity1 != this.shootingEntity || this.ticksInAir >= 10)
            {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

                if (raytraceresult != null)
                {
                    double d1 = start.squareDistanceTo(raytraceresult.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    @SideOnly(Side.CLIENT)
    private void spawnArrowParticles() {
//        Particles.ArrowParticle particle = new Particles.ArrowParticle(world, posX - 0.25 + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ - 0.25 + rand.nextDouble() * 0.5, 0xff6000, 0.2F + rand.nextFloat() * 0.5f);
//        double mm = 0.2;
//        particle.motionX = (rand.nextDouble() - 0.5) * mm;
//        particle.motionY = (rand.nextDouble() - 0.5) * mm;
//        particle.motionZ = (rand.nextDouble() - 0.5) * mm;
//        ParticleHandler.spawnCustomParticle(particle, 64);
    }

    //region Save

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
//        compound.setShort("xTile", (short) blockHitPos.getX());
//        compound.setShort("yTile", (short) blockHitPos.getY());
//        compound.setShort("zTile", (short) blockHitPos.getZ());
//        compound.setShort("life", (short) this.ticksInGround);
//        compound.setByte("inTile", (byte) Block.getIdFromBlock(this.blockHit));
//        compound.setByte("inData", (byte) this.inData);
//        compound.setByte("shake", (byte) this.arrowShake);
//        compound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
//        compound.setByte("pickup", (byte) pickupStatus.ordinal());
        super.writeEntityToNBT(compound);
        if (bowProperties != null) {
            bowProperties.writeToNBT(compound);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
//        blockHitPos = new BlockPos(compound.getShort("xTile"), compound.getShort("yTile"), compound.getShort("zTile"));
//        this.ticksInGround = compound.getShort("life");
//        this.blockHit = Block.getBlockById(compound.getByte("inTile") & 255);
//        this.inData = compound.getByte("inData") & 255;
//        this.arrowShake = compound.getByte("shake") & 255;
//        this.inGround = compound.getByte("inGround") == 1;
//
//        if (compound.hasKey("pickup", 99))
//        {
//            this.pickupStatus = EntityArrow.PickupStatus.getByOrdinal(compound.getByte("pickup"));
//        }
//        else if (compound.hasKey("player", 99))
//        {
//            this.pickupStatus = compound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
//        }
        super.readEntityFromNBT(compound);
        if (bowProperties != null) {
            bowProperties.readFromNBT(compound);
        }
    }

    //endregion

    @Override
    protected void onHit(RayTraceResult traceResult) {

        if (bowProperties.explosionPower > 0 && !world.isRemote) {
            Explosion explosion = new Explosion(world, this, prevPosX, prevPosY, prevPosZ, bowProperties.explosionPower, false, DEConfig.bowBlockDamage) {
                @Override
                public EntityLivingBase getExplosivePlacedBy() {
                    return shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null;
                }
            };
            if (!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) {
                explosion.doExplosionA();
                explosion.doExplosionB(true);
                explosion.clearAffectedBlockPositions();

                for (EntityPlayer entityplayer : world.playerEntities) {
                    if (entityplayer.getDistanceSq(prevPosX, prevPosY, prevPosZ) < 4096.0D) {
                        ((EntityPlayerMP) entityplayer).connection.sendPacket(new SPacketExplosion(prevPosX, prevPosY, prevPosZ, bowProperties.explosionPower, explosion.getAffectedBlockPositions(), (Vec3d) explosion.getPlayerKnockbackMap().get(entityplayer)));
                    }
                }
            }

//            world.createExplosion(this, prevPosX, prevPosY, prevPosZ, bowProperties.explosionPower, DEConfig.bowBlockDamage);
            setDead();
        }

        //region Shock Wave
        if (bowProperties.shockWavePower > 0 && !world.isRemote) {
            //DraconicEvolution.network.sendToAllAround(new GenericParticlePacket(GenericParticlePacket.ARROW_SHOCK_WAVE, posX, posY, posZ, (int) (bowProperties.shockWavePower * 100)), new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 256));
            //world.playSoundEffect(posX, posY, posZ, "random.explode", 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
            BCEffectHandler.spawnFX(DEParticles.ARROW_SHOCKWAVE, world, posX, posY, posZ, 0, 0, 0, 256D, (int) (bowProperties.shockWavePower * 100));

            double range = (double) bowProperties.shockWavePower + 5;
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().expand(range, range, range));

            float damage = 40F * bowProperties.shockWavePower;

            for (Entity e : list) {
                if (e instanceof EntityLivingBase) {
                    Entity entity = e;
                    float distanceModifier = 1F - (entity.getDistanceToEntity(this) / (float) range);

                    if (e instanceof EntityDragon) {
                        entity = ((EntityDragon) entity).dragonPartBody;
                        distanceModifier = 1F - (entity.getDistanceToEntity(this) / (bowProperties.shockWavePower * 4));
                    }

                    if (distanceModifier > 0) {
                        entity.attackEntityFrom(getDamageSource(), distanceModifier * damage);
                    }
                }
            }

            setDead();
        }
        //endregion

        if (traceResult.entityHit != null) {
            if (isDead) {
                return;
            }

            int actualDamage;
            //Calculate Damage
            float velocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            actualDamage = MathHelper.ceil(velocity * bowProperties.arrowDamage);

            if (bowProperties.energyBolt) {
                actualDamage *= 1.1F;
            }

            if (bowProperties.energyBolt) {
                traceResult.entityHit.hurtResistantTime = 0;
            }

            if (traceResult.entityHit instanceof MultiPartEntityPart && ((MultiPartEntityPart) traceResult.entityHit).parent instanceof EntityDragon && bowProperties.energyBolt) {
                ((EntityDragon) ((MultiPartEntityPart) traceResult.entityHit).parent).hurtResistantTime = 0;
            }

            if (traceResult.entityHit.attackEntityFrom(getDamageSource(), actualDamage)) {
                if (traceResult.entityHit instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) traceResult.entityHit;

                    if (!this.world.isRemote) {
                        entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                    }

//                    if (this.knockbackStrength > 0) {
//                        f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//
//                        if (f4 > 0.0F) {
//                            traceResult.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6000000238418579D / f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
//                        }
//                    }

//                    if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
//                        EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
//                        EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity, entitylivingbase);
//                    }
//
//                    if (this.shootingEntity != null && traceResult.entityHit != this.shootingEntity && traceResult.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
//                        ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
//                    }
                }

//                this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
                this.setDead();

            }
        }
        else {
            BlockPos blockpos = traceResult.getBlockPos();
            xTile = blockpos.getX();
            yTile = blockpos.getY();
            zTile = blockpos.getZ();
            IBlockState iblockstate = world.getBlockState(blockpos);
            inTile = iblockstate.getBlock();
            inData = inTile.getMetaFromState(iblockstate);
            motionX = (double) ((float) (traceResult.hitVec.x - posX));
            motionY = (double) ((float) (traceResult.hitVec.y - posY));
            motionZ = (double) ((float) (traceResult.hitVec.z - posZ));
            float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            posX -= motionX / (double) f2 * 0.05000000074505806D;
            posY -= motionY / (double) f2 * 0.05000000074505806D;
            posZ -= motionZ / (double) f2 * 0.05000000074505806D;
            playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
            inGround = true;
            arrowShake = 7;
            setIsCritical(false);

            if (iblockstate.getMaterial() != Material.AIR) {
                inTile.onEntityCollidedWithBlock(world, blockpos, iblockstate, this);
            }
        }


    }


    private DamageSource getDamageSource() {
        if (bowProperties.energyBolt) {
            return new EntityDamageSourceIndirect("customArrowEnergy", this, shootingEntity != null ? shootingEntity : this).setProjectile().setDamageIsAbsolute();
        }
        else return DamageSource.causeArrowDamage(this, shootingEntity != null ? shootingEntity : this);
    }
}
