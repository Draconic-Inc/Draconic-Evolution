package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.properties.PropertyString;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingPedestal;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileCraftingPedestal;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingPedestal extends BlockBCore implements ITileEntityProvider, ICustomRender {

    public static final PropertyString TIER = new PropertyString("tier", "basic", "wyvern", "draconic", "chaotic");
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    public CraftingPedestal(){
        super(Material.IRON);
        this.setDefaultState(blockState.getBaseState().withProperty(TIER, "basic").withProperty(FACING, EnumFacing.UP));
        setIsFullCube(false);
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TIER, FACING);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileCraftingPedestal){
            return state.withProperty(FACING, EnumFacing.getFront(((TileCraftingPedestal) tile).facing.value));
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
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
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

        if (tile instanceof TileCraftingPedestal) {
            ((TileCraftingPedestal) tile).facing.value = (byte) BlockPistonBase.getFacingFromEntity(pos, placer).getIndex();
        }
    }

    //endregion

    //region Block

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCraftingPedestal();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (!(tile instanceof TileCraftingPedestal)) {
            return false;
        }

        TileCraftingPedestal craftingPedestal = (TileCraftingPedestal)tile;

        if (craftingPedestal.getStackInSlot(0) != null){
            if (player.getHeldItemMainhand() == null){
                player.setHeldItem(EnumHand.MAIN_HAND, craftingPedestal.getStackInSlot(0));
                craftingPedestal.setInventorySlotContents(0, null);
            }else {
                world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, craftingPedestal.getStackInSlot(0)));
                craftingPedestal.setInventorySlotContents(0, null);
            }

        }else {
            ItemStack stack = player.getHeldItemMainhand();
            craftingPedestal.setInventorySlotContents(0, stack);
            player.setHeldItem(EnumHand.MAIN_HAND, null);
        }

        return true;
    }

    //endregion

    //region Rendering

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = getActualState(state, source, pos).getValue(FACING);

        switch (facing){
            case DOWN:  return new AxisAlignedBB(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
            case UP:    return new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
            case NORTH: return new AxisAlignedBB(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
            case SOUTH: return new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
            case WEST:  return new AxisAlignedBB(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
            case EAST:  return new AxisAlignedBB(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
        }

        return super.getBoundingBox(state, source, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingPedestal.class, new RenderTileCraftingPedestal());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    //endregion
}
