package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.datamanager.ManagedLong;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.entity.EntityChaosImplosion;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;


/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCBase implements ITickable {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register("guardianDefeated", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    /**This is used to store the spawn location of the crystal so the crystal can tell if it gets moved*/
    private final ManagedLong posLock = register("pos_lock", new ManagedLong(-1)).saveToTile().finish();
    private final ManagedInt dimLock = register("dim_lock", new ManagedInt(1)).saveToTile().finish();
    private int soundTimer;

    boolean validateOldHash = false;
    int oldhash = 0;

    public TileChaosCrystal() {
    }

    @Override
    public void update() {
        tick++;

        //Prevent existing crystals breaking due to update
        if (validateOldHash) {
            int hash = (pos.toString() + String.valueOf(world.provider.getDimension())).hashCode();
            if (hash == oldhash) {
                setLockPos();
            }
            else {
                world.setBlockToAir(pos);
            }
            validateOldHash = false;
        }

        if (tick > 1 && !world.isRemote && hasBeenMoved()) {
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

        if (DEConfig.disableChaosIslandExplosion || hasBeenMoved()) {
            world.setBlockToAir(pos);
        }
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
    public void readExtraNBT(NBTTagCompound compound) {
        super.readExtraNBT(compound);

        //Prevent existing crystals breaking due to update
        if (compound.hasKey("LocationHash")) {
            oldhash = compound.getInteger("LocationHash");
            validateOldHash = true;
        }
    }

    public void setLockPos() {
        posLock.value = pos.toLong();
        dimLock.value = world.provider.getDimension();
    }

    private boolean hasBeenMoved() {
        return posLock.value != pos.toLong() || dimLock.value != world.provider.getDimension();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().grow(1, 3, 1);
    }

    @Override
    public boolean saveToItem() {
        return false;
    }
}
