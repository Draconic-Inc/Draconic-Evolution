package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerServer<T extends TileEntity & IENetEffectTile> extends ENetFXHandler<T> {

    private BatchedCrystalUpdate batchedUpdate;
    private Map<Byte, Byte> lastTickIndexToFlow = new HashMap<>();
    private long lastTickEnergy = -1;


    public ENetFXHandlerServer(T tile) {
        super(tile);
    }

    @Override
    public void update() {}

    @Override
    public void detectAndSendChanges() {
        lastTickIndexToFlow.clear();
        BatchedCrystalUpdate update = new BatchedCrystalUpdate(tile.getIDHash(), tile.getEnergyStored());
        for (byte i = 0; i < tile.getFlowRates().size(); i++) {
            byte flow = tile.getFlowRates().get(i);

            if (!lastTickIndexToFlow.containsKey(i) || lastTickIndexToFlow.get(i) != flow) {
                update.indexToFlowMap.put(i, flow);
                lastTickIndexToFlow.put(i, flow);
            }
        }

        if (update.indexToFlowMap.size() > 0 || Math.abs(lastTickEnergy - tile.getEnergyStored()) > 100) {
            lastTickEnergy = tile.getEnergyStored();
            batchedUpdate = update;
        }

        if (batchedUpdate != null) {
            queUpdate();
        }
    }

    @Override
    public void reloadConnections() {
        lastTickIndexToFlow.clear();
    }

    private void queUpdate() {
        ServerWorld serverWorld = ((ServerWorld) tile.getWorld());

        if (serverWorld != null){
            serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(tile.getPos()), false).forEach(player -> CrystalUpdateBatcher.queData(batchedUpdate, player));
        }

        batchedUpdate = null;
    }
}
