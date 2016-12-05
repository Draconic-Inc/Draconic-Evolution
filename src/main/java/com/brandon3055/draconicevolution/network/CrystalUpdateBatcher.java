package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.utils.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 29/11/2016.
 * Update batcher for the Energy Net Beam effects.
 */
public class CrystalUpdateBatcher extends PacketCompressible {

    //TODO Abstract this out to TileBCBase if it turns out to be useful for other things.
    public static final Map<Integer, TileCrystalBase> ID_CRYSTAL_MAP = new HashMap<>();
    private static final Map<EntityPlayerMP, List<BatchedCrystalUpdate>> batchQue = new HashMap<>();
    private List<BatchedCrystalUpdate> packetData;
    
    public CrystalUpdateBatcher() {}

    public static void gueData(BatchedCrystalUpdate update, EntityPlayerMP target) {
        if (!batchQue.containsKey(target)) {
            batchQue.put(target, new ArrayList<BatchedCrystalUpdate>());
        }
        
        batchQue.get(target).add(update);
    }
    
    public static void tickEnd() {
        if (!batchQue.isEmpty()) {
            for (EntityPlayerMP playerMP : batchQue.keySet()) {
                CrystalUpdateBatcher packet = new CrystalUpdateBatcher();
                packet.packetData = batchQue.get(playerMP);
                DraconicEvolution.network.sendTo(packet, playerMP);                
            }
            batchQue.clear();
        }
    }
    
    //region Packet
    
    @Override
    public void writeBytes(ByteBuf buf) {
        if (packetData.size() > Short.MAX_VALUE) {
            LogHelper.error("Do you seriously have more that 32000 crystals in your base? W... T... F...");
            buf.writeShort(0);
            return;
        }
        buf.writeShort(packetData.size());
        for (BatchedCrystalUpdate update : packetData) {
            update.writeData(buf);
        }
    }

    @Override
    public void readBytes(ByteBuf buf) {
        packetData = new ArrayList<>();
        int size = buf.readShort();
        for (int i = 0; i < size; i++) {
            BatchedCrystalUpdate update = new BatchedCrystalUpdate();
            update.readData(buf);
            packetData.add(update);
        }
    }

    public static class Handler extends MessageHandlerWrapper<CrystalUpdateBatcher, IMessage> {

        @Override
        public IMessage handleMessage(CrystalUpdateBatcher message, MessageContext ctx) {
            for (BatchedCrystalUpdate update : message.packetData) {
                TileCrystalBase tile = ID_CRYSTAL_MAP.get(update.crystalID);
                if (tile != null && !tile.isInvalid()) {
                    tile.receiveBatchedUpdate(update);
                }
            }
            return null;
        }

    }
    
    //endregion
    
    public static class BatchedCrystalUpdate {

        public int crystalID;

        public BatchedCrystalUpdate() {}
        
        public BatchedCrystalUpdate(int crystalID) {
            this.crystalID = crystalID;
            //TODO Add update data
        }
        
        public void writeData(ByteBuf buf) {
            buf.writeInt(crystalID);
        }

        public void readData(ByteBuf buf) {
            crystalID = buf.readInt();
        }
    }
}
