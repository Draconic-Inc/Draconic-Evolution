package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

//TODO move to guardian package
public class GuardianProjectileEntity extends DamagingProjectileEntity implements IEntityAdditionalSpawnData {
    private Vector3d target;
    private double splashRange = 15;
    private double power = 10;
    private DamageSource damageSource = new DamageSource("damage.draconicevolution.guardian_projectile").setDamageIsAbsolute().setDamageBypassesArmor().setMagicDamage().setExplosion();
    private double closestApproach;

    public GuardianProjectileEntity(EntityType<?> type, World world) {
        super(DEContent.guardianProjectile, world);
    }

    public GuardianProjectileEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, Vector3d target, double splashRange, double power) {
        super(DEContent.guardianProjectile, shooter, accelX, accelY, accelZ, worldIn);
        this.target = target;
        this.splashRange = splashRange;
        this.power = power;
        this.damageSource = new IndirectEntityDamageSource("draconicevolution.guardian_projectile", this, shooter).setDamageIsAbsolute().setDamageBypassesArmor().setMagicDamage().setExplosion();
        if (target != null) {
            closestApproach = getDistanceSq(target);
        }
        double accelDotProduct = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        if (accelDotProduct != 0.0D) {
            this.accelerationX = accelX / accelDotProduct * 0.3D;
            this.accelerationY = accelY / accelDotProduct * 0.3D;
            this.accelerationZ = accelZ / accelDotProduct * 0.3D;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        Entity shooter = this.func_234616_v_();
        if (result.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult) result).getEntity().isEntityEqual(shooter)) {
            if (!this.world.isRemote) {
                detonate();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (target != null) {
            double distSq = getDistanceSq(target);
            if (distSq <= 1) {
                detonate();
            } else if (distSq < closestApproach) {
                closestApproach = distSq;
            } else if (ticksExisted > 5){
                detonate();
            }
        }
    }

    private void detonate() {
        List<LivingEntity> list = world.getEntitiesWithinAABB(LivingEntity.class, getBoundingBox().grow(splashRange), EntityPredicates.CAN_AI_TARGET);
        Entity shooter = this.func_234616_v_();
        for (LivingEntity entity : list) {
            if (entity == shooter) continue;
            double distance = entity.getDistance(this);
            double df = (1D - (distance / power));
            if (df <= 0) {
                continue;
            }
            df *= Explosion.getBlockDensity(getPositionVec(), entity);
            float damage = (float) ((int) ((df * df + df) / 2.0D * 6.0D * power + 1.0D));
            entity.attackEntityFrom(damageSource, damage);
        }
        DraconicNetwork.sendImpactEffect(world, getPosition(), 0);
        BCoreNetwork.sendSound(world, getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 10, rand.nextFloat() * 0.1F + 0.9F, false);
        this.remove();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected IParticleData getParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {

    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {

    }
}
