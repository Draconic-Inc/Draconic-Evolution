package com.brandon3055.draconicevolution.blocks;

import codechicken.lib.raytracer.ICuboidProvider;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.brandon3055.draconicevolution.client.render.tile.RenderTilePlacedItem;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class PlacedItem extends BlockBCore implements ITileEntityProvider, ICustomRender {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public PlacedItem() {
        super(Material.ROCK);
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(1.5F).setResistance(10.0F);
        this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setIsFullCube(false);
    }

    //region Block state and stuff...

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePlacedItem();
    }

    //endregion

    //region Render

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
    }

    //endregion

    //region Interact

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePlacedItem) {

            RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
            RayTraceResult subHitResult = RayTracer.rayTraceCuboidsClosest(new Vector3(RayTracer.getStartVec(player)), new Vector3(RayTracer.getEndVec(player)), ((TilePlacedItem) tile).getIndexedCuboids(), pos);

            if (subHitResult != null) {
                hit = subHitResult;
            }
            else if (hit == null) {
                return true;
            }

            ((TilePlacedItem) tile).handleClick(hit.subHit, player);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);

        if (tile instanceof ICuboidProvider) {
            return ((ICuboidProvider) tile).getIndexedCuboids().get(0).aabb();
//            return ((TilePlacedItem) tile).getBlockBounds().aabb();
        }

        return super.getBoundingBox(state, source, pos);
    }

    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ICuboidProvider) {
            return RayTracer.rayTraceCuboidsClosest(start, end, ((ICuboidProvider) tile).getIndexedCuboids(), pos);
        }
        return super.collisionRayTrace(state, world, pos, start, end);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return false;
    }

    //endregion

    //region Harvest

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePlacedItem) {

            RayTraceResult hit = target;
            RayTraceResult subHitResult = RayTracer.rayTraceCuboidsClosest(new Vector3(RayTracer.getStartVec(player)), new Vector3(RayTracer.getEndVec(player)), ((TilePlacedItem) tile).getIndexedCuboids(), pos);

            if (subHitResult != null) {
                hit = subHitResult;
            }
            else if (hit == null) {
                return null;
            }

            if (hit.subHit > 0 && ((TilePlacedItem) tile).getStackInSlot(hit.subHit - 1) != null) {
                return ((TilePlacedItem) tile).getStackInSlot(hit.subHit - 1);
            }
        }

        return null;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack heldStack) {
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TilePlacedItem) {
            ((TilePlacedItem) tile).breakBlock();
        }

        super.breakBlock(world, pos, state);
    }

    //endregion
}
