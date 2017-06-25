package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.integration.computers.oc.DEManagedPeripheral;
import com.brandon3055.draconicevolution.integration.computers.oc.IExtendedRFStoragePeripheral;
import li.cil.oc.api.Driver;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

    public static void init() {
        if (Loader.isModLoaded("ComputerCraft")) {
            initCC();
        }
        if (Loader.isModLoaded("OpenComputers")) {
            initOC();
        }
    }

    @Optional.Method(modid = "ComputerCraft")
    public static void initCC() {
        //ComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
    }

    @Optional.Method(modid = "OpenComputers")
    public static void initOC() {
        Driver.add(new OCAdapter());
        Driver.add(new OCExtendedRFAdapter());
    }

    public static class OCAdapter extends DriverSidedTileEntity {

        @Override
        public Class<?> getTileEntityClass() {
            return IDEPeripheral.class;
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
            return new DEManagedPeripheral((IDEPeripheral) world.getTileEntity(pos));
        }
    }

    public static class OCExtendedRFAdapter extends DriverSidedTileEntity {

        @Override
        public Class<?> getTileEntityClass() {
            return IExtendedRFStorage.class;
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
            return new IExtendedRFStoragePeripheral((IExtendedRFStorage) world.getTileEntity(pos));
        }
    }

    //Computercraft
//	public static class DEPeripheralProvider implements IPeripheralProvider {
//
//		@Override
//		public IPeripheral getPeripheral(World world, int x, int y, int z, int i3) {
//			TileEntity tile = world.tileEntity(x, y, z);
//			if (tile instanceof IDEPeripheral) {
//				return new CCAdapter((IDEPeripheral)tile);
//			}
//			else return null;
//		}
//	}

}
