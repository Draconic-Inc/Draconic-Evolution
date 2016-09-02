package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.draconicevolution.entity.EntityChaosVortex;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCBase implements ITickable{

    public int tick = 0;
    public final SyncableBool guardianDefeated = new SyncableBool(false, true, false, true);
    private int soundTimer;
    public int locationHash = 0;

    public TileChaosCrystal() {
        registerSyncableObject(guardianDefeated, true);
    }

    @Override
    public void update() {
        tick++;

        if (tick > 1 && !worldObj.isRemote && locationHash != getLocationHash(pos, worldObj.provider.getDimension())) {
            worldObj.setBlockToAir(pos);
        }

        if (worldObj.isRemote && soundTimer-- <= 0) {
            soundTimer = 3600 + worldObj.rand.nextInt(1200);
            worldObj.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESoundHandler.chaosChamberAmbient, SoundCategory.HOSTILE, 1.5F, worldObj.rand.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!worldObj.isRemote && guardianDefeated.value && worldObj.rand.nextInt(50) == 0) {
            int x = 5 - worldObj.rand.nextInt(11);
            int z = 5 - worldObj.rand.nextInt(11);
            EntityLightningBolt bolt = new EntityLightningBolt(worldObj, pos.getX() + x, worldObj.getTopSolidOrLiquidBlock(pos.add(x, 0, z)).getY(), pos.getZ() + z, false);
            bolt.ignoreFrustumCheck = true;
            worldObj.addWeatherEffect(bolt);
        }
    }

    public void detonate() {
        if (worldObj.isRemote){
            return;
        }

        if (locationHash != getLocationHash(pos, worldObj.provider.getDimension()))
            worldObj.setBlockToAir(pos);
        else {
            EntityChaosVortex vortex = new EntityChaosVortex(worldObj);
            vortex.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            worldObj.spawnEntityInWorld(vortex);
        }
    }

    public void setDefeated() {
        guardianDefeated.value = true;
    }

    @Override
    public void writeExtraNBT(NBTTagCompound compound) {
        super.writeExtraNBT(compound);
        compound.setInteger("LocationHash", locationHash);
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        super.readExtraNBT(compound);
        locationHash = compound.getInteger("LocationHash");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(1, 3, 1);
    }

    public int getLocationHash(BlockPos location, int dimension) {
        return (location.toString() + String.valueOf(dimension)).hashCode();
    }
}
