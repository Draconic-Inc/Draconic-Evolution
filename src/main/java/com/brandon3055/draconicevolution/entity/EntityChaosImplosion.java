package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 3/10/2015.
 */
public class EntityChaosImplosion extends Entity {
    protected static final DataParameter<Integer> TICKS = EntityDataManager.<Integer>createKey(EntityChaosImplosion.class, DataSerializers.VARINT);


    public EntityChaosImplosion(World world) {
        super(world);
        this.noClip = true;
        this.setSize(0F, 0F);
    }

    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(TICKS, ticksExisted);
    }

    @Override
    public void onUpdate() {
        if (!worldObj.isRemote){
            dataManager.set(TICKS, ticksExisted);
        }

        Vec3D pos = new Vec3D(posX, posY, posZ);

        if (ticksExisted < 30 && ticksExisted % 5 == 0 && worldObj.isRemote){
            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, worldObj, pos, pos, 1024D, 1);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(worldObj, posX, posY, posZ, false), 512);
        }
        if (ticksExisted >= 100 && ticksExisted < 130 && ticksExisted % 5 == 0 && worldObj.isRemote){
            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, worldObj, pos, pos, 1024D, 2);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(worldObj, posX, posY, posZ, true), 512);
        }
        if (ticksExisted < 100) {
            return;
        }

        if (ticksExisted < 600) {
            for (int i = 0; i < 10; i++) {
                double x = posX - 18 + rand.nextDouble() * 36;
                double y = posY - 8 + rand.nextDouble() * 16;
                double z = posZ - 18 + rand.nextDouble() * 36;
                if (worldObj.isRemote) {
                    BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, worldObj, new Vec3D(x, y, z), pos, 512D, 0);
//                DraconicEvolution.proxy.spawnParticle(new Particles.AdvancedSeekerParticle(worldObj, x, y, z, posX, posY, posZ, 2, 1f, 1f, 1f, 100), 128);
                }
            }

            if (ticksExisted > 130 && worldObj.isRemote && ticksExisted % 2 == 0) {
                shakeScreen();
            }
        }

        if (ticksExisted == 700 && !worldObj.isRemote) {
            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, worldObj, pos, pos, 1024D, 5);
            //DraconicEvolution.network.sendToAllAround(new GenericParticlePacket(GenericParticlePacket.CHAOS_IMPLOSION, posX, posY, posZ), new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 512));
            ProcessHandler.addProcess(new ProcessChaosImplosion(worldObj, (int) posX, (int) posY, (int) posZ));
        }

        if (ticksExisted > 720) {
            setDead();
        }
    }


    private void shakeScreen() {
        double intensity = (ticksExisted - 130) / 100D;
        if (intensity > 1D) intensity = 1D;

        @SuppressWarnings("unchecked") List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().expand(200, 200, 200));

        for (EntityPlayer player : players) {
            double x = (rand.nextDouble() - 0.5) * 2 * intensity;
            double z = (rand.nextDouble() - 0.5) * 2 * intensity;
            player.moveEntity(x / 5D, 0, z / 5D);
            player.rotationYaw -= x * 2;
            player.rotationPitch -= z * 2;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

    }
}
