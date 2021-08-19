package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.api.hud.IHudBlock;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;

import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.api.hud.IHudDisplay;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockBCore implements IHudBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static VoxelShape SHAPE_DOWN = VoxelShapes.box(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
    private static VoxelShape SHAPE_UP = VoxelShapes.box(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
    private static VoxelShape SHAPE_NORTH = VoxelShapes.box(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
    private static VoxelShape SHAPE_SOUTH = VoxelShapes.box(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
    private static VoxelShape SHAPE_WEST = VoxelShapes.box(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
    private static VoxelShape SHAPE_EAST = VoxelShapes.box(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
    private final TechLevel techLevel;

    public CraftingInjector(Properties properties, TechLevel techLevel) {
        super(properties);
        this.techLevel = techLevel;
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFusionCraftingInjector();
    }

    @Override
    public void destroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
        super.destroy(p_176206_1_, p_176206_2_, p_176206_3_);
    }

    @Override
    public void onRemove(BlockState p_196243_1_, World world, BlockPos pos, BlockState p_196243_4_, boolean p_196243_5_) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFusionCraftingInjector) {
            ((TileFusionCraftingInjector) tile).onDestroyed();
        }
        super.onRemove(p_196243_1_, world, pos, p_196243_4_, p_196243_5_);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        TileEntity tile = world.getBlockEntity(pos);

        if (!(tile instanceof TileFusionCraftingInjector)) {
            return ActionResultType.FAIL;
        }

        TileFusionCraftingInjector craftingPedestal = (TileFusionCraftingInjector) tile;

        if (player.isShiftKeyDown()) {
            craftingPedestal.singleItem.set(!craftingPedestal.singleItem.get());
            ChatHelper.sendIndexed(player, new TranslationTextComponent("fusion_inj.draconicevolution." + (craftingPedestal.singleItem.get() ? "single_item" : "multi_item")), 98);
            craftingPedestal.getDataManager().detectAndSendChanges();
            return ActionResultType.SUCCESS;
        }

        if (!craftingPedestal.itemHandler.getStackInSlot(0).isEmpty()) {
            if (player.getMainHandItem().isEmpty()) {
                player.setItemInHand(Hand.MAIN_HAND, craftingPedestal.itemHandler.getStackInSlot(0));
                craftingPedestal.setInjectorStack(ItemStack.EMPTY);
            } else {
                world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), craftingPedestal.itemHandler.getStackInSlot(0)));
                craftingPedestal.setInjectorStack(ItemStack.EMPTY);
            }
        } else {
            ItemStack stack = player.getMainHandItem();
            player.setItemInHand(Hand.MAIN_HAND, InventoryUtils.insertItem(craftingPedestal.itemHandler, stack, false));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction facing = state.getValue(FACING);
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

    @Override
    public void generateHudText(World world, BlockPos pos, PlayerEntity player, List<ITextComponent> displayList) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof TileFusionCraftingInjector) {
            boolean single = ((TileFusionCraftingInjector) te).singleItem.get();
            displayList.add(new TranslationTextComponent("fusion_inj.draconicevolution." + (single ? "single_item" : "multi_item")).withStyle(TextFormatting.ITALIC, TextFormatting.GOLD));
        }
    }
}
