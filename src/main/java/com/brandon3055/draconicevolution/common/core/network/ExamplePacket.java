package draconicevolution.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ExamplePacket implements IPacket
{
	@Override
	public void readBytes(ByteBuf bytes)
	{
		System.out.println(bytes.readBoolean());
		System.out.println(bytes.readDouble());
		System.out.println(bytes.readInt());
		System.out.println(bytes.readChar());
		System.out.println(ByteBufUtils.readUTF8String(bytes));
		System.out.println(ByteBufUtils.readItemStack(bytes));
		System.out.println(ByteBufUtils.readTag(bytes));
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeBoolean(true);
		bytes.writeDouble(3.141592653589D);
		bytes.writeInt(65536);
		bytes.writeChar("c".charAt(0));
		ByteBufUtils.writeUTF8String(bytes, "A string");
		ByteBufUtils.writeItemStack(bytes, new ItemStack(Items.boat));
		ByteBufUtils.writeTag(bytes, new NBTTagCompound());
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player)
	{
		System.out.println("a client");
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer player)
	{
		System.out.println("a server");
	}

}