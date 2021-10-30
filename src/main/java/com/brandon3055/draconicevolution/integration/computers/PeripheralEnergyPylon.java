package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PeripheralEnergyPylon implements IPeripheral {
	
	TileEnergyPylon tile;
	public PeripheralEnergyPylon(TileEnergyPylon tile) {
        this.tile = tile;
    }
	
	@Override
	public String getType() {
		return "draconic_rf_storage";
	}

	@Override
	public boolean equals(IPeripheral other) {
		return false;
	}
	
	@LuaFunction
	public final long getEnergyStored() {
		return tile.getExtendedStorage();
	}
	
	@LuaFunction
	public final long getMaxEnergyStored() {
		return tile.getExtendedCapacity();
	}
	
	@LuaFunction
	public final long getTransferPerTick(boolean state) {
		if (!tile.hasCoreLock.get() || tile.getCore() == null) {
            return 0;
        }
        return tile.getCore().transferRate.get();
	}
}
