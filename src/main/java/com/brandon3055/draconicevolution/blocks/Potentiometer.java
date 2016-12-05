package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer;
import com.brandon3055.draconicevolution.client.render.tile.RenderTilePotentiometer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class Potentiometer extends BlockBCore implements ITileEntityProvider, ICustomRender {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625D, 0.9375D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.9375D, 0.9375D, 0.9375D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.0625D);
    protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.9375D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.0625D, 0.9375D, 0.9375D);

    public Potentiometer() {
        setIsFullCube(false);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.canProvidePower = true;
        this.setHardness(2F);
    }

    //region Blockstate


    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing;

        switch (meta)
        {
            case 0:
                enumfacing = EnumFacing.DOWN;
                break;
            case 1:
                enumfacing = EnumFacing.EAST;
                break;
            case 2:
                enumfacing = EnumFacing.WEST;
                break;
            case 3:
                enumfacing = EnumFacing.SOUTH;
                break;
            case 4:
                enumfacing = EnumFacing.NORTH;
                break;
            case 5:
            default:
                enumfacing = EnumFacing.UP;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        int i;

        switch (state.getValue(FACING))
        {
            case EAST:
                i = 1;
                break;
            case WEST:
                i = 2;
                break;
            case SOUTH:
                i = 3;
                break;
            case NORTH:
                i = 4;
                break;
            case UP:
            default:
                i = 5;
                break;
            case DOWN:
                i = 0;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    //endregion

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePotentiometer();
    }

    //region place

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return canPlaceBlock(worldIn, pos, facing.getOpposite()) ? this.getDefaultState().withProperty(FACING, facing) : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN);
    }

    protected static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing direction)
    {
        BlockPos blockpos = pos.offset(direction);
        return worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, direction.getOpposite());
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        if (this.checkForDrop(worldIn, pos, state) && !canPlaceBlock(worldIn, pos, ((EnumFacing)state.getValue(FACING)).getOpposite()))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.canPlaceBlockAt(worldIn, pos))
        {
            return true;
        }
        else
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return canPlaceBlock(worldIn, pos, side.getOpposite());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (canPlaceBlock(worldIn, pos, enumfacing))
            {
                return true;
            }
        }

        return false;
    }

    //endregion

    //region render

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
//        ModelRegistryHelper.register(new ModelResourceLocation(feature.registryName(), "normal"), new CCOverrideBakedBlockModel(""));
        ClientRegistry.bindTileEntitySpecialRenderer(TilePotentiometer.class, new RenderTilePotentiometer());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        switch (enumfacing)
        {
            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case SOUTH:
                return AABB_SOUTH;
            case NORTH:
            default:
                return AABB_NORTH;
            case UP:
                return AABB_UP;
            case DOWN:
                return AABB_DOWN;
        }
    }

    //endregion
}
