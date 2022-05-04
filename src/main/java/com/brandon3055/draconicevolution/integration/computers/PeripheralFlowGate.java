package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.util.CapabilityUtil;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralFlowGate implements IPeripheral, ICapabilityProvider {
	
	TileFlowGate tile;
	private LazyOptional<IPeripheral> self;
	
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
