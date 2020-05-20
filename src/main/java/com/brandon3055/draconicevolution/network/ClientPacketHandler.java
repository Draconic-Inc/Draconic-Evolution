package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
        switch (packet.getType()) {
//            case 1: //Portal Arrival
//                TileEntity tile = mc.world.getTileEntity(packet.readPos());
//                if (tile instanceof TileDislocatorReceptacle) {
//                    ((TileDislocatorReceptacle) tile).setHidden();
//                }
//                break;
            case DraconicNetwork.C_CRYSTAL_UPDATE:
                CrystalUpdateBatcher.handleBatchedData(packet);
                break;
            case DraconicNetwork.C_TRACKER:
                int entityID = packet.readVarInt();
                CompoundNBT nbt = packet.readCompoundNBT();
                if (mc.world == null || nbt == null) return;
                Entity entity = mc.world.getEntityByID(entityID);
                if (entity == null) return;
                entity.getPersistentData().put("wr:trackers", nbt.getList("l", 10));
                break;
        }
    }
}