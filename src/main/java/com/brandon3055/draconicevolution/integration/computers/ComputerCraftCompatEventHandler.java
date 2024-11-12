package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.init.DEContent;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ComputerCraftCompatEventHandler {
    
    @SubscribeEvent (priority = EventPriority.LOW)
    public void onAttachCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_REACTOR_INJECTOR.get(), (be, direction) -> new PeripheralReactorComponent(be));
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_REACTOR_STABILIZER.get(), (be, direction) -> new PeripheralReactorComponent(be));

		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_ENERGY_PYLON.get(), (be, direction) -> new PeripheralEnergyPylon(be));
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_FLUID_GATE.get(), (be, direction) -> new PeripheralFlowGate(be));
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_FLUID_GATE.get(), (be, direction) -> new PeripheralFluidGate(be));
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_FLUX_GATE.get(), (be, direction) -> new PeripheralFlowGate(be));
		event.registerBlockEntity(PeripheralCapability.get(), DEContent.TILE_FLUX_GATE.get(), (be, direction) -> new PeripheralFluxGate(be));
    }
}
