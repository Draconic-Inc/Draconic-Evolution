package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
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
public class EntityChaosImplosion extends Entity {
    protected static final DataParameter<Integer> TICKS = EntityDataManager.<Integer>createKey(EntityChaosImplosion.class, DataSerializers.VARINT);

    public EntityChaosImplosion(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return null;
    }

    //    public EntityChaosImplosion(World world) {
//        super(world);
//        this.noClip = true;
//        this.setSize(0F, 0F);
//    }

    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return true;
    }

//    @Override
//    protected void entityInit() {
//        dataManager.register(TICKS, ticksExisted);
//    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            dataManager.set(TICKS, ticksExisted);
        }

//        Vec3D pos = new Vec3D(getPosX(), getPosY(), getPosZ());

        if (ticksExisted < 30 && ticksExisted % 5 == 0 && world.isRemote) {
            //TODO Particles
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 1);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, false), 512);
        }
        if (ticksExisted >= 100 && ticksExisted < 130 && ticksExisted % 5 == 0 && world.isRemote) {
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 2);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, true), 512);
        }
        if (ticksExisted < 100) {
            return;
        }

        if (ticksExisted < 600) {
            for (int i = 0; i < 10; i++) {
//                double x = posX - 18 + rand.nextDouble() * 36;
//                double y = posY - 8 + rand.nextDouble() * 16;
//                double z = posZ - 18 + rand.nextDouble() * 36;
                if (world.isRemote) {
//                    BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, new Vec3D(x, y, z), pos, 512D, 0);
                }
            }

            if (ticksExisted > 130 && world.isRemote && ticksExisted % 2 == 0) {
                shakeScreen();
            }
        }

        if (ticksExisted == 700 && !world.isRemote) {
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 5);
            ProcessHandler.addProcess(new ProcessChaosImplosion(world, (int) getPosX(), (int) getPosY(), (int) getPosZ()));
        }

        if (ticksExisted > 720) {
            remove();
        }
    }


    private void shakeScreen() {
        double intensity = (ticksExisted - 130) / 100D;
        if (intensity > 1D) intensity = 1D;

        @SuppressWarnings("unchecked") List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox().grow(200, 200, 200));

        for (PlayerEntity player : players) {
            double x = (rand.nextDouble() - 0.5) * 2 * intensity;
            double z = (rand.nextDouble() - 0.5) * 2 * intensity;
            player.move(MoverType.SELF, new Vector3d(x / 5D, 0, z / 5D));
            player.rotationYaw -= x * 2;
            player.rotationPitch -= z * 2;
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }
}
