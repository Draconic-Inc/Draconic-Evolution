package com.brandon3055.draconicevolution.entity;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Created by Brandon on 21/11/2014.
 * This is still a thing because of covers1624. I was going to go with something a bit less shiny but he talked me out of it.
 */
public class EntityDragonHeart extends Entity {

    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.<Integer>defineId(EntityDragonHeart.class, EntityDataSerializers.INT);
    public float rotation = 0f;
    public float rotationInc = 0;
    public ItemStack renderStack = new ItemStack(DEContent.dragon_heart);
    private boolean burstFired = false;

    public EntityDragonHeart(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    //    public EntityDragonHeart(World world) {
//        super(world);
//        this.setSize(1F, 1F);
//    }
//
//    public EntityDragonHeart(World par1World, double x, double y, double z) {
//        super(par1World);
//        this.setPosition(x, y, z);
//        this.motionX = 0;
//        this.motionY = 0;
//        this.motionZ = 0;
//        this.setSize(1F, 1F);
//    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    //    @Override
//    protected void entityInit() {
//        dataManager.register(AGE, 0);
//    }

    @Override
    public boolean hurt(DamageSource damageSource, float dmg) {
        return false;
    }

//    @Override
//    public void onUpdate() {
//        rotationInc = (getAge() / 1200F) * 1F;
//
//        motionX = 0;
//        motionZ = 0;
//        motionY = 0;
//
//        if (getAge() < 800) {
//            setAge(800);
//        }
//
//        setAge(getAge() + 1);
//        rotation += rotationInc;
//        super.onUpdate();
//
//        if (getAge() == 1280 && !world.isRemote) {
//            drop();
//        }
//
//        if (getAge() > 1290) {
//            setDead();
//        }
//
//        if (world.isRemote && getAge() < 1200) {
//            for (int i = 0; i < 10; i++) {
//                double rotation = rand.nextDouble() * 1024D;
//                double offsetX = Math.sin(rotation) * (8F + (rand.nextFloat() * 4F));
//                double offsetZ = Math.cos(rotation) * (8F + (rand.nextFloat() * 4F));
//                BCEffectHandler.spawnFX(DEParticles.DRAGON_HEART, world, posX + offsetX, posY + 0.6 + ((rand.nextDouble() - 0.5) * 4), posZ + offsetZ, posX, posY + 0.6 + ((rand.nextDouble() - 0.5) * 0.2), posZ, 128D);
//            }
//        }
//
//        if (world.isRemote && getAge() > 1280 && !burstFired) {
//            burstFired = true;
//            for (int i = 0; i < 1000; i++) {
//                double y = rand.nextBoolean() ? 10 : -10;//posY + ((rand.nextDouble() - 0.5) * 10);
//                double rotation = rand.nextDouble() * 1024D;
//                double offsetX = Math.sin(rotation) * (20F);
//                double offsetZ = Math.cos(rotation) * (20F);
//                Vec3D dir = Vec3D.getDirectionVec(new Vec3D(this).add(0, 0.6, 0), new Vec3D(posX + offsetX, posY + 0.6 + y, posZ + offsetZ));
//                double dist = 1D + rand.nextDouble() * 15D;
//
//                BCEffectHandler.spawnFX(DEParticles.DRAGON_HEART, world, posX, posY + 0.6, posZ, posX + (dir.x * dist), posY + 0.6 + (dir.y * dist), posZ + (dir.z * dist), 128D, 0, 255, 255);
//            }
//        }
//    }
//
//    private void drop() {
//        PlayerEntity player = world.getClosestPlayerToEntity(this, 512);
//
//        if (player != null) {
//            BCEffectHandler.spawnFX(DEParticles.DRAGON_HEART, world, new Vec3D(this), new Vec3D(player), 128D, 0, 0, 0, 1);
//            FeatureUtils.dropItemNoDellay(new ItemStack(DEFeatures.dragonHeart), world, new Vec3D(player).toVector3());
//        }
//        else {
//            FeatureUtils.dropItemNoDellay(new ItemStack(DEFeatures.dragonHeart), world, new Vec3D(this).toVector3());
//        }
//
//        setDead();
//    }

    @Override
    public void playerTouch(Player player) {
        if (getAge() < 1200) {
            //setAge(1200);
        }
    }

    public int getAge() {
        return entityData.get(AGE);
    }

    public void setAge(int age) {
        entityData.set(AGE, age);
    }
}
