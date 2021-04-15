package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends BlockBCore/*, IRenderOverride*/ {

    private static VoxelShape SHAPE = VoxelShapes.box(0, -2, 0, 1, 3, 1);

    public ChaosCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        TileChaosCrystal tile = world.getBlockEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getBlockEntity(pos) : null;
        if (tile == null || !tile.canBreak()) {
            return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        TileChaosCrystal tile = world.getBlockEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getBlockEntity(pos) : null;
        if (tile != null) return tile.canBreak() ? super.getDestroyProgress(state, player, world, pos) : -1F;
        return super.getDestroyProgress(state, player, world, pos);
    }


    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileChaosCrystal();
    }

    @Override
    public void wasExploded(World worldIn, BlockPos pos, Explosion explosionIn) {}

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getBlockEntity(pos);
        if (!world.isClientSide && tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).detonate(null);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof PlayerEntity && ((PlayerEntity) placer).abilities.instabuild) {
            TileEntity tile = world.getBlockEntity(pos);
            if (!world.isClientSide && tile instanceof TileChaosCrystal) {
                ((TileChaosCrystal) tile).onValidPlacement();
                ((TileChaosCrystal) tile).guardianDefeated.set(true);
            }
        } else {
            placer.hurt(punishment, Float.MAX_VALUE);
        }
    }

    private static DamageSource punishment = new DamageSource("chrystalMoved").bypassInvul().bypassArmor().bypassMagic();

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        List<PlayerEntity> players = world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos, pos.offset(1, 1, 1)).inflate(15, 15, 15));

        for (PlayerEntity player : players) {
            if (player.abilities.instabuild) {
                return;
            }
            player.hurt(punishment, Float.MAX_VALUE);
        }
    }

    //region Rendering


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {

        if (state.getBlock() == DEContent.chaos_crystal) {
            return SHAPE;
        }
        else {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileChaosCrystal) {
                BlockPos offset = ((TileChaosCrystal) tile).parentPos.get().subtract(pos);
                return SHAPE.move(0, offset.getY(), 0);
            }
        }

        return SHAPE;
    }


    //endregion
}
