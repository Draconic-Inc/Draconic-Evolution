package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class EntityDetector extends BlockBCore implements EntityBlock {
    //    public static final PropertyBool ADVANCED = PropertyBool.create("advanced");
    public static final AABB AABB = new AABB(0, 0, 0, 1, 1, 1);

    public EntityDetector(Properties properties) {
        super(properties);
        this.canProvidePower = true;
//        setBlockEntity(DEContent.tile_entity, true);
        //SEE flow gate for dual tile example
    }

//    public EntityDetector() {
//        setDefaultState(stateContainer.getBaseState().with(ADVANCED, false));

//        this.addName(0, "entity_detector_basic");
//        this.addName(1, "entity_detector_advanced");
//    }


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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.0626, 0, 0.0626, 0.9375, 0.125, 0.9375);
    }


    //endregion

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
