package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.brandon3055.draconicevolution.common.entity.EntityChaosVortex;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosShard extends TileEntity {

    public int tick = 0;
    public boolean guardianDefeated = false;
    private int soundTimer;
    public int locationHash = 0;

    @Override
    public void updateEntity() {
        tick++;

        if (tick > 1 && !worldObj.isRemote
                && locationHash != getLocationHash(xCoord, yCoord, zCoord, worldObj.provider.dimensionId))
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);

        if (worldObj.isRemote && soundTimer-- <= 0) {
            soundTimer = 3600 + worldObj.rand.nextInt(1200);
            worldObj.playSound(
                    xCoord + 0.5D,
                    yCoord + 0.5D,
                    zCoord + 0.5D,
                    "draconicevolution:chaosChamberAmbient",
                    1.5F,
                    worldObj.rand.nextFloat() * 0.4F + 0.8F,
                    false);
        }

        if (!worldObj.isRemote && guardianDefeated && worldObj.rand.nextInt(50) == 0) {
            int x = 5 - worldObj.rand.nextInt(11);
            int z = 5 - worldObj.rand.nextInt(11);
            EntityLightningBolt bolt = new EntityLightningBolt(
                    worldObj,
                    xCoord + x,
                    worldObj.getTopSolidOrLiquidBlock(xCoord + x, zCoord + z),
                    zCoord + z);
            bolt.ignoreFrustumCheck = true;
            worldObj.addWeatherEffect(bolt);
        }
    }

    public void detonate() {
        if (!worldObj.isRemote
                && locationHash != getLocationHash(xCoord, yCoord, zCoord, worldObj.provider.dimensionId))
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        else {
            EntityChaosVortex vortex = new EntityChaosVortex(worldObj);
            vortex.setPosition(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
            worldObj.spawnEntityInWorld(vortex);
        }
    }

    public void setDefeated() {
        guardianDefeated = true;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("GuardianDefeated", guardianDefeated);
        compound.setInteger("LocationHash", locationHash);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        guardianDefeated = compound.getBoolean("GuardianDefeated");
        locationHash = compound.getInteger("LocationHash");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(1, 3, 1);
    }

    public int getLocationHash(int xCoord, int yCoord, int zCoord, int dimension) {
        return (String.valueOf(xCoord) + String.valueOf(yCoord) + String.valueOf(zCoord) + String.valueOf(dimension))
                .hashCode();
    }
}
