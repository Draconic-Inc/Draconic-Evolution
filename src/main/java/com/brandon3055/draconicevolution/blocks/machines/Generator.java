package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Brandon on 23/07/2014.
 * Block for DE Generator
 */
public class Generator extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public Generator(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ACTIVE, false));
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    //    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(feature.getRegistryName(), "inventory"));
//        ClientRegistry.bindTileEntitySpecialRenderer(TileGenerator.class, new AnimationTESR<>());
//    }

    //region BlockState
//    @Override
//    protected ExtendedBlockState createBlockState() {
//        return new ExtendedBlockState(this, new IProperty[] {FACING, ACTIVE, Properties.StaticProperty}, new IUnlistedProperty[]{Properties.AnimationProperty});
//    }

//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileGenerator tileGenerator = worldIn.getTileEntity(pos) instanceof TileGenerator ? (TileGenerator) worldIn.getTileEntity(pos) : null;
//        return state.withProperty(ACTIVE, tileGenerator != null && tileGenerator.active.get());
//    }

//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
////        TileGenerator tileGenerator = world.getTileEntity(pos) instanceof TileGenerator ? (TileGenerator) world.getTileEntity(pos) : null;
////        return state.withProperty(ACTIVE, tileGenerator != null && tileGenerator.active.get());
//        return super.getExtendedState(state, world, pos);
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
//    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand) {
//        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
//    }

//    @Override
//    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
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
        return new TileGenerator();
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        return 0; //TODO Light Level  //state.getActualState(world, pos).getValue(ACTIVE) ? 13 : 0;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        if (!world.isRemote) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_GENERATOR, world, pos.getX(), pos.getY(), pos.getZ());
//        }
        return true;
    }
}


