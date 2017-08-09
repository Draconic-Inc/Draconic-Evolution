package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileCraftingInjector;
import com.brandon3055.draconicevolution.lib.PropertyStringTemp;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockBCore implements ITileEntityProvider, IRenderOverride {

    public static final PropertyStringTemp TIER = new PropertyStringTemp("tier", "basic", "wyvern", "draconic", "chaotic");
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    public CraftingInjector() {
        super(Material.IRON);
        this.setDefaultState(blockState.getBaseState().withProperty(TIER, "basic").withProperty(FACING, EnumFacing.UP));
        this.addName(0, "crafting_injector_basic");
        this.addName(1, "crafting_injector_wyvern");
        this.addName(2, "crafting_injector_draconic");
        this.addName(3, "crafting_injector_chaotic");
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TIER, FACING);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileCraftingInjector) {
            return state.withProperty(FACING, EnumFacing.getFront(((TileCraftingInjector) tile).facing.value));
        }

        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, TIER.fromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return TIER.toMeta(state.getValue(TIER));
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(TIER, TIER.fromMeta(stack.getItemDamage())));
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCraftingInjector) {
            ((TileCraftingInjector) tile).facing.value = (byte) EnumFacing.getDirectionFromEntityLiving(pos, placer).getIndex();
        }
    }

    //endregion

    //region Block

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCraftingInjector();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (!(tile instanceof TileCraftingInjector)) {
            return false;
        }

        TileCraftingInjector craftingPedestal = (TileCraftingInjector) tile;

        if (!craftingPedestal.getStackInSlot(0).isEmpty()) {
            if (player.getHeldItemMainhand().isEmpty()) {
                player.setHeldItem(EnumHand.MAIN_HAND, craftingPedestal.getStackInSlot(0));
                craftingPedestal.setInventorySlotContents(0, ItemStack.EMPTY);
            }
            else {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, craftingPedestal.getStackInSlot(0)));
                craftingPedestal.setInventorySlotContents(0, ItemStack.EMPTY);
            }

        }
        else {
            ItemStack stack = player.getHeldItemMainhand();
            craftingPedestal.setInventorySlotContents(0, stack);
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }

        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    //endregion

    //region Rendering

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = getActualState(state, source, pos).getValue(FACING);

        switch (facing) {
            case DOWN:
                return new AxisAlignedBB(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
            case UP:
                return new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
            case NORTH:
                return new AxisAlignedBB(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
            case SOUTH:
                return new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
            case WEST:
                return new AxisAlignedBB(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
            case EAST:
                return new AxisAlignedBB(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
        }

        return super.getBoundingBox(state, source, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingInjector.class, new RenderTileCraftingInjector());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    //endregion
}
