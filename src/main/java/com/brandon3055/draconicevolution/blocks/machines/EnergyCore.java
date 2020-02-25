package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStorageCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyCore extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {

    public static final IntegerProperty RENDER_TYPE = IntegerProperty.create("modelrender", 0, 2); //If this is what i think it is then it needs to go!
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active"); //Why do i need this on the block?

    public EnergyCore(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(RENDER_TYPE, 0).with(ACTIVE, false));
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(RENDER_TYPE, ACTIVE);
    }

    //region BlockState

//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, RENDER_TYPE, ACTIVE);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(ACTIVE) ? 1 : 0;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(ACTIVE, meta == 1);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileEntity core = worldIn.getTileEntity(pos);
//        return state.withProperty(RENDER_TYPE, 0).withProperty(ACTIVE, core instanceof TileEnergyStorageCore && ((TileEnergyStorageCore) core).active.get());
//    }

    //endregion


    //region Render Stuff


//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return getActualState(state, worldIn, pos).getValue(ACTIVE) ? new AxisAlignedBB(0, 0, 0, 0, 0, 0) : super.getCollisionBoundingBox(state, worldIn, pos);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockAccess blockAccess, BlockPos pos, Direction side) {
//        return !state.getValue(ACTIVE);
//    }

    //endregion


    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity core = world.getTileEntity(pos);

        if (core instanceof TileStorageCore && !world.isRemote) {
            ((TileStorageCore) core).onStructureClicked(world, pos, state, player);
        }

        return true;
    }

    //region Interfaces


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileStorageCore();
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        Item item = Item.getItemFromBlock(this);
//        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ResourceHelperDE.RESOURCE_PREFIX + feature.getName(), "inventory"));
//
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyStorageCore.class, new RenderTileEnergyStorageCore());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    //endregion
}
