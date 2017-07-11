package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEntityDetector;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class EntityDetector extends BlockBCore implements ITileEntityProvider, IRenderOverride {
    public static final PropertyBool ADVANCED = PropertyBool.create("advanced");
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public EntityDetector() {
        setDefaultState(blockState.getBaseState().withProperty(ADVANCED, false));
        this.canProvidePower = true;
        setIsFullCube(false);
        this.addName(0, "entity_detector_basic");
        this.addName(1, "entity_detector_advanced");
    }

    //region BlockState

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ADVANCED) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ADVANCED, meta == 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ADVANCED);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(ADVANCED) ? 1 : 0;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(ADVANCED, stack.getItemDamage() == 1));
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0626, 0, 0.0626, 0.9375, 0.125, 0.9375);
    }

    //endregion

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDetector();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetector.class, new RenderTileEntityDetector());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }
}
