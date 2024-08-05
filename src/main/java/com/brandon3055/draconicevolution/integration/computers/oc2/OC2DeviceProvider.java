package com.brandon3055.draconicevolution.integration.computers.oc2;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import li.cil.oc2r.api.bus.device.Device;
import li.cil.oc2r.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2r.api.bus.device.provider.BlockDeviceQuery;
import li.cil.oc2r.api.util.Invalidatable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static li.cil.oc2r.api.util.Registries.BLOCK_DEVICE_PROVIDER;

public class OC2DeviceProvider {
    private static final DeferredRegister<BlockDeviceProvider> BLOCK_DEVICE_PROVIDERS =
            DeferredRegister.create(BLOCK_DEVICE_PROVIDER, MODID);

    public static void init() {
        BLOCK_DEVICE_PROVIDERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_DEVICE_PROVIDERS.register(MODID, Provider::new);
    }

    public static class Provider implements BlockDeviceProvider {

        @Override
        public @NotNull Invalidatable<Device> getDevice(BlockDeviceQuery query) {
            BlockEntity blockEntity = query.getLevel().getBlockEntity(query.getQueryPosition());
            if (blockEntity instanceof TileBCore tile) {
                if (tile instanceof TileReactorComponent reactorComponent) {
                    return Invalidatable.of(ReactorOC2Device.create(reactorComponent));
                }
                if (tile instanceof TileEnergyPylon energyPylon) {
                    return Invalidatable.of(EnergyPylonOC2Device.create(energyPylon));
                }
                if (tile instanceof TileFluidGate fluidGate) {
                    return Invalidatable.of(FluidGateOC2Device.create(fluidGate));
                }
                if (tile instanceof TileFluxGate fluxGate) {
                    return Invalidatable.of(FluxGateOC2Device.create(fluxGate));
                }
                if (tile instanceof TileFlowGate flowGate) {
                    return Invalidatable.of(FlowGateOC2Device.create(flowGate));
                }
            }
            return Invalidatable.empty();
        }

    }
}
