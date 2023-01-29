package com.brandon3055.draconicevolution.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.network.GenericParticlePacket;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * Created by brandon3055 on 3/10/2015.
 */
public class EntityChaosVortex extends Entity {

    public EntityChaosVortex(World world) {
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
        dataWatcher.addObject(20, ticksExisted);
    }

    @Override
    public void onUpdate() {
        if (!worldObj.isRemote) dataWatcher.updateObject(20, ticksExisted);
        else ticksExisted = dataWatcher.getWatchableObjectInt(20);

        if (ticksExisted < 30 && ticksExisted % 5 == 0 && worldObj.isRemote) DraconicEvolution.proxy
                .spawnParticle(new Particles.ChaosExpansionParticle(worldObj, posX, posY, posZ, false), 512);
        if (ticksExisted >= 100 && ticksExisted < 130 && ticksExisted % 5 == 0 && worldObj.isRemote)
            DraconicEvolution.proxy
                    .spawnParticle(new Particles.ChaosExpansionParticle(worldObj, posX, posY, posZ, true), 512);
        if (ticksExisted < 100) return;

        for (int i = 0; i < 10; i++) {
            double x = posX - 18 + rand.nextDouble() * 36;
            double y = posY - 8 + rand.nextDouble() * 16;
            double z = posZ - 18 + rand.nextDouble() * 36;
            if (worldObj.isRemote) DraconicEvolution.proxy.spawnParticle(
                    new Particles.AdvancedSeekerParticle(worldObj, x, y, z, posX, posY, posZ, 2, 1f, 1f, 1f, 100),
                    128);
        }

        if (ticksExisted > 130 && worldObj.isRemote && ticksExisted % 2 == 0) shakeScreen();

        if (ticksExisted == 600 && !worldObj.isRemote) {
            DraconicEvolution.network.sendToAllAround(
                    new GenericParticlePacket(GenericParticlePacket.CHAOS_IMPLOSION, posX, posY, posZ),
                    new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 512));
            ProcessHandler.addProcess(new ChaosImplosion(worldObj, (int) posX, (int) posY, (int) posZ));
        }

        if (ticksExisted > 620) setDead();
    }

    private void shakeScreen() {
        double intensity = (ticksExisted - 130) / 100D;
        if (intensity > 1D) intensity = 1D;

        List<EntityPlayer> players = worldObj
                .getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(200, 200, 200));

        for (EntityPlayer player : players) {
            double x = (rand.nextDouble() - 0.5) * 2 * intensity;
            double z = (rand.nextDouble() - 0.5) * 2 * intensity;
            player.moveEntity(x / 5D, 0, z / 5D);
            player.rotationYaw -= x * 2;
            player.rotationPitch -= z * 2;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}
}
