package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PeripheralFlowGate implements IPeripheral {
	
	TileFlowGate tile;
	public PeripheralFlowGate(TileFlowGate tile) {
        this.tile = tile;
    }
	
	@Override
	public String getType() {
		return "flow_gate";
	}

	@Override
	public boolean equals(IPeripheral other) {
		return false;
	}
	
	@LuaFunction
	public final long getFlow() {
		return tile.getFlow();
	}
	
	@LuaFunction
	public final void setOverrideEnabled(boolean state) {
		tile.flowOverridden.set(state);
	}
	
	@LuaFunction
	public final boolean getOverrideEnabled() {
		return tile.flowOverridden.get();
	}
	
	@LuaFunction
	public final void setFlowOverride(long amount) {
		tile.flowOverride.set(amount);
	}
	
	@LuaFunction
	public final void setSignalHighFlow(long amount) {
		tile.maxFlow.set(amount);
	}
	
	@LuaFunction
	public final long getSignalHighFlow() {
		return tile.maxFlow.get();
	}
	
	@LuaFunction
	public final void setSignalLowFlow(long amount) {
		tile.minFlow.set(amount);
	}
	
	@LuaFunction
	public final long getSignalLowFlow() {
		return tile.minFlow.get();
	}
}
