package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.client.tile.RenderTileEnergyStorageCore;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 *  Created by brandon3055 on 30/3/2016.
 */
public class EnergyStorageCore extends BlockBCore implements ITileEntityProvider, ICustomRender {

    public static final PropertyInteger RENDER_TYPE = PropertyInteger.create("modelrender", 0, 2);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public EnergyStorageCore(){
        super(Material.iron);
        this.setDefaultState(blockState.getBaseState().withProperty(RENDER_TYPE, 0).withProperty(ACTIVE, false));
    }

    //region BlockState

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, RENDER_TYPE, ACTIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta == 1);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity core = worldIn.getTileEntity(pos);
        return state.withProperty(RENDER_TYPE, 0).withProperty(ACTIVE, core instanceof TileEnergyStorageCore && ((TileEnergyStorageCore)core).active.value);
    }

    //endregion


    //region Render Stuff

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return getActualState(state, worldIn, pos).getValue(ACTIVE) ? new AxisAlignedBB(0, 0, 0, 0, 0, 0) : super.getSelectedBoundingBox(state, worldIn, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return !state.getValue(ACTIVE);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    //endregion

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {


        return false;
    }


    //region Interfaces

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnergyStorageCore();
    }

    @Override
    public void registerRenderer(Feature feature) {
        OBJLoader.INSTANCE.addDomain(DraconicEvolution.MODID);
        Item item = Item.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ResourceHelperDE.RESOURCE_PREFIX + feature.name(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyStorageCore.class, new RenderTileEnergyStorageCore());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    //endregion
}
