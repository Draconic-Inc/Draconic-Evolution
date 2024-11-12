package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;

public class PeripheralFlowGate implements IPeripheral/*, ICapabilityProvider*/ {
	
	TileFlowGate tile;
//	private LazyOptional<IPeripheral> self;
	
	public PeripheralFlowGate(TileFlowGate tile) {
        this.tile = tile;
    }
	
	@Override
	public String getType() {
		return "flow_gate";
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other instanceof PeripheralFlowGate o && tile == o.tile;
	}
	
	@LuaFunction(mainThread = true)
	public final long getFlow() {
		return tile.getFlow();
	}
	
	@LuaFunction(mainThread = true)
	public final void setOverrideEnabled(boolean state) {
		tile.flowOverridden.set(state);
	}
	
	@LuaFunction(mainThread = true)
	public final boolean getOverrideEnabled() {
		return tile.flowOverridden.get();
	}
	
	@LuaFunction(mainThread = true)
	public final void setFlowOverride(long amount) {
		tile.flowOverride.set(amount);
	}
	
	@LuaFunction(mainThread = true)
	public final void setSignalHighFlow(long amount) {
		tile.maxFlow.set(amount);
	}
	
	@LuaFunction(mainThread = true)
	public final long getSignalHighFlow() {
		return tile.maxFlow.get();
	}
	
	@LuaFunction(mainThread = true)
	public final void setSignalLowFlow(long amount) {
		tile.minFlow.set(amount);
	}
	
	@LuaFunction(mainThread = true)
	public final long getSignalLowFlow() {
		return tile.minFlow.get();
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (CCOCIntegration.isPeripheral(cap)) {
//			if (self == null) self = LazyOptional.of(() -> this);
//			return self.cast();
//		}
//		return LazyOptional.empty();
//	}
//
//	public void invalidate() {
//		if (self == null) return;
//		self.invalidate();
//		self = null;
//    }
}
