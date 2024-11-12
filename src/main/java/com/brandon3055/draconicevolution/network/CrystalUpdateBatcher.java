package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;


/**
 * Created by brandon3055 on 29/11/2016.
 * Update batcher for the Energy Net Beam effects.
 */
public class CrystalUpdateBatcher {

    public static final Map<Integer, BlockPos> ID_CRYSTAL_MAP = new HashMap<>();
    private static final Map<ServerPlayer, List<BatchedCrystalUpdate>> batchQue = new HashMap<>();

    public CrystalUpdateBatcher() {}

    public static void queData(BatchedCrystalUpdate update, ServerPlayer target) {
        batchQue.computeIfAbsent(target, e -> new ArrayList<>()).add(update);
    }

    public static void tickEnd() {
        if (!batchQue.isEmpty()) {
            for (ServerPlayer playerMP : batchQue.keySet()) {
                List<BatchedCrystalUpdate> playerData = batchQue.get(playerMP);
                if (!playerData.isEmpty()) {
                    PacketCustom packet = new PacketCustom(DraconicNetwork.CHANNEL_NAME, DraconicNetwork.C_CRYSTAL_UPDATE);
                    packet.writeVarInt(playerData.size());
                    playerData.forEach(update -> update.writeData(packet));
                    packet.sendToPlayer(playerMP);
                }
            }
            batchQue.clear();
        }
    }

    public static void handleBatchedData(MCDataInput input) {
        int count = input.readVarInt();
        for (int i = 0; i < count; i++) {
            BatchedCrystalUpdate update = new BatchedCrystalUpdate();
            update.readData(input);
            if (!ID_CRYSTAL_MAP.containsKey(update.crystalID)) {
                continue;
            }

            BlockEntity tile = BrandonsCore.proxy.getClientWorld().getBlockEntity(ID_CRYSTAL_MAP.get(update.crystalID));
            if (tile instanceof TileCrystalBase && !tile.isRemoved()) {
                ((TileCrystalBase) tile).receiveBatchedUpdate(update);
            }
        }

    }

    public static class BatchedCrystalUpdate {
        public int crystalID;
        public long crystalCapacity;
        public Map<Byte, Byte> indexToFlowMap = new LinkedHashMap<>();

        public BatchedCrystalUpdate() {}

        public BatchedCrystalUpdate(int crystalID, long crystalCapacity) {
            this.crystalID = crystalID;
            this.crystalCapacity = crystalCapacity;
        }

        public void writeData(MCDataOutput output) {
            output.writeVarInt(crystalID);
            output.writeVarLong(crystalCapacity);
            output.writeByte(indexToFlowMap.size());
            for (Byte index : indexToFlowMap.keySet()) {
                output.writeByte(index);
                output.writeByte(indexToFlowMap.get(index));
            }
        }

        public void readData(MCDataInput input) {
            crystalID = input.readVarInt();
            crystalCapacity = input.readVarLong();
            byte count = input.readByte();
            for (int i = 0; i < count; i++) {
                byte index = input.readByte();
                indexToFlowMap.put(index, input.readByte());
            }
        }
    }
}
