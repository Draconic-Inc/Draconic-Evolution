package com.brandon3055.draconicevolution.blocks.energynet;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.config.IRegisterMyOwnTiles;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.render.item.RenderItemEnergyCrystal;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyCrystal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
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
 * Created by brandon3055 on 19/11/2016.
 */
public class EnergyCrystal extends BlockBCore implements ICustomRender, IRegisterMyOwnTiles, IHudDisplay {

    public static final PropertyEnum<CrystalType> TYPE = PropertyEnum.create("type", CrystalType.class);
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 2);

    public EnergyCrystal() {
        super(Material.GLASS);
        this.setDefaultState(blockState.getBaseState().withProperty(TYPE, CrystalType.RELAY).withProperty(TIER, 0));
        this.setIsFullCube(false);
        this.setHarvestLevel("pickaxe", 0);
        for (CrystalType type : CrystalType.values()) {
            for (int i = 0; i < 3; i++) {
                addName((type.getIndex() * 3) + i, "energy_crystal." + type.name().toLowerCase() + "." + (i == 0 ? "basic" : i == 1 ? "wyvern" : "draconic"));
            }
        }
    }

    //region Block

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 9; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    //endregion

    //region Blockstate

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, TIER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, CrystalType.fromMeta(meta)).withProperty(TIER, CrystalType.getTier(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta(state.getValue(TIER));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {


        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);//getDefaultState().withProperty(TYPE, Type.fromMeta(meta));
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileCrystalIO) {
//            LogHelper.info("2 " + state);
////            ((TileCrystalIO) tile).facing.value = ((IExtendedBlockState) state).getValue(FACING);
//            //            RayTraceResult result = RayTracer.retrace((EntityPlayer) placer);
////            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
////                ((TileCrystalIO) tile).facing.value = result.sideHit.getOpposite();
////            }
//        }

        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    //endregion

    //region Render/Tile

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalRelay.class, new RenderTileEnergyCrystal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalDirectIO.class, new RenderTileEnergyCrystal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalWirelessIO.class, new RenderTileEnergyCrystal());
        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemEnergyCrystal());
        StateMap deviceStateMap = new StateMap.Builder().ignore(TIER).ignore(TYPE).build();
        ModelLoader.setCustomStateMapper(this, deviceStateMap);
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TYPE).createTile();
    }

    @Override
    public void registerTiles(String modidPrefix, String blockName) {
        GameRegistry.registerTileEntity(TileCrystalRelay.class, modidPrefix + "energy_relay");
        GameRegistry.registerTileEntity(TileCrystalDirectIO.class, modidPrefix + "energy_io");
        GameRegistry.registerTileEntity(TileCrystalWirelessIO.class, modidPrefix + "energy_wireless");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(TYPE) == CrystalType.CRYSTAL_IO) {
            TileEntity tile = source.getTileEntity(pos);
            EnumFacing facing = tile instanceof TileCrystalDirectIO ? ((TileCrystalDirectIO) tile).facing.value : EnumFacing.DOWN;
            Cuboid6 c = new Cuboid6(0.35, 0, 0.35, 0.65, 0.425, 0.65);
            c.apply(Rotation.sideRotations[facing.getIndex()].at(Vector3.center));
            return c.aabb();
        }
        return new AxisAlignedBB(0.375, 0.125, 0.375, 0.625, 0.875, 0.625); //Crystal
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return super.getSelectedBoundingBox(state, worldIn, pos);
//        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    //endregion

    //region Info

    @SideOnly(Side.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileCrystalBase)) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        displayList.add(InfoHelper.HITC() + I18n.format("tile.draconicevolution:" + nameOverrides.get(state.getValue(TYPE).getMeta(state.getValue(TIER))) + ".name"));
        TileCrystalBase tile = (TileCrystalBase) te;
        tile.addDisplayData(displayList);
    }

    //endregion

    public enum CrystalType implements IStringSerializable {
        RELAY(0) {
            @Override
            public TileEntity createTile() {
                return new TileCrystalRelay();
            }
        },
        CRYSTAL_IO(1) {
            @Override
            public TileEntity createTile() {
                return new TileCrystalDirectIO();
            }
        },
        WIRELESS(2) {
            @Override
            public TileEntity createTile() {
                return new TileCrystalWirelessIO();
            }
        };

        private final int index;

        CrystalType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        private static CrystalType fromIndex(int i) {
            return i == 0 ? RELAY : i == 1 ? CRYSTAL_IO : WIRELESS;
        }

        public static CrystalType fromMeta(int meta) {
            return fromIndex(meta / 3);
        }

        public int getMeta(int tier) {
            return (getIndex() * 3) + tier;
        }

        public static int getTier(int meta) {
            return meta % 3;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public abstract TileEntity createTile();
    }
}
