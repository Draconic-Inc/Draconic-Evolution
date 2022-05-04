package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by Brandon on 23/07/2014.
 * Block for DE Generator
 */
public class Grinder extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public Grinder(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false)); //TODO figure out if/when set default is actually needed.
        setMobResistant();
    }

    // Rendering

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

//    @Override
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(feature.getRegistryName(), "inventory"));
//        ClientRegistry.bindTileEntitySpecialRenderer(TileGrinder.class, new RenderTileGrinder());
//    }
//
//    //region BlockState
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, ACTIVE, FACING, STATIC);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileGrinder tileGrinder = worldIn.getTileEntity(pos) instanceof TileGrinder ? (TileGrinder) worldIn.getTileEntity(pos) : null;
//        return state.withProperty(ACTIVE, tileGrinder != null && tileGrinder.active.get());
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        Direction enumfacing = Direction.getFront(meta);
//
//        if (enumfacing.getAxis() == Direction.Axis.Y) {
//            enumfacing = Direction.NORTH;
//        }
//
//        return this.getDefaultState().withProperty(FACING, enumfacing);
//    }
//
//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        return super.getBoundingBox(state, source, pos);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(FACING).getIndex();
//    }
//
//    @Override
//    public BlockState withRotation(BlockState state, Rotation rot) {
//        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
//    }
//
//    @Override
//    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
//        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
//    }
//
//    @Override
//    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
//        boolean b = super.rotateBlock(world, pos, axis);
//
//        TileEntity tile = world.getTileEntity(pos);
//        if (b && tile instanceof TileGrinder) {
//            ((TileGrinder) tile).validateKillZone(true);
//        }
//
//        return b;
//    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

//    @Override
//    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
//        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
//    }
    //endregion


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileGrinder();
    }

//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        if (player.isShiftKeyDown()) {
////            TileEntity tile = world.getTileEntity(pos);
////            if (tile instanceof TileGrinder && world.isRemote) {
////                AxisAlignedBB bb = ((TileGrinder) tile).getKillBoxForRender();
////
////                for (double i = 0; i <= 7; i += 0.01) {
////                    Vec3D minX = new Vec3D(bb.minX + i, bb.minY, bb.minZ);
////                    Vec3D minY = new Vec3D(bb.minX, bb.minY + i, bb.minZ);
////                    Vec3D minZ = new Vec3D(bb.minX, bb.minY, bb.minZ + i);
////
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minX, new Vec3D(), 0, 255, 255, 130);
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minY, new Vec3D(), 0, 255, 255, 130);
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, minZ, new Vec3D(), 0, 255, 255, 130);
////
////                    Vec3D maxX = new Vec3D(bb.maxX - i, bb.maxY, bb.maxZ);
////                    Vec3D maxY = new Vec3D(bb.maxX, bb.maxY - i, bb.maxZ);
////                    Vec3D maxZ = new Vec3D(bb.maxX, bb.maxY, bb.maxZ - i);
////
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxX, new Vec3D(), 0, 255, 255, 130);
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxY, new Vec3D(), 0, 255, 255, 130);
////                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, maxZ, new Vec3D(), 0, 255, 255, 130);
////                }
////
////
////            }
//        }
//        else if (!world.isRemote) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_GRINDER, world, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
}


