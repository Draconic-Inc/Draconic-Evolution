package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;

/**
 * Created by brandon3055 on 29/11/2016.
 * This is the base class of a 2 sided energy net FX handler built to render and update energy crystals.
 * The server side is responsible for monitoring the crystal and sending any needed updates to the client via {@link com.brandon3055.draconicevolution.network.CrystalUpdateBatcher}
 * The client side is responsible for creating and updating the render FX.
 */
public abstract class ENetFXHandler {

    protected final TileCrystalBase tile;

    public ENetFXHandler(TileCrystalBase tile) {
        this.tile = tile;

    }

    public abstract void update();

    public void tileUnload() {}

    public void updateReceived(BatchedCrystalUpdate update) {}

    public abstract void reloadConnections();
}
