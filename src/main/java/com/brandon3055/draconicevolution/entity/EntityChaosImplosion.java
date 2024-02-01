//package com.brandon3055.draconicevolution.entity;
//
//import com.brandon3055.brandonscore.handlers.ProcessHandler;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.EntityDataSerializers;
//import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.MoverType;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//
//import java.util.List;
//
///**
// * Created by brandon3055 on 3/10/2015.
// */
//@Deprecated
//public class EntityChaosImplosion extends Entity {
//    protected static final EntityDataAccessor<Integer> TICKS = SynchedEntityData.<Integer>defineId(EntityChaosImplosion.class, EntityDataSerializers.INT);
//
//    public EntityChaosImplosion(EntityType<?> entityTypeIn, Level worldIn) {
//        super(entityTypeIn, worldIn);
//    }
//
//    @Override
//    protected void defineSynchedData() {
//
//    }
//
//    @Override
//    public Packet<?> getAddEntityPacket() {
//        return null;
//    }
//
//    //    public EntityChaosImplosion(World world) {
////        super(world);
////        this.noClip = true;
////        this.setSize(0F, 0F);
////    }
//
//    @Override
//    public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
//        return true;
//    }
//
////    @Override
////    protected void entityInit() {
////        dataManager.register(TICKS, ticksExisted);
////    }
//
//    @Override
//    public void tick() {
//        if (!level.isClientSide) {
//            entityData.set(TICKS, tickCount);
//        }
//
////        Vec3D pos = new Vec3D(getPosX(), getPosY(), getPosZ());
//
//        if (tickCount < 30 && tickCount % 5 == 0 && level.isClientSide) {
//            //TODO Particles
////            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 1);
////            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, false), 512);
//        }
//        if (tickCount >= 100 && tickCount < 130 && tickCount % 5 == 0 && level.isClientSide) {
////            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 2);
////            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, true), 512);
//        }
//        if (tickCount < 100) {
//            return;
//        }
//
//        if (tickCount < 600) {
//            for (int i = 0; i < 10; i++) {
////                double x = posX - 18 + rand.nextDouble() * 36;
////                double y = posY - 8 + rand.nextDouble() * 16;
////                double z = posZ - 18 + rand.nextDouble() * 36;
//                if (level.isClientSide) {
////                    BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, new Vec3D(x, y, z), pos, 512D, 0);
//                }
//            }
//
//            if (tickCount > 130 && level.isClientSide && tickCount % 2 == 0) {
//                shakeScreen();
//            }
//        }
//
//        if (tickCount == 700 && !level.isClientSide) {
////            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 5);
//            ProcessHandler.addProcess(new ProcessChaosImplosion(level, (int) getX(), (int) getY(), (int) getZ()));
//        }
//
//        if (tickCount > 720) {
//            discard();
//        }
//    }
//
//
//    private void shakeScreen() {
//        float intensity = (tickCount - 130) / 100F;
//        if (intensity > 1F) intensity = 1F;
//
//        @SuppressWarnings("unchecked") List<Player> players = level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(200, 200, 200));
//
//        for (Player player : players) {
//            float x = (random.nextFloat() - 0.5F) * 2 * intensity;
//            float z = (random.nextFloat() - 0.5F) * 2 * intensity;
//            player.move(MoverType.SELF, new Vec3(x / 5D, 0, z / 5D));
//            player.setYRot(player.getYRot() - (x * 2));
//            player.setXRot(player.getXRot() - (z * 2));
//        }
//    }
//
//    @Override
//    protected void readAdditionalSaveData(CompoundTag compound) {
//
//    }
//
//    @Override
//    protected void addAdditionalSaveData(CompoundTag compound) {
//
//    }
//}
