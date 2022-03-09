package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerServerWireless extends ENetFXHandler<TileCrystalWirelessIO> {

    private Map<Byte, Byte> lastTickIndexToFlow = new HashMap<>();
    private Map<Byte, Byte> lastTickIndexToRFlow = new HashMap<>();
    private long lastTickEnergy = -1;


    public ENetFXHandlerServerWireless(TileCrystalWirelessIO tile) {
        super(tile);
    }

    @Override
    public void update() {}

    @Override
    public void detectAndSendChanges() {
        BatchedCrystalUpdate update = new BatchedCrystalUpdate(tile.getIDHash(), tile.getEnergyStored());
        for (byte i = 0; i < tile.flowRates.size(); i++) {
            byte flow = tile.flowRates.get(i);

            if (!lastTickIndexToFlow.containsKey(i) || lastTickIndexToFlow.get(i) != flow) {
                update.indexToFlowMap.put(i, flow);
                lastTickIndexToFlow.put(i, flow);
            }
        }

        for (byte i = 0; i < tile.receiverFlowRates.size(); i++) {
            byte flow = tile.receiverFlowRates.get(i);

            if (!lastTickIndexToRFlow.containsKey(i) || lastTickIndexToRFlow.get(i) != flow) {
                update.indexToFlowMap.put((byte) (i + 128), flow);
                lastTickIndexToRFlow.put((byte) (i + 128), flow);
            }
        }

        if (update.indexToFlowMap.size() > 0 || Math.abs(lastTickEnergy - tile.getEnergyStored()) > 100) {
            lastTickEnergy = tile.getEnergyStored();
            queUpdate(update);
        }
    }

    @Override
    public void reloadConnections() {
        lastTickIndexToFlow.clear();
    }

    private void queUpdate(BatchedCrystalUpdate update) {
        ServerWorld serverWorld = ((ServerWorld) tile.getLevel());

        if (serverWorld != null) {
            serverWorld.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false).forEach(player -> CrystalUpdateBatcher.queData(update, player));
        }
    }
}
