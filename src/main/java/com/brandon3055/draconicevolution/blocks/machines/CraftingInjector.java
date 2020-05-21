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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockBCore implements /*ITileEntityProvider, IRenderOverride,*/ IHudDisplay {

//    public static final PropertyString TIER = new PropertyString("tier", "basic", "wyvern", "draconic", "chaotic");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public CraftingInjector(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState()/*.with(TIER, "basic")*/.with(FACING, Direction.UP));

//        this.addName(0, "crafting_injector_basic");
//        this.addName(1, "crafting_injector_wyvern");
//        this.addName(2, "crafting_injector_draconic");
//        this.addName(3, "crafting_injector_chaotic");
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, TIER, FACING);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileEntity tile = worldIn.getTileEntity(pos);
//
//        if (tile instanceof TileCraftingInjector) {
//            return state.withProperty(FACING, Direction.getFront(((TileCraftingInjector) tile).facing.get()));
//        }
//
//        return state;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return this.getDefaultState().withProperty(TIER, TIER.fromMeta(meta));
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return TIER.toMeta(state.getValue(TIER));
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        list.add(new ItemStack(this, 1, 0));
//        list.add(new ItemStack(this, 1, 1));
//        list.add(new ItemStack(this, 1, 2));
//        list.add(new ItemStack(this, 1, 3));
//    }
//
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

    //region Block


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCraftingInjector(TechLevel.DRACONIUM);
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

        if (player.isShiftKeyDown()) {
            craftingPedestal.singleItem.set(!craftingPedestal.singleItem.get());
            ChatHelper.indexedTrans(player, "msg.craftingInjector.singleItem" + (craftingPedestal.singleItem.get() ? "On" : "Off") + ".txt", -30553055);
            craftingPedestal.getDataManager().detectAndSendChanges();
            return ActionResultType.SUCCESS;
        }

        if (!craftingPedestal.itemHandler.getStackInSlot(0).isEmpty()) {
            if (player.getHeldItemMainhand().isEmpty()) {
                player.setHeldItem(Hand.MAIN_HAND, craftingPedestal.itemHandler.getStackInSlot(0));
                craftingPedestal.setStackInPedestal(ItemStack.EMPTY);
            } else {
                world.addEntity(new ItemEntity(world, player.posX, player.posY, player.posZ, craftingPedestal.itemHandler.getStackInSlot(0)));
                craftingPedestal.setStackInPedestal(ItemStack.EMPTY);
            }
        } else {
            ItemStack stack = player.getHeldItemMainhand();
//            ItemStack remainder = ;
//            stack.setCount(remainder);
            player.setHeldItem(Hand.MAIN_HAND, InventoryUtils.insertItem(craftingPedestal.itemHandler, stack, false));
        }

        return ActionResultType.SUCCESS;
    }

//    @Override
//    public int damageDropped(BlockState state) {
//        return getMetaFromState(state);
//    }

    //endregion

    //region Rendering

//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        Direction facing = getActualState(state, source, pos).getValue(FACING);
//
//        switch (facing) {
//            case DOWN:
//                return new AxisAlignedBB(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
//            case UP:
//                return new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
//            case NORTH:
//                return new AxisAlignedBB(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
//            case SOUTH:
//                return new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
//            case WEST:
//                return new AxisAlignedBB(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
//            case EAST:
//                return new AxisAlignedBB(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
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
