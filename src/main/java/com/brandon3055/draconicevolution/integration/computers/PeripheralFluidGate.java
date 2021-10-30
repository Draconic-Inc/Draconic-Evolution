package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;

public class PeripheralFluidGate extends PeripheralFlowGate {
	
	TileFluidGate tile;
	public PeripheralFluidGate(TileFluidGate tile) {
        super(tile);
    }
	
	@Override
	public String getType() {
		return "fluid_gate";
	}
}
