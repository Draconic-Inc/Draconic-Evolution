package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Brandon on 24/5/2015.
 */
public class TilePortalBlock extends TileEntity {

    public int masterX = 0;
    public int masterY = 0;
    public int masterZ = 0;

    @Override
    @SideOnly(Side.SERVER)
    public boolean canUpdate() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateEntity() {
        if (!worldObj.isRemote) return;
        double distanceMod = Utills.getDistanceAtoB(
                xCoord + 0.5,
                yCoord + 0.5,
                zCoord + 0.5,
                RenderManager.renderPosX,
                RenderManager.renderPosY,
                RenderManager.renderPosZ);
        if (worldObj.rand.nextInt(Math.max((int) (distanceMod * (distanceMod / 5D)), 1)) == 0) {
            if (blockMetadata == -1) blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

            double rD1 = worldObj.rand.nextDouble();
            double rD2 = worldObj.rand.nextDouble();
            double rO1 = -0.1 + worldObj.rand.nextDouble() * 0.2;
            double rO2 = -0.1 + worldObj.rand.nextDouble() * 0.2;

            if (blockMetadata == 1 && RenderManager.renderPosZ < zCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord + rD1,
                                yCoord + rD2,
                                zCoord,
                                xCoord + rD1 + rO1,
                                yCoord + rD2 + rO2,
                                zCoord + 0.75),
                        256);
            else if (blockMetadata == 1 && RenderManager.renderPosZ > zCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord + rD1,
                                yCoord + rD2,
                                zCoord + 1,
                                xCoord + rD1 + rO1,
                                yCoord + rD2 + rO2,
                                zCoord + 0.25),
                        256);
            else if (blockMetadata == 2 && RenderManager.renderPosX < xCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord,
                                yCoord + rD1,
                                zCoord + rD2,
                                xCoord + 0.75,
                                yCoord + rD1 + rO1,
                                zCoord + rD2 + rO2),
                        256);
            else if (blockMetadata == 2 && RenderManager.renderPosX > xCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord + 1,
                                yCoord + rD1,
                                zCoord + rD2,
                                xCoord + 0.25,
                                yCoord + rD1 + rO1,
                                zCoord + rD2 + rO2),
                        256);
            else if (blockMetadata == 3 && RenderManager.renderPosY > yCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord + rD1,
                                yCoord + 1,
                                zCoord + rD2,
                                xCoord + rD1 + rO1,
                                yCoord + 0.25,
                                zCoord + rD2 + rO2),
                        256);
            else if (blockMetadata == 3 && RenderManager.renderPosY < yCoord + 0.5)
                DraconicEvolution.proxy.spawnParticle(
                        new Particles.PortalParticle(
                                worldObj,
                                xCoord + rD1,
                                yCoord,
                                zCoord + rD2,
                                xCoord + rD1 + rO1,
                                yCoord + 0.75,
                                zCoord + rD2 + rO2),
                        256);
        }
    }

    public TileDislocatorReceptacle getMaster() {
        return worldObj.getTileEntity(masterX, masterY, masterZ) instanceof TileDislocatorReceptacle
                ? (TileDislocatorReceptacle) worldObj.getTileEntity(masterX, masterY, masterZ)
                : null;
    }

    public boolean isPortalStillValid() {
        TileDislocatorReceptacle master = getMaster();
        if (master == null || !master.isActive) return false;
        master.validateActivePortal();
        return master.isActive;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("MasterX", masterX);
        compound.setInteger("MasterY", masterY);
        compound.setInteger("MasterZ", masterZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        masterX = compound.getInteger("MasterX");
        masterY = compound.getInteger("MasterY");
        masterZ = compound.getInteger("MasterZ");
    }
}
