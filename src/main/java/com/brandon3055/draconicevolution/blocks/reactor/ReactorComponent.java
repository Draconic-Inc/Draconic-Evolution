package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.util.RotationUtils;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.properties.PropertyString;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.config.ITileRegisterer;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.render.item.RenderItemReactorComponent;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorComponent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class ReactorComponent extends BlockBCore implements ITileEntityProvider, ITileRegisterer, ICustomRender {

    public static final PropertyString TYPE = new PropertyString("type", "stabilizer", "injector");
    private static final AxisAlignedBB AABB_INJ_DOWN = new AxisAlignedBB(0F, 0.885F, 0F, 1F, 1F, 1F);
    private static final AxisAlignedBB AABB_INJ_UP = new AxisAlignedBB(0F, 0F, 0F, 1F, 0.125F, 1F);
    private static final AxisAlignedBB AABB_INJ_NORTH = new AxisAlignedBB(0F, 0F, 0.885F, 1F, 1F, 1F);
    private static final AxisAlignedBB AABB_INJ_SOUTH = new AxisAlignedBB(0F, 0F, 0F, 1F, 1F, 0.125F);
    private static final AxisAlignedBB AABB_INJ_WEST = new AxisAlignedBB(0.885F, 0F, 0F, 1F, 1F, 1F);
    private static final AxisAlignedBB AABB_INJ_EAST = new AxisAlignedBB(0F, 0F, 0F, 0.125F, 1F, 1F);

    public ReactorComponent() {
        this.setHardness(25F);
        this.setDefaultState(blockState.getBaseState().withProperty(TYPE, "stabilizer"));
        this.addName(0, "reactor_stabilizer");
        this.addName(1, "reactor_injector");
    }

    //region Block & Registry

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).equals("stabilizer") ? 0 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? "stabilizer" : "injector");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta == 0 ? new TileReactorStabilizer() : new TileReactorEnergyInjector();
    }

    @Override
    public void registerTiles(String modidPrefix, String blockName) {
        GameRegistry.registerTileEntity(TileReactorStabilizer.class, blockName + "_stabilizer");
        GameRegistry.registerTileEntity(TileReactorEnergyInjector.class, blockName + "_injector");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);

        if (tile instanceof TileReactorEnergyInjector) {
            switch (((TileReactorEnergyInjector) tile).facing.value) {
                case DOWN: return AABB_INJ_DOWN;
                case UP: return AABB_INJ_UP;
                case NORTH: return AABB_INJ_NORTH;
                case SOUTH: return AABB_INJ_SOUTH;
                case WEST: return AABB_INJ_WEST;
                case EAST: return AABB_INJ_EAST;
            }
        }

        return super.getBoundingBox(state, source, pos);
    }

    //endregion

    //region Rendering

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        StateMap deviceStateMap = new StateMap.Builder().ignore(TYPE).build();
        ModelLoader.setCustomStateMapper(this, deviceStateMap);
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorStabilizer.class, new RenderTileReactorComponent());
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorEnergyInjector.class, new RenderTileReactorComponent());
        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemReactorComponent());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    //endregion

    //region Place & Interact

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        EnumFacing facing = RotationUtils.getPlacedRotation(pos, placer);
        if (placer.isSneaking()) {
            facing = facing.getOpposite();
        }

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).facing.value = facing;
            ((TileReactorComponent) te).onPlaced();
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onActivated(playerIn);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileReactorComponent) {
            ((TileReactorComponent) te).onBroken();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("info.de.shiftReversePlaceLogic.txt"));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    //endregion


    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileReactorComponent) {
            return ((TileReactorComponent) tileEntity).rsPower.value;
        }

        return 0;
    }
}
