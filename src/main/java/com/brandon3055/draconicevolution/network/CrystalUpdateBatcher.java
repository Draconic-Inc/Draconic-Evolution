package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.utils.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.*;

/**
 * Created by brandon3055 on 29/11/2016.
 * Update batcher for the Energy Net Beam effects.
 */
public class CrystalUpdateBatcher extends PacketCompressible {

    //TODO Abstract this out to TileBCBase if it turns out to be useful for other things.
    public static final Map<Integer, BlockPos> ID_CRYSTAL_MAP = new HashMap<>();
    private static final Map<EntityPlayerMP, List<BatchedCrystalUpdate>> batchQue = new HashMap<>();
    private List<BatchedCrystalUpdate> packetData;

    public CrystalUpdateBatcher() {
    }

    public static void queData(BatchedCrystalUpdate update, EntityPlayerMP target) {
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
                if (!ID_CRYSTAL_MAP.containsKey(update.crystalID)) {
                    continue;
                }

                TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(ID_CRYSTAL_MAP.get(update.crystalID));
                if (tile instanceof TileCrystalBase && !tile.isInvalid()) {
                    ((TileCrystalBase) tile).receiveBatchedUpdate(update);
                }

            }
            return null;
        }

    }

    //endregion

    public static class BatchedCrystalUpdate {

        public int crystalID;
        public int crystalCapacity;
        public Map<Byte, Byte> indexToFlowMap = new LinkedHashMap<>();

        public BatchedCrystalUpdate() {
        }

        public BatchedCrystalUpdate(int crystalID, int crystalCapacity) {
            this.crystalID = crystalID;
            this.crystalCapacity = crystalCapacity;
        }

        public void writeData(ByteBuf buf) {
            buf.writeInt(crystalID);
            buf.writeInt(crystalCapacity);
            buf.writeByte(indexToFlowMap.size());
            for (Byte index : indexToFlowMap.keySet()) {
                buf.writeByte(index);
                buf.writeByte(indexToFlowMap.get(index));
            }
        }

        public void readData(ByteBuf buf) {
            crystalID = buf.readInt();
            crystalCapacity = buf.readInt();
            byte count = buf.readByte();
            for (int i = 0; i < count; i++) {
                byte index = buf.readByte();
                indexToFlowMap.put(index, buf.readByte());
            }
        }
    }
}
