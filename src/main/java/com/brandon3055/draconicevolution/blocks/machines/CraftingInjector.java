package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.hud.IHudBlock;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends EntityBlockBCore implements IHudBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static VoxelShape SHAPE_DOWN = Shapes.box(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
    private static VoxelShape SHAPE_UP = Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
    private static VoxelShape SHAPE_NORTH = Shapes.box(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
    private static VoxelShape SHAPE_SOUTH = Shapes.box(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
    private static VoxelShape SHAPE_WEST = Shapes.box(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
    private static VoxelShape SHAPE_EAST = Shapes.box(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
    private final TechLevel techLevel;

    public CraftingInjector(Properties properties, TechLevel techLevel) {
        super(properties);
        this.techLevel = techLevel;
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
        setBlockEntity(() -> DEContent.tile_crafting_injector, false);
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void destroy(LevelAccessor p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
        super.destroy(p_176206_1_, p_176206_2_, p_176206_3_);
    }

    @Override
    public void onRemove(BlockState p_196243_1_, Level world, BlockPos pos, BlockState p_196243_4_, boolean p_196243_5_) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFusionCraftingInjector) {
            ((TileFusionCraftingInjector) tile).onDestroyed();
        }
        super.onRemove(p_196243_1_, world, pos, p_196243_4_, p_196243_5_);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity tile = world.getBlockEntity(pos);

        if (!(tile instanceof TileFusionCraftingInjector)) {
            return InteractionResult.FAIL;
        }

        TileFusionCraftingInjector craftingPedestal = (TileFusionCraftingInjector) tile;

        if (player.isShiftKeyDown()) {
            craftingPedestal.singleItem.set(!craftingPedestal.singleItem.get());
            ChatHelper.sendIndexed(player, new TranslatableComponent("fusion_inj.draconicevolution." + (craftingPedestal.singleItem.get() ? "single_item" : "multi_item")), 98);
            craftingPedestal.getDataManager().detectAndSendChanges();
            return InteractionResult.SUCCESS;
        }

        if (!craftingPedestal.itemHandler.getStackInSlot(0).isEmpty()) {
            if (player.getMainHandItem().isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, craftingPedestal.itemHandler.getStackInSlot(0));
                craftingPedestal.setInjectorStack(ItemStack.EMPTY);
            } else {
                world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), craftingPedestal.itemHandler.getStackInSlot(0)));
                craftingPedestal.setInjectorStack(ItemStack.EMPTY);
            }
        } else {
            ItemStack stack = player.getMainHandItem();
            player.setItemInHand(InteractionHand.MAIN_HAND, InventoryUtils.insertItem(craftingPedestal.itemHandler, stack, false));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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
    public void generateHudText(Level world, BlockPos pos, Player player, List<Component> displayList) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileFusionCraftingInjector) {
            boolean single = ((TileFusionCraftingInjector) te).singleItem.get();
            displayList.add(new TranslatableComponent("fusion_inj.draconicevolution." + (single ? "single_item" : "multi_item")).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        }
    }
}
