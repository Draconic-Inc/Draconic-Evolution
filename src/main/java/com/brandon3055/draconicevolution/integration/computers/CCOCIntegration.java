package com.brandon3055.draconicevolution.integration.computers;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.neoforged.fml.ModList;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

    public static void init() {
//        if (ModList.get().isLoaded("opencomputers")) {
//            initOC();
//        }
    }

//    @Optional.Method(modid = "opencomputers")
    public static void initOC() {
//        Driver.add(new OCAdapter());
//        Driver.add(new OCExtendedRFAdapter());
    }

//    public static boolean isPeripheral(Capability<?> capability) {
//        return CAPABILITY_PERIPHERAL != null && capability == CAPABILITY_PERIPHERAL;
//    }

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
}
