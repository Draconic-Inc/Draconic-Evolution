package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;

public class PeripheralFluxGate extends PeripheralFlowGate {
	
	TileFluxGate tile;
	public PeripheralFluxGate(TileFluxGate tile) {
        super(tile);
    }
	
	@Override
	public String getType() {
		return "flux_gate";
	}
}
