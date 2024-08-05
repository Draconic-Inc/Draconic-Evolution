package com.brandon3055.draconicevolution.integration.computers.oc2;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import li.cil.oc2r.api.bus.device.object.Callback;
import li.cil.oc2r.api.bus.device.object.NamedDevice;
import li.cil.oc2r.api.bus.device.object.ObjectDevice;
import li.cil.oc2r.api.bus.device.rpc.RPCDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.FLUX_GATE;
import static java.util.Collections.singletonList;

public class FluxGateOC2Device {
    public static RPCDevice create(TileFluxGate blockEntity) {
        return new ObjectDevice(new TileFluxGateRecord(blockEntity));
    }

    public record TileFluxGateRecord(TileFluxGate tile) implements NamedDevice {

        @Override
        public @NotNull Collection<String> getDeviceTypeNames() {
            return singletonList(FLUX_GATE);
        }

        @Callback
        public long getFlow() {
            return tile.getFlow();
        }

        @Callback
        public void setOverrideEnabled(boolean state) {
            tile.flowOverridden.set(state);
        }

        @Callback
        public boolean getOverrideEnabled() {
            return tile.flowOverridden.get();
        }

        @Callback
        public void setFlowOverride(long amount) {
            tile.flowOverride.set(amount);
        }

        @Callback
        public void setSignalHighFlow(long amount) {
            tile.maxFlow.set(amount);
        }

        @Callback
        public long getSignalHighFlow() {
            return tile.maxFlow.get();
        }

        @Callback
        public void setSignalLowFlow(long amount) {
            tile.minFlow.set(amount);
        }

        @Callback
        public long getSignalLowFlow() {
            return tile.minFlow.get();
        }
    }
}
