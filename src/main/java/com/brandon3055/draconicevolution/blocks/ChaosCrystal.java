package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends BlockBCore/*, IRenderOverride*/ {

    public ChaosCrystal(Properties properties) {
        super(properties);
    }


    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        TileChaosCrystal tile = world.getTileEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getTileEntity(pos) : null;
        if (tile == null || !tile.guardianDefeated.get()) {
            return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        TileChaosCrystal tile = world.getTileEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getTileEntity(pos) : null;
        if (tile != null) return tile.guardianDefeated.get() ? 100F : -1F;
        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }


    //    @Override
//    public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
//        TileChaosCrystal tile = world.getTileEntity(pos) instanceof TileChaosCrystal ? (TileChaosCrystal) world.getTileEntity(pos) : null;
//        if (tile != null) return tile.guardianDefeated.get() ? 100F : -1F;
//        return super.getBlockHardness(blockState, world, pos);
//    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileChaosCrystal();
    }

//    @Nullable
//    @Override
//    public Item getItemDropped(BlockState state, Random rand, int fortune) {
//        return DEFeatures.chaosShard;
//    }
//
//    @Override
//    public int quantityDropped(Random random) {
//        return 5;
//    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).detonate();
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof PlayerEntity && ((PlayerEntity) placer).abilities.isCreativeMode) {
            TileEntity tile = world.getTileEntity(pos);
            if (!world.isRemote && tile instanceof TileChaosCrystal) {
                ((TileChaosCrystal) tile).setLockPos();
                ((TileChaosCrystal) tile).guardianDefeated.set(true);
            }
        } else {
            placer.attackEntityFrom(punishment, Float.MAX_VALUE);
        }
    }

    private static DamageSource punishment = new DamageSource("chrystalMoved").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(15, 15, 15));

        for (PlayerEntity player : players) {
            if (player.abilities.isCreativeMode) {
                return;
            }
            player.attackEntityFrom(punishment, Float.MAX_VALUE);
        }
    }

    //region Rendering


//    @Override
//    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
//        return false;
//    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileChaosCrystal.class, new RenderTileChaosCrystal());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    //endregion
}
