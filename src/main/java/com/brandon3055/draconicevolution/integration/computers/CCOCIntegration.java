package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.integration.computers.cc.DEPeripheralProvider;
import com.brandon3055.draconicevolution.integration.computers.oc.DEManagedPeripheral;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Driver;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCOCIntegration {

	public static void init() {
		if (Loader.isModLoaded("ComputerCraft")) initCC();
		if (Loader.isModLoaded("OpenComputers")) initOC();
	}

	@Optional.Method(modid = "ComputerCraft")
	public static void initCC() {
		ComputerCraftAPI.registerPeripheralProvider(new DEPeripheralProvider());
	}

	@Optional.Method(modid = "OpenComputers")
	public static void initOC() {
		Driver.add(new OCAdapter());
	}

	public static class OCAdapter extends DriverTileEntity {

		@Override
		public Class<?> getTileEntityClass() {
			return IDEPeripheral.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof IDEPeripheral) return new DEManagedPeripheral((IDEPeripheral)tile);
			else return null;
		}
	}

	public static class CCAdapter implements IPeripheral {
		private IDEPeripheral peripheral;

		public CCAdapter(IDEPeripheral peripheral){
			this.peripheral = peripheral;
		}

		@Override
		public String getType() {
			return peripheral.getName();
		}

		@Override
		public String[] getMethodNames() {
			return peripheral.getMethodNames();
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			return peripheral.callMethod(peripheral.getMethodNames()[method], arguments);
		}

		@Override
		public void attach(IComputerAccess iComputerAccess) {}

		@Override
		public void detach(IComputerAccess iComputerAccess) {}

		@Override
		public boolean equals(IPeripheral iPeripheral) {
			return false;
		}
	}

}
