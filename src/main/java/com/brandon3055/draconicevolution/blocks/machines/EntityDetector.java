package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class EntityDetector extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {
//    public static final PropertyBool ADVANCED = PropertyBool.create("advanced");
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public EntityDetector(Properties properties) {
        super(properties);
        this.canProvidePower = true;
    }

//    public EntityDetector() {
//        setDefaultState(stateContainer.getBaseState().with(ADVANCED, false));

//        this.addName(0, "entity_detector_basic");
//        this.addName(1, "entity_detector_advanced");
//    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    //region BlockState

//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        list.add(new ItemStack(this));
//        list.add(new ItemStack(this, 1, 1));
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(ADVANCED) ? 1 : 0;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(ADVANCED, meta == 1);
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, ADVANCED);
//    }
//
//    @Override
//    public int damageDropped(BlockState state) {
//        return state.getValue(ADVANCED) ? 1 : 0;
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        world.setBlockState(pos, state.withProperty(ADVANCED, stack.getItemDamage() == 1));
//        super.onBlockPlacedBy(world, pos, state, placer, stack);
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.create(0.0626, 0, 0.0626, 0.9375, 0.125, 0.9375);
    }


    //endregion


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityDetector(true);
    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetector.class, new RenderTileEntityDetector());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
}
