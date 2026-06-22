package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PeripheralCelestialManipulator implements IPeripheral {
	
	TileCelestialManipulator tile;
//	private LazyOptional<IPeripheral> self;
	
	public PeripheralCelestialManipulator(TileCelestialManipulator tile) {
        this.tile = tile;
    }
	
	@Override
	public String getType() {
		return "celestial_manipulator";
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other instanceof PeripheralCelestialManipulator o && tile == o.tile;
	}
	
    @LuaFunction(mainThread = true)
    public final boolean isActive() {
        return tile.active.get();
    }

    @LuaFunction(mainThread = true)
    public final long getEnergyStored() {
        return tile.opStorage.getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return tile.getLevel().isRaining();
    }

    @LuaFunction(mainThread = true)
    public final boolean isThundering() {
        return tile.getLevel().isThundering();
    }

    @LuaFunction(mainThread = true)
    public final void sunrise() {
        tile.handleInteract("SUN_RISE", null);
    }

    @LuaFunction(mainThread = true)
    public final void midday() {
        tile.handleInteract("MID_DAY", null);
    }

    @LuaFunction(mainThread = true)
    public final void sunset() {
        tile.handleInteract("SUN_SET", null);
    }

    @LuaFunction(mainThread = true)
    public final void midnight() {
        tile.handleInteract("MIDNIGHT", null);
    }

    @LuaFunction(mainThread = true)
    public final void moonrise() {
        tile.handleInteract("MOON_RISE", null);
    }

    @LuaFunction(mainThread = true)
    public final void moonset() {
        tile.handleInteract("MOON_SET", null);
    }

    @LuaFunction(mainThread = true)
    public final void skipDay() {
        tile.handleInteract("SKIP_24", null);
    }

    @LuaFunction(mainThread = true)
    public final void startRain() {
        tile.handleInteract("START_RAIN", null);
    }

    @LuaFunction(mainThread = true)
    public final void stopRain() {
        tile.handleInteract("STOP_RAIN", null);
    }

    @LuaFunction(mainThread = true)
    public final void startStorm() {
        tile.handleInteract("START_STORM", null);
    }

    @LuaFunction(mainThread = true)
    public final void stop() {
        tile.handleInteract("STOP", null);
    }
}
