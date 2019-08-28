package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileCraftingInjector;
import com.brandon3055.draconicevolution.lib.PropertyStringTemp;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockBCore implements ITileEntityProvider, IRenderOverride, IHudDisplay {

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
			return state.withProperty(FACING, EnumFacing.VALUES[MathHelper.abs(((TileCraftingInjector) tile).facing.value) % EnumFacing.VALUES.length]);
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
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
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

        if (player.isSneaking()) {
            craftingPedestal.singleItem.value = !craftingPedestal.singleItem.value;
            ChatHelper.indexedTrans(player, "msg.craftingInjector.singleItem" + (craftingPedestal.singleItem.value ? "On" : "Off") + ".txt", -30553055);
            craftingPedestal.getDataManager().detectAndSendChanges();
            return true;
        }

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
            int remainder = InventoryUtils.insertItem(craftingPedestal, stack, false);
            stack.setCount(remainder);
            player.setHeldItem(EnumHand.MAIN_HAND, stack);
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileCraftingInjector)) {
            return;
        }

        displayList.add(InfoHelper.HITC() + I18n.format("msg.craftingInjector.singleItem" + (((TileCraftingInjector) te).singleItem.value ? "On" : "Off") + ".txt"));
    }

    //endregion
}
