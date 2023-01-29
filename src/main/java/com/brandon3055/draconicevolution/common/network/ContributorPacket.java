package com.brandon3055.draconicevolution.common.network;

import net.minecraft.entity.player.EntityPlayer;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class ContributorPacket implements IMessage {

    public String contributor;
    public boolean wings;
    public boolean badge;

    public ContributorPacket() {}

    public ContributorPacket(String contributor, boolean wings, boolean badge) {
        this.contributor = contributor;
        this.wings = wings;
        this.badge = badge;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.contributor = ByteBufUtils.readUTF8String(bytes);
        this.wings = bytes.readBoolean();
        this.badge = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        ByteBufUtils.writeUTF8String(bytes, contributor);
        bytes.writeBoolean(wings);
        bytes.writeBoolean(badge);
    }

    public static class Handler implements IMessageHandler<ContributorPacket, IMessage> {

        @Override
        public IMessage onMessage(ContributorPacket message, MessageContext ctx) {
            if (ContributorHandler.contributors.containsKey(message.contributor)) {
                ContributorHandler.Contributor contributor1 = ContributorHandler.contributors.get(message.contributor);

                if (ctx.side == Side.SERVER) {
                    if (!contributor1.isUserValid(ctx.getServerHandler().playerEntity)) {
                        return null;
                    }

                    contributor1.contributorWingsEnabled = message.wings;
                    contributor1.patreonBadgeEnabled = message.badge;

                    DraconicEvolution.network.sendToAll(message);
                } else {
                    EntityPlayer player = BrandonsCore.proxy.getClientPlayer();
                    if (!contributor1.isUserValid(player)
                            || message.contributor.equals(player.getCommandSenderName())) {
                        return null;
                    }

                    contributor1.contributorWingsEnabled = message.wings;
                    contributor1.patreonBadgeEnabled = message.badge;
                }
            }

            return null;
        }
    }
}
