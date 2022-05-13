package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.damage.DraconicIndirectEntityDamage;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class GuardianProjectileEntity extends AbstractHurtingProjectile implements IEntityAdditionalSpawnData {
    private Vec3 target;
    private double splashRange = 15;
    private double power = 10;
    private DamageSource damageSource = new DamageSource("damage.draconicevolution.guardian_projectile").bypassMagic().bypassArmor().setMagic().setExplosion();
    private double closestApproach;

    public GuardianProjectileEntity(EntityType<?> type, Level world) {
        super(DEContent.guardianProjectile, world);
    }

    public GuardianProjectileEntity(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, Vec3 target, double splashRange, double power) {
        super(DEContent.guardianProjectile, shooter, accelX, accelY, accelZ, worldIn);
        this.target = target;
        this.splashRange = splashRange;
        this.power = power;
        this.damageSource = new DraconicIndirectEntityDamage("draconicevolution.guardian_projectile", this, shooter, TechLevel.CHAOTIC).bypassMagic().bypassArmor().setMagic().setExplosion();
        if (target != null) {
            closestApproach = distanceToSqr(target);
        }
        double accelDotProduct = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        if (accelDotProduct != 0.0D) {
            this.xPower = accelX / accelDotProduct * 0.3D;
            this.yPower = accelY / accelDotProduct * 0.3D;
            this.zPower = accelZ / accelDotProduct * 0.3D;
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        Entity shooter = this.getOwner();
        if (result.getType() != HitResult.Type.ENTITY || !((EntityHitResult) result).getEntity().is(shooter)) {
            if (!this.level.isClientSide) {
                detonate();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (target != null) {
            double distSq = distanceToSqr(target);
            if (distSq <= 1) {
                detonate();
            } else if (distSq < closestApproach) {
                closestApproach = distSq;
            } else if (tickCount > 5) {
                detonate();
            }
        }
    }

    private void detonate() {
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(splashRange), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        Entity shooter = this.getOwner();
        for (LivingEntity entity : list) {
            if (entity == shooter) continue;
            double distance = entity.distanceTo(this);
            double df = (1D - (distance / power));
            if (df <= 0) {
                continue;
            }
            df *= Explosion.getSeenPercent(position(), entity);
            float damage = (float) ((int) ((df * df + df) / 2.0D * 6.0D * power + 1.0D));
            entity.hurt(damageSource, damage);
        }
        boolean destroy = false;
        if (shooter instanceof DraconicGuardianEntity) {
            GuardianFightManager manager = ((DraconicGuardianEntity) shooter).getFightManager();
            if (manager != null && blockPosition().getY() > (manager.getArenaOrigin().getY() + (GuardianFightManager.CRYSTAL_HEIGHT_FROM_ORIGIN - 20))) {
                destroy = true;
            }
        }
//                BCoreNetwork.sendSound(level, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.HOSTILE, 10, random.nextFloat() * 0.1F + 0.9F, false);
        level.explode(shooter, blockPosition().getX(), blockPosition().getY(), blockPosition().getZ(), 8, destroy ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
        DraconicNetwork.sendImpactEffect(level, blockPosition(), 0);

        this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {

    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

    }
}
