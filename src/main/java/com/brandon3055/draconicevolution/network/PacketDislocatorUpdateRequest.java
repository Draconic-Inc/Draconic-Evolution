package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler.LinkData;
import com.brandon3055.draconicevolution.utils.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDislocatorUpdateRequest implements IMessage {

    private String linkID;
    private LinkData data;
    private boolean isRequest;

    public PacketDislocatorUpdateRequest() {
    }

    public PacketDislocatorUpdateRequest(String linkID) {
        this.linkID = linkID;
        isRequest = true;
    }

    public PacketDislocatorUpdateRequest(String linkID, LinkData data) {
        this.linkID = linkID;
        this.data = data;
        isRequest = false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isRequest = buf.readBoolean();
        linkID = ByteBufUtils.readUTF8String(buf);
        if (!isRequest) {
            NBTTagCompound compound = ByteBufUtils.readTag(buf);
            if (compound == null || compound.isEmpty()) {
                data = null;
            }
            else {
                data = new LinkData(null).fromNBT(compound);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isRequest);
        ByteBufUtils.writeUTF8String(buf, linkID);
        if (!isRequest) {
            ByteBufUtils.writeTag(buf, data == null ? new NBTTagCompound() : data.toNBT(new NBTTagCompound()));
        }
    }

    public static class Handler extends MessageHandlerWrapper<PacketDislocatorUpdateRequest, IMessage> {

        @Override
        public IMessage handleMessage(PacketDislocatorUpdateRequest message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                DislocatorLinkHandler data = DislocatorLinkHandler.getDataInstance(player.world);
                LinkData reply = null;
                if (data != null && DislocatorLinkHandler.getLinkPos(player.world, message.linkID) != null) {
                    reply = data.linkDataMap.get(message.linkID);
                    LogHelper.dev("Reply: " + reply.isPlayer);
                }

                DraconicEvolution.network.sendTo(new PacketDislocatorUpdateRequest(message.linkID, reply), player);
            }
            else {
                DislocatorLinkHandler data = DislocatorLinkHandler.getDataInstance(BrandonsCore.proxy.getClientWorld());
                if (data != null) {
                    if (message.data == null) {
                        data.linkDataMap.remove(message.linkID);
                    }
                    else {
                        data.linkDataMap.put(message.linkID, message.data);
                    }
                }
            }

            return null;
        }

    }
}