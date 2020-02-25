package com.brandon3055.draconicevolution.entity;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.brandonscore.utils.FilterUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by brandon3055 on 23/8/2015.
 */
public class EntityGuardianProjectile extends Entity {

    protected static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityGuardianProjectile.class, DataSerializers.BYTE);
    public int type = 0;
    public LivingEntity target;
    public Entity shooter;
    public float power;
    public boolean isChaser;
    private double lastTickTargetDistance = 100;
    private float heath = 5F;
    private DamageSource damageFireball = new DamageSource("de.GuardianFireball").setDamageAllowedInCreativeMode().setMagicDamage().setExplosion();
    private DamageSource damageEnergy = new DamageSource("de.GuardianEnergyBall").setDamageAllowedInCreativeMode().setDamageBypassesArmor();
    private DamageSource damageChaos = new DamageSource("de.GuardianChaosBall").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

    //public static final int FIREBALL = 0; 			/** Generic fireball a lot more powerful then ghast fireball */
    public static final int FIREBOMB = 1;
    /**
     * Large fireball with a trail and large fiery AOE TODO Update Doc
     */
    public static final int TELEPORT = 2;
    /**
     * Ender pearl which teleports the player away if it hits. TODO Update Doc
     */
    public static final int FIRE_CHASER = 3;
    /**
     * Fireball that chases the player it is fired at (Explodes on impact with blocks) TODO Update Doc
     */
    public static final int ENERGY_CHASER = 4;
    /**
     * Energy gall that chases the player. (Can pass through blocks) TODO Update Doc
     */
    public static final int CHAOS_CHASER = 5;
    /**
     * Chases player. On impact splits into mini chaos charges which lock on to other or the same player (can pass through blocks) TODO Update Doc
     */
    public static final int MINI_CHAOS_CHASER = 6;
    /**
     * ^
     */
    public static final int IGNITION_CHARGE = 7;

    /**
     * Reignites Crystals
     */
//    public EntityGuardianProjectile(World world) {
//        this(world, 0, null, 10, null);
//    }
    public EntityGuardianProjectile(EntityType<?> entityTypeIn, World world, int type, LivingEntity target, float power, Entity shooter) {
        super(entityTypeIn, world);
        this.type = type;
        this.target = target;
        this.shooter = shooter;
        this.power = power;
        this.isChaser = type == FIRE_CHASER || type == ENERGY_CHASER || type == CHAOS_CHASER || type == MINI_CHAOS_CHASER || type == IGNITION_CHARGE;
//        this.setSize(1F, 1F);
//
//        if (shooter != null) {
//            if (!world.isRemote) {
//                DESoundHandler.playSoundFromServer(world, shooter.posX + 0.5D, shooter.posY + 0.5D, shooter.posZ + 0.5D, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, rand.nextFloat() * 0.3F + 0.85F, false, 256);
//            }
//
//            this.rotationYaw = shooter instanceof EntityChaosGuardian ? shooter.rotationYaw + 180F : shooter.rotationYaw;
//            this.rotationPitch = shooter.rotationPitch;
//            if (type == FIREBOMB || type == TELEPORT) {
//                rotationPitch += (rand.nextFloat() - 0.5F) * 20F;
//                rotationYaw += (rand.nextFloat() - 0.5F) * 20F;
//            }
////            this.yOffset = 0.0F;
//            this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
//            this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
//            this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI));
//            double speed = 5;
//            this.motionX *= speed;
//            this.motionY *= speed;
//            this.motionZ *= speed;
//        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return null;
    }

    //    @OnlyIn(Dist.CLIENT)
//    public boolean isInRangeToRenderDist(double distance) {
//        double d0 = this.getEntityBoundingBox().getAverageEdgeLength();
//
//        if (Double.isNaN(d0)) {
//            d0 = 1.0D;
//        }
//
//        d0 = d0 * 64.0D * 100D;
//        return distance < d0 * d0;
//    }

//    @Override
//    protected void entityInit() {
//        if (type == ENERGY_CHASER || type == CHAOS_CHASER || type == MINI_CHAOS_CHASER || type == IGNITION_CHARGE || world.isRemote) {
//            noClip = true;
//        }
//        dataManager.register(TYPE, (byte) type);
//    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote && ticksExisted == 1) {
            dataManager.set(TYPE, (byte) type);
        }
        if (world.isRemote) {
            if (type == 0) {
                type = (int) dataManager.get(TYPE);
            }
            spawnParticle();
        }

        //Check that there is still a target available and if not kills the projectile.
        if (target == null) {
            target = Utils.getClosestPlayer(world, posX, posY, posZ, 60, true);
            if (target == null) {
                if (!world.isRemote) {
                    remove();
                }
                return;
            }
        }

        if (isChaser && !world.isRemote) {
            double tDist = Utils.getDistanceAtoB(target.posX, target.posY, target.posZ, posX, posY, posZ);
            if (tDist <= 0) tDist = 0.1;

            double x = (target.posX - posX) / tDist;
            double y = (target.posY - posY - -1) / tDist;
            double z = (target.posZ - posZ) / tDist;
            double speed = type == CHAOS_CHASER ? 0.15D : 0.1D;

//            motionX /= 1.1;
//            motionY /= 1.1;
//            motionZ /= 1.1;
            setMotion(getMotion().mul(0.9, 0.9, 0.9));
            setMotion(getMotion().x + x * speed, getMotion().y + y * speed, getMotion().z + z * speed);
        }
        move(MoverType.SELF, getMotion());
        checkTargetCondition();
    }

    private boolean checkTargetCondition() {
        if (world.isRemote) {
            return false;
        }

        double targetDistance = Utils.getDistanceAtoB(posX, posY, posZ, target.posX, target.posY, target.posZ);

        Entity entityHit = getHitEntity();
        if (entityHit instanceof EnderDragonPartEntity) {
            entityHit = null;
        }

        boolean genericHit = entityHit != null || targetDistance <= 1;

        switch (type) {
            case FIREBOMB:
                if (genericHit || (targetDistance > lastTickTargetDistance && targetDistance < power) || collided || ticksExisted > 600 || heath <= 0) {
                    remove();
                    world.createExplosion(shooter, this.posX, this.posY, this.posZ, 2F, true, Explosion.Mode.BREAK);
                    damageEntitiesInRadius(damageFireball, power, power * 2);
                }
                break;
            case TELEPORT:
                if (genericHit) {
                    remove();
                    Entity hit = entityHit != null ? entityHit : target;
                    if (!(hit instanceof PlayerEntity)) {
                        break;
                    }
                    int r = rand.nextInt();
                    if (shooter != null) {
                        new Teleporter.TeleportLocation(shooter.posX + (Math.cos(r) * 600), rand.nextInt(255), shooter.posZ + (Math.sin(r) * 600), hit.dimension).teleport(hit);
                    } else {
                        new Teleporter.TeleportLocation(posX + (Math.cos(r) * 600), rand.nextInt(255), posZ + (Math.sin(r) * 600), hit.dimension).teleport(hit);
                    }

                    hit.attackEntityFrom(DamageSource.FALL, 10F);

                } else if (collided || ticksExisted > 400 || heath <= 0) {
                    remove();
                }
                break;
            case FIRE_CHASER:
                noClip = ticksExisted < 60;
                if (genericHit || (targetDistance > lastTickTargetDistance && targetDistance < power / 2) || (collided && ticksExisted > 60) || ticksExisted > 400 || heath <= 0) {
                    remove();
                    world.createExplosion(shooter, this.posX, this.posY, this.posZ, 2F, true, Explosion.Mode.BREAK);
                    damageEntitiesInRadius(damageFireball, power, power * 2);
                }
                break;
            case ENERGY_CHASER:
                if (genericHit || (targetDistance > lastTickTargetDistance && targetDistance < power) || ticksExisted > 800 || heath <= 0) {
                    remove();
                    //TODO Particles
//                    BCEffectHandler.spawnFX(DEParticles.GUARDIAN_PROJECTILE, world, posX, posY, posZ, 0, 0, 0, 256, this.getEntityId(), 0, 255, 255);
                    damageEntitiesInRadius(damageEnergy, power, power * 3);
                    world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                }
                break;
            case CHAOS_CHASER:

                if (genericHit || (targetDistance > lastTickTargetDistance && targetDistance < power) || ticksExisted > 800 || heath <= 0) {
                    remove();
                    //TODO Particles
//                    BCEffectHandler.spawnFX(DEParticles.GUARDIAN_PROJECTILE, world, posX, posY, posZ, 0, 0, 0, 256, this.getEntityId(), 0x44, 0, 0);
                    damageEntitiesInRadius(damageChaos, power, power * 3);
                    world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                    int i = 3 + rand.nextInt(3);
                    EntityGuardianProjectile newProjectile;
                    @SuppressWarnings("unchecked") List<Entity> list = world.getEntitiesInAABBexcluding(shooter, getBoundingBox().grow(60, 60, 60), FilterUtils.IS_PLAYER);
                    for (i = i; i > 0; i--) {
                        Entity target = list.size() > 0 ? list.get(rand.nextInt(list.size())) : null;

                        if (!(target instanceof LivingEntity)) {
                            target = null;
                        }

//                        newProjectile = new EntityGuardianProjectile(world, MINI_CHAOS_CHASER, (LivingEntity) target, power / 2F, shooter);
//                        newProjectile.motionY = 0;
//                        int randDir = rand.nextInt();
//                        double speed = 1 + rand.nextDouble() * 5;
//                        newProjectile.motionX = Math.sin(randDir) * speed;
//                        newProjectile.motionZ = Math.cos(randDir) * speed;
//                        newProjectile.setPosition(posX, posY, posZ);
//                        world.spawnEntity(newProjectile);
                    }
                }

                break;
            case MINI_CHAOS_CHASER:
                if ((genericHit || (targetDistance > lastTickTargetDistance && targetDistance < power) || ticksExisted > 800 || heath <= 0) && ticksExisted > 5) {
                    remove();
                    //TODO Particles
//                    BCEffectHandler.spawnFX(DEParticles.GUARDIAN_PROJECTILE, world, posX, posY, posZ, 0, 0, 0, 256, this.getEntityId(), 0x44, 0, 0);
                    //DraconicEvolution.network.sendToAllAround(new GenericParticlePacket(GenericParticlePacket.CHAOS_BALL_KILL, posX, posY, posZ), new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 128));
                    damageEntitiesInRadius(damageChaos, power, power * 3);
                    world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                }
                break;
            case IGNITION_CHARGE:
                if (targetDistance < 1) {
                    if (target instanceof EntityGuardianCrystal) {
                        ((EntityGuardianCrystal) target).revive();
                    }
                    remove();
                }
                break;
        }

        lastTickTargetDistance = targetDistance;
        return false;
    }

    private Entity getHitEntity() {
//        Vec3d vec31 = new Vec3d(this.posX, this.posY, this.posZ);
//        Vec3d vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//
//        Entity entityHit = null;
//        List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(1.0D, 1.0D, 1.0D));
//        double d0 = 0.0D;
//        int i;
//        float f1;
//
//        for (i = 0; i < list.size(); ++i) {
//            Entity entity1 = (Entity) list.get(i);
//
//            if (entity1.canBeCollidedWith() && (entity1 != this.shooter || this.ticksExisted >= 5)) {
//                f1 = 1F;
//                AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().grow(f1, f1, f1);
//                RayTraceResult traceResult = axisalignedbb1.calculateIntercept(vec31, vec3);
//
//                if (traceResult != null) {
//                    double d1 = vec31.distanceTo(traceResult.hitVec);
//
//                    if (d1 < d0 || d0 == 0.0D) {
//                        entityHit = entity1;
//                        d0 = d1;
//                    }
//                }
//            }
//        }

//        return entityHit instanceof EntityGuardianProjectile ? null : entityHit;
        return null;
    }

    private void damageEntitiesInRadius(DamageSource source, double radius, float damage) {
        if (world.isRemote) return;
        @SuppressWarnings("unchecked") List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).grow(radius, radius, radius));

        for (LivingEntity entityLivingBase : entities) {
            if (entityLivingBase == shooter) {
                continue;
            }
            entityLivingBase.hurtResistantTime = 0;
            entityLivingBase.attackEntityFrom(source, damage / (float) (Utils.getDistanceAtoB(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, posX, posY, posZ) / radius));
            if (source == damageChaos && entityLivingBase instanceof PlayerEntity) {
                for (ItemStack stack : ((PlayerEntity) entityLivingBase).inventory.armorInventory) {
                    if (stack.getItem() instanceof IEnergyContainerItem) {
                        ((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, 30000 + rand.nextInt(10000), false);
                    }
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float dmg) {
        if (heath <= 0) {
            return false;
        }
        if ((source.getTrueSource() instanceof PlayerEntity || source.getTrueSource() instanceof ArrowEntity) && ticksExisted > 5) {
            heath -= dmg;
        }
        if (source.getImmediateSource() instanceof ArrowEntity) {
            source.getImmediateSource().remove();
        }

        if (heath <= 0) {
            world.createExplosion(this, this.posX, this.posY, this.posZ, 2F, false, Explosion.Mode.BREAK);
            remove();
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticle() {
        int[] colour = getParticleColour();
        if (Arrays.equals(colour, new int[]{0, 0, 0})) {
            return;
        }

        //TODO Particles
//        BCEffectHandler.spawnFX(DEParticles.GUARDIAN_PROJECTILE, world, posX, posY, posZ, 0, 0, 0, 256D, this.getEntityId(), colour[0], colour[1], colour[2]);


//        Particles.DragonProjectileParticle particle = new Particles.DragonProjectileParticle(world, posX - 0.25 + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ - 0.25 + rand.nextDouble() * 0.5, this);
//        double mm = 0.2;
//        particle.motionX = (rand.nextDouble() - 0.5) * mm;
//        particle.motionY = (rand.nextDouble() - 0.5) * mm;
//        particle.motionZ = (rand.nextDouble() - 0.5) * mm;
//        ParticleHandler.spawnCustomParticle(particle, 64);
    }

    public int[] getParticleColour() {
        switch (type) {
            case FIREBOMB:
                return new int[]{0xFF, 0x66, 0x00};
            case TELEPORT:
                return new int[]{0, 0, 0};
            case FIRE_CHASER:
                return new int[]{0xFF, 0x66, 0};
            case ENERGY_CHASER:
                return new int[]{0, 0xFF, 0xFF};
            case CHAOS_CHASER:
                return new int[]{0x44, 0, 0};
            case MINI_CHAOS_CHASER:
                return new int[]{0x44, 0, 0};
            case IGNITION_CHARGE:
                return new int[]{0xFF, 0xFF, 0xFF};
        }
        return new int[]{0, 0, 0};
    }

//    @Override
//    public void onEntityUpdate() {
//        super.onEntityUpdate();
//    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public float getCollisionBorderSize() {
        return 1.0F;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        type = compound.getInt("Type");
        if (!world.isRemote) {
            dataManager.set(TYPE, (byte) type);
            //dataWatcher.updateObject(10, (byte) type);
        }
        noClip = type == ENERGY_CHASER || type == CHAOS_CHASER || type == MINI_CHAOS_CHASER || type == IGNITION_CHARGE;
        isChaser = type == FIRE_CHASER || type == ENERGY_CHASER || type == CHAOS_CHASER || type == MINI_CHAOS_CHASER || type == IGNITION_CHARGE;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("Type", type);
    }
}
