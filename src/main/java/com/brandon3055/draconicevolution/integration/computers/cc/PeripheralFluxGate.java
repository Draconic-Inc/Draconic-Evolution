package com.brandon3055.draconicevolution.integration.computers.cc;

import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;

import static com.brandon3055.draconicevolution.integration.computers.CCOCIntegration.FLUX_GATE;

public class PeripheralFluxGate extends PeripheralFlowGate {
	
	TileFluxGate tile;
	public PeripheralFluxGate(TileFluxGate tile) {
        super(tile);
    }
	
	@Override
	public String getType() {
		return FLUX_GATE;
	}
}
