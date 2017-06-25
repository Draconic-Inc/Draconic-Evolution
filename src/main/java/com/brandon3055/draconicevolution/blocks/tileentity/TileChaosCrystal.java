package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.draconicevolution.entity.EntityChaosImplosion;
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
public class TileChaosCrystal extends TileBCBase implements ITickable {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register("guardianDefeated", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    private int soundTimer;
    public int locationHash = 0;

    public TileChaosCrystal() {
    }

    @Override
    public void update() {
        tick++;

        if (tick > 1 && !world.isRemote && locationHash != getLocationHash(pos, world.provider.getDimension())) {
            world.setBlockToAir(pos);
        }

        if (world.isRemote && soundTimer-- <= 0) {
            soundTimer = 3600 + world.rand.nextInt(1200);
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESoundHandler.chaosChamberAmbient, SoundCategory.AMBIENT, 1.5F, world.rand.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!world.isRemote && guardianDefeated.value && world.rand.nextInt(50) == 0) {
            int x = 5 - world.rand.nextInt(11);
            int z = 5 - world.rand.nextInt(11);
            EntityLightningBolt bolt = new EntityLightningBolt(world, pos.getX() + x, world.getTopSolidOrLiquidBlock(pos.add(x, 0, z)).getY(), pos.getZ() + z, false);
            bolt.ignoreFrustumCheck = true;
            world.addWeatherEffect(bolt);
        }
    }

    public void detonate() {
        if (world.isRemote) {
            return;
        }

        if (locationHash != getLocationHash(pos, world.provider.getDimension())) world.setBlockToAir(pos);
        else {
            EntityChaosImplosion vortex = new EntityChaosImplosion(world);
            vortex.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            world.spawnEntity(vortex);
        }
    }

    public void setDefeated() {
        guardianDefeated.value = true;
        super.update();
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
