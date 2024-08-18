package com.brandon3055.draconicevolution.integration.computers.cc;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;

import static com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.FLUID_GATE;

public class PeripheralFluidGate extends PeripheralFlowGate {
	
	TileFluidGate tile;
	public PeripheralFluidGate(TileFluidGate tile) {
        super(tile);
    }
	
	@Override
	public String getType() {
		return FLUID_GATE;
	}
}
