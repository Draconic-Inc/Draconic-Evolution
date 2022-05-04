package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.util.CapabilityUtil;
import net.minecraft.util.Direction;
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
		return tile.getExtendedStorage();
	}
	
	@LuaFunction
	public final long getMaxEnergyStored() {
		return tile.getExtendedCapacity();
	}
	
	@LuaFunction
	public final long getTransferPerTick() {
		if (!tile.hasCoreLock.get() || tile.getCore() == null) {
            return 0;
        }
        return tile.getCore().transferRate.get();
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
