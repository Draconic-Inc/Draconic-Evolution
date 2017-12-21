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
    }

    @Override
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
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

        if (world.isRemote) {
            bowProperties.energyBolt = dataManager.get(IS_ENERGY);
        }
        else {
            dataManager.set(IS_ENERGY, bowProperties.energyBolt);
        }
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
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
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

//            posX += motionX;
//            posY += motionY;
//            posZ += motionZ;

            double range = (double) (bowProperties.shockWavePower + 5) * 1.5;
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).grow(range * 2));

            float damage = 40F * bowProperties.shockWavePower;

            for (Entity e : list) {
                if (e instanceof EntityLivingBase) {
                    Entity entity = e;
                    float distanceModifier = 1F - (entity.getDistance(this) / (float) range);

                    if (e instanceof EntityDragon) {
                        entity = ((EntityDragon) entity).dragonPartBody;
                        distanceModifier = 1F - (entity.getDistance(this) / (bowProperties.shockWavePower * 4));
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
