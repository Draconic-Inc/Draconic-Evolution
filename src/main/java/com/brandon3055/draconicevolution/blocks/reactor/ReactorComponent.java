package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.block.property.PropertyString;
import codechicken.lib.util.RotationUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class ReactorComponent extends BlockBCore/* implements ITileEntityProvider, IRegistryOverride, IRenderOverride*/ {

    public static final PropertyString TYPE = new PropertyString("type", "stabilizer", "injector");
    private static final VoxelShape SHAPE_INJ_DOWN  = VoxelShapes.create(0F, 0.885F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_UP    = VoxelShapes.create(0F, 0F, 0F, 1F, 0.125F, 1F);
    private static final VoxelShape SHAPE_INJ_NORTH = VoxelShapes.create(0F, 0F, 0.885F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_SOUTH = VoxelShapes.create(0F, 0F, 0F, 1F, 1F, 0.125F);
    private static final VoxelShape SHAPE_INJ_WEST  = VoxelShapes.create(0.885F, 0F, 0F, 1F, 1F, 1F);
    private static final VoxelShape SHAPE_INJ_EAST  = VoxelShapes.create(0F, 0F, 0F, 0.125F, 1F, 1F);
    private final boolean injector;

    public ReactorComponent(Properties properties, boolean injector) {
        super(properties);
        this.injector = injector;
        this.setDefaultState(stateContainer.getBaseState().with(TYPE, "stabilizer"));
//        this.addName(0, "reactor_stabilizer");
//        this.addName(1, "reactor_injector");
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    //region Block & Registry

//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, TYPE);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(TYPE).equals("stabilizer") ? 0 : 1;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(TYPE, meta == 0 ? "stabilizer" : "injector");
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        list.add(new ItemStack(this, 1, 0));
//        list.add(new ItemStack(this, 1, 1));
//    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return injector ? new TileReactorInjector() : new TileReactorStabilizer();
    }

//    @Override
//    public void handleCustomRegistration(Feature feature) {
//        GameRegistry.registerTileEntity(TileReactorStabilizer.class, feature.getRegistryName() + "_stabilizer");
//        GameRegistry.registerTileEntity(TileReactorEnergyInjector.class, feature.getRegistryName() + "_injector");
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileReactorInjector) {
            switch (((TileReactorInjector) tile).facing.get()) {
                case DOWN:
                    return SHAPE_INJ_DOWN;
                case UP:
                    return SHAPE_INJ_UP;
                case NORTH:
                    return SHAPE_INJ_NORTH;
                case SOUTH:
                    return SHAPE_INJ_SOUTH;
                case WEST:
                    return SHAPE_INJ_WEST;
                case EAST:
                    return SHAPE_INJ_EAST;
            }
        }
        return super.getShape(state, world, pos, context);
    }

    //endregion

    //region Rendering

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        StateMap deviceStateMap = new StateMap.Builder().ignore(TYPE).build();
//        ModelLoader.setCustomStateMapper(this, deviceStateMap);
//        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorStabilizer.class, new RenderTileReactorComponent());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorEnergyInjector.class, new RenderTileReactorComponent());
//        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemReactorComponent());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    //endregion

    //region Place & Interact

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        Direction facing = RotationUtils.getPlacedRotation(pos, placer);
        if (placer.isShiftKeyDown()) {
            facing = facing.getOpposite();
        }

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).facing.set(facing);
            ((TileReactorComponent) te).onPlaced();
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onActivated(player);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onBroken();
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("info.de.shiftReversePlaceLogic.txt"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

//    @Override
//    public int damageDropped(BlockState state) {
//        return getMetaFromState(state);
//    }

    //endregion


    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileReactorComponent) {
            return ((TileReactorComponent) tileEntity).rsPower.get();
        }

        return 0;
    }
}
