package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockMobSafe;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

/**
 * Created by Brandon on 23/07/2014.
 * Block for DE Generator
 */
public class Grinder extends BlockMobSafe implements ITileEntityProvider {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public Grinder() {
        super(Material.IRON);
        this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileGrinder tileGrinder = worldIn.getTileEntity(pos) instanceof TileGrinder ? (TileGrinder) worldIn.getTileEntity(pos) : null;
        return state.withProperty(ACTIVE, tileGrinder != null && tileGrinder.active.value);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return super.getBoundingBox(state, source, pos);
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
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        boolean b = super.rotateBlock(world, pos, axis);

        TileEntity tile = world.getTileEntity(pos);
        if (b && tile instanceof TileGrinder) {
            ((TileGrinder) tile).updateKillBox();
        }

        return b;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
    //endregion

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGrinder();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileGrinder && world.isRemote) {
                AxisAlignedBB bb = ((TileGrinder) tile).getKillBoxForRender();

                for (double i = 0; i <= 7; i += 0.01) {
                    Vec3D minX = new Vec3D(bb.minX + i, bb.minY, bb.minZ);
                    Vec3D minY = new Vec3D(bb.minX, bb.minY + i, bb.minZ);
                    Vec3D minZ = new Vec3D(bb.minX, bb.minY, bb.minZ + i);

                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minX, new Vec3D(), 0, 255, 255, 130);
                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minY, new Vec3D(), 0, 255, 255, 130);
                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minZ, new Vec3D(), 0, 255, 255, 130);

                    Vec3D maxX = new Vec3D(bb.maxX - i, bb.maxY, bb.maxZ);
                    Vec3D maxY = new Vec3D(bb.maxX, bb.maxY - i, bb.maxZ);
                    Vec3D maxZ = new Vec3D(bb.maxX, bb.maxY, bb.maxZ - i);

                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxX, new Vec3D(), 0, 255, 255, 130);
                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxY, new Vec3D(), 0, 255, 255, 130);
                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxZ, new Vec3D(), 0, 255, 255, 130);
                }


            }
        }
        else if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_GRINDER, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    //    @Override
//    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
//        super.neighborChanged(state, worldIn, pos, blockIn);
//    }
//
//    @Override
//	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
//		TileEntity tileEntity = worldIn.getTileEntity(pos);
//		if (tileEntity instanceof TileGrinder){
//			((TileGrinder)tileEntity).updateKillBox();
//            ((TileGrinder)tileEntity).powered = worldIn.isBlockPowered(pos);
//		}
//	}

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileGrinder) {
            ((TileGrinder) tileEntity).updateKillBox();
            ((TileGrinder) tileEntity).powered = world.isBlockPowered(pos);
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileGrinder) {
            ((TileGrinder) tileEntity).updateKillBox();
        }
    }
}


