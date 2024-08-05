package com.brandon3055.draconicevolution.integration.computers.oc2;

import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import li.cil.oc2r.api.bus.device.object.Callback;
import li.cil.oc2r.api.bus.device.object.NamedDevice;
import li.cil.oc2r.api.bus.device.object.ObjectDevice;
import li.cil.oc2r.api.bus.device.rpc.RPCDevice;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.ENERGY_PYLON;
import static java.util.Collections.singletonList;

public class EnergyPylonOC2Device {

    public static RPCDevice create(TileEnergyPylon blockEntity) {
        return new ObjectDevice(new TileEnergyPylonRecord(blockEntity));
    }

    public record TileEnergyPylonRecord(TileEnergyPylon tile) implements NamedDevice {

        @Override
        public @NotNull Collection<String> getDeviceTypeNames() {
            return singletonList(ENERGY_PYLON);
        }

        @Callback
        public long getEnergyStored() {
            return tile.opAdapter.getOPStored();
        }

        @Callback
        public long getMaxEnergyStored() {
            return tile.opAdapter.getMaxOPStored();
        }

        @Callback
        public Map<Object, Object> getEnergyStoredInNotation() {
            Map<Object, Object> map = new HashMap<Object, Object>();
            String[] split = tile.getCore().energy.getScientific().split("E", 2);
            map.put("coefficient", Double.parseDouble(split[0]));
            map.put("exponent", Integer.parseInt(split[1]));
            return map;
        }

        @Callback
        public Map<Object, Object> getMaxEnergyStoredInNotation() {
            Map<Object, Object> map = new HashMap<Object, Object>();
            long maxEnergy = tile.opAdapter.getMaxOPStored();
            String[] split = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(maxEnergy).split("E", 2);
            map.put("coefficient", Double.parseDouble(maxEnergy == Long.MAX_VALUE ? "-1" : split[0]));
            map.put("exponent", Integer.parseInt(maxEnergy == Long.MAX_VALUE ? "-1" : split[1]));
            return map;
        }

        @Callback
        public long getTransferPerTick() {
            if (tile.coreOffset.isNull() || tile.getCore() == null) {
                return 0;
            }
            IOInfo io = tile.getCore().energy.getIOInfo();
            return io == null ? 0 : io.currentInput() - io.currentOutput();
        }

        @Callback
        public long getInputPerTick() {
            if (tile.coreOffset.isNull() || tile.getCore() == null) {
                return 0;
            }
            IOInfo io = tile.getCore().energy.getIOInfo();
            return io == null ? 0 : io.currentInput();
        }

        @Callback
        public long getOutputPerTick() {
            if (tile.coreOffset.isNull() || tile.getCore() == null) {
                return 0;
            }
            IOInfo io = tile.getCore().energy.getIOInfo();
            return io == null ? 0 : io.currentOutput();
        }
    }
}
