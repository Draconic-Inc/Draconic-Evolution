package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class ENetFXHandlerServer<T extends BlockEntity & IENetEffectTile> extends ENetFXHandler<T> {

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
            queUpdate(update);
        }
    }

    @Override
    public void reloadConnections() {
        lastTickIndexToFlow.clear();
    }

    private void queUpdate(BatchedCrystalUpdate update) {
        ServerLevel serverWorld = ((ServerLevel) tile.getLevel());

        if (serverWorld != null) {
            serverWorld.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false).forEach(player -> CrystalUpdateBatcher.queData(update, player));
        }
    }
}
