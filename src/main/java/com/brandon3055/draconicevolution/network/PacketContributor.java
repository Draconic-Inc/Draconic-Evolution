package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.handlers.ContributorHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketContributor implements IMessage {
    public String contributor;
    public boolean wings;
    public boolean badge;
    public boolean lBadge;

    public PacketContributor() {
    }

    public PacketContributor(String contributor, boolean wings, boolean badge, boolean lBadge) {
        this.contributor = contributor;
        this.wings = wings;
        this.badge = badge;
        this.lBadge = lBadge;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.contributor = ByteBufUtils.readUTF8String(bytes);
        this.wings = bytes.readBoolean();
        this.badge = bytes.readBoolean();
        this.lBadge = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        ByteBufUtils.writeUTF8String(bytes, contributor);
        bytes.writeBoolean(wings);
        bytes.writeBoolean(badge);
        bytes.writeBoolean(lBadge);
    }

    public static class Handler implements IMessageHandler<PacketContributor, IMessage> {

        @Override
        public IMessage onMessage(PacketContributor message, MessageContext ctx) {
            if (ContributorHandler.contributors.containsKey(message.contributor)) {
                ContributorHandler.Contributor contributor = ContributorHandler.contributors.get(message.contributor);

                if (ctx.side == Side.SERVER) {
                    if (!contributor.isUserValid(ctx.getServerHandler().player)) {
                        return null;
                    }

                    contributor.contributorWingsEnabled = message.wings;
                    contributor.patreonBadgeEnabled = message.badge;
                    contributor.lolnetBadgeEnabled = message.lBadge;
                    ContributorHandler.saveContributorConfig();
                    DraconicEvolution.network.sendToAll(message);
                } else {
                    EntityPlayer player = BrandonsCore.proxy.getClientPlayer();
                    if (!contributor.isUserValid(player) || message.contributor.equals(player.getName())) {
                        return null;
                    }

                    contributor.contributorWingsEnabled = message.wings;
                    contributor.patreonBadgeEnabled = message.badge;
                    contributor.lolnetBadgeEnabled = message.lBadge;
                }
            }

            return null;
        }
    }
}