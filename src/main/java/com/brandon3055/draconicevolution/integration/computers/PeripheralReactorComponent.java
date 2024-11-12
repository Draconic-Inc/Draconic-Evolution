package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PeripheralReactorComponent implements IPeripheral {
	
	TileReactorComponent tile;
	TileReactorCore reactor;
//	private LazyOptional<IPeripheral> self;
	
	public PeripheralReactorComponent(TileReactorComponent tile) {
        this.tile = tile;
    }
	
	@Override
	public String getType() {
		return "draconic_reactor";
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other instanceof PeripheralReactorComponent o && tile == o.tile;
	}
	
	private boolean refreshCoreStatus() {
		reactor = tile.getCachedCore();
        if (reactor == null) {
            return false;
        }
        return true;
	}

	@LuaFunction(mainThread = true)
	public final Map<Object, Object> getReactorInfo() {
		if (refreshCoreStatus()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
	        map.put("temperature", MathUtils.round(reactor.temperature.get(), 100));
	        map.put("fieldStrength", MathUtils.round(reactor.shieldCharge.get(), 100));
	        map.put("maxFieldStrength", MathUtils.round(reactor.maxShieldCharge.get(), 100));
	        map.put("energySaturation", reactor.saturation.get());
	        map.put("maxEnergySaturation", reactor.maxSaturation.get());
	        map.put("fuelConversion", MathUtils.round(reactor.convertedFuel.get(), 1000));
	        map.put("maxFuelConversion", reactor.reactableFuel.get() + reactor.convertedFuel.get());
	        map.put("generationRate", (int)reactor.generationRate.get());
	        map.put("fieldDrainRate", reactor.fieldDrain.get());
	        map.put("fuelConversionRate", (int) Math.round(reactor.fuelUseRate.get() * 1000000D));
	        map.put("status", reactor.reactorState.get().name().toLowerCase(Locale.ENGLISH));//reactor.reactorState.value == TileReactorCore.ReactorState.COLD ? "offline" : reactor.reactorState == 1 && !reactor.canStart() ? "charging" : reactor.reactorState == 1 && reactor.canStart() ? "charged" : reactor.reactorState == 2 ? "online" : reactor.reactorState == 3 ? "stopping" : "invalid");
	        map.put("failSafe", reactor.failSafeMode.get());
	        return map;
		}
		return null;
	}
	
	@LuaFunction(mainThread = true)
	public final boolean chargeReactor() {
		if (refreshCoreStatus()) {
			reactor.chargeReactor();
            return true;
        }
        return false;
	}
	
	@LuaFunction(mainThread = true)
	public final boolean activateReactor() {
		if (refreshCoreStatus()) {
			reactor.activateReactor();
            return true;
        }
        return false;
	}
	
	@LuaFunction(mainThread = true)
	public final boolean stopReactor() {
		if (refreshCoreStatus()) {
			reactor.shutdownReactor();
            return true;
        }
        return false;
	}
	
	@LuaFunction(mainThread = true)
	public final boolean toggleFailSafe() {
		if (refreshCoreStatus()) {
			reactor.toggleFailSafe();
			return true;
		}
		return false;
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
