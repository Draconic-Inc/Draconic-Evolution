package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;

import java.util.LinkedList;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerClient extends ENetFXHandler {

    private CrystalGLFXBase staticFX;
    private LinkedList<CrystalGLFXBase> beamFX = new LinkedList<>();

    public ENetFXHandlerClient(TileCrystalBase tile) {
        super(tile);
    }

    @Override
    public void update() {
        if (staticFX == null || !staticFX.isAlive()) {
            staticFX = tile.createStaticFX();
            BCEffectHandler.spawnGLParticle(CrystalGLFXBase.CRYSTAL_FX_HANDLER, staticFX);
        }
        staticFX.updateFX(0.5F);
    }

    @Override
    public void updateReceived(CrystalUpdateBatcher.BatchedCrystalUpdate update) {
        super.updateReceived(update);
    }

    @Override
    public void reloadConnections() {
        beamFX.clear();
        //TODO add beams
    }

    @Override
    public void tileUnload() {
        super.tileUnload();
    }
}
