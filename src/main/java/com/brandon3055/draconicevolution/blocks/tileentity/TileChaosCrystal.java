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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class TileChaosCrystal extends TileBCore {

    public int tick = 0;
    public final ManagedBool guardianDefeated = register(new ManagedBool("guardian_defeated", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedPos parentPos = register(new ManagedPos("parent_pos", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    /**
     * This is used to store the spawn location of the crystal so the crystal can tell if it gets moved
     */
    private final ManagedLong posLock = register(new ManagedLong("pos_lock", -1L, DataFlags.SAVE_NBT));
    private final ManagedString dimLock = register(new ManagedString("dim_lock", "", DataFlags.SAVE_NBT));
    private boolean validatePlacement = false;
    private int soundTimer;

    public TileChaosCrystal(BlockPos pos, BlockState state) {
        super(DEContent.TILE_CHAOS_CRYSTAL.get(), pos, state);
    }

    @Override
    public void tick() {
        if (validatePlacement) {
            posLock.set(worldPosition.asLong());
            dimLock.set(level.dimension().location().toString());
            for (int i = 1; i <= 2; i++) {
                level.setBlockAndUpdate(worldPosition.above(i), DEContent.CHAOS_CRYSTAL_PART.get().defaultBlockState());
                level.setBlockAndUpdate(worldPosition.below(i), DEContent.CHAOS_CRYSTAL_PART.get().defaultBlockState());
                BlockEntity tile = level.getBlockEntity(worldPosition.above(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(worldPosition);
                tile = level.getBlockEntity(worldPosition.below(i));
                if (tile instanceof TileChaosCrystal) ((TileChaosCrystal) tile).parentPos.set(worldPosition);
            }
            validatePlacement = false;
        }

        if (!getBlockState().is(DEContent.CHAOS_CRYSTAL.get())) return;
        tick++;

        if (tick > 1 && !level.isClientSide && hasBeenMoved()) {
            level.removeBlock(worldPosition, false);
        }

        if (!level.isClientSide && soundTimer-- <= 0) {
            soundTimer = 3600 + level.random.nextInt(1200);
//            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, DESounds.chaosChamberAmbient, SoundCategory.AMBIENT, 1F, world.rand.nextFloat() * 0.4F + 0.8F, false);
            BCoreNetwork.sendSound(level, worldPosition, DESounds.CHAOS_CHAMBER_AMBIENT.get(), SoundSource.AMBIENT, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }

        if (!level.isClientSide && level instanceof ServerLevel && guardianDefeated.get() && level.random.nextInt(50) == 0) {
            int x = 5 - level.random.nextInt(11);
            int z = 5 - level.random.nextInt(11);
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.setPos(worldPosition.getX() + x, level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, worldPosition).getY(), worldPosition.getZ() + z);
            bolt.noCulling = true;
            level.addFreshEntity(bolt);
        }
    }

    private boolean removing = false;

    public void detonate(Entity entity) {
        if (level.isClientSide) {
            return;
        }

        if (parentPos.notNull()) {
            BlockEntity tile = level.getBlockEntity(parentPos.get());
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
            Level world = level;
            BlockPos pos = worldPosition;
            ProcessHandler.addProcess(new DelayedTask.Task(1, () -> {
                world.setBlock(pos, DEContent.CHAOS_CRYSTAL.get().defaultBlockState(), 3);
                TileChaosCrystal tileChaosShard = (TileChaosCrystal) world.getBlockEntity(pos);
                tileChaosShard.onValidPlacement();
            }));
            return;
        }

        Block.popResource(level, worldPosition, new ItemStack(DEContent.CHAOS_SHARD.get(), DEConfig.chaosDropCount));
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
    public void writeExtraNBT(CompoundTag compound) {
        super.writeExtraNBT(compound);
        if (validatePlacement) {
            compound.putBoolean("validate_placement", true);
        }
    }

    @Override
    public void readExtraNBT(CompoundTag compound) {
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
    public boolean saveToItem() {
        return false;
    }

    public boolean attemptingBreak(Player player) {
        if (parentPos.isNull()) {
            if (player != null && player.getAbilities().instabuild) {
                guardianDefeated.set(true);
            }
            return guardianDefeated.get();
        }
        if (level.getBlockEntity(parentPos.get()) instanceof TileChaosCrystal tile) {
            return tile.attemptingBreak(player);
        }
        return false;
    }
}
