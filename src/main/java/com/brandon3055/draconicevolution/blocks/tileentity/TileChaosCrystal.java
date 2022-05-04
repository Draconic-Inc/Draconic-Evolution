package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCore implements ITickableTileEntity {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register(new ManagedBool("guardian_defeated", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedPos parentPos = register(new ManagedPos("parent_pos", new BlockPos(0, -1, 0), DataFlags.SAVE_NBT_SYNC_TILE));
    /**
     * This is used to store the spawn location of the crystal so the crystal can tell if it gets moved
     */
    private final ManagedLong posLock = register(new ManagedLong("pos_lock", -1L, DataFlags.SAVE_NBT));
    private final ManagedString dimLock = register(new ManagedString("dim_lock", "", DataFlags.SAVE_NBT));
    private boolean validatePlacement = false;
    private int soundTimer;

    public TileChaosCrystal() {
        super(DEContent.tile_chaos_crystal);
    }

    @Override
    public void tick() {
        if (validatePlacement) {
            posLock.set(worldPosition.asLong());
            dimLock.set(level.dimension().location().toString());
            for (int i = 1; i <= 2; i++) {
                level.setBlockAndUpdate(worldPosition.above(i), DEContent.chaos_crystal_part.defaultBlockState());
                level.setBlockAndUpdate(worldPosition.below(i), DEContent.chaos_crystal_part.defaultBlockState());
                TileEntity tile = level.getBlockEntity(worldPosition.above(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(worldPosition);
                tile = level.getBlockEntity(worldPosition.below(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(worldPosition);
            }
            validatePlacement = false;
        }

        if (getBlockState().getBlock() != DEContent.chaos_crystal) return;
        tick++;

        if (tick > 1 && !level.isClientSide && hasBeenMoved()) {
            level.removeBlock(worldPosition, false);
        }

        if (!level.isClientSide && soundTimer-- <= 0) {
            soundTimer = 3600 + level.random.nextInt(1200);
//            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESounds.chaosChamberAmbient, SoundCategory.AMBIENT, 1F, world.rand.nextFloat() * 0.4F + 0.8F, false);
            BCoreNetwork.sendSound(level, worldPosition, DESounds.chaosChamberAmbient, SoundCategory.AMBIENT, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!level.isClientSide && level instanceof ServerWorld && guardianDefeated.get() && level.random.nextInt(50) == 0) {
            int x = 5 - level.random.nextInt(11);
            int z = 5 - level.random.nextInt(11);
            LightningBoltEntity bolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, level);
            bolt.setPos(worldPosition.getX() + x, level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, worldPosition).getY(), worldPosition.getZ() + z);
            bolt.noCulling = true;
            level.addFreshEntity(bolt);
        }
    }

    private boolean removing = false;
    public void detonate(Entity entity) {
        if (level.isClientSide) {
            return;
        }

        if (parentPos.get().getY() != -1) {
            TileEntity tile = level.getBlockEntity(parentPos.get());
            if (tile instanceof TileChaosCrystal && !((TileChaosCrystal) tile).removing) {
                ((TileChaosCrystal) tile).detonate(entity);
                level.destroyBlock(tile.getBlockPos(), true, entity);
            }
            return;
        }

        if (removing) {
            return;
        }
        removing = true;
        level.setBlockAndUpdate(worldPosition.above(), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(worldPosition.above(2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(worldPosition.below(), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(worldPosition.below(2), Blocks.AIR.defaultBlockState());

        if (!guardianDefeated.get()) {
            World world = level;
            BlockPos pos = worldPosition;
            ProcessHandler.addProcess(new DelayedTask.Task(1, () -> {
                world.setBlock(pos, DEContent.chaos_crystal.defaultBlockState(), 3);
                TileChaosCrystal tileChaosShard = (TileChaosCrystal) world.getBlockEntity(pos);
                tileChaosShard.onValidPlacement();
            }));
            return;
        }

        Block.popResource(level, worldPosition, new ItemStack(DEContent.chaos_shard, DEConfig.chaosDropCount));
        level.removeBlock(worldPosition, false);

        if (DEOldConfig.disableChaosIslandExplosion || hasBeenMoved()) {
//            level.removeBlock(worldPosition, false);
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
        return posLock.get() != worldPosition.asLong() || !dimLock.get().equals(level.dimension().location().toString());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(3, 3, 3);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return 512;
    }

    @Override
    public boolean saveToItem() {
        return false;
    }

    public boolean canBreak() {
        if (parentPos.get().getY() == -1) {
            return guardianDefeated.get();
        }
        TileEntity tile = level.getBlockEntity(parentPos.get());
        if (tile instanceof TileChaosCrystal) {
            return ((TileChaosCrystal) tile).guardianDefeated.get();
        }
        return false;
    }
}
