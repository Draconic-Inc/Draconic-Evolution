package com.brandon3055.draconicevolution.integration.computers.cc;

import com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.CCAdapter;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class DEPeripheralProvider implements IPeripheralProvider{

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int i3) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IDEPeripheral) {
			return new CCAdapter((IDEPeripheral)tile);
		}
		else return null;
	}
}
