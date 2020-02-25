package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 14/11/2016.
 */
public class FlowGate extends BlockBCore/* implements IRegistryOverride*/ {

//    public static final PropertyStringTemp TYPE = new PropertyStringTemp("type", "flux", "fluid");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FlowGate(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
//        this.addName(0, "flux_gate");
//        this.addName(8, "fluid_gate");
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, TYPE, FACING);
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        Direction enumfacing;
//
//        switch (meta & 7) {
//            case 0:
//                enumfacing = Direction.DOWN;
//                break;
//            case 1:
//                enumfacing = Direction.EAST;
//                break;
//            case 2:
//                enumfacing = Direction.WEST;
//                break;
//            case 3:
//                enumfacing = Direction.SOUTH;
//                break;
//            case 4:
//                enumfacing = Direction.NORTH;
//                break;
//            case 5:
//            default:
//                enumfacing = Direction.UP;
//        }
//
//        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(TYPE, (meta & 8) > 0 ? "fluid" : "flux");
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        int i;
//
//        switch (state.getValue(FACING)) {
//            case EAST:
//                i = 1;
//                break;
//            case WEST:
//                i = 2;
//                break;
//            case SOUTH:
//                i = 3;
//                break;
//            case NORTH:
//                i = 4;
//                break;
//            case UP:
//            default:
//                i = 5;
//                break;
//            case DOWN:
//                i = 0;
//        }
//
//        if (state.getValue(TYPE).equals("fluid")) {
//            i |= 8;
//        }
//
//        return i;
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        list.add(new ItemStack(this, 1, 0));
//        list.add(new ItemStack(this, 1, 8));
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        Direction facing = RotationUtils.getPlacedRotation(pos, placer);
//        if (placer.isSneaking()) {
//            facing = facing.getOpposite();
//        }
//        world.setBlockState(pos, state.withProperty(FACING, facing).withProperty(TYPE, (stack.getItemDamage() & 8) > 0 ? "fluid" : "flux"), 2);
//        super.onBlockPlacedBy(world, pos, state, placer, stack);
//    }
//
//
//    //endregion
//
//    @Override
//    public boolean hasTileEntity(BlockState state) {
//        return true;
//    }
//
//    @Override
//    public TileEntity createTileEntity(World world, BlockState state) {
//        return state.getValue(TYPE).equals("flux") ? new TileFluxGate() : new TileFluidGate();
//    }
//
//    @Override
//    public void handleCustomRegistration(Feature feature) {
//        GameRegistry.registerTileEntity(TileFluxGate.class, feature.getModid() + ":flux_gate");
//        GameRegistry.registerTileEntity(TileFluidGate.class, feature.getModid() + ":fluid_gate");
//    }
//
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        if (!worldIn.isRemote) {
//            FMLNetworkHandler.openGui(playerIn, DraconicEvolution.instance, GuiHandler.GUIID_FLOW_GATE, worldIn, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }
//
//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
//        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
//        stack.setItemDamage(state.getValue(TYPE).equals("flux") ? 0 : 8);
//        return stack;
//    }
//
//    @Override
//    public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side) {
//        return true;
//    }
//
//    @Override
//    public int damageDropped(BlockState state) {
//        return state.getValue(TYPE).equals("fluid") ? 8 : 0;
//    }
}