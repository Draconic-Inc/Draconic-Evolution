package com.brandon3055.draconicevolution.integration.computers.oc2;

import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import li.cil.oc2r.api.bus.device.object.Callback;
import li.cil.oc2r.api.bus.device.object.NamedDevice;
import li.cil.oc2r.api.bus.device.object.ObjectDevice;
import li.cil.oc2r.api.bus.device.rpc.RPCDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.DRACONIC_REACTOR;
import static java.util.Collections.singletonList;

public class ReactorOC2Device {
    public static RPCDevice create(TileReactorComponent blockEntity) {
        return new ObjectDevice(new TileReactorComponentRecord(blockEntity));
    }

    public record TileReactorComponentRecord(TileReactorComponent reactor) implements NamedDevice {

        private static TileReactorCore core;

        @Override
        public @NotNull Collection<String> getDeviceTypeNames() {
            return singletonList(DRACONIC_REACTOR);
        }

        private boolean refreshCoreStatus() {
            core = reactor.getCachedCore();
            if (core == null) {
                return false;
            }
            return true;
        }

        @Callback
        public Map<Object, Object> getReactorInfo() {
            if (refreshCoreStatus()) {
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.put("temperature", MathUtils.round(core.temperature.get(), 100));
                map.put("fieldStrength", MathUtils.round(core.shieldCharge.get(), 100));
                map.put("maxFieldStrength", MathUtils.round(core.maxShieldCharge.get(), 100));
                map.put("energySaturation", core.saturation.get());
                map.put("maxEnergySaturation", core.maxSaturation.get());
                map.put("fuelConversion", MathUtils.round(core.convertedFuel.get(), 1000));
                map.put("maxFuelConversion", core.reactableFuel.get() + core.convertedFuel.get());
                map.put("generationRate", (int)core.generationRate.get());
                map.put("fieldDrainRate", core.fieldDrain.get());
                map.put("fuelConversionRate", (int) Math.round(core.fuelUseRate.get() * 1000000D));
                map.put("status", core.reactorState.get().name().toLowerCase(Locale.ENGLISH));//reactor.reactorState.value == TileReactorCore.ReactorState.COLD ? "offline" : reactor.reactorState == 1 && !reactor.canStart() ? "charging" : reactor.reactorState == 1 && reactor.canStart() ? "charged" : reactor.reactorState == 2 ? "online" : reactor.reactorState == 3 ? "stopping" : "invalid");
                map.put("failSafe", core.failSafeMode.get());
                return map;
            }
            return null;
        }

        @Callback
        public boolean chargeReactor() {
            if (refreshCoreStatus()) {
                core.chargeReactor();
                return true;
            }
            return false;
        }

        @Callback
        public boolean activateReactor() {
            if (refreshCoreStatus()) {
                core.activateReactor();
                return true;
            }
            return false;
        }

        @Callback
        public boolean stopReactor() {
            if (refreshCoreStatus()) {
                core.shutdownReactor();
                return true;
            }
            return false;
        }

        @Callback
        public boolean toggleFailSafe() {
            if (refreshCoreStatus()) {
                core.toggleFailSafe();
                return true;
            }
            return false;
        }
    }
}
