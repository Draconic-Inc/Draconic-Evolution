package com.brandon3055.draconicevolution.blocks.energynet;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 19/11/2016.
 */
public class EnergyCrystal extends BlockBCore implements /*IRenderOverride, IRegistryOverride,*/ IHudDisplay {

    public static final EnumProperty<CrystalType> TYPE = EnumProperty.create("type", CrystalType.class);
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 2);

    public EnergyCrystal(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(TYPE, CrystalType.RELAY).with(TIER, 0));
//        this.setHarvestLevel("pickaxe", 0);
        for (CrystalType type : CrystalType.values()) {
            for (int i = 0; i < 3; i++) {
                addName((type.getIndex() * 3) + i, "energy_crystal." + type.name().toLowerCase() + "." + (i == 0 ? "basic" : i == 1 ? "wyvern" : "draconic"));
            }
        }
    }

    //region Block

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }

//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        for (int i = 0; i < 9; i++) {
//            list.add(new ItemStack(this, 1, i));
//        }
//    }

//    @Override
//    public int damageDropped(BlockState state) {
//        return getMetaFromState(state);
//    }
//
//    //endregion
//
//    //region Blockstate
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, TYPE, TIER);
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(TYPE, CrystalType.fromMeta(meta)).withProperty(TIER, CrystalType.getTier(meta));
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(TYPE).getMeta(state.getValue(TIER));
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return super.getActualState(state, worldIn, pos);
//    }
//
//    //endregion
//
//    //region Render/Tile
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalRelay.class, new RenderTileEnergyCrystal());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalDirectIO.class, new RenderTileEnergyCrystal());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalWirelessIO.class, new RenderTileEnergyCrystal());
//        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemEnergyCrystal());
//        ModelRegistryHelper.register(new ModelResourceLocation(feature.getRegistryName(), "normal"), GlassParticleDummyModel.INSTANCE);
//        StateMap deviceStateMap = new StateMap.Builder().ignore(TIER).ignore(TYPE).build();
//        ModelLoader.setCustomStateMapper(this, deviceStateMap);
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return state.get(TYPE).createTile();
    }

//    @Override
//    public void handleCustomRegistration(Feature feature) {
//        GameRegistry.registerTileEntity(TileCrystalRelay.class, feature.getModid() + ":energy_relay");
//        GameRegistry.registerTileEntity(TileCrystalDirectIO.class, feature.getModid() + ":energy_io");
//        GameRegistry.registerTileEntity(TileCrystalWirelessIO.class, feature.getModid() + ":energy_wireless");
//    }

//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        if (state.getValue(TYPE) == CrystalType.CRYSTAL_IO) {
//            TileEntity tile = source.getTileEntity(pos);
//            Direction facing = tile instanceof TileCrystalDirectIO ? ((TileCrystalDirectIO) tile).facing.get() : Direction.DOWN;
//            Cuboid6 c = new Cuboid6(0.35, 0, 0.35, 0.65, 0.425, 0.65);
//            c.apply(Rotation.sideRotations[facing.getIndex()].at(Vector3.center));
//            return c.aabb();
//        }
//        return new AxisAlignedBB(0.375, 0.125, 0.375, 0.625, 0.875, 0.625); //Crystal
//    }

//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
//        return super.getSelectedBoundingBox(state, worldIn, pos);
////        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
//    }

    //endregion

    //region Info

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileCrystalBase)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        displayList.add(InfoHelper.HITC() + I18n.format("tile.draconicevolution:" + nameOverrides.get(state.get(TYPE).getMeta(state.get(TIER))) + ".name"));
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
        }, CRYSTAL_IO(1) {
            @Override
            public TileEntity createTile() {
                return new TileCrystalDirectIO();
            }
        }, WIRELESS(2) {
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
