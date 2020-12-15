package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;

import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockBCore implements IHudDisplay {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static VoxelShape SHAPE_DOWN = VoxelShapes.create(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
    private static VoxelShape SHAPE_UP = VoxelShapes.create(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
    private static VoxelShape SHAPE_NORTH = VoxelShapes.create(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
    private static VoxelShape SHAPE_SOUTH = VoxelShapes.create(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
    private static VoxelShape SHAPE_WEST = VoxelShapes.create(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
    private static VoxelShape SHAPE_EAST = VoxelShapes.create(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
    private final TechLevel techLevel;


    public CraftingInjector(Properties properties, TechLevel techLevel) {
        super(properties);
        this.techLevel = techLevel;
        this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP));
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        world.setBlockState(pos, state.withProperty(TIER, TIER.fromMeta(stack.getItemDamage())));
//        super.onBlockPlacedBy(world, pos, state, placer, stack);
//
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileCraftingInjector) {
//            ((TileCraftingInjector) tile).facing.set((byte) Direction.getDirectionFromEntityLiving(pos, placer).getIndex());
//        }
//    }

    //endregion

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    //region Block


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCraftingInjector(techLevel);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (!(tile instanceof TileCraftingInjector)) {
            return ActionResultType.FAIL;
        }

        TileCraftingInjector craftingPedestal = (TileCraftingInjector) tile;

        if (player.isSneaking()) {
            craftingPedestal.singleItem.set(!craftingPedestal.singleItem.get());
            ChatHelper.sendIndexed(player, new TranslationTextComponent("msg.craftingInjector.singleItem" + (craftingPedestal.singleItem.get() ? "On" : "Off") + ".txt"), 98);
            craftingPedestal.getDataManager().detectAndSendChanges();
            return ActionResultType.SUCCESS;
        }

        if (!craftingPedestal.itemHandler.getStackInSlot(0).isEmpty()) {
            if (player.getHeldItemMainhand().isEmpty()) {
                player.setHeldItem(Hand.MAIN_HAND, craftingPedestal.itemHandler.getStackInSlot(0));
                craftingPedestal.setStackInPedestal(ItemStack.EMPTY);
            } else {
                world.addEntity(new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), craftingPedestal.itemHandler.getStackInSlot(0)));
                craftingPedestal.setStackInPedestal(ItemStack.EMPTY);
            }
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            player.setHeldItem(Hand.MAIN_HAND, InventoryUtils.insertItem(craftingPedestal.itemHandler, stack, false));
        }

        return ActionResultType.SUCCESS;
    }

    //endregion

    //region Rendering

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction facing = state.get(FACING);
        switch (facing) {
            case DOWN:
                return SHAPE_DOWN;
            case UP:
                return SHAPE_UP;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
        }
        return super.getShape(state, worldIn, pos, context);
    }

    //    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        Direction facing = getActualState(state, source, pos).getValue(FACING);
//
//        switch (facing) {
//            case DOWN:
//                return new AxisAlignedBB();
//            case UP:
//                return new AxisAlignedBB();
//            case NORTH:
//                return new AxisAlignedBB();
//            case SOUTH:
//                return new AxisAlignedBB();
//            case WEST:
//                return new AxisAlignedBB();
//            case EAST:
//                return new AxisAlignedBB();
//        }
//
//        return super.getBoundingBox(state, source, pos);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingInjector.class, new RenderTileCraftingInjector());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
//
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileCraftingInjector)) {
            return;
        }

        displayList.add(InfoHelper.HITC() + I18n.format("msg.craftingInjector.singleItem" + (((TileCraftingInjector) te).singleItem.get() ? "On" : "Off") + ".txt"));
    }

    //endregion
}
