package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBeam;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXRing;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerClient extends ENetFXHandler {

    private CrystalGLFXBase staticFX;
    private Map<CrystalGLFXBase, Float> beamFXFlowMap = new HashMap<>();
    private LinkedList<CrystalGLFXBase> beamFXList = new LinkedList<>();

    public ENetFXHandlerClient(TileCrystalBase tile) {
        super(tile);
    }

    @Override
    public void update() {
        if (staticFX == null || !staticFX.isAlive()) {
            staticFX = tile.createStaticFX();
            BCEffectHandler.spawnGLParticle(CrystalFXRing.FX_HANDLER, staticFX);
        }
        staticFX.updateFX(0.5F);

        boolean requiresUpdate = false;
        for (CrystalGLFXBase beam : beamFXList) {
            if (!beam.isAlive()) {
                requiresUpdate = true;
            }
            beam.updateFX(beamFXFlowMap.get(beam));
        }

        if (requiresUpdate){// || tile.getLinks().size() != beamFXList.size()) {
            reloadConnections();//TODO Make This Better. If needed...
        }
    }

    @Override
    public void updateReceived(BatchedCrystalUpdate update) {
        super.updateReceived(update);
    }

    @Override
    public void reloadConnections() {
        LogHelper.dev("Reload Connections");
        beamFXFlowMap.clear();
        beamFXList.clear();

        for (BlockPos pos : tile.getLinks()) {
            CrystalFXBeam beam = new CrystalFXBeam(tile.getWorld(), tile, pos);
            beamFXList.add(beam);
            beamFXFlowMap.put(beam, 0F);
            BCEffectHandler.spawnGLParticle(CrystalFXBeam.FX_HANDLER, beam);
            break;
        }

        //TODO add beams
    }

    @Override
    public void tileUnload() {
        super.tileUnload();
    }
}
