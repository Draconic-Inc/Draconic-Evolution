package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBeam;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXLink;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXWireless;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerClientWireless extends ENetFXHandler<TileCrystalWirelessIO> {

    protected CrystalGLFXBase staticFX;
    protected LinkedList<CrystalGLFXBase> beamFXList = new LinkedList<>();
    protected LinkedList<CrystalGLFXBase> transferFXList = new LinkedList<>();
    protected LinkedList<CrystalGLFXBase> linkFX = null;

    public ENetFXHandlerClientWireless(TileCrystalWirelessIO tile) {
        super(tile);
    }

    @Override
    public void update() {
        //region Update Static FX
        if (staticFX == null || !staticFX.isAlive()) {
            staticFX = tile.createStaticFX();
            BCEffectHandler.spawnGLParticle(staticFX.getFXHandler(), staticFX);
        }
        staticFX.updateFX(0.5F);
        staticFX.renderEnabled = renderCooldown > 0;
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

            int i = beamFXList.indexOf(beam);
            if (tile.flowRates.size() > i && i >= 0) {
                beam.updateFX((tile.flowRates.get((byte) i) & 0xFF) / 255F);
            }
        }

        //endregion

        //region Update ReceiverFX
//        LogHelper.dev(transferFXList.size() + " " + tile.getReceivers().size()+" "+tile.receiverFlowRates.size());
        for (CrystalGLFXBase transFX : transferFXList) {
            if (!transFX.isAlive()) {
                requiresUpdate = true;
            }

            int i = transferFXList.indexOf(transFX);
            if (tile.receiverFlowRates.size() > i && i >= 0) {
                transFX.updateFX((tile.receiverFlowRates.get(i) & 0xFF) / 255F);
            }
        }

        if (requiresUpdate || tile.getReceivers().size() != transferFXList.size()) {
            reloadConnections();//TODO Make This Better. If needed...
        }
        //endregion

        if (ClientEventHandler.playerHoldingWrench) {
            if (linkFX == null || linkFX.size() != tile.getReceivers().size()) {
                if (linkFX != null) {
                    for (CrystalGLFXBase fx : linkFX) {
                        fx.setExpired();
                    }
                }

                linkFX = new LinkedList<>();
                for (BlockPos receiver : tile.getReceivers()) {
                    CrystalFXLink link = new CrystalFXLink(tile.getWorld(), tile, Vec3D.getCenter(receiver));
                    linkFX.add(link);
                    BCEffectHandler.spawnGLParticle(link.getFXHandler(), link);
                }
            }
        }
        else if (linkFX != null) {
            linkFX = null;
        }
    }

    @Override
    public void updateReceived(BatchedCrystalUpdate update) {
        tile.modifyEnergyStored(update.crystalCapacity - tile.getEnergyStored());
        Map<Byte, Byte> flowMap = update.indexToFlowMap;

        for (byte index = 0; index < tile.flowRates.size(); index++) {
            if (!flowMap.containsKey(index)) {
                flowMap.put(index, tile.flowRates.get(index));
            }
        }

        for (byte index = 0; index < tile.receiverFlowRates.size(); index++) {
            if (!flowMap.containsKey((byte) (index + 128))) {
                flowMap.put((byte) (index + 128), tile.receiverFlowRates.get(index));
            }
        }

        tile.flowRates.clear();
        tile.receiverFlowRates.clear();

        for (byte b : flowMap.keySet()) {
            if ((b & 0xFF) >= 128) {
                tile.receiverFlowRates.add(flowMap.get(b));
            }
            else {
                tile.flowRates.add(flowMap.get(b));
            }
        }
    }

    @Override
    public void reloadConnections() {
        beamFXList.clear();
        transferFXList.clear();

        for (BlockPos pos : tile.getLinks()) {
            TileEntity target = tile.getWorld().getTileEntity(pos);
            if (!(target instanceof ICrystalLink)) {
                continue;
            }
            CrystalFXBeam beam = new CrystalFXBeam(tile.getWorld(), tile, (ICrystalLink) target);
            beamFXList.add(beam);
            BCEffectHandler.spawnGLParticle(beam.getFXHandler(), beam);
        }

        for (BlockPos pos : tile.getReceivers()) {
            CrystalFXWireless wirelessFX = new CrystalFXWireless(tile.getWorld(), tile, pos);
            transferFXList.add(wirelessFX);
            BCEffectHandler.spawnGLParticle(wirelessFX.getFXHandler(), wirelessFX);
        }
    }

    @Override
    public void tileUnload() {
        super.tileUnload();
    }
}
