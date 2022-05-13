package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 30/9/2015.
 */
public class ChaosShardAtmos extends BlockBCore {
    public ChaosShardAtmos() {
        super(Properties.of(Material.AIR).randomTicks().noCollission());

    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {}

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
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return true;
    }

    //endregion
}
