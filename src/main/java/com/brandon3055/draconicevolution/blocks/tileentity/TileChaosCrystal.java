package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;


/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCore implements ITickableTileEntity {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register(new ManagedBool("guardian_defeated", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedPos parentPos = register(new ManagedPos("parent_pos", new BlockPos(0, -1, 0), SAVE_NBT_SYNC_TILE));
    /**
     * This is used to store the spawn location of the crystal so the crystal can tell if it gets moved
     */
    private final ManagedLong posLock = register(new ManagedLong("pos_lock", -1L, SAVE_NBT));
    private final ManagedString dimLock = register(new ManagedString("dim_lock", "", SAVE_NBT));
    private boolean validatePlacement = false;
    private int soundTimer;

    public TileChaosCrystal() {
        super(DEContent.tile_chaos_crystal);
    }

    @Override
    public void tick() {
        if (validatePlacement) {
            posLock.set(pos.toLong());
            dimLock.set(world.getDimensionKey().getLocation().toString());
            for (int i = 1; i <= 2; i++) {
                world.setBlockState(pos.up(i), DEContent.chaos_crystal_part.getDefaultState());
                world.setBlockState(pos.down(i), DEContent.chaos_crystal_part.getDefaultState());
                TileEntity tile = world.getTileEntity(pos.up(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(pos);
                tile = world.getTileEntity(pos.down(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(pos);
            }
            validatePlacement = false;
        }

        if (getBlockState().getBlock() != DEContent.chaos_crystal) return;
        tick++;

        if (tick > 1 && !world.isRemote && hasBeenMoved()) {
            world.removeBlock(pos, false);
        }

        if (world.isRemote && soundTimer-- <= 0) {
            soundTimer = 3600 + world.rand.nextInt(1200);
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESounds.chaosChamberAmbient, SoundCategory.AMBIENT, 1.5F, world.rand.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!world.isRemote && world instanceof ServerWorld && guardianDefeated.get() && world.rand.nextInt(50) == 0) {
            int x = 5 - world.rand.nextInt(11);
            int z = 5 - world.rand.nextInt(11);
            LightningBoltEntity bolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
            bolt.setPosition(pos.getX() + x, world.getHeight(Heightmap.Type.WORLD_SURFACE, pos).getY(), pos.getZ() + z);
            bolt.ignoreFrustumCheck = true;
            world.addEntity(bolt);
        }
    }

    private boolean removing = false;
    public void detonate(Entity entity) {
        if (world.isRemote) {
            return;
        }

        if (parentPos.get().getY() != -1) {
            TileEntity tile = world.getTileEntity(parentPos.get());
            if (tile instanceof TileChaosCrystal && !((TileChaosCrystal) tile).removing) {
                ((TileChaosCrystal) tile).detonate(entity);
                world.destroyBlock(tile.getPos(), true, entity);
            }
            return;
        }

        removing = true;
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.up(2), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.down(2), Blocks.AIR.getDefaultState());

        if (DEOldConfig.disableChaosIslandExplosion || hasBeenMoved()) {
            world.removeBlock(pos, false);
        } else {
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
    public void writeExtraNBT(CompoundNBT compound) {
        super.writeExtraNBT(compound);
        if (validatePlacement) {
            compound.putBoolean("validate_placement", true);
        }
    }

    @Override
    public void readExtraNBT(CompoundNBT compound) {
        super.readExtraNBT(compound);
        validatePlacement = compound.contains("validate_placement") && compound.getBoolean("validate_placement");
    }

    public void onValidPlacement() {
        validatePlacement = true;
    }

    private boolean hasBeenMoved() {
        return posLock.get() != pos.toLong() || !dimLock.get().equals(world.getDimensionKey().getLocation().toString());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(3, 3, 3);
    }

    @Override
    public boolean saveToItem() {
        return false;
    }

    public boolean canBreak() {
        if (parentPos.get().getY() == -1) {
            return guardianDefeated.get();
        }
        TileEntity tile = world.getTileEntity(parentPos.get());
        if (tile instanceof TileChaosCrystal) {
            return ((TileChaosCrystal) tile).guardianDefeated.get();
        }
        return false;
    }
}
