package com.brandon3055.draconicevolution.integration.computers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.util.CapabilityUtil;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralEnergyPylon implements IPeripheral, ICapabilityProvider {
	
	TileEnergyPylon tile;
	private LazyOptional<IPeripheral> self;
	
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
		return tile.opAdapter.getOPStored();
	}
	
	@LuaFunction
	public final long getMaxEnergyStored() {
		return tile.opAdapter.getMaxOPStored();
	}
	
	@LuaFunction
	public final Map<Object, Object> getEnergyStoredInNotation() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		String[] split = tile.getCore().energy.getScientific().split("E", 2);
		map.put("coefficient", Double.parseDouble(split[0]));
		map.put("exponent", Integer.parseInt(split[1]));
		return map;
	}
	
	@LuaFunction
	public final Map<Object, Object> getMaxEnergyStoredInNotation() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		long maxEnergy = tile.opAdapter.getMaxOPStored();
		String[] split = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(maxEnergy).split("E", 2);
		map.put("coefficient", Double.parseDouble(maxEnergy == Long.MAX_VALUE ? "-1" : split[0]));
		map.put("exponent", Integer.parseInt(maxEnergy == Long.MAX_VALUE ? "-1" : split[1]));
		return map;
	}
	
	@LuaFunction
	public final long getTransferPerTick() {
		if (tile.coreOffset.isNull() || tile.getCore() == null) {
            return 0;
        }
        IOInfo io = tile.getCore().energy.getIOInfo();//transferRate.get();
		return io == null ? 0 : io.currentInput() - io.currentOutput();
	}

	@LuaFunction
	public final long getInputPerTick() {
		if (tile.coreOffset.isNull() || tile.getCore() == null) {
			return 0;
		}
		IOInfo io = tile.getCore().energy.getIOInfo();
		return io == null ? 0 : io.currentInput();
	}

	@LuaFunction
	public final long getOutputPerTick() {
		if (tile.coreOffset.isNull() || tile.getCore() == null) {
			return 0;
		}
		IOInfo io = tile.getCore().energy.getIOInfo();
		return io == null ? 0 : io.currentOutput();
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.CAPABILITY_PERIPHERAL) {
            if (self == null) self = LazyOptional.of(() -> this);
            return self.cast();
        }
        return LazyOptional.empty();
	}
	
	public void invalidate() {
        self = CapabilityUtil.invalidate(self);
    }
}
