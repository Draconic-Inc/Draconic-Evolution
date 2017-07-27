package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.integration.computers.cc.CCAdapter;
import com.brandon3055.draconicevolution.integration.computers.oc.DEManagedPeripheral;
import com.brandon3055.draconicevolution.integration.computers.oc.IExtendedRFStoragePeripheral;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import li.cil.oc.api.Driver;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

    public static void init() {
        if (Loader.isModLoaded("computercraft")) {
            initCC();
        }
        if (Loader.isModLoaded("opencomputers")) {
            initOC();
        }
    }

    @Optional.Method(modid = "computercraft")
    public static void initCC() {
        ComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
    }

    @Optional.Method(modid = "opencomputers")
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
	public static class DEPeripheralProvider implements IPeripheralProvider {
        /**
         * Produce an peripheral implementation from a block location.
         *
         * @param world The world the block is in.
         * @param pos   The position the block is at.
         * @param side  The side to get the peripheral from.
         * @return A peripheral, or {@code null} if there is not a peripheral here you'd like to handle.
         * @see ComputerCraftAPI#registerPeripheralProvider(IPeripheralProvider)
         */
        @Nullable
        @Override
        public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof IDEPeripheral) {
                return new CCAdapter((IDEPeripheral)tile);
            }
            else return null;
        }
	}

}
