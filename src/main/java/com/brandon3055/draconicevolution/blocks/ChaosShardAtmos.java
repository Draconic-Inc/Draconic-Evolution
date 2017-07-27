package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 30/9/2015.
 */
public class ChaosShardAtmos extends BlockBCore {
    public ChaosShardAtmos() {
        super(Material.AIR);
        this.setTickRandomly(true);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote || Utils.getClosestPlayer(world, pos.getX(), pos.getY(), pos.getZ(), 24, true, true) == null) {
            return;
        }

//        if (rand.nextInt(25) == 0) {
//            for (int searchX = x - 15; searchX < x + 15; searchX++) {
//                for (int searchZ = z - 15; searchZ < z + 15; searchZ++) {
//                    if (world.getBlock(searchX, 80, searchZ) == ModBlocks.chaosCrystal) {
//                        EntityChaosBolt bolt = new EntityChaosBolt(world, x + 0.5, y + 0.5, z + 0.5, searchX + 0.5, 80.5, searchZ + 0.5);
//                        world.spawnEntityInWorld(bolt);
//                        return;
//                    }
//                }
//            }
//            world.setBlockToAir(x, y, z);
//        }
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    //region Air Block Stuff

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    //endregion
}
