package com.brandon3055.draconicevolution.integration.computers;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        ComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
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
    //Computercraft
	public static class DEPeripheralProvider implements IPeripheralProvider {
		private IPeripheral peripheral;
		private LazyOptional<IPeripheral> holderPeripheral;
		
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
        public LazyOptional<IPeripheral> getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction side) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof IPeripheral) {
            	setPeripheral((IPeripheral)tile);
                return holderPeripheral;
            }
            else return null;
        }
        
        protected void setPeripheral(IPeripheral peripheral) {
            this.peripheral = peripheral;
            this.holderPeripheral = LazyOptional.of(() -> peripheral);
        }
	}
}
