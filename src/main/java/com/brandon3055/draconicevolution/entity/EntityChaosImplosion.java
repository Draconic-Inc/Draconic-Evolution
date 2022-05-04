package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.handlers.ProcessHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 3/10/2015.
 */
@Deprecated
public class EntityChaosImplosion extends Entity {
    protected static final DataParameter<Integer> TICKS = EntityDataManager.<Integer>defineId(EntityChaosImplosion.class, DataSerializers.INT);

    public EntityChaosImplosion(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return null;
    }

    //    public EntityChaosImplosion(World world) {
//        super(world);
//        this.noClip = true;
//        this.setSize(0F, 0F);
//    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
        return true;
    }

//    @Override
//    protected void entityInit() {
//        dataManager.register(TICKS, ticksExisted);
//    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            entityData.set(TICKS, tickCount);
        }

//        Vec3D pos = new Vec3D(getPosX(), getPosY(), getPosZ());

        if (tickCount < 30 && tickCount % 5 == 0 && level.isClientSide) {
            //TODO Particles
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 1);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, false), 512);
        }
        if (tickCount >= 100 && tickCount < 130 && tickCount % 5 == 0 && level.isClientSide) {
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 2);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, true), 512);
        }
        if (tickCount < 100) {
            return;
        }

        if (tickCount < 600) {
            for (int i = 0; i < 10; i++) {
//                double x = posX - 18 + rand.nextDouble() * 36;
//                double y = posY - 8 + rand.nextDouble() * 16;
//                double z = posZ - 18 + rand.nextDouble() * 36;
                if (level.isClientSide) {
//                    BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, new Vec3D(x, y, z), pos, 512D, 0);
                }
            }

            if (tickCount > 130 && level.isClientSide && tickCount % 2 == 0) {
                shakeScreen();
            }
        }

        if (tickCount == 700 && !level.isClientSide) {
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 5);
            ProcessHandler.addProcess(new ProcessChaosImplosion(level, (int) getX(), (int) getY(), (int) getZ()));
        }

        if (tickCount > 720) {
            remove();
        }
    }


    private void shakeScreen() {
        double intensity = (tickCount - 130) / 100D;
        if (intensity > 1D) intensity = 1D;

        @SuppressWarnings("unchecked") List<PlayerEntity> players = level.getEntitiesOfClass(PlayerEntity.class, getBoundingBox().inflate(200, 200, 200));

        for (PlayerEntity player : players) {
            double x = (random.nextDouble() - 0.5) * 2 * intensity;
            double z = (random.nextDouble() - 0.5) * 2 * intensity;
            player.move(MoverType.SELF, new Vector3d(x / 5D, 0, z / 5D));
            player.yRot -= x * 2;
            player.xRot -= z * 2;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {

    }
}
