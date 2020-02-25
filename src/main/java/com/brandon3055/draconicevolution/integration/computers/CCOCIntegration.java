package com.brandon3055.draconicevolution.integration.computers;

import net.minecraftforge.fml.ModList;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

    public static void init() {
        if (ModList.get().isLoaded("computercraft")) {
            initCC();
        }
        if (ModList.get().isLoaded("opencomputers")) {
            initOC();
        }
    }

//    @Optional.Method(modid = "computercraft")
    public static void initCC() {
//        ComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
    }

//    @Optional.Method(modid = "opencomputers")
    public static void initOC() {
//        Driver.add(new OCAdapter());
//        Driver.add(new OCExtendedRFAdapter());
    }

//    public static class OCAdapter extends DriverSidedTileEntity {
//
//        @Override
//        public Class<?> getTileEntityClass() {
//            return IDEPeripheral.class;
//        }
//
//        @Override
//        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side) {
//            return new DEManagedPeripheral((IDEPeripheral) world.getTileEntity(pos));
//        }
//    }
//
//    public static class OCExtendedRFAdapter extends DriverSidedTileEntity {
//
//        @Override
//        public Class<?> getTileEntityClass() {
//            return IExtendedRFStorage.class;
//        }
//
//        @Override
//        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side) {
//            return new IExtendedRFStoragePeripheral((IExtendedRFStorage) world.getTileEntity(pos));
//        }
//    }
//
//    //Computercraft
//	public static class DEPeripheralProvider implements IPeripheralProvider {
//        /**
//         * Produce an peripheral implementation from a block location.
//         *
//         * @param world The world the block is in.
//         * @param pos   The position the block is at.
//         * @param side  The side to get the peripheral from.
//         * @return A peripheral, or {@code null} if there is not a peripheral here you'd like to handle.
//         * @see ComputerCraftAPI#registerPeripheralProvider(IPeripheralProvider)
//         */
//        @Nullable
//        @Override
//        public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction side) {
//            TileEntity tile = world.getTileEntity(pos);
//            if (tile instanceof IDEPeripheral) {
//                return new CCAdapter((IDEPeripheral)tile);
//            }
//            else return null;
//        }
//	}

}
