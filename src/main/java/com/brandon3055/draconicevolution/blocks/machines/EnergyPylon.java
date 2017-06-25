package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyPylon;
import com.brandon3055.draconicevolution.lib.PropertyStringTemp;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyPylon extends BlockBCore implements ITileEntityProvider, IRenderOverride {

    public static final PropertyBool OUTPUT = PropertyBool.create("output");
    public static final PropertyStringTemp FACING = new PropertyStringTemp("facing", "up", "down", "null");

    public EnergyPylon() {
        super(Material.IRON);
        this.setDefaultState(blockState.getBaseState().withProperty(OUTPUT, false).withProperty(FACING, "null"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnergyPylon();
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OUTPUT, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).equals("up") ? 1 : state.getValue(FACING).equals("down") ? 2 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, meta == 1 ? "up" : meta == 2 ? "down" : "null");
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return state.withProperty(OUTPUT, tile instanceof TileEnergyPylon && ((TileEnergyPylon) tile).isOutputMode.value);
    }
    //endregion

    //region Block Stuff

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEnergyPylon) {
            if (playerIn.isSneaking()) {
                ((TileEnergyPylon) tile).selectNextCore();
            }
            else {
                ((TileEnergyPylon) tile).validateStructure();
            }
            return ((TileEnergyPylon) tile).structureValid.value;
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyPylon) {
            ((TileEnergyPylon) tile).validateStructure();
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    }

//    @Override
//    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
//        TileEntity tile = worldIn.getTileEntity(pos);
//
//        if (tile instanceof TileEnergyPylon){
//            ((TileEnergyPylon)tile).validateStructure();
//        }
//    }


    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEnergyPylon && ((TileEnergyPylon) tile).getExtendedCapacity() > 0) {
            return (int) ((double) ((TileEnergyPylon) tile).getExtendedStorage() / ((TileEnergyPylon) tile).getExtendedCapacity() * 15D);
        }
        return 0;
    }

    //endregion

    //region Registry

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPylon.class, new RenderTileEnergyPylon());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    //endregion
}
