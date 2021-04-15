package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.handlers.BowHandler.BowProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;

@Deprecated
public class EntityCustomArrow extends ArrowEntity {

    private static final DataParameter<Boolean> IS_ENERGY = EntityDataManager.<Boolean>defineId(EntityCustomArrow.class, DataSerializers.BOOLEAN);

    public BowProperties bowProperties = new BowProperties();

    public EntityCustomArrow(EntityType<? extends ArrowEntity> p_i50172_1_, World p_i50172_2_) {
        super(p_i50172_1_, p_i50172_2_);
    }

    //    public EntityCustomArrow(World worldIn) {
//        super(worldIn);
//    }
//
//    public EntityCustomArrow(World worldIn, double x, double y, double z) {
//        super(worldIn, x, y, z);
//    }
//
//    public EntityCustomArrow(BowProperties bowProperties, World worldIn, LivingEntity shooter) {
//        super(worldIn, shooter);
//        this.bowProperties = bowProperties;
//    }

    //region Arrow Stuffs

//    @Override
//    protected void entityInit() {
//        super.entityInit();
//        dataManager.register(IS_ENERGY, false);
//    }

    @Override
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par3 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par5 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0007499999832361937D * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
//        this.motionX = par1;
//        this.motionY = par3;
//        this.motionZ = par5;
        float f3 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        this.yRotO = this.yRot = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.xRotO = this.xRot = (float) (Math.atan2(par3, f3) * 180.0D / Math.PI);
//        this.ticksInGround = 0;
    }

//    @SuppressWarnings("rawtypes")
//    @Override
//    public void onUpdate() {
//        super.onUpdate();
//
//        if (world.isRemote) {
//            bowProperties.energyBolt = dataManager.get(IS_ENERGY);
//        }
//        else {
//            dataManager.set(IS_ENERGY, bowProperties.energyBolt);
//        }
//    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void playerTouch(PlayerEntity entityIn) {
        if (!this.level.isClientSide && this.inGround && this.shakeTime <= 0) {
            boolean flag = this.pickup == ArrowEntity.PickupStatus.ALLOWED || this.pickup == ArrowEntity.PickupStatus.CREATIVE_ONLY && entityIn.abilities.instabuild;

            if (this.pickup == ArrowEntity.PickupStatus.ALLOWED && !entityIn.inventory.add(getPickupItem())) {
                flag = false;
            }

            if (flag) {
                this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityIn.take(this, 1);
                this.remove();
            }
        }
    }

    //endregion

//    @Override
//    @Nullable
//    protected Entity findEntityOnPath(Vec3d start, Vec3d end)
//    {
//        if (world.isRemote) {
//            return null;
//        }
//        Entity entity = null;
//        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
//        double d0 = 0.0D;
//
//        for (int i = 0; i < list.size(); ++i)
//        {
//            Entity entity1 = (Entity)list.get(i);
//
//            if (entity1 != this.shootingEntity || this.ticksInAir >= 10)
//            {
//                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
//                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
//
//                if (raytraceresult != null)
//                {
//                    double d1 = start.squareDistanceTo(raytraceresult.hitVec);
//
//                    if (d1 < d0 || d0 == 0.0D)
//                    {
//                        entity = entity1;
//                        d0 = d1;
//                    }
//                }
//            }
//        }
//
//        return entity;
//    }

    //region Save


    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (bowProperties != null) {
            bowProperties.writeToNBT(compound);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (bowProperties != null) {
            bowProperties.readFromNBT(compound);
        }
    }

    //endregion

    @Override
    protected void onHitEntity(EntityRayTraceResult traceResult) {

        if (bowProperties.explosionPower > 0 && !level.isClientSide) {
            Explosion explosion = new Explosion(level, this, xo, yo, zo, bowProperties.explosionPower, false, DEOldConfig.bowBlockDamage ? Explosion.Mode.BREAK : Explosion.Mode.NONE) {
                @Override
                public LivingEntity getSourceMob() {
                    return getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null;
                }
            };
            if (!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) {
                explosion.explode();
                explosion.finalizeExplosion(true);
                explosion.clearToBlow();

                for (PlayerEntity entityplayer : level.players()) {
                    if (entityplayer.distanceToSqr(xo, yo, zo) < 4096.0D) {
                        ((ServerPlayerEntity) entityplayer).connection.send(new SExplosionPacket(xo, yo, zo, bowProperties.explosionPower, explosion.getToBlow(), explosion.getHitPlayers().get(entityplayer)));
                    }
                }
            }

            remove();
        }

        //region Shock Wave
        if (bowProperties.shockWavePower > 0 && !level.isClientSide) {
            Vec3D hitPos = new Vec3D(this);
//            if (traceResult instanceof BlockRayTraceResult) {
//                hitPos = Vec3D.getCenter(((BlockRayTraceResult) traceResult).getPos());
//            }
//            else if (traceResult instanceof EntityRayTraceResult) {
                hitPos = new Vec3D(((EntityRayTraceResult) traceResult).getEntity());
//            }
//            Vec3D hitPos = traceResult.typeOfHit == RayTraceResult.Type.BLOCK ? Vec3D.getCenter(traceResult.getBlockPos()) : traceResult.typeOfHit == RayTraceResult.Type.ENTITY ? new Vec3D(traceResult.entityHit) : new Vec3D(this);

            //TODO Particles
//            BCEffectHandler.spawnFX(DEParticles.ARROW_SHOCKWAVE, world, hitPos.x, hitPos.y, hitPos.z, 0, 0, 0, 256D, (int) (bowProperties.shockWavePower * 100));

            double range = (double) (bowProperties.shockWavePower + 5) * 1.5;
            List<Entity> list = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(hitPos.x, hitPos.y, hitPos.z, hitPos.x, hitPos.y, hitPos.z).inflate(range * 2));

            float damage = 40F * bowProperties.shockWavePower;

            for (Entity e : list) {
                if (e instanceof LivingEntity) {
                    Entity entity = e;
                    float distanceModifier = 1F - (entity.distanceTo(this) / (float) range);

                    if (e instanceof EnderDragonEntity) {
//                        entity = ((EnderDragonEntity) entity).body;
//                        distanceModifier = 1F - (entity.getDistance(this) / (bowProperties.shockWavePower * 4));
                    }

                    if (distanceModifier > 0) {
                        DamageSource source = new IndirectEntityDamageSource("customArrowEnergy", this, getOwner() != null ? getOwner() : this).setProjectile().setExplosion().bypassMagic();
                        entity.hurt(source, distanceModifier * damage);
                    }
                }
            }

            remove();
        }
        //endregion

        if (traceResult instanceof EntityRayTraceResult) {
            if (!isAlive()) {
                return;
            }

            int actualDamage;
            //Calculate Damage
            double velocity = this.getDeltaMovement().length();
            actualDamage = MathHelper.ceil(velocity * bowProperties.arrowDamage);

            if (bowProperties.energyBolt) {
                actualDamage *= 1.1F;
            }

            if (bowProperties.energyBolt) {
                ((EntityRayTraceResult) traceResult).getEntity().invulnerableTime = 0;
            }

            if (((EntityRayTraceResult) traceResult).getEntity() instanceof EnderDragonPartEntity && ((EnderDragonPartEntity) ((EntityRayTraceResult) traceResult).getEntity()).parentMob != null && bowProperties.energyBolt) {
                ((EnderDragonEntity) ((EnderDragonPartEntity) ((EntityRayTraceResult) traceResult).getEntity()).parentMob).invulnerableTime = 0;
            }

            if (((EntityRayTraceResult) traceResult).getEntity().hurt(getDamageSource(), actualDamage)) {
                if (((EntityRayTraceResult) traceResult).getEntity() instanceof LivingEntity) {
                    LivingEntity entitylivingbase = (LivingEntity) ((EntityRayTraceResult) traceResult).getEntity();

                    if (!this.level.isClientSide) {
                        entitylivingbase.setArrowCount(entitylivingbase.getArrowCount() + 1);
                    }

//                    if (this.knockbackStrength > 0) {
//                        f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//
//                        if (f4 > 0.0F) {
//                            traceResult.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6000000238418579D / f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
//                        }
//                    }

//                    if (this.shootingEntity != null && this.shootingEntity instanceof LivingEntity) {
//                        EnchantmentHelper.doPostHurtEffects(entitylivingbase, this.shootingEntity);
//                        EnchantmentHelper.doPostDamageEffects((LivingEntity) this.shootingEntity, entitylivingbase);
//                    }
//
//                    if (this.shootingEntity != null && traceResult.entityHit != this.shootingEntity && traceResult.entityHit instanceof PlayerEntity && this.shootingEntity instanceof ServerPlayerEntity) {
//                        ((ServerPlayerEntity) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
//                    }
                }

//                this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
                this.remove();

            }
        }
        else {
//            BlockPos blockpos = traceResult.getBlockPos();
//            xTile = blockpos.getX();
//            yTile = blockpos.getY();
//            zTile = blockpos.getZ();
//            BlockState iblockstate = world.getBlockState(blockpos);
//            inTile = iblockstate.getBlock();
//            inData = inTile.getMetaFromState(iblockstate);
//            motionX = (double) ((float) (traceResult.hitVec.x - posX));
//            motionY = (double) ((float) (traceResult.hitVec.y - posY));
//            motionZ = (double) ((float) (traceResult.hitVec.z - posZ));
//            float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
//            posX -= motionX / (double) f2 * 0.05000000074505806D;
//            posY -= motionY / (double) f2 * 0.05000000074505806D;
//            posZ -= motionZ / (double) f2 * 0.05000000074505806D;
//            playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
//            inGround = true;
//            arrowShake = 7;
//            setIsCritical(false);
//
//            if (iblockstate.getMaterial() != Material.AIR) {
//                inTile.onEntityCollidedWithBlock(world, blockpos, iblockstate, this);
//            }
        }
    }


    private DamageSource getDamageSource() {
        if (bowProperties.energyBolt) {
            return new IndirectEntityDamageSource("customArrowEnergy", this, getOwner() != null ? getOwner() : this).setProjectile().bypassMagic();
        }
        else return DamageSource.arrow(this, getOwner() != null ? getOwner() : this);
    }
}
