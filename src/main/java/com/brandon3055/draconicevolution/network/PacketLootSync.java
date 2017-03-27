package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class PacketLootSync implements IMessage
{

	private int entityID;
	private Map<ItemStack, Integer> map;

	public PacketLootSync() {}

	public PacketLootSync(int entityID, Map<ItemStack, Integer> map) {
		this.entityID = entityID;
		this.map = map;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		entityID = buf.readInt();
		map = new HashMap<ItemStack, Integer>();

		int count = buf.readInt();
		for (int i = 0; i < count; i++) {
			ItemStack stack = ByteBufUtils.readItemStack(buf);
			map.put(stack, buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(entityID);
		buf.writeInt(map.size());

		for (ItemStack stack : map.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			stack.writeToNBT(tag);
			ByteBufUtils.writeItemStack(buf, stack);
			buf.writeInt(map.get(stack));
		}
	}

	public static class Handler extends MessageHandlerWrapper<PacketLootSync, IMessage> {

        @Override
        public IMessage handleMessage(PacketLootSync message, MessageContext ctx) {
			Entity entity = BrandonsCore.proxy.getClientWorld().getEntityByID(message.entityID);

			if (entity instanceof EntityLootCore) {
				((EntityLootCore) entity).displayMap = message.map;
			}

            return null;
        }

	}
}