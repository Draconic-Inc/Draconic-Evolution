package com.brandon3055.draconicevolution.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.Particles;

/**
 * Created by brandon3055 on 30/9/2015.
 */
public class EntityChaosBolt extends Entity {

    public double shardX;
    public double shardY;
    public double shardZ;
    public boolean burst;
    public int ticks = 0;
    private static DamageSource chaosBurst = new DamageSource("chaosBurst").setDamageBypassesArmor()
            .setDamageIsAbsolute();

    public EntityChaosBolt(World world) {
        this(world, 0, 0, 0, 0, 0, 0);
    }

    public EntityChaosBolt(World world, double x, double y, double z, double shardX, double shardY, double shardZ) {
        super(world);
        this.shardX = shardX;
        this.shardY = shardY;
        this.shardZ = shardZ;
        this.setPosition(x, y, z);
        this.setSize(0.25F, 0.25F);
    }

    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return false;
    }

    @Override
    protected void entityInit() {
        burst = rand.nextInt(30) == 0;
        dataWatcher.addObject(20, (float) shardX);
        dataWatcher.addObject(21, (float) shardZ);
        dataWatcher.addObject(22, burst ? (byte) 1 : (byte) 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            shardX = dataWatcher.getWatchableObjectFloat(20);
            shardZ = dataWatcher.getWatchableObjectFloat(21);
            burst = dataWatcher.getWatchableObjectByte(22) == 1;
        } else {
            dataWatcher.updateObject(20, (float) shardX);
            dataWatcher.updateObject(21, (float) shardZ);
            dataWatcher.updateObject(22, burst ? (byte) 1 : (byte) 0);
        }

        if (ticks == 10 && !burst) {
            if (worldObj.isRemote) {
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.ChaosBoltParticle(worldObj, posX, posY, posZ, shardX, shardY, shardZ, 10),
                        32);
            }
            setDead();
        } else if (ticks > 10) {
            if (ticks < 25) {
                for (int i = 0; i < 20; i++) {
                    if (worldObj.isRemote) DraconicEvolution.proxy.spawnParticle(
                            new Particles.ChaosBoltParticle(worldObj, posX, posY, posZ, shardX, shardY, shardZ, 0),
                            32);
                }
            }
            if (ticks > 40 && !worldObj.isRemote) {
                // if (ticks % 5 == 0)
                // {
                // double dist = Utills.getDistanceAtoB(posX, posY, posZ, shardX, shardY, shardZ);
                // Vec3 dir = Vec3.createVectorHelper((shardX - posX) / dist, (shardY - posY) / dist, (shardZ -
                // posZ) / dist);
                // MovingObjectPosition movingobjectposition =
                // this.worldObj.//func_147447_a(Vec3.createVectorHelper(posX, posY, posZ), dir, false, true, false);
                // Entity entity = movingobjectposition.entityHit; //WorldUtil.rayTraceEntities(worldObj,
                // Vec3.createVectorHelper(posX, posY, posZ), dir, 20);
                //
                // LogHelper.info(movingobjectposition);
                // if (entity instanceof EntityLivingBase) {
                // entity.hurtResistantTime = 0;
                // entity.attackEntityFrom(chaosBurst, 50F);
                // }
                // }
            }
        }

        if (ticks > 60) {
            setDead();
        }

        ticks++;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        // if (compound.hasKey("ShardPos")){
        // int[] pos = compound.getIntArray("ShardPos");
        // shardX = pos[0];
        // shardY = pos[1];
        // shardZ = pos[2];
        // LogHelper.info("read");
        // }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        // compound.setIntArray("ShardPos", new int[] {(int)(shardX-0.5D), (int)(shardY-0.5D), (int)(shardZ-0.5D)});
        // LogHelper.info("write");
    }
}
