package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;

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
            case DraconicNetwork.C_SHIELD_HIT:
//                handleShieldHit(packet.readInt());
                break;
        }
    }


//    private static void handleShieldHit(int entityID) {
//        if (Minecraft.getInstance().world != null) {
//            Entity ent = Minecraft.getInstance().world.getEntityByID(entityID);
//            if (ent instanceof LivingEntity) {
//                LivingEntity entity = (LivingEntity) ent;
//                ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
//                LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
//                if (!stack.isEmpty() && optionalHost.isPresent()) {
//                    ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
//                    ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
//                    if (shieldControl != null) {
//                        shieldControl.handleClientSideHit(entity);
//                    }
//                }
//            }
//        }
//    }

}