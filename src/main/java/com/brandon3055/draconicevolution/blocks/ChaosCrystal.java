package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEDamage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends EntityBlockBCore {

    private static VoxelShape SHAPE = Shapes.box(0, -2, 0, 1, 3, 1);

    public ChaosCrystal(Properties properties) {
        super(properties);
        setBlockEntity(DEContent.TILE_CHAOS_CRYSTAL::get, true);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof TileChaosCrystal tile) {
            if (!tile.attemptingBreak(player)) {
                return false;
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        TileChaosCrystal tile = world.getBlockEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getBlockEntity(pos) : null;
        if (tile != null) return tile.attemptingBreak(null) ? super.getDestroyProgress(state, player, world, pos) : -1F;
        return -1;
    }

    @Override
    public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn) {}

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (!world.isClientSide && tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).detonate(null);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player && ((Player) placer).getAbilities().instabuild) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (!level.isClientSide && tile instanceof TileChaosCrystal) {
                ((TileChaosCrystal) tile).onValidPlacement();
                ((TileChaosCrystal) tile).guardianDefeated.set(true);
            }
        } else {
            placer.hurt(DEDamage.chaosImplosion(level), Float.MAX_VALUE);
        }
    }

//    @Override
//    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
//        List<PlayerEntity> players = world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos, pos.offset(1, 1, 1)).inflate(15, 15, 15));
//
//        for (PlayerEntity player : players) {
//            if (player.abilities.instabuild) {
//                return;
//            }
//            player.hurt(punishment, Float.MAX_VALUE);
//        }
//    }

    //region Rendering


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        if (state.is(DEContent.CHAOS_CRYSTAL.get())) {
            return SHAPE;
        } else {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileChaosCrystal) {
                BlockPos offset = ((TileChaosCrystal) tile).parentPos.get().subtract(pos);
                return SHAPE.move(0, offset.getY(), 0);
            }
        }

        return SHAPE;
    }


    //endregion
}
