package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerServer extends ENetFXHandler<IENetEffectTile> {

    private BatchedCrystalUpdate batchedUpdate;
    private Map<Byte, Byte> lastTickIndexToFlow = new HashMap<>();
    private long lastTickEnergy = -1;


    public ENetFXHandlerServer(IENetEffectTile tile) {
        super(tile);
    }

    @Override
    public void update() {
    }

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
            sendUpdate();
        }
    }

    @Override
    public void reloadConnections() {
        lastTickIndexToFlow.clear();
    }

    private void sendUpdate() {
        ServerWorld worldServer = ((ServerWorld) ((TileEntity) tile).getWorld());
        //TODO Packets CrystalUpdateBatcher
//        PlayerChunkMapEntry playerChunkMap = worldServer.getPlayerChunkMap().getEntry(((TileEntity) tile).getPos().getX() >> 4, ((TileEntity) tile).getPos().getZ() >> 4);
//        if (playerChunkMap != null) {
//            playerChunkMap.players.forEach(playerMP -> CrystalUpdateBatcher.queData(batchedUpdate, playerMP));
//        }

        batchedUpdate = null;
    }
}
