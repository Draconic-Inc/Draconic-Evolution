package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedLong;
import com.brandon3055.brandonscore.lib.datamanager.ManagedString;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;


/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCore implements ITickableTileEntity {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register(new ManagedBool("guardian_defeated", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    /**This is used to store the spawn location of the crystal so the crystal can tell if it gets moved*/
    private final ManagedLong posLock = register(new ManagedLong("pos_lock", -1L, SAVE_NBT));
    private final ManagedString dimLock = register(new ManagedString("dim_lock", "", SAVE_NBT));
    private int soundTimer;

    boolean validateOldHash = false;
    int oldhash = 0;

    public TileChaosCrystal() {
        super(DEContent.tile_chaos_crystal);
    }

    @Override
    public void tick() {
        tick++;

        //Prevent existing crystals breaking due to update
        if (validateOldHash) {
            int hash = (pos.toString() + world.dimension.getType()).hashCode();
            if (hash == oldhash) {
                setLockPos();
            }
            else {
                world.removeBlock(pos, false);
            }
            validateOldHash = false;
        }

        if (tick > 1 && !world.isRemote && hasBeenMoved()) {
            world.removeBlock(pos, false);
        }

        if (world.isRemote && soundTimer-- <= 0) {
            soundTimer = 3600 + world.rand.nextInt(1200);
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESoundHandler.chaosChamberAmbient, SoundCategory.AMBIENT, 1.5F, world.rand.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!world.isRemote && world instanceof ServerWorld && guardianDefeated.get() && world.rand.nextInt(50) == 0) {
            int x = 5 - world.rand.nextInt(11);
            int z = 5 - world.rand.nextInt(11);
            LightningBoltEntity bolt = new LightningBoltEntity(world, pos.getX() + x, world.getHeight(Heightmap.Type.WORLD_SURFACE, pos).getY(), pos.getZ() + z, false);
            bolt.ignoreFrustumCheck = true;
            ((ServerWorld) world).addLightningBolt(bolt);
        }
    }

    public void detonate() {
        if (world.isRemote) {
            return;
        }

        if (DEOldConfig.disableChaosIslandExplosion || hasBeenMoved()) {
            world.removeBlock(pos, false);
        }
        else {
//            EntityChaosImplosion vortex = new EntityChaosImplosion(world); TODO Implosion
//            vortex.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
//            world.addEntity(vortex);
        }
    }

    public void setDefeated() {
        guardianDefeated.set(true);
        super.tick();
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);

        //Prevent existing crystals breaking due to update
        if (compound.contains("LocationHash")) {
            oldhash = compound.getInt("LocationHash");
            validateOldHash = true;
        }
    }

    public void setLockPos() {
        posLock.set(pos.toLong());
        dimLock.set(world.getDimension().getType().getRegistryName().toString());
    }

    private boolean hasBeenMoved() {
        return posLock.get() != pos.toLong() || !dimLock.get().equals(world.getDimension().getType().getRegistryName().toString());
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
