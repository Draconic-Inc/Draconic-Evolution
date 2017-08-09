package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.util.RotationUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRegistryOverride;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.brandon3055.draconicevolution.lib.PropertyStringTemp;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by brandon3055 on 14/11/2016.
 */
public class FlowGate extends BlockBCore implements IRegistryOverride {

    public static final PropertyStringTemp TYPE = new PropertyStringTemp("type", "flux", "fluid");
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    public FlowGate() {
        this.setDefaultState(blockState.getBaseState().withProperty(TYPE, "flux").withProperty(FACING, EnumFacing.NORTH));
        this.addName(0, "flux_gate");
        this.addName(8, "fluid_gate");
    }

    //region BlockState

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing;

        switch (meta & 7) {
            case 0:
                enumfacing = EnumFacing.DOWN;
                break;
            case 1:
                enumfacing = EnumFacing.EAST;
                break;
            case 2:
                enumfacing = EnumFacing.WEST;
                break;
            case 3:
                enumfacing = EnumFacing.SOUTH;
                break;
            case 4:
                enumfacing = EnumFacing.NORTH;
                break;
            case 5:
            default:
                enumfacing = EnumFacing.UP;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(TYPE, (meta & 8) > 0 ? "fluid" : "flux");
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i;

        switch (state.getValue(FACING)) {
            case EAST:
                i = 1;
                break;
            case WEST:
                i = 2;
                break;
            case SOUTH:
                i = 3;
                break;
            case NORTH:
                i = 4;
                break;
            case UP:
            default:
                i = 5;
                break;
            case DOWN:
                i = 0;
        }

        if (state.getValue(TYPE).equals("fluid")) {
            i |= 8;
        }

        return i;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 8));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = RotationUtils.getPlacedRotation(pos, placer);
        if (placer.isSneaking()) {
            facing = facing.getOpposite();
        }
        world.setBlockState(pos, state.withProperty(FACING, facing).withProperty(TYPE, (stack.getItemDamage() & 8) > 0 ? "fluid" : "flux"), 2);
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }


    //endregion

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TYPE).equals("flux") ? new TileFluxGate() : new TileFluidGate();
    }

    @Override
    public void handleCustomRegistration(Feature feature) {
        GameRegistry.registerTileEntity(TileFluxGate.class, feature.getModid() + ":flux_gate");
        GameRegistry.registerTileEntity(TileFluidGate.class, feature.getModid() + ":fluid_gate");
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, DraconicEvolution.instance, GuiHandler.GUIID_FLOW_GATE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        stack.setItemDamage(state.getValue(TYPE).equals("flux") ? 0 : 8);
        return stack;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).equals("fluid") ? 8 : 0;
    }
}
