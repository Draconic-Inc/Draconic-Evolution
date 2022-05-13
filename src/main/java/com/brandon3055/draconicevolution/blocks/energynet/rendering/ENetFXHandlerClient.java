package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBeam;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerClient<T extends BlockEntity & IENetEffectTile> extends ENetFXHandler<T> {

    protected CrystalFXBase staticFX;
    protected LinkedList<CrystalFXBase> beamFXList = new LinkedList<>();

    public ENetFXHandlerClient(T tile) {
        super(tile);
    }

    @Override
    public void update() {
        //region Update Static FX
        if (tile.hasStaticFX()) {
            if (staticFX == null || !staticFX.isAlive()) {
                staticFX = tile.createStaticFX();
                DEParticles.addParticleDirect(tile.getLevel(), staticFX);
            }
            staticFX.updateFX(0.5F);
        }
        //endregion

        //region Update Beams
        boolean requiresUpdate = false;
        for (CrystalFXBase beam : beamFXList) {
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
            BlockEntity target = tile.getLevel().getBlockEntity(pos);
            if (!(target instanceof ICrystalLink)) {
                continue;
            }
            CrystalFXBeam beam = new CrystalFXBeam(tile.getLevel(), tile, (ICrystalLink) target);
            beamFXList.add(beam);
            DEParticles.addParticleDirect(tile.getLevel(), beam);
        }

    }

    @Override
    public void tileUnload() {
        super.tileUnload();
    }
}
