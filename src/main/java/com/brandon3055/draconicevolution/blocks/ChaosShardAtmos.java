package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

/**
 * Created by brandon3055 on 30/9/2015.
 */
public class ChaosShardAtmos extends BlockBCore {
    public ChaosShardAtmos() {
        super(Properties.of(Material.AIR).randomTicks().noCollission());

    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {}

//    @Override
//    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
//        if (world.isRemote || Utils.getClosestPlayer(world, pos.getX(), pos.getY(), pos.getZ(), 24, true, true) == null) {
//            return;
//        }
//
////        if (rand.nextInt(25) == 0) {
////            for (int searchX = x - 15; searchX < x + 15; searchX++) {
////                for (int searchZ = z - 15; searchZ < z + 15; searchZ++) {
////                    if (world.getBlock(searchX, 80, searchZ) == ModBlocks.chaosCrystal) {
////                        EntityChaosBolt bolt = new EntityChaosBolt(world, x + 0.5, y + 0.5, z + 0.5, searchX + 0.5, 80.5, searchZ + 0.5);
////                        world.spawnEntityInWorld(bolt);
////                        return;
////                    }
////                }
////            }
////            world.setBlockToAir(x, y, z);
////        }
//    }


    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }
//
//    @Nullable
//    @Override
//    public Item getItemDropped(BlockState state, Random rand, int fortune) {
//        return null;
//    }

    //region Air Block Stuff


    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
        return true;
    }

    //endregion
}
