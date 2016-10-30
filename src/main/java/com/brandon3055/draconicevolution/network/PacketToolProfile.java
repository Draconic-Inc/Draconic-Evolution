package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketToolProfile implements IMessage
{

	private PlayerSlot slot;
	public String name = "";

	public PacketToolProfile() {}

	public PacketToolProfile(PlayerSlot slot, String name) {
		this.slot = slot;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf bytes){
		slot = PlayerSlot.fromBuff(bytes);
		this.name = ByteBufUtils.readUTF8String(bytes);
	}

	@Override
	public void toBytes(ByteBuf bytes){
		slot.toBuff(bytes);
		ByteBufUtils.writeUTF8String(bytes, name);
	}

	public static class Handler extends MessageHandlerWrapper<PacketToolProfile, IMessage> {

        @Override
        public IMessage handleMessage(PacketToolProfile message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack stack = message.slot.getStackInSlot(player);

			if (stack != null && stack.getItem() instanceof IConfigurableItem) {
				ToolConfigHelper.setProfileName(stack, ToolConfigHelper.getProfile(stack), message.name);
			}

            return null;
        }

	}
}