package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

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
	public final long getEnergyStored() {
		return tile.opStorage.getOPStored();
	}

    @LuaFunction(mainThread = true)
	public final long getMaxEnergyStored() {
		return tile.opStorage.getMaxOPStored();
	}

    @LuaFunction(mainThread = true)
    public final boolean isActive() {
        return tile.active.get();
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
    public final String sunrise() {
        Component message = tile.handleInteract("SUN_RISE", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String midday() {
        Component message = tile.handleInteract("MID_DAY", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String sunset() {
        Component message = tile.handleInteract("SUN_SET", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String midnight() {
        Component message = tile.handleInteract("MIDNIGHT", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String moonrise() {
        Component message = tile.handleInteract("MOON_RISE", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String moonset() {
        Component message = tile.handleInteract("MOON_SET", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String skipDay() {
        Component message = tile.handleInteract("SKIP_24", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String startRain() {
        Component message = tile.handleInteract("START_RAIN", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String stopRain() {
        Component message = tile.handleInteract("STOP_RAIN", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String startStorm() {
        Component message = tile.handleInteract("START_STORM", null);
        return message == null ? "" : message.getString();
    }

    @LuaFunction(mainThread = true)
    public final String stop() {
        Component message = tile.handleInteract("STOP", null);
        return message == null ? "" : message.getString();
    }
}
