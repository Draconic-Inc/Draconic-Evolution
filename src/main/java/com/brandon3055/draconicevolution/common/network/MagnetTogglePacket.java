package com.brandon3055.draconicevolution.common.network;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.tools.Magnet;
import com.brandon3055.draconicevolution.common.utills.InventoryUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class MagnetTogglePacket implements IMessage {

    public MagnetTogglePacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        // do nothing
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // do nothing
    }

    public static final class Handler implements IMessageHandler<MagnetTogglePacket, IMessage> {

        @Override
        public IMessage onMessage(MagnetTogglePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            Optional<ItemStack> magnetOptional = InventoryUtils.getItemInPlayerInventory(player, Magnet.class);
            magnetOptional.ifPresent(itemStack -> {
                Magnet.toggle(itemStack);
                DraconicEvolution.network.sendTo(new MagnetToggleAckPacket(Magnet.isEnabled(itemStack)), player);
            });

            return null;
        }
    }
}
