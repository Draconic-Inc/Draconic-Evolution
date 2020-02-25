package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBeam;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerClient extends ENetFXHandler<IENetEffectTile> {

    protected CrystalGLFXBase staticFX;
    protected LinkedList<CrystalGLFXBase> beamFXList = new LinkedList<>();

    public ENetFXHandlerClient(IENetEffectTile tile) {
        super(tile);
    }

    @Override
    public void update() {
        //region Update Static FX
        if (tile.hasStaticFX()) {
            if (staticFX == null || !staticFX.isAlive()) {
                staticFX = tile.createStaticFX();
                //TODO Particles
//                BCEffectHandler.spawnGLParticle(staticFX.getFXHandler(), staticFX);
            }
            staticFX.updateFX(0.5F);
            staticFX.renderEnabled = renderCooldown > 0;
        }
        if (renderCooldown > 0) {
            renderCooldown--;
        }
        //endregion

        //region Update Beams
        boolean requiresUpdate = false;
        for (CrystalGLFXBase beam : beamFXList) {
            if (!beam.isAlive()) {
                requiresUpdate = true;
            }

            if (tile.getFlowRates().size() > beamFXList.indexOf(beam)) {
                beam.updateFX((tile.getFlowRates().get((byte) beamFXList.indexOf(beam)) & 0xFF) / 255F);
            }
        }

        if (requiresUpdate || tile.getLinks().size() != beamFXList.size()) {
            reloadConnections();//TODO Make This Better. If needed...
        }
        //endregion

    }

    @Override
    public void updateReceived(BatchedCrystalUpdate update) {
        tile.modifyEnergyStored(update.crystalCapacity - tile.getEnergyStored());
        Map<Byte, Byte> flowMap = update.indexToFlowMap;

        for (byte index = 0; index < tile.getFlowRates().size(); index++) {
            if (!flowMap.containsKey(index)) {
                flowMap.put(index, tile.getFlowRates().get(index));
            }
        }

        tile.getFlowRates().clear();
        for (byte i = 0; i < flowMap.size(); i++) {
            if (flowMap.containsKey(i)) {
                tile.getFlowRates().add(flowMap.get(i));
            }
        }
    }

    @Override
    public void reloadConnections() {
        beamFXList.clear();

        for (BlockPos pos : tile.getLinks()) {
            TileEntity target = ((TileEntity) tile).getWorld().getTileEntity(pos);
            if (!(target instanceof ICrystalLink)) {
                continue;
            }
            CrystalFXBeam beam = new CrystalFXBeam(((TileEntity) tile).getWorld(), tile, (ICrystalLink) target);
            beamFXList.add(beam);
            //TODO Particles
//            BCEffectHandler.spawnGLParticle(beam.getFXHandler(), beam);
        }

    }

    @Override
    public void tileUnload() {
        super.tileUnload();
    }
}
